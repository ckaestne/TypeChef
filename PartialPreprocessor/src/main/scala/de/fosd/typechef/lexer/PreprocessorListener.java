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

import de.fosd.typechef.VALexer;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import de.fosd.typechef.lexer.options.ILexerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * A handler for preprocessor events, primarily errors and warnings.
 * <p/>
 * If no PreprocessorListener is installed in a Preprocessor, all error and
 * warning events will throw an exception. Installing a listener allows more
 * intelligent handling of these events.
 */
public class PreprocessorListener {

    private int errors;
    private int warnings;
    private VALexer pp;
    private ILexerOptions options;

    private FeatureExpr invalidConfigurations = FeatureExprFactory.False();

    public PreprocessorListener(VALexer pp, ILexerOptions options) {
        clear();
        this.pp = pp;
        this.options = options;
    }

    public void clear() {
        errors = 0;
        warnings = 0;
    }

    public int getErrors() {
        return errors;
    }

    public int getWarnings() {
        return warnings;
    }

    protected void print(String msg, Level level) {
        if (options.printLexerErrorsToStdErr()) {
            Preprocessor.logger.log(level, msg);
            System.err.println(msg);
        }
    }

    /**
     * Handles a warning.
     * <p/>
     * The behaviour of this method is defined by the implementation. It may
     * simply record the error message, or it may throw an exception.
     */
    public void handleWarning(String source, int line, int column, String msg,
                              FeatureExpr featureExpr) throws LexerException {
        if (featureExpr != null && !featureExpr.isSatisfiable(pp.getFeatureModel()))
            return;

        if (options.isHandleWarningsAsErrors())
            handleError(source, line, column, msg, featureExpr);
        else {
            warnings++;
            print((source == null ? "" : source) + ":" + line + ":" + column + ": warning: "
                    + msg, Level.WARNING);
        }
    }

    /**
     * Handles an error.
     * <p/>
     * The behaviour of this method is defined by the implementation. It may
     * simply record the error message, or it may throw an exception.
     *
     * @param featureExpr
     */
    public void handleError(String source, int line, int column, String msg,
                            FeatureExpr featureExpr) throws LexerException {
        //do not report error if the error cannot occur in valid configurations
        //that is if (FM => !fexpr) is a tautology
        if (featureExpr != null && !featureExpr.isSatisfiable(pp.getFeatureModel()))
            return;

        errors++;
        print((source == null ? "" : source) + ":" + line + ":" + column + ": error: " + msg
                + "; condition: " + featureExpr, Level.SEVERE);
        pp.debugPreprocessorDone();

        errorList.add(new Pair<FeatureExpr, LexerFrontend.LexerError>(featureExpr, new LexerFrontend.LexerError(msg, source, line, column)));
        invalidConfigurations = invalidConfigurations.or(featureExpr);
        if (invalidConfigurations.isTautology(pp.getFeatureModel()))
            throw new LexerException("Lexer exception in all configurations. Quitting.");
    }

    public void handleSourceChange(Source source, String event) {
    }


    static class Pair<A, B> {
        final B _2;
        final A _1;

        public Pair(A _1, B _2) {
            this._1 = _1;
            this._2 = _2;
        }
    }

    /**
     * ordered list of errors occurring in the lexer and their conditions
     */
    private final List<Pair<FeatureExpr, LexerFrontend.LexerError>> errorList = new ArrayList<Pair<FeatureExpr, LexerFrontend.LexerError>>();

    public FeatureExpr getInvalidConfigurations() {
        return invalidConfigurations;
    }

    List<Pair<FeatureExpr, LexerFrontend.LexerError>> getLexerErrorList() {
        return errorList;
    }

}
