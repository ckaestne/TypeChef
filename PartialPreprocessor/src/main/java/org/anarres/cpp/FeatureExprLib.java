package org.anarres.cpp;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExpr$;
import de.fosd.typechef.featureexpr.FeatureExprValue;

/**
 * helper class for more conventient access to scala library
 *
 * @author kaestner
 */
public class FeatureExprLib {
    public static FeatureExpr$ l() {
        return FeatureExpr$.MODULE$;
    }

    public static FeatureExpr base() {
        return FeatureExpr$.MODULE$.base();
    }

    public static FeatureExpr dead() {
        return FeatureExpr$.MODULE$.dead();
    }

    public static FeatureExprValue zero() {
        return FeatureExpr$.MODULE$.createInteger(0);
    }

}
