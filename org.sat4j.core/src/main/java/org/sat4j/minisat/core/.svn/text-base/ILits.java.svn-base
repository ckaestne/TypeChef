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
package org.sat4j.minisat.core;

import org.sat4j.specs.IVec;

/**
 * That interface manages the solver's internal vocabulary. Everything related
 * to variables and literals is available from here.
 * 
 * For sake of efficiency, literals and variables are not object in SAT4J. They
 * are represented by numbers. If the vocabulary contains n variables, then
 * variables should be accessed by numbers from 1 to n and literals by numbers
 * from 2 to 2*n+1.
 * 
 * For a Dimacs variable v, the variable index in SAT4J is v, it's positive
 * literal is 2*v (v << 1) and it's negative literal is 2*v+1 ((v<<1)^1). Note
 * that one can easily access to the complementary literal of p by using bitwise
 * operation ^.
 * 
 * In SAT4J, literals are usualy denoted by p or q and variables by v or x.
 * 
 * @author leberre
 */
public interface ILits {

	public static int UNDEFINED = -1;

	public abstract void init(int nvar);

	/**
	 * Translates a Dimacs literal into an internal representation literal.
	 * 
	 * @param x
	 *            the Dimacs literal (a non null integer).
	 * @return the literal in the internal representation.
	 */
	public abstract int getFromPool(int x);

	/**
	 * Returns true iff the variable is used in the set of constraints.
	 * 
	 * @param x
	 * @return true iff the variable belongs to the formula.
	 */
	boolean belongsToPool(int x);

	public abstract void resetPool();

	public abstract void ensurePool(int howmany);

	public abstract void unassign(int lit);

	public abstract void satisfies(int lit);

	public abstract boolean isSatisfied(int lit);

	public abstract boolean isFalsified(int lit);

	public abstract boolean isUnassigned(int lit);

	/**
	 * @param lit
	 * @return true iff the truth value of that literal is due to a unit
	 *         propagation or a decision.
	 */
	public abstract boolean isImplied(int lit);

	/**
	 * to obtain the max id of the variable
	 * 
	 * @return the maximum number of variables in the formula
	 */
	public abstract int nVars();

	/**
	 * to obtain the real number of variables appearing in the formula
	 * 
	 * @return the number of variables used in the pool
	 */
	int realnVars();

	/**
	 * Ask the solver for a free variable identifier, in Dimacs format (i.e. a
	 * positive number). Note that a previous call to ensurePool(max) will
	 * reserve in the solver the variable identifier from 1 to max, so
	 * nextFreeVarId() would return max+1, even if some variable identifiers
	 * smaller than max are not used.
	 * 
	 * @return a variable identifier not in use in the constraints already
	 *         inside the solver.
	 * @since 2.1
	 */
	int nextFreeVarId(boolean reserve);

	public abstract int not(int lit);

	public abstract void reset(int lit);

	public abstract int getLevel(int lit);

	public abstract void setLevel(int lit, int l);

	public abstract Constr getReason(int lit);

	public abstract void setReason(int lit, Constr r);

	public abstract IVec<Undoable> undos(int lit);

	public abstract void watch(int lit, Propagatable c);

	/**
	 * @param lit
	 *            a literal
	 * @return the list of all the constraints that watch the negation of lit
	 */
	public abstract IVec<Propagatable> watches(int lit);

	public abstract String valueToString(int lit);
}
