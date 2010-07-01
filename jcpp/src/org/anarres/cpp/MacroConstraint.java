package org.anarres.cpp;

import de.fosd.typechef.featureexpr.FeatureExpr;

public class MacroConstraint {

	private final String name;
	private final MacroConstraintKind kind;
	private final FeatureExpr expr;

	public MacroConstraint(String macroName, MacroConstraintKind kind,
			FeatureExpr featureExpression) {
		this.name=macroName;
		this.kind=kind;
		this.expr=featureExpression;
	}

	public enum MacroConstraintKind {
		NOTEXPANDING

	}

}
