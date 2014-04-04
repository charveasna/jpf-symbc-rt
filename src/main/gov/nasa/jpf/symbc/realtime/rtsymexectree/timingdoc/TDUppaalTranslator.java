/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.timingdoc;

import uppaal.Automaton;
import uppaal.Location;
import uppaal.Transition;
import uppaal.labels.Guard;
import uppaal.labels.Synchronization;
import uppaal.labels.Synchronization.SyncType;
import gov.nasa.jpf.symbc.realtime.AUppaalTranslator;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IHasBCET;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IHasWCET;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.RTFireSporadicNode;
import gov.nasa.jpf.symbc.symexectree.structure.MonitorEnterNode;
import gov.nasa.jpf.symbc.symexectree.structure.MonitorExitNode;
import gov.nasa.jpf.symbc.symexectree.structure.Node;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class TDUppaalTranslator extends AUppaalTranslator {
	
	public TDUppaalTranslator(boolean targetSymRT, boolean useProgressMeasure) {
		super(targetSymRT, useProgressMeasure);
	}

	@Override
	protected Location translateTreeNode(Node treeNode, Automaton ta) {
		Location newLoc = new Location(ta, getLocationName(treeNode));
		StringBuilder invariantBuilder = new StringBuilder();
		invariantBuilder.append(JBC_CLOCK_N + " <= ")
						.append(((IHasWCET) treeNode).getWCET());

		if(targetSymRT) {
			invariantBuilder.append("&&\n")
							.append(JBC_CLOCK_N + "' == running[tID]");
		}
		newLoc.setInvariant(invariantBuilder.toString());
		newLoc.setComment(treeNode.getInstructionContext().getInstr().getFilePos());
		return newLoc;
	}

	@Override
	protected Location decoratePlatformDependentTransition(Automaton ta, Transition uppTrans, Node treeNode) {
		uppTrans.setGuard(JBC_CLOCK_N + " >= " + ((IHasBCET) treeNode).getBCET() + " &&\n" +
				JBC_CLOCK_N + " <= " + ((IHasWCET) treeNode).getWCET());
		uppTrans.addUpdate(JBC_CLOCK_N + " = 0");
		return uppTrans.getSource();
	}
}
