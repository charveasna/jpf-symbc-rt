/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import gov.nasa.jpf.symbc.realtime.util.EnteredMethodsSet;
import gov.nasa.jpf.vm.MethodInfo;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class FIFOVarBlockCache extends AJOPCacheBuilder {

	private int cacheBlocks;
	private int cacheBlockSize;
	
	//Should maybe be hoisted to ajopcache...
	private EnteredMethodsSet enteredMethods; 
	
	public FIFOVarBlockCache(EnteredMethodsSet enteredMethods, int cacheBlocks, int totalCacheSize) {
		this.cacheBlocks = cacheBlocks;
		this.cacheBlockSize = totalCacheSize / cacheBlocks;
		this.enteredMethods = enteredMethods;
	}
	
	@Override
	public String generateCacheTemplate() {
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
		return cacheBuilder.toString();
	}

	//TODO: Review this...
	//TODO: should probably be hoisted to AJOPCacheBuilder
	private int numberOfBlocksForMethod(MethodInfo method) {
		int methodWordSize = (method.getNumberOfInstructions() + 3) / 4;
		return methodWordSize / this.cacheBlockSize;
	}
	
	//TODO: should probably be hoisted to AJOPCacheBuilder
	private static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}	
}
