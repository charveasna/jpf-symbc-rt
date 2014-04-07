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
import gov.nasa.jpf.symbc.bytecode.INVOKESPECIAL;
import gov.nasa.jpf.symbc.bytecode.INVOKESTATIC;
import gov.nasa.jpf.symbc.bytecode.INVOKEVIRTUAL;
import gov.nasa.jpf.symbc.realtime.ICacheAffectedNode;
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
public class JOPFireSporadicNode extends RTFireSporadicNode implements ICacheAffectedNode {
	private int wcet;
	private InvokeInstruction instr;
	private JOPTiming jopTiming;
	
	public JOPFireSporadicNode(InstrContext instructionContext, JOPTiming jopTiming, CACHE_POLICY cachePol) {
		this(instructionContext, jopTiming, cachePol, null);
	}
	
	public JOPFireSporadicNode(InstrContext instructionContext, JOPTiming jopTiming, CACHE_POLICY cachePol, SymbolicExecutionTree tree) {
		super(instructionContext, tree);
		this.jopTiming = jopTiming;
		Instruction i = instructionContext.getInstr();
		if(i instanceof InvokeInstruction) { //Not really necessary...
			this.instr = (InvokeInstruction)i;
			this.wcet = getCacheAffectedWCET(cachePol == CACHE_POLICY.HIT || cachePol == CACHE_POLICY.SIMULATE);
		}
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

	//TODO: getCacheAffectedWCET is the same in invokenode and returnnode
	@Override
	public int getCacheAffectedWCET(boolean cacheHit) {
		int instrWCET = this.jopTiming.getWCET(instr);
		int cacheLoadCost = this.jopTiming.calculateCacheLoadTime(instr, this.instr.getMethodInfo(), cacheHit);
		return instrWCET + cacheLoadCost; //From JOP Handbook
	}
}
