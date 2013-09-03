/**
 * 
 */
package gov.nasa.jpf.symbc.realtime;

import gov.nasa.jpf.symbc.InvokeTest;
import gov.nasa.jpf.symbc.realtime.simple.SimpleSys;
import gov.nasa.jpf.symbc.realtime.simple.TestSimpleSys;

import org.junit.Test;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class TestControlFlowSys extends InvokeTest {

	private static final String SYM_METHOD = "+symbolic.method=gov.nasa.jpf.symbc.realtime.ControlFlowSys.computation(sym)";
	
	private static final String LISTENER = "+listener = gov.nasa.jpf.symbc.realtime.UppaalTranslationListener";
	private static final String REALTIME_PLATFORM = "+symbolic.realtime.platform = jop";
	private static final String TETASARTS = "+symbolic.realtime.targettetasarts = false";
	private static final String REALTIME_PATH = "+symbolic.realtime.outputbasepath = ./output";
	private static final String OPTIMIZE = "+symbolic.realtime.optimize = true";
	
	private static final String SOLVER = "+symbolic.dp=choco";
	
	
	//remember the nosolver option is like a cfg traversal!
	
	//private static final String SOLVER = "+symbolic.dp=no_solver";
	private static final String[] JPF_ARGS = {INSN_FACTORY, 
											  TETASARTS, 
											  LISTENER,
											  SYM_METHOD, 
											  OPTIMIZE, 
											  REALTIME_PATH, 
											  REALTIME_PLATFORM,
											  SOLVER};

	
	public static void main(String[] args) {
		TestSimpleSys testInvocation = new TestSimpleSys();
		testInvocation.mainTest();		
	}
	
	@Test
	public void mainTest() {
		if (verifyNoPropertyViolation(JPF_ARGS)) {
			ControlFlowSys test = new ControlFlowSys();
			test.computation(false);
		}
	}
	

	
}
