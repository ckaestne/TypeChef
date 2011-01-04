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

import java.io.ObjectInputStream;
import java.io.PrintWriter;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;

/**
 * Solver used to display in a writer the CNF instance in Dimacs format.
 * 
 * That solver is useful to produce CNF files to be used by third party solvers.
 * 
 * @author leberre
 * 
 */
public class DimacsOutputSolver extends AbstractOutputSolver {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private transient PrintWriter out;

	public DimacsOutputSolver() {
		this(new PrintWriter(System.out, true));
	}

	public DimacsOutputSolver(PrintWriter pw) {
		out = pw;
	}

	private void readObject(ObjectInputStream stream) {
		out = new PrintWriter(System.out, true);
	}

	public int newVar() {
		return 0;
	}

	public int newVar(int howmany) {
		out.print("p cnf " + howmany);
		nbvars = howmany;
		return 0;
	}

	public void setExpectedNumberOfClauses(int nb) {
		out.println(" " + nb);
		nbclauses = nb;
		fixedNbClauses = true;
	}

	public IConstr addClause(IVecInt literals) throws ContradictionException {
		if (firstConstr) {
			if (!fixedNbClauses) {
				out.println(" XXXXXX");
			}
			firstConstr = false;
		}
		for (IteratorInt iterator = literals.iterator(); iterator.hasNext();) {
			out.print(iterator.next() + " ");
		}
		out.println("0");
		return null;
	}

	public IConstr addAtMost(IVecInt literals, int degree)
			throws ContradictionException {
		if (degree > 1) {
			throw new UnsupportedOperationException(
					"Not a clausal problem! degree " + degree);
		}
		assert degree == 1;
		if (firstConstr) {
			if (!fixedNbClauses) {
				out.println("XXXXXX");
			}
			firstConstr = false;
		}
		for (int i = 0; i <= literals.size(); i++) {
			for (int j = i + 1; j < literals.size(); j++) {
				out.println("" + (-literals.get(i)) + " " + (-literals.get(j))
						+ " 0");
			}
		}
		return null;
	}

	public IConstr addAtLeast(IVecInt literals, int degree)
			throws ContradictionException {
		if (degree > 1) {
			throw new UnsupportedOperationException(
					"Not a clausal problem! degree " + degree);
		}
		assert degree == 1;
		return addClause(literals);
	}

	public void reset() {
		fixedNbClauses = false;
		firstConstr = true;

	}

	public String toString(String prefix) {
		return "Dimacs output solver";
	}

	public int nConstraints() {
		return nbclauses;
	}

	public int nVars() {
		return nbvars;
	}

	/**
	 * @since 2.1
	 */
	public int nextFreeVarId(boolean reserve) {
		throw new UnsupportedOperationException();
	}
}
