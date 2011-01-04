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
import org.sat4j.minisat.core.IOrder;
import org.sat4j.minisat.core.Solver;

/**
 * Learn clauses with a great number of active variables.
 * 
 * @author leberre
 */
public final class ActiveLearning<D extends DataStructureFactory> extends
		LimitedLearning<D> {

	private static final long serialVersionUID = 1L;

	private double percent;

	private IOrder order;

	private int maxpercent;

	public ActiveLearning() {
		this(0.95);
	}

	public ActiveLearning(double d) {
		this.percent = d;
	}

	public void setOrder(IOrder order) {
		this.order = order;
	}

	@Override
	public void setSolver(Solver<D> s) {
		super.setSolver(s);
		this.order = s.getOrder();
	}

	public void setActivityPercent(double d) {
		percent = d;
	}

	public double getActivityPercent() {
		return percent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sat4j.minisat.LimitedLearning#learningCondition(org.sat4j.minisat
	 * .Constr)
	 */
	@Override
	protected boolean learningCondition(Constr clause) {
		int nbactivevars = 0;
		for (int i = 0; i < clause.size(); i++) {
			if (order.varActivity(clause.get(i)) > 1) {
				nbactivevars++;
			}
		}
		return nbactivevars > clause.size() * percent;
	}

	@Override
	public String toString() {
		return "Limit learning to clauses containing active literals (" + percent * 100 + "%)"; //$NON-NLS-1$
	}

	public void setLimit(int percent) {
		maxpercent = percent;
	}

	public int getLimit() {
		return maxpercent;
	}
}
