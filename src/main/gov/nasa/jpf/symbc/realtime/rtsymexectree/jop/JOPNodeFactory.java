/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.jop;

import gov.nasa.jpf.symbc.realtime.RTNodeFactory;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.NodeFactory;
import gov.nasa.jpf.symbc.symexectree.structure.IfNode;
import gov.nasa.jpf.symbc.symexectree.structure.InvokeNode;
import gov.nasa.jpf.symbc.symexectree.structure.MonitorEnterNode;
import gov.nasa.jpf.symbc.symexectree.structure.MonitorExitNode;
import gov.nasa.jpf.symbc.symexectree.structure.Node;
import gov.nasa.jpf.symbc.symexectree.structure.ReturnNode;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class JOPNodeFactory extends RTNodeFactory {

	@Override
	public Node constructStdNode(InstrContext instrCtx) {
		return new JOPStdNode(instrCtx);
	}

	@Override
	public IfNode constructIfNode(InstrContext instrCtx) {
		return new JOPIfNode(instrCtx);
	}

	@Override
	public InvokeNode constructStdInvokeNode(InstrContext instrCtx) {
		return new JOPInvokeNode(instrCtx);
	}

	@Override
	public ReturnNode constructReturnNode(InstrContext instrCtx) {
		return new JOPReturnNode(instrCtx);
	}

	@Override
	public MonitorEnterNode constructMonitorEnterNode(InstrContext instrCtx) {
		return new JOPMonitorEnterNode(instrCtx);
	}

	@Override
	public MonitorExitNode constructMonitorExitNode(InstrContext instrCtx) {
		return new JOPMonitorExitNode(instrCtx);
	}

	@Override
	public InvokeNode constructFireSporadicEventNode(InstrContext instrCtx) {
		return new JOPFireSporadicNode(instrCtx);
	}

}
