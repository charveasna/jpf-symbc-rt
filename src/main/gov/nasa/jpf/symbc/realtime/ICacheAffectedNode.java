/**
 * 
 */
package gov.nasa.jpf.symbc.realtime;

import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.IJOPRealTimeNode;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public interface ICacheAffectedNode extends IJOPRealTimeNode {
	public int getCacheAffectedWCET(boolean cacheHit);
}
