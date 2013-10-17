package gov.nasa.jpf.symbc.realtime.jembench.lift;

import javax.scj.PeriodicParameters;
import javax.scj.RealtimeSystem;

public class Main {

	public static void main(String[] args) {
		
		new PeriodicLiftControl(new PeriodicParameters(20));
		PeriodicLiftControl ctrl = new PeriodicLiftControl(new PeriodicParameters(20));
		//ctrl.run();
		
		RealtimeSystem.start();
	}
}
