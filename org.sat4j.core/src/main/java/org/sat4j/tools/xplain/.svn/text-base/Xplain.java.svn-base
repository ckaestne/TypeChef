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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.SolverDecorator;

/**
 * Explanation framework for SAT4J.
 * 
 * The explanation uses selector variables and assumptions.
 * 
 * It is based on a two steps method: 1) extraction of a set of assumptions
 * implying the inconsistency 2) minimization of that set.
 * 
 * @author daniel
 * 
 * @param <T>
 *            a subinterface to ISolver.
 * @since 2.1
 */
public class Xplain<T extends ISolver> extends SolverDecorator<T> implements
		Explainer {

	protected Map<Integer, IConstr> constrs = new HashMap<Integer, IConstr>();

	protected IVecInt assump;

	private int lastCreatedVar;
	private boolean pooledVarId = false;
	private final IVecInt lastClause = new VecInt();

	private static final XplainStrategy XPLAIN_STRATEGY = new QuickXplainStrategy();

	public Xplain(T solver) {
		super(solver);
	}

	@Override
	public IConstr addClause(IVecInt literals) throws ContradictionException {
		if (literals.equals(lastClause)) {
			// System.err.println("c Duplicated entry: " + literals);
			return null;
		}
		lastClause.clear();
		literals.copyTo(lastClause);
		int newvar = createNewVar(literals);
		literals.push(newvar);
		IConstr constr = super.addClause(literals);
		if (constr == null) {
			discardLastestVar();
		} else {
			constrs.put(newvar, constr);
		}
		return constr;
	}

	/**
	 * 
	 * @param literals
	 * @return
	 * @since 2.1
	 */
	protected int createNewVar(IVecInt literals) {
		if (pooledVarId) {
			pooledVarId = false;
			return lastCreatedVar;
		}
		lastCreatedVar = nextFreeVarId(true);
		return lastCreatedVar;
	}

	protected void discardLastestVar() {
		pooledVarId = true;
	}

	@Override
	public IConstr addAtLeast(IVecInt literals, int degree)
			throws ContradictionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IConstr addAtMost(IVecInt literals, int degree)
			throws ContradictionException {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @since 2.2.4
	 * @return
	 * @throws TimeoutException
	 */
	private IVecInt explanationKeys() throws TimeoutException {
		assert !isSatisfiable(assump);
		ISolver solver = decorated();
		if (solver instanceof SolverDecorator<?>) {
			solver = ((SolverDecorator<? extends ISolver>) solver).decorated();
		}
		return XPLAIN_STRATEGY.explain(solver, constrs, assump);
	}

	public int[] minimalExplanation() throws TimeoutException {
		IVecInt keys = explanationKeys();
		keys.sort();
		List<Integer> allKeys = new ArrayList<Integer>(constrs.keySet());
		Collections.sort(allKeys);
		int[] model = new int[keys.size()];
		int i = 0;
		for (IteratorInt it = keys.iterator(); it.hasNext();) {
			model[i++] = allKeys.indexOf(it.next()) + 1;
		}
		return model;
	}

	/**
	 * @since 2.1
	 * @return
	 * @throws TimeoutException
	 */
	public Collection<IConstr> explain() throws TimeoutException {
		IVecInt keys = explanationKeys();
		Collection<IConstr> explanation = new ArrayList<IConstr>(keys.size());
		for (IteratorInt it = keys.iterator(); it.hasNext();) {
			explanation.add(constrs.get(it.next()));
		}
		return explanation;
	}

	/**
	 * @since 2.1
	 */
	public void cancelExplanation() {
		XPLAIN_STRATEGY.cancelExplanationComputation();
	}

	/**
	 * 
	 * @since 2.1
	 */
	public Collection<IConstr> getConstraints() {
		return constrs.values();
	}

	@Override
	public int[] findModel() throws TimeoutException {
		assump = VecInt.EMPTY;
		IVecInt extraVariables = new VecInt();
		for (Integer p : constrs.keySet()) {
			extraVariables.push(-p);
		}
		return super.findModel(extraVariables);
	}

	@Override
	public int[] findModel(IVecInt assumps) throws TimeoutException {
		assump = assumps;
		IVecInt extraVariables = new VecInt();
		assumps.copyTo(extraVariables);
		for (Integer p : constrs.keySet()) {
			extraVariables.push(-p);
		}
		return super.findModel(extraVariables);
	}

	@Override
	public boolean isSatisfiable() throws TimeoutException {
		assump = VecInt.EMPTY;
		IVecInt extraVariables = new VecInt();
		for (Integer p : constrs.keySet()) {
			extraVariables.push(-p);
		}
		return super.isSatisfiable(extraVariables);
	}

	@Override
	public boolean isSatisfiable(boolean global) throws TimeoutException {
		assump = VecInt.EMPTY;
		IVecInt extraVariables = new VecInt();
		for (Integer p : constrs.keySet()) {
			extraVariables.push(-p);
		}
		return super.isSatisfiable(extraVariables, global);
	}

	@Override
	public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
		assump = assumps;
		IVecInt extraVariables = new VecInt();
		assumps.copyTo(extraVariables);
		for (Integer p : constrs.keySet()) {
			extraVariables.push(-p);
		}
		return super.isSatisfiable(extraVariables);
	}

	@Override
	public boolean isSatisfiable(IVecInt assumps, boolean global)
			throws TimeoutException {
		assump = assumps;
		IVecInt extraVariables = new VecInt();
		assumps.copyTo(extraVariables);
		for (Integer p : constrs.keySet()) {
			extraVariables.push(-p);
		}
		return super.isSatisfiable(extraVariables, global);
	}

	@Override
	public int[] model() {
		int[] fullmodel = super.model();
		if (fullmodel == null) {
			return null;
		}
		int[] model = new int[fullmodel.length - constrs.size()];
		int j = 0;
		for (int i = 0; i < fullmodel.length; i++) {
			if (constrs.get(Math.abs(fullmodel[i])) == null) {
				model[j++] = fullmodel[i];
			}
		}
		return model;
	}
}
