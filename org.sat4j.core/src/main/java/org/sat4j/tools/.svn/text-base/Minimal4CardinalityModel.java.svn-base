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
 * Computes models with a minimal number (with respect to cardinality) of
 * negative literals. This is done be adding a constraint on the number of
 * negative literals each time a model if found (the number of negative literals
 * occuring in the model minus one).
 * 
 * @author leberre
 * @see org.sat4j.specs.ISolver#addAtMost(IVecInt, int)
 */
public class Minimal4CardinalityModel extends SolverDecorator<ISolver> {

	private static final long serialVersionUID = 1L;

	/**
	 * @param solver
	 */
	public Minimal4CardinalityModel(ISolver solver) {
		super(solver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sat4j.ISolver#model()
	 */
	@Override
	public int[] model() {
		int[] prevmodel = null;
		IVecInt vec = new VecInt();
		try {
			do {
				prevmodel = super.model();
				vec.clear();
				for (int i = 1; i <= nVars(); i++) {
					vec.push(-i);
				}
				int counter = 0;
				for (int q : prevmodel) {
					if (q < 0) {
						counter++;
					}
				}
				addAtMost(vec, counter - 1);
			} while (isSatisfiable());
		} catch (TimeoutException e) {
			throw new IllegalStateException("Solver timed out"); //$NON-NLS-1$
		} catch (ContradictionException e) {
			// added trivial unsat clauses
		}
		// restore();
		return prevmodel;
	}
}
