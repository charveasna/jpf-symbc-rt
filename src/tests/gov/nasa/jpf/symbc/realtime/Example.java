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
public class Example  extends InvokeTest {
	private static final String SYM_METHOD = "+symbolic.method=gov.nasa.jpf.symbc.realtime.Example.comp(sym#sym)";
	private static final String LISTENER = "+listener = gov.nasa.jpf.symbc.realtime.UppaalTranslationListener";
	//private static final String LISTENER = "+listener = gov.nasa.jpf.symbc.symexectree.visualizer.SymExecTreeVisualizerListener";
	private static final String OUTPUTPATH = "+symbolic.visualizer.basepath = ${jpf-symbc}/prettyprint";
	private static final String FORMAT = "+symbolic.visualizer.outputformat = pdf";
	private static final String CACHE = "+symbolic.realtime.jop.cachepolicy = simulate";
	
	//Real time config:
	private static final String CLPATH = "+classpath=${jpf-symbc}/build";
	private static final String REALTIME_PLATFORM = "+symbolic.realtime.platform = jop";
	private static final String SYMRT = "+symbolic.realtime.targetsymrt = false";
	private static final String REALTIME_PATH = "+symbolic.realtime.outputbasepath = ./output";
	private static final String OPTIMIZE = "+symbolic.realtime.optimize = false";
	
	private static final String SOLVER = "+symbolic.dp=choco";
	
	
	private static final String[] JPF_ARGS = {INSN_FACTORY,
											  OUTPUTPATH,
											  FORMAT,
											  CACHE,
											  LISTENER, 
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
			Example test = new Example();
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
