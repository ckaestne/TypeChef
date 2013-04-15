package de.fosd.typechef.lexer;

import de.fosd.typechef.featureexpr.FeatureExpr;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * A TokenSequenceToken is a token that internally is build from multiple tokens
 * <p/>
 * the rational for this token is to avoid creating new objects with potentially
 * large strings (especially if FeatureExprTokens are involved)
 * <p/>
 * behaves like the first token in the list (except for getText and printLazy)
 *
 * @author kaestner
 */
class TokenSequenceToken extends Token {

    private List<Token> internalTokens;
    private int type;
    private int line;
    private int column;
    private Source source;

    public TokenSequenceToken(int type, int line, int column,
                              List<Token> tokenList, Source source) {
        assert tokenList.size() > 0;
        this.type = type;
        this.line = line;
        this.column = column;
        this.source = source;
        this.internalTokens = tokenList;
    }

    private Token firstToken() {
        return internalTokens.get(0);
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public void setLine(int Line) {
        line = Line;
    }

    @Override
    public Source getSource() {
        return source;
    }

    @Override
    public String getSourceName() {
        return source.toString();
    }

    @Override
    public String getText() {
        StringWriter strWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(strWriter);
        lazyPrint(writer);
        return strWriter.getBuffer().toString();
    }

    @Override
    public void lazyPrint(PrintWriter writer) {
        for (Token tok : internalTokens)
            tok.lazyPrint(writer);
    }

    @Override
    public Token clone() {
        List<Token> tokenList = new ArrayList<Token>();
        for (Token t : internalTokens)
            tokenList.add(t.clone());
        return new TokenSequenceToken(type, line, column, tokenList, source);
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public Object getValue() {
        return firstToken().getValue();
    }

    @Override
    public boolean mayExpand() {
        return firstToken().mayExpand();
    }

    @Override
    public void setLocation(int line, int column) {
        firstToken().setLocation(line, column);
    }

    @Override
    public void setNoFurtherExpansion() {
        firstToken().setNoFurtherExpansion();
    }

    @Override
    public String toString() {
        return "TokenSequence[" + firstToken().toString() + "]";
    }

    @Override
    public FeatureExpr getFeature() {
        return firstToken().getFeature();
    }

    @Override
    public void setFeature(FeatureExpr expr) {
        for (Token t : internalTokens)
            t.setFeature(expr);
    }

}
