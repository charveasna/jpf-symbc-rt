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
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IRealTimeNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTReturnNode;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;
import gov.nasa.jpf.vm.Instruction;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 * 
 */
public class JOPReturnNode extends RTReturnNode implements ICacheAffectedNode {
	private int wcet;
	private ReturnInstruction instr;
	private JOPTiming jopTiming;
	
	public JOPReturnNode(InstrContext instructionContext, JOPTiming jopTiming,
			CACHE_POLICY cachePol) {
		this(instructionContext, jopTiming, cachePol, null);
	}

	public JOPReturnNode(InstrContext instructionContext, JOPTiming jopTiming,
			CACHE_POLICY cachePol, SymbolicExecutionTree tree) {
		super(instructionContext, tree);
		this.jopTiming = jopTiming;
		Instruction i = instructionContext.getInstr();
		if (i instanceof ReturnInstruction) {
			this.instr = (ReturnInstruction)i;
			this.wcet = this.getCacheAffectedWCET(cachePol == CACHE_POLICY.HIT || cachePol == CACHE_POLICY.SIMULATE);
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
		return true;
	}
	
	@Override
	public int getCacheAffectedWCET(boolean cacheHit) {
		int instrWCET = this.jopTiming.getWCET(instr);
		int cacheLoadCost = this.jopTiming.calculateCacheLoadTime(instr.getMethodInfo(), cacheHit);
		if (instr instanceof IRETURN || 
			instr instanceof FRETURN ||
			instr instanceof ARETURN) {
			return instrWCET + ((cacheLoadCost > 10) ? cacheLoadCost - 10 : 0); //From JOP Handbook
		} else if(instr instanceof LRETURN ||
				instr instanceof DRETURN) {
			return instrWCET + ((cacheLoadCost > 11) ? cacheLoadCost - 11 : 0); //From JOP Handbook
		} else { //Must be RETURN
			return instrWCET + ((cacheLoadCost > 9) ? cacheLoadCost - 9 : 0); //From JOP Handbook
		}
	}
}
