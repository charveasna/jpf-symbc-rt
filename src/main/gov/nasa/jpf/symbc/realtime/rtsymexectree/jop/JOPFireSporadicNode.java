/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.jop;

import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.symbc.realtime.JOPUtil;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IRealTimeNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IStateReducible;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTFireSporadicNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTInvokeNode;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;
import gov.nasa.jpf.vm.Instruction;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class JOPFireSporadicNode extends RTFireSporadicNode implements IJOPRealTimeNode {
	private int wcet;
	
	public JOPFireSporadicNode(InstrContext instructionContext) {
		this(instructionContext, null);
	}
	
	public JOPFireSporadicNode(InstrContext instructionContext, SymbolicExecutionTree tree) {
		super(instructionContext, tree);
		Instruction instr = instructionContext.getInstr();
		this.wcet = JOPUtil.getWCET(instr);
		
		/*Add the method switch cost if the instruction is a return instruction
		* Note that we assume 'worst-case behavior' in terms of the cache - a
		* cache miss is assumed to always occur
		*/
		if(instr instanceof InvokeInstruction)
			this.wcet += JOPUtil.calculateMethodSwitchCost(false, ((InvokeInstruction)instr).getMethodInfo());
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
