/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.cache;

import java.util.Iterator;
import java.util.List;

import gov.nasa.jpf.symbc.realtime.util.EnteredMethodsSet;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class FIFOVarBlockCache extends AJOPCacheBuilder {

	public FIFOVarBlockCache(EnteredMethodsSet enteredMethods, int cacheBlocks, int totalCacheSize) {
		super(enteredMethods, cacheBlocks, totalCacheSize);
	}
	
	@Override
	public String generateCacheTemplate() {
		StringBuilder cacheBuilder = new StringBuilder();
		cacheBuilder.append("void access_cache(int mid) {\n")
					.append("  int i = 0;\n")
					.append("  int sz = NUM_BLOCKS[mid];\n")
					.append("  cacheHit = false;\n")
					.append("  for(i = 0; i < ").append(cacheBlocks).append("; i++) {\n")
					.append("    if(cache[i] == mid) {\n")
					.append("      cacheHit = true;\n")
					.append("      return;\n")
					.append("    }\n")
					.append("  }\n")
					.append("  for(i = ").append(cacheBlocks - 1).append("; i >= sz; i--) {\n")
					.append("    cache[i]=cache[i-sz];\n")
					.append("  }\n")
					.append("  for(i = 0; i < sz-1; i++) {\n")
					.append("    cache[i] = num_methods;\n")
					.append("  }\n")
					.append("  cache[i] = mid;\n")
					.append("}\n");
		return cacheBuilder.toString();
	}	
}
