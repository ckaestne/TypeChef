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

import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

/**
 * That class allows to iterate through all the models (implicants) of a
 * formula.
 * 
 * <pre>
 * ISolver solver = new ModelIterator(SolverFactory.OneSolver());
 * boolean unsat = true;
 * while (solver.isSatisfiable()) {
 * 	unsat = false;
 * 	int[] model = solver.model();
 * 	// do something with model
 * }
 * if (unsat) {
 * 	// UNSAT case
 * }
 * </pre>
 * 
 * It is also possible to limit the number of models returned:
 * 
 * <pre>
 * ISolver solver = new ModelIterator(SolverFactory.OneSolver(), 10);
 * </pre>
 * 
 * will return at most 10 models.
 * 
 * @author leberre
 */
public class ModelIterator extends SolverDecorator<ISolver> {

	private static final long serialVersionUID = 1L;

	private boolean trivialfalsity = false;
	private final int bound;
	private int nbModelFound = 0;

	/**
	 * @param solver
	 */
	public ModelIterator(ISolver solver) {
		this(solver, Integer.MAX_VALUE);
	}

	/**
	 * 
	 * @param solver
	 * @param bound
	 * @since 2.1
	 */
	public ModelIterator(ISolver solver, int bound) {
		super(solver);
		this.bound = bound;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sat4j.ISolver#model()
	 */
	@Override
	public int[] model() {
		int[] last = super.model();
		nbModelFound++;
		IVecInt clause = new VecInt(last.length);
		for (int q : last) {
			clause.push(-q);
		}
		try {
			// System.out.println("adding " + clause);
			addBlockingClause(clause);
		} catch (ContradictionException e) {
			trivialfalsity = true;
		}
		return last;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sat4j.ISolver#isSatisfiable()
	 */
	@Override
	public boolean isSatisfiable() throws TimeoutException {
		if (trivialfalsity || nbModelFound >= bound) {
			return false;
		}
		trivialfalsity = false;
		return super.isSatisfiable(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sat4j.ISolver#isSatisfiable(org.sat4j.datatype.VecInt)
	 */
	@Override
	public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
		if (trivialfalsity || nbModelFound >= bound) {
			return false;
		}
		trivialfalsity = false;
		return super.isSatisfiable(assumps, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sat4j.ISolver#reset()
	 */
	@Override
	public void reset() {
		trivialfalsity = false;
		nbModelFound = 0;
		super.reset();
	}
}
