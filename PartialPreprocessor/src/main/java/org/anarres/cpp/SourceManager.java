package org.anarres.cpp;

import static org.anarres.cpp.Token.EOF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * sources are organized in form of an extended stack. we start with one source
 * and when we expand a macro, we push a new source on top of the stack. when
 * expanding an item, we look up the stack whether we are already expanding this
 * item in parent sources
 *
 * how, at some points, we push multiple items to the stack that are not in a
 * parent-child relationship. therefore multiple sources can be wrapped with a
 * SourceListSource, which behaves like a single source
 *
 *
 *
 * @author ckaestne
 *
 */
public class SourceManager {

	List<Source> inputs = new ArrayList<Source>();

	private Source source = null;

	private final Preprocessor pp;

	public SourceManager(Preprocessor preprocessor) {
		this.pp = preprocessor;
	}

	/**
	 * Returns the top Source on the input stack.
	 *
	 * @see Source
	 * @see #push_source(Source,boolean)
	 * @see #pop_source()
	 */
	 Source getSource() {
		return source;
	}

	/**
	 * Pushes a Source onto the input stack. (either as new first sibling, or as
	 * new parent)
	 *
	 * @see #getSource()
	 * @see #pop_source()
	 */
	private void _push_source(Source source, boolean autopop, boolean asSibling) {
		source.init(pp);
		Source parent = this.source;
		if (asSibling)
			parent = this.source.getParent();
		source.setParent(parent, autopop);
		if (asSibling)
			source.setSibling(this.source);
		// source.setListener(listener);
		if (pp.listener != null)
			pp.listener.handleSourceChange(this.source, "suspend");
		this.source = source;
		if (pp.listener != null)
			pp.listener.handleSourceChange(this.source, "push");
	}

	/**
	 * Pushes a Source onto the input stack.
	 *
	 * @see #getSource()
	 * @see #pop_source()
	 */
	void push_source(Source source, boolean autopop) {
		_push_source(source, autopop, false);
		pp.debugSourceBegin(source,pp.state);
	}

	/**
	 * pushes a list of sources which are handled as siblings
	 *
	 * @param resultList
	 * @param b
	 */
	void push_sources(List<Source> resultList, boolean autopop) {
		ArrayList<Source> list = new ArrayList<Source>(resultList);
		Collections.reverse(list);
		for (Source s : list)
			_push_source(s, autopop, true);
	}

	/**
	 * Pops a Source from the input stack.
	 *
	 * @see #getSource()
	 * @see #push_source(Source,boolean)
	 */
	protected void pop_source() throws IOException {
		pp.debugSourceEnd(source);

		if (pp.listener != null)
			pp.listener.handleSourceChange(this.source, "pop");
		Source s = this.source;
		if (this.source.getSibling() != null)
			this.source = s.getSibling();
		else
			this.source = s.getParent();
		/* Always a noop unless called externally. */
		s.close();
		if (pp.listener != null && this.source != null)
			pp.listener.handleSourceChange(this.source, "resume");
	}

	public void addInput(Source source2) {
		inputs.add(source2);
	}

	public void reinit() {
		Source s = source;
		while (s != null) {
			s.init(pp);
			if (s.getSibling() != null)
				s = s.getSibling();
			else
				s = s.getParent();
		}

	}

	public Token getNextToken() throws IOException, LexerException {
		for (;;) {
			Source s = getSource();
			if (s == null) {
				if (inputs.isEmpty())
					return new SimpleToken(EOF, null);
				Source t = inputs.remove(0);
				push_source(t, true);
				if (pp.getFeature(Feature.LINEMARKERS))
					return Preprocessor.OutputHelper.line_token(t.getLine(), t.getName(), " 1");
				continue;
			}
			Token tok = s.token();
			/* XXX Refactor with skipline() */
			if (tok.getType() == EOF && s.isAutopop()) {
				// System.out.println("Autopop " + s);
				pop_source();
				Source t = getSource();
				if (pp.getFeature(Feature.LINEMARKERS) && s.isNumbered()
						&& t != null) {
					/*
					 * We actually want 'did the nested source contain a newline
					 * token', which isNumbered() approximates. This is not
					 * perfect, but works.
					 */
					return Preprocessor.OutputHelper.line_token(t.getLine() + 1, t.getName(), " 2");
				}
				continue;
			}
			if (pp.getFeature(Feature.DEBUG))
				System.err.println("Returning fresh token " + tok);
			pp.debug_receivedToken(source,tok);
			return tok;
		}
	}

	public void close() throws IOException {
		{
			Source s = source;
			while (s != null) {
				s.close();
				if (s.getSibling() != null)
					s = s.getSibling();
				else
					s = s.getParent();
			}
		}
		for (Source s : inputs) {
			s.close();
		}
	}

	String debug_sourceDelta(Source debugOrigSource) {
		String newSources="";
		Source _source = getSource();
		while (_source!=debugOrigSource && _source!=null){
			newSources=newSources+", "+_source.debug_getContent();
			_source=_source.getSibling();
		}

		return "["+newSources+"]";
	}

}
