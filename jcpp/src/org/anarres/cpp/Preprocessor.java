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

import de.fosd.typechef.featureexpr.*;

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

public class Preprocessor implements Closeable {
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
	};

	private List<Source> inputs;

	/* The fundamental engine. */
	private MacroContext macros = new MacroContext();
	private State state;
	private Source source;

	/* Miscellaneous support. */
	private int counter;

	/* Support junk to make it work like cpp */
	private List<String> quoteincludepath; /* -iquote */
	private List<String> sysincludepath; /* -I */
	private List<String> frameworkspath;
	private Set<Feature> features;
	private Set<Warning> warnings;
	private VirtualFileSystem filesystem;
	private PreprocessorListener listener;

	public Preprocessor() {
		this.inputs = new ArrayList<Source>();

		macros = macros.define("__LINE__", new BaseFeature(),
				new MacroData(INTERNAL)).define("__FILE__", new BaseFeature(),
				new MacroData(INTERNAL)).define("__COUNTER__",
				new BaseFeature(), new MacroData(INTERNAL));

		state = new State();
		this.source = null;

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
		Source s = source;
		while (s != null) {
			// s.setListener(listener);
			s.init(this);
			s = s.getParent();
		}
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
		inputs.add(source);
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
			listener.handleError(source, line, column, msg);
		else
			throw new LexerException("Error at " + line + ":" + column + ": "
					+ msg);
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
			listener.handleWarning(source, line, column, msg);
		else
			throw new LexerException("Warning at " + line + ":" + column + ": "
					+ msg);
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
			throw new LexerException("Cannot redefine name 'defined'");
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
	 * This is a convnience method, and is equivalent to
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

	/* Sources */

	/**
	 * Returns the top Source on the input stack.
	 * 
	 * @see Source
	 * @see #push_source(Source,boolean)
	 * @see #pop_source()
	 */
	protected Source getSource() {
		return source;
	}

	/**
	 * Pushes a Source onto the input stack.
	 * 
	 * @see #getSource()
	 * @see #pop_source()
	 */
	protected void push_source(Source source, boolean autopop) {
		source.init(this);
		source.setParent(this.source, autopop);
		// source.setListener(listener);
		if (listener != null)
			listener.handleSourceChange(this.source, "suspend");
		this.source = source;
		if (listener != null)
			listener.handleSourceChange(this.source, "push");
	}

	/**
	 * Pops a Source from the input stack.
	 * 
	 * @see #getSource()
	 * @see #push_source(Source,boolean)
	 */
	protected void pop_source() throws IOException {
		if (listener != null)
			listener.handleSourceChange(this.source, "pop");
		Source s = this.source;
		this.source = s.getParent();
		/* Always a noop unless called externally. */
		s.close();
		if (listener != null && this.source != null)
			listener.handleSourceChange(this.source, "resume");
	}

	/* Source tokens */

	private Token source_token;

	/*
	 * XXX Make this include the NL, and make all cpp directives eat their own
	 * NL.
	 */
	private Token line_token(int line, String name, String extra) {
		StringBuilder buf = new StringBuilder();
		buf.append("#line ").append(line).append(" \"");
		/* XXX This call to escape(name) is correct but ugly. */
		MacroTokenSource.escape(buf, name);
		buf.append("\"").append(extra).append("\n");
		return new Token(P_LINE, line, 0, buf.toString(), null);
	}

	private String if_tokenStr(FeatureExpr FeatureExpr) {
		StringBuilder buf = new StringBuilder();
		buf.append("#if ").append(FeatureExpr).append("\n");
		return buf.toString();
	}

	private String elif_tokenStr(FeatureExpr FeatureExpr) {
		StringBuilder buf = new StringBuilder();
		buf.append("#elif ").append(FeatureExpr).append("\n");
		return buf.toString();
	}

	private String endif_tokenStr() {
		return "#endif\n";
	}

	private String else_tokenStr() {
		return "#else\n";
	}

	private Token if_token(int line, FeatureExpr featureExpr) {
		return new Token(P_LINE, line, 0, if_tokenStr(featureExpr), null);
	}

	private Token elif_token(int line, FeatureExpr FeatureExpr) {
		StringBuilder buf = new StringBuilder();
		buf.append("#endif\n#if ").append(FeatureExpr).append("\n");
		return new Token(P_LINE, line, 0, buf.toString(), null);
	}

	private Token endif_token(int line) {
		return new Token(P_LINE, line, 0, endif_tokenStr(), null);
	}

	private Token source_token() throws IOException, LexerException {
		if (source_token != null) {
			Token tok = source_token;
			source_token = null;
			if (getFeature(Feature.DEBUG))
				System.err.println("Returning unget token " + tok);
			return tok;
		}

		for (;;) {
			Source s = getSource();
			if (s == null) {
				if (inputs.isEmpty())
					return new Token(EOF);
				Source t = inputs.remove(0);
				push_source(t, true);
				if (getFeature(Feature.LINEMARKERS))
					return line_token(t.getLine(), t.getName(), " 1");
				continue;
			}
			Token tok = s.token();
			/* XXX Refactor with skipline() */
			if (tok.getType() == EOF && s.isAutopop()) {
				// System.out.println("Autopop " + s);
				pop_source();
				Source t = getSource();
				if (getFeature(Feature.LINEMARKERS) && s.isNumbered()
						&& t != null) {
					/*
					 * We actually want 'did the nested source contain a newline
					 * token', which isNumbered() approximates. This is not
					 * perfect, but works.
					 */
					return line_token(t.getLine() + 1, t.getName(), " 2");
				}
				continue;
			}
			if (getFeature(Feature.DEBUG))
				System.err.println("Returning fresh token " + tok);
			return tok;
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
			tok = source_token();
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
			pop_source();
			Source t = getSource();
			if (getFeature(Feature.LINEMARKERS) && s.isNumbered() && t != null) {
				/*
				 * We actually want 'did the nested source contain a newline
				 * token', which isNumbered() approximates. This is not perfect,
				 * but works.
				 */
				return line_token(t.getLine() + 1, t.getName(), " 2");
			}
		}
		return tok;
	}

	/**
	 * processes and expands a macro.
	 * 
	 * @param inlineCondition
	 *            if false alternatives are replaced by #ifdef-#elif statements
	 *            in different lines. if true, alternatives are replaced by
	 *            expressions insided a line in the form
	 *            "__if__(FeatureExpr,thenClause,elseClause)". such __if__
	 *            statements can be nested
	 * */
	private boolean macro(String macroName, MacroExpansion[] macroExpansions,
			Token orig, boolean inlineCondition) throws IOException,
			LexerException {
		Token tok;
		List<Token> originalTokens = new ArrayList<Token>();
		originalTokens.add(orig);
		List<Argument> args;
		assert macroExpansions.length > 0;

		// check compatible macros
		MacroData firstMacro = ((MacroData) macroExpansions[0].getExpansion());
		int argCount = firstMacro.getArgs();
		boolean isVariadic = ((MacroData) macroExpansions[0].getExpansion())
				.isVariadic();
		for (int i = 1; i < macroExpansions.length; i++) {
			MacroData macro = ((MacroData) macroExpansions[0].getExpansion());
			if (macro.getArgs() != argCount || macro.isVariadic() != isVariadic)
				error(orig,
						"Multiple alternative macros with different signatures not yet supported. "
								+ macro.getText() + "/" + firstMacro.getText());
		}

		// attempt to parse all alternative macros in parallel (when all have
		// the same parameters)
		if (firstMacro.isFunctionLike()) {
			OPEN: for (;;) {
				tok = source_token();
				originalTokens.add(tok);
				// System.out.println("pp: open: token is " + tok);
				switch (tok.getType()) {
				case WHITESPACE: /* XXX Really? */
				case CCOMMENT:
				case CPPCOMMENT:
				case NL:
					break; /* continue */
				case '(':
					break OPEN;
				default:
					source_untoken(tok);
					originalTokens.remove(originalTokens.size() - 1);
					return false;
				}
			}

			// tok = expanded_token_nonwhite();
			tok = source_token_nonwhite();
			originalTokens.add(tok);

			/*
			 * We either have, or we should have args. This deals elegantly with
			 * the case that we have one empty arg.
			 */
			if (tok.getType() != ')' || firstMacro.getArgs() > 0) {
				args = new ArrayList<Argument>();

				Argument arg = new Argument();
				int depth = 0;
				boolean space = false;

				ARGS: for (;;) {
					// System.out.println("pp: arg: token is " + tok);
					switch (tok.getType()) {
					case EOF:
						error(tok, "EOF in macro args");
						return false;

					case ',':
						if (depth == 0) {
							if (firstMacro.isVariadic() &&
							/* We are building the last arg. */
							args.size() == firstMacro.getArgs() - 1) {
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
					tok = source_token();
					originalTokens.add(tok);
				}
				/*
				 * space may still be true here, thus trailing space is stripped
				 * from arguments.
				 */

				if (args.size() != firstMacro.getArgs()) {
					error(tok, "macro " + macroName + " has "
							+ firstMacro.getArgs() + " parameters "
							+ "but given " + args.size() + " args");
					/*
					 * We could replay the arg tokens, but I note that GNU cpp
					 * does exactly what we do, i.e. output the macro name and
					 * chew the args.
					 */
					return false;
				}

				/*
				 * for (Argument a : args) a.expand(this);
				 */

				for (int i = 0; i < args.size(); i++) {
					args.get(i).expand(this);
				}

				// System.out.println("Macro " + m + " args " + args);
			} else {
				/* nargs == 0 and we (correctly) got () */
				args = null;
			}

		} else {
			/* Macro without args. */
			args = null;
		}

		if (macroName.equals("__LINE__")) {
			push_source(new FixedTokenSource(new Token[] { new Token(INTEGER,
					orig.getLine(), orig.getColumn(), String.valueOf(orig
							.getLine()), Integer.valueOf(orig.getLine())) }),
					true);
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
			push_source(new FixedTokenSource(new Token[] { new Token(STRING,
					orig.getLine(), orig.getColumn(), text, text) }), true);
		} else if (macroName.equals("__COUNTER__")) {
			/*
			 * This could equivalently have been done by adding a special Macro
			 * subclass which overrides getTokens().
			 */
			int value = this.counter++;
			push_source(new FixedTokenSource(new Token[] { new Token(INTEGER,
					orig.getLine(), orig.getColumn(), String.valueOf(value),
					Integer.valueOf(value)) }), true);
		} else {
			if (macroExpansions.length == 1)
				push_source(new MacroTokenSource(macroName,firstMacro, args), true);
			// expand all alternative macros
			else {
				if (inlineCondition)
					macroExpandAlternativesInline(macroName,macroExpansions, args,
							originalTokens);
				else
					macroExpandAlternatives(macroName, macroExpansions, args,
							originalTokens);
			}
		}

		return true;
	}

	private void macroExpandAlternatives(String macroName, MacroExpansion[] macroExpansions,
			List<Argument> args, List<Token> originalTokens) throws IOException {
		List<Source> resultList = new ArrayList<Source>();
		for (int i = macroExpansions.length - 1; i >= 0; i--) {
			FeatureExpr feature = macroExpansions[i].getFeature();
			MacroData macroData = (MacroData) macroExpansions[i].getExpansion();

			if (i == macroExpansions.length - 1)
				resultList.add(new UnnumberedStringLexerSource("\n"
						+ if_tokenStr(feature)));
			else
				resultList.add(new UnnumberedStringLexerSource("\n"
						+ elif_tokenStr(feature)));
			resultList.add(new MacroTokenSource(macroName, macroData, args));
			if (i == 0) {
				resultList.add(new UnnumberedStringLexerSource("\n"
						+ else_tokenStr()));
				resultList.add(new FixedTokenSource(originalTokens));
				resultList.add(new UnnumberedStringLexerSource("\n"
						+ endif_tokenStr()));
			}
		}
		for (int i = resultList.size() - 1; i >= 0; i--)
			push_source(resultList.get(i), true);
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
	private void macroExpandAlternativesInline(final String macroName,
			MacroExpansion[] macroExpansions, List<Argument> args,
			List<Token> originalTokens) throws IOException {
		assert macroExpansions.length > 1;

		List<Source> resultList = new ArrayList<Source>();
		for (int i = macroExpansions.length - 1; i >= 0; i--) {
			FeatureExpr feature = macroExpansions[i].getFeature();
			MacroData macroData = (MacroData) macroExpansions[i].getExpansion();

			resultList.add(new UnnumberedStringLexerSource("__IF__("
					+ feature.toString() + ","));
			resultList.add(new MacroTokenSource(macroName, macroData, args));
			resultList.add(new UnnumberedStringLexerSource(","));
		}
		resultList.add(new FixedTokenSource(originalTokens) {
			@Override
			boolean isExpanding(String lMacroName) {
				return lMacroName.equals(macroName);
			}
		});
		String closingBrackets = "";
		for (int i = macroExpansions.length - 1; i >= 0; i--)
			closingBrackets += ")";
		resultList.add(new UnnumberedStringLexerSource(closingBrackets));
		for (int i = resultList.size() - 1; i >= 0; i--)
			push_source(resultList.get(i), true);
	}

	/**
	 * Expands an argument.
	 */
	/* I'd rather this were done lazily, but doing so breaks spec. */
	/* pp */List<Token> expand(List<Token> arg) throws IOException,
			LexerException {
		List<Token> expansion = new ArrayList<Token>();
		boolean space = false;

		push_source(new FixedTokenSource(arg), false);

		EXPANSION: for (;;) {
			Token tok = expanded_token();
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

		pop_source();

		return expansion;
	}

	/* processes a #define directive */
	private Token define() throws IOException, LexerException {
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

		tok = source_token();
		if (tok.getType() == '(') {
			tok = source_token_nonwhite();
			if (tok.getType() != ')') {
				args = new ArrayList<String>();
				ARGS: for (;;) {
					switch (tok.getType()) {
					case IDENTIFIER:
						args.add(tok.getText());
						break;
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
						break;
					case ELLIPSIS:
						tok = source_token_nonwhite();
						if (tok.getType() != ')')
							error(tok, "ellipsis must be on last argument");
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
									.valueOf(idx)));
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
							tok.getText(), Integer.valueOf(idx)));
				break;

			default:
				if (space)
					m.addToken(Token.space);
				space = false;
				paste = false;
				m.addToken(tok);
				break;
			}
			tok = source_token();
		}

		if (getFeature(Feature.DEBUG))
			System.err.println("Defined macro " + m);
		addMacro(name, state.getFullPresenceCondition(), m);

		return tok; /* NL or EOF. */
	}

	private Token undef() throws IOException, LexerException {
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
		push_source(file.getSource(), true);
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
	 */
	private void include(String parent, int line, String name, boolean quoted)
			throws IOException, LexerException {
		VirtualFile pdir = null;
		if (quoted) {
			VirtualFile pfile = filesystem.getFile(parent);
			pdir = pfile.getParentFile();
			VirtualFile ifile = pdir.getChildFile(name);
			if (include(ifile))
				return;
			if (include(quoteincludepath, name))
				return;
		}

		if (include(sysincludepath, name))
			return;

		StringBuilder buf = new StringBuilder();
		buf.append("File not found: ").append(name);
		buf.append(" in");
		if (quoted) {
			buf.append(" .").append('(').append(pdir).append(')');
			for (String dir : quoteincludepath)
				buf.append(" ").append(dir);
		}
		for (String dir : sysincludepath)
			buf.append(" ").append(dir);
		error(line, 0, buf.toString());
	}

	private Token include(boolean next) throws IOException, LexerException {
		LexerSource lexer = (LexerSource) source;
		try {
			lexer.setInclude(true);
			Token tok = token_nonwhite();

			String name;
			boolean quoted;

			if (tok.getType() == STRING) {
				/*
				 * XXX Use the original text, not the value. Backslashes must
				 * not be treated as escapes here.
				 */
				StringBuilder buf = new StringBuilder((String) tok.getValue());
				HEADER: for (;;) {
					tok = token_nonwhite();
					switch (tok.getType()) {
					case STRING:
						buf.append((String) tok.getValue());
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
			include(source.getPath(), tok.getLine(), name, quoted);

			/*
			 * 'tok' is the 'nl' after the include. We use it after the #line
			 * directive.
			 */
			if (getFeature(Feature.LINEMARKERS))
				return line_token(1, source.getName(), " 1");
			return tok;
		} finally {
			lexer.setInclude(false);
		}
	}

	protected void pragma(Token name, List<Token> value) throws IOException,
			LexerException {
		warning(name, "Unknown #" + "pragma: " + name.getText());
	}

	private Token pragma() throws IOException, LexerException {
		Token name;

		NAME: for (;;) {
			Token tok = token();
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
			tok = token();
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

	/* For #error and #warning. */
	private void error(Token pptok, boolean is_error) throws IOException,
			LexerException {
		StringBuilder buf = new StringBuilder();
		buf.append('#').append(pptok.getText()).append(' ');
		/* Peculiar construction to ditch first whitespace. */
		Token tok = source_token_nonwhite();
		ERROR: for (;;) {
			switch (tok.getType()) {
			case NL:
			case EOF:
				break ERROR;
			default:
				buf.append(tok.getText());
				break;
			}
			tok = source_token();
		}
		if (is_error)
			error(pptok, buf.toString());
		else
			warning(pptok, buf.toString());
	}

	/*
	 * This bypasses token() for #elif expressions. If we don't do this, then
	 * isActive() == false causes token() to simply chew the entire input line.
	 */
	private Token expanded_token() throws IOException, LexerException {
		for (;;) {
			Token tok = source_token();
			// System.out.println("Source token is " + tok);
			if (tok.getType() == IDENTIFIER) {
				MacroExpansion[] m = macros.getMacroExpansions(tok.getText());
				if (m.length > 0 && !source.isExpanding(tok.getText())
						&& macro(tok.getText(), m, tok, true))
					continue;
				return tok;
			}
			return tok;
		}
	}

	private Token expanded_token_nonwhite() throws IOException, LexerException {
		Token tok;
		do {
			tok = expanded_token();
			// System.out.println("expanded token is " + tok);
		} while (isWhite(tok));
		return tok;
	}

	private Token expr_token = null;

	private Token expr_token() throws IOException, LexerException {
		Token tok = expr_token;

		if (tok != null) {
			// System.out.println("ungetting");
			expr_token = null;
		} else {
			tok = expanded_token_nonwhite();
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

	private FeatureExpr parseFeatureExpr(int priority) throws IOException,
			LexerException {
		/*
		 * System.out.flush(); (new Exception("expr(" + priority +
		 * ") called")).printStackTrace(); System.err.flush();
		 */

		Token tok = expr_token();
		FeatureExpr lhs, rhs;

		// System.out.println("Expr lhs token is " + tok);

		switch (tok.getType()) {
		case '(':
			lhs = parseFeatureExpr(0);
			tok = expr_token();
			if (tok.getType() != ')') {
				expr_untoken(tok);
				error(tok, "missing ) in expression");
				return new DeadFeature();
			}
			break;

		case '~':
			lhs = new FeatureExpr$().createComplement(parseFeatureExpr(11));
			break;
		case '!':
			lhs = new Not(parseFeatureExpr(11));
			break;
		case '-':
			lhs = new FeatureExpr$().createNeg(parseFeatureExpr(11));
			break;
		case INTEGER:
			lhs = new IntegerLit(((Number) tok.getValue()).longValue());
			break;
		case CHARACTER:
			lhs = new CharacterLit((Character) tok.getValue());
			break;
		case IDENTIFIER:
			if (tok.getText().equals("BASE"))
				lhs = new BaseFeature();
			else if (tok.getText().equals("DEAD"))
				lhs = new DeadFeature();
			else if (tok.getText().equals("__IF__")) {
				lhs = parseIfExpr();
			} else if (tok.getText().equals("defined")) {
				lhs = parseDefinedExpr();
			} else {

				if (warnings.contains(Warning.UNDEF))
					warning(tok, "Undefined token '" + tok.getText()
							+ "' encountered in conditional.");
				lhs = new DeadFeature();
			}
			break;

		default:
			expr_untoken(tok);
			error(tok, "Bad token in expression: " + tok.getText());
			return new DeadFeature();
		}

		EXPR: for (;;) {
			// System.out.println("expr: lhs is " + lhs + ", pri = " +
			// priority);
			Token op = expr_token();
			int pri = expr_priority(op); /* 0 if not a binop. */
			if (pri == 0 || priority >= pri) {
				expr_untoken(op);
				break EXPR;
			}
			rhs = parseFeatureExpr(pri);
			// System.out.println("rhs token is " + rhs);
			switch (op.getType()) {
			case '/':
				lhs = new FeatureExpr$().createDivision(lhs, rhs);
				// if (rhs == 0) {
				// error(op, "Division by zero");
				// lhs = 0;
				// } else {
				// lhs = lhs / rhs;
				// }
				break;
			case '%':
				lhs = new FeatureExpr$().createModulo(lhs, rhs);
				// if (rhs == 0) {
				// error(op, "Modulus by zero");
				// lhs = 0;
				// } else {
				// lhs = lhs % rhs;
				// }
				break;
			case '*':
				lhs = new FeatureExpr$().createMult(lhs, rhs);
				break;
			case '+':
				lhs = new FeatureExpr$().createPlus(lhs, rhs);
				break;
			case '-':
				lhs = new FeatureExpr$().createMinus(lhs, rhs);
				break;
			case '<':
				lhs = new FeatureExpr$().createLessThan(lhs, rhs);
				// lhs < rhs ? 1 : 0;
				break;
			case '>':
				lhs = new FeatureExpr$().createGreaterThan(lhs, rhs);// lhs >
				// rhs ?
				// 1 : 0;
				break;
			case '&':
				lhs = new FeatureExpr$().createBitAnd(lhs, rhs);// lhs & rhs;
				break;
			case '^':
				lhs = new FeatureExpr$().createPwr(lhs, rhs);// lhs ^ rhs;
				break;
			case '|':
				lhs = new FeatureExpr$().createBitOr(lhs, rhs);// lhs | rhs;
				break;

			case LSH:
				lhs = new FeatureExpr$().createShiftLeft(lhs, rhs);// lhs <<
				// rhs;
				break;
			case RSH:
				lhs = new FeatureExpr$().createShiftRight(lhs, rhs);// lhs >>
				// rhs;
				break;
			case LE:
				lhs = new FeatureExpr$().createLessThanEquals(lhs, rhs);// lhs
				// <=
				// rhs ?
				// 1 :
				// 0;
				break;
			case GE:
				lhs = new FeatureExpr$().createGreaterThanEquals(lhs, rhs);// lhs
				// >=
				// rhs ?
				// 1 :
				// 0;
				break;
			case EQ:
				lhs = new FeatureExpr$().createEquals(lhs, rhs);// lhs == rhs ?
				// 1 :
				// 0;
				break;
			case NE:
				lhs = new FeatureExpr$().createNotEquals(lhs, rhs);// lhs != rhs
				// ?
				// 1 : 0;
				break;
			case LAND:
				lhs = new And(lhs, rhs);// (lhs != 0) && (rhs
				// != 0) ? 1 : 0;
				break;
			case LOR:
				lhs = new Or(lhs, rhs);// (lhs != 0) || (rhs
				// != 0) ? 1 : 0;
				break;

			case '?':
				/* XXX Handle this? */

			default:
				error(op, "Unexpected operator " + op.getText());
				return new DeadFeature();

			}
		}

		/*
		 * System.out.flush(); (new Exception("expr returning " +
		 * lhs)).printStackTrace(); System.err.flush();
		 */
		// System.out.println("expr returning " + lhs);

		return lhs;
	}

	private FeatureExpr parseDefinedExpr() throws IOException, LexerException {
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
			lhs = new DeadFeature();
		} else
			// System.out.println("Found macro");
			lhs = new FeatureExpr$().createDefined(la.getText(), macros);

		if (paren) {
			la = source_token_nonwhite();
			if (la.getType() != ')') {
				expr_untoken(la);
				error(la, "Missing ) in defined()");
			}
		}
		return lhs;
	}

	private FeatureExpr parseIfExpr() throws IOException, LexerException {
		FeatureExpr lhs;

		consumeToken('(');
		FeatureExpr condition = parseFeatureExpr(0);
		consumeToken(',');
		FeatureExpr thenBranch = parseFeatureExpr(0);
		consumeToken(',');
		FeatureExpr elseBranch = parseFeatureExpr(0);
		consumeToken(')');
		return new IfExpr(condition, thenBranch, elseBranch);
	}

	private void consumeToken(int tokenType) throws IOException, LexerException {
		Token la = expr_token();
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
				new String(cbuf));
	}

	private final Token _token() throws IOException, LexerException {

		for (;;) {
			Token tok;
			if (!isActive()) {
				try {
					/* XXX Tell lexer to ignore warnings. */
					source.setActive(false);
					tok = source_token();
				} finally {
					/* XXX Tell lexer to stop ignoring warnings. */
					if (source != null)
						source.setActive(true);
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
				tok = source_token();
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
				MacroExpansion[] m = macros.getMacroExpansions(tok.getText());
				if (m.length == 0)
					return tok;
				if (source.isExpanding(tok.getText()))
					return tok;
				if (macro(tok.getText(), m, tok, false))
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
					error(tok, "Preprocessor directive not a word "
							+ tok.getText());
					return source_skipline(false);
				}
				Integer _ppcmd = ppcmds.get(tok.getText());
				if (_ppcmd == null) {
					error(tok, "Unknown preprocessor directive "
							+ tok.getText());
					return source_skipline(false);
				}
				int ppcmd = _ppcmd.intValue();

				PP: switch (ppcmd) {

				case PP_DEFINE:
					if (!isActive())
						return source_skipline(false);
					else
						return define();
					// break;

				case PP_UNDEF:
					if (!isActive())
						return source_skipline(false);
					else
						return undef();
					// break;

				case PP_INCLUDE:
					if (!isActive())
						return source_skipline(false);
					else
						return include(false);
					// break;
				case PP_INCLUDE_NEXT:
					if (!isActive())
						return source_skipline(false);
					if (!getFeature(Feature.INCLUDENEXT)) {
						error(tok, "Directive include_next not enabled");
						return source_skipline(false);
					}
					return include(true);
					// break;

				case PP_WARNING:
				case PP_ERROR:
					if (!isActive())
						return source_skipline(false);
					else
						error(tok, ppcmd == PP_ERROR);
					break;

				case PP_IF:
					push_state();
					if (!isActive()) {
						return source_skipline(false);
					}
					expr_token = null;
					state.putLocalFeature(parseFeatureExpr(0));
					tok = expr_token(); /* unget */

					if (tok.getType() != NL)
						source_skipline(true);

					return if_token(tok.getLine(), state.getLocalFeatureExpr());
					// break;

				case PP_ELIF:
					if (state.sawElse()) {
						error(tok, "#elif after #" + "else");
						return source_skipline(false);
					} else {
						expr_token = null;
						// state.setActive(expr(0) != 0);
						state.putLocalFeature(parseFeatureExpr(0));
						tok = expr_token(); /* unget */
						if (tok.getType() != NL)
							source_skipline(true);
						return elif_token(tok.getLine(), state
								.getLocalFeatureExpr());
					}
					// break;

				case PP_ELSE:
					if (false)
						/* Check for 'if' */;
					else if (state.sawElse()) {
						error(tok, "#" + "else after #" + "else");
						return source_skipline(false);
					} else {
						state.setSawElse();
						source_skipline(warnings.contains(Warning.ENDIF_LABELS));
						return elif_token(tok.getLine(), state
								.getLocalFeatureExpr());
					}
					// break;

				case PP_IFDEF:
					push_state();
					if (!isActive()) {
						return source_skipline(false);
					} else {
						tok = source_token_nonwhite();
						// System.out.println("ifdef " + tok);
						if (tok.getType() != IDENTIFIER) {
							error(tok, "Expected identifier, not "
									+ tok.getText());
							return source_skipline(false);
						} else {
							state.putLocalFeature(parseIfDef(tok.getText()));
							// return
							source_skipline(true);
							return if_token(tok.getLine(), state
									.getLocalFeatureExpr());
						}
					}
					// break;

				case PP_IFNDEF:
					push_state();
					if (!isActive()) {
						return source_skipline(false);
					} else {
						tok = source_token_nonwhite();
						if (tok.getType() != IDENTIFIER) {
							error(tok, "Expected identifier, not "
									+ tok.getText());
							return source_skipline(false);
						} else {
							state.putLocalFeature(parseIfNDef(tok.getText()));
							source_skipline(true);
							return if_token(tok.getLine(), state
									.getLocalFeatureExpr());
						}
					}
					// break;

				case PP_ENDIF:
					pop_state();
					source_skipline(warnings.contains(Warning.ENDIF_LABELS));
					return endif_token(tok.getLine());
					// break;

				case PP_LINE:
					return source_skipline(false);
					// break;

				case PP_PRAGMA:
					if (!isActive())
						return source_skipline(false);
					return pragma();
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

	private FeatureExpr parseIfNDef(String feature) {
		return new Not(parseIfDef(feature));
	}

	private FeatureExpr parseIfDef(String feature) {
		return new FeatureExpr$().createDefined(feature, macros);
	}

	private Token token_nonwhite() throws IOException, LexerException {
		Token tok;
		do {
			tok = _token();
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
	public Token token() throws IOException, LexerException {
		Token tok = _token();
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
		{
			Source s = source;
			while (s != null) {
				s.close();
				s = s.getParent();
			}
		}
		for (Source s : inputs) {
			s.close();
		}
	}

}
