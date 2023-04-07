# High-level Pcode (PcodeAST) output in Ghidra

Ghidra uses a processor independent representation called Pcode.  It is easy to grab and work with in scripts (Instruction.getPcode()).  Lesser known is that the decompiler exposes a higher-level Pcode representation.

The problem with using Pcode for processor-independent algorithms or AI model training is that low-level Pcode looks a lot like the assembly language that it came from.  The low-level Pcode is just an implementation of each assembly instruction in the Pcode language.

The Ghidra decompiler is essentially a Pcode simplifier and Pcode-to-C translation engine.  It simplifies the more verbose low-level Pcode, and converts it to a single-static-assignment (SSA) form (PcodeAST).  It then outputs this Pcode AST as C code.  The decompiler has an API to output the PCodeAST. Ghidra has a couple of built-in scripts that use the API but they are written to generate graphs (GraphAST.java, GraphASTandFlow.java).  This script instead just prints the PCode AST in text form.
