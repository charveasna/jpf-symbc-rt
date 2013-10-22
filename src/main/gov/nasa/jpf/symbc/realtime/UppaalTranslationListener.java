/**
 * 
 */
package gov.nasa.jpf.symbc.realtime;

import java.util.LinkedList;

import uppaal.NTA;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.symbc.realtime.optimization.RTOptimizer;
import gov.nasa.jpf.symbc.realtime.optimization.SeqInstructionReduction;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.JOPNodeFactory;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.platformagnostic.PlatformAgnosticTimingNodeFactory;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.platformagnostic.PlatformAgnosticTimingStdNode;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.timingdoc.TimingDocNodeFactory;
import gov.nasa.jpf.symbc.symexectree.ASymbolicExecutionTreeListener;
import gov.nasa.jpf.symbc.symexectree.NodeFactory;
import gov.nasa.jpf.symbc.symexectree.structure.SymbolicExecutionTree;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class UppaalTranslationListener extends ASymbolicExecutionTreeListener {
	/**
	 * This is the listener used for translating the symbolic execution tree generated
	 * by SPF to a timed automaton amenable to model checking using UPPAAL.
	 * The configurations for this listener are:
	 * 
	 * symbolic.realtime.platform 			=	[jop|agnostic|timingdoc]	(default: jop)
	 * symbolic.realtime.targetsymrt	 	=	[true|false]				(default: false)
	 * symbolic.realtime.outputbasepath 	=	<output path>				(default: ./)
	 * symbolic.realtime.optimize 			= 	[true|false]				(default: true)
	 * symbolic.realtime.generatequeries 	= 	[true|false]				(default: true)
	 * 
	 * If the target platform is 'timingdoc', a Timing Doc - describing the execution
	 * times of the individual Java Bytecodes of the particular platform - must be
	 * supplied as well using:
	 * 
	 * symbolic.realtime.timingdocpath = <source path>
	 */
	
	private static final String DEF_OUTPUT_PATH = "./";
	private String targetPlatform;
	private boolean targetSymRT;
	private boolean optimize;
	private String outputBasePath;
	private boolean generateQueries;

	public UppaalTranslationListener(Config conf, JPF jpf) {
		super(conf, jpf);
		this.targetSymRT = conf.getBoolean("symbolic.realtime.targetsymrt", false);
		this.optimize = conf.getBoolean("symbolic.realtime.optimize", true);
		this.outputBasePath = conf.getString("symbolic.realtime.outputbasepath", UppaalTranslationListener.DEF_OUTPUT_PATH);
		this.generateQueries = conf.getBoolean("symbolic.realtime.generatequeries", !this.targetSymRT);
	}

	@Override
	protected NodeFactory getNodeFactory() {
		this.targetPlatform = super.jpfConf.getString("symbolic.realtime.platform", "").toLowerCase();
		switch(this.targetPlatform) {
			case "jop":
				return new JOPNodeFactory();
			case "agnostic":
				return new PlatformAgnosticTimingNodeFactory();
			case "timingdoc":
				String timingDocPath = super.jpfConf.getString("symbolic.realtime.timingdocpath");
				if(timingDocPath == null) 
					throw new TimingDocException("symbolic.realtime.timingdocpath has not been set.");
				TimingDoc tDoc = TimingDocGenerator.generate(timingDocPath);
				return new TimingDocNodeFactory(tDoc);
			default:
				System.out.println("Default platform JOP is used");
				return new JOPNodeFactory();
		}
	}

	@Override
	protected void doneConstructingSymbExecTree(LinkedList<SymbolicExecutionTree> trees) {
		if(trees.isEmpty())
			throw new UppaalTranslatorException("No symbolic execution trees were generated! Have you set the target method correctly?");
		UppaalTranslator translator = new UppaalTranslator(this.targetSymRT);
		RTOptimizer optimizer = null;
		if(this.optimize) {
			optimizer = new RTOptimizer();
			optimizer.addOptimization(new SeqInstructionReduction(this.targetSymRT));
		}
		for(SymbolicExecutionTree tree : trees) {
			if(this.optimize)
				optimizer.optimize(tree);
			NTA ntaSystem = translator.translateSymTree(tree);
			ntaSystem.writePrettyLayoutModelToFile(this.getNTAFileName(ntaSystem, tree));
			if(this.generateQueries)
				QueriesFileGenerator.writeQueriesFile(ntaSystem, getQueriesFileName(ntaSystem, tree));
		}
	}
	
	private String getNTAFileName(NTA nta, SymbolicExecutionTree tree) {
		return this.getBaseFileName(nta, tree) + ".xml";
	}
	
	private String getQueriesFileName(NTA nta, SymbolicExecutionTree tree) {
		return this.getBaseFileName(nta, tree) + ".q";
	}
	private String getBaseFileName(NTA nta, SymbolicExecutionTree tree) {
		return outputBasePath + (outputBasePath.endsWith("/") ? "" : "/") + tree.getTargetMethod().getMethodName() + "_SPF";
	}
}
