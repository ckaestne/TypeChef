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

import java.io.Serializable;

import org.sat4j.core.LiteralsUtils;
import org.sat4j.core.Vec;
import org.sat4j.minisat.core.Constr;
import org.sat4j.minisat.core.ILits;
import org.sat4j.minisat.core.Propagatable;
import org.sat4j.minisat.core.Undoable;
import org.sat4j.specs.IVec;

/**
 * @author laihem
 * @author leberre
 * 
 */
public final class Lits implements Serializable, ILits {

	private static final int DEFAULT_INIT_SIZE = 128;

	private static final long serialVersionUID = 1L;

	private boolean pool[] = new boolean[1];

	private int realnVars = 0;

	@SuppressWarnings("unchecked")
	private IVec<Propagatable>[] watches = new IVec[0];

	private int[] level = new int[0];

	private Constr[] reason = new Constr[0];

	private int maxvarid = 0;

	@SuppressWarnings("unchecked")
	private IVec<Undoable>[] undos = new IVec[0];

	private boolean[] falsified = new boolean[0];

	public Lits() {
		init(DEFAULT_INIT_SIZE);
	}

	@SuppressWarnings( { "unchecked" })
	public final void init(int nvar) {
		if (nvar < pool.length) {
			return;
		}
		assert nvar >= 0;
		// let some space for unused 0 indexer.
		int nvars = nvar + 1;
		boolean[] npool = new boolean[nvars];
		System.arraycopy(pool, 0, npool, 0, pool.length);
		pool = npool;

		int[] nlevel = new int[nvars];
		System.arraycopy(level, 0, nlevel, 0, level.length);
		level = nlevel;

		IVec<Propagatable>[] nwatches = new IVec[2 * nvars];
		System.arraycopy(watches, 0, nwatches, 0, watches.length);
		watches = nwatches;

		IVec<Undoable>[] nundos = new IVec[nvars];
		System.arraycopy(undos, 0, nundos, 0, undos.length);
		undos = nundos;

		Constr[] nreason = new Constr[nvars];
		System.arraycopy(reason, 0, nreason, 0, reason.length);
		reason = nreason;

		boolean[] newFalsified = new boolean[2 * nvars];
		System.arraycopy(falsified, 0, newFalsified, 0, falsified.length);
		falsified = newFalsified;
	}

	public int getFromPool(int x) {
		int var = Math.abs(x);
		if (var >= pool.length) {
			init(Math.max(var, pool.length << 1));
		}
		assert var < pool.length;
		if (var > maxvarid) {
			maxvarid = var;
		}
		int lit = LiteralsUtils.toInternal(x);
		assert lit > 1;
		if (!pool[var]) {
			realnVars++;
			pool[var] = true;
			watches[var << 1] = new Vec<Propagatable>();
			watches[(var << 1) | 1] = new Vec<Propagatable>();
			undos[var] = new Vec<Undoable>();
			level[var] = -1;
			falsified[var << 1] = false; // because truthValue[var] is
			// UNDEFINED
			falsified[var << 1 | 1] = false; // because truthValue[var] is
			// UNDEFINED
		}
		return lit;
	}

	public boolean belongsToPool(int x) {
		assert x > 0;
		if (x >= pool.length) {
			return false;
		}
		return pool[x];
	}

	public void resetPool() {
		for (int i = 0; i < pool.length; i++) {
			if (pool[i]) {
				reset(i << 1);
			}
		}
	}

	public void ensurePool(int howmany) {
		if (howmany >= pool.length) {
			init(Math.max(howmany, pool.length << 1));
		}
		maxvarid = howmany;
	}

	public void unassign(int lit) {
		assert falsified[lit] || falsified[lit ^ 1];
		falsified[lit] = false;
		falsified[lit ^ 1] = false;
	}

	public void satisfies(int lit) {
		assert !falsified[lit] && !falsified[lit ^ 1];
		falsified[lit] = false;
		falsified[lit ^ 1] = true;
	}

	public boolean isSatisfied(int lit) {
		return falsified[lit ^ 1];
	}

	public final boolean isFalsified(int lit) {
		return falsified[lit];
	}

	public boolean isUnassigned(int lit) {
		return !falsified[lit] && !falsified[lit ^ 1];
	}

	public String valueToString(int lit) {
		if (isUnassigned(lit)) {
			return "?"; //$NON-NLS-1$
		}
		if (isSatisfied(lit)) {
			return "T"; //$NON-NLS-1$
		}
		return "F"; //$NON-NLS-1$
	}

	public int nVars() {
		// return pool.length - 1;
		return maxvarid;
	}

	public int not(int lit) {
		return lit ^ 1;
	}

	public static String toString(int lit) {
		return ((lit & 1) == 0 ? "" : "-") + (lit >> 1); //$NON-NLS-1$//$NON-NLS-2$
	}

	public void reset(int lit) {
		watches[lit].clear();
		watches[lit ^ 1].clear();
		level[lit >> 1] = -1;
		reason[lit >> 1] = null;
		undos[lit >> 1].clear();
		falsified[lit] = false;
		falsified[lit ^ 1] = false;
	}

	public int getLevel(int lit) {
		return level[lit >> 1];
	}

	public void setLevel(int lit, int l) {
		level[lit >> 1] = l;
	}

	public Constr getReason(int lit) {
		return reason[lit >> 1];
	}

	public void setReason(int lit, Constr r) {
		reason[lit >> 1] = r;
	}

	public IVec<Undoable> undos(int lit) {
		return undos[lit >> 1];
	}

	public void watch(int lit, Propagatable c) {
		watches[lit].push(c);
	}

	public IVec<Propagatable> watches(int lit) {
		return watches[lit];
	}

	public boolean isImplied(int lit) {
		int var = lit >> 1;
		assert reason[var] == null || falsified[lit] || falsified[lit ^ 1];
		// a literal is implied if it is a unit clause, ie
		// propagated without reason at decision level 0.
		return pool[var] && (reason[var] != null || level[var] == 0);
	}

	public int realnVars() {
		return realnVars;
	}

	/**
	 * To get the capacity of the current vocabulary.
	 * 
	 * @return the total number of variables that can be managed by the
	 *         vocabulary.
	 */
	protected int capacity() {
		return pool.length - 1;
	}

	/**
	 * @since 2.1
	 */
	public int nextFreeVarId(boolean reserve) {
		if (reserve) {
			ensurePool(maxvarid + 1);
			// ensure pool changes maxvarid
			return maxvarid;
		}
		return maxvarid + 1;
	}
}
