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
package org.sat4j.tools.xplain;

import java.util.Map;
import java.util.Set;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

/**
 * An implementation of the ReplayXplain algorithm as explained by Ulrich Junker
 * in the following paper:
 * 
 * @inproceedings{ junker01:quickxplain:inp, author={Ulrich Junker},
 *                 title={QUICKXPLAIN: Conflict Detection for Arbitrary
 *                 Constraint Propagation Algorithms}, booktitle={IJCAI'01
 *                 Workshop on Modelling and Solving problems with constraints
 *                 (CONS-1)}, year={2001}, month={August}, address={Seattle, WA,
 *                 USA}, url={citeseer.ist.psu.edu/junker01quickxplain.html},
 *                 url={http://www.lirmm.fr/~bessiere/ws_ijcai01/junker.ps.gz} }
 * 
 *                 The algorithm has been adapted to work properly in a context
 *                 where we can afford to add a selector variable to each clause
 *                 to enable or disable each constraint.
 * 
 *                 Note that for the moment, QuickXplain does not work properly
 *                 in an optimization setting.
 * 
 * 
 * @author daniel
 * @since 2.1
 */
public class ReplayXplainStrategy implements XplainStrategy {

	private boolean computationCanceled;

	/**
	 * @since 2.1
	 */
	public void cancelExplanationComputation() {
		computationCanceled = true;
	}

	/**
	 * @since 2.1
	 */
	public IVecInt explain(ISolver solver, Map<Integer, ?> constrs,
			IVecInt assumps) throws TimeoutException {
		computationCanceled = false;
		IVecInt encodingAssumptions = new VecInt(constrs.size()
				+ assumps.size());
		assumps.copyTo(encodingAssumptions);
		IVecInt firstExplanation = solver.unsatExplanation();
		Set<Integer> constraintsVariables = constrs.keySet();
		int p;
		for (int i = 0; i < firstExplanation.size(); i++) {
			if (constraintsVariables.contains(p = -firstExplanation.get(i))) {
				encodingAssumptions.push(p);
			}
		}
		boolean shouldContinue;
		int startingPoint = assumps.size();
		do {
			shouldContinue = false;
			int i = startingPoint;
			encodingAssumptions.set(i, -encodingAssumptions.get(i));
			assert encodingAssumptions.get(i) < 0;
			while (!computationCanceled
					&& solver.isSatisfiable(encodingAssumptions)) {
				i++;
				assert encodingAssumptions.get(i) > 0;
				encodingAssumptions.set(i, -encodingAssumptions.get(i));
			}
			if (!computationCanceled && i > startingPoint) {
				assert !solver.isSatisfiable(encodingAssumptions);
				if (i < encodingAssumptions.size()) {
					// latest constraint is for sure responsible for the
					// inconsistency.
					int tmp = encodingAssumptions.get(i);
					for (int j = i; j > startingPoint; j--) {
						encodingAssumptions.set(j,
								-encodingAssumptions.get(j - 1));
					}
					encodingAssumptions.set(startingPoint, tmp);
				}
				shouldContinue = true;
			}
			startingPoint++;
		} while (!computationCanceled && shouldContinue
				&& solver.isSatisfiable(encodingAssumptions));
		if (computationCanceled) {
			throw new TimeoutException();
		}
		IVecInt constrsKeys = new VecInt(startingPoint);
		for (int i = assumps.size(); i < startingPoint; i++) {
			constrsKeys.push(-encodingAssumptions.get(i));
		}
		return constrsKeys;
	}
}
