/**
 * 
 */
package gov.nasa.jpf.symbc.realtime;

import gov.nasa.jpf.symbc.InvokeTest;
import gov.nasa.jpf.symbc.realtime.scd_micro.Main;

import org.junit.Test;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class TestSCDMicro extends InvokeTest {
	/*
	 * For Linux: Remember to set the LD_LIBRARY_PATH environment variable to point to the appropriate lib directory in
	 * jpf-symbc (32bit or 64bit). This is because we need to use the cvc3bitvec solver since liftcontrol performs bit operations
	 */
	
	private static final String SYM_METHOD = "+symbolic.method=gov.nasa.jpf.symbc.realtime.scd_micro.Main.run()";

	private static final String CLPATH = "+classpath=${jpf-symbc}/lib/scjNoRelativeTime_1.0.0.jar";
	private static final String LISTENER = "+listener = gov.nasa.jpf.symbc.realtime.UppaalTranslationListener";
	private static final String REALTIME_PLATFORM = "+symbolic.realtime.platform = jop";
	private static final String SYMRT = "+symbolic.realtime.targetsymrt = false";
	private static final String REALTIME_PATH = "+symbolic.realtime.outputbasepath = ./output";
	private static final String OPTIMIZE = "+symbolic.realtime.optimize = false";
	
	private static final String SOLVER = "+symbolic.dp=cvc3bitvec";
	
	private static final String[] JPF_ARGS = {INSN_FACTORY, 
											  LISTENER, 
											  OPTIMIZE,
											  SYM_METHOD, 
											  CLPATH,
											  SYMRT,
											  REALTIME_PLATFORM,
											  REALTIME_PATH,
											  SOLVER};
	@Test
	public void mainTest() {
		if (verifyNoPropertyViolation(JPF_ARGS)) {
			Main.main(null);
		}
	}
}
