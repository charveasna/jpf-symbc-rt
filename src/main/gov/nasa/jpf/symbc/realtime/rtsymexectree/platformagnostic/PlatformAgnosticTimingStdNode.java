/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.platformagnostic;

import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTStdNode;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class PlatformAgnosticTimingStdNode extends RTStdNode implements IPlatformAgnosticRealTimeNode {

	/**
	 * @param instructionContext
	 */
	public PlatformAgnosticTimingStdNode(InstrContext instructionContext) {
		super(instructionContext);
	}

	public PlatformAgnosticTimingStdNode(InstrContext instructionContext, SymbolicExecutionTree tree) {
		super(instructionContext, tree);
	}
}
