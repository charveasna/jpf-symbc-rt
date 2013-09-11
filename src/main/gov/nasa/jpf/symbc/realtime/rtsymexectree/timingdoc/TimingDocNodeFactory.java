/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.timingdoc;

import gov.nasa.jpf.symbc.realtime.TimingDoc;
import gov.nasa.jpf.symbc.symexectree.InstrContext;
import gov.nasa.jpf.symbc.symexectree.NodeFactory;
import gov.nasa.jpf.symbc.symexectree.structure.IfNode;
import gov.nasa.jpf.symbc.symexectree.structure.InvokeNode;
import gov.nasa.jpf.symbc.symexectree.structure.Node;
import gov.nasa.jpf.symbc.symexectree.structure.ReturnNode;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class TimingDocNodeFactory extends NodeFactory {

	private TimingDoc tDoc;
	
	public TimingDocNodeFactory(TimingDoc tDoc) {
		this.tDoc = tDoc;
	}
	
	@Override
	public Node constructStdNode(InstrContext instrCtx) {
		return new TimingDocStdNode(instrCtx, this.tDoc.get(instrCtx.getInstr()));
	}

	@Override
	public IfNode constructIfNode(InstrContext instrCtx) {
		return new TimingDocIfNode(instrCtx, this.tDoc.get(instrCtx.getInstr()));
	}

	@Override
	public InvokeNode constructInvokeNode(InstrContext instrCtx) {
		return new TimingDocInvokeNode(instrCtx, this.tDoc.get(instrCtx.getInstr()));
	}

	@Override
	public ReturnNode constructReturnNode(InstrContext instrCtx) {
		return new TimingDocReturnNode(instrCtx, this.tDoc.get(instrCtx.getInstr()));
	}

}
