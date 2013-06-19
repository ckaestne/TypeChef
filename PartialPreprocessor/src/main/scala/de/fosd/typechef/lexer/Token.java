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

import de.fosd.typechef.LexerToken;
import de.fosd.typechef.featureexpr.FeatureExpr;

import java.io.PrintWriter;

/**
 * A Preprocessor token.
 *
 * @see Preprocessor
 */
public abstract class Token implements LexerToken {

    /**
     * Returns the semantic type of this token.
     */
    /*package*/
    abstract int getType();

    public abstract void setLocation(int line, int column);

    /**
     * Returns the line at which this token started.
     * <p/>
     * Lines are numbered from zero.
     */
    public abstract int getLine();

    /**
     * setLine can be used to overwrite the line location
     */
    public abstract void setLine(int line);

    /**
     * Returns the column at which this token started.
     * <p/>
     * Columns are numbered from zero.
     */
    public abstract int getColumn();

    /**
     * Returns the original or generated text of this token.
     * <p/>
     * This is distinct from the semantic value of the token.
     *
     * @see #getValue()
     */
    public abstract String getText();

    /**
     * Returns the semantic value of this token.
     * <p/>
     * For strings, this is the parsed String. For integers, this is an Integer
     * object. For other token types, as appropriate.
     *
     * @see #getText()
     */
    public abstract Object getValue();

    public abstract FeatureExpr getFeature();

    public abstract void setFeature(FeatureExpr expr);

    /**
     * Returns a description of this token, for debugging purposes.
     */
    public abstract String toString();

    public boolean isEOF() {
        return getType() == Token.EOF;
    }


    /**
     * properties of tokens used by other clients like the C parser
     */
    @Override
    public boolean isLanguageToken() {
        return getType() != Token.P_LINE
                && getType() != Token.WHITESPACE
                && !(getType() != Token.P_FEATUREEXPR
                && getText().equals("__extension__"))
                && getType() != Token.NL
                && getType() != Token.P_IF
                && getType() != Token.CCOMMENT
                && getType() != Token.CPPCOMMENT
                && getType() != Token.P_ENDIF
                && getType() != Token.P_ELIF;
    }


    @Override
    public boolean isNumberLiteral() {
        return getType() == INTEGER;
    }

    @Override
    public boolean isStringLiteral() {
        return getType() == STRING;
    }

    @Override
    public boolean isCharacterLiteral() {
        return getType() == CHARACTER;
    }

    @Override
    public boolean isKeywordOrIdentifier() {
        return getType() == IDENTIFIER;
    }


    /**
     * Returns the descriptive name of the given token type.
     * <p/>
     * This is mostly used for stringification and debugging.
     */
    public static final String getTokenName(int type) {
        if (type < 0)
            return "Invalid" + type;
        if (type >= names.length)
            return "Invalid" + type;
        if (names[type] == null)
            return "Unknown" + type;
        return names[type];
    }

    /**
     * The token type AND_EQ.
     */
    /*package*/ static final int AND_EQ = 257;
    /**
     * The token type ARROW.
     */
    /*package*/ static final int ARROW = 258;
    /**
     * The token type CHARACTER.
     */
    /*package*/ static final int CHARACTER = 259;
    /**
     * The token type CCOMMENT.
     */
    /*package*/ static final int CCOMMENT = 260;
    /**
     * The token type CPPCOMMENT.
     */
    /*package*/ static final int CPPCOMMENT = 261;
    /**
     * The token type DEC.
     */
    /*package*/ static final int DEC = 262;
    /**
     * The token type DIV_EQ.
     */
    /*package*/ static final int DIV_EQ = 263;
    /**
     * The token type ELLIPSIS.
     */
    /*package*/ static final int ELLIPSIS = 264;
    /**
     * The token type EOF.
     */
    /*package*/ static final int EOF = 265;
    /**
     * The token type EQ.
     */
    /*package*/ static final int EQ = 266;
    /**
     * The token type GE.
     */
    /*package*/ static final int GE = 267;
    /**
     * The token type HASH.
     */
    /*package*/ static final int HASH = 268;
    /**
     * The token type HEADER.
     */
    /*package*/ static final int HEADER = 269;
    /**
     * The token type IDENTIFIER.
     */
    /*package*/ static final int IDENTIFIER = 270;
    /**
     * The token type INC.
     */
    /*package*/ static final int INC = 271;
    /**
     * The token type INTEGER.
     */
    /*package*/ static final int INTEGER = 272;
    /**
     * The token type LAND.
     */
    /*package*/ static final int LAND = 273;
    /**
     * The token type LAND_EQ.
     */
    /*package*/ static final int LAND_EQ = 274;
    /**
     * The token type LE.
     */
    /*package*/ static final int LE = 275;
    /**
     * The token type LITERAL.
     */
    /*package*/ static final int LITERAL = 276;
    /**
     * The token type LOR.
     */
    /*package*/ static final int LOR = 277;
    /**
     * The token type LOR_EQ.
     */
    /*package*/ static final int LOR_EQ = 278;
    /**
     * The token type LSH.
     */
    /*package*/ static final int LSH = 279;
    /**
     * The token type LSH_EQ.
     */
    /*package*/ static final int LSH_EQ = 280;
    /**
     * The token type MOD_EQ.
     */
    /*package*/ static final int MOD_EQ = 281;
    /**
     * The token type MULT_EQ.
     */
    /*package*/ static final int MULT_EQ = 282;
    /**
     * The token type NE.
     */
    /*package*/ static final int NE = 283;
    /**
     * The token type NL.
     */
    /*package*/ static final int NL = 284;
    /**
     * The token type OR_EQ.
     */
    /*package*/ static final int OR_EQ = 285;
    /**
     * The token type PASTE.
     */
    /*package*/ static final int PASTE = 286;
    /**
     * The token type PLUS_EQ.
     */
    /*package*/ static final int PLUS_EQ = 287;
    /**
     * The token type RANGE.
     */
    /*package*/ static final int RANGE = 288;
    /**
     * The token type RSH.
     */
    /*package*/ static final int RSH = 289;
    /**
     * The token type RSH_EQ.
     */
    /*package*/ static final int RSH_EQ = 290;
    /**
     * The token type STRING.
     */
    /*package*/ static final int STRING = 291;
    /**
     * The token type SUB_EQ.
     */
    /*package*/ static final int SUB_EQ = 292;
    /**
     * The token type WHITESPACE.
     */
    /*package*/ static final int WHITESPACE = 293;
    /**
     * The token type XOR_EQ.
     */
    /*package*/ static final int XOR_EQ = 294;
    /**
     * The token type M_ARG.
     */
    /*package*/ static final int M_ARG = 295;
    /**
     * The token type M_PASTE.
     */
    /*package*/ static final int M_PASTE = 296;
    /**
     * The token type M_STRING.
     */
    /*package*/ static final int M_STRING = 297;
    /**
     * The token type P_LINE.
     */
    /*package*/ static final int P_LINE = 298;
    /**
     * The token type INVALID.
     */
    /*package*/ static final int INVALID = 299;
    /**
     * The token type P_LINE.
     */
    /*package*/ static final int P_IF = 300;
    /**
     * The token type P_LINE.
     */
    /*package*/ static final int P_ENDIF = 301;
    /**
     * The token type P_LINE.
     */
    /*package*/ static final int P_ELIF = 302;
    /**
     * The token type P_LINE.
     */
    /*package*/ static final int P_FEATUREEXPR = 303;
    /**
     * The number of possible semantic token types.
     * <p/>
     * Please note that not all token types below 255 are used.
     */
    /*package*/ static final int _TOKENS = 304;

    /**
     * The position-less space token.
     */
    /* pp */static final Token space = new SimpleToken(WHITESPACE, -1, -1, " ", null);

    private static final String[] names = new String[_TOKENS];
    protected static final String[] texts = new String[_TOKENS];

    static {
        for (int i = 0; i < 255; i++) {
            texts[i] = String.valueOf(new char[]{(char) i});
            names[i] = texts[i];
        }

        texts[AND_EQ] = "&=";
        texts[ARROW] = "->";
        texts[DEC] = "--";
        texts[DIV_EQ] = "/=";
        texts[ELLIPSIS] = "...";
        texts[EQ] = "==";
        texts[GE] = ">=";
        texts[HASH] = "#";
        texts[INC] = "++";
        texts[LAND] = "&&";
        texts[LAND_EQ] = "&&=";
        texts[LE] = "<=";
        texts[LOR] = "||";
        texts[LOR_EQ] = "||=";
        texts[LSH] = "<<";
        texts[LSH_EQ] = "<<=";
        texts[MOD_EQ] = "%=";
        texts[MULT_EQ] = "*=";
        texts[NE] = "!=";
        texts[NL] = "\n";
        texts[OR_EQ] = "|=";
        /* We have to split the two hashes or Velocity eats them. */
        texts[PASTE] = "#" + "#";
        texts[PLUS_EQ] = "+=";
        texts[RANGE] = "..";
        texts[RSH] = ">>";
        texts[RSH_EQ] = ">>=";
        texts[SUB_EQ] = "-=";
        texts[XOR_EQ] = "^=";

        names[AND_EQ] = "AND_EQ";
        names[ARROW] = "ARROW";
        names[CHARACTER] = "CHARACTER";
        names[CCOMMENT] = "CCOMMENT";
        names[CPPCOMMENT] = "CPPCOMMENT";
        names[DEC] = "DEC";
        names[DIV_EQ] = "DIV_EQ";
        names[ELLIPSIS] = "ELLIPSIS";
        names[EOF] = "EOF";
        names[EQ] = "EQ";
        names[GE] = "GE";
        names[HASH] = "HASH";
        names[HEADER] = "HEADER";
        names[IDENTIFIER] = "IDENTIFIER";
        names[INC] = "INC";
        names[INTEGER] = "INTEGER";
        names[LAND] = "LAND";
        names[LAND_EQ] = "LAND_EQ";
        names[LE] = "LE";
        names[LITERAL] = "LITERAL";
        names[LOR] = "LOR";
        names[LOR_EQ] = "LOR_EQ";
        names[LSH] = "LSH";
        names[LSH_EQ] = "LSH_EQ";
        names[MOD_EQ] = "MOD_EQ";
        names[MULT_EQ] = "MULT_EQ";
        names[NE] = "NE";
        names[NL] = "NL";
        names[OR_EQ] = "OR_EQ";
        names[PASTE] = "PASTE";
        names[PLUS_EQ] = "PLUS_EQ";
        names[RANGE] = "RANGE";
        names[RSH] = "RSH";
        names[RSH_EQ] = "RSH_EQ";
        names[STRING] = "STRING";
        names[SUB_EQ] = "SUB_EQ";
        names[WHITESPACE] = "WHITESPACE";
        names[XOR_EQ] = "XOR_EQ";
        names[M_ARG] = "M_ARG";
        names[M_PASTE] = "M_PASTE";
        names[M_STRING] = "M_STRING";
        names[P_LINE] = "P_LINE";
        names[P_IF] = "P_IF";
        names[P_ELIF] = "P_ELIF";
        names[P_ENDIF] = "P_ENDIF";
        names[P_FEATUREEXPR] = "P_FEATUREEXPR";
        names[INVALID] = "INVALID";
    }

    public abstract void setNoFurtherExpansion();

    public abstract boolean mayExpand();

    public abstract Source getSource();

    public abstract String getSourceName();

    public abstract void setSourceName(String src);

    /**
     * "Lazily print" this token, i.e. print it without constructing a full in-memory representation. This is just a
     * default implementation, override it for tokens with a potentially huge string representation.
     *
     * @param writer The {@link java.io.PrintWriter} to print onto.
     */
    public void lazyPrint(PrintWriter writer) {
        writer.append(getText());
    }

    public boolean isWhite() {
        int type = getType();
        return (type == WHITESPACE) || (type == CCOMMENT)
                || (type == CPPCOMMENT);
    }

    public abstract Token clone();
}
