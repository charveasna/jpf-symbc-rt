/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.timingdoc;

import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.symbc.realtime.InstructionTimingInfo;
import gov.nasa.jpf.symbc.realtime.JOPUtil;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTIfNode;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;
import gov.nasa.jpf.vm.Instruction;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class TimingDocIfNode extends RTIfNode implements ITimingDocRealTimeNode {

	private InstructionTimingInfo instrTimingInfo;
	
	public TimingDocIfNode(InstrContext instructionContext, InstructionTimingInfo instrTiming) {
		this(instructionContext, null, instrTiming);
	}

	public TimingDocIfNode(InstrContext instructionContext, SymbolicExecutionTree tree, InstructionTimingInfo instrTiming) {
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
}
