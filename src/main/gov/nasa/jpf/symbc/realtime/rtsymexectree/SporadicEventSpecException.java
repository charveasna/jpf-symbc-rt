/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree;

import gov.nasa.jpf.symbc.realtime.RealTimeRuntimeException;

import java.io.PrintStream;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class SporadicEventSpecException extends RealTimeRuntimeException {

	public SporadicEventSpecException (String details) {
		super(details);
	}

	public SporadicEventSpecException (Throwable cause) {
		super(cause);
	}

	public SporadicEventSpecException (String details, Throwable cause){
		super(details, cause);
	}
	
	public void printStackTrace (PrintStream out) {
		out.println("---------------------- Real-time Symbc JPF error stack trace ---------------------");
		super.printStackTrace(out);
	}
}
