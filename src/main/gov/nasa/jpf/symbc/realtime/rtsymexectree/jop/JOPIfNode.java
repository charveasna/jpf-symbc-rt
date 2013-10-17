/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.jop;

import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.symbc.realtime.JOPUtil;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTIfNode;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;
import gov.nasa.jpf.vm.Instruction;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class JOPIfNode extends RTIfNode implements IJOPRealTimeNode {
	private int wcet;
	
	public JOPIfNode(InstrContext instructionContext) {
		this(instructionContext, null);
	}
	
	public JOPIfNode(InstrContext instructionContext, SymbolicExecutionTree tree) {
		super(instructionContext, tree);
		Instruction instr = instructionContext.getInstr();
		this.wcet = JOPUtil.getWCET(instr);
	}

	@Override
	public int getWCET() {
		return this.wcet;
	}

	@Override
	public void setWCET(int wcet) {
		this.wcet = wcet;
	}

	@Override
	public boolean isReducible() {
		return (outgoingTransitions.size() > 1) ? false : true;
	}
}
