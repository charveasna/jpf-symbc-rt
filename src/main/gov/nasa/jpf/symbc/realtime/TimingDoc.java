/**
 * 
 */
package gov.nasa.jpf.symbc.realtime;

import gov.nasa.jpf.vm.Instruction;

import java.util.HashMap;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class TimingDoc extends HashMap<String, InstructionTimingInfo>{

	private static final long serialVersionUID = 1L;
	private static int DEF_WCET = 100;
	private static int DEF_BCET = 100;
	
	@Override
	public InstructionTimingInfo get(Object key) {
		InstructionTimingInfo tInfo;
		String mnemonic;
		if(key instanceof Instruction)
			mnemonic = ((Instruction)key).getMnemonic().toLowerCase();
		else if(key instanceof String)
			mnemonic = ((String)key).toLowerCase();
		else
			throw new RealTimeRuntimeException("Cannot retrieve timing info for key with type [" + key.toString() + "]");
		tInfo = super.get(mnemonic).makeCopy();
		if(tInfo != null)
			return tInfo;
		else {
			System.err.println("Warning! Setting default WCET=" + DEF_WCET + " and default BCET=" + DEF_BCET + " for bytecode [" + mnemonic + "]");
			return new InstructionTimingInfo(mnemonic, -1, 100, 100);
		}
	}
}
