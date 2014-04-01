/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.optimization.pm;

import java.util.HashSet;

import gov.nasa.jpf.symbc.symexectree.Transition;
import gov.nasa.jpf.symbc.symexectree.structure.Node;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;


/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class ProgressMeasureUtil {

	public static int calculateMaxBranches(SymbolicExecutionTree tree) {
		return calculateMaxBranches(tree.getRootNode());
	}
	
	private static int calculateMaxBranches(Node node) {
		int localMax = 0;
		if(node.getOutgoingTransitions().size() > 0) {
			for(Transition out : node.getOutgoingTransitions()) {
				int max = 1 + calculateMaxBranches(out.getDstNode());
				if(max > localMax) localMax = max;
			}
		}
		return localMax;
	}
}
