package org.anarres.cpp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.fosd.typechef.featureexpr.*;

class State {
	List<FeatureExpr> localFeatures = new LinkedList<FeatureExpr>();
	final State parent;

	boolean sawElse;

	/* pp */State() {
		this(null);
	}

	/* pp */State(State parent) {
		this.parent = parent;
		this.sawElse = false;
	}

	/* pp */void setSawElse() {
		fullPresenceConditionCache=null;
		sawElse = true;
	}

	/* pp */boolean sawElse() {
		return sawElse;
	}

	public String toString() {
		return "parent=" + parent + ", active=" + localFeatures + ", sawelse="
				+ sawElse;
	}

	/**
	 * add a feature expression to the state. first the #if expression. if
	 * called again, this is interpreted as an elif expression.
	 * 
	 * @param feature
	 */
	public void putLocalFeature(FeatureExpr feature) {
		fullPresenceConditionCache=null;
		localFeatures.add(feature);
	}

	/**
	 * returns the local feature expression (explicitly negating prior features
	 * from other elif branches, but not including features from outer nested
	 * ifdefs)
	 * 
	 * if this is already the else branch (sawElse is true) than the condition
	 * for the else branch (negating all features) is returned
	 * 
	 * @return
	 */
	public FeatureExpr getLocalFeatureExpr() {
		if (sawElse())
			assert !localFeatures.isEmpty();

		if (localFeatures.isEmpty())
			return new BaseFeature();
		FeatureExpr result = localFeatures.get(localFeatures.size() - 1);
		if (sawElse)
			result = new Not(result);
		for (int i = 0; i < localFeatures.size() - 1; i++)
			result = new And(result, new Not(localFeatures.get(i)));

		return result;
	}

	private FeatureExpr fullPresenceConditionCache = null;

	/**
	 * returns the full feature condition that leads to the inclusion of the
	 * current token (includes all features of nested ifdefs)
	 * 
	 * @return
	 */
	public FeatureExpr getFullPresenceCondition() {
		if (fullPresenceConditionCache == null) {
			FeatureExpr result = getLocalFeatureExpr();
			if (parent != null)
				result = new And(result, parent.getFullPresenceCondition());
			fullPresenceConditionCache = result.toCNF();
		}
		return fullPresenceConditionCache;
	}

	/**
	 * only returns false if a code fragment is certainly dead, i.e., there is
	 * no variant in which it is included.
	 * 
	 * this can happen when a feature is explicitly undefined or explicitly
	 * defined in the source code
	 * 
	 * @param context
	 * 
	 * @return
	 */
	public boolean isActive() {
		if (getFullPresenceCondition().isDead())
			return false;
		return true;
	}
}
