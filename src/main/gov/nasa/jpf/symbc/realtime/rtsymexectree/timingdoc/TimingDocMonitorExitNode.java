/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.timingdoc;

import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.symbc.realtime.InstructionTimingInfo;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTInvokeNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTMonitorEnterNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTMonitorExitNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.JOPTiming;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;
import gov.nasa.jpf.vm.Instruction;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class TimingDocMonitorExitNode extends RTMonitorExitNode implements ITimingDocRealTimeNode {

	private InstructionTimingInfo instrTimingInfo;
	public TimingDocMonitorExitNode(InstrContext instructionContext, InstructionTimingInfo instrTiming) {
		this(instructionContext, null, instrTiming);
	}

	public TimingDocMonitorExitNode(InstrContext instructionContext, SymbolicExecutionTree tree, InstructionTimingInfo instrTiming) {
		super(instructionContext, tree);
		this.instrTimingInfo = instrTiming;
	}

	@Override
	public int getBCET() {
		return this.instrTimingInfo.getBcet();
	}

	@Override
	public void setBCET(int bcet) {
		this.instrTimingInfo.setBcet(bcet);		
	}

	@Override
	public int getWCET() {
		return this.instrTimingInfo.getWcet();
	}

	@Override
	public void setWCET(int wcet) {
		this.instrTimingInfo.setWcet(wcet);
	}

	@Override
	public boolean isReducible() {
		return false;
	}
}
