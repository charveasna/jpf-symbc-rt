/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.jop;

import gov.nasa.jpf.symbc.realtime.rtsymexectree.IHasWCET;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IRealTimeNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IStateReducible;
import gov.nasa.jpf.vm.StateRestorer;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public interface IJOPRealTimeNode extends IRealTimeNode, IHasWCET, IStateReducible {

}
