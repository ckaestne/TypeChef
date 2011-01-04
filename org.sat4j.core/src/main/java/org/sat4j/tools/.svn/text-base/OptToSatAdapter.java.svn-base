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
package org.sat4j.tools;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IOptimizationProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

public class OptToSatAdapter extends SolverDecorator<ISolver> {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	IOptimizationProblem problem;

	boolean modelComputed = false;
	boolean optimalValueForced = false;

	public OptToSatAdapter(IOptimizationProblem problem) {
		super((ISolver) problem);
		this.problem = problem;
	}

	@Override
	public boolean isSatisfiable() throws TimeoutException {
		modelComputed = false;
		return problem.admitABetterSolution();
	}

	@Override
	public void reset() {
		super.reset();
		optimalValueForced = false;
	}

	@Override
	public boolean isSatisfiable(boolean global) throws TimeoutException {
		modelComputed = false;
		return problem.admitABetterSolution();
	}

	@Override
	public boolean isSatisfiable(IVecInt assumps, boolean global)
			throws TimeoutException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] model() {
		if (modelComputed)
			return problem.model();

		try {
			assert problem.admitABetterSolution();
			do {
				problem.discardCurrentSolution();
			} while (problem.admitABetterSolution());
			if (!optimalValueForced) {
				try {
					problem.forceObjectiveValueTo(problem.getObjectiveValue());
				} catch (ContradictionException e1) {
					throw new IllegalStateException();
				}
				optimalValueForced = true;
			}
		} catch (TimeoutException e) {
			// solver timeout
		} catch (ContradictionException e) {
			// OK, optimal model found
			if (!optimalValueForced) {
				try {
					problem.forceObjectiveValueTo(problem.getObjectiveValue());
				} catch (ContradictionException e1) {
					throw new IllegalStateException();
				}
				optimalValueForced = true;
			}
		}
		modelComputed = true;
		return problem.model();
	}

	@Override
	public boolean model(int var) {
		if (!modelComputed)
			model();
		return problem.model(var);
	}

	@Override
	public String toString(String prefix) {
		return prefix + "Optimization to SAT adapter\n"
				+ super.toString(prefix);
	}

	/**
	 * Allow to easily check is the solution returned by isSatisfiable is
	 * optimal or not.
	 * 
	 * @return true is the solution found is indeed optimal.
	 */
	public boolean isOptimal() {
		return problem.isOptimal();
	}
}
