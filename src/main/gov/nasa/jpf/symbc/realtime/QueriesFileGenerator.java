/**
 * 
 */
package gov.nasa.jpf.symbc.realtime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import uppaal.Automaton;
import uppaal.NTA;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class QueriesFileGenerator {
	
	public static void writeQueriesFile(NTA ntaSystem, String outputPath) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(outputPath));
			for(Automaton aut : ntaSystem.getAutomata()) {
				bw.write(getBCETStr(aut));
				bw.write(getWCETStr(aut));
			}
			bw.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String getBCETStr(Automaton aut) {
		return "inf " + getBasicExecTimeStr(aut);
	}
	
	private static String getWCETStr(Automaton aut) {
		return "sup " + getBasicExecTimeStr(aut);
	}
	
	private static String getBasicExecTimeStr(Automaton aut) {
		return "{" + aut.getName().getName() + ".final} : executionTime\n";
	}
}
