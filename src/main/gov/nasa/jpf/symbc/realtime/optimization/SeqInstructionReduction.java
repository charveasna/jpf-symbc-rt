/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.optimization;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import gov.nasa.jpf.symbc.realtime.rtsymexectree.IHasBCET;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IHasWCET;
import gov.nasa.jpf.symbc.symexectree.SymbolicExecutionTreeVisitor;
import gov.nasa.jpf.symbc.symexectree.Transition;
import gov.nasa.jpf.symbc.symexectree.structure.Node;
import gov.nasa.jpf.symbc.symexectree.structure.StdNode;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class SeqInstructionReduction implements IRTOptimization, SymbolicExecutionTreeVisitor {

	private LinkedList<Node> sequentialInstrNodes;
	private SymbolicExecutionTree tree;
	
	public SeqInstructionReduction() {
		this.sequentialInstrNodes = new LinkedList<>();
	}
	
	@Override
	public void conductOptimization(SymbolicExecutionTree tree) {
		Node randomNode = tree.getRootNode();
		if(randomNode instanceof IHasWCET ||
		   randomNode instanceof IHasBCET) {
			this.tree = tree;
			tree.accept(this);
		}
	}

	@Override
	public void visit(Node node) {
		this.sequentialInstrNodes.addLast(node);
		if(node.getOutgoingTransitions().size() > 1 || 
		   node.getOutgoingTransitions().isEmpty()) {
			collapseNodes(this.sequentialInstrNodes);
			this.sequentialInstrNodes.clear();
		}
	}

	private void collapseNodes(LinkedList<Node> nodes) {
		Node firstNode = nodes.getFirst();
		Node lastNode = nodes.getLast();
		LinkedList<Transition> firstNodeIncoming = firstNode.getIncomingTransitions();
		
		int aggrWCET = 0;
		int aggrBCET = 0;
		for(Node n : nodes) {
			if(n instanceof IHasWCET) {
				aggrWCET += ((IHasWCET) n).getWCET();
			}
			if(n instanceof IHasBCET) {
				aggrBCET += ((IHasBCET) n).getBCET();
			}
		}
		if(lastNode instanceof IHasWCET) {
			((IHasWCET) lastNode).setWCET(aggrWCET);
		}
		if(lastNode instanceof IHasBCET) {
			((IHasBCET) lastNode).setBCET(aggrBCET);
		}
		lastNode.getIncomingTransitions().clear();
		for(Transition in : firstNodeIncoming) {
			in.setDstNode(lastNode);
			lastNode.addIncomingTransition(in);
		}

		Iterator<Node> nodeIter = nodes.iterator();
		while(nodeIter.hasNext()) {
			Node removeNode = nodeIter.next();
			if(!removeNode.equals(lastNode) && 
			   removeNode.getOutgoingTransitions().size() != 0)
				this.tree.removeNode(removeNode);
		}
		
	}
	
	@Override
	public void visit(Transition transition) {}
	
	@Override
	public void visit(SymbolicExecutionTree tree) {}
}
