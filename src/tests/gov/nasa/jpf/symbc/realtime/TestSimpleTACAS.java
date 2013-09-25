/**
 * 
 */
package gov.nasa.jpf.symbc.realtime;

import gov.nasa.jpf.symbc.InvokeTest;
import gov.nasa.jpf.symbc.realtime.minepump.scj.PeriodicMethaneDetectionEventHandler;

import org.junit.Test;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class TestSimpleTACAS  extends InvokeTest {
	private static final String SYM_METHOD = "+symbolic.method=gov.nasa.jpf.symbc.realtime.TestSimpleTACAS.comp(sym#sym)";
	private static final String LISTENER = "+listener = gov.nasa.jpf.symbc.realtime.UppaalTranslationListener";

	//Real time config:
	private static final String CLPATH = "+classpath=${jpf-symbc}/build";
	private static final String REALTIME_PLATFORM = "+symbolic.realtime.platform = jop";
	private static final String TETASARTS = "+symbolic.realtime.targettetasarts = false";
	private static final String REALTIME_PATH = "+symbolic.realtime.outputbasepath = ./output";
	private static final String OPTIMIZE = "+symbolic.realtime.optimize = false";
	
	private static final String SOLVER = "+symbolic.dp=choco";
	
	
	private static final String[] JPF_ARGS = {INSN_FACTORY, 
											  LISTENER, 
											  SYM_METHOD, 
											  CLPATH,
											  REALTIME_PLATFORM,
											  TETASARTS,
											  REALTIME_PATH,
											  OPTIMIZE,
											  SOLVER};
	@Test
	public void mainTest() {
		if (verifyNoPropertyViolation(JPF_ARGS)) {
			TestSimpleTACAS test = new TestSimpleTACAS();
			test.comp(2, 2);
		}
	}
	
	
	public int comp(int a, int b) {
		if(a > b) {
			if(a == b) {
				return constant() + 1;
			} else
				return 42;
		} else
			return constant();
	}
	
	public int constant() {
		return 42;
	}

}
