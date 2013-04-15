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

    public PreprocessorListener(VALexer pp) {
        clear();
        this.pp = pp;
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
        Preprocessor.logger.log(level, msg);
        System.err.println(msg);
    }

    /**
     * Handles a warning.
     * <p/>
     * The behaviour of this method is defined by the implementation. It may
     * simply record the error message, or it may throw an exception.
     */
    public void handleWarning(Source source, int line, int column, String msg)
            throws LexerException {
        warnings++;
        print((source == null ? "" : source.getName()) + ":" + line + ":" + column + ": warning: "
                + msg, Level.WARNING);
    }

    /**
     * Handles an error.
     * <p/>
     * The behaviour of this method is defined by the implementation. It may
     * simply record the error message, or it may throw an exception.
     *
     * @param featureExpr
     */
    public void handleError(Source source, int line, int column, String msg,
                            FeatureExpr featureExpr) throws LexerException {
        errors++;
        print(source.getName() + ":" + line + ":" + column + ": error: " + msg
                + "; condition: " + featureExpr, Level.SEVERE);
        pp.debugPreprocessorDone();
        throw new LexerException(msg);
    }

    public void handleSourceChange(Source source, String event) {
    }

}
