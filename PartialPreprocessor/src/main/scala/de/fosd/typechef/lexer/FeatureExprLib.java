package de.fosd.typechef.lexer;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExpr$;
import de.fosd.typechef.featureexpr.FeatureExprTree;
import de.fosd.typechef.featureexpr.FeatureExprValue$;

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

    //TODO: for some reason FeatureExprTree<Long> does no longer work since
    //scala 2.9. We use FeatureExprTree<Object>, which seems unproblematic since
    //the result is not constructed or inspected in Java code anyway, but only passed around
    public static FeatureExprTree<Object> zero() {
        return FeatureExpr$.MODULE$.createInteger(0);
    }

    public static FeatureExpr toFeatureExpr(FeatureExprTree<Object> v) {
        return FeatureExprValue$.MODULE$.toFeatureExpr(v);
    }
}
