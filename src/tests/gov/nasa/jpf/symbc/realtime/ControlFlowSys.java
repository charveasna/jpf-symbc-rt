/**
 * 
 */
package gov.nasa.jpf.symbc.realtime;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class ControlFlowSys {
	
	public void computation(boolean cond) {
		int a = 2;
		if(cond) {
			a++;
			a++;
			a++;
		} else {
			a = 200;
		}
		int b = 100;
		if(cond) {
			b += 100;
			b += 100;
			b += 100;
			b += 100;
		}
	}
	
}
