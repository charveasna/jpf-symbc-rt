/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.cache;

import gov.nasa.jpf.symbc.realtime.util.EnteredMethodsSet;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 * TODO: review if the cache is correct
 */
public class FIFOCache extends AJOPCacheBuilder {

	public FIFOCache(EnteredMethodsSet enteredMethods, int cacheBlocks,
			int totalCacheSize) {
		super(enteredMethods, cacheBlocks, totalCacheSize);
	}

	@Override
	public String generateCacheTemplate() {
		StringBuilder cacheBuilder = new StringBuilder();
		cacheBuilder
				.append("void access_cache(int mid) {\n")
				.append("  int i = 0;\n")
				.append("  cacheHit = false;\n")
				.append("  for(i = 0; i < " + super.cacheBlocks + "; i++) {\n")
				.append("    if(cache[i] == mid) {\n")
				.append("      cacheHit = true;\n")
				.append("      return;\n")
				.append("    }\n")
				.append("  }\n")
				.append("  for(i = " + (super.cacheBlocks - 1) + "; i > 0; i--) {\n")
				.append("    cache[i] = cache[i - 1];\n").append("  }\n")
				.append("  cache[0] = mid;\n").append("}\n");
		
		return cacheBuilder.toString();
	}
}
