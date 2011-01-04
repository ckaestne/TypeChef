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
package org.sat4j.minisat.restarts;

import org.sat4j.minisat.core.RestartStrategy;
import org.sat4j.minisat.core.SearchParams;

/**
 * Luby series
 */
public final class LubyRestarts implements RestartStrategy {

	private static final int DEFAULT_LUBY_FACTOR = 32;
	private static final int PRECOMPUTED_VALUES_IN_POOL = 32;
	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private static int[] cachedValues = new int[] { 0, 1, 1, 2 };

	public static final int luby(int i) {
		if (i >= Integer.MAX_VALUE / 2) {
			throw new IllegalArgumentException("i is too big");
		}
		if (i >= cachedValues.length) {
			int oldsize = cachedValues.length;
			int newsize = i << 1;
			int[] newContent = new int[newsize + 1];
			System.arraycopy(cachedValues, 0, newContent, 0, oldsize);
			int nextPowerOfTwo = 1;
			while (nextPowerOfTwo <= oldsize) {
				nextPowerOfTwo <<= 1;
			}
			int lastPowerOfTwo = nextPowerOfTwo >> 1;
			for (int j = oldsize; j <= newsize; j++) {
				if (j + 1 == nextPowerOfTwo) {
					newContent[j] = lastPowerOfTwo;
					lastPowerOfTwo = nextPowerOfTwo;
					nextPowerOfTwo <<= 1;
				} else {
					newContent[j] = newContent[j - lastPowerOfTwo + 1];
				}
			}
			cachedValues = newContent;

		}
		return cachedValues[i];
	}

	static {
		luby(PRECOMPUTED_VALUES_IN_POOL);
	}

	private int factor;

	private int count;

	public LubyRestarts() {
		this(DEFAULT_LUBY_FACTOR); // uses TiniSAT default
	}

	/**
	 * @param factor
	 *            the factor used for the Luby series.
	 * @since 2.1
	 */
	public LubyRestarts(int factor) {
		setFactor(factor);
	}

	public final void setFactor(int factor) {
		this.factor = factor;
	}

	public int getFactor() {
		return factor;
	}

	public void init(SearchParams params) {
		count = 1;
	}

	public long nextRestartNumberOfConflict() {
		return luby(count) * factor;
	}

	public void onRestart() {
		count++;
	}

	@Override
	public String toString() {
		return "luby style (SATZ_rand, TiniSAT) restarts strategy with factor "
				+ factor;
	}

}
