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

import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains some statistics regarding the search.
 * 
 * @author daniel
 * 
 */
public class SolverStats implements Serializable {
	private static final long serialVersionUID = 1L;

	public int starts;

	public long decisions;

	public long propagations;

	public long inspects;

	public long conflicts;

	public long learnedliterals;

	public long learnedbinaryclauses;

	public long learnedternaryclauses;

	public long learnedclauses;

	public long ignoredclauses;

	public long rootSimplifications;

	public long reducedliterals;

	public long changedreason;

	public int reduceddb;

	public int shortcuts;

	public void reset() {
		starts = 0;
		decisions = 0;
		propagations = 0;
		inspects = 0;
		shortcuts = 0;
		conflicts = 0;
		learnedliterals = 0;
		learnedclauses = 0;
		ignoredclauses = 0;
		learnedbinaryclauses = 0;
		learnedternaryclauses = 0;
		rootSimplifications = 0;
		reducedliterals = 0;
		changedreason = 0;
		reduceddb = 0;
	}

	public void printStat(PrintWriter out, String prefix) {
		out.println(prefix + "starts\t\t: " + starts);
		out.println(prefix + "conflicts\t\t: " + conflicts);
		out.println(prefix + "decisions\t\t: " + decisions);
		out.println(prefix + "propagations\t\t: " + propagations);
		out.println(prefix + "inspects\t\t: " + inspects);
		out.println(prefix + "shortcuts\t\t: " + shortcuts);
		out.println(prefix + "learnt literals\t: " + learnedliterals);
		out
				.println(prefix + "learnt binary clauses\t: "
						+ learnedbinaryclauses);
		out.println(prefix + "learnt ternary clauses\t: "
				+ learnedternaryclauses);
		out.println(prefix + "learnt constraints\t: " + learnedclauses);
		out.println(prefix + "ignored constraints\t: " + ignoredclauses);
		out.println(prefix + "root simplifications\t: " + rootSimplifications);
		out.println(prefix + "removed literals (reason simplification)\t: "
				+ reducedliterals);
		out.println(prefix + "reason swapping (by a shorter reason)\t: "
				+ changedreason);
		out.println(prefix + "Calls to reduceDB\t: " + reduceddb);
	}

	public Map<String, Number> toMap() {
		Map<String, Number> map = new HashMap<String, Number>();
		for (Field f : this.getClass().getDeclaredFields()) {
			try {
				map.put(f.getName(), (Number) f.get(this));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return map;
	}
}
