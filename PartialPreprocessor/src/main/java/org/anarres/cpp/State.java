package org.anarres.cpp;

import java.util.ArrayList;
import java.util.List;

import de.fosd.typechef.featureexpr.FeatureExpr;

class State {
	List<FeatureExpr> localFeatures = new ArrayList<FeatureExpr>();
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
		clearCache();
		sawElse = true;
		processElIf();
	}

	/* pp */boolean sawElse() {
		return sawElse;
	}

	public String toString() {
		return "currentpc=" + getFullPresenceCondition() + ", parent=" + parent
				+ ", active=" + localFeatures + ", sawelse=" + sawElse;
	}

	/**
	 * add a feature expression to the state. first the #if expression. if
	 * called again, this is interpreted as an elif expression.
	 * 
	 * @param feature
	 */
	public void putLocalFeature(FeatureExpr feature) {
		clearCache();
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
			assert !localFeatures.isEmpty() : "else before #if?";

		if (localFeatures.isEmpty())
			return new FeatureExpr().base();
		FeatureExpr result = localFeatures.get(localFeatures.size() - 1);
		/*if (sawElse)
			result = result.not();*/
		for (int i = 0; i < localFeatures.size() - 1; i++)
			//result = result.and(localFeatures.get(i).not());
			result = result.and(localFeatures.get(i));

		return result;
	}

	private FeatureExpr cache_fullPresenceCondition = null;
	private Boolean cache_isActive = null;

	/**
	 * returns the full feature condition that leads to the inclusion of the
	 * current token (includes all features of nested ifdefs)
	 * 
	 * @return
	 */
	public FeatureExpr getFullPresenceCondition() {
		if (cache_fullPresenceCondition == null) {
			FeatureExpr result = getLocalFeatureExpr();
			if (parent != null)
				result = result.and(parent.getFullPresenceCondition());
			cache_fullPresenceCondition = result;
		}
		return cache_fullPresenceCondition;
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
		if (cache_isActive == null)
			cache_isActive = new Boolean(!getFullPresenceCondition().isDead());
		return cache_isActive.booleanValue();
	}

	private void clearCache() {
		cache_fullPresenceCondition = null;
		cache_isActive = null;
	}

	/**
	 * normally each state represents a code block if an ifdef and endif. if the
	 * feature expression was base or dead, then the initial ifdef definition
	 * was skipped. the skipped expression is remembered here, so that also an
	 * according endif is not output
	 */
	private boolean ifdefBegin = true;

	public void setNoIfdefBegin() {
		ifdefBegin = false;
	}

	public boolean hasIfdefBegin() {
		return ifdefBegin;
	}

	public void processElIf() {
		localFeatures.set(localFeatures.size() - 1, localFeatures.get(localFeatures.size() - 1).not());
	}
}
