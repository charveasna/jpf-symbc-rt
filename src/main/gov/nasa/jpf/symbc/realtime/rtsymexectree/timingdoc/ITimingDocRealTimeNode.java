/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.rtsymexectree.timingdoc;

import gov.nasa.jpf.symbc.realtime.rtsymexectree.IHasBCET;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IHasWCET;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.IRealTimeNode;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public interface ITimingDocRealTimeNode extends IRealTimeNode, IHasBCET, IHasWCET {

}
