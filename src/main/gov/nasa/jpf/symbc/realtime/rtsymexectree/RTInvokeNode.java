/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree;

import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.structure.InvokeNode;
import gov.nasa.jpf.symbc.symexectree.structure.Node;
import gov.nasa.jpf.symbc.symexectree.structure.StdNode;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public abstract class RTInvokeNode extends InvokeNode implements IRealTimeNode {

	public RTInvokeNode(InstrContext instructionContext) {
		super(instructionContext);
	}
	
	public RTInvokeNode(InstrContext instructionContext, SymbolicExecutionTree tree) {
		super(instructionContext, tree);
	}	
}
