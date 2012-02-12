package de.fosd.typechef.lexer.options;

import de.fosd.typechef.featureexpr.FeatureExpr;

/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 12.02.12
 * Time: 11:36
 * To change this template use File | Settings | File Templates.
 */
public class PartialConfiguration {
    private String[] def;
    private String[] undef;
    private FeatureExpr fexpr;

    public PartialConfiguration(
            String[] definedFeatures,
            String[] undefinedFeatures,
            FeatureExpr fexpr) {
        this.def = definedFeatures;
        this.undef = undefinedFeatures;
        this.fexpr = fexpr;
    }

    public String[] getDefinedFeatures() {
        return def;
    }

    public String[] getUndefinedFeatures() {
        return undef;
    }

    public FeatureExpr getFeatureExpr() {
        return fexpr;
    }
}
