package org.sat4j;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.LecteurDimacs;
import org.sat4j.reader.Reader;
import org.sat4j.reader.StructuredCNFReader;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.xplain.Explainer;
import org.sat4j.tools.xplain.HighLevelXplain;
import org.sat4j.tools.xplain.Xplain;

public class MUSLauncher extends AbstractLauncher {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int[] mus;

	private Explainer xplain;

	@Override
	public void usage() {
		log("java -jar sat4j-mus.jar <cnffile>");
	}

	@Override
	protected Reader createReader(ISolver theSolver, String problemname) {
		if (problemname.endsWith(".scnf")) {
			return new StructuredCNFReader((HighLevelXplain<ISolver>) theSolver);
		}
		return new LecteurDimacs(theSolver);
	}

	@Override
	protected String getInstanceName(String[] args) {
		if (args.length == 0) {
			return null;
		}
		return args[0];
	}

	@Override
	protected ISolver configureSolver(String[] args) {
		ISolver solver;
		if (args[0].endsWith(".scnf")) {
			HighLevelXplain<ISolver> hlxp = new HighLevelXplain<ISolver>(
					SolverFactory.newDefault());
			xplain = hlxp;
			solver = hlxp;
		} else {
			Xplain<ISolver> xp = new Xplain<ISolver>(SolverFactory.newDefault());
			xplain = xp;
			solver = xp;
		}
		solver.setTimeout(Integer.MAX_VALUE);
		solver.setDBSimplificationAllowed(true);
		getLogWriter().println(solver.toString(COMMENT_PREFIX)); //$NON-NLS-1$
		return solver;
	}

	@Override
	protected void displayResult() {
		if (solver != null) {
			double wallclocktime = (System.currentTimeMillis() - beginTime) / 1000.0;
			solver.printStat(out, COMMENT_PREFIX);
			solver.printInfos(out, COMMENT_PREFIX);
			out.println(ANSWER_PREFIX + exitCode);
			if (exitCode == ExitCode.SATISFIABLE) {
				int[] model = solver.model();
				out.print(SOLUTION_PREFIX);
				reader.decode(model, out);
				out.println();
			} else if (exitCode == ExitCode.UNSATISFIABLE && mus != null) {
				out.print(SOLUTION_PREFIX);
				reader.decode(mus, out);
				out.println();
			}
			log("Total wall clock time (in seconds) : " + wallclocktime); //$NON-NLS-1$
		}
	}

	@Override
	public void run(String[] args) {
		mus = null;
		super.run(args);
		double wallclocktime = (System.currentTimeMillis() - beginTime) / 1000.0;
		if (exitCode == ExitCode.UNSATISFIABLE) {
			try {
				log("Unsat detection wall clock time (in seconds) : "
						+ wallclocktime);
				log("Size of initial unsat subformula: "
						+ solver.unsatExplanation().size());
				log("Computing MUS ...");
				double beginmus = System.currentTimeMillis();
				mus = xplain.minimalExplanation();
				log("Size of the MUS: " + mus.length);
				log("Unsat core  computation wall clock time (in seconds) : "
						+ (System.currentTimeMillis() - beginmus) / 1000.0);
			} catch (TimeoutException e) {
				log("Cannot compute MUS within the timeout.");
			}
		}

	}

	public static void main(final String[] args) {
		MUSLauncher lanceur = new MUSLauncher();
		if (args.length != 1) {
			lanceur.usage();
			return;
		}
		lanceur.run(args);
		System.exit(lanceur.getExitCode().value());
	}
}
