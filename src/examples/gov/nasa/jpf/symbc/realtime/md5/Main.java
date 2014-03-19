// no error here

package gov.nasa.jpf.symbc.realtime.md5;

import javax.scj.PeriodicParameters;
import javax.scj.RealtimeSystem;


public class Main {

	public static void main(String[] args) {

		new MD5SCJ(new PeriodicParameters(2000));
		new MD5SCJ(new PeriodicParameters(2000));

		RealtimeSystem.start();
	}

}
