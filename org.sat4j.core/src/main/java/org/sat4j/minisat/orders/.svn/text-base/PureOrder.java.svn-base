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
package org.sat4j.minisat.orders;

/**
 * @author leberre TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public final class PureOrder extends VarOrderHeap {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	private int period;

	private int cpt;

	public PureOrder() {
		this(20);
	}

	public PureOrder(int p) {
		setPeriod(p);
	}

	public final void setPeriod(int p) {
		period = p;
		cpt = period;
	}

	public int getPeriod() {
		return period;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sat4j.minisat.core.VarOrder#select()
	 */
	@Override
	public int select() {
		// wait period branching
		if (cpt < period) {
			cpt++;
		} else {
			// try to find a pure literal
			cpt = 0;
			int nblits = 2 * lits.nVars();
			for (int i = 2; i <= nblits; i++) {
				if (lits.isUnassigned(i) && lits.watches(i).size() > 0
						&& lits.watches(i ^ 1).size() == 0) {
					return i;
				}
			}
		}
		// not found: using normal order
		return super.select();
	}

	@Override
	public String toString() {
		return "tries to first branch on a single phase watched unassigned variable (pure literal if using a CB data structure) else VSIDS from MiniSAT"; //$NON-NLS-1$
	}
}
