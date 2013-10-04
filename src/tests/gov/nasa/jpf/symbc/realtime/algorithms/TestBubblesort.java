/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.algorithms;

import gov.nasa.jpf.symbc.InvokeTest;
import gov.nasa.jpf.symbc.realtime.jembench.lift.Main;
import gov.nasa.jpf.symbc.realtime.minepump.scj.PeriodicMethaneDetectionEventHandler;

import org.junit.Test;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class TestBubblesort extends InvokeTest {
	private static final String SYM_METHOD = "+symbolic.method=gov.nasa.jpf.symbc.realtime.algorithms.BubbleSort.runBubbleSort()";

	private static final String CLPATH = "+classpath=${jpf-symbc}/lib/scjNoRelativeTime_1.0.0.jar";
	private static final String LISTENER = "+listener = gov.nasa.jpf.symbc.realtime.UppaalTranslationListener";
	private static final String REALTIME_PLATFORM = "+symbolic.realtime.platform = jop";
	private static final String SYMRT = "+symbolic.realtime.targetsymrt = false";
	private static final String REALTIME_PATH = "+symbolic.realtime.outputbasepath = ./output";
	private static final String OPTIMIZE = "+symbolic.realtime.optimize = true";
	
	private static final String SOLVER = "+symbolic.dp=choco";
	
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
			BubbleSort.main(null);
		}
	}
}
