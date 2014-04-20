/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.jop;

import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.symbc.realtime.ICacheAffectedNode;
import gov.nasa.jpf.symbc.realtime.RealTimeRuntimeException;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IRealTimeNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IStateReducible;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTInvokeNode;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.structure.Node;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 * TODO: clean up redundancy in invokenode, returnnode, and firesporadicnode. Could  use default methods from java 8
 */
public class JOPInvokeNode extends RTInvokeNode implements ICacheAffectedNode {
	private int wcet;
	private InvokeInstruction instr;
	private JOPTiming jopTiming;
	private MethodInfo targetMethod;
	private CACHE_POLICY cachePol;
	
	public JOPInvokeNode(InstrContext instructionContext, JOPTiming jopTiming, CACHE_POLICY cachePol) {
		this(instructionContext, jopTiming, cachePol, null);
	}
	
	public JOPInvokeNode(InstrContext instructionContext, JOPTiming jopTiming, CACHE_POLICY cachePol, SymbolicExecutionTree tree) {
		super(instructionContext, tree);
		this.jopTiming = jopTiming;
		this.cachePol = cachePol;
		this.wcet = -1;
		Instruction i = instructionContext.getInstr();
		if(i instanceof InvokeInstruction) { //Not really necessary...
			this.instr = (InvokeInstruction)i;
		} else
			throw new RealTimeRuntimeException("Instruction " + this.instr.getMnemonic() + " is not an instance of " + InvokeInstruction.class.getName());
	}

	@Override
	public int getWCET() {
		//We lazily calculate wcet
		if(this.wcet >= 0) //wcet has been set
			return this.wcet;
		else {
			this.setTargetMethodInfo();
			return this.wcet = this.getCacheAffectedWCET(this.cachePol == CACHE_POLICY.HIT || this.cachePol == CACHE_POLICY.SIMULATE);
		}
	}

	//TODO: setTargetMethodInfo is the same in returnnode and firesporadicnode
	private void setTargetMethodInfo() {
		this.targetMethod = this.instr.getInvokedMethod();
	}

	@Override
	public void setWCET(int wcet) {
		this.wcet = wcet;
	}

	@Override
	public boolean isReducible() {
		return true;
	}
	
	//TODO: getCacheAffectedWCET is the same in returnnode and firesporadicnode
	@Override
	public int getCacheAffectedWCET(boolean cacheHit) {
		this.setTargetMethodInfo();
		int instrWCET = this.jopTiming.getWCET(instr);
		if(this.targetMethod != null) {
			int cacheLoadCost = this.jopTiming.calculateCacheLoadTime(instr, this.targetMethod, cacheHit);
			return instrWCET + cacheLoadCost;
		} else
			return instrWCET;
	}
}
