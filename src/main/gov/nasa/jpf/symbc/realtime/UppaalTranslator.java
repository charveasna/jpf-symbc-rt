/**
 * 
 */
package gov.nasa.jpf.symbc.realtime;

import gov.nasa.jpf.jvm.bytecode.IfInstruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
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
public class UppaalTranslator {
	private HashMap<Node, Location> visitedTreeNodesMap;
	private boolean targetSymRT;
	private int uniqueID;
	
	private Location finalLoc;
	
	public UppaalTranslator(boolean targetSymRT) {
		this.targetSymRT = targetSymRT;
		this.uniqueID = 0;
	}
	
	public NTA translateSymTree(SymbolicExecutionTree tree) {
		this.visitedTreeNodesMap = new HashMap<Node, Location>(); 
		
		Automaton ta = new Automaton(getTemplateName(tree.getTargetMethod()));
		Location initLoc = new Location(ta, "initLoc");
		ta.setInit(initLoc);
		this.finalLoc = new Location(ta, "final");
		this.finalLoc.setType(LocationType.COMMITTED);
		ta.getDeclaration().add("clock executionTime;");

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
		
		NTA nta = new NTA();
		if(!this.targetSymRT) {
			nta.getSystemDeclaration().addSystemInstance(ta.getName().getName());
			nta.getDeclarations().add("clock executionTime;");
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
			uppaal.Transition uppFinalTrans = new uppaal.Transition(ta, targetLoc, this.finalLoc);
			patchTransition(uppFinalTrans, treeNode);
		} else {
			for(Transition t : outTransitions) {
				uppaal.Transition uppTrans = new uppaal.Transition(ta, targetLoc, recursivelyTraverseSymTree(t.getDstNode(), ta));
				this.patchTransition(uppTrans, treeNode);
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
	
	private void patchTransition(uppaal.Transition uppTrans, Node treeNode) {
		if(!(treeNode instanceof IHasBCET) &&
		   !(treeNode instanceof IHasWCET)) {
			if(this.targetSymRT)
				uppTrans.setGuard("running[tID] == true");
			uppTrans.setSync(new Synchronization("jvm_execute", SyncType.INITIATOR));
			uppTrans.addUpdate("jvm_instruction = JVM_" + treeNode.getInstructionContext().getInstr().getMnemonic().toUpperCase());
		} else if((treeNode instanceof IHasBCET) &&
				  (treeNode instanceof IHasWCET)) {
			uppTrans.setGuard("executionTime >= " + ((IHasBCET) treeNode).getBCET() + "&&\n" +
							  "executionTime <= " + ((IHasWCET) treeNode).getWCET());
			uppTrans.addUpdate("executionTime = 0");
		}
		else if(treeNode instanceof IHasWCET) {
			uppTrans.setGuard("executionTime == " + ((IHasWCET) treeNode).getWCET());
			uppTrans.addUpdate("executionTime = 0");
		}
		
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
				
	}
	
	private Location translateTreeNode(Node treeNode, Automaton ta) {
		Instruction instr = treeNode.getInstructionContext().getInstr();
		Location newLoc = new Location(ta, instr.getMnemonic() + "_" + this.getUniqueIDString());
		boolean isStaticET = false;
		StringBuilder invariantBuilder = new StringBuilder();
		if(treeNode instanceof IHasWCET) {
			invariantBuilder.append("executionTime <= ")
							.append(((IHasWCET) treeNode).getWCET());
			isStaticET = true;
		}
		/*if(treeNode instanceof IHasBCET) {
			invariantBuilder.append("&&\n")
							.append("executionTime >= ")
							.append(((IHasBCET) treeNode).getBCET());
			isStaticET = true;
		}*/
		if(targetSymRT && isStaticET) {
			invariantBuilder.append("&&\n")
							.append("executionTime' == running[tID]");
		}
		newLoc.setInvariant(invariantBuilder.toString());
		newLoc.setComment(instr.getFilePos());
		return newLoc;
	}
}
