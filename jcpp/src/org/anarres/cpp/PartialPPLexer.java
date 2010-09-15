package org.anarres.cpp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.fosd.typechef.featureexpr.FeatureExpr;

/**
 * helper class that returns a list of tokens (each with presence condition),
 * but without any #ifdef tokens, whitespace or such
 * 
 * to be extended to mirror the power of Main.java
 * 
 * @author kaestner
 * 
 */
public class PartialPPLexer {

	public boolean debug = false;

	public List<Token> parse(String code, String folder) throws LexerException,
			IOException {
		return parse(new StringLexerSource(code, true), folder);
	}

	public List<Token> parse(Source source, String folder)
			throws LexerException, IOException {
		Preprocessor pp = new Preprocessor();
		pp.addFeature(Feature.DIGRAPHS);
		pp.addFeature(Feature.TRIGRAPHS);
		pp.addFeature(Feature.LINEMARKERS);
		pp.addWarnings(Warning.allWarnings());
		pp.setListener(new PreprocessorListener(pp) {
			@Override
			public void handleWarning(Source source, int line, int column,
					String msg) throws LexerException {
				super.handleWarning(source, line, column, msg);
				throw new LexerException(msg);
			}
		});
		pp.addMacro("__JCPP__", new FeatureExpr().base());

		// include path
		if (folder != null)
			pp.getSystemIncludePath().add(folder);

		pp.addInput(source);

		ArrayList<Token> result = new ArrayList<Token>();
		for (;;) {
			Token tok = pp.getNextToken();
			if (tok == null)
				break;
			if (tok.getType() == Token.EOF)
				break;

			if (tok.getType() != Token.P_LINE
					&& tok.getType() != Token.WHITESPACE
					&& tok.getType() != Token.NL
					&& tok.getType() != Token.INVALID
					&& tok.getType() != Token.P_IF
					&& tok.getType() != Token.CCOMMENT
					&& tok.getType() != Token.CPPCOMMENT
					&& tok.getType() != Token.P_ENDIF
					&& tok.getType() != Token.P_ELIF)
				result.add(tok);
			if (debug)
				System.out.print(tok.getText());
		}
		return result;
	}
}
