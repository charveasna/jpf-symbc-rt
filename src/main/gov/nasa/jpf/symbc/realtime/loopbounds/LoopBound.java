/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.loopbounds;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 * container... Not strictly necessary
 */
public class LoopBound {
	private int bound;
	
	public LoopBound(int bound) {
		this.bound = bound;
	}
	
	public int getLoopBound() {
		return this.bound;
	}
	
	public void setLoopBound(int bound) {
		this.bound = bound;
	}
}
