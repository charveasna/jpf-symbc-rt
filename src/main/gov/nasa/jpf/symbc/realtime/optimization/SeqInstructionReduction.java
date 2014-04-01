/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.optimization;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import gov.nasa.jpf.symbc.realtime.rtsymexectree.IHasBCET;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IHasWCET;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IStateReducible;
import gov.nasa.jpf.symbc.symexectree.SymbolicExecutionTreeVisitor;
import gov.nasa.jpf.symbc.symexectree.Transition;
import gov.nasa.jpf.symbc.symexectree.structure.Node;
import gov.nasa.jpf.symbc.symexectree.structure.StdNode;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 * 
 */
public class SeqInstructionReduction implements IRTOptimization,
		SymbolicExecutionTreeVisitor {

	private static final long AGGR_EXEC_TIME_MAX = 1073741822L; // max of UPPAAL constraints... ~32bit uint/2

	private SeqInstrNodesList sequentialInstrNodes;
	private SymbolicExecutionTree tree;

	private final boolean targetSymRT;

	public SeqInstructionReduction(boolean targetSymRT) {
		this.targetSymRT = targetSymRT;
		this.sequentialInstrNodes = new SeqInstrNodesList();
	}

	@Override
	public void conductOptimization(SymbolicExecutionTree tree) {
		Node randomNode = tree.getRootNode();
		if (randomNode instanceof IHasWCET || randomNode instanceof IHasBCET) {
			this.tree = tree;
			tree.accept(this);
		}
	}

	@Override
	public void visit(Node node) {
		if (aggregateExecTimeOverflows(node, this.sequentialInstrNodes,
				AGGR_EXEC_TIME_MAX)) {
			collapseNodes(this.sequentialInstrNodes);
			this.sequentialInstrNodes.clear();
			this.sequentialInstrNodes.addLast(node);
		} else {
			this.sequentialInstrNodes.addLast(node);
			if (node.getOutgoingTransitions().size() > 1
					|| node.getOutgoingTransitions().isEmpty()
					|| (!this.isNodeReducible(node) && this.targetSymRT)) {
				collapseNodes(this.sequentialInstrNodes);
				this.sequentialInstrNodes.clear();
			}
		}
	}

	private boolean isNodeReducible(Node node) {
		if (node instanceof IStateReducible) {
			return ((IStateReducible) node).isReducible();
		} else
			return false;
	}

	private void collapseNodes(SeqInstrNodesList nodes) {
		Node firstNode = nodes.getFirst();
		Node lastNode = nodes.getLast();
		LinkedList<Transition> firstNodeIncoming = firstNode
				.getIncomingTransitions();
		if (lastNode instanceof IHasWCET) {
			((IHasWCET) lastNode).setWCET((int) nodes.getAggregatedWCET());
		}
		if (lastNode instanceof IHasBCET) {
			((IHasBCET) lastNode).setBCET((int) nodes.getAggregatedBCET());
		}
		lastNode.getIncomingTransitions().clear();
		for (Transition in : firstNodeIncoming) {
			in.setDstNode(lastNode);
			lastNode.addIncomingTransition(in);
		}

		Iterator<Node> nodeIter = nodes.iterator();
		while (nodeIter.hasNext()) {
			Node removeNode = nodeIter.next();
			//if (!removeNode.equals(lastNode)
			//		&& removeNode.getOutgoingTransitions().size() != 0)
			if(!nodeIter.hasNext())
				break;
			else
				this.tree.removeNode(removeNode);
		}
	}

	private boolean aggregateExecTimeOverflows(Node n,
			SeqInstrNodesList nodesList, long maxAggrVal) {
		if (n instanceof IHasWCET) {
			return ((IHasWCET) n).getWCET() + nodesList.getAggregatedWCET() >= maxAggrVal;
		}
		if (n instanceof IHasBCET) {
			return ((IHasBCET) n).getBCET() + nodesList.getAggregatedBCET() >= maxAggrVal;
		}
		return false;
	}

	@Override
	public void visit(Transition transition) {
	}

	@Override
	public void visit(SymbolicExecutionTree tree) {
	}
}
