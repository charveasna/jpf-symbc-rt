/**
 * 
 */
package gov.nasa.jpf.symbc.realtime;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

import uppaal.NTA;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.bytecode.FieldInstruction;
import gov.nasa.jpf.jvm.bytecode.GETFIELD;
import gov.nasa.jpf.jvm.bytecode.GETSTATIC;
import gov.nasa.jpf.jvm.bytecode.PUTFIELD;
import gov.nasa.jpf.jvm.bytecode.PUTSTATIC;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.realtime.loopbounds.LoopBound;
import gov.nasa.jpf.symbc.realtime.loopbounds.LoopBoundExtractor;
import gov.nasa.jpf.symbc.realtime.ntaanalysis.INTAAnalysis;
import gov.nasa.jpf.symbc.realtime.optimization.RTOptimizer;
import gov.nasa.jpf.symbc.realtime.optimization.SeqInstructionReduction;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.CACHE_POLICY;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.JOPCacheSimUppaalTranslator;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.JOPUppaalTranslator;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.JOPNodeFactory;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.JOPTiming;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.JOPWCATiming;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.JOP_TIMING_MODEL;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.cache.AJOPCacheBuilder;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.cache.FIFOCache;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.cache.FIFOVarBlockCache;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.cache.JOP_CACHE;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.cache.LRUCache;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.platformagnostic.PlatformAgnosticTimingNodeFactory;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.platformagnostic.PlatformAgnosticUppaalTranslator;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.timingdoc.TimingDocNodeFactory;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.timingdoc.TimingDocUppaalTranslator;
import gov.nasa.jpf.symbc.realtime.util.EnteredMethodsSet;
import gov.nasa.jpf.symbc.symexectree.ASymbolicExecutionTreeListener;
import gov.nasa.jpf.symbc.symexectree.NodeFactory;
import gov.nasa.jpf.symbc.symexectree.SymExecTreeUtils;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class UppaalTranslationListener extends ASymbolicExecutionTreeListener {
	/**
	 * This is the listener used for translating the symbolic execution tree generated
	 * by SPF to a timed automaton amenable to model checking using UPPAAL.
	 * The configurations for this listener are:
	 * 
	 * symbolic.realtime.platform 					=	[jop|agnostic|timingdoc]	(default: jop)
	 * symbolic.realtime.targetsymrt	 			=	[true|false]				(default: false)
	 * symbolic.realtime.outputbasepath 			=	<output path>				(default: ./)
	 * symbolic.realtime.optimize 					= 	[true|false]				(default: true)
	 * symbolic.realtime.progressmeasure			= 	[true|false]				(default: true)
	 * symbolic.realtime.generatequeries 			= 	[true|false]				(default: true)
	 * 
	 * If the target platform is 'timingdoc', a Timing Doc - describing the execution
	 * times of the individual Java Bytecodes of the particular platform - must be
	 * supplied as well using:
	 * symbolic.realtime.timingdoc.path 			= 	<source path>
	 * 
	 * ------JOP-specific settings---------------------------------------------------------
	 * symbolic.realtime.jop.cachepolicy			=	[miss|hit|simulate]			(default: miss)
	 * symbolic.realtime.jop.timingmodel			=	[handbook|thesis]			(default: handbook)
	 * symbolic.realtime.jop.cachetype				=	[fifovarblock|fifo|lru]		(default: fifovarblock (Applies only for "simulate" cache policy. Currently only support for fifo))
	 * symbolic.realtime.jop.cache.blocks			=	[:number:]					(default: 16)
	 * symbolic.realtime.jop.cache.size				=	[:number:]					(default: 1024)
	 * symbolic.realtime.jop.ram_cnt				=	[:number:]					(default: 2 (applies for Cyclone EP1C6@100Mhz and 15ns SRAM))
	 * symbolic.realtime.jop.rws					=	[:number:]					(will supersede *.jop.ram_cnt if set (1 applies for Cyclone EP1C6@100Mhz and 15ns SRAM))
	 * symbolic.realtime.jop.wws					=	[:number:]					(will supersede *.jop.ram_cnt if set (1 applies for Cyclone EP1C6@100Mhz and 15ns SRAM))
	 */
	
	private int injectedSymbID;
	private EnteredMethodsSet enteredMethods; //only used for cache simulation in JOP
	private RTConfig rtConf;
	
	private HashSet<ElementInfo> visitedEi = new HashSet<ElementInfo>();
	private LinkedList<INTAAnalysis> ntaAnalyses;

	public UppaalTranslationListener(Config conf, JPF jpf) {
		super(conf, jpf);
		this.injectedSymbID = 0;
		this.ntaAnalyses = new LinkedList<>();
		this.enteredMethods = new EnteredMethodsSet();
	}

	@Override
	public void instructionExecuted(VM vm, ThreadInfo currentThread, Instruction nextInstruction, Instruction executedInstruction) {
		if (!vm.getSystemState().isIgnored()) {
			MethodInfo mi = executedInstruction.getMethodInfo();
			if(SymExecTreeUtils.isInSymbolicCallChain(mi, currentThread.getTopFrame(), this.jpfConf)) {
				if(executedInstruction.isBackJump()) {
					handleLoop(executedInstruction, vm);
				}
			}
		}
	}
	
	@Override
	protected NodeFactory getNodeFactory() {
		//This is a small hack because getnodefactory is called before initialising the instance variables in the constructor
		this.rtConf = new RTConfig(super.jpfConf);
		
		switch(this.rtConf.getValue(RTConfig.PLATFORM, RTPLATFORM.class)) {
			case AGNOSTIC:
				return new PlatformAgnosticTimingNodeFactory();
			case TIMINGDOC:
				return new TimingDocNodeFactory(this.rtConf);
			case JOP:
			default:
				return new JOPNodeFactory(this.rtConf, RTConfig.isConfSet(RTConfig.JOP_RWS, super.jpfConf));
		}
	}
	
	@Override
	public void executeInstruction(VM vm, ThreadInfo currentThread, Instruction instructionToExecute) {
		super.executeInstruction(vm, currentThread, instructionToExecute);
		if (!vm.getSystemState().isIgnored()) {
			MethodInfo mi = instructionToExecute.getMethodInfo();
			if(SymExecTreeUtils.isInSymbolicCallChain(mi, currentThread.getTopFrame(), this.jpfConf)) {
				if(instructionToExecute instanceof GETFIELD ||
					instructionToExecute instanceof GETSTATIC) {
						ThreadInfo ti = vm.getCurrentThread();
						if(ti.getTopFrame() != null) {
							if(ti.getTopFrame().getSlots().length > 0) {
								FieldInstruction fieldInstr = (FieldInstruction) instructionToExecute;
								ElementInfo ei = fieldInstr.peekElementInfo(ti);
								if(ei != null) {
									if(ei.isShared()) {
										if(!ei.hasFieldAttr(Expression.class) && !ei.isFrozen()) {//Assuming the if the field already has an attr of type Expression, it is symbolic.
											FieldInfo fi = fieldInstr.getFieldInfo();
											ei.addFieldAttr(fi, new SymbolicInteger("SHARED SYMB " + this.injectedSymbID++));
										}
									}
								}
							}
						}
					}

				if(instructionToExecute instanceof PUTFIELD ||
				   instructionToExecute instanceof PUTSTATIC) {
					ThreadInfo ti = vm.getCurrentThread();
					FieldInstruction putInstr = (FieldInstruction) instructionToExecute;
					
					ElementInfo eiOwner = putInstr.peekElementInfo(ti);
					FieldInfo fi = putInstr.getFieldInfo();
					if(fi.isReference() && eiOwner.isShared()) {
						int objRef = ti.getTopFrame().peek();
						if(objRef == -1) {
						} else {
							ElementInfo ei = ti.getElementInfo(objRef);
							this.setSharedness(ei, ti);
						}
					}					
				}
			}
		}
	}
	
	private void setSharedness(ElementInfo ei, ThreadInfo ti) {
		this.visitedEi.clear();
		recursivelySetSharedness(ei, ti);
	}
	
	private void recursivelySetSharedness(ElementInfo ei, ThreadInfo ti) {
		if(visitedEi.contains(ei))
			return;
		ClassInfo ci = ei.getClassInfo();
		FieldInfo[] fis = ci.getDeclaredInstanceFields();
		
		for(FieldInfo fi : fis) {
			if(fi.isReference()) {
				int objRef = ei.getReferenceField(fi);
				if(objRef == -1)
					continue;
				ElementInfo thisEi = ti.getElementInfo(objRef);
				if(thisEi == null)
					throw new RuntimeException("ElementInfo is null!");
				thisEi.setShared(true);
				visitedEi.add(thisEi);
				recursivelySetSharedness(thisEi, ti);
			}
		}
	}
	
	@Override
	public void searchConstraintHit(Search search) {
		if (!search.isEndState() && !search.isErrorState()) {
			String searchDepth = super.jpfConf.getString("search.depth_limit");
			System.err.println("Warning: Search depth " + searchDepth + " has been hit! You may want to increase the bound or adjust loop bounds. Otherwise, the timing model is possibly unsafe!");
		}
	}
	
	@Override
	public void methodEntered (VM vm, ThreadInfo currentThread, MethodInfo enteredMethod) {
		if (!vm.getSystemState().isIgnored()) {
			if(SymExecTreeUtils.isInSymbolicCallChain(enteredMethod, currentThread.getTopFrame(), this.jpfConf)) {
				this.enteredMethods.add(enteredMethod);
			}
		}
	}
	
	private static class LoopProcessedMarker { public boolean containedBound;}
	private void handleLoop(Instruction instr, VM vm) {
		if(!instr.hasAttr(LoopProcessedMarker.class)) {
			String fileLocation = instr.getFileLocation();
			fileLocation = fileLocation.substring(0, fileLocation.indexOf(':'));
			LoopBound lb = new LoopBound(LoopBoundExtractor.extractBound(fileLocation, instr.getLineNumber()));
			LoopProcessedMarker marker = new LoopProcessedMarker();
			if(lb.getLoopBound() >= 0) {
				instr.addAttr(lb);
				marker.containedBound = true;
			} else
				marker.containedBound = false;
			instr.addAttr(marker);
		}
		if(instr.getAttr(LoopProcessedMarker.class).containedBound) {
			LoopBound lb = instr.getAttr(LoopBound.class);
			int newBound = lb.getLoopBound() - 1;
			if (newBound <= 0){
				vm.getSystemState().setIgnored(true);
			}
			lb.setLoopBound(newBound);
		}
	}
	
	@Override
	protected void processSymbExecTree(LinkedList<SymbolicExecutionTree> trees) {
		if(trees.isEmpty())
			throw new UppaalTranslatorException("No symbolic execution trees were generated! Have you set the target method correctly?");
		boolean reduceCacheAffectedNodes = true;
		CACHE_POLICY cachePol = this.rtConf.getValue(RTConfig.JOP_CACHE_POLICY, CACHE_POLICY.class);
		if(cachePol == CACHE_POLICY.SIMULATE)
			reduceCacheAffectedNodes = false;
		AUppaalTranslator translator = this.constructUppaalTranslator(this.rtConf);
		RTOptimizer optimizer = null;
		if(this.rtConf.getValue(RTConfig.OPTIMIZE, Boolean.class)) {
			optimizer = new RTOptimizer();
			optimizer.addOptimization(new SeqInstructionReduction(this.rtConf.getValue(RTConfig.TARGET_SYMRT, Boolean.class), reduceCacheAffectedNodes));
		}
		for(SymbolicExecutionTree tree : trees) {
			if(this.rtConf.getValue(RTConfig.OPTIMIZE, Boolean.class))
				optimizer.optimize(tree);
			NTA ntaSystem = translator.translateSymTree(tree);
			//We write the config to the model file
			StringBuilder configStr = new StringBuilder();
			configStr.append("/*Configuration for this timing model:\n").append(rtConf.getAllSettings().toStringIfConfigSet(super.jpfConf)).append("\n*/");
			ntaSystem.getDeclarations().add(configStr.toString());
			
			for(INTAAnalysis ntaAnalysis : this.ntaAnalyses) {
				ntaAnalysis.conductAnalysis(ntaSystem);
			}
			ntaSystem.writePrettyLayoutModelToFile(this.getNTAFileName(ntaSystem, tree));
			if(this.rtConf.getValue(RTConfig.GENERATE_QUERIES, Boolean.class))
				QueriesFileGenerator.writeQueriesFile(ntaSystem, getQueriesFileName(ntaSystem, tree));
			try {
				System.out.println("Wrote model of target method " + tree.getTargetMethod().getShortMethodName() + " to " + new File(this.rtConf.getValue(RTConfig.OUTPUT_BASE_PATH, String.class)).getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private AUppaalTranslator constructUppaalTranslator(RTConfig rtConf) {
		AUppaalTranslator translator = null;
		switch(this.rtConf.getValue(RTConfig.PLATFORM, RTPLATFORM.class)) {
			case AGNOSTIC:
				translator = new PlatformAgnosticUppaalTranslator(rtConf);
				break;
			case TIMINGDOC:
				translator = new TimingDocUppaalTranslator(rtConf);
				break;
			case JOP:
			default:
				CACHE_POLICY cachePol = this.rtConf.getValue(RTConfig.JOP_CACHE_POLICY, CACHE_POLICY.class);
				if(cachePol == CACHE_POLICY.SIMULATE)
					translator = new JOPCacheSimUppaalTranslator(rtConf, this.enteredMethods);
				else
					translator = new JOPUppaalTranslator(rtConf);
		}
		return translator;
	}
	
	private String getNTAFileName(NTA nta, SymbolicExecutionTree tree) {
		return this.getBaseFileName(nta, tree) + ".xml";
	}
	
	private String getQueriesFileName(NTA nta, SymbolicExecutionTree tree) {
		return this.getBaseFileName(nta, tree) + ".q";
	}
	private String getBaseFileName(NTA nta, SymbolicExecutionTree tree) {
		String basePath = this.rtConf.getValue(RTConfig.OUTPUT_BASE_PATH, String.class);
		return basePath + (basePath.endsWith("/") ? "" : "/") + tree.getTargetMethod().getMethodName() + "_SPF";
	}
}
