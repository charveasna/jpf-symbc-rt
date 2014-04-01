/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.optimization;

import java.util.Collection;
import java.util.LinkedList;

import gov.nasa.jpf.symbc.realtime.rtsymexectree.IHasBCET;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IHasWCET;
import gov.nasa.jpf.symbc.symexectree.structure.Node;
/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 * Does not decrement aggrWCET and aggrBCET when elements are removed... too lazy
 */
public class SeqInstrNodesList extends LinkedList<Node> {
	
	private static final long serialVersionUID = 53L;
	private long aggrWCET;
	private long aggrBCET;
	
	public SeqInstrNodesList() {
		this.aggrBCET = 0;
		this.aggrWCET = 0;
	}
	
	@Override
	public boolean add(Node node) {
		boolean ret = super.add(node);
		aggrExecTime(node);
		return ret;
	}
	
	@Override
	public void add(int i, Node node) {
		super.add(i, node);
		aggrExecTime(node);
	}
	
	@Override
	public void addFirst(Node node) {
		super.addFirst(node);
		aggrExecTime(node);
	}
	
	@Override
	public void addLast(Node node) {
		super.addLast(node);
		aggrExecTime(node);
	}
	
	@Override
	public boolean addAll(int i, Collection<? extends Node> nodes) {
		boolean ret = super.addAll(i, nodes);
		for(Node n : nodes)
			aggrExecTime(n);
		return ret;
	}
	
	@Override
	public boolean addAll(Collection<? extends Node> nodes) {
		boolean ret = super.addAll(nodes);
		for(Node n : nodes)
			aggrExecTime(n);
		return ret;
	}
	
	@Override
	public void clear() {
		super.clear();
		this.aggrBCET = this.aggrWCET = 0;
	}
	
	public long getAggregatedWCET() {
		return this.aggrWCET;
	}
	
	public long getAggregatedBCET() {
		return this.aggrBCET;
	}
	
	private void aggrExecTime(Node node) {
		if(node instanceof IHasWCET) {
			aggrWCET += ((IHasWCET) node).getWCET();
		}
		if(node instanceof IHasBCET) {
			aggrBCET += ((IHasBCET) node).getBCET();
		}
	}
}
