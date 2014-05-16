package de.fosd.typechef.lexer;

import de.fosd.typechef.featureexpr.*;

/**
 * helper class for more conventient access to scala library
 *
 * @author kaestner
 */
public class FeatureExprLib {
    public static AbstractFeatureExprFactory l() {
        return FeatureExprFactory$.MODULE$.dflt();
    }

    public static FeatureExpr True() {
        return FeatureExprFactory$.MODULE$.True();
    }

    public static FeatureExpr False() {
        return FeatureExprFactory$.MODULE$.False();
    }

    //TODO: for some reason FeatureExprTree<Long> does no longer work since
    //scala 2.9. We use FeatureExprTree<Object>, which seems unproblematic since
    //the result is not constructed or inspected in Java code anyway, but only passed around
    public static FeatureExprTree<Object> zero() {
        return FeatureExprFactory$.MODULE$.dflt().createInteger(0);
    }

    public static FeatureExpr toFeatureExpr(FeatureExprTree<Object> v) {
        return FeatureExprValue$.MODULE$.toFeatureExpr(v, l());
    }

    public static FeatureModelFactory featureModelFactory() {
        return FeatureExprFactory$.MODULE$.dflt().featureModelFactory();
    }


    public static FeatureExprParser featureExprParser() {
        return new FeatureExprParserJava(l());
    }

}
