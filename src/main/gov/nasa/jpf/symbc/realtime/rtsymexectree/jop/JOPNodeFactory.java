/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.jop;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.symbc.realtime.RTConfig;
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
	private String targetMethod;
	
	public JOPNodeFactory(CACHE_POLICY cachePol, JOPTiming jopTiming, String targetMethod) {
		this.jopTiming = jopTiming;
		this.cachePol = cachePol;
		this.targetMethod = targetMethod;
	}
	
	public JOPNodeFactory(RTConfig rtConf, boolean useWaitStates) {
		this(rtConf.getValue(RTConfig.JOP_CACHE_POLICY, CACHE_POLICY.class), 
			 getJOPTimingModel(rtConf, useWaitStates), 
			 rtConf.getValue(RTConfig.SYM_TARGET_METHOD, String.class));
	}
	
	private static JOPTiming getJOPTimingModel(RTConfig rtConf, boolean useWaitStates) {
		JOP_TIMING_MODEL tModel = rtConf.getValue(RTConfig.JOP_TIMINGMODEL, JOP_TIMING_MODEL.class);
		if(useWaitStates) {
			int readWaitStates = rtConf.getValue(RTConfig.JOP_RWS, Integer.class);
			int writeWaitStates = rtConf.getValue(RTConfig.JOP_WWS, Integer.class);
			switch(tModel) {
				case THESIS:
					return new JOPWCATiming(readWaitStates, writeWaitStates);
				case HANDBOOK:
				default:
					return new JOPTiming(readWaitStates, writeWaitStates);
			}
		} else {
			int ram_cnt = rtConf.getValue(RTConfig.JOP_RAM_CNT, Integer.class);
			switch (tModel) {
			case THESIS:
				return new JOPWCATiming(ram_cnt);
			case HANDBOOK:
			default:
				return new JOPTiming(ram_cnt);
			}
		}
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
		return new JOPReturnNode(instrCtx, this.jopTiming, this.cachePol, this.targetMethod);
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
