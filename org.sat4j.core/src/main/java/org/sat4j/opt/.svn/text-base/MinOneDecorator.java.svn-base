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
package org.sat4j.opt;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.IOptimizationProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.SolverDecorator;

/**
 * Computes a solution with the smallest number of satisfied literals.
 * 
 * Please make sure that newVar(howmany) is called first to setup the decorator.
 * 
 * @author leberre
 */
public final class MinOneDecorator extends SolverDecorator<ISolver> implements
		IOptimizationProblem {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private int[] prevmodel;

	private boolean isSolutionOptimal;

	public MinOneDecorator(ISolver solver) {
		super(solver);
	}

	public boolean admitABetterSolution() throws TimeoutException {
		return admitABetterSolution(VecInt.EMPTY);
	}

	/**
	 * @since 2.1
	 */
	public boolean admitABetterSolution(IVecInt assumps)
			throws TimeoutException {
		isSolutionOptimal = false;
		boolean result = isSatisfiable(assumps, true);
		if (result) {
			prevmodel = super.model();
			calculateObjectiveValue();
		} else {
			isSolutionOptimal = true;
		}
		return result;
	}

	public boolean hasNoObjectiveFunction() {
		return false;
	}

	public boolean nonOptimalMeansSatisfiable() {
		return true;
	}

	private int counter;

	public Number calculateObjective() {
		calculateObjectiveValue();
		return counter;
	}

	private void calculateObjectiveValue() {
		counter = 0;
		for (int p : prevmodel) {
			if (p > 0) {
				counter++;
			}
		}
	}

	private final IVecInt literals = new VecInt();

	private IConstr previousConstr;

	/**
	 * @since 2.1
	 */
	public void discardCurrentSolution() throws ContradictionException {
		if (literals.isEmpty()) {
			for (int i = 1; i <= nVars(); i++) {
				literals.push(i);
			}
		}
		if (previousConstr != null) {
			super.removeConstr(previousConstr);
		}
		previousConstr = addAtMost(literals, counter - 1);
	}

	@Override
	public int[] model() {
		// DLB findbugs ok
		return prevmodel;
	}

	@Override
	public void reset() {
		literals.clear();
		previousConstr = null;
		super.reset();
	}

	/**
	 * @since 2.1
	 */
	public Number getObjectiveValue() {
		return counter;
	}

	public void discard() throws ContradictionException {
		discardCurrentSolution();
	}

	/**
	 * @since 2.1
	 */
	public void forceObjectiveValueTo(Number forcedValue)
			throws ContradictionException {
		try {
			addAtMost(literals, forcedValue.intValue());
		} catch (ContradictionException ce) {
			isSolutionOptimal = true;
			throw ce;
		}

	}

	public boolean isOptimal() {
		return isSolutionOptimal;
	}
}
