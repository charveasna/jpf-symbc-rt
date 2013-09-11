/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.platformagnostic;

import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.NodeFactory;
import gov.nasa.jpf.symbc.symexectree.structure.IfNode;
import gov.nasa.jpf.symbc.symexectree.structure.InvokeNode;
import gov.nasa.jpf.symbc.symexectree.structure.Node;
import gov.nasa.jpf.symbc.symexectree.structure.ReturnNode;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class PlatformAgnosticTimingNodeFactory extends NodeFactory {

	@Override
	public Node constructStdNode(InstrContext instrCtx) {
		return new PlatformAgnosticTimingStdNode(instrCtx);
	}

	@Override
	public IfNode constructIfNode(InstrContext instrCtx) {
		return new PlatformAgnosticTimingIfNode(instrCtx);
	}

	@Override
	public InvokeNode constructInvokeNode(InstrContext instrCtx) {
		return new PlatformAgnosticTimingInvokeNode(instrCtx);
	}

	@Override
	public ReturnNode constructReturnNode(InstrContext instrCtx) {
		return new PlatformAgnosticTimingReturnNode(instrCtx);
	}
}
