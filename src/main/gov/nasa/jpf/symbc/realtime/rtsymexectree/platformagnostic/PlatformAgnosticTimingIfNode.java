/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.platformagnostic;

import gov.nasa.jpf.symbc.realtime.rtsymexectree.IRealTimeNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTIfNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTStdNode;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class PlatformAgnosticTimingIfNode extends RTIfNode implements IPlatformAgnosticRealTimeNode {

	/**
	 * @param instructionContext
	 */
	public PlatformAgnosticTimingIfNode(InstrContext instructionContext) {
		super(instructionContext);
	}

	public PlatformAgnosticTimingIfNode(InstrContext instructionContext, SymbolicExecutionTree tree) {
		super(instructionContext, tree);
	}
}
