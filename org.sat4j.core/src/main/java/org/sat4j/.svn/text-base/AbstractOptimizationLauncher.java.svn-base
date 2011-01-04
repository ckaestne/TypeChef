/*******************************************************************************
 * SAT4J: a SATisfiability library for Java Copyright (C) 2004-2008 Daniel Le Berre
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU Lesser General Public License Version 2.1 or later (the
 * "LGPL"), in which case the provisions of the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL, and not to allow others to use your version of
 * this file under the terms of the EPL, indicate your decision by deleting
 * the provisions above and replace them with the notice and other provisions
 * required by the LGPL. If you do not delete the provisions above, a recipient
 * may use your version of this file under the terms of the EPL or the LGPL.
 * 
 * Based on the original MiniSat specification from:
 * 
 * An extensible SAT solver. Niklas Een and Niklas Sorensson. Proceedings of the
 * Sixth International Conference on Theory and Applications of Satisfiability
 * Testing, LNCS 2919, pp 502-518, 2003.
 *
 * See www.minisat.se for the original solver in C++.
 * 
 *******************************************************************************/
package org.sat4j;

import java.io.PrintWriter;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IOptimizationProblem;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.TimeoutException;

/**
 * This class is intended to be used by launchers to solve optimization
 * problems, i.e. problems for which a loop is needed to find the optimal
 * solution.
 * 
 * @author leberre
 * 
 */
public abstract class AbstractOptimizationLauncher extends AbstractLauncher {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String CURRENT_OPTIMUM_VALUE_PREFIX = "o "; //$NON-NLS-1$

	@Override
	protected void displayResult() {
		displayAnswer();

		log("Total wall clock time (in seconds): " //$NON-NLS-1$
				+ (System.currentTimeMillis() - getBeginTime()) / 1000.0);
	}

	protected void displayAnswer() {
		if (solver == null)
			return;
		System.out.flush();
		PrintWriter out = getLogWriter();
		out.flush();
		solver.printStat(out, COMMENT_PREFIX);
		solver.printInfos(out, COMMENT_PREFIX);
		ExitCode exitCode = getExitCode();
		out.println(ANSWER_PREFIX + exitCode);
		if (exitCode == ExitCode.SATISFIABLE
				|| exitCode == ExitCode.OPTIMUM_FOUND) {
			out.print(SOLUTION_PREFIX);
			getReader().decode(solver.model(), out);
			out.println();
			IOptimizationProblem optproblem = (IOptimizationProblem) solver;
			if (!optproblem.hasNoObjectiveFunction()) {
				log("objective function=" + optproblem.getObjectiveValue()); //$NON-NLS-1$
			}
		}
	}

	@Override
	protected void solve(IProblem problem) throws TimeoutException {
		boolean isSatisfiable = false;

		IOptimizationProblem optproblem = (IOptimizationProblem) problem;

		try {
			while (optproblem.admitABetterSolution()) {
				if (!isSatisfiable) {
					if (optproblem.nonOptimalMeansSatisfiable()) {
						setExitCode(ExitCode.SATISFIABLE);
						if (optproblem.hasNoObjectiveFunction()) {
							return;
						}
						log("SATISFIABLE"); //$NON-NLS-1$
					}
					isSatisfiable = true;
					log("OPTIMIZING..."); //$NON-NLS-1$
				}
				log("Got one! Elapsed wall clock time (in seconds):" //$NON-NLS-1$
						+ (System.currentTimeMillis() - getBeginTime())
						/ 1000.0);
				getLogWriter().println(
						CURRENT_OPTIMUM_VALUE_PREFIX
								+ optproblem.getObjectiveValue());
				optproblem.discardCurrentSolution();
			}
			if (isSatisfiable) {
				setExitCode(ExitCode.OPTIMUM_FOUND);
			} else {
				setExitCode(ExitCode.UNSATISFIABLE);
			}
		} catch (ContradictionException ex) {
			assert isSatisfiable;
			setExitCode(ExitCode.OPTIMUM_FOUND);
		}
	}

}
