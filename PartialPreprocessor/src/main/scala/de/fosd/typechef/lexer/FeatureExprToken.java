package de.fosd.typechef.lexer;

import de.fosd.typechef.featureexpr.FeatureExpr;

import java.io.PrintWriter;

/**
 * special kind of token that contains a (possibly complex) feature expression
 * <p/>
 * if necessary the token can be serialized for output, but preferably the token
 * is directly used internally to avoid reparsing large expressions and to avoid
 * expanding feature expressions to external tokens (which may cause huge
 * formulas)
 *
 * @author kaestner
 */
public class FeatureExprToken extends SimpleToken {
    private FeatureExpr expr;

    FeatureExprToken(FeatureExpr expr, Source source) {
        super(P_FEATUREEXPR, null/* initial text */, source);
        this.expr = expr;
        this.text = null;
    }

    /**
     * eager expansion, hopefully never used. prefer lazyPrint instead
     */
    @Override
    public String getText() {
        throw new IllegalArgumentException("getText not supported on FeatureExprToken");
    }

    public FeatureExpr getExpr() {
        return expr;
    }

    @Override
    public void lazyPrint(PrintWriter writer) {
        expr.print(writer);
    }

    public Token clone() {
        return new FeatureExprToken(expr, source);
    }

}
