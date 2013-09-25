/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree;

import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.symbc.realtime.JOPUtil;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;
import gov.nasa.jpf.vm.Instruction;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public abstract class RTFireSporadicNode extends RTInvokeNode {
	
	protected int sporadicEventID;
	
	public RTFireSporadicNode(InstrContext instructionContext) {
		this(instructionContext, null);
	}
	
	public RTFireSporadicNode(InstrContext instructionContext, SymbolicExecutionTree tree) {
		super(instructionContext, tree);
		
		Instruction instr = instructionContext.getInstr();
		if(instr instanceof InvokeInstruction) {
			InvokeInstruction invInstr = (InvokeInstruction)instr;
			Object[] argVals = invInstr.getArgumentValues(instructionContext.getThreadInfo());
			if(argVals.length > 1 ||
			   (argVals.length == 1 &&
			   !(argVals[0] instanceof Integer))) {
				throw new SporadicEventSpecException("Expected a single int as argument for method call at: " + invInstr.getFilePos());
			}
			this.sporadicEventID = ((Integer)argVals[0]).intValue();
		}
	}

	public int getSporadicEventID() {
		return this.sporadicEventID;
	}
	
}
