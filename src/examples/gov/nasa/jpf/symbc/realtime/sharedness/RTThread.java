/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.sharedness;

import javax.scj.PeriodicParameters;
import javax.scj.PeriodicThread;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class RTThread extends PeriodicThread {

	private int field = 0;
	/**
	 * @param pp
	 */
	public RTThread(PeriodicParameters pp) {
		super(pp);
	}

	@Override
	public boolean run() {

		if(field > 20) {
			int a = 2;
		} else {
			int a = 3+ 5;
		}
		
		return false;
	}

}
