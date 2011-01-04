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
package org.sat4j.specs;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Map;

/**
 * This interface contains all services provided by a SAT solver.
 * 
 * @author leberre
 */
public interface ISolver extends IProblem, Serializable {

	/**
	 * Create a new variable in the solver (and thus in the vocabulary).
	 * 
	 * WE STRONGLY ENCOURAGE TO PRECOMPUTE THE NUMBER OF VARIABLES NEEDED AND TO
	 * USE newVar(howmany) INSTEAD. IF YOU EXPERIENCE A PROBLEM OF EFFICIENCY
	 * WHEN READING/BUILDING YOUR SAT INSTANCE, PLEASE CHECK THAT YOU ARE NOT
	 * USING THAT METHOD.
	 * 
	 * @return the number of variables available in the vocabulary, which is the
	 *         identifier of the new variable.
	 */
	@Deprecated
	int newVar();

	/**
	 * Create <code>howmany</code> variables in the solver (and thus in the
	 * vocabulary).
	 * 
	 * @param howmany
	 *            number of variables to create
	 * @return the total number of variables available in the solver (the
	 *         highest variable number)
	 */
	int newVar(int howmany);

	/**
	 * Ask the solver for a free variable identifier, in Dimacs format (i.e. a
	 * positive number). Note that a previous call to newVar(max) will reserve
	 * in the solver the variable identifier from 1 to max, so nextFreeVarId()
	 * would return max+1, even if some variable identifiers smaller than max
	 * are not used. By default, the method will always answer by the maximum
	 * variable identifier used so far + 1.
	 * 
	 * @param reserve
	 *            if true, the maxVarId is updated in the solver, i.e.
	 *            successive calls to nextFreeVarId(true) will return increasing
	 *            variable id while successive calls to nextFreeVarId(false)
	 *            will always answer the same.
	 * @return a variable identifier not in use in the constraints already
	 *         inside the solver.
	 * @since 2.1
	 */
	int nextFreeVarId(boolean reserve);

	/**
	 * To inform the solver of the expected number of clauses to read. This is
	 * an optional method, that is called when the <code>p cnf</code> line is
	 * read in dimacs formatted input file.
	 * 
	 * Note that this method is supposed to be called AFTER a call to
	 * newVar(int)
	 * 
	 * @param nb
	 *            the expected number of clauses.
	 * @see #newVar(int)
	 * @since 1.6
	 */
	void setExpectedNumberOfClauses(int nb);

	/**
	 * Create a clause from a set of literals The literals are represented by
	 * non null integers such that opposite literals a represented by opposite
	 * values. (classical Dimacs way of representing literals).
	 * 
	 * @param literals
	 *            a set of literals
	 * @return a reference to the constraint added in the solver, to use in
	 *         removeConstr().
	 * @throws ContradictionException
	 *             iff the vector of literals is empty or if it contains only
	 *             falsified literals after unit propagation
	 * @see #removeConstr(IConstr)
	 */
	IConstr addClause(IVecInt literals) throws ContradictionException;

	/**
	 * Add a clause in order to prevent an assignment to occur. This happens
	 * usually when iterating over models for instance.
	 * 
	 * @param literals
	 * @return
	 * @throws ContradictionException
	 * @since 2.1
	 */
	IConstr addBlockingClause(IVecInt literals) throws ContradictionException;

	/**
	 * Remove a constraint returned by one of the add method from the solver.
	 * All learned clauses will be cleared.
	 * 
	 * Current implementation does not handle properly the case of unit clauses.
	 * 
	 * @param c
	 *            a constraint returned by one of the add method.
	 * @return true if the constraint was successfully removed.
	 */
	boolean removeConstr(IConstr c);

	/**
	 * Remove a constraint returned by one of the add method from the solver
	 * that is subsumed by a constraint already in the solver or to be added to
	 * the solver.
	 * 
	 * Unlike the removeConstr() method, learned clauses will NOT be cleared.
	 * 
	 * That method is expected to be used to remove constraints used in the
	 * optimization process.
	 * 
	 * In order to prevent a wrong from the user, the method will only work if
	 * the argument is the last constraint added to the solver. An illegal
	 * argument exception will be thrown in other cases.
	 * 
	 * @param c
	 *            a constraint returned by one of the add method. It must be the
	 *            latest constr added to the solver.
	 * @return true if the constraint was successfully removed.
	 * @since 2.1
	 */
	boolean removeSubsumedConstr(IConstr c);

	/**
	 * Create clauses from a set of set of literals. This is convenient to
	 * create in a single call all the clauses (mandatory for the distributed
	 * version of the solver). It is mainly a loop to addClause().
	 * 
	 * @param clauses
	 *            a vector of set (VecInt) of literals in the dimacs format. The
	 *            vector can be reused since the solver is not supposed to keep
	 *            a reference to that vector.
	 * @throws ContradictionException
	 *             iff the vector of literals is empty or if it contains only
	 *             falsified literals after unit propagation
	 * @see #addClause(IVecInt)
	 */
	void addAllClauses(IVec<IVecInt> clauses) throws ContradictionException;

	/**
	 * Create a cardinality constraint of the type "at most n of those literals
	 * must be satisfied"
	 * 
	 * @param literals
	 *            a set of literals The vector can be reused since the solver is
	 *            not supposed to keep a reference to that vector.
	 * @param degree
	 *            the degree of the cardinality constraint
	 * @return a reference to the constraint added in the solver, to use in
	 *         removeConstr().
	 * @throws ContradictionException
	 *             iff the vector of literals is empty or if it contains more
	 *             than degree satisfied literals after unit propagation
	 * @see #removeConstr(IConstr)
	 */

	IConstr addAtMost(IVecInt literals, int degree)
			throws ContradictionException;

	/**
	 * Create a cardinality constraint of the type "at least n of those literals
	 * must be satisfied"
	 * 
	 * @param literals
	 *            a set of literals. The vector can be reused since the solver
	 *            is not supposed to keep a reference to that vector.
	 * @param degree
	 *            the degree of the cardinality constraint
	 * @return a reference to the constraint added in the solver, to use in
	 *         removeConstr().
	 * @throws ContradictionException
	 *             iff the vector of literals is empty or if degree literals are
	 *             not remaining unfalsified after unit propagation
	 * @see #removeConstr(IConstr)
	 */
	IConstr addAtLeast(IVecInt literals, int degree)
			throws ContradictionException;

	/**
	 * To set the internal timeout of the solver. When the timeout is reached, a
	 * timeout exception is launched by the solver.
	 * 
	 * @param t
	 *            the timeout (in s)
	 */
	void setTimeout(int t);

	/**
	 * To set the internal timeout of the solver. When the timeout is reached, a
	 * timeout exception is launched by the solver.
	 * 
	 * Here the timeout is given in number of conflicts. That way, the behavior
	 * of the solver should be the same across different architecture.
	 * 
	 * @param count
	 *            the timeout (in number of counflicts)
	 */
	void setTimeoutOnConflicts(int count);

	/**
	 * To set the internal timeout of the solver. When the timeout is reached, a
	 * timeout exception is launched by the solver.
	 * 
	 * @param t
	 *            the timeout (in milliseconds)
	 */
	void setTimeoutMs(long t);

	/**
	 * Useful to check the internal timeout of the solver.
	 * 
	 * @return the internal timeout of the solver (in seconds)
	 */
	int getTimeout();

	/**
	 * Useful to check the internal timeout of the solver.
	 * 
	 * @return the internal timeout of the solver (in milliseconds)
	 * @since 2.1
	 */
	long getTimeoutMs();

	/**
	 * Expire the timeout of the solver.
	 */
	void expireTimeout();

	/**
	 * Clean up the internal state of the solver.
	 */
	void reset();

	/**
	 * Display statistics to the given output stream Please use writers instead
	 * of stream.
	 * 
	 * @param out
	 * @param prefix
	 *            the prefix to put in front of each line
	 * @see #printStat(PrintWriter, String)
	 */
	@Deprecated
	void printStat(PrintStream out, String prefix);

	/**
	 * Display statistics to the given output writer
	 * 
	 * @param out
	 * @param prefix
	 *            the prefix to put in front of each line
	 * @since 1.6
	 */
	void printStat(PrintWriter out, String prefix);

	/**
	 * To obtain a map of the available statistics from the solver. Note that
	 * some keys might be specific to some solvers.
	 * 
	 * @return a Map with the name of the statistics as key.
	 */
	Map<String, Number> getStat();

	/**
	 * Display a textual representation of the solver configuration.
	 * 
	 * @param prefix
	 *            the prefix to use on each line.
	 * @return a textual description of the solver internals.
	 */
	String toString(String prefix);

	/**
	 * Remove clauses learned during the solving process.
	 */
	void clearLearntClauses();

	/**
	 * Set whether the solver is allowed to simplify the formula by propagating
	 * the truth value of top level satisfied variables.
	 * 
	 * Note that the solver should not be allowed to perform such simplification
	 * when constraint removal is planned.
	 */
	void setDBSimplificationAllowed(boolean status);

	/**
	 * Indicate whether the solver is allowed to simplify the formula by
	 * propagating the truth value of top level satisfied variables.
	 * 
	 * Note that the solver should not be allowed to perform such simplification
	 * when constraint removal is planned.
	 */
	boolean isDBSimplificationAllowed();

	/**
	 * Allow the user to hook a listener to the solver to be notified of the
	 * main steps of the search process.
	 * 
	 * @param sl
	 *            a Search Listener.
	 * @since 2.1
	 */
	void setSearchListener(SearchListener sl);

	/**
	 * Get the current SearchListener.
	 * 
	 * @return a Search Listener.
	 * @since 2.2
	 */
	SearchListener getSearchListener();

	/**
	 * To know if the solver is in verbose mode (output allowed) or not.
	 * 
	 * @return true if the solver is verbose.
	 * @since 2.2
	 */
	boolean isVerbose();

	/**
	 * Set the verbosity of the solver
	 * 
	 * @param value
	 *            true to allow the solver to output messages on the console,
	 *            false either.
	 * @since 2.2
	 */
	void setVerbose(boolean value);

	/**
	 * Set the prefix used to display information.
	 * 
	 * @param prefix
	 *            the prefix to be in front of each line of text
	 * @since 2.2
	 */
	void setLogPrefix(String prefix);

	/**
	 * 
	 * @return the string used to prefix the output.
	 * @since 2.2
	 */
	String getLogPrefix();

	/**
	 * 
	 * Retrieve an explanation of the inconsistency in terms of assumption
	 * literals. This is only application when isSatisfiable(assumps) is used.
	 * Note that !isSatisfiable(assumps)&&assumps.contains(unsatExplanation())
	 * should hold.
	 * 
	 * @return a subset of the assumptions used when calling
	 *         isSatisfiable(assumps). Will return null if not applicable (i.e.
	 *         no assumptions used).
	 * @see #isSatisfiable(IVecInt)
	 * @see #isSatisfiable(IVecInt, boolean)
	 * @since 2.2
	 */
	IVecInt unsatExplanation();
}
