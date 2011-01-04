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
 * Rapid restart strategy presented by Armin Biere during it's SAT 07 invited
 * talk.
 * 
 * @author leberre
 * 
 */
public final class ArminRestarts implements RestartStrategy {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private double inner, outer;
	private long conflicts;
	private SearchParams params;

	public void init(SearchParams theParams) {
		this.params = theParams;
		inner = theParams.getInitConflictBound();
		outer = theParams.getInitConflictBound();
		conflicts = Math.round(inner);
	}

	public long nextRestartNumberOfConflict() {
		return conflicts;
	}

	public void onRestart() {
		if (inner >= outer) {
			outer *= params.getConflictBoundIncFactor();
			inner = params.getInitConflictBound();
		} else {
			inner *= params.getConflictBoundIncFactor();
		}
		conflicts = Math.round(inner);
	}

	@Override
	public String toString() {
		return "Armin Biere (Picosat) restarts strategy";
	}
}
