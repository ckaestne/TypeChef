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
package org.sat4j.minisat.learning;

import org.sat4j.minisat.core.Constr;
import org.sat4j.minisat.core.DataStructureFactory;

/**
 * Selects the constraints to learn according to its length as a percentage of
 * the total number of variables in the solver universe.
 * 
 * @author daniel
 * 
 */
public final class PercentLengthLearning<D extends DataStructureFactory>
		extends LimitedLearning<D> {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private int maxpercent;
	private int bound;

	public PercentLengthLearning() {
		this(10);
	}

	public PercentLengthLearning(int percent) {
		maxpercent = percent;
	}

	public void setLimit(int percent) {
		maxpercent = percent;
	}

	public int getLimit() {
		return maxpercent;
	}

	@Override
	public void init() {
		super.init();
		setBound(lits.realnVars() * maxpercent / 100);
	}

	@Override
	public String toString() {
		return "Limit learning to clauses of size smaller or equal to " //$NON-NLS-1$
				+ maxpercent + "% of the number of variables"; //$NON-NLS-1$
	}

	protected void setBound(int newbound) {
		bound = newbound;
	}

	@Override
	protected boolean learningCondition(Constr constr) {
		return constr.size() <= bound;
	}

}
