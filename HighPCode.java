//Decompile the function at the cursor and output the highlevel Pcode (PcodeAST)
//@category PCode

//evm
//JHU/APL
//March 7, 2023

import java.util.Iterator;
import ghidra.app.decompiler.*;
import ghidra.app.script.GhidraScript;
import ghidra.framework.plugintool.PluginTool;
import ghidra.program.model.listing.Function;
import ghidra.program.model.pcode.*;
import ghidra.util.Msg;

public class HighPCode extends GhidraScript {

	private Function func;
	protected HighFunction high;

	@Override
	public void run() throws Exception {
		PluginTool tool = state.getTool();
		if (tool == null) {
			println("Script is not running in GUI");
		}
		func = this.getFunctionContaining(this.currentAddress);
		println("Analyzing: " + func.toString());
		if (func == null) {
			Msg.showWarn(this, state.getTool().getToolFrame(), "High Pcode Error",
				"No Function at current location");
			return;
		}

		getHighPcode();
		printHighPcode();

	}


	private void getHighPcode() throws DecompileException {
		DecompileOptions options = new DecompileOptions();
		DecompInterface ifc = new DecompInterface();
		ifc.setOptions(options);

		if (!ifc.openProgram(this.currentProgram)) {
			throw new DecompileException("Decompiler",
				"Unable to initialize: " + ifc.getLastMessage());
		}
		ifc.setSimplificationStyle("decompile"); //this gives us HighVar names
		DecompileResults res = ifc.decompileFunction(func, 30, null);
		high = res.getHighFunction();

	}

	protected String getVarnodeString(VarnodeAST v) {
		String retstr="";
		retstr += v.toString();

		//include HighVariable information if it's there
		//but don't output UNNAMED a ton of times

		if (v.getHigh() != null) {
		   if (v.getHigh().getName() != null) {
		     if (v.getHigh().getName() != "UNNAMED") {
			retstr += "[" + v.getHigh().getName() + "]";
		     }
		   }
		}
		return retstr;
	}

	//You get a pretty good output by just printing op.toString()
	//I've tried to get pretty close to this output but include the 
	//HighVariable information when it's there

        protected void printHighPcode() {
		Iterator<PcodeOpAST> opiter = high.getPcodeOps();
		while (opiter.hasNext()) {						
			PcodeOpAST op = opiter.next();
			String highPcodeInst="";
			
			//Output Pcode op's output Varnode
			VarnodeAST outvn = (VarnodeAST) op.getOutput();
			if (outvn != null) {
				highPcodeInst += getVarnodeString(outvn);
			}
			else {
				highPcodeInst += "---"; //op with no output
			}			

			//Output opcode itself
			highPcodeInst += "," + " " + op.getMnemonic();
			
			//Output Pcode op's input Varnodes
			for (int i = 0; i < op.getNumInputs(); ++i) {
				highPcodeInst += "," + " " + getVarnodeString((VarnodeAST)op.getInput(i));
			}			

			println(highPcodeInst);
		}
	}
}
