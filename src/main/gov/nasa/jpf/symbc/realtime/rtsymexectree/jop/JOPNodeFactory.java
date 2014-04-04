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

	private JOPTiming jopTiming;
	private CACHE_POLICY cachePol;
	
	public JOPNodeFactory(CACHE_POLICY cachePol, JOPTiming jopTiming) {
		this.jopTiming = jopTiming;
		this.cachePol = cachePol;
	}
	
	@Override
	public Node constructStdNode(InstrContext instrCtx) {
		return new JOPStdNode(instrCtx, this.jopTiming);
	}

	@Override
	public IfNode constructIfNode(InstrContext instrCtx) {
		return new JOPIfNode(instrCtx, this.jopTiming);
	}

	@Override
	public InvokeNode constructStdInvokeNode(InstrContext instrCtx) {
		return new JOPInvokeNode(instrCtx, this.jopTiming, this.cachePol);
	}

	@Override
	public ReturnNode constructReturnNode(InstrContext instrCtx) {
		return new JOPReturnNode(instrCtx, this.jopTiming, this.cachePol);
	}

	@Override
	public MonitorEnterNode constructMonitorEnterNode(InstrContext instrCtx) {
		return new JOPMonitorEnterNode(instrCtx, this.jopTiming);
	}

	@Override
	public MonitorExitNode constructMonitorExitNode(InstrContext instrCtx) {
		return new JOPMonitorExitNode(instrCtx, this.jopTiming);
	}

	@Override
	public InvokeNode constructFireSporadicEventNode(InstrContext instrCtx) {
		return new JOPFireSporadicNode(instrCtx, this.jopTiming, this.cachePol);
	}

}
