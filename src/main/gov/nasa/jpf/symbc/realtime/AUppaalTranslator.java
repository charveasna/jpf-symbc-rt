/**
 * 
 */
package gov.nasa.jpf.symbc.realtime;

import gov.nasa.jpf.jvm.bytecode.IfInstruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.symbc.realtime.optimization.pm.ProgressMeasureUtil;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IHasBCET;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IHasWCET;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTFireSporadicNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.SporadicEventSpecException;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.MethodDesc;
import gov.nasa.jpf.symbc.symexectree.Transition;
import gov.nasa.jpf.symbc.symexectree.structure.MonitorEnterNode;
import gov.nasa.jpf.symbc.symexectree.structure.MonitorExitNode;
import gov.nasa.jpf.symbc.symexectree.structure.Node;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;
import gov.nasa.jpf.symbc.symexectree.visualizer.VisualizerException;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import uppaal.Automaton;
import uppaal.Location;
import uppaal.NTA;
import uppaal.Location.LocationType;
import uppaal.labels.Guard;
import uppaal.labels.Synchronization;
import uppaal.labels.Synchronization.SyncType;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public abstract class AUppaalTranslator {
	private HashMap<Node, Location> visitedTreeNodesMap;
	private boolean generateProgressMeasure;
	protected boolean targetSymRT;
	protected static final String PM_VAR_N = "pm";
	protected static final String JBC_CLOCK_N = "jbcExecTime";
	protected NTA nta;
	private int uniqueID;
	private Location finalLoc;
	
	public AUppaalTranslator(boolean targetSymRT, boolean useProgressMeasure) {
		this.targetSymRT = targetSymRT;
		this.generateProgressMeasure = useProgressMeasure;
		if(this.generateProgressMeasure && this.targetSymRT)
			throw new RealTimeRuntimeException("Progress measures are currently not supported in SymRT");
		this.uniqueID = 0;
		this.nta = new NTA();
	}
	
	public AUppaalTranslator(RTConfig rtConf) {
		this(rtConf.getValue(RTConfig.TARGET_SYMRT, Boolean.class), rtConf.getValue(RTConfig.PROGRESS_MEASURE, Boolean.class));
	}
	
	public NTA translateSymTree(SymbolicExecutionTree tree) {
		this.visitedTreeNodesMap = new HashMap<Node, Location>(); 
		Automaton ta = new Automaton(getTemplateName(tree.getTargetMethod()));
		Location initLoc = new Location(ta, "initLoc");
		ta.setInit(initLoc);
		this.finalLoc = new Location(ta, "final");
		this.finalLoc.setType(LocationType.COMMITTED);
		ta.getDeclaration().add("clock " + JBC_CLOCK_N + ";");

		Location taRoot = recursivelyTraverseSymTree(tree.getRootNode(), ta);
		uppaal.Transition initTrans = new uppaal.Transition(ta, initLoc, taRoot);
		if(this.targetSymRT) {
			ta.setParameter("const ThreadID tID");
			uppaal.Transition finalTrans = new uppaal.Transition(ta, finalLoc, initLoc);
			finalTrans.setSync(new Synchronization("run[tID]", SyncType.INITIATOR));
			initTrans.setSync(new Synchronization("run[tID]", SyncType.RECEIVER));
		} else {
			initLoc.setType(LocationType.COMMITTED);
		}

		if(!this.targetSymRT) {
			nta.getSystemDeclaration().addSystemInstance(ta.getName().getName());
			nta.getDeclarations().add("clock executionTime;");
		}
		if(this.generateProgressMeasure) {
			int pmUB = ProgressMeasureUtil.calculateMaxBranches(tree);
			nta.getDeclarations().add("int[0," + pmUB + "] " +  PM_VAR_N + " = 0;");
			nta.getSystemDeclaration().addProgressMeasure(PM_VAR_N);
		}
		
		nta.addAutomaton(ta);
		return nta;
	}
	
	private String getTemplateName(MethodDesc mDesc) {
		String[] splitMethDesc = mDesc.getMethodName().split("\\.");
		String templateName;
		if(splitMethDesc.length > 1) {
			templateName = splitMethDesc[splitMethDesc.length - 2] + "_" + splitMethDesc[splitMethDesc.length - 1]; 
		} else {
			templateName = splitMethDesc[splitMethDesc.length - 1];
		}
		
		return templateName + "_" + mDesc.getArgsNum();
	}
	
	private String getUniqueIDString() {
		return Integer.toString(this.uniqueID++);
	}
	
	private Location recursivelyTraverseSymTree(Node treeNode, Automaton ta) {
		if(visitedTreeNodesMap.containsKey(treeNode)) {
			return visitedTreeNodesMap.get(treeNode);
		}
		List<Transition> outTransitions = treeNode.getOutgoingTransitions();
		Location targetLoc = null;
		if(skipNode(treeNode)) {
			for(Transition outTrans : outTransitions) {
				return recursivelyTraverseSymTree(outTrans.getDstNode(), ta);
			}
		}
		
		targetLoc = translateTreeNode(treeNode, ta);
		visitedTreeNodesMap.put(treeNode, targetLoc);
		
		//A leaf has been hit, so we add a transition to the final location
		if(outTransitions.isEmpty()) {
			Location newSrc = createTransition(ta, treeNode, targetLoc, this.finalLoc);
			targetLoc = newSrc;
		} else {
			for(Transition t : outTransitions) {
				Location newSrc = createTransition(ta, treeNode, targetLoc, recursivelyTraverseSymTree(t.getDstNode(), ta));
				targetLoc = newSrc;
			}
		}
		return targetLoc;
	}
	
	/*We skip certain nodes when translating to UPPAAL
	 * Internally, JPF has certain instructions e.g. executenative,
	 * directcallreturn, nativereturn etc. which are not part of
	 * the JVM spec and as such, do not have an execution time
	 * defined by most platforms.
	 */
	private boolean skipNode(Node node) {
		Instruction instr = node.getInstructionContext().getInstr();
		if(instr.getByteCode() > 255)
			return true;
		/*if(instr.getMethodInfo().getClassInfo().getPackageName().contains("gov.nasa.jpf.symbc") &&
		   instr.getMethodInfo().getClassName().equals("Debug"))*/
		if(this.isInCallChain(node.getInstructionContext(), "gov.nasa.jpf.symbc", "Debug"))
			return true;
		return false;
	}
	
	private Location createTransition(Automaton ta, Node treeNode, Location src, Location target) {
		uppaal.Transition uppTrans = new uppaal.Transition(ta, src, target);
		Location newSrc = this.decoratePlatformDependentTransition(ta, uppTrans, treeNode);
		if(this.targetSymRT &&
		   treeNode instanceof MonitorEnterNode)
			uppTrans.addUpdate("monitorEnter()");
		else if(this.targetSymRT && 
		        treeNode instanceof MonitorExitNode)
			uppTrans.addUpdate("monitorExit()");
		
		else if(this.targetSymRT && 
		        treeNode instanceof RTFireSporadicNode) {
			RTFireSporadicNode spoNode = (RTFireSporadicNode)treeNode;
			String chanName = "fire[" + spoNode.getSporadicEventID() + "]";
			uppTrans.setSync(new Synchronization(chanName, SyncType.INITIATOR));
			
			Guard prevGuard = uppTrans.getGuard();
			String finalGuard = "fireable[" + spoNode.getSporadicEventID() + "]";
			if(prevGuard != null)
				finalGuard += " &&\n" + prevGuard.toString();
			uppTrans.setGuard(finalGuard);
		}
		
		if(treeNode.getOutgoingTransitions().size() > 1 && this.generateProgressMeasure) {
			uppTrans.addUpdate(PM_VAR_N + "++");
		}
		return newSrc;
	}
	
	private boolean isInCallChain(InstrContext instrCtx, String packageName, String className) {
		StackFrame frame = instrCtx.getFrame();
		while(frame != null) {
			ClassInfo cInfo = frame.getMethodInfo().getClassInfo();
			if(cInfo.getPackageName().equals(packageName) &&
			   cInfo.getSimpleName().equals(className))
				return true;
			frame = frame.getPrevious();
		}
		return false;
	}
	
	protected abstract Location decoratePlatformDependentTransition(Automaton ta, uppaal.Transition uppTrans, Node treeNode);
	
	protected String getLocationName(Node treeNode) {
		Instruction instr = treeNode.getInstructionContext().getInstr();
		return instr.getMnemonic() + "_" + this.getUniqueIDString();
	}
	
	protected abstract Location translateTreeNode(Node treeNode, Automaton ta);
}
