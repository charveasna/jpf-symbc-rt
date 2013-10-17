package gov.nasa.jpf.symbc.realtime.rtsm;

import gov.nasa.jpf.symbc.Debug;
import gov.nasa.jpf.symbc.Symbolic;

public class Sensors {

	public void synchronizedReadSensors() {
		// TODO Auto-generated method stub
		
	}
	public int getBufferedSensor(int i) {
		// TODO Auto-generated method stub
		return Debug.makeSymbolicInteger("BUFSENS");
	}

}
