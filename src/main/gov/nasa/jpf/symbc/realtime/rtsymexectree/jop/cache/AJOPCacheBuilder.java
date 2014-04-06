/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.cache;

import gov.nasa.jpf.symbc.realtime.util.EnteredMethodsSet;
import gov.nasa.jpf.vm.MethodInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 * TODO: Consider making this an interface instead
 */
public abstract class AJOPCacheBuilder {

	protected int cacheBlocks;
	protected int cacheBlockSize;
	protected EnteredMethodsSet enteredMethods;

	public AJOPCacheBuilder(EnteredMethodsSet enteredMethods, int cacheBlocks, int totalCacheSize) {
		this.cacheBlocks = cacheBlocks;
		this.cacheBlockSize = totalCacheSize / cacheBlocks;
		this.enteredMethods = enteredMethods;
	}
	
	protected static <T extends Comparable<? super T>> List<T> asSortedList(
			Collection<T> c) {
			  List<T> list = new ArrayList<T>(c);
			  java.util.Collections.sort(list);
			  return list;
			}

	protected int numberOfBlocksForMethod(MethodInfo method) {
		int methodWordSize = (method.getNumberOfInstructions() + 3) / 4;
		return methodWordSize / this.cacheBlockSize;
	}

	public String buildCache() {
		StringBuilder cacheGen = new StringBuilder();
		cacheGen.append(this.initCache());
		cacheGen.append("\n//Cache type: ").append(this.getClass().getName()).append("\n");
		cacheGen.append(generateCacheTemplate());
		return cacheGen.toString();
	}
	
	protected abstract String generateCacheTemplate();
	
	private String initCache() {
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
		cacheBuilder.append("int[0, num_methods] cache[").append(this.cacheBlocks).append("] = { ");
		for(int i = 0; i < this.cacheBlocks; i++) {
			cacheBuilder.append("num_methods");
			if(i < this.cacheBlocks - 1) {
				cacheBuilder.append(", ");
			}
		}
		cacheBuilder.append(" };\n");
		cacheBuilder.append("bool cacheHit;\n");		
		return cacheBuilder.toString();
	}
}
