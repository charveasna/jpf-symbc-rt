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
public class TestLoops  extends InvokeTest {
	private static final String SYM_METHOD = "+symbolic.method=gov.nasa.jpf.symbc.realtime.TestLoops.comp(sym#sym)";
	private static final String LISTENER = "+listener = gov.nasa.jpf.symbc.realtime.UppaalTranslationListener";
	private static final String OUTPUTPATH = "+symbolic.visualizer.basepath = ${jpf-symbc}/prettyprint";
	private static final String FORMAT = "+symbolic.visualizer.outputformat = pdf";
	private static final String DEPTH = "+search.depth_limit = 30";
	//Real time config:
	private static final String CLPATH = "+classpath=${jpf-symbc}/build;${jpf-symbc}/build";
	private static final String SRCPATH = "+sourcepath=${jpf-symbc-rt}/src/tests";
	private static final String REALTIME_PLATFORM = "+symbolic.realtime.platform = jop";
	private static final String SYMRT = "+symbolic.realtime.targetsymrt = false";
	private static final String REALTIME_PATH = "+symbolic.realtime.outputbasepath = ./output";
	private static final String OPTIMIZE = "+symbolic.realtime.optimize = true";
	
	private static final String SOLVER = "+symbolic.dp=choco";
	
	
	private static final String[] JPF_ARGS = {INSN_FACTORY,
											 // OUTPUTPATH,
											 // FORMAT,
											  SRCPATH,
											  LISTENER,
											  DEPTH,
											  SYM_METHOD, 
											  CLPATH,
											  REALTIME_PLATFORM,
											  SYMRT,
											  REALTIME_PATH,
											  OPTIMIZE,
											  SOLVER};
	@Test
	public void mainTest() {
		if (verifyNoPropertyViolation(JPF_ARGS)) {
			TestLoops test = new TestLoops();
			test.comp(2, 2);
		}
	}
	
	
	public int comp(int a, int b) {
		int c = 0;
		for(int i = 0; i < 5; i++) {
			if(a > 10) {
				if(a < 10) {
					c = 3 * 7 *6 *4 *3 *2 *6 *7 *6 *2* 5 + b;
				}
			}
		}
		
		return c;
	}

}
