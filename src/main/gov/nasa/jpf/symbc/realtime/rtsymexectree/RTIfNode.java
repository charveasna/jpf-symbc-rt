/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree;

import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.structure.IfNode;
import gov.nasa.jpf.symbc.symexectree.structure.Node;
import gov.nasa.jpf.symbc.symexectree.structure.StdNode;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public abstract class RTIfNode extends IfNode implements IRealTimeNode {

	public RTIfNode(InstrContext instructionContext) {
		super(instructionContext);
	}
	
	public RTIfNode(InstrContext instructionContext, SymbolicExecutionTree tree) {
		super(instructionContext, tree);
	}	
}
