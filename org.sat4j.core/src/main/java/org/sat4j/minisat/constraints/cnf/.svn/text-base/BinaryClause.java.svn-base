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
package org.sat4j.minisat.constraints.cnf;

import static org.sat4j.core.LiteralsUtils.neg;

import java.io.Serializable;

import org.sat4j.minisat.core.Constr;
import org.sat4j.minisat.core.ILits;
import org.sat4j.minisat.core.UnitPropagationListener;
import org.sat4j.specs.IVecInt;

/**
 * Data structure for binary clause.
 * 
 * @author leberre
 * @since 2.1
 */
public abstract class BinaryClause implements Constr, Serializable {

	private static final long serialVersionUID = 1L;

	protected double activity;

	private final ILits voc;

	protected int head;

	protected int tail;

	/**
	 * Creates a new basic clause
	 * 
	 * @param voc
	 *            the vocabulary of the formula
	 * @param ps
	 *            A VecInt that WILL BE EMPTY after calling that method.
	 */
	public BinaryClause(IVecInt ps, ILits voc) {
		assert ps.size() == 2;
		head = ps.get(0);
		tail = ps.get(1);
		this.voc = voc;
		activity = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Constr#calcReason(Solver, Lit, Vec)
	 */
	public void calcReason(int p, IVecInt outReason) {
		if (voc.isFalsified(head)) {
			outReason.push(neg(head));
		}
		if (voc.isFalsified(tail)) {
			outReason.push(neg(tail));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Constr#remove(Solver)
	 */
	public void remove(UnitPropagationListener upl) {
		voc.watches(neg(head)).remove(this);
		voc.watches(neg(tail)).remove(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Constr#simplify(Solver)
	 */
	public boolean simplify() {
		if (voc.isSatisfied(head) || voc.isSatisfied(tail)) {
			return true;
		}
		return false;
	}

	public boolean propagate(UnitPropagationListener s, int p) {
		voc.watch(p, this);
		if (head == neg(p)) {
			return s.enqueue(tail, this);
		}
		assert tail == neg(p);
		return s.enqueue(head, this);
	}

	/*
	 * For learnt clauses only @author leberre
	 */
	public boolean locked() {
		return voc.getReason(head) == this || voc.getReason(tail) == this;
	}

	/**
	 * @return the activity of the clause
	 */
	public double getActivity() {
		return activity;
	}

	@Override
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append(Lits.toString(head));
		stb.append("["); //$NON-NLS-1$
		stb.append(voc.valueToString(head));
		stb.append("]"); //$NON-NLS-1$
		stb.append(" "); //$NON-NLS-1$
		stb.append(Lits.toString(tail));
		stb.append("["); //$NON-NLS-1$
		stb.append(voc.valueToString(tail));
		stb.append("]"); //$NON-NLS-1$
		return stb.toString();
	}

	/**
	 * Retourne le ieme literal de la clause. Attention, cet ordre change durant
	 * la recherche.
	 * 
	 * @param i
	 *            the index of the literal
	 * @return the literal
	 */
	public int get(int i) {
		if (i == 0)
			return head;
		assert i == 1;
		return tail;
	}

	/**
	 * @param d
	 */
	public void rescaleBy(double d) {
		activity *= d;
	}

	public int size() {
		return 2;
	}

	public void assertConstraint(UnitPropagationListener s) {
		assert voc.isUnassigned(head);
		boolean ret = s.enqueue(head, this);
		assert ret;
	}

	public ILits getVocabulary() {
		return voc;
	}

	public int[] getLits() {
		int[] tmp = new int[2];
		tmp[0] = head;
		tmp[1] = tail;
		return tmp;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		try {
			BinaryClause wcl = (BinaryClause) obj;
			if (wcl.head != head || wcl.tail != tail) {
				return false;
			}
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		long sum = head + tail;
		return (int) sum / 2;
	}

	public void register() {
		voc.watch(neg(head), this);
		voc.watch(neg(tail), this);
	}
}
