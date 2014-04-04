/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.jop;

import gov.nasa.jpf.jvm.bytecode.MONITORENTER;
import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTInvokeNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTMonitorEnterNode;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;
import gov.nasa.jpf.vm.Instruction;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class JOPMonitorEnterNode extends RTMonitorEnterNode implements IJOPRealTimeNode {
	private int wcet;
	
	public JOPMonitorEnterNode(InstrContext instructionContext, JOPTiming jopTiming) {
		this(instructionContext, jopTiming, null);
	}

	public JOPMonitorEnterNode(InstrContext instructionContext, JOPTiming jopTiming, SymbolicExecutionTree tree) {
		super(instructionContext, tree);
		Instruction instr = instructionContext.getInstr();
		this.wcet = jopTiming.getWCET(instr);
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
		return false;
	}
}
