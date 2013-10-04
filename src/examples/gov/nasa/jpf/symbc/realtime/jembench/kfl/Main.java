package gov.nasa.jpf.symbc.realtime.jembench.kfl;

import javax.scj.PeriodicParameters;
import javax.scj.RealtimeSystem;

public class Main {

	public static void main(String[] args) {
		
		Mast m1 = new Mast(new PeriodicParameters(300000));
		/*new Mast(new PeriodicParameters(300000));
		
		RealtimeSystem.start();	
		*/
		
		m1.run();
		
	}
	
}
