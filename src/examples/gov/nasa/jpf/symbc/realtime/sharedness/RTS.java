/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.sharedness;

import javax.scj.PeriodicParameters;
import javax.scj.RealtimeSystem;
import javax.scj.RealtimeThread;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class RTS {

	public static void main(String[] args) {
		
		new RTThread(new PeriodicParameters(222));
		new RTThread(new PeriodicParameters(222));
		
		RealtimeSystem.start();
		
	}
	
}
