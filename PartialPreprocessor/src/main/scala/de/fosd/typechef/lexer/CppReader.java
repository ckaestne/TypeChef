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

import de.fosd.typechef.lexer.macrotable.MacroFilter;

import java.io.IOException;
import java.io.Reader;

import static de.fosd.typechef.lexer.Token.*;

/**
 * A Reader wrapper around the Preprocessor.
 * <p/>
 * This is a utility class to provide a transparent {Reader} which
 * preprocesses the input text.
 *
 * @see Preprocessor
 * @see Reader
 */
public class CppReader extends Reader {

    private Preprocessor cpp;
    private String token;
    private int idx;

    public CppReader(final Reader r) {
        cpp = new Preprocessor(new MacroFilter(), new LexerSource(r, true) {
            @Override
            public String getName() {
                return "<CppReader Input@" + System.identityHashCode(r) + ">";
            }
        }, null);
        token = "";
        idx = 0;
    }

    public CppReader(Preprocessor p) {
        cpp = p;
        token = "";
        idx = 0;
    }

    /**
     * Returns the Preprocessor used by this CppReader.
     */
    public Preprocessor getPreprocessor() {
        return cpp;
    }

    /**
     * Defines the given name as a macro.
     * <p/>
     * This is a convnience method.
     */
    public void addMacro(String name) throws LexerException {
        cpp.addMacro(name, FeatureExprLib.True());
    }

    /**
     * Defines the given name as a macro.
     * <p/>
     * This is a convnience method.
     */
    public void addMacro(String name, String value) throws LexerException {
        cpp.addMacro(name, FeatureExprLib.True(), value);
    }

    private boolean refill() throws IOException {
        try {
            assert cpp != null : "cpp is null : was it closed?";
            if (token == null)
                return false;
            while (idx >= token.length()) {
                Token tok = cpp.getNextToken();
                switch (tok.getType()) {
                    case EOF:
                        token = null;
                        return false;
                    case CCOMMENT:
                    case CPPCOMMENT:
                        if (!cpp.getFeature(Feature.KEEPCOMMENTS)) {
                            token = " ";
                            break;
                        }
                    default:
                        token = tok.getText();
                        break;
                }
                idx = 0;
            }
            return true;
        } catch (LexerException e) {
            /*
                * Never happens. if (e.getCause() instanceof IOException) throw
                * (IOException)e.getCause();
                */
            IOException ie = new IOException(String.valueOf(e));
            ie.initCause(e);
            throw ie;
        }
    }

    public int read() throws IOException {
        if (!refill())
            return -1;
        return token.charAt(idx++);
    }

    /* XXX Very slow and inefficient. */
    public int read(char cbuf[], int off, int len) throws IOException {
        if (token == null)
            return -1;
        for (int i = 0; i < len; i++) {
            int ch = read();
            if (ch == -1)
                return i;
            cbuf[off + i] = (char) ch;
        }
        return len;
    }

    public void close() throws IOException {
        if (cpp != null) {
            cpp.close();
            cpp = null;
        }
        token = null;
    }

}
