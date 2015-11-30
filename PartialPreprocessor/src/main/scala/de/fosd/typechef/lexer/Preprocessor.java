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
import de.fosd.typechef.featureexpr.FeatureExprTree;
import de.fosd.typechef.featureexpr.FeatureModel;
import de.fosd.typechef.lexer.MacroConstraint.MacroConstraintKind;
import de.fosd.typechef.lexer.macrotable.MacroContext;
import de.fosd.typechef.lexer.macrotable.MacroExpansion;
import de.fosd.typechef.lexer.macrotable.MacroFilter;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import static de.fosd.typechef.lexer.Token.*;


/**
 * modified C preprocessor with the following changes
 *
 * * ifdef never hides code (except for include guards
 *   which are recognized with some mechanism)
 *
 * * a history of all defines is remembered and associated
 *   with features. multiple defines for the same macro
 *   are possible.
 *
 * * when applying a macro all alternative expansions are considered
 *
 */

/**
 * when to expand macros:
 * *	always expand macros in pure text, do not reexpand expanded tokens
 * * 	expand macros after #include
 * * 	expand macros in expression of #if and #elif, but not in defined(X)
 * * 	no expansion in other # directives, never expand the identifier after #
 *
 */

/**
 * A C Preprocessor. The Preprocessor outputs a token stream which does not need
 * re-lexing for C or C++. Alternatively, the output text may be reconstructed
 * by concatenating the {@link Token#getText() text} values of the returned
 * {@link Token Tokens}. (See {@link CppReader}, which does this.)
 * XXX: the above is now incorrect, because getText is not a valid operation on tokens which would produce huge strings.
 */

/*
 * Source file name and line number information is conveyed by lines of the form
 *
 * # linenum filename flags
 *
 * These are called linemarkers. They are inserted as needed into the output
 * (but never within a string or character constant). They mean that the
 * following line originated in file filename at line linenum. filename will
 * never contain any non-printing characters; they are replaced with octal
 * escape sequences.
 *
 * After the file name comes zero or more flags, which are `1', `2', `3', or
 * `4'. If there are multiple flags, spaces separate them. Here is what the
 * flags mean:
 *
 * `1' This indicates the start of a new file. `2' This indicates returning to a
 * file (after having included another file). `3' This indicates that the
 * following text comes from a system header file, so certain warnings should be
 * suppressed. `4' This indicates that the following text should be treated as
 * being wrapped in an implicit extern "C" block.
 */

public class Preprocessor extends DebuggingPreprocessor implements Closeable, VALexer {

    @SuppressWarnings("serial")
    private class ParseParamException extends Exception {

        private Token tok;
        private String errorMsg;

        public ParseParamException(Token tok, String string) {
            this.tok = tok;
            this.errorMsg = string;
        }

    }

    private static final Source INTERNAL = new Source() {
        @Override
        public Token token() throws IOException, LexerException {
            throw new LexerException("Cannot read from " + getName());
        }

        @Override
        public String getPath() {
            return "<internal-data>";
        }

        @Override
        public String getName() {
            return "internal data";
        }

        @Override
        String debug_getContent() {
            return "INTERNAL";
        }
    };

    SourceManager sourceManager = new SourceManager(this);

    private final FeatureModel featureModel;
    /* The fundamental engine. */
    private MacroContext<MacroData> macros;
    State state;

    protected MacroContext<MacroData> getMacros() {
        return macros;
    }

    /* Miscellaneous support. */
    private int counter;

    /* Support junk to make it work like cpp */
    private List<String> quoteincludepath; /* -iquote */
    private List<String> sysincludepath; /* -I */
    private List<String> frameworkspath;
    private Set<Feature> features;
    private Set<Warning> warnings;
    private VirtualFileSystem filesystem;
    PreprocessorListener listener;

    private List<MacroConstraint> macroConstraints = new ArrayList<MacroConstraint>();
    private Map<VirtualFile, FeatureExpr> includedFileMap;

    public Preprocessor(MacroFilter macroFilter, FeatureModel fm) {
        this.featureModel = fm;
        macros = new MacroContext<MacroData>(featureModel, macroFilter);
        for (String name : new String[]{
                "__LINE__", "__FILE__", "__BASE_FILE__", "__COUNTER__", "__TIME__", "__DATE__"
        }) {
            macros = macros.define(name, FeatureExprLib.True(), new MacroData(INTERNAL));
        }

        state = new State();

        this.counter = 0;

        this.quoteincludepath = new ArrayList<String>();
        this.sysincludepath = new ArrayList<String>();
        this.frameworkspath = new ArrayList<String>();
        this.features = EnumSet.noneOf(Feature.class);
        this.warnings = EnumSet.noneOf(Warning.class);
        this.filesystem = new JavaFileSystem();
        this.listener = null;
        this.includedFileMap = new HashMap<VirtualFile, FeatureExpr>();
    }

    public Preprocessor() {
        this(new MacroFilter(), null);
    }

    public Preprocessor(MacroFilter macroFilter, Source initial, FeatureModel fm) {
        this(macroFilter, fm);
        addInput(initial);
    }

    /**
     * Equivalent to 'new Preprocessor(new {@link FileLexerSource}(file))'
     */
    public Preprocessor(MacroFilter macroFilter, File file, FeatureModel fm) throws IOException {
        this(macroFilter, new FileLexerSource(file), fm);
    }

    /**
     * Sets the VirtualFileSystem used by this Preprocessor.
     */
    public void setFileSystem(VirtualFileSystem filesystem) {
        this.filesystem = filesystem;
    }

    /**
     * Returns the VirtualFileSystem used by this Preprocessor.
     */
    public VirtualFileSystem getFileSystem() {
        return filesystem;
    }

    /**
     * Sets the PreprocessorListener which handles events for this Preprocessor.
     * <p/>
     * The listener is notified of warnings, errors and source changes, amongst
     * other things.
     */
    public void setListener(PreprocessorListener listener) {
        this.listener = listener;
        sourceManager.reinit();
    }

    @Override
    public FeatureModel getFeatureModel() {
        return featureModel;
    }

    /**
     * Returns the PreprocessorListener which handles events for this
     * Preprocessor.
     */
    public PreprocessorListener getListener() {
        return listener;
    }

    /**
     * Returns the feature-set for this Preprocessor.
     * <p/>
     * This set may be freely modified by user code.
     */
    public Set<Feature> getFeatures() {
        return features;
    }


    /**
     * Adds a feature to the feature-set of this Preprocessor.
     */
    public void addFeature(Feature f) {
        features.add(f);
    }

    /**
     * Adds features to the feature-set of this Preprocessor.
     */
    public void addFeatures(Collection<Feature> f) {
        features.addAll(f);
    }

    /**
     * Returns true if the given feature is in the feature-set of this
     * Preprocessor.
     */
    public boolean getFeature(Feature f) {
        return features.contains(f);
    }

    /**
     * Returns the warning-set for this Preprocessor.
     * <p/>
     * This set may be freely modified by user code.
     */
    public Set<Warning> getWarnings() {
        return warnings;
    }

    /**
     * Adds a warning to the warning-set of this Preprocessor.
     */
    public void addWarning(Warning w) {
        warnings.add(w);
    }

    /**
     * Adds warnings to the warning-set of this Preprocessor.
     */
    public void addWarnings(Collection<Warning> w) {
        warnings.addAll(w);
    }

    /**
     * Returns true if the given warning is in the warning-set of this
     * Preprocessor.
     */
    public boolean getWarning(Warning w) {
        return warnings.contains(w);
    }

    /**
     * Adds input for the Preprocessor.
     * <p/>
     * Inputs are processed in the order in which they are added.
     */
    public void addInput(Source source) {
        source.init(this);
        sourceManager.addInput(source);
    }

    /**
     * Adds input for the Preprocessor.
     *
     * @see #addInput(Source)
     */
    public void addInput(File file) throws IOException {
        addInput(new FileLexerSource(file));
    }

    protected void error(int line, int column, String msg)
            throws LexerException {
        error(line, column, msg, null);
    }
    /**
     * Handles an error.
     * <p/>
     * If a PreprocessorListener is installed, it receives the error. Otherwise,
     * an exception is thrown.
     */
    protected void error(int line, int column, String msg, FeatureExpr pc)
            throws LexerException {

        if (pc == null)
            pc = state.getFullPresenceCondition();
        if (listener != null)
            listener.handleError(sourceManager.getSource().getName(), line, column, msg,
                    pc);
        else
            throw new LexerException("Error at " + line + ":" + column + ": "
                    + msg, pc);
    }

    /**
     * Handles an error.
     * <p/>
     * If a PreprocessorListener is installed, it receives the error. Otherwise,
     * an exception is thrown.
     *
     * @see #error(int, int, String)
     */
    protected void error(Token tok, String msg) throws LexerException {
        error(tok.getLine(), tok.getColumn(), msg);
    }

    /**
     * Handles a warning.
     * <p/>
     * If a PreprocessorListener is installed, it receives the warning.
     * Otherwise, an exception is thrown.
     */
    protected void warning(int line, int column, String msg, FeatureExpr fexpr)
            throws LexerException {
        if (fexpr == null)
            fexpr = FeatureExprLib.True();
        fexpr = fexpr.and(state.getFullPresenceCondition());
        if (warnings.contains(Warning.ERROR))
            error(line, column, msg);
        else if (listener != null)
            listener
                    .handleWarning(sourceManager.getSource().getName(), line, column, msg, fexpr);
        else
            throw new LexerException("Warning at " + line + ":" + column + ": "
                    + msg, fexpr);
    }

    /**
     * Handles a warning.
     * <p/>
     * If a PreprocessorListener is installed, it receives the warning.
     * Otherwise, an exception is thrown.
     *
     * @see #warning(int, int, String)
     */
    protected void warning(Token tok, String msg) throws LexerException {
        warning(tok, msg, null);
    }

    protected void warning(Token tok, String msg, FeatureExpr fexpr) throws LexerException {
        warning(tok.getLine(), tok.getColumn(), msg, fexpr);
    }

    /**
     * Adds a Macro to this Preprocessor.
     * <p/>
     * The given {@link MacroData} object encapsulates both the name and the
     * expansion.
     * <p/>
     * There can be multiple macros. They can have different features. Newer
     * macros replace older macros if they cover the same features.
     *
     * @param feature
     * @param name
     */
    public void addMacro(String name, FeatureExpr feature, MacroData m)
            throws LexerException {
        // System.out.println("Macro " + m);
        /* Already handled as a source error in macro(). */
        if ("defined".equals(name))
            throw new LexerException("Cannot redefine name 'defined'", state
                    .getFullPresenceCondition());
        if (this.getSource() != null)
            logAddMacro(name, feature, m, this.getSource());
        macros = macros.define(name, feature, m);
    }


    public void removeMacro(String name, FeatureExpr feature) {
        macros = macros.undefine(name, feature);
    }

    /**
     * Defines the given name as a macro.
     * <p/>
     * The String value is lexed into a token stream, which is used as the macro
     * expansion.
     */
    public void addMacro(String name, FeatureExpr feature, String value)
            throws LexerException {
        try {
            MacroData m = new MacroData(null);
            StringLexerSource s = new StringLexerSource(value);
            for (; ; ) {
                Token tok = s.token();
                if (tok.getType() == EOF)
                    break;
                m.addToken(tok);
            }
            addMacro(name, feature, m);
        } catch (IOException e) {
            throw new LexerException(e);
        }
    }

    /**
     * Defines the given name as a macro, with the value <code>1</code>.
     * <p/>
     * This is a convenience method, and is equivalent to
     * <code>addMacro(name, "1")</code>.
     */
    public void addMacro(String name, FeatureExpr feature)
            throws LexerException {
        addMacro(name, feature, "1");
    }


    /**
     * Sets the user include path used by this Preprocessor.
     */
    /* Note for future: Create an IncludeHandler? */
    public void setQuoteIncludePath(List<String> path) {
        this.quoteincludepath = path;
    }

    /**
     * Returns the user include-path of this Preprocessor.
     * <p/>
     * This list may be freely modified by user code.
     */
    public List<String> getQuoteIncludePath() {
        return quoteincludepath;
    }

    /**
     * Sets the system include path used by this Preprocessor.
     */
    /* Note for future: Create an IncludeHandler? */
    public void setSystemIncludePath(List<String> path) {
        this.sysincludepath = path;
    }

    /**
     * Returns the system include-path of this Preprocessor.
     * <p/>
     * This list may be freely modified by user code.
     */
    public List<String> getSystemIncludePath() {
        return sysincludepath;
    }

    /**
     * Sets the Objective-C frameworks path used by this Preprocessor.
     */
    /* Note for future: Create an IncludeHandler? */
    public void setFrameworksPath(List<String> path) {
        this.frameworkspath = path;
    }

    /**
     * Returns the Objective-C frameworks path used by this Preprocessor.
     * <p/>
     * This list may be freely modified by user code.
     */
    public List<String> getFrameworksPath() {
        return frameworkspath;
    }

    /* States */

    private void push_state() {
        state = new State(state);
    }

    private void pop_state() throws LexerException {
        if (state.parent == null)
            error(0, 0, "#" + "endif without #" + "if");
        else
            state = state.parent;
    }

    /**
     * only returns false if a code fragment is certainly False, i.e., there is
     * no variant in which it is included.
     * <p/>
     * this can happen when a feature is explicitly undefined or explicitly
     * defined in the source code
     *
     * @return
     */
    private boolean isActive() {
        return state.isActive(featureModel);
    }

    private boolean isParentActive() {
        return state.parent == null || state.parent.isActive(featureModel);
    }

    /* Source tokens */

    private Token source_token;

    private IfdefPrinter ifdefPrinter = new IfdefPrinter();

    class IfdefPrinter {
        private class IfdefBlock {
            public IfdefBlock(boolean visible) {
                this.visible = visible;
            }

            boolean visible = true;
        }

        private Stack<IfdefBlock> stack = new Stack<IfdefBlock>();

        public Token startIf(Token tok, boolean parentActive, State state) {
            FeatureExpr localCondition = state.getLocalFeatureExpr();
            boolean visible = isIfVisible(parentActive, state);

            stack.push(new IfdefBlock(visible));
            if (visible)
                return OutputHelper.if_token(tok.getLine(), localCondition);
            else
                return OutputHelper.emptyLine(tok.getLine(), tok.getColumn());

        }

        // skip output of ifdef 0 and ifdef 1
        private boolean isIfVisible(boolean parentActive, State state) {
            FeatureExpr expr = state.getLocalFeatureExpr();
            FeatureExpr fullPresenceCondition = state.getFullPresenceCondition();
            FeatureExpr parentPc = state.parent.getFullPresenceCondition();
            boolean visible = parentActive;
            if (visible &&
                    (expr.isContradiction(featureModel) ||
                            expr.isTautology(featureModel) ||
                            fullPresenceCondition.isContradiction(featureModel) ||
                            fullPresenceCondition.isTautology(featureModel) ||
                            parentPc.implies(expr).isTautology(featureModel) ||
                            parentPc.implies(expr.not()).isTautology(featureModel)))
                visible = false;
            return visible;
        }

        public Token endIf(Token tok) {
            if (stack.pop().visible)
                return OutputHelper.endif_token(tok.getLine());
            else
                return OutputHelper.emptyLine(tok.getLine(), tok.getColumn());
        }

        public Token startElIf(Token tok, boolean parentActive,
                               State state) {
            FeatureExpr localCondition = state.getLocalFeatureExpr();

            boolean wasVisible = stack.pop().visible;
            boolean isVisible = isIfVisible(parentActive, state);
            stack.push(new IfdefBlock(isVisible));

            return OutputHelper.elif_token(tok.getLine(), localCondition,
                    wasVisible, isVisible);
        }
    }

    static class OutputHelper {
        private OutputHelper() {
        }

        /*
           * XXX Make this include the NL, and make all cpp directives eat their
           * own NL.
           */
        static Token line_token(int line, String name, String extra) {
            StringBuilder buf = new StringBuilder();
            buf.append("#line ").append(line).append(" \"");
            /* XXX This call to escape(name) is correct but ugly. */
            MacroTokenSource$.MODULE$.escape(buf, name);
            buf.append("\"").append(extra).append("\n");
            return new SimpleToken(P_LINE, line, 0, buf.toString(), null);
        }

        static List<Token> elif_tokenStr(FeatureExpr featureExpr) {
            return ifelif_tokenStr(featureExpr, true);
        }

        static List<Token> if_tokenStr(FeatureExpr featureExpr) {
            return ifelif_tokenStr(featureExpr, false);
        }

        private static List<Token> ifelif_tokenStr(FeatureExpr featureExpr,
                                                   boolean isElif) {
            // #elif featureexpr
            List<Token> result = new ArrayList<Token>(5);
            result.add(new SimpleToken(Token.HASH, "#", null));
            if (isElif)
                result.add(new SimpleToken(Token.IDENTIFIER, "elif", null));
            else
                result.add(new SimpleToken(Token.IDENTIFIER, "if", null));
            result.add(new SimpleToken(Token.WHITESPACE, " ", null));
            result.add(new FeatureExprToken(featureExpr, null));
            result.add(new SimpleToken(Token.NL, "\n", null));
            return result;
        }

        static List<Token> inlineIf_tokenStr(FeatureExpr featureExpr) {
            // "__IF__(" + feature.print() + ","
            List<Token> result = new ArrayList<Token>(5);
            result.add(new SimpleToken(Token.IDENTIFIER, "__IF__", null));
            result.add(new SimpleToken('(', null));
            result.add(new FeatureExprToken(featureExpr, null));
            result.add(comma());
            return result;

        }

        static Token comma() {
            return new SimpleToken(',', null);
        }

        static Token closing_bracket() {
            return new SimpleToken(')', null);
        }

        static Token zero() {
            return new SimpleToken(Token.INTEGER, "0", Long.valueOf(0), null);
        }

        static List<Token> else_tokenStr() {
            List<Token> result = new ArrayList<Token>(5);
            result.add(new SimpleToken(Token.HASH, "#", null));
            result.add(new SimpleToken(Token.IDENTIFIER, "else", null));
            result.add(new SimpleToken(Token.NL, "\n", null));
            return result;
        }

        static List<Token> endif_tokenStr() {
            // return "#endif\n";
            List<Token> result = new ArrayList<Token>(5);
            result.add(new SimpleToken(Token.HASH, "#", null));
            result.add(new SimpleToken(Token.IDENTIFIER, "endif", null));
            result.add(new SimpleToken(Token.NL, "\n", null));
            return result;
        }

        static TokenSequenceToken if_token(int line, FeatureExpr featureExpr) {
            return new TokenSequenceToken(P_IF, line, 0,
                    if_tokenStr(featureExpr), null);
        }

        static TokenSequenceToken elif_token(int line, FeatureExpr featureExpr,
                                             boolean printEnd, boolean printIf) {

            List<Token> result = new ArrayList<Token>(5);
            if (printEnd) {
                result.add(new SimpleToken(Token.HASH, "#", null));
                result.add(new SimpleToken(Token.IDENTIFIER, "endif", null));
                result.add(new SimpleToken(Token.NL, "\n", null));
            }

            if (printIf)
                result.addAll(if_tokenStr(featureExpr));

            if (result.isEmpty())
                result.add(new SimpleToken(Token.WHITESPACE, " ", null));

            return new TokenSequenceToken(P_ELIF, line, 0, result, null);
        }

        static SimpleToken endif_token(int line) {
            return new SimpleToken(P_ENDIF, line, 0, "#endif\n", null);
        }

        public static SimpleToken emptyLine(int line, int column) {
            return new SimpleToken(NL, line, column, "\n", null);
        }

    }

    private void source_untoken(Token tok) {
        if (this.source_token != null)
            throw new IllegalStateException("Cannot return two tokens");
        this.source_token = tok;
    }

    private Token source_token_nonwhite() throws IOException, LexerException {
        Token tok;
        do {
            tok = retrieveTokenFromSource();
        } while (tok.isWhite());
        return tok;
    }

    /**
     * Returns an NL or an EOF token.
     * <p/>
     * The metadata on the token will be correct, which is better than
     * generating a new one.
     * <p/>
     * This method can, as of recent patches, return a P_LINE token.
     */
    private Token source_skipline(boolean white) throws IOException,
            LexerException {
        // (new Exception("skipping line")).printStackTrace(System.out);
        Source s = getSource();
        Token tok = s.skipline(white);
        /* XXX Refactor with source_token() */
        if (tok.getType() == EOF && s.isAutopop()) {
            // System.out.println("Autopop " + s);
            sourceManager.pop_source();
            Source t = getSource();
            if (getFeature(Feature.LINEMARKERS) && s.isNumbered() && t != null) {
                /*
                     * We actually want 'did the nested source contain a newline
                     * token', which isNumbered() approximates. This is not perfect,
                     * but works.
                     */
                return OutputHelper.line_token(t.getLine() + 1, t.getName(),
                        " 2");
            }
        }
        return tok;
    }

    /**
     * processes and expands a macro. Called when parsing an identifier (and
     * when in visible code that may expand)
     *
     * @param inlineCppExpression if false alternatives are replaced by #ifdef-#elif statements
     *                            in different lines. if true, alternatives are replaced by
     *                            expressions inside a line in the form
     *                            "__if__(FeatureExpr,thenClause,elseClause)". such __if__
     *                            statements can be nested
     * @return whether something was expanded or not
     */
    private boolean macro_expandToken(String macroName,
                                      MacroExpansion<MacroData>[] macroExpansions, Token origInvokeTok,
                                      boolean inlineCppExpression) throws IOException, LexerException {
        //originalTokens.add(origInvokeTok);
        List<Argument> args;
        assert macroExpansions.length > 0;

        // check compatible macros. We allow a object-like macro to exist together with function-like macros, and check
        // arity of all function-like definitions. Non-variadic function-like definitions are currently restricted to
        // have the same arity; otherwise, each macro invocation would be required to use conditional compilation.

        // Look for the first non-object-like macro, non-variadic if possible, to get the arity.
        MacroData firstMacro = null;

        boolean hasFunctionLikeDefs = false;
        for (int i = 0; i < macroExpansions.length; i++) {
            MacroData macro = macroExpansions[i].getExpansion();
            if (macro.isFunctionLike() &&
                    (!hasFunctionLikeDefs || firstMacro.isVariadic() && !macro.isVariadic())) {
                firstMacro = macro;
                hasFunctionLikeDefs = true;
            }
        }

        if (!hasFunctionLikeDefs)
            //Only if no function-like defs are available, we choose an object-like def as firstMacro.
            firstMacro = macroExpansions[0].getExpansion();

        int arity = firstMacro.getArgCount();
        boolean areAllVariadic = firstMacro.isVariadic();

        for (int i = 1; i < macroExpansions.length; i++) {
            MacroData macro = macroExpansions[i].getExpansion();
            if (macro.isFunctionLike() && !areAllVariadic) {
                if (!macro.isVariadic() && macro.getArgCount() != arity) {
                    error(origInvokeTok,
                            "Alternative non-variadic macros with different arities are not yet supported: "
                                    + macro + " vs. "
                                    + firstMacro);
                }
                if (macro.isVariadic() && macro.getArgCount() > arity + 1) {
                    //Rationale for the "+ 1" in the above arity check
                    //The last argument of a variadic macro can be omitted, but is counted by getArgCount()
                    error(origInvokeTok,
                            String.format("Alternative variadic expansion %s has %d arguments, more than the arity %d",
                                    macro, macro.getArgCount(), arity));
                }
            }
        }

        List<Token> origArgTokens = new ArrayList<Token>();
        // parse parameters
        try {
            args = parse_macroParameters(macroName, inlineCppExpression,
                    origArgTokens, firstMacro);
        } catch (ParseParamException e) {
            warning(e.tok, e.errorMsg);// ChK: skip errors for now
            return false;
        }
        // XXX: we should still expand non-function-like defs.
        if (hasFunctionLikeDefs && args == null) {
            return false;// cannot expand function-like macro here
        }


        // replace macro
        if (macroName.equals("__LINE__")) {
            // origInvokeTok is the original occurrence of __LINE__, which might come from a macro body; therefore, we need to
            // get the line number from the source.
            int lineNum = getSource().getLine();
            sourceManager.push_source(new FixedTokenSource(
                    new SimpleToken[]{new SimpleToken(INTEGER,
                            lineNum, getSource().getColumn(),
                            String.valueOf(lineNum),
                            Integer.valueOf(lineNum), null)}), true);
        } else if (macroName.equals("__DATE__")) {
            outputStringTokenSource(origInvokeTok, String.format(Locale.US, "\"%1$tb %1$2te %1$tY\"", Calendar.getInstance()));
        } else if (macroName.equals("__TIME__")) {
            outputStringTokenSource(origInvokeTok, String.format(Locale.US, "\"%1$tT\"", Calendar.getInstance()));
        } else if (macroName.equals("__FILE__") || macroName.equals("__BASE_FILE__")) {
            //BASE file refers to the last input, whereas _FILE refers to the current source
            Source source =
                    macroName.equals("__BASE_FILE__") ? getLastInput() : getSource();

            StringBuilder buf = new StringBuilder("\"");
            String name = source == null ? null : source.getName();
            if (name == null)
                name = "<no file>";
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                switch (c) {
                    case '\\':
                        buf.append("\\\\");
                        break;
                    case '"':
                        buf.append("\\\"");
                        break;
                    default:
                        buf.append(c);
                        break;
                }
            }
            buf.append("\"");
            String text = buf.toString();
            outputStringTokenSource(origInvokeTok, text);
        } else if (macroName.equals("__COUNTER__")) {
            /*
                * This could equivalently have been done by adding a special Macro
                * subclass which overrides getTokens().
                */
            int value = this.counter++;
            sourceManager.push_source(new FixedTokenSource(
                    new SimpleToken[]{new SimpleToken(INTEGER,
                            origInvokeTok.getLine(), origInvokeTok.getColumn(), String
                            .valueOf(value), Integer.valueOf(value),
                            null)}), true);
        } else {
            // check that every variant is covered, there is no case when this
            // macro
            // is not replaced
            // currentstate => (alt1 || alt2|| alt3)
            FeatureExpr commonCondition = getCommonCondition(macroExpansions);
            try {
                if (macroExpansions.length == 1 && isExaustive(commonCondition)) {
                    sourceManager.push_source(createMacroTokenSource(macroName,
                            args, macroExpansions[0], origInvokeTok, inlineCppExpression), true);

                    // expand all alternative macros
                } else {
                    if (inlineCppExpression)
                        macro_expandAlternativesInline(macroName, macroExpansions,
                                args, origInvokeTok, origArgTokens, commonCondition);
                    else
                        macro_expandAlternatives(macroName, macroExpansions, args,
                                origInvokeTok, origArgTokens, commonCondition);
                }
            } catch (ParseParamException e) {
                e.printStackTrace();
                warning(e.tok, e.errorMsg, null);
                return false;
            }
        }

        logger.info("expanding " + macroName // + " by "
                // + sourceManager.debug_sourceDelta(debug_origSource)
        );

        return true;
    }

    /**
     * @param text text to output. Must include embedded quotes.
     */
    private void outputStringTokenSource(Token positionToken, String text) {
        sourceManager.push_source(new FixedTokenSource(
                new SimpleToken[]{new SimpleToken(STRING, positionToken.getLine(),
                        positionToken.getColumn(), text, text, null)}), true);
    }

    // private MacroExpansion<MacroData>[] filterApplicableMacros(
    // MacroExpansion<MacroData>[] macroExpansions) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // private FeatureExpr getCombinedMacroCondition(
    // MacroExpansion<MacroData>[] macroExpansions) {
    // FeatureExpr commonCondition = state.getFullPresenceCondition().not();
    // for (int i = 0; i < macroExpansions.length; i++)
    // commonCondition = commonCondition.or(macroExpansions[i]
    // .getFeature());
    // return commonCondition;
    // }

    private List<Argument> parse_macroParameters(String macroName,
                                                 boolean inlineCppExpression, List<Token> originalTokens,
                                                 MacroData firstMacro) throws IOException, LexerException,
            ParseParamException {
        Token tok;
        List<Argument> args;
        // attempt to parse all alternative macros in parallel (when all have
        // the same parameters)
        if (firstMacro.isFunctionLike()) {
            OPEN:
            for (; ; ) {
                tok = retrieveTokenFromSource();
                originalTokens.add(tok);
                // System.out.println("pp: open: token is " + tok);
                switch (tok.getType()) {
                    case WHITESPACE:
                    case CCOMMENT:
                    case CPPCOMMENT:
                    case NL:
                        break; /* continue */
                    // ChK TODO whitespace will be removed in case a bracket is not
                    // found eventually
                    case '(':
                        break OPEN;
                    default:
                        //ChK: do not use source_untoken here, instead push back  originalTokens to the source
                        //push back swallowed whitespace from parsing parameters!
                        sourceManager.push_source(new FixedTokenSource(originalTokens), true);
//                        source_untoken(tok);
//                        originalTokens.remove(originalTokens.size() - 1);
                        return null;
                }
            }

            // tok = expanded_token_nonwhite();
            tok = source_token_nonwhite();
            //Note: tok might be a NL, and we would like to strip
            //that away. It makes a difference if the argument is stringified!
            originalTokens.add(tok);

            args = new ArrayList<Argument>();
            /*
                * We either have, or we should have args. This deals elegantly with
                * the case that we have one empty arg.
                */
            if (tok.getType() != ')' || firstMacro.getArgCount() > 0) {
                List<Token> argTokens = new ArrayList<Token>();
                int depth = 0;
                boolean space = false;

                ARGS:
                for (; ; ) {
                    // System.out.println("pp: arg: token is " + tok);
                    switch (tok.getType()) {
                        case EOF:
                            throw new ParseParamException(tok, "EOF in macro args");

                        case ',':
                            if (depth == 0) {
                                args.add(MacroArg.create(argTokens));
                                argTokens = new ArrayList<Token>();
                            } else {
                                argTokens.add(tok);
                            }
                            space = false;
                            break;
                        case ')':
                            if (depth == 0) {
                                args.add(MacroArg.create(argTokens));
                                break ARGS;
                            } else {
                                depth--;
                                argTokens.add(tok);
                            }
                            space = false;
                            break;
                        case '(':
                            depth++;
                            argTokens.add(tok);
                            space = false;
                            break;

                        case WHITESPACE:
                        case CCOMMENT:
                        case CPPCOMMENT:
                            /* Avoid duplicating spaces. */
                            space = true;
                            break;
                        case HASH:
                            // PG: TODO - I've seen #if in a macro argument,
                            // and this code sometimes _expands_ the if (which can
                            // be defined as a macro in the Linux kernel)!!!
                            // throw new
                            // LexerException("Unimplemented handling of # in macro args, important TODO!");
                        default:
                            /*
                                * Do not put space on the beginning of an argument
                                * token.
                                */
                            if (space && !argTokens.isEmpty())
                                argTokens.add(Token.space);
                            argTokens.add(tok);
                            space = false;
                            break;

                    }
                    // tok = expanded_token();
                    tok = retrieveTokenFromSource();
                    originalTokens.add(tok);
                }
                /*
                     * space may still be true here, thus trailing space is stripped
                     * from arguments.
                     */
            }
            /* Otherwise, nargs == 0 and we (correctly) got (); so leave the list
             * empty. Don't use Collections.emptyList() because that's
             * immutable. */

        } else {
            /* Macro without args. */
            args = null;
        }
        return args;
    }

    /**
     * Compares macro arity against the count of actual arguments. For variadic macros, a new argument list is produced with
     * the right argument count (i.e. the macro arity), by concatenating variadic arguments or producing an empty one.
     * Otherwise, the original list is returned.
     *
     * @return a list containing as many
     * @code {args} is unchanged.
     */
    private List<Argument> checkExpansionArity(String macroName, MacroExpansion<MacroData> macroExpansion, Token tok, List<Argument> args)
            throws LexerException, ParseParamException {
        MacroData expansion = macroExpansion.getExpansion();
        assert (!expansion.isFunctionLike() || args != null);

        if (expansion.isFunctionLike() && args.size() != expansion.getArgCount()) {
            if (expansion.isVariadic()) {
                // Try producing (and then returning) the fixed-up argument list.

                assert (expansion.getArgCount() > 0);

                if (args.size() == expansion.getArgCount() - 1
                        && getFeature(Feature.GNUCEXTENSIONS)) {
                    // This is a GCC extension:
                    // http://gcc.gnu.org/onlinedocs/cpp/Variadic-Macros.html
                    List<Argument> argsCopy = new ArrayList<Argument>(args.size() + 1);
                    argsCopy.addAll(args);
                    argsCopy.add(MacroArg.omittedVariadicArgument());
                    return argsCopy;
                } else if (args.size() > expansion.getArgCount()) {
                    //Remember that fromList accepts a [from, to[ range!
                    List<Argument> newArgs = new ArrayList<Argument>(args.subList(0, expansion.getArgCount() - 1));
                    List<Argument> argsToJoin = args.subList(expansion.getArgCount() - 1, args.size());
                    List<Token> joinedArgToks = new ArrayList<Token>();

                    int i = 0;
                    for (Argument arg : argsToJoin) {
                        joinedArgToks.addAll(arg.jTokens());
                        i++;
                        if (i < argsToJoin.size()) {
                            joinedArgToks.add(OutputHelper.comma());
                            joinedArgToks.add(Token.space);
                        }
                    }
                    newArgs.add(MacroArg.create(joinedArgToks));
                    return newArgs;
                }
            }
            // If the macro was not variadic, or if there was no way to make
            // arities match, complain. This warning can be harmless if we are
            // in a False branch.
            warning(tok, "macro " + macroName
                    + " has " + expansion.getArgCount()
                    + " parameters (variadic: " + expansion.isVariadic() + ") for expansion under condition "
                    + macroExpansion.getFeature() + " but given " + args.size()
                    + " args", macroExpansion.getFeature());
            return args;
        } else {
            return args;
        }
    }

    List<Source> macro_expandAlternative(String macroName, MacroExpansion<MacroData> macroExpansion, List<Argument> args,
                                         Token origInvokeTok, List<Token> origArgTokens, boolean inline)
            throws IOException, LexerException, ParseParamException {
        List<Source> resultList = new ArrayList<Source>();
        //push_state();
        //state.putLocalFeature(macroExpansion.getFeature(), macros);
        //XXX: here, the current status is not saved in the created macro token source. The caller will not understand the needed ifdef directives.
        //My God. So this change is useless. The produced #if directives are then not parsed by expanded_token, leading to breakage.
        resultList.add(createMacroTokenSource(macroName, args, macroExpansion, origInvokeTok, inline));
        //pop_state();
        if (!macroExpansion.getExpansion().isFunctionLike())
            resultList.add(new FixedTokenSource(origArgTokens));
        return resultList;
    }

    private void macro_expandAlternatives(String macroName,
                                          MacroExpansion<MacroData>[] macroExpansions, List<Argument> args,
                                          Token origInvokeTok, List<Token> origArgTokens, FeatureExpr commonCondition)
            throws IOException, LexerException, ParseParamException {
        boolean alternativesExaustive = isExaustive(commonCondition);

        List<Source> resultList = new ArrayList<Source>();
        for (int i = macroExpansions.length - 1; i >= 0; i--) {
            FeatureExpr feature = macroExpansions[i].getFeature();
            MacroData macroData = macroExpansions[i].getExpansion();

            if (i == macroExpansions.length - 1)
                resultList.add(new UnnumberedUnexpandingTokenStreamSource(
                        prependNL(OutputHelper.if_tokenStr(feature))));
            else
                resultList.add(new UnnumberedUnexpandingTokenStreamSource(
                        prependNL(OutputHelper.elif_tokenStr(feature))));
            resultList.addAll(macro_expandAlternative(macroName, macroExpansions[i], args, origInvokeTok, origArgTokens, false));
            if (i == 0 && !alternativesExaustive) {
                resultList.add(new UnnumberedUnexpandingTokenStreamSource(
                        prependNL(OutputHelper.else_tokenStr())));
                resultList.add(new FixedUnexpandingTokenSource(Collections.singletonList(origInvokeTok),
                        macroName));
                resultList.add(new FixedUnexpandingTokenSource(origArgTokens,
                        macroName));
            }
        }
        resultList.add(new UnnumberedUnexpandingTokenStreamSource(
                prependNL(OutputHelper.endif_tokenStr())));
        sourceManager.push_sources(resultList, true);
    }

    private List<Token> prependNL(List<Token> tokenList) {
        ArrayList<Token> result = new ArrayList<Token>(tokenList.size() + 1);
        result.add(new SimpleToken(Token.NL, "\n", null));
        result.addAll(tokenList);
        return result;
    }

    private MacroTokenSource createMacroTokenSource(String macroName,
                                                    List<Argument> args,
                                                    MacroExpansion<MacroData> macroExpansion,
                                                    Token origTok,
                                                    boolean inlineCppExpansion) throws ParseParamException, IOException, LexerException {
        List<Argument> argsFixed = checkExpansionArity(macroName, macroExpansion, origTok, args);
        MacroData macroData = macroExpansion.getExpansion();
        if (macroData.isFunctionLike())
            for (Argument arg : argsFixed)
                arg.expand(this, inlineCppExpansion, macroName);
        MacroTokenSource ret = new MacroTokenSource(macroName, macroData, argsFixed,
                getFeature(Feature.GNUCEXTENSIONS));
        return ret;
    }

    /**
     * uses __if__(FeatureExpr,a,b) to express alternative conditions
     * <p/>
     * __if__(exp3,macro3,__if__(exp2,macro2,__if__(exp1,macro1,originalTokens))
     * ) )
     * <p/>
     * order is irrelevant, because MacroContext makes sure that all
     * alternatives are mutually exclusive
     * <p/>
     * can be used even if just a single expansion is required. in case that the
     * presence condition of this expansion is not a tautology (exhaustive) it
     * is only conditionally expanded
     *
     * @param macroExpansions
     * @param args
     * @throws IOException
     * @throws LexerException
     */
    private void macro_expandAlternativesInline(final String macroName,
                                                MacroExpansion<MacroData>[] macroExpansions, List<Argument> args,
                                                Token origInvokeTok, List<Token> origArgTokens, FeatureExpr commonCondition)
            throws IOException, LexerException, ParseParamException {
        boolean alternativesExaustive = isExaustive(commonCondition);
        if (!alternativesExaustive)
            macroConstraints.add(new MacroConstraint(macroName,
                    MacroConstraintKind.NOTEXPANDING, commonCondition.not()));

        List<Source> resultList = new ArrayList<Source>();
        for (int i = macroExpansions.length - 1; i >= 0; i--) {
            FeatureExpr feature = macroExpansions[i].getFeature();
            MacroData macroData = macroExpansions[i].getExpansion();

            if (i > 0 || !alternativesExaustive)
                resultList.add(new UnnumberedUnexpandingTokenStreamSource(
                        OutputHelper.inlineIf_tokenStr(feature)));// "__IF__(feature,"
            resultList.addAll(macro_expandAlternative(macroName, macroExpansions[i], args, origInvokeTok, origArgTokens, true));
            if (i > 0 || !alternativesExaustive)
                resultList.add(new UnnumberedUnexpandingTokenStreamSource(
                        Collections.singletonList(OutputHelper.comma())));
        }

        if (!alternativesExaustive) {
            warning(origInvokeTok,
                    "inline expansion of macro " + macroName
                            + " is not exaustive. assuming 0 for "
                            + commonCondition.not(), commonCondition.not());
            resultList.add(new UnnumberedUnexpandingTokenStreamSource(
                    Collections.singletonList(OutputHelper.zero())));
        }
        List<Token> closingBrackets = new ArrayList<Token>();
        for (int i = macroExpansions.length - 2; i >= 0; i--)
            closingBrackets.add(OutputHelper.closing_bracket());
        if (!alternativesExaustive)
            closingBrackets.add(OutputHelper.closing_bracket());
        resultList.add(new UnnumberedUnexpandingTokenStreamSource(
                closingBrackets));
        sourceManager.push_sources(resultList, true);
    }

    private boolean isExaustive(FeatureExpr commonCondition) {
        //commonCondition is already an implication (see
        //getCommonCondition), therefore this is correct: it checks
        //whether the alternative expansions are correct in the context
        //of the current presence condition.
        return commonCondition.isTautology(featureModel);
    }

    private FeatureExpr getCommonCondition(MacroExpansion<MacroData>[] macroExpansions) {
        FeatureExpr commonCondition = macroExpansions[0].getFeature();
        for (int i = macroExpansions.length - 1; i >= 1; i--)
            commonCondition = commonCondition.or(macroExpansions[i]
                    .getFeature());
        commonCondition = state.getFullPresenceCondition().implies(commonCondition);
        return commonCondition;
    }

    /**
     * Expands an argument.
     *
     * @param inlineCppExpression
     * @param macroName
     */
    /* I'd rather this were done lazily, but doing so breaks spec. */
    /* pp */List<Token> macro_expandArgument(List<Token> arg,
                                             boolean inlineCppExpression, String macroName) throws IOException,
            LexerException {
        // return arg;//ChK lazytest
        List<Token> expansion = new ArrayList<Token>();
        boolean space = false;

        sourceManager.push_source(new FixedTokenSource(arg), false);

        EXPANSION:
        for (; ; ) {
            Token tok = expanded_token(inlineCppExpression, true);
            switch (tok.getType()) {
                case EOF:
                    break EXPANSION;

                case WHITESPACE:
                case CCOMMENT:
                case CPPCOMMENT:
                    space = true;
                    break;

                default:
                    if (space && !expansion.isEmpty())
                        expansion.add(Token.space);
                    expansion.add(tok);
                    space = false;
                    break;
            }
        }

        sourceManager.pop_source();

        return expansion;
    }

    /* processes a #define directive */
    private Token parse_define() throws IOException, LexerException {
        Token tok = source_token_nonwhite();
        if (tok.getType() != IDENTIFIER) {
            error(tok, "Expected identifier");
            return source_skipline(false);
        }
        /* if predefined */

        String name = tok.getText();
        if ("defined".equals(name)) {
            error(tok, "Cannot redefine name 'defined'");
            return source_skipline(false);
        }

        MacroData m = new MacroData(getSource());
        List<String> args;

        tok = retrieveTokenFromSource();
        if (tok.getType() == '(') {
            tok = source_token_nonwhite();
            boolean seenParamName = false;
            if (tok.getType() != ')') {
                args = new ArrayList<String>();
                ARGS:
                for (; ; ) {
                    switch (tok.getType()) {
                        case IDENTIFIER:
                            args.add(tok.getText());
                            seenParamName = true;
                            break;
                        case ELLIPSIS:
                            assert !seenParamName; // PG: let's check this.
                            if (!seenParamName) {
                                // This trick correctly implements the semantics of
                                // C99 variadic macros as described by the standard
                                // (6.3.10.1).
                                args.add("__VA_ARGS__");
                            }
                            tok = source_token_nonwhite();
                            if (tok.getType() != ')')
                                error(tok, "ellipsis must be on last argument");
                            m.setVariadic(true);
                            break ARGS;
                        case NL:
                        case EOF:
                            error(tok, "Unterminated macro parameter list");
                            return tok;
                        default:
                            error(tok, "error in macro parameters: "
                                    + getTextOrDefault(tok, "<Feature Expression>"));
                            return source_skipline(false);
                    }
                    tok = source_token_nonwhite();
                    switch (tok.getType()) {
                        case ',':
                            seenParamName = false;
                            break;
                        case ELLIPSIS:
                            assert seenParamName; // PG: verify if this is true
                            tok = source_token_nonwhite();
                            if (tok.getType() != ')')
                                error(tok, "ellipsis must be on last argument");
                            if (m.isVariadic()) {
                                error(tok, "ellipsis must not be repeated");
                            }
                            m.setVariadic(true);
                            break ARGS;
                        case ')':
                            break ARGS;

                        case NL:
                        case EOF:
                            /* Do not skip line. */
                            error(tok, "Unterminated macro parameters");
                            return tok;
                        default:
                            error(tok, "Bad token in macro parameters: "
                                    + getTextOrDefault(tok, "<Feature Expression>"));
                            return source_skipline(false);
                    }
                    tok = source_token_nonwhite();
                }
            } else {
                assert tok.getType() == ')' : "Expected ')'";
                args = Collections.emptyList();
            }

            m.setArgs(args);
        } else {
            /* For searching. */
            args = Collections.emptyList();
            source_untoken(tok);
        }

        tok = parse_macroBody(m, args);

        logger.info("#define " + name + " " + m);
        addMacro(name, state.getFullPresenceCondition(), m);

        return tok; /* NL or EOF. */
    }

    private Token parse_macroBody(MacroData m, List<String> args)
            throws IOException, LexerException {
        Token tok;
        /* Get an expansion for the macro, using indexOf. */
        boolean space = false;
        boolean paste = false;
        int idx;

        /* Ensure no space at start. */
        tok = source_token_nonwhite();
        EXPANSION:
        for (; ; ) {
            switch (tok.getType()) {
                case EOF:
                    break EXPANSION;
                case NL:
                    break EXPANSION;

                case CCOMMENT:
                case CPPCOMMENT:
                    /* XXX This is where we implement GNU's cpp -CC. */
                    // break;
                case WHITESPACE:
                    if (!paste)
                        space = true;
                    break;

                /* Paste. */
                case PASTE:
                    space = false;
                    paste = true;
                    try {
                        m.addPaste(new SimpleToken(M_PASTE, tok.getLine(), tok
                                .getColumn(), "#" + "#", null));
                    } catch (LexerException le) {
                        error(tok, le.getMessage());
                    }
                    break;

                /* Stringify. */
                case '#':
                    if (space)
                        m.addToken(Token.space);
                    space = false;
                    Token la = source_token_nonwhite();
                    if (la.getType() == IDENTIFIER
                            && ((idx = args.indexOf(la.getText())) != -1)) {
                        m.addToken(new SimpleToken(M_STRING, la.getLine(), la
                                .getColumn(), "#" + la.getText(), Integer
                                .valueOf(idx), null));
                    } else {
                        m.addToken(tok);
                        /* Allow for special processing. */
                        source_untoken(la);
                    }
                    break;

                case IDENTIFIER:
                    if (space)
                        m.addToken(Token.space);
                    space = false;
                    paste = false;
                    idx = args.indexOf(tok.getText());
                    if (idx == -1)
                        m.addToken(tok);
                    else
                        m.addToken(new SimpleToken(M_ARG, tok.getLine(), tok
                                .getColumn(), tok.getText(), Integer.valueOf(idx),
                                null));
                    break;

                default:
                    if (space)
                        m.addToken(Token.space);
                    space = false;
                    paste = false;
                    m.addToken(tok);
                    break;
            }
            tok = retrieveTokenFromSource();
        }
        return tok;
    }

    private Token parse_undef() throws IOException, LexerException {
        Token tok = source_token_nonwhite();
        if (tok.getType() != IDENTIFIER) {
            error(tok, "Expected identifier, not " + tok.getText());
            if (tok.getType() == NL || tok.getType() == EOF)
                return tok;
        } else {
            // Macro m = macros.get(tok.getText());
            // if (m != null) {
            /* XXX error if predefined */
            macros = macros.undefine(tok.getText(), state
                    .getFullPresenceCondition());
            // }
        }
        return source_skipline(true);
    }

    /**
     * Attempts to include the given file.
     * <p/>
     * User code may override this method to implement a virtual file system.
     */
    private boolean include(VirtualFile file) throws IOException,
            LexerException {
        // System.out.println("Try to include " + file);
        if (!file.isFile())
            return false;
        if (getFeature(Feature.DEBUG_VERBOSE))
            System.err.println("pp: including " + file);

		// include the file only if we have NOT already included the file under a condition
		// which contains also the current condition
        FeatureExpr condition = this.includedFileMap.get(file);
        FeatureExpr currentCondition = this.state.getFullPresenceCondition();

        if (condition == null) {
            sourceManager.push_source(file.getSource(), true);
            this.includedFileMap.put(file, currentCondition);
        } else if (currentCondition.andNot(condition).isSatisfiable()) {
            sourceManager.push_source(file.getSource(), true);
            this.includedFileMap.put(file, currentCondition.or(condition));
        }

        return true;
    }

    /**
     * Includes a file from an include path, by name.
     */
    private boolean include(Iterable<String> path, String name)
            throws IOException, LexerException {
        for (String dir : path) {
            VirtualFile file = filesystem.getFile(dir, name);
            if (include(file))
                return true;
        }
        return false;
    }

    /**
     * Handles an include directive.
     *
     * @param next
     */
    private void include(String parent, int line, String name, boolean quoted,
                         boolean next) throws IOException, LexerException {
        // The parent path can be null when using --include. Should then use the
        // current directory. XXX: but later we take the parent of this! Why
        // doesn't it break???
        if (parent == null)
            parent = ".";
        VirtualFile pfile = filesystem.getFile(parent);
        VirtualFile pdir = pfile.getParentFile();
        String parentDir = pdir.getPath();

        if (quoted && !next) {
            VirtualFile ifile = pdir.getChildFile(name);
            if (include(ifile))
                return;
            if (include(quoteincludepath, name))
                return;
        }

        List<String> path = getSystemIncludePath();
        if (next) {
            int idx = path.indexOf(parentDir);
            if (idx != -1)
                path = path.subList(idx + 1, path.size());
        }
        if (include(path, name))
            return;

        // Report error
        StringBuilder buf = new StringBuilder();
        buf.append("File not found: ").append(name);
        buf.append(" in");
        if (quoted) {
            buf.append(" .").append('(').append(pdir).append(')');
            for (String dir : quoteincludepath)
                buf.append(" ").append(dir);
        }
        for (String dir : getSystemIncludePath())
            buf.append(" ").append(dir);
        error(line, 0, buf.toString());
    }

    private Token parse_include(boolean next) throws IOException,
            LexerException {
        LexerSource lexerSource = (LexerSource) sourceManager.getSource();
        try {
            lexerSource.setInclude(true);
            processing_include = true;
            Token tok = getNextNonwhiteToken();

            String name;
            boolean quoted;
            StringBuilder buf = new StringBuilder();

            if (tok.getType() == STRING) {
                /*
                     * XXX Use the original text, not the value. Backslashes must
                     * not be treated as escapes here.
                     * PG: the above is no more needed, because the lexerSource does
                     * not handle backslashes as escapes when processing includes.
                     */
                buf.append((String) tok.getValue());
                HEADER:
                for (; ; ) {
                    tok = getNextToken();
                    switch (tok.getType()) {
                        case STRING:
                            //XXX: PG: That's forbidden by the standard (see 6.10.2 and 5.1.1.2)
                            buf.append((String) tok.getValue());
                            break;
                        case WHITESPACE:// ignore whitespace and comments for now
                            // ChK
                        case CPPCOMMENT:
                        case CCOMMENT:
                            break;
                        case NL:
                        case EOF:
                            break HEADER;
                        default:
                            warning(tok, "Unexpected token on #" + "include line", null);
                            return source_skipline(false);
                    }
                }
                quoted = true;
            } else if (tok.getType() == HEADER) {
                buf.append((String) tok.getValue());
                quoted = false;
                tok = source_skipline(true);
            } else if (tok.getType() == '<') {
                quoted = false;
                HEADER:
                for (; ; ) {
                    tok = getNextToken();
                    switch (tok.getType()) {
                        case '>':
                            break HEADER;
                        // XXX: PG: don't ignore WHITESPACE or CCOMMENT for now, I don't think they can be there;
                        // recheck!
                        case WHITESPACE:
                        case CCOMMENT:
                            warning(tok, "Unexpected token \"" + tok.getText() + "\" on #" + "include line");
                            return source_skipline(false);

                        case '.':
                        case '/':
                            buf.append((char) tok.getType());
                            break;
                        case INTEGER:
                        case IDENTIFIER:
                        default:
                            buf.append(getTextOrDefault(tok, "<Feature Expression>"));
                            break;
                    }
                }
            } else {
                error(tok, "Expected string or header, not " + getTextOrDefault(tok, "<Feature Expression>"));
                switch (tok.getType()) {
                    case NL:
                    case EOF:
                        return tok;
                    default:
                        /* Only if not a NL or EOF already. */
                        return source_skipline(false);
                }
            }

            name = buf.toString();
            processing_include = false;
            /* Do the inclusion. */
            include(lexerSource.getPath(), tok.getLine(), name,
                    quoted, next);

            /*
                * 'tok' is the 'nl' after the include. We use it after the #line
                * directive.
                */
            if (getFeature(Feature.LINEMARKERS))
                return OutputHelper.line_token(1, sourceManager.getSource()
                        .getName(), " 1");
            return tok;
        } finally {
            processing_include = false;
            lexerSource.setInclude(false);
        }
    }

    protected void pragma(Token nameTok, List<Token> value) throws IOException,
            LexerException {
        String pragmaName = nameTok.getText();
        if (!"pack".equals(pragmaName))
            warning(nameTok, "Unknown #" + "pragma: " + pragmaName);
    }

    private Token parse_pragma() throws IOException, LexerException {
        Token name;

        NAME:
        for (; ; ) {
            Token tok = getNextToken();
            switch (tok.getType()) {
                case EOF:
                    /*
                      * There ought to be a newline before EOF. At least, in any
                      * skipline context.
                      */
                    /* XXX Are we sure about this? */
                    warning(tok, "End of file in #" + "pragma");
                    return tok;
                case NL:
                    /* This may contain one or more newlines. */
                    warning(tok, "Empty #" + "pragma");
                    return tok;
                case CCOMMENT:
                case CPPCOMMENT:
                case WHITESPACE:
                    continue NAME;
                case IDENTIFIER:
                    name = tok;
                    break NAME;
                default:
                    return source_skipline(false);
            }
        }

        Token tok;
        List<Token> value = new ArrayList<Token>();
        VALUE:
        for (; ; ) {
            tok = getNextToken();
            switch (tok.getType()) {
                case EOF:
                    /*
                      * There ought to be a newline before EOF. At least, in any
                      * skipline context.
                      */
                    /* XXX Are we sure about this? */
                    warning(tok, "End of file in #" + "pragma");
                    break VALUE;
                case NL:
                    /* This may contain one or more newlines. */
                    break VALUE;
                case CCOMMENT:
                case CPPCOMMENT:
                    break;
                case WHITESPACE:
                    value.add(tok);
                    break;
                default:
                    value.add(tok);
                    break;
            }
        }

        pragma(name, value);

        return tok; /* The NL. */
    }

    // /* For #error and #warning. */
    // private void parse_cppError(Token pptok, boolean is_error)
    // throws IOException, LexerException {
    // StringBuilder buf = new StringBuilder();
    // buf.append('#').append(pptok.getText()).append(' ');
    // /* Peculiar construction to ditch first whitespace. */
    // Token tok = source_token_nonwhite();
    // ERROR: for (;;) {
    // switch (tok.getType()) {
    // case NL:
    // case EOF:
    // break ERROR;
    // default:
    // buf.append(tok.getText());
    // break;
    // }
    // tok = retrieveTokenFromSource();
    // }
    // buf.append("\nPresence condition: "
    // + state.getFullPresenceCondition().print());
    // if (is_error)
    // error(pptok, buf.toString());
    // else
    // warning(pptok, buf.toString());
    // } /* For #error and #warning. */
    //
    private Token parseErrorToken(Token pptok, boolean is_error)
            throws IOException, LexerException {
        StringBuilder buf = new StringBuilder();
        buf.append('#').append(pptok.getText()).append(' ');
        /* Peculiar construction to ditch first whitespace. */
        Token tok = source_token_nonwhite();
        ERROR:
        for (; ; ) {
            switch (tok.getType()) {
                case NL:
                case EOF:
                    buf.append('\n');
                    break ERROR;
                default:
                    buf.append(tok.getText());
                    break;
            }
            tok = retrieveTokenFromSource();
        }
        if (is_error)
            error(pptok, buf.toString());
        else
            warning(pptok, buf.toString());

        return new SimpleToken(P_LINE, pptok.getLine(), pptok.getColumn(), buf
                .toString(), null);
    }

    /**
     * hack to temporarily disable expansion inside defined() statements: (a)
     * every time a "defined" is found this counter is reset (b) every time a
     * token is found this counter is increased. (c) every time the counter is 1
     * and the token is "(" the counter is reset (d) if the counter is 1, the
     * argument is not expanded
     * <p/>
     * this way, we prevent to expand the argument after defined. To avoid that
     * the condition is triggered before a "defined" is found, set it to more
     * than 0 initially.
     */
    private int hack_definedCounter = 2;

    /*
      * This bypasses token() for #elif expressions. If we don't do this, then
      * isActive() == false causes token() to simply chew the entire input line.
      *
      * hack_definedActivated excludes the parameter of a defined statement from
      * expansion during pre-expansion of arguments
      *
      * TODO: this is completely broken for partial preprocessing. It only passes on #if directives, does not parse them.
      * Therefore, we do not correctly update the state, which in turn results in getApplicableMacroExpansions returning
      * too many expansions, including ones which might be invalid. It should probably just call parse_main again
      * (actually, I fear something more complicated is needed).
      */
    private Token expanded_token(boolean inlineCppExpression,
                                 boolean hack_definedActivated) throws IOException, LexerException {
        for (; ; ) {
            Token tok = retrieveTokenFromSource();
            // System.out.println("Source token is " + tok);
            if ("defined".equals(getTextOrDefault(tok, ""))
                    || (hack_definedCounter == 0 && "(".equals(getTextOrDefault(tok, ""))))
                hack_definedCounter = 0;
            else
                hack_definedCounter++;
            if (tok.getType() == IDENTIFIER
                    && (!hack_definedActivated || hack_definedCounter != 1)) {
                MacroExpansion<MacroData>[] m = macros.getApplicableMacroExpansions(tok
                        .getText(), state.getFullPresenceCondition());
                if (m.length > 0
                        && tok.mayExpand()
                        && sourceManager.getSource().mayExpand(tok.getText())
                        && macro_expandToken(tok.getText(), m, tok,
                        inlineCppExpression))
                    continue;
                return tok;
            }
            return tok;
        }
    }

    private Token expanded_token_nonwhite(boolean inlineCppExpression)
            throws IOException, LexerException {
        Token tok;
        do {
            tok = expanded_token(inlineCppExpression, false);
            // System.out.println("expanded token is " + tok);
        } while (tok.isWhite());
        return tok;
    }

    private Token expr_token = null;

    private boolean processing_include;

    //XXX:Rename as expr_token_get, and move in a small subclass.
    private Token expr_token(boolean inlineCppExpression) throws IOException,
            LexerException {
        Token tok = expr_token;

        if (tok != null) {
            // System.out.println("ungetting");
            expr_token = null;
        } else {
            tok = expanded_token_nonwhite(inlineCppExpression);
        }

        return tok;
    }

    //XXX:Rename as expr_token_unget
    private void expr_untoken(Token tok) throws LexerException {
        if (expr_token != null)
            throw new InternalException("Cannot unget two expression tokens.");
        expr_token = tok;
    }

    private int expr_priority(Token op) {
        switch (op.getType()) {
            case '/':
                return 11;
            case '%':
                return 11;
            case '*':
                return 11;
            case '+':
                return 10;
            case '-':
                return 10;
            case LSH:
                return 9;
            case RSH:
                return 9;
            case '<':
                return 8;
            case '>':
                return 8;
            case LE:
                return 8;
            case GE:
                return 8;
            case EQ:
                return 7;
            case NE:
                return 7;
            case '&':
                return 6;
            case '^':
                return 5;
            case '|':
                return 4;
            case LAND:
                return 3;
            case LOR:
                return 2;
            case '?':
                return 1;
            default:
                // System.out.println("Unrecognised operator " + op);
                return 0;
        }
    }

    /**
     * accessible for test suite as well
     */
    public FeatureExpr parse_featureExpr() throws IOException,
            LexerException {
        try {
            return parse_featureExprOrValue(0, true).assumeExpression(expr_token);
        } catch (ArithmeticException e) {
            System.err.println("ArithmeticException in " + expr_token.getSourceName() + " line " + expr_token.getLine());
            throw e;
        }
    }

    private class ExprOrValue {
        final FeatureExpr expr;
        final FeatureExprTree<Object> value;

        ExprOrValue(FeatureExpr expr, FeatureExprTree<Object> value) {
            this.expr = expr;
            this.value = value;
        }

        ExprOrValue(FeatureExpr expr) {
            this(expr, null);
        }

        ExprOrValue(FeatureExprTree<Object> value) {
            this(null, value);
        }

        public FeatureExprTree<Object> assumeValue(Token tok) throws LexerException {
            if (value == null) {
                warning(tok, "expecting value before token, found boolean expression " + expr + " instead");

                return (FeatureExprTree<Object>)
                        FeatureExprLib.l().createIf(expr,
                                FeatureExprLib.l().createInteger(1),
                                FeatureExprLib.l().createInteger(0)
                        );
            } else
                return value;
        }

        public FeatureExpr assumeExpression(Token tok) throws LexerException {
            if (expr == null) {
//                warning(tok, "interpreting value " + value + " as expression " + value.toFeatureExpr());
//                System.out.println("interpreting value " + value + " as expression " + FeatureExprLib.toFeatureExpr(value));
                return FeatureExprLib.toFeatureExpr(value);
            } else
                return expr;
        }
    }

    public ExprOrValue parse_featureExprOrValue(int priority, boolean expectExpr) throws IOException,
            LexerException {

        /*
           * System.out.flush(); (new Exception("expr(" + priority +
           * ") called")).printStackTrace(); System.err.flush();
           */

        Token tok = expr_token(true);
        ExprOrValue lhs;

        // System.out.println("Expr lhs token is " + tok);

        switch (tok.getType()) {
            case Token.P_FEATUREEXPR:
                // only found internally: generated FeatureExprToken
                assert tok instanceof FeatureExprToken;
                assert expectExpr;
                lhs = new ExprOrValue(((FeatureExprToken) tok).getExpr());
                break;
            case '(':
                lhs = parse_featureExprOrValue(0, expectExpr);
                tok = expr_token(true);
                if (tok.getType() != ')') {
                    expr_untoken(tok);
                    error(tok, "missing ) in expression after " + lhs + ", found "
                            + tok + " instead");
                    return new ExprOrValue(FeatureExprLib.False(), FeatureExprLib.zero());
                }
                break;

            case '~':
                lhs = new ExprOrValue(FeatureExprLib.l().createComplement(parse_featureExprOrValue(11, false).assumeValue(tok)));
                break;
            case '!':
                lhs = new ExprOrValue(parse_featureExprOrValue(11, true).assumeExpression(tok).not());
                break;
            case '-':
                lhs = new ExprOrValue(FeatureExprLib.l().createNeg(parse_featureExprOrValue(11, false).assumeValue(tok)));
                break;
            case '+':
                lhs = new ExprOrValue((parse_featureExprOrValue(11, false).assumeValue(tok)));
                break;
            case INTEGER:
                lhs = new ExprOrValue(FeatureExprLib.l().createInteger(
                        ((Number) tok.getValue()).longValue()));
                break;
            case CHARACTER:
                lhs = new ExprOrValue(FeatureExprLib.l()
                        .createCharacter((Character) tok.getValue()));
                break;
            case IDENTIFIER:
                if (tok.getText().equals("___BASE___"))                //XXX: False code?
                    lhs = new ExprOrValue(FeatureExprLib.True());
                else if (tok.getText().equals("___DEAD___"))            //XXX: False code?
                    lhs = new ExprOrValue(FeatureExprLib.False());
                else if (tok.getText().equals("__IF__")) {
                    lhs = new ExprOrValue(parse_ifExpr(tok));
                } else if (tok.getText().equals("defined")) {
                    lhs = new ExprOrValue(parse_definedExpr(false));
                } else if (tok.getText().equals("definedEx")) {
                    lhs = new ExprOrValue(parse_definedExpr(true));
                } else {

                    if (isPotentialFlag(tok.getText())
                            && warnings.contains(Warning.UNDEF))
                        warning(tok, "Undefined token '" + tok.getText()
                                + "' encountered in conditional.");
                    lhs = new ExprOrValue(FeatureExprLib.False(), FeatureExprLib.zero());
                }
                break;

            default:
                expr_untoken(tok);
                error(tok, "Bad token in expression: " + getTextOrDefault(tok, "<Feature Expression>"));
                return new ExprOrValue(FeatureExprLib.False(), FeatureExprLib.zero());
        }

        EXPR:
        for (; ; ) {
            // System.out.println("expr: lhs is " + lhs + ", pri = " +
            // priority);
            Token op = expr_token(true);
            int pri = expr_priority(op); /* 0 if not a binop. */
            if (pri == 0 || priority >= pri) {
                expr_untoken(op);
                break EXPR;
            }
            // System.out.println("rhs token is " + rhs);
            switch (op.getType()) {
                case '/':
                    lhs = new ExprOrValue(FeatureExprLib.l().createDivision(lhs.assumeValue(tok), parse_featureExprOrValue(pri, false).assumeValue(tok)));
                    // if (rhs == 0) {
                    // error(op, "Division by zero");
                    // lhs = 0;
                    // } else {
                    // lhs = lhs / rhs;
                    // }
                    break;
                case '%':
                    lhs = new ExprOrValue(FeatureExprLib.l().createModulo(lhs.assumeValue(tok), parse_featureExprOrValue(pri, false).assumeValue(tok)));
                    // if (rhs == 0) {
                    // error(op, "Modulus by zero");
                    // lhs = 0;
                    // } else {
                    // lhs = lhs % rhs;
                    // }
                    break;
                case '*':
                    lhs = new ExprOrValue(FeatureExprLib.l().createMult(lhs.assumeValue(tok), parse_featureExprOrValue(pri, false).assumeValue(tok)));
                    break;
                case '+':
                    lhs = new ExprOrValue(FeatureExprLib.l().createPlus(lhs.assumeValue(tok), parse_featureExprOrValue(pri, false).assumeValue(tok)));
                    break;
                case '-':
                    lhs = new ExprOrValue(FeatureExprLib.l().createMinus(lhs.assumeValue(tok), parse_featureExprOrValue(pri, false).assumeValue(tok)));
                    break;
                case '<':
                    lhs = new ExprOrValue(FeatureExprLib.l().createLessThan(lhs.assumeValue(tok), parse_featureExprOrValue(pri, false).assumeValue(tok)));
                    // lhs < rhs ? 1 : 0;
                    break;
                case '>':
                    lhs = new ExprOrValue(FeatureExprLib.l().createGreaterThan(lhs.assumeValue(tok), parse_featureExprOrValue(pri, false).assumeValue(tok)));// lhs >
                    // rhs ?
                    // 1 : 0;
                    break;
                case '&':
                    lhs = new ExprOrValue(FeatureExprLib.l().createBitAnd(lhs.assumeValue(tok), parse_featureExprOrValue(pri, false).assumeValue(tok)));// lhs & rhs;
                    break;
                case '^':
                    lhs = new ExprOrValue(FeatureExprLib.l().createPwr(lhs.assumeValue(tok), parse_featureExprOrValue(pri, false).assumeValue(tok)));// lhs ^ rhs;
                    break;
                case '|':
                    lhs = new ExprOrValue(FeatureExprLib.l().createBitOr(lhs.assumeValue(tok), parse_featureExprOrValue(pri, false).assumeValue(tok)));// lhs | rhs;
                    break;

                case LSH:
                    lhs = new ExprOrValue(FeatureExprLib.l().createShiftLeft(lhs.assumeValue(tok), parse_featureExprOrValue(pri, false).assumeValue(tok)));// lhs <<
                    // rhs;
                    break;
                case RSH:
                    lhs = new ExprOrValue(FeatureExprLib.l().createShiftRight(lhs.assumeValue(tok), parse_featureExprOrValue(pri, false).assumeValue(tok)));// lhs >>
                    // rhs;
                    break;
                case LE:
                    lhs = new ExprOrValue(FeatureExprLib.l().createLessThanEquals(lhs.assumeValue(tok), parse_featureExprOrValue(pri, false).assumeValue(tok)));// lhs
                    // <=
                    // rhs ?
                    // 1 :
                    // 0;
                    break;
                case GE:
                    lhs = new ExprOrValue(FeatureExprLib.l().createGreaterThanEquals(lhs.assumeValue(tok), parse_featureExprOrValue(pri, false).assumeValue(tok)));// lhs
                    // >=
                    // rhs ?
                    // 1 :
                    // 0;
                    break;
                case EQ:
                    lhs = new ExprOrValue(FeatureExprLib.l().createEquals(lhs.assumeValue(tok), parse_featureExprOrValue(pri, false).assumeValue(tok)));// lhs == rhs
                    // ?
                    // 1 :
                    // 0;
                    break;
                case NE:
                    lhs = new ExprOrValue(FeatureExprLib.l().createNotEquals(lhs.assumeValue(tok), parse_featureExprOrValue(pri, false).assumeValue(tok)));// lhs !=
                    // rhs
                    // ?
                    // 1 : 0;
                    break;
                case LAND:
                    lhs = new ExprOrValue(lhs.assumeExpression(tok).and(parse_featureExprOrValue(pri, true).assumeExpression(tok)));// (lhs != 0) && (rhs
                    // != 0) ? 1 : 0;
                    break;
                case LOR:
                    lhs = new ExprOrValue(lhs.assumeExpression(tok).or(parse_featureExprOrValue(pri, true).assumeExpression(tok)));// (lhs != 0) || (rhs
                    // != 0) ? 1 : 0;
                    break;

                case '?':
                    lhs = parse_qifExpr(lhs.assumeExpression(tok), tok);
                    break;

                default:
                    error(op, "Unexpected operator " + op.getText());
                    return new ExprOrValue(FeatureExprLib.False(), FeatureExprLib.zero());

            }
        }

        /*
           * System.out.flush(); (new Exception("expr returning " +
           * lhs)).printStackTrace(); System.err.flush();
           */
        // System.out.println("expr returning " + lhs);

        return lhs;
    }

    /**
     * checks whether a flag is excluded by ifdefs, so that we do not show false
     * warnings for code fragments such as
     * <p/>
     * #ifdef debug #if debug>2 #endif #endif
     * <p/>
     * we assume that the flag is a feature and check whether this feature is
     * reachable in the current state
     */
    private boolean isPotentialFlag(String flag) {
        if (!isActive())
            return false;

        // is there a possibility that flag is undefined in the current state?
        // i.e. (state AND NOT flag) satisfiable?
        // i.e. NOT (state AND NOT flag) False

        return !state.getFullPresenceCondition().and(
                FeatureExprLib.l().createDefinedExternal(flag).not()).isContradiction(featureModel);
    }

    private FeatureExpr parse_definedExpr(boolean referToExternalDefinitionsOnly)
            throws IOException, LexerException {
        FeatureExpr lhs;
        Token la = source_token_nonwhite();
        boolean paren = false;
        if (la.getType() == '(') {
            paren = true;
            la = source_token_nonwhite();
        }

        // System.out.println("Core token is " + la);

        if (la.getType() != IDENTIFIER) {
            error(la, "defined() needs identifier, not " + getTextOrDefault(la, "<Feature Expression>"));
            lhs = FeatureExprLib.False();
        } else
            // System.out.println("Found macro");
            if (!(la.getSource().isNormalizedExternalFeatureExpr() || referToExternalDefinitionsOnly))
                lhs = FeatureExprLib.l().createDefinedMacro(la.getText(), macros);
            else
                /*
                 * when expression was created by expanding a macro (or by reading
                 * definedEx instead of defined), do not look up macro definition,
                 * it is already based on external features only
                 */
                lhs = FeatureExprLib.l().createDefinedExternal(la.getText());

        if (paren) {
            la = source_token_nonwhite();
            if (la.getType() != ')') {
                expr_untoken(la);
                error(la, "Missing ) in defined()");
            }
        }
        return lhs;
    }

    private FeatureExprTree<Object> parse_ifExpr(Token tok) throws IOException, LexerException {
        consumeToken('(', true);
        ExprOrValue condition = parse_featureExprOrValue(0, true);
        consumeToken(',', true);
        ExprOrValue thenBranch = parse_featureExprOrValue(0, false);
        consumeToken(',', true);
        ExprOrValue elseBranch = parse_featureExprOrValue(0, false);
        consumeToken(')', true);
        return FeatureExprLib.l().createIf(condition.assumeExpression(tok), thenBranch.assumeValue(tok), elseBranch.assumeValue(tok));
    }

    /**
     * For parsing the ?: operator - the condition has already been parsed by
     * the caller.
     *
     * @param condition The parsed condition
     * @param tok       Used only for error reporting.
     * @return
     * @throws IOException
     * @throws LexerException
     */
    private ExprOrValue parse_qifExpr(FeatureExpr condition, Token tok) throws IOException, LexerException {
        ExprOrValue thenBranch = parse_featureExprOrValue(0, false);
        consumeToken(':', true);
        ExprOrValue elseBranch = parse_featureExprOrValue(0, false);
        if (thenBranch.expr != null && elseBranch.expr != null)
            return new ExprOrValue(FeatureExprLib.l().createBooleanIf(condition, thenBranch.expr, elseBranch.expr));
        else
            return new ExprOrValue(FeatureExprLib.l().createIf(condition, thenBranch.assumeValue(tok), elseBranch.assumeValue(tok)));
    }

    private void consumeToken(int tokenType, boolean inlineCppExpression)
            throws IOException, LexerException {
        Token la = expr_token(inlineCppExpression);
        if (la.getType() != tokenType)
            error(la, "expected " + Token.getTokenName(tokenType)
                    + " but found " + Token.getTokenName(la.getType()));
    }

    private Token toWhitespace(Token tok) {
        String text = tok.getText();
        int len = text.length();
        boolean cr = false;
        int nls = 0;

        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);

            switch (c) {
                case '\r':
                    cr = true;
                    nls++;
                    break;
                case '\n':
                    if (cr) {
                        cr = false;
                        break;
                    }
                    /* fallthrough */
                case '\u2028':
                case '\u2029':
                case '\u000B':
                case '\u000C':
                case '\u0085':
                    cr = false;
                    nls++;
                    break;
            }
        }

        char[] cbuf = new char[nls];
        Arrays.fill(cbuf, '\n');
        return new SimpleToken(WHITESPACE, tok.getLine(), tok.getColumn(),
                new String(cbuf), null);
    }

    protected final Token parse_main() throws IOException, LexerException {

        for (; ; ) {
            Token tok;
            if (!isActive()) {
                try {
                    /* XXX Tell lexer to ignore warnings. */
                    sourceManager.getSource().setActive(false);
                    tok = retrieveTokenFromSource();
                } finally {
                    /* XXX Tell lexer to stop ignoring warnings. */
                    if (sourceManager.getSource() != null)
                        sourceManager.getSource().setActive(true);
                }
                switch (tok.getType()) {
                    case HASH:
                    case NL:
                        /* The preprocessor has to take action here. */
                        break;
                    case EOF:
                    case WHITESPACE:
                        return tok;
                    case CCOMMENT:
                    case CPPCOMMENT:
                        // Patch up to preserve whitespace.
                        if (getFeature(Feature.KEEPALLCOMMENTS))
                            return tok;
                        if (!isActive())
                            return toWhitespace(tok);
                        if (getFeature(Feature.KEEPCOMMENTS))
                            return tok;
                        return toWhitespace(tok);
                    default:
                        // Return NL to preserve whitespace.
                        /* XXX This might lose a comment. */
                        return source_skipline(false);
                }
            } else {
                tok = retrieveTokenFromSource();
            }

            LEX:
            switch (tok.getType()) {
                case EOF:
                    /* Pop the stacks. */
                    return tok;

                case WHITESPACE:
                case NL:
                    return tok;

                case CCOMMENT:
                case CPPCOMMENT:
                    return tok;

                case '!':
                case '%':
                case '&':
                case '(':
                case ')':
                case '*':
                case '+':
                case ',':
                case '-':
                case '/':
                case ':':
                case ';':
                case '<':
                case '=':
                case '>':
                case '?':
                case '[':
                case ']':
                case '^':
                case '{':
                case '|':
                case '}':
                case '~':
                case '.':

                    /* From Olivier Chafik for Objective C? */
                case '@':
                    /* The one remaining ASCII, might as well. */
                case '`':

                    // case '#':

                case AND_EQ:
                case ARROW:
                case CHARACTER:
                case DEC:
                case DIV_EQ:
                case ELLIPSIS:
                case EQ:
                case GE:
                case HEADER: /* Should only arise from include() */
                case INC:
                case LAND:
                case LE:
                case LOR:
                case LSH:
                case LSH_EQ:
                case SUB_EQ:
                case MOD_EQ:
                case MULT_EQ:
                case NE:
                case OR_EQ:
                case PLUS_EQ:
                case RANGE:
                case RSH:
                case RSH_EQ:
                case STRING:
                case XOR_EQ:
                    return tok;

                case INTEGER:
                    return tok;

                case IDENTIFIER:
                    // apply macro (in visible code)
                    MacroExpansion<MacroData>[] m = macros.getApplicableMacroExpansions(tok
                            .getText(), state.getFullPresenceCondition());
                    if (m.length == 0)
                        return tok;
                    if (!sourceManager.getSource().mayExpand(tok.getText())
                            || !tok.mayExpand())
                        return tok;
                    if (macro_expandToken(tok.getText(), m, tok, processing_include))
                        break;
                    return tok;

                case P_LINE:
                    if (getFeature(Feature.LINEMARKERS))
                        return tok;
                    break;

                case INVALID:
                    if (getFeature(Feature.CSYNTAX))
                        error(tok, String.valueOf(tok.getValue()));
                    return tok;

                case HASH:
                    tok = source_token_nonwhite();
                    // (new Exception("here")).printStackTrace();
                    switch (tok.getType()) {
                        case NL:
                            break LEX; /* Some code has #\n */
                        case IDENTIFIER:
                            break;
                        default:
                            //no warning, interpreted as comment
//                            warning(tok, "Preprocessor directive not a word "
//                                    + tok.getText() + ", skipping line");
                            return source_skipline(false);
                    }
                    // System.out.println(previousToken);
                    Integer _ppcmd = ppcmds.get(tok.getText());
                    if (_ppcmd == null) {
                        warning(tok, "Unknown preprocessor directive "
                                + tok.getText() + ", skipping line");
                        return source_skipline(false);
                    }
                    int ppcmd = _ppcmd.intValue();

                    // PP:
                    switch (ppcmd) {

                        case PP_DEFINE:
                            if (!isActive())
                                return source_skipline(false);
                            else
                                return parse_define();
                            // break;

                        case PP_UNDEF:
                            if (!isActive())
                                return source_skipline(false);
                            else
                                return parse_undef();
                            // break;

                        case PP_INCLUDE:
                            if (!isActive())
                                return source_skipline(false);
                            else {
                                Token ret_tok = parse_include(false);
                                assert !processing_include;
                                return ret_tok;
                            }
                            // break;
                        case PP_INCLUDE_NEXT:
                            if (!isActive())
                                return source_skipline(false);
                            if (!getFeature(Feature.INCLUDENEXT)) {
                                error(tok, "Directive include_next not enabled");
                                return source_skipline(false);
                            }
                            Token ret_tok = parse_include(true);
                            assert !processing_include;
                            return ret_tok;
                        // break;

                        /**
                         * error tokens are not processed by the jcpp but left for
                         * the type system
                         */
                        case PP_WARNING:
                        case PP_ERROR:
                            if (!isActive())
                                return source_skipline(false);
                            else
                                // cppError(tok, ppcmd == PP_ERROR);
                                return parseErrorToken(tok, ppcmd == PP_ERROR);

                        case PP_IF:
                            push_state();
                            expr_token = null;
                            if (isParentActive()) {
                                FeatureExpr localFeatureExpr = parse_featureExpr();
                                state.putLocalFeature(localFeatureExpr, macros);
                                tok = expr_token(true); /* unget */
                                if (tok.getType() != NL)
                                    source_skipline(isParentActive());
                            } else {
                                state.putLocalFeature(FeatureExprLib.False(), macros);
                                source_skipline(false);
                            }


                            return ifdefPrinter.startIf(tok, isParentActive(), state);

                        // break;

                        case PP_ELIF:
                            if (state.sawElse()) {
                                error(tok, "#elif after #" + "else");
                                return source_skipline(false);
                            } else {
                                expr_token = null;
                                // parse with parents state to allow macro expansion
                                State oldState = state;
                                state = state.parent;
                                FeatureExpr localFeaturExpr = isActive() ? parse_featureExpr() : FeatureExprLib.False();
                                state = oldState;
                                state.processElIf();
                                state.putLocalFeature(localFeaturExpr, macros);
                                tok = expr_token(true); /* unget */

                                if (tok.getType() != NL)
                                    source_skipline(isParentActive());

                                return ifdefPrinter.startElIf(tok, isParentActive(),
                                        state);

                            }
                            // break;

                        case PP_ELSE:
                            if (state.sawElse()) {
                                error(tok, "#" + "else after #" + "else");
                                return source_skipline(false);
                            } else {
                                state.setSawElse();
                                source_skipline(warnings.contains(Warning.ENDIF_LABELS));

                                return ifdefPrinter.startElIf(tok, isParentActive(),
                                        state);
                            }
                            // break;

                        case PP_IFDEF:
                            push_state();
                            tok = source_token_nonwhite();
                            // System.out.println("ifdef " + tok);
                            if (tok.getType() != IDENTIFIER) {
                                error(tok, "Expected identifier, not " + tok.getText());
                                return source_skipline(false);
                            } else {
                                FeatureExpr localFeatureExpr2 = parse_ifdefExpr(tok
                                        .getText());
                                state.putLocalFeature(
                                        isParentActive() ? localFeatureExpr2
                                                : FeatureExprLib.False(), macros);
                                // return

                                if (tok.getType() != NL)
                                    source_skipline(isParentActive());

                                return ifdefPrinter.startIf(tok, isParentActive(),
                                        state);
                            }
                            // break;

                        case PP_IFNDEF:
                            push_state();
                            tok = source_token_nonwhite();
                            if (tok.getType() != IDENTIFIER) {
                                error(tok, "Expected identifier, not " + tok.getText());
                                return source_skipline(false);
                            } else {
                                FeatureExpr localFeatureExpr3 = parse_ifndefExpr(tok
                                        .getText());
                                state.putLocalFeature(
                                        isParentActive() ? localFeatureExpr3
                                                : FeatureExprLib.False(), macros);
                                if (tok.getType() != NL)
                                    source_skipline(isParentActive());

                                return ifdefPrinter.startIf(tok, isParentActive(),
                                        state);

                            }
                            // break;

                        case PP_ENDIF:
                            pop_state();

                            Token l = source_skipline(warnings
                                    .contains(Warning.ENDIF_LABELS));
                            return ifdefPrinter.endIf(l);
                        // break;

                        case PP_LINE:
                            return source_skipline(false);
                        // break;

                        case PP_PRAGMA:
                            if (!isActive())
                                return source_skipline(false);
                            return parse_pragma();
                        // break;

                        default:
                            /*
                            * Actual unknown directives are processed above. If we get
                            * here, we succeeded the map lookup but failed to handle
                            * it. Therefore, this is (unconditionally?) fatal.
                            */
                            // if (isActive()) /* XXX Could be warning. */
                            throw new InternalException(
                                    "Internal error: Unknown directive " + tok);
                            // return source_skipline(false);
                    }
                default:
                    throw new InternalException("Bad token, type: " + tok.getType() + ", token: " + tok + ", source: " + tok.getSourceName());
                    // break;
            }
        }
    }

    private FeatureExpr parse_ifndefExpr(String feature) {
        return parse_ifdefExpr(feature).not();
    }

    private FeatureExpr parse_ifdefExpr(String feature) {
        return FeatureExprLib.l().createDefinedMacro(feature, macros);
    }

    private Token getNextNonwhiteToken() throws IOException, LexerException {
        Token tok;
        do {
            tok = parse_main();
        } while (tok.isWhite());
        return tok;
    }

    /**
     * Returns the next preprocessor token.
     *
     * @throws LexerException    if a preprocessing error occurs.
     * @throws InternalException if an unexpected error condition arises.
     * @see Token
     */
    public Token getNextToken() throws IOException, LexerException {
//        FeatureExpr lastPC = state.getFullPresenceCondition();
        try {
            Token tok = parse_main();
            tok = tok.clone();
            tok.setFeature(state.getFullPresenceCondition());
            if (getFeature(Feature.DEBUG_VERBOSE))
                System.err.println("pp: Returning " + tok);
            return tok;
        } catch (de.fosd.typechef.featureexpr.FeatureException e) {
//            error(0,0,e.getMessage(),lastPC);
//            return new SimpleToken(Token.WHITESPACE,"", sourceManager.getSource());
            throw new LexerException(e);
        }
    }

    /* First ppcmd is 1, not 0. */
    private static final int PP_DEFINE = 1;
    private static final int PP_ELIF = 2;
    private static final int PP_ELSE = 3;
    private static final int PP_ENDIF = 4;
    private static final int PP_ERROR = 5;
    private static final int PP_IF = 6;
    private static final int PP_IFDEF = 7;
    private static final int PP_IFNDEF = 8;
    private static final int PP_INCLUDE = 9;
    private static final int PP_LINE = 10;
    private static final int PP_PRAGMA = 11;
    private static final int PP_UNDEF = 12;
    private static final int PP_WARNING = 13;
    private static final int PP_INCLUDE_NEXT = 14;
    private static final int PP_IMPORT = 15;

    private static final Map<String, Integer> ppcmds = new HashMap<String, Integer>();

    static {
        ppcmds.put("define", Integer.valueOf(PP_DEFINE));
        ppcmds.put("elif", Integer.valueOf(PP_ELIF));
        ppcmds.put("else", Integer.valueOf(PP_ELSE));
        ppcmds.put("endif", Integer.valueOf(PP_ENDIF));
        ppcmds.put("error", Integer.valueOf(PP_ERROR));
        ppcmds.put("if", Integer.valueOf(PP_IF));
        ppcmds.put("ifdef", Integer.valueOf(PP_IFDEF));
        ppcmds.put("ifndef", Integer.valueOf(PP_IFNDEF));
        ppcmds.put("include", Integer.valueOf(PP_INCLUDE));
        ppcmds.put("line", Integer.valueOf(PP_LINE));
        ppcmds.put("pragma", Integer.valueOf(PP_PRAGMA));
        ppcmds.put("undef", Integer.valueOf(PP_UNDEF));
        ppcmds.put("warning", Integer.valueOf(PP_WARNING));
        ppcmds.put("include_next", Integer.valueOf(PP_INCLUDE_NEXT));
        ppcmds.put("import", Integer.valueOf(PP_IMPORT));
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        Source s = getSource();
        while (s != null) {
            buf.append(" -> ").append(String.valueOf(s)).append("\n");
            s = s.getParent();
        }

        buf.append(macros.toString() + "\n");

        return buf.toString();
    }

    public void close() throws IOException {
        sourceManager.close();
    }

    private Token retrieveTokenFromSource() throws IOException, LexerException {
        if (source_token != null) {
            Token tok = source_token;
            source_token = null;
            if (getFeature(Feature.DEBUG_VERBOSE))
                System.err.println("Returning unget token " + tok);
            return tok;
        }
        return sourceManager.getNextToken();
    }

    public Source getSource() {
        return sourceManager.getSource();
    }

    public Source getLastInput() {
        List<Source> inputs = sourceManager.inputs;
        if (inputs.isEmpty()) {
            //return parentmost source
            Source s = getSource();
            while (s != null && s.getParent() != null)
                s = s.getParent();
            return s;
        } else return inputs.get(inputs.size() - 1);
    }

    /**
     * Return the token representation or an empty string.
     *
     * @param tok
     * @return String representation or empty string.
     */
    public String getTextOrDefault(Token tok, String def) {
        if (tok.getType() == P_FEATUREEXPR) {
            return def;
        } else {
            return tok.getText();
        }
    }


    /**
     * for compatibility with the VALexer interface
     *
     * @param folder
     */
    @Override
    public void addSystemIncludePath(String folder) {
        getSystemIncludePath().add(folder);
    }

    /**
     * for compatibility with the VALexer interface
     *
     * @param folder
     */
    @Override
    public void addQuoteIncludePath(String folder) {
        getQuoteIncludePath().add(folder);
    }


    /**
     * * for compatibility with the VALexer interface
     *
     * @param source
     */
    @Override
    public void addInput(LexerInput source) throws IOException {
        //ugly but will do for now
        if (source instanceof FileSource)
            addInput(new FileLexerSource(((FileSource) source).file));
        else if (source instanceof StreamSource)
            addInput(new FileLexerSource(((StreamSource) source).inputStream, ((StreamSource) source).filename));
        else if (source instanceof TextSource)
            addInput(new StringLexerSource(((TextSource) source).code, true));
        else
            throw new RuntimeException("unexpected input");
    }

    @Override
    public void printSourceStack(PrintStream stream) {
        Source s = getSource();
        while (s != null) {
            stream.println(" -> " + s);
            s = s.getParent();
        }
    }

}
