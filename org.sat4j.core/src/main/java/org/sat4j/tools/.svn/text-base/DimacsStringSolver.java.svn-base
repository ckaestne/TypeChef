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
import org.sat4j.specs.IConstr;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;

/**
 * Solver used to write down a CNF into a String.
 * 
 * It is especially useful compared to the DimacsOutputSolver because the number
 * of clauses does not need to be known in advance.
 * 
 * @author leberre
 * 
 */
public class DimacsStringSolver extends AbstractOutputSolver {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private StringBuffer out;

	private int firstCharPos;

	private final int initBuilderSize;

	private int maxvarid = 0;

	public DimacsStringSolver() {
		this(16);
	}

	public DimacsStringSolver(int initSize) {
		out = new StringBuffer(initSize);
		initBuilderSize = initSize;
	}

	public StringBuffer getOut() {
		return out;
	}

	public int newVar() {
		return 0;
	}

	public int newVar(int howmany) {
		setNbVars(howmany);
		return howmany;
	}

	protected void setNbVars(int howmany) {
		nbvars = howmany;
		maxvarid = howmany;
	}

	public void setExpectedNumberOfClauses(int nb) {
		out.append(" ");
		out.append(nb);
		nbclauses = nb;
		fixedNbClauses = true;
	}

	public IConstr addClause(IVecInt literals) throws ContradictionException {
		if (firstConstr) {
			if (!fixedNbClauses) {
				firstCharPos = 7 + Integer.toString(nbvars).length();
				out.append("                    ");
				out.append("\n");
				nbclauses = 0;
			}
			firstConstr = false;
		}
		if (!fixedNbClauses) {
			nbclauses++;
		}
		for (IteratorInt iterator = literals.iterator(); iterator.hasNext();) {
			out.append(iterator.next()).append(" ");
		}
		out.append("0\n");
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
			firstCharPos = 0;
			out.append("                    ");
			out.append("\n");
			nbclauses = 0;
			firstConstr = false;
		}

		for (int i = 0; i <= literals.size(); i++) {
			for (int j = i + 1; j < literals.size(); j++) {
				if (!fixedNbClauses) {
					nbclauses++;
				}
				out.append(-literals.get(i));
				out.append(" ");
				out.append(-literals.get(j));
				out.append(" 0\n");
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
		out = new StringBuffer(initBuilderSize);
		maxvarid = 0;
	}

	public String toString(String prefix) {
		return "Dimacs output solver";
	}

	public int nConstraints() {
		return nbclauses;
	}

	public int nVars() {
		return maxvarid;
	}

	@Override
	public String toString() {
		// String numClauses = Integer.toString(nbclauses);
		// int numClausesLength = numClauses.length();
		// for (int i = 0; i < numClausesLength; ++i) {
		// out.setCharAt(firstCharPos + i, numClauses.charAt(i));
		// }
		out.insert(firstCharPos, "p cnf " + maxvarid + " " + nbclauses);
		return out.toString();
	}

	/**
	 * @since 2.1
	 */
	public int nextFreeVarId(boolean reserve) {
		if (reserve) {
			maxvarid++;
			return maxvarid;
		}
		return maxvarid;
	}
}
