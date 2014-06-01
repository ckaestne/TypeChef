//package de.fosd.typechef.lexer;
//
//import de.fosd.typechef.LexerToken;
//import de.fosd.typechef.featureexpr.FeatureModel;
//import de.fosd.typechef.lexer.macrotable.MacroFilter;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * helper class that returns a list of tokens (each with presence condition),
// * but without any #ifdef tokens, whitespace or such
// * <p/>
// * to be extended to mirror the power of LexerFrontend.java
// *
// * @author kaestner
// */
//public class PartialPPLexer {
//
//    public boolean debug = false;
//
//    public List<LexerToken> parse(String code, List<String> systemIncludePath, FeatureModel featureModel) throws LexerException,
//            IOException {
//        return parse(new StringLexerSource(code, true), systemIncludePath, featureModel);
//    }
//
//    public List<LexerToken> parseFile(String fileName, List<String> systemIncludePath, FeatureModel featureModel)
//            throws LexerException, IOException {
//        return parse(new FileLexerSource(new File(fileName)), systemIncludePath, featureModel);
//    }
//
//    /**
//     * @param stream     stream containing the data to preprocess
//     * @param filePath   path of the file represented by the stream
//     * @param folderPath path of the containing folder.
//     * @return
//     * @throws LexerException
//     * @throws IOException
//     */
//    public List<LexerToken> parseStream(InputStream stream, String filePath, List<String> systemIncludePath, FeatureModel featureModel)
//            throws LexerException, IOException {
//        return parse(new FileLexerSource(stream, filePath), systemIncludePath, featureModel);
//    }
//
//    public List<LexerToken> parse(Source source, List<String> systemIncludePath, FeatureModel featureModel)
//            throws LexerException, IOException {
//        Preprocessor pp = new Preprocessor(new MacroFilter(), featureModel);
//        pp.addFeature(Feature.DIGRAPHS);
//        pp.addFeature(Feature.TRIGRAPHS);
//        pp.addFeature(Feature.LINEMARKERS);
//        pp.addWarnings(Warning.allWarnings());
//        pp.setListener(new PreprocessorListener(pp) {
//            // @Override
//            // public void handleWarning(Source source, int line, int column,
//            // String msg) throws LexerException {
//            // super.handleWarning(source, line, column, msg);
//            // throw new LexerException(msg);
//            // }
//        });
//        pp.addMacro("__JCPP__", FeatureExprLib.True());
//
//        // include path
//        if (systemIncludePath != null)
//            pp.getSystemIncludePath().addAll(systemIncludePath);
//
//        pp.addInput(source);
//
//        ArrayList<LexerToken> result = new ArrayList<LexerToken>();
//        PrintWriter stdOut = new PrintWriter(new OutputStreamWriter(System.out));
//        for (; ; ) {
//            Token tok = pp.getNextToken();
//            if (tok == null)
//                break;
//            if (tok.getType() == Token.EOF)
//                break;
//
//            if (tok.getType() == Token.INVALID)
//                System.err.println("Invalid token: " + tok);
//            // throw new LexerException(...)
//
//            if (tok.isLanguageToken())
//                result.add(tok);
//            if (debug)
//                tok.lazyPrint(stdOut);
//        }
//        return result;
//    }
//
//
//}
