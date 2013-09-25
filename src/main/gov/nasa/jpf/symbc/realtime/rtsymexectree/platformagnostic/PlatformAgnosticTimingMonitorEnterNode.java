/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.platformagnostic;

import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTMonitorEnterNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTStdNode;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class PlatformAgnosticTimingMonitorEnterNode extends RTMonitorEnterNode implements IPlatformAgnosticRealTimeNode {

	/**
	 * @param instructionContext
	 */
	public PlatformAgnosticTimingMonitorEnterNode(InstrContext instructionContext) {
		super(instructionContext);
	}

	public PlatformAgnosticTimingMonitorEnterNode(InstrContext instructionContext, SymbolicExecutionTree tree) {
		super(instructionContext, tree);
	}
}
