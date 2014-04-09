/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.jop;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.symbc.realtime.AUppaalTranslator;
import gov.nasa.jpf.symbc.realtime.ICacheAffectedNode;
import gov.nasa.jpf.symbc.realtime.RTConfig;
import gov.nasa.jpf.symbc.realtime.RealTimeRuntimeException;
import gov.nasa.jpf.symbc.realtime.UppaalTranslatorException;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.cache.AJOPCacheBuilder;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.cache.FIFOCache;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.cache.FIFOVarBlockCache;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.cache.JOP_CACHE;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.cache.LRUCache;
import gov.nasa.jpf.symbc.realtime.util.EnteredMethodsSet;
import gov.nasa.jpf.symbc.symexectree.structure.Node;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import uppaal.Automaton;
import uppaal.Location;
import uppaal.Transition;
import uppaal.Location.LocationType;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class JOPCacheSimUppaalTranslator extends JOPUppaalTranslator {
	
	private EnteredMethodsSet enteredMethods;
	private int cacheLocationId;
	private AJOPCacheBuilder jopCache;
	
	public JOPCacheSimUppaalTranslator(boolean targetSymRT,
			boolean useProgressMeasure, EnteredMethodsSet enteredMethods, AJOPCacheBuilder cache) {
		super(targetSymRT, useProgressMeasure);
		this.enteredMethods = enteredMethods;
		this.cacheLocationId = 0;
		this.jopCache = cache;
		
		String jopCacheDecl = jopCache.buildCache();
		super.nta.getDeclarations().add(jopCacheDecl);
	}
	
	public JOPCacheSimUppaalTranslator(RTConfig rtConf, EnteredMethodsSet enteredMethods) {
		this(rtConf.getValue(RTConfig.TARGET_SYMRT, Boolean.class), rtConf.getValue(RTConfig.PROGRESS_MEASURE
				, Boolean.class), enteredMethods, getCacheBuilder(rtConf, enteredMethods));
	}
	
	private static AJOPCacheBuilder getCacheBuilder(RTConfig rtConf, EnteredMethodsSet enteredMethods) {
		AJOPCacheBuilder cacheBuilder;
		JOP_CACHE cache = rtConf.getValue(RTConfig.JOP_CACHE_TYPE, JOP_CACHE.class);
		int cacheBlocks = rtConf.getValue(RTConfig.JOP_CACHE_BLOCKS, Integer.class);
		int cacheSize = rtConf.getValue(RTConfig.JOP_CACHE_SIZE, Integer.class);
		switch(cache) {
			case LRU:
				cacheBuilder = new LRUCache(enteredMethods, cacheBlocks, cacheSize);
				break;
			case FIFO:
				cacheBuilder = new FIFOCache(enteredMethods, cacheBlocks, cacheSize);
				break;
			case FIFOVARBLOCK:
			default:
				cacheBuilder = new FIFOVarBlockCache(enteredMethods, cacheBlocks, cacheSize);
		}
		return cacheBuilder;
	}
	
	
	@Override
	protected Location decoratePlatformDependentTransition(Automaton ta, Transition uppTrans, Node treeNode) {
		super.decoratePlatformDependentTransition(ta, uppTrans, treeNode);
		Location newSrc = uppTrans.getSource();
		if(treeNode instanceof ICacheAffectedNode) {
			newSrc = this.generateCacheTemplate(ta, uppTrans.getSource(), treeNode);
		}
		return newSrc;
	}
	
	private Location generateCacheTemplate(Automaton ta, Location targetLoc, Node treeNode) {
		ICacheAffectedNode cacheAffectedNode = (ICacheAffectedNode) treeNode;
		Instruction instr = treeNode.getInstructionContext().getInstr();
		MethodInfo cacheAffectedMethod;
		if(instr instanceof InvokeInstruction) {
			InvokeInstruction invokeInstr = (InvokeInstruction)instr;
			cacheAffectedMethod = invokeInstr.getInvokedMethod();
		} else if(instr instanceof ReturnInstruction) {
			/* TODO:
			 * This seems like a hack, but for some reason the stackframe to which
			 * we are returning is always null thereby preventing obtaining the methodinfo
			 * of the receiver! Optimally, it should work like below:
			 * ReturnInstruction retInstr = (ReturnInstruction)instr;
			 * StackFrame returnFrame = retInstr.getReturnFrame();
			 * if(returnFrame != null)
			 *		cacheAffectedMethod = returnFrame.getMethodInfo();
			 */
			if(treeNode.getOutgoingTransitions().size() > 0) {
				Node nxtNode = treeNode.getOutgoingTransitions().get(0).getDstNode();
				Instruction nxtInstr = nxtNode.getInstructionContext().getInstr();
				cacheAffectedMethod = nxtInstr.getMethodInfo();
			} else {
				return targetLoc; //If the frame is null, it means we are returning from main, so it will not have a cache effect...
			}			
		} else {
			throw new UppaalTranslatorException("Cache affected treeNode did not contain either a return or invoke instruction");
		}
		int accessedMethodId = this.enteredMethods.getIdForMethod(cacheAffectedMethod);
		Location accessCacheLoc = new Location(ta, "accessCache_" + this.cacheLocationId++);
		accessCacheLoc.setType(LocationType.COMMITTED);
		Location hitOrMissLoc = new Location(ta, "HitOrMiss_" + this.cacheLocationId++);
		hitOrMissLoc.setType(LocationType.COMMITTED);
		
		Transition accCacheTrans = new Transition(ta, accessCacheLoc, hitOrMissLoc);
		accCacheTrans.addUpdate("access_cache(" + accessedMethodId + ")");
		
		Transition cacheHitTrans = new Transition(ta, hitOrMissLoc, targetLoc);
		cacheHitTrans.setGuard("cacheHit");
		
		Location cacheMissLoc = new Location(ta, "cacheMiss_" + this.cacheLocationId++);
		int cacheMissCost = cacheAffectedNode.getCacheAffectedWCET(false) - cacheAffectedNode.getCacheAffectedWCET(true);
		cacheMissLoc.setInvariant(JBC_CLOCK_N + " <= " + cacheMissCost);
		
		Transition cacheMissTrans = new Transition(ta, hitOrMissLoc, cacheMissLoc);
		cacheMissTrans.setGuard("!cacheHit");
		
		Transition cacheMissDoneTrans = new Transition(ta, cacheMissLoc, targetLoc);
		cacheMissDoneTrans.setGuard(JBC_CLOCK_N + " == " + cacheMissCost);
		cacheMissDoneTrans.addUpdate(JBC_CLOCK_N + " = 0");
		
		return accessCacheLoc;
	}
}
