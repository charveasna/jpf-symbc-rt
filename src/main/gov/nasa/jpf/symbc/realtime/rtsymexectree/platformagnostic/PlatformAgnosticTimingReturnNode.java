/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.platformagnostic;

import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTReturnNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTStdNode;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class PlatformAgnosticTimingReturnNode extends RTReturnNode implements IPlatformAgnosticRealTimeNode {

	/**
	 * @param instructionContext
	 */
	public PlatformAgnosticTimingReturnNode(InstrContext instructionContext) {
		super(instructionContext);
	}

	public PlatformAgnosticTimingReturnNode(InstrContext instructionContext, SymbolicExecutionTree tree) {
		super(instructionContext, tree);
	}
}
