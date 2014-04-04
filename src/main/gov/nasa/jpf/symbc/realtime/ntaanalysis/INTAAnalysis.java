/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.ntaanalysis;

import uppaal.NTA;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public interface INTAAnalysis {
	public void conductAnalysis(NTA nta);
}
