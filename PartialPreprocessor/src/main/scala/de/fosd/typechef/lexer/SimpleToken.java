/*
 * TypeChef Variability-Aware Lexer.
 * Copyright 2010-2011, Christian Kaestner, Paolo Giarrusso
 * Licensed under GPL 3.0
 *
 * built on top of
 *
 * Anarres C Preprocessor
 * Copyright (c) 2007-2008, Shevek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package de.fosd.typechef.lexer;


import de.fosd.typechef.featureexpr.FeatureExpr;

/**
 * A Preprocessor token.
 *
 * @see Preprocessor
 */
public class SimpleToken extends Token {

    // public static final int EOF = -1;

    private int type;
    private int line;
    private int column;
    private Object value;
    protected String text;
    protected Source source;// for debugging purposes only
    private FeatureExpr presenceCondition = FeatureExprLib.True();
    private String sourceStr;

    public SimpleToken(int type, int line, int column, String text,
                       Object value, Source source) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.text = text;
        this.value = value;
        this.source = source;
        if (source == null) sourceStr = null;
        else sourceStr = source.toString();
    }

    public SimpleToken(int type, int line, int column, String text,
                       Source source) {
        this(type, line, column, text, null, source);
    }

    /* pp */SimpleToken(int type, String text, Object value, Source source) {
        this(type, -1, -1, text, value, source);
    }

    /* pp */SimpleToken(int type, String text, Source source) {
        this(type, text, null, source);
    }

    /* pp */SimpleToken(int type, Source source) {
        this(type, type < _TOKENS ? texts[type] : "TOK" + type, source);
    }

    /**
     * Returns the semantic type of this token.
     */
    public int getType() {
        return type;
    }

    public void setLocation(int line, int column) {
        this.line = line;
        this.column = column;
    }

    /**
     * Returns the line at which this token started.
     * <p/>
     * Lines are numbered from zero.
     */
    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    /**
     * Returns the column at which this token started.
     * <p/>
     * Columns are numbered from zero.
     */
    public int getColumn() {
        return column;
    }


    /**
     * Returns the original or generated text of this token.
     * <p/>
     * This is distinct from the semantic value of the token.
     *
     * @see #getValue()
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the semantic value of this token.
     * <p/>
     * For strings, this is the parsed String. For integers, this is an Integer
     * object. For other token types, as appropriate.
     *
     * @see #getText()
     */
    public Object getValue() {
        return value;
    }

    public FeatureExpr getFeature() {
        return presenceCondition;
    }

    public void setFeature(FeatureExpr expr) {
        presenceCondition = expr;
    }

    /**
     * Returns a description of this token, for debugging purposes.
     */
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append('[').append(getTokenName(type));
        if (line != -1) {
            buf.append('@').append(line);
            if (column != -1)
                buf.append(',').append(column);
        }
        buf.append("]:");
        if (text != null)
            buf.append('"').append(text).append('"');
        else if (type > 3 && type < 256)
            buf.append((char) type);
        else
            buf.append('<').append(type).append('>');
        if (value != null)
            buf.append('=').append(value);
        buf.append('@').append(presenceCondition);
        return buf.toString();
    }

    private boolean mayExpand = true;

    public void setNoFurtherExpansion() {
        mayExpand = false;
    }

    public boolean mayExpand() {
        return mayExpand;
    }

    @Override
    public String getSourceName() {
        return sourceStr;
    }

    @Override
    public void setSourceName(String src) {
        this.sourceStr = src;
    }

    public Source getSource() {
        return source;
    }

    @Override
    public Token clone() {
        Token t = new SimpleToken(type, line, column, text, value, source);
        t.setFeature(presenceCondition);
        return t;
    }
}
