package org.anarres.cpp;

import de.fosd.typechef.featureexpr.FeatureModel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * helper class that returns a list of tokens (each with presence condition),
 * but without any #ifdef tokens, whitespace or such
 * <p/>
 * to be extended to mirror the power of Main.java
 *
 * @author kaestner
 */
public class PartialPPLexer {

    public boolean debug = false;

    public List<Token> parse(String code, String folderPath, FeatureModel featureModel) throws LexerException,
            IOException {
        return parse(new StringLexerSource(code, true), folderPath, featureModel);
    }

    public List<Token> parseFile(String fileName, String folderPath, FeatureModel featureModel)
            throws LexerException, IOException {
        return parse(new FileLexerSource(new File(fileName)), folderPath, featureModel);
    }

    /**
     * @param stream     stream containing the data to preprocess
     * @param filePath   path of the file represented by the stream
     * @param folderPath path of the containing folder.
     * @return
     * @throws LexerException
     * @throws IOException
     */
    public List<Token> parseStream(InputStream stream, String filePath, String folderPath, FeatureModel featureModel)
            throws LexerException, IOException {
        return parse(new FileLexerSource(stream, filePath), folderPath, featureModel);
    }

    public List<Token> parse(Source source, String folderPath, FeatureModel featureModel)
            throws LexerException, IOException {
        Preprocessor pp = new Preprocessor(featureModel);
        pp.addFeature(Feature.DIGRAPHS);
        pp.addFeature(Feature.TRIGRAPHS);
        pp.addFeature(Feature.LINEMARKERS);
        pp.addWarnings(Warning.allWarnings());
        pp.setListener(new PreprocessorListener(pp) {
            // @Override
            // public void handleWarning(Source source, int line, int column,
            // String msg) throws LexerException {
            // super.handleWarning(source, line, column, msg);
            // throw new LexerException(msg);
            // }
        });
        pp.addMacro("__JCPP__", FeatureExprLib.base());

        // include path
        if (folderPath != null)
            pp.getSystemIncludePath().add(folderPath);

        pp.addInput(source);

        ArrayList<Token> result = new ArrayList<Token>();
        PrintWriter stdOut = new PrintWriter(new OutputStreamWriter(System.out));
        for (; ;) {
            Token tok = pp.getNextToken();
            if (tok == null)
                break;
            if (tok.getType() == Token.EOF)
                break;

            if (tok.getType() == Token.INVALID)
                System.err.println("Invalid token: " + tok);
            // throw new LexerException(...)

            if (isResultToken(tok))
                result.add(tok);
            if (debug)
                tok.lazyPrint(stdOut);
        }
        return result;
    }

    static boolean isResultToken(Token tok) {
        return tok.getType() != Token.P_LINE
                && tok.getType() != Token.WHITESPACE
                && !(tok.getType() != Token.P_FEATUREEXPR && tok.getText().equals("__extension__"))
                && tok.getType() != Token.NL && tok.getType() != Token.P_IF
                && tok.getType() != Token.CCOMMENT
                && tok.getType() != Token.CPPCOMMENT
                && tok.getType() != Token.P_ENDIF
                && tok.getType() != Token.P_ELIF;
    }
}
