package org.sat4j.minisat.orders;

import java.io.PrintWriter;
import java.util.Random;

import org.sat4j.minisat.core.ILits;
import org.sat4j.minisat.core.IOrder;
import org.sat4j.minisat.core.IPhaseSelectionStrategy;

/**
 * @since 2.2
 */
public final class RandomWalkDecorator implements IOrder {

	private final IOrder decorated;

	private final double p;

	private final Random rand = new Random();
	private ILits voc;
	private int nbRandomWalks;

	public RandomWalkDecorator(IOrder order) {
		this(order, 0.01);
	}

	public RandomWalkDecorator(IOrder order, double p) {
		decorated = order;
		this.p = p;
	}

	public void assignLiteral(int q) {
		decorated.assignLiteral(q);
	}

	public IPhaseSelectionStrategy getPhaseSelectionStrategy() {
		return decorated.getPhaseSelectionStrategy();
	}

	public void init() {
		decorated.init();
		nbRandomWalks = 0;
	}

	public void printStat(PrintWriter out, String prefix) {
		out.println(prefix + "random Assignements: " + nbRandomWalks);
		decorated.printStat(out, prefix);
	}

	public int select() {
		if (rand.nextDouble() < p) {
			int lit;
			int maxlitid = (voc.nVars() << 1);
			for (int i = 0; i <= 10; i++) {
				lit = rand.nextInt(maxlitid) + 2;
				if (voc.isUnassigned(lit)) {
					nbRandomWalks++;
					return getPhaseSelectionStrategy().select(lit >> 1);
				}
			}
		}
		return decorated.select();
	}

	public void setLits(ILits lits) {
		decorated.setLits(lits);
		voc = lits;
	}

	public void setPhaseSelectionStrategy(IPhaseSelectionStrategy strategy) {
		decorated.setPhaseSelectionStrategy(strategy);
	}

	public void setVarDecay(double d) {
		decorated.setVarDecay(d);
	}

	public void undo(int x) {
		decorated.undo(x);
	}

	public void updateVar(int q) {
		decorated.updateVar(q);
	}

	public double varActivity(int q) {
		return decorated.varActivity(q);
	}

	public void varDecayActivity() {
		decorated.varDecayActivity();
	}

	public void updateVarAtDecisionLevel(int q) {
		decorated.updateVarAtDecisionLevel(q);
	}

	@Override
	public String toString() {
		return decorated.toString() + " with random walks " + p;
	}

}
