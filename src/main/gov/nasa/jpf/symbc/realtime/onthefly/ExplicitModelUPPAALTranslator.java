/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.onthefly;

import uppaal.Automaton;
import uppaal.Location;
import uppaal.Transition;
import uppaal.labels.Synchronization;
import uppaal.labels.Synchronization.SyncType;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.vm.Instruction;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class ExplicitModelUPPAALTranslator extends AUPPAALTranslator {

	public ExplicitModelUPPAALTranslator(Config jpfConf, boolean targetSymRT) {
		super(jpfConf, targetSymRT);
	}

	@Override
	protected Location constructLocation(Instruction instr, Automaton ta, boolean targetSymRT) {
		Location loc = new Location(ta, instr.getMnemonic() + "_" + super.unique_id++);		
		StringBuilder commentBuilder = new StringBuilder();
		commentBuilder.append("Location: ")
					  .append(instr.getFileLocation());
		loc.setComment(commentBuilder.toString());
		return loc;
	}

	@Override
	protected Transition constructTransition(Instruction instr, Location prevLoc, Location nxtLoc, Automaton ta, boolean targetSymRT) {
		Transition trans = new Transition(ta, prevLoc, nxtLoc);
		if(targetSymRT)
			trans.setGuard("running[tID] = true");
		trans.setSync(new Synchronization("jvm_execute", SyncType.INITIATOR));
		trans.addUpdate("jvm_instruction = JVM_" + instr);
		
		return null;
	}

}
