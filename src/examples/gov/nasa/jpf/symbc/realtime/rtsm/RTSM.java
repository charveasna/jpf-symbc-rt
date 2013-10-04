package gov.nasa.jpf.symbc.realtime.rtsm;

import javax.scj.PeriodicParameters;
import javax.scj.RealtimeSystem;
import javax.scj.SporadicParameters;

public class RTSM {
    public final int WHITE = 2;
    public final int BLUE = 4;

	public static void main(String[] args) {
	    new SporadicPushMotor(
	    		new SporadicParameters(4,//BLUE
	    				4000,
	    				60),
	    		0);
	    new SporadicPushMotor(
	    		new SporadicParameters(2,//WHITE
	    				4000,
	    				60), 
	    		1);

		PeriodicMotorSpooler motorSpooler = new PeriodicMotorSpooler(
				new PeriodicParameters(4000)); //original period: 4000

		new PeriodicReadSensor(new PeriodicParameters(2000), motorSpooler); //original period: 2000

		RealtimeSystem.start();
	}
}
