/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.jop;

import gov.nasa.jpf.jvm.bytecode.ARETURN;
import gov.nasa.jpf.jvm.bytecode.DRETURN;
import gov.nasa.jpf.jvm.bytecode.FRETURN;
import gov.nasa.jpf.jvm.bytecode.IRETURN;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.LRETURN;
import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.symbc.realtime.ICacheAffectedNode;
import gov.nasa.jpf.symbc.realtime.RealTimeRuntimeException;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IRealTimeNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTReturnNode;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.SymExecTreeUtils;
import gov.nasa.jpf.symbc.symexectree.structure.Node;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 * TODO: clean up redundancy in invokenode, returnnode, and firesporadicnode. Could  use default methods from java 8
 */
public class JOPReturnNode extends RTReturnNode implements ICacheAffectedNode {
	private int wcet;
	private ReturnInstruction instr;
	private JOPTiming jopTiming;
	private CACHE_POLICY cachePol;
	private MethodInfo targetMethod;
	
	public JOPReturnNode(InstrContext instructionContext, JOPTiming jopTiming,
			CACHE_POLICY cachePol, String symbolicTargetMethod) {
		this(instructionContext, jopTiming, cachePol, null, symbolicTargetMethod);
	}

	public JOPReturnNode(InstrContext instructionContext, JOPTiming jopTiming,
			CACHE_POLICY cachePol, SymbolicExecutionTree tree, String symbolicTargetMethod) {
		super(instructionContext, tree);
		this.jopTiming = jopTiming;
		this.cachePol = cachePol;
		this.wcet = -1;
		Instruction i = instructionContext.getInstr();
		if (i instanceof ReturnInstruction) {
			this.instr = (ReturnInstruction)i;
			StackFrame returnFrame = instructionContext.getFrame().getPrevious();
			//NOTE: if the frame is not part in the call chain of the target method, then the cache affect should not be accounted for!
			if(SymExecTreeUtils.isInCallStackOfTargetMethod(symbolicTargetMethod, returnFrame))
				this.targetMethod = returnFrame.getMethodInfo();
		} else
			throw new RealTimeRuntimeException("Instruction " + this.instr.getMnemonic() + " is not an instance of " + ReturnInstruction.class.getName());
	}

	@Override
	public int getWCET() {
		//We lazily calculate wcet
		if(this.wcet >= 0) //wcet has been set
			return this.wcet;
		else {
			return this.wcet = this.getCacheAffectedWCET(this.cachePol == CACHE_POLICY.HIT || this.cachePol == CACHE_POLICY.SIMULATE);
		}
	}
	
	@Override
	public void setWCET(int wcet) {
		this.wcet = wcet;
	}

	@Override
	public boolean isReducible() {
		return true;
	}
	
	@Override
	public int getCacheAffectedWCET(boolean cacheHit) {
		int instrWCET = this.jopTiming.getWCET(instr);
		if(this.targetMethod != null) {
			int cacheLoadCost = this.jopTiming.calculateCacheLoadTime(instr, this.targetMethod, cacheHit);
			return instrWCET + cacheLoadCost;
		} else
			return instrWCET;
	}
}
