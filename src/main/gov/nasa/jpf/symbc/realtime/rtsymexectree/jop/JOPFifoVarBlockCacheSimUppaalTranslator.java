/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.jop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.symbc.realtime.ICacheAffectedNode;
import gov.nasa.jpf.symbc.realtime.UppaalTranslatorException;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IHasWCET;
import gov.nasa.jpf.symbc.realtime.util.EnteredMethodsSet;
import gov.nasa.jpf.symbc.symexectree.structure.Node;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import uppaal.Automaton;
import uppaal.Location;
import uppaal.NTA;
import uppaal.Transition;
import uppaal.Location.LocationType;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class JOPFifoVarBlockCacheSimUppaalTranslator extends JOPUppaalTranslator {
	
	private int cacheBlocks;
	private int totalCacheSize;
	private int cacheBlockSize;
	private EnteredMethodsSet enteredMethods;
	private int cacheLocationId;
	
	public JOPFifoVarBlockCacheSimUppaalTranslator(boolean targetSymRT,
			boolean useProgressMeasure, EnteredMethodsSet enteredMethods, int cacheBlocks, int totalCacheSize) {
		super(targetSymRT, useProgressMeasure);
		this.cacheBlocks = cacheBlocks;
		this.totalCacheSize = totalCacheSize;
		this.cacheBlockSize = totalCacheSize / cacheBlocks;
		this.enteredMethods = enteredMethods;
		this.cacheLocationId = 0;
		generateFIFOCache(super.nta, enteredMethods);
	}
	
	private void generateFIFOCache(NTA nta, EnteredMethodsSet enteredMethods) {
		StringBuilder cacheBuilder = new StringBuilder();
		int numMethods = enteredMethods.getEnteredMethods().size();
		cacheBuilder.append("const int num_methods = " + numMethods + ";\n");
		cacheBuilder.append("const int NUM_BLOCKS[num_methods] = { ");
		
		List<Integer> sortedIds = asSortedList(enteredMethods.getIds().keySet());
		Iterator<Integer> idIter = sortedIds.iterator();
		while(idIter.hasNext()) {
			int currId = idIter.next();
			int methodBlocks = numberOfBlocksForMethod(enteredMethods.getMethod(currId));
			cacheBuilder.append(methodBlocks);
			if(idIter.hasNext()) {
				cacheBuilder.append(", ");
			}
		}
		cacheBuilder.append(" };\n");
		//NOTE, we here assume that the first method is ->NOT<- in the cache (because 0 is not present at index 0);
		cacheBuilder.append("int[0, num_methods] cache[").append(this.cacheBlocks).append("] = { ");
		for(int i = 0; i < this.cacheBlocks; i++) {
			cacheBuilder.append("num_methods");
			if(i < this.cacheBlocks - 1) {
				cacheBuilder.append(", ");
			}
		}
		cacheBuilder.append(" };\n");
		cacheBuilder.append("bool cacheHit;\n");
		cacheBuilder.append("void access_cache(int mid) {\n")
					.append("\tint i = 0;\n")
					.append("\tint sz = NUM_BLOCKS[mid];\n")
					.append("\tcacheHit = false;\n")
					.append("\tfor(i = 0; i < ").append(cacheBlocks).append("; i++) {\n")
					.append("\t\tif(cache[i] == mid) {\n")
					.append("\t\t\tcacheHit = true;\n")
					.append("\t\t\treturn;\n")
					.append("\t\t}\n")
					.append("\t}\n")
					.append("\tfor(i = ").append(cacheBlocks - 1).append("; i >= sz; i--) {\n")
					.append("\t\tcache[i]=cache[i-sz];\n")
					.append("\t}\n")
					.append("\tfor(i = 0; i < sz-1; i++) {\n")
					.append("\t\tcache[i] = num_methods;\n")
					.append("\t}\n")
					.append("\tcache[i] = mid;\n")
					.append("}\n");
		
		nta.getDeclarations().add(cacheBuilder.toString());
	}
	
	//TODO: Review this...
	private int numberOfBlocksForMethod(MethodInfo method) {
		int methodWordSize = (method.getNumberOfInstructions() + 3) / 4;
		return methodWordSize / this.cacheBlockSize;
	}
	
	private <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
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
			Instruction nextInstr = instr.getNext(); //We obtain the first instruction of the caller
			if(nextInstr != null) {
				cacheAffectedMethod = nextInstr.getMethodInfo();
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
		
		return accessCacheLoc;
	}
}
