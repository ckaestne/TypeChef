/*
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

package org.anarres.cpp;

import static org.anarres.cpp.Token.AND_EQ;
import static org.anarres.cpp.Token.ARROW;
import static org.anarres.cpp.Token.CCOMMENT;
import static org.anarres.cpp.Token.CHARACTER;
import static org.anarres.cpp.Token.CPPCOMMENT;
import static org.anarres.cpp.Token.DEC;
import static org.anarres.cpp.Token.DIV_EQ;
import static org.anarres.cpp.Token.ELLIPSIS;
import static org.anarres.cpp.Token.EOF;
import static org.anarres.cpp.Token.EQ;
import static org.anarres.cpp.Token.GE;
import static org.anarres.cpp.Token.HASH;
import static org.anarres.cpp.Token.HEADER;
import static org.anarres.cpp.Token.IDENTIFIER;
import static org.anarres.cpp.Token.INC;
import static org.anarres.cpp.Token.INTEGER;
import static org.anarres.cpp.Token.INVALID;
import static org.anarres.cpp.Token.LAND;
import static org.anarres.cpp.Token.LE;
import static org.anarres.cpp.Token.LOR;
import static org.anarres.cpp.Token.LSH;
import static org.anarres.cpp.Token.LSH_EQ;
import static org.anarres.cpp.Token.MOD_EQ;
import static org.anarres.cpp.Token.MULT_EQ;
import static org.anarres.cpp.Token.M_ARG;
import static org.anarres.cpp.Token.M_PASTE;
import static org.anarres.cpp.Token.M_STRING;
import static org.anarres.cpp.Token.NE;
import static org.anarres.cpp.Token.NL;
import static org.anarres.cpp.Token.OR_EQ;
import static org.anarres.cpp.Token.PASTE;
import static org.anarres.cpp.Token.PLUS_EQ;
import static org.anarres.cpp.Token.P_ELIF;
import static org.anarres.cpp.Token.P_ENDIF;
import static org.anarres.cpp.Token.P_IF;
import static org.anarres.cpp.Token.P_LINE;
import static org.anarres.cpp.Token.RANGE;
import static org.anarres.cpp.Token.RSH;
import static org.anarres.cpp.Token.RSH_EQ;
import static org.anarres.cpp.Token.STRING;
import static org.anarres.cpp.Token.SUB_EQ;
import static org.anarres.cpp.Token.WHITESPACE;
import static org.anarres.cpp.Token.XOR_EQ;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.anarres.cpp.MacroConstraint.MacroConstraintKind;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.MacroContext;
import de.fosd.typechef.featureexpr.MacroExpansion;

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
 * * 	no expansion in other # directives, nie den identifier nach # expandieren
 * 
 */

/**
 * A C Preprocessor. The Preprocessor outputs a token stream which does not need
 * re-lexing for C or C++. Alternatively, the output text may be reconstructed
 * by concatenating the {@link Token#getText() text} values of the returned
 * {@link Token Tokens}. (See {@link CppReader}, which does this.)
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

public class Preprocessor extends DebuggingPreprocessor implements Closeable {

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

	/* The fundamental engine. */
	private MacroContext macros = new MacroContext();
	State state;

	protected MacroContext getMacros() {
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

	public Preprocessor() {

		macros = macros.define("__LINE__", FeatureExprLib.base(),
				new MacroData(INTERNAL)).define("__FILE__",
				FeatureExprLib.base(), new MacroData(INTERNAL)).define(
				"__COUNTER__", FeatureExprLib.base(), new MacroData(INTERNAL));

		state = new State();

		this.counter = 0;

		this.quoteincludepath = new ArrayList<String>();
		this.sysincludepath = new ArrayList<String>();
		this.frameworkspath = new ArrayList<String>();
		this.features = EnumSet.noneOf(Feature.class);
		this.warnings = EnumSet.noneOf(Warning.class);
		this.filesystem = new JavaFileSystem();
		this.listener = null;
	}

	public Preprocessor(Source initial) {
		this();
		addInput(initial);
	}

	/**
	 * Equivalent to 'new Preprocessor(new {@link FileLexerSource}(file))'
	 */
	public Preprocessor(File file) throws IOException {
		this(new FileLexerSource(file));
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
	 * 
	 * The listener is notified of warnings, errors and source changes, amongst
	 * other things.
	 */
	public void setListener(PreprocessorListener listener) {
		this.listener = listener;
		sourceManager.reinit();
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
	 * 
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
	 * 
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
	 * 
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

	/**
	 * Handles an error.
	 * 
	 * If a PreprocessorListener is installed, it receives the error. Otherwise,
	 * an exception is thrown.
	 */
	protected void error(int line, int column, String msg)
			throws LexerException {
		if (listener != null)
			listener.handleError(sourceManager.getSource(), line, column, msg,
					state.getFullPresenceCondition());
		else
			throw new LexerException("Error at " + line + ":" + column + ": "
					+ msg, state.getFullPresenceCondition());
	}

	/**
	 * Handles an error.
	 * 
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
	 * 
	 * If a PreprocessorListener is installed, it receives the warning.
	 * Otherwise, an exception is thrown.
	 */
	protected void warning(int line, int column, String msg)
			throws LexerException {
		if (warnings.contains(Warning.ERROR))
			error(line, column, msg);
		else if (listener != null)
			listener
					.handleWarning(sourceManager.getSource(), line, column, msg);
		else
			throw new LexerException("Warning at " + line + ":" + column + ": "
					+ msg, state.getFullPresenceCondition());
	}

	/**
	 * Handles a warning.
	 * 
	 * If a PreprocessorListener is installed, it receives the warning.
	 * Otherwise, an exception is thrown.
	 * 
	 * @see #warning(int, int, String)
	 */
	protected void warning(Token tok, String msg) throws LexerException {
		warning(tok.getLine(), tok.getColumn(), msg);
	}

	/**
	 * Adds a Macro to this Preprocessor.
	 * 
	 * The given {@link MacroData} object encapsulates both the name and the
	 * expansion.
	 * 
	 * There can be multiple macros. They can have different features. Newer
	 * macros replace older macros if they cover the same features.
	 * 
	 * @param feature
	 * @param name
	 * 
	 */
	public void addMacro(String name, FeatureExpr feature, MacroData m)
			throws LexerException {
		// System.out.println("Macro " + m);
		/* Already handled as a source error in macro(). */
		if ("defined".equals(name))
			throw new LexerException("Cannot redefine name 'defined'", state
					.getFullPresenceCondition());
		macros = macros.define(name, feature, m);
	}

	public void removeMacro(String name, FeatureExpr feature) {
		macros = macros.undefine(name, feature);
	}

	/**
	 * Defines the given name as a macro.
	 * 
	 * The String value is lexed into a token stream, which is used as the macro
	 * expansion.
	 */
	public void addMacro(String name, FeatureExpr feature, String value)
			throws LexerException {
		try {
			MacroData m = new MacroData(null);
			StringLexerSource s = new StringLexerSource(value);
			for (;;) {
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * only returns false if a code fragment is certainly dead, i.e., there is
	 * no variant in which it is included.
	 * 
	 * this can happen when a feature is explicitly undefined or explicitly
	 * defined in the source code
	 * 
	 * @return
	 */
	private boolean isActive() {
		return state.isActive();
	}

	private boolean isParentActive() {
		return state.parent == null || state.parent.isActive();
	}

	/* Source tokens */

	private Token source_token;

	private IfdefPrinter ifdefPrinter = new IfdefPrinter();

	static class IfdefPrinter {
		private static class IfdefBlock {
			public IfdefBlock(boolean visible) {
				this.visible = visible;
			}

			boolean visible = true;
		}

		private Stack<IfdefBlock> stack = new Stack<IfdefBlock>();

		public Token startIf(Token tok, boolean parentActive, FeatureExpr expr,
				FeatureExpr fullPresenceCondition) {
			// skip output of ifdef 0 and ifdef 1
			boolean visible = parentActive
					&& !(fullPresenceCondition.isDead()
							|| fullPresenceCondition.isBase() || expr.isBase());
			stack.push(new IfdefBlock(visible));
			if (visible)
				return new OutputHelper().if_token(tok.getLine(), expr);
			else
				return new OutputHelper().emptyLine(tok.getLine(), tok
						.getColumn());

		}

		public Token endIf(Token tok) {

			if (stack.pop().visible)
				return new OutputHelper().endif_token(tok.getLine());
			else
				return new OutputHelper().emptyLine(tok.getLine(), tok
						.getColumn());
		}

		public Token startElIf(Token tok, boolean parentActive,
				FeatureExpr localFeatureExpr, FeatureExpr fullPresenceCondition) {
			boolean wasVisible = stack.pop().visible;
			boolean isVisible = parentActive
					&& !(fullPresenceCondition.isDead() || fullPresenceCondition
							.isBase());
			stack.push(new IfdefBlock(isVisible));

			return new OutputHelper().elif_token(tok.getLine(),
					localFeatureExpr, wasVisible, isVisible);
		}
	}

	static class OutputHelper {
		/*
		 * XXX Make this include the NL, and make all cpp directives eat their
		 * own NL.
		 */
		Token line_token(int line, String name, String extra) {
			StringBuilder buf = new StringBuilder();
			buf.append("#line ").append(line).append(" \"");
			/* XXX This call to escape(name) is correct but ugly. */
			MacroTokenSource.escape(buf, name);
			buf.append("\"").append(extra).append("\n");
			return new Token(P_LINE, line, 0, buf.toString(), null);
		}

		String if_tokenStr(FeatureExpr featureExpr) {
			StringBuilder buf = new StringBuilder();
			buf.append("#if ").append(featureExpr.print()).append("\n");
			return buf.toString();
		}

		String elif_tokenStr(FeatureExpr featureExpr) {
			StringBuilder buf = new StringBuilder();
			buf.append("#elif ").append(featureExpr.print()).append("\n");
			return buf.toString();
		}

		String endif_tokenStr() {
			return "#endif\n";
		}

		String else_tokenStr() {
			return "#else\n";
		}

		Token if_token(int line, FeatureExpr featureExpr) {
			return new Token(P_IF, line, 0, if_tokenStr(featureExpr), null);
		}

		Token elif_token(int line, FeatureExpr featureExpr, boolean printEnd,
				boolean printIf) {
			StringBuilder buf = new StringBuilder();
			if (printEnd)
				buf.append("#endif\n");
			if (printIf)
				buf.append("#if ").append(featureExpr.print());
			buf.append("\n");
			return new Token(P_ELIF, line, 0, buf.toString(), null);
		}

		Token endif_token(int line) {
			return new Token(P_ENDIF, line, 0, endif_tokenStr(), null);
		}

		public Token emptyLine(int line, int column) {
			return new Token(NL, line, column, "\n", null);
		}

	}

	private void source_untoken(Token tok) {
		if (this.source_token != null)
			throw new IllegalStateException("Cannot return two tokens");
		this.source_token = tok;
	}

	private boolean isWhite(Token tok) {
		int type = tok.getType();
		return (type == WHITESPACE) || (type == CCOMMENT)
				|| (type == CPPCOMMENT);
	}

	private Token source_token_nonwhite() throws IOException, LexerException {
		Token tok;
		do {
			tok = retrieveTokenFromSource();
		} while (isWhite(tok));
		return tok;
	}

	/**
	 * Returns an NL or an EOF token.
	 * 
	 * The metadata on the token will be correct, which is better than
	 * generating a new one.
	 * 
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
				return new OutputHelper().line_token(t.getLine() + 1, t
						.getName(), " 2");
			}
		}
		return tok;
	}

	/**
	 * processes and expands a macro. Called when parsing an identifier (and
	 * when in visible code that may expand)
	 * 
	 * @param inlineCppExpression
	 *            if false alternatives are replaced by #ifdef-#elif statements
	 *            in different lines. if true, alternatives are replaced by
	 *            expressions inside a line in the form
	 *            "__if__(FeatureExpr,thenClause,elseClause)". such __if__
	 *            statements can be nested
	 * @return whether something was expanded or not
	 * */
	private boolean macro_expandToken(String macroName,
			MacroExpansion[] macroExpansions, Token orig,
			boolean inlineCppExpression) throws IOException, LexerException {
		List<Token> originalTokens = new ArrayList<Token>();
		originalTokens.add(orig);
		List<Argument> args;
		assert macroExpansions.length > 0;

		Source debug_origSource = sourceManager.getSource();

		// check compatible macros
		MacroData firstMacro = ((MacroData) macroExpansions[0].getExpansion());
		int argCount = firstMacro.getArgCount();
		boolean isVariadic = ((MacroData) macroExpansions[0].getExpansion())
				.isVariadic();
		for (int i = 1; i < macroExpansions.length; i++) {
			MacroData macro = ((MacroData) macroExpansions[0].getExpansion());
			if (macro.getArgCount() != argCount
					|| macro.isVariadic() != isVariadic)
				error(orig,
						"Multiple alternative macros with different signatures not yet supported. "
								+ macro.getText() + "/" + firstMacro.getText());
		}

		// parse parameters
		try {
			args = parse_macroParameters(macroName, inlineCppExpression,
					originalTokens, firstMacro);
		} catch (ParseParamException e) {
			warning(e.tok, e.errorMsg);// ChK: skip errors for now
			return false;
		}
		if (firstMacro.isFunctionLike() && args == null)
			return false;// cannot expand function-like macro here (has to start
		// with lparan, see spec)

		// replace macro
		if (macroName.equals("__LINE__")) {
			sourceManager.push_source(new FixedTokenSource(
					new Token[] { new Token(INTEGER, orig.getLine(), orig
							.getColumn(), String.valueOf(orig.getLine()),
							Integer.valueOf(orig.getLine()), null) }), true);
		} else if (macroName.equals("__FILE__")) {
			StringBuilder buf = new StringBuilder("\"");
			String name = getSource().getName();
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
			sourceManager.push_source(new FixedTokenSource(
					new Token[] { new Token(STRING, orig.getLine(), orig
							.getColumn(), text, text, null) }), true);
		} else if (macroName.equals("__COUNTER__")) {
			/*
			 * This could equivalently have been done by adding a special Macro
			 * subclass which overrides getTokens().
			 */
			int value = this.counter++;
			sourceManager.push_source(new FixedTokenSource(
					new Token[] { new Token(INTEGER, orig.getLine(), orig
							.getColumn(), String.valueOf(value), Integer
							.valueOf(value), null) }), true);
		} else {
			if (macroExpansions.length == 1) {// TODO what happens if the macro
				// is not always defined? check this!
				// currentFeature => macroFeature
				FeatureExpr commonCondition = state.getFullPresenceCondition()
						.not().or(macroExpansions[0].getFeature());
				if (!commonCondition.isBase())
					macroConstraints.add(new MacroConstraint(macroName,
							MacroConstraintKind.NOTEXPANDING, commonCondition
									.not()));

				sourceManager.push_source(new MacroTokenSource(macroName,
						firstMacro, args), true);
				// expand all alternative macros
			} else {
				if (inlineCppExpression)
					macro_expandAlternativesInline(macroName, macroExpansions,
							args, originalTokens);
				else
					macro_expandAlternatives(macroName, macroExpansions, args,
							originalTokens);
			}
		}

		logger.info("expanding " + macroName + " by "
				+ sourceManager.debug_sourceDelta(debug_origSource));

		return true;
	}

	// private MacroExpansion[] filterApplicableMacros(
	// MacroExpansion[] macroExpansions) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// private FeatureExpr getCombinedMacroCondition(
	// MacroExpansion[] macroExpansions) {
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
			OPEN: for (;;) {
				tok = retrieveTokenFromSource();
				originalTokens.add(tok);
				// System.out.println("pp: open: token is " + tok);
				switch (tok.getType()) {
				case WHITESPACE: /* XXX Really? */
				case CCOMMENT:
				case CPPCOMMENT:
				case NL:
					break; /* continue */
				// ChK TODO whitespace will be removed in case a bracket is not
				// found eventually
				case '(':
					break OPEN;
				default:
					source_untoken(tok);
					originalTokens.remove(originalTokens.size() - 1);
					return null;
				}
			}

			// tok = expanded_token_nonwhite();
			tok = source_token_nonwhite();
			originalTokens.add(tok);

			/*
			 * We either have, or we should have args. This deals elegantly with
			 * the case that we have one empty arg.
			 */
			if (tok.getType() != ')' || firstMacro.getArgCount() > 0) {
				args = new ArrayList<Argument>();

				Argument arg = new Argument();
				int depth = 0;
				boolean space = false;

				ARGS: for (;;) {
					// System.out.println("pp: arg: token is " + tok);
					switch (tok.getType()) {
					case EOF:
						throw new ParseParamException(tok, "EOF in macro args");

					case ',':
						if (depth == 0) {
							if (firstMacro.isVariadic() &&
							/* We are building the last arg. */
							args.size() == firstMacro.getArgCount() - 1) {
								/* Just add the comma. */
								arg.addToken(tok);
							} else {
								args.add(arg);
								arg = new Argument();
							}
						} else {
							arg.addToken(tok);
						}
						space = false;
						break;
					case ')':
						if (depth == 0) {
							args.add(arg);
							break ARGS;
						} else {
							depth--;
							arg.addToken(tok);
						}
						space = false;
						break;
					case '(':
						depth++;
						arg.addToken(tok);
						space = false;
						break;

					case WHITESPACE:
					case CCOMMENT:
					case CPPCOMMENT:
						/* Avoid duplicating spaces. */
						space = true;
						break;
					case HASH:
					        //PG: TODO - I've seen #if in a macro argument,
					        //and this code sometimes _expands_ the if (which can be defined as a macro in the Linux kernel)!!!
					        //throw new LexerException("Unimplemented handling of # in macro args, important TODO!");
					default:
						/*
						 * Do not put space on the beginning of an argument
						 * token.
						 */
						if (space && !arg.isEmpty())
							arg.addToken(Token.space);
						arg.addToken(tok);
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

				if (args.size() != firstMacro.getArgCount()) {
				        if (firstMacro.isVariadic() && args.size() == firstMacro.getArgCount() - 1) {
				                args.add(Argument.omittedVariadicArgument());
				        } else {
				                throw new ParseParamException(tok, "macro " + macroName
				                                + " has " + firstMacro.getArgCount()
				                                + " parameters " + "but given " + args.size()
				                                + " args");
				        }
				}

				/*
				 * for (Argument a : args) a.expand(this);
				 */

				for (int i = 0; i < args.size(); i++) {
					args.get(i).expand(this, inlineCppExpression);
				}

				// System.out.println("Macro " + m + " args " + args);
			} else {
				/* nargs == 0 and we (correctly) got () */
				args = Collections.emptyList();
			}

		} else {
			/* Macro without args. */
			args = null;
		}
		return args;
	}

	private void macro_expandAlternatives(String macroName,
			MacroExpansion[] macroExpansions, List<Argument> args,
			List<Token> originalTokens) throws IOException {
		FeatureExpr commonCondition = getCommonCondition(macroExpansions);
		boolean alternativesExaustive = isExaustive(commonCondition);

		List<Source> resultList = new ArrayList<Source>();
		for (int i = macroExpansions.length - 1; i >= 0; i--) {
			FeatureExpr feature = macroExpansions[i].getFeature();
			MacroData macroData = (MacroData) macroExpansions[i].getExpansion();

			if (i == macroExpansions.length - 1)
				resultList.add(new UnnumberedUnexpandingStringLexerSource("\n"
						+ new OutputHelper().if_tokenStr(feature)));
			else
				resultList.add(new UnnumberedUnexpandingStringLexerSource("\n"
						+ new OutputHelper().elif_tokenStr(feature)));
			resultList.add(new MacroTokenSource(macroName, macroData, args));
			if (i == 0 && !alternativesExaustive) {
				resultList.add(new UnnumberedUnexpandingStringLexerSource("\n"
						+ new OutputHelper().else_tokenStr()));
				resultList.add(new FixedUnexpandingTokenSource(originalTokens,
						macroName));
			}
		}
		resultList.add(new UnnumberedUnexpandingStringLexerSource("\n"
				+ new OutputHelper().endif_tokenStr()));
		sourceManager.push_sources(resultList, true);
	}

	/**
	 * uses __if__(FeatureExpr,a,b) to express alternative conditions
	 * 
	 * __if__(exp3,macro3,__if__(exp2,macro2,__if__(exp1,macro1,originalTokens))
	 * ) )
	 * 
	 * order is irrelevant, because MacroContext makes sure that all
	 * alternatives are mutually exclusive
	 * 
	 * @param macroExpansions
	 * @param args
	 * @throws IOException
	 */
	private void macro_expandAlternativesInline(final String macroName,
			MacroExpansion[] macroExpansions, List<Argument> args,
			List<Token> originalTokens) throws IOException {
		assert macroExpansions.length > 1;

		// check that every variant is covered, there is no case when this macro
		// is not replaced
		// currentstate => (alt1 || alt2|| alt3)
		FeatureExpr commonCondition = getCommonCondition(macroExpansions);
		boolean alternativesExaustive = isExaustive(commonCondition);
		if (!alternativesExaustive)
			macroConstraints.add(new MacroConstraint(macroName,
					MacroConstraintKind.NOTEXPANDING, commonCondition.not()));

		List<Source> resultList = new ArrayList<Source>();
		for (int i = macroExpansions.length - 1; i >= 0; i--) {
			FeatureExpr feature = macroExpansions[i].getFeature();
			MacroData macroData = (MacroData) macroExpansions[i].getExpansion();

			if (i > 0 || !alternativesExaustive)
				resultList.add(new UnnumberedUnexpandingStringLexerSource(
						"__IF__(" + feature.print() + ","));
			resultList.add(new MacroTokenSource(macroName, macroData, args));
			if (i > 0 || !alternativesExaustive)
				resultList.add(new UnnumberedUnexpandingStringLexerSource(","));
		}

		if (!alternativesExaustive)
			resultList.add(new UnnumberedUnexpandingStringLexerSource(
					"0 /*#ERROR not expanded macro " + macroName + " when "
							+ commonCondition.not() + "*/"));
		String closingBrackets = "";
		for (int i = macroExpansions.length - 2; i >= 0; i--)
			closingBrackets += ")";
		if (!alternativesExaustive)
			closingBrackets += ")";
		resultList.add(new UnnumberedUnexpandingStringLexerSource(
				closingBrackets));
		sourceManager.push_sources(resultList, true);
	}

	private boolean isExaustive(FeatureExpr commonCondition) {
		return commonCondition.isBase();
	}

	private FeatureExpr getCommonCondition(MacroExpansion[] macroExpansions) {
		FeatureExpr commonCondition = macroExpansions[0].getFeature();
		for (int i = macroExpansions.length - 1; i >= 1; i--)
			commonCondition = commonCondition.or(macroExpansions[i]
					.getFeature());
		commonCondition = state.getLocalFeatureExpr().not().or(commonCondition);
		return commonCondition;
	}

	/**
	 * Expands an argument.
	 * 
	 * @param inlineCppExpression
	 */
	/* I'd rather this were done lazily, but doing so breaks spec. */
	/* pp */List<Token> macro_expandArgument(List<Token> arg,
			boolean inlineCppExpression) throws IOException, LexerException {
		// return arg;//ChK lazytest
		List<Token> expansion = new ArrayList<Token>();
		boolean space = false;

		sourceManager.push_source(new FixedTokenSource(arg), false);

		EXPANSION: for (;;) {
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
				ARGS: for (;;) {
					switch (tok.getType()) {
					case IDENTIFIER:
						args.add(tok.getText());
						seenParamName = true;
						break;
					case ELLIPSIS:
					        if (!seenParamName) {
					                //C99 macro!
					                args.add("__VA_ARGS__"); //PG: hack, I don't expect it to really work.
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
								+ tok.getText());
						return source_skipline(false);
					}
					tok = source_token_nonwhite();
					switch (tok.getType()) {
					case ',':
					        seenParamName = false;
						break;
					case ELLIPSIS:
					        assert seenParamName; //PG: verify if this is true
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
								+ tok.getText());
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

		/* Get an expansion for the macro, using indexOf. */
		boolean space = false;
		boolean paste = false;
		int idx;

		/* Ensure no space at start. */
		tok = source_token_nonwhite();
		EXPANSION: for (;;) {
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
				m.addPaste(new Token(M_PASTE, tok.getLine(), tok.getColumn(),
						"#" + "#", null));
				break;

			/* Stringify. */
			case '#':
				if (space)
					m.addToken(Token.space);
				space = false;
				Token la = source_token_nonwhite();
				if (la.getType() == IDENTIFIER
						&& ((idx = args.indexOf(la.getText())) != -1)) {
					m.addToken(new Token(M_STRING, la.getLine(),
							la.getColumn(), "#" + la.getText(), Integer
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
					m.addToken(new Token(M_ARG, tok.getLine(), tok.getColumn(),
							tok.getText(), Integer.valueOf(idx), null));
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

		logger.info("#define " + name + " " + m);
		addMacro(name, state.getFullPresenceCondition(), m);

		return tok; /* NL or EOF. */
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
	 * 
	 * User code may override this method to implement a virtual file system.
	 */
	private boolean include(VirtualFile file) throws IOException,
			LexerException {
		// System.out.println("Try to include " + file);
		if (!file.isFile())
			return false;
		if (getFeature(Feature.DEBUG))
			System.err.println("pp: including " + file);
		sourceManager.push_source(file.getSource(), true);
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
		LexerSource lexer = (LexerSource) sourceManager.getSource();
		try {
			lexer.setInclude(true);
			Token tok = getNextNonwhiteToken();

			String name;
			boolean quoted;

			if (tok.getType() == STRING) {
				/*
				 * XXX Use the original text, not the value. Backslashes must
				 * not be treated as escapes here.
				 */
				StringBuilder buf = new StringBuilder((String) tok.getValue());
				HEADER: for (;;) {
					tok = getNextToken();
					switch (tok.getType()) {
					case STRING:
						buf.append((String) tok.getValue());
						break;
					case WHITESPACE:// ignore whitespace and comments for now
						// ChK
					case CCOMMENT:
						break;
					case NL:
					case EOF:
						break HEADER;
					default:
						warning(tok, "Unexpected token on #" + "include line");
						return source_skipline(false);
					}
				}
				name = buf.toString();
				quoted = true;
			} else if (tok.getType() == HEADER) {
				name = (String) tok.getValue();
				quoted = false;
				tok = source_skipline(true);
			} else {
				error(tok, "Expected string or header, not " + tok.getText());
				switch (tok.getType()) {
				case NL:
				case EOF:
					return tok;
				default:
					/* Only if not a NL or EOF already. */
					return source_skipline(false);
				}
			}

			/* Do the inclusion. */
			include(sourceManager.getSource().getPath(), tok.getLine(), name,
					quoted, next);

			/*
			 * 'tok' is the 'nl' after the include. We use it after the #line
			 * directive.
			 */
			if (getFeature(Feature.LINEMARKERS))
				return new OutputHelper().line_token(1, sourceManager
						.getSource().getName(), " 1");
			return tok;
		} finally {
			lexer.setInclude(false);
		}
	}

	protected void pragma(Token name, List<Token> value) throws IOException,
			LexerException {
		warning(name, "Unknown #" + "pragma: " + name.getText());
	}

	private Token parse_pragma() throws IOException, LexerException {
		Token name;

		NAME: for (;;) {
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
		VALUE: for (;;) {
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
		ERROR: for (;;) {
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
		return new Token(P_LINE, pptok.getLine(), pptok.getColumn(), buf
				.toString(), null);
	}

	/**
	 * hack to temporarily disable expansion inside defined() statements: (a)
	 * every time a "defined" is found this counter is reset (b) every time a
	 * token is found this counter is increased. (c) every time the counter is 1
	 * and the token is "(" the counter is reset (d) if the counter is 1, the
	 * argument is not expanded
	 * 
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
	 */
	private Token expanded_token(boolean inlineCppExpression,
			boolean hack_definedActivated) throws IOException, LexerException {
		for (;;) {
			Token tok = retrieveTokenFromSource();
			// System.out.println("Source token is " + tok);
			if (tok.getText().equals("defined")
					|| (hack_definedCounter == 0 && tok.getText().equals("(")))
				hack_definedCounter = 0;
			else
				hack_definedCounter++;
			if (tok.getType() == IDENTIFIER
					&& (!hack_definedActivated || hack_definedCounter != 1)) {
				MacroExpansion[] m = macros.getApplicableMacroExpansions(tok
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
		} while (isWhite(tok));
		return tok;
	}

	private Token expr_token = null;

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

	private FeatureExpr parse_featureExpr(int priority) throws IOException,
			LexerException {
		/*
		 * System.out.flush(); (new Exception("expr(" + priority +
		 * ") called")).printStackTrace(); System.err.flush();
		 */

		Token tok = expr_token(true);
		FeatureExpr lhs, rhs;

		// System.out.println("Expr lhs token is " + tok);

		switch (tok.getType()) {
		case '(':
			lhs = parse_featureExpr(0);
			tok = expr_token(true);
			if (tok.getType() != ')') {
				expr_untoken(tok);
				error(tok, "missing ) in expression after " + lhs + ", found "
						+ tok + " instead");
				return FeatureExprLib.dead();
			}
			break;

		case '~':
			lhs = FeatureExprLib.l().createComplement(parse_featureExpr(11));
			break;
		case '!':
			lhs = parse_featureExpr(11).not();
			break;
		case '-':
			lhs = FeatureExprLib.l().createNeg(parse_featureExpr(11));
			break;
		case INTEGER:
			lhs = FeatureExprLib.l().createInteger(((Number) tok.getValue())
					.longValue());
			break;
		case CHARACTER:
			lhs = FeatureExprLib.l().createCharacter((Character) tok.getValue());
			break;
		case IDENTIFIER:
			if (tok.getText().equals("BASE"))
				lhs = FeatureExprLib.base();
			else if (tok.getText().equals("DEAD"))
				lhs = FeatureExprLib.dead();
			else if (tok.getText().equals("__IF__")) {
				lhs = parse_ifExpr();
			} else if (tok.getText().equals("defined")) {
				lhs = parse_definedExpr();
			} else {

				if (isPotentialFlag(tok.getText())
						&& warnings.contains(Warning.UNDEF))
					warning(tok, "Undefined token '" + tok.getText()
							+ "' encountered in conditional.");
				lhs = FeatureExprLib.dead();
			}
			break;

		default:
			expr_untoken(tok);
			error(tok, "Bad token in expression: " + tok.getText());
			return FeatureExprLib.dead();
		}

		EXPR: for (;;) {
			// System.out.println("expr: lhs is " + lhs + ", pri = " +
			// priority);
			Token op = expr_token(true);
			int pri = expr_priority(op); /* 0 if not a binop. */
			if (pri == 0 || priority >= pri) {
				expr_untoken(op);
				break EXPR;
			}
			rhs = parse_featureExpr(pri);
			// System.out.println("rhs token is " + rhs);
			switch (op.getType()) {
			case '/':
				lhs = FeatureExprLib.l().createDivision(lhs, rhs);
				// if (rhs == 0) {
				// error(op, "Division by zero");
				// lhs = 0;
				// } else {
				// lhs = lhs / rhs;
				// }
				break;
			case '%':
				lhs = FeatureExprLib.l().createModulo(lhs, rhs);
				// if (rhs == 0) {
				// error(op, "Modulus by zero");
				// lhs = 0;
				// } else {
				// lhs = lhs % rhs;
				// }
				break;
			case '*':
				lhs = FeatureExprLib.l().createMult(lhs, rhs);
				break;
			case '+':
				lhs = FeatureExprLib.l().createPlus(lhs, rhs);
				break;
			case '-':
				lhs = FeatureExprLib.l().createMinus(lhs, rhs);
				break;
			case '<':
				lhs = FeatureExprLib.l().createLessThan(lhs, rhs);
				// lhs < rhs ? 1 : 0;
				break;
			case '>':
				lhs = FeatureExprLib.l().createGreaterThan(lhs, rhs);// lhs >
				// rhs ?
				// 1 : 0;
				break;
			case '&':
				lhs = FeatureExprLib.l().createBitAnd(lhs, rhs);// lhs & rhs;
				break;
			case '^':
				lhs = FeatureExprLib.l().createPwr(lhs, rhs);// lhs ^ rhs;
				break;
			case '|':
				lhs = FeatureExprLib.l().createBitOr(lhs, rhs);// lhs | rhs;
				break;

			case LSH:
				lhs = FeatureExprLib.l().createShiftLeft(lhs, rhs);// lhs <<
				// rhs;
				break;
			case RSH:
				lhs = FeatureExprLib.l().createShiftRight(lhs, rhs);// lhs >>
				// rhs;
				break;
			case LE:
				lhs = FeatureExprLib.l().createLessThanEquals(lhs, rhs);// lhs
				// <=
				// rhs ?
				// 1 :
				// 0;
				break;
			case GE:
				lhs = FeatureExprLib.l().createGreaterThanEquals(lhs, rhs);// lhs
				// >=
				// rhs ?
				// 1 :
				// 0;
				break;
			case EQ:
				lhs = FeatureExprLib.l().createEquals(lhs, rhs);// lhs == rhs
				// ?
				// 1 :
				// 0;
				break;
			case NE:
				lhs = FeatureExprLib.l().createNotEquals(lhs, rhs);// lhs !=
				// rhs
				// ?
				// 1 : 0;
				break;
			case LAND:
				lhs = lhs.and(rhs);// (lhs != 0) && (rhs
				// != 0) ? 1 : 0;
				break;
			case LOR:
				lhs = lhs.or(rhs);// (lhs != 0) || (rhs
				// != 0) ? 1 : 0;
				break;

			case '?':
				lhs = parse_qifExpr(lhs, rhs);
				break;

			default:
				error(op, "Unexpected operator " + op.getText());
				return FeatureExprLib.dead();

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
	 * 
	 * #ifdef debug #if debug>2 #endif #endif
	 * 
	 * we assume that the flag is a feature and check whether this feature is
	 * reachable in the current state
	 */
	private boolean isPotentialFlag(String flag) {
		if (!isActive())
			return false;

		// is there a possibility that flag is undefined in the current state?
		// i.e. (state AND NOT flag) satisfiable?
		// i.e. NOT (state AND NOT flag) dead

		return !state.getFullPresenceCondition().and(
				FeatureExprLib.l().createDefinedExternal(flag).not()).isDead();
	}

	private FeatureExpr parse_definedExpr() throws IOException, LexerException {
		FeatureExpr lhs;
		Token la = source_token_nonwhite();
		boolean paren = false;
		if (la.getType() == '(') {
			paren = true;
			la = source_token_nonwhite();
		}

		// System.out.println("Core token is " + la);

		if (la.getType() != IDENTIFIER) {
			error(la, "defined() needs identifier, not " + la.getText());
			lhs = FeatureExprLib.dead();
		} else
			// System.out.println("Found macro");
			lhs = FeatureExprLib.l().createDefined(la.getText(), macros);

		if (paren) {
			la = source_token_nonwhite();
			if (la.getType() != ')') {
				expr_untoken(la);
				error(la, "Missing ) in defined()");
			}
		}
		return lhs;
	}

	private FeatureExpr parse_ifExpr() throws IOException, LexerException {
		consumeToken('(', true);
		FeatureExpr condition = parse_featureExpr(0);
		consumeToken(',', true);
		FeatureExpr thenBranch = parse_featureExpr(0);
		consumeToken(',', true);
		FeatureExpr elseBranch = parse_featureExpr(0);
		consumeToken(')', true);
		return FeatureExprLib.l().createIf(condition, thenBranch, elseBranch);
	}

	/**
	 * condition and thenBranch have already been parsed. also parse else branch
	 * 
	 * @param condition
	 * @param rhs
	 * @return
	 * @throws IOException
	 * @throws LexerException
	 */
	private FeatureExpr parse_qifExpr(FeatureExpr condition,
			FeatureExpr thenBranch) throws IOException, LexerException {
		consumeToken(':', true);
		FeatureExpr elseBranch = parse_featureExpr(0);
		return FeatureExprLib.l().createIf(condition, thenBranch, elseBranch);
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
		return new Token(WHITESPACE, tok.getLine(), tok.getColumn(),
				new String(cbuf), null);
	}

	protected final Token parse_main() throws IOException, LexerException {

		for (;;) {
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

			LEX: switch (tok.getType()) {
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
				MacroExpansion[] m = macros.getApplicableMacroExpansions(tok
						.getText(), state.getFullPresenceCondition());
				if (m.length == 0)
					return tok;
				if (!sourceManager.getSource().mayExpand(tok.getText())
						|| !tok.mayExpand())
					return tok;
				if (macro_expandToken(tok.getText(), m, tok, false))
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

			default:
				throw new InternalException("Bad token " + tok);
				// break;

			case HASH:
				tok = source_token_nonwhite();
				// (new Exception("here")).printStackTrace();
				switch (tok.getType()) {
				case NL:
					break LEX; /* Some code has #\n */
				case IDENTIFIER:
					break;
				default:
					warning(tok, "Preprocessor directive not a word "
							+ tok.getText() + ", skipping line");
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
					else
						return parse_include(false);
					// break;
				case PP_INCLUDE_NEXT:
					if (!isActive())
						return source_skipline(false);
					if (!getFeature(Feature.INCLUDENEXT)) {
						error(tok, "Directive include_next not enabled");
						return source_skipline(false);
					}
					return parse_include(true);
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
					FeatureExpr localFeatureExpr = parse_featureExpr(0);
					state.putLocalFeature(localFeatureExpr);
					tok = expr_token(true); /* unget */

					if (tok.getType() != NL)
						source_skipline(isParentActive());

					return ifdefPrinter.startIf(tok, isParentActive(), state
							.getLocalFeatureExpr(), state
							.getFullPresenceCondition());

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
						FeatureExpr localFeaturExpr = parse_featureExpr(0);
						state = oldState;
						state.processElIf();
						state.putLocalFeature(localFeaturExpr);
						tok = expr_token(true); /* unget */

						if (tok.getType() != NL)
							source_skipline(isParentActive());

						return ifdefPrinter.startElIf(tok, isParentActive(),
								state.getLocalFeatureExpr(), state
										.getFullPresenceCondition());

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
								state.getLocalFeatureExpr(), state
										.getFullPresenceCondition());
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
						state.putLocalFeature(localFeatureExpr2);
						// return

						if (tok.getType() != NL)
							source_skipline(isParentActive());

						return ifdefPrinter.startIf(tok, isParentActive(),
								state.getLocalFeatureExpr(), state
										.getFullPresenceCondition());
					}
					// break;

				case PP_IFNDEF:
					push_state();
					tok = source_token_nonwhite();
					if (tok.getType() != IDENTIFIER) {
						error(tok, "Expected identifier, not " + tok.getText());
						return source_skipline(false);
					} else {
						state.putLocalFeature(parse_ifndefExpr(tok.getText()));
						if (tok.getType() != NL)
							source_skipline(isParentActive());

						return ifdefPrinter.startIf(tok, isParentActive(),
								state.getLocalFeatureExpr(), state
										.getFullPresenceCondition());

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

			}
		}
	}

	private FeatureExpr parse_ifndefExpr(String feature) {
		return parse_ifdefExpr(feature).not();
	}

	private FeatureExpr parse_ifdefExpr(String feature) {
		return FeatureExprLib.l().createDefined(feature, macros);
	}

	private Token getNextNonwhiteToken() throws IOException, LexerException {
		Token tok;
		do {
			tok = parse_main();
		} while (isWhite(tok));
		return tok;
	}

	/**
	 * Returns the next preprocessor token.
	 * 
	 * @see Token
	 * @throws LexerException
	 *             if a preprocessing error occurs.
	 * @throws InternalException
	 *             if an unexpected error condition arises.
	 */
	public Token getNextToken() throws IOException, LexerException {
		Token tok = parse_main();
		tok.setFeature(state.getFullPresenceCondition());
		if (getFeature(Feature.DEBUG))
			System.err.println("pp: Returning " + tok);
		return tok;
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
			if (getFeature(Feature.DEBUG))
				System.err.println("Returning unget token " + tok);
			return tok;
		}
		return sourceManager.getNextToken();
	}

	public Source getSource() {
		return sourceManager.getSource();

	}

}
