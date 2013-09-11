/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.platformagnostic;

import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTInvokeNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTStdNode;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class PlatformAgnosticTimingInvokeNode extends RTInvokeNode implements IPlatformAgnosticRealTimeNode {

	/**
	 * @param instructionContext
	 */
	public PlatformAgnosticTimingInvokeNode(InstrContext instructionContext) {
		super(instructionContext);
	}

	public PlatformAgnosticTimingInvokeNode(InstrContext instructionContext, SymbolicExecutionTree tree) {
		super(instructionContext, tree);
	}
}
