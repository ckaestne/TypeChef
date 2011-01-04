package org.sat4j.tools;

import java.util.ArrayList;
import java.util.List;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.IOptimizationProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;
import org.sat4j.specs.TimeoutException;

public class LexicoDecorator<T extends ISolver> extends SolverDecorator<T>
		implements IOptimizationProblem {

	protected final List<IVecInt> criteria = new ArrayList<IVecInt>();

	protected int currentCriterion = 0;

	private IConstr prevConstr;

	private int currentValue = -1;

	protected int[] prevfullmodel;
	protected boolean[] prevboolmodel;

	private boolean isSolutionOptimal;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LexicoDecorator(T solver) {
		super(solver);
	}

	public void addCriterion(IVecInt literals) {
		IVecInt copy = new VecInt(literals.size());
		literals.copyTo(copy);
		criteria.add(copy);
	}

	public boolean admitABetterSolution() throws TimeoutException {
		return admitABetterSolution(VecInt.EMPTY);
	}

	public boolean admitABetterSolution(IVecInt assumps)
			throws TimeoutException {
		isSolutionOptimal = false;
		if (decorated().isSatisfiable(assumps, true)) {
			prevboolmodel = new boolean[nVars()];
			for (int i = 0; i < nVars(); i++) {
				prevboolmodel[i] = decorated().model(i + 1);
			}
			calculateObjective();
			prevfullmodel = decorated().model();
			return true;
		}
		return manageUnsatCase();
	}

	private boolean manageUnsatCase() {
		if (currentCriterion < criteria.size() - 1) {
			if (prevConstr != null) {
				super.removeConstr(prevConstr);
				prevConstr = null;
			}
			try {
				super.addAtMost(criteria.get(currentCriterion), currentValue);
				super.addAtLeast(criteria.get(currentCriterion), currentValue);
			} catch (ContradictionException e) {
				throw new IllegalStateException(e);
			}
			if (isVerbose()) {
				System.out.println(getLogPrefix()
						+ "Found optimal criterion number "
						+ (currentCriterion + 1));
			}
			currentCriterion++;
			calculateObjective();
			return true;
		}
		if (isVerbose()) {
			System.out.println(getLogPrefix()
					+ "Found optimal solution for the last criterion ");
		}
		isSolutionOptimal = true;
		if (prevConstr != null) {
			super.removeConstr(prevConstr);
			prevConstr = null;
		}
		return false;
	}

	@Override
	public int[] model() {
		return prevfullmodel;
	}

	@Override
	public boolean model(int var) {
		return prevboolmodel[var - 1];
	}

	public boolean hasNoObjectiveFunction() {
		return false;
	}

	public boolean nonOptimalMeansSatisfiable() {
		return true;
	}

	public Number calculateObjective() {
		currentValue = evaluate();
		return currentValue;
	}

	public Number getObjectiveValue() {
		return currentValue;
	}

	public void forceObjectiveValueTo(Number forcedValue)
			throws ContradictionException {
		throw new UnsupportedOperationException();
	}

	public void discard() throws ContradictionException {
		discardCurrentSolution();

	}

	public void discardCurrentSolution() throws ContradictionException {
		if (prevConstr != null) {
			super.removeSubsumedConstr(prevConstr);
		}
		try {
			prevConstr = super.addAtMost(criteria.get(currentCriterion),
					currentValue - 1);
		} catch (ContradictionException c) {
			prevConstr = null;
			if (!manageUnsatCase()) {
				throw c;
			}
		}

	}

	private int evaluate() {
		int value = 0;
		int lit;
		for (IteratorInt it = criteria.get(currentCriterion).iterator(); it
				.hasNext();) {
			lit = it.next();
			if ((lit > 0 && prevboolmodel[lit - 1])
					|| (lit < 0 && !prevboolmodel[-lit - 1])) {
				value++;
			}
		}
		return value;
	}

	public boolean isOptimal() {
		return isSolutionOptimal;
	}

}
