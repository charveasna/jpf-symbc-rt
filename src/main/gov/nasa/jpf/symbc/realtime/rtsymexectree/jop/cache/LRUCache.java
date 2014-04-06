/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.cache;

import gov.nasa.jpf.symbc.realtime.util.EnteredMethodsSet;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 * TODO: Review this cache implementation
 */
public class LRUCache extends AJOPCacheBuilder {

	public LRUCache(EnteredMethodsSet enteredMethods, int cacheBlocks,
			int totalCacheSize) {
		super(enteredMethods, cacheBlocks, totalCacheSize);
	}

	@Override
	protected String generateCacheTemplate() {
		StringBuilder cacheBuilder = new StringBuilder();
		cacheBuilder
				.append("void access_cache(int mid) {\n")
				.append("  cacheHit = false;\n")
				.append("  if(cache[0] == mid) {\n")
				.append("    cacheHit = true;\n")
				.append("  } else {\n")
				.append("    int i = 0;\n")
				.append("    int last = cache[0];\n")
				.append("    for(i = 0; i < " + (super.cacheBlocks - 1)).append(" && (!cacheHit); i++) {\n")
				.append("      int next = cache[i+1];\n")
				.append("      if(next == mid) {\n")
				.append("        cacheHit = true;\n")
				.append("      }\n")
				.append("      cache[i+1] = last;\n")
				.append("      last = next;\n")
				.append("    }\n")
				.append("    cache[0] = mid;\n")
				.append("  }\n")
				.append("}\n");
		return cacheBuilder.toString();
	}
}
