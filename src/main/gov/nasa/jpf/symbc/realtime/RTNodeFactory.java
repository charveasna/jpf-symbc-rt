/**
 * 
 */
package gov.nasa.jpf.symbc.realtime;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.NodeFactory;
import gov.nasa.jpf.symbc.symexectree.structure.InvokeNode;
import gov.nasa.jpf.symbc.symexectree.structure.Node;
import gov.nasa.jpf.symbc.symexectree.structure.ReturnNode;
import gov.nasa.jpf.symbc.symexectree.structure.StdNode;
import gov.nasa.jpf.symbc.symexectree.structure.UnexpectedInstructionTypeException;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public abstract class RTNodeFactory extends NodeFactory {

	private static final String SPORADIC_FIRE_CANONICAL_METHOD_NAME = "javax.scj.RealtimeSystem.fire";
	
	public abstract InvokeNode constructFireSporadicEventNode(InstrContext instrCtx);
	public abstract InvokeNode constructStdInvokeNode(InstrContext instrCtx);
	
	@Override
	public final InvokeNode constructInvokeNode(InstrContext instrCtx) {
		Instruction instr = instrCtx.getInstr();
		if(instr instanceof InvokeInstruction) {
			MethodInfo invokedMethod = ((InvokeInstruction)instr).getInvokedMethod();
			if(invokedMethod != null && invokedMethod.getBaseName().equals(SPORADIC_FIRE_CANONICAL_METHOD_NAME))
				return constructFireSporadicEventNode(instrCtx);
			else
				return constructStdInvokeNode(instrCtx);
		} else
			throw new UnexpectedInstructionTypeException(instr.getClass(), InvokeInstruction.class);
		
	}
}
