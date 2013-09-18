/**
 * 
 */
package gov.nasa.jpf.symbc.realtime;

import gov.nasa.jpf.symbc.InvokeTest;
import gov.nasa.jpf.symbc.realtime.minepump.scj.PeriodicMethaneDetectionEventHandler;
import gov.nasa.jpf.symbc.realtime.minepump.scj.PeriodicWaterLevelDetectionEventHandler;

import org.junit.Test;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class TestVisualizeMinepump extends InvokeTest {
	private static final String SYM_METHOD = "+symbolic.method=gov.nasa.jpf.symbc.realtime.minepump.scj.PeriodicMethaneDetectionEventHandler.run()";

	//private static final String SYM_METHOD = "+symbolic.method=gov.nasa.jpf.symbc.realtime.minepump.scj.PeriodicWaterLevelDetectionEventHandler.run()";
	private static final String LISTENER = "+listener = gov.nasa.jpf.symbc.symexectree.visualizer.SymExecTreeVisualizerListener";

	//Real time config:
	private static final String CLPATH = "+classpath=${jpf-symbc}/lib/scjNoRelativeTime_1.0.0.jar;${jpf-symbc}/build";
	private static final String OUTPUTPATH = "+symbolic.visualizer.basepath = ${jpf-symbc-rt}";
	private static final String FORMAT = "+symbolic.visualizer.outputformat = pdf";
	
	private static final String[] JPF_ARGS = {INSN_FACTORY, 
											  LISTENER, 
											  SYM_METHOD, 
											  CLPATH,
											  OUTPUTPATH,
											  FORMAT};
	@Test
	public void mainTest() {
		if (verifyNoPropertyViolation(JPF_ARGS)) {
			PeriodicMethaneDetectionEventHandler.main(null);
			//PeriodicWaterLevelDetectionEventHandler.main(null);
		}
	}
}
