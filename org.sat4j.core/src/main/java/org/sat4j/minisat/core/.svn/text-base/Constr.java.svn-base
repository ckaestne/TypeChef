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

import org.sat4j.specs.IConstr;
import org.sat4j.specs.IVecInt;

/*
 * Created on 16 oct. 2003
 */

/**
 * Basic constraint abstraction used in Solver.
 * 
 * Any new constraint type should implement that interface.
 * 
 * @author leberre
 */
public interface Constr extends Propagatable, IConstr {

	/**
	 * Remove a constraint from the solver.
	 * 
	 * @param upl
	 * @since 2.1
	 */
	void remove(UnitPropagationListener upl);

	/**
	 * Simplifies a constraint, by removing top level falsified literals for
	 * instance.
	 * 
	 * @return true iff the constraint is satisfied.
	 */
	boolean simplify();

	/**
	 * Compute the reason for a given assignment.
	 * 
	 * If the constraint is a clause, it is supposed to be either a unit clause
	 * or a falsified one.
	 * 
	 * @param p
	 *            a satisfied literal (or Lit.UNDEFINED)
	 * @param outReason
	 *            the list of falsified literals whose negation is the reason of
	 *            the assignment of p to true.
	 */
	void calcReason(int p, IVecInt outReason);

	/**
	 * Increase the constraint activity.
	 * 
	 * @param claInc
	 *            the value to increase the activity with
	 */
	void incActivity(double claInc);

	/**
	 * 
	 * @param claInc
	 * @since 2.1
	 * 
	 */
	@Deprecated
	void forwardActivity(double claInc);

	/**
	 * Indicate wether a constraint is responsible from an assignment.
	 * 
	 * @return true if a constraint is a "reason" for an assignment.
	 */
	boolean locked();

	/**
	 * Mark a constraint as learnt.
	 */

	void setLearnt();

	/**
	 * Register the constraint to the solver.
	 */
	void register();

	/**
	 * Rescale the clause activity by a value.
	 * 
	 * @param d
	 *            the value to rescale the clause activity with.
	 */
	void rescaleBy(double d);

	/**
	 * Method called when the constraint is to be asserted. It means that the
	 * constraint was learnt during the search and it should now propagate some
	 * truth values. In the clausal case, only one literal should be propagated.
	 * In other cases, it might be different.
	 * 
	 * @param s
	 *            a UnitPropagationListener to use for unit propagation.
	 */
	void assertConstraint(UnitPropagationListener s);
}
