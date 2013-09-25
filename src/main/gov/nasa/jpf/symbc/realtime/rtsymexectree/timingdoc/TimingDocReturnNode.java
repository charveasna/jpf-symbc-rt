/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.timingdoc;

import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.symbc.realtime.InstructionTimingInfo;
import gov.nasa.jpf.symbc.realtime.JOPUtil;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTReturnNode;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;
import gov.nasa.jpf.vm.Instruction;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class TimingDocReturnNode extends RTReturnNode implements ITimingDocRealTimeNode {

	private InstructionTimingInfo instrTimingInfo;
	
	public TimingDocReturnNode(InstrContext instructionContext, InstructionTimingInfo instrTiming) {
		this(instructionContext, null, instrTiming);
	}

	public TimingDocReturnNode(InstrContext instructionContext, SymbolicExecutionTree tree, InstructionTimingInfo instrTiming) {
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
