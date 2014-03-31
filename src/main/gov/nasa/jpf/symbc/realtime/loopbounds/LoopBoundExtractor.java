/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.loopbounds;

import gov.nasa.jpf.symbc.realtime.UppaalTranslatorException;
import gov.nasa.jpf.util.Source;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class LoopBoundExtractor {
	private static final int LOOK_BACK = 3;
	private static final int ERR_LOOPBOUND = -1;
	
	private static final String ANNOTATION_FORMAT = "//@loopbound=([0-9]+)";
	private static final Pattern annotationRegex = Pattern.compile(ANNOTATION_FORMAT);

	public static int extractBound(String sourceFilePath, int backJumpLineNum) {
		Source src = Source.getSource(sourceFilePath);
		if(src == null)
			throw new UppaalTranslatorException("Could not find file: " + sourceFilePath + ". Maybe you have forgotten to set sourcepath in your jpf-config. E.g. sourcepath=${jpf-symbc-rt}/src/examples");
		
		for(int lineNum = backJumpLineNum - LOOK_BACK; lineNum <= backJumpLineNum; ++lineNum) {
			String line = src.getLine(lineNum).replaceAll("\\s+","");
			Matcher regexMatcher = annotationRegex.matcher(line);
			if (regexMatcher.find()) {
				return Integer.parseInt(regexMatcher.group(1));
			}
		}
		return ERR_LOOPBOUND;
	}
}
