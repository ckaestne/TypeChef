package org.anarres.cpp;

import de.fosd.typechef.featureexpr.FeatureExpr;

public class MacroConstraint {

	final String name;
	final MacroConstraintKind kind;
	final FeatureExpr expr;

	public MacroConstraint(String macroName, MacroConstraintKind kind,
			FeatureExpr featureExpression) {
		this.name = macroName;
		this.kind = kind;
		this.expr = featureExpression;
	}

	public enum MacroConstraintKind {
		NOTEXPANDING

	}

}
