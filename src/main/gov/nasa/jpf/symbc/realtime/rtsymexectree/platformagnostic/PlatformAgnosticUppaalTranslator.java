/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.platformagnostic;

import uppaal.Automaton;
import uppaal.Location;
import uppaal.Transition;
import uppaal.labels.Synchronization;
import uppaal.labels.Synchronization.SyncType;
import gov.nasa.jpf.symbc.realtime.AUppaalTranslator;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IHasWCET;
import gov.nasa.jpf.symbc.symexectree.structure.Node;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class PlatformAgnosticUppaalTranslator extends AUppaalTranslator {

	public PlatformAgnosticUppaalTranslator(boolean targetSymRT,
			boolean useProgressMeasure) {
		super(targetSymRT, useProgressMeasure);
	}

	@Override
	protected Location decoratePlatformDependentTransition(Automaton ta, Transition uppTrans, Node treeNode) {
		if(this.targetSymRT)
			uppTrans.setGuard("running[tID] == true");
		uppTrans.setSync(new Synchronization("jvm_execute", SyncType.INITIATOR));
		uppTrans.addUpdate("jvm_instruction = JVM_" + treeNode.getInstructionContext().getInstr().getMnemonic().toUpperCase());
		return uppTrans.getSource();
	}

	@Override
	protected Location translateTreeNode(Node treeNode, Automaton ta) {
		Location newLoc = new Location(ta, getLocationName(treeNode));
		StringBuilder invariantBuilder = new StringBuilder();
		newLoc.setInvariant(invariantBuilder.toString());
		newLoc.setComment(treeNode.getInstructionContext().getInstr().getFilePos());
		return newLoc;
	}
}
