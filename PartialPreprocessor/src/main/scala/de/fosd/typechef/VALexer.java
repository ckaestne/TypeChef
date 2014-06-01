package de.fosd.typechef;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureModel;
import de.fosd.typechef.lexer.Feature;
import de.fosd.typechef.lexer.LexerException;
import de.fosd.typechef.lexer.PreprocessorListener;
import de.fosd.typechef.lexer.Warning;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * interface to the variability-aware lexer (or partial preprocessor)
 * <p/>
 * used to abstract over differences between typechef and xtc/superc
 */
public interface VALexer {

    void addInput(LexerInput source) throws IOException;


    public static interface LexerFactory {
        public VALexer create(FeatureModel featureModel);
    }

    public static interface LexerInput {
    }

    public static class TextSource implements LexerInput {
        public final String code;

        public TextSource(String code) {
            this.code = code;

        }
    }

    public static class FileSource implements LexerInput {

        final public File file;

        public FileSource(File file) {
            this.file = file;
        }
    }

    public static class StreamSource implements LexerInput {
        final public String filename;
        final public InputStream inputStream;

        /**
         * @param inputStream
         * @param fileName    used to find headers in the same directory and for error reporting
         */
        public StreamSource(InputStream inputStream, String fileName) {
            this.filename = fileName;
            this.inputStream = inputStream;
        }
    }


    void debugPreprocessorDone();

    void addFeature(Feature feature);

    void addWarning(Warning warning);

    void addMacro(String macro, FeatureExpr fexpr) throws LexerException;

    void addMacro(String macro, FeatureExpr fexpr, String value) throws LexerException;

    void removeMacro(String macro, FeatureExpr fexpr);

    void addSystemIncludePath(String folder);

    void addQuoteIncludePath(String folder);

    public List<String> getSystemIncludePath();//readonly

    public List<String> getQuoteIncludePath();//readonly

    LexerToken getNextToken() throws IOException, LexerException;

    void setListener(PreprocessorListener preprocessorListener);

    FeatureModel getFeatureModel();


    // debugging only
    void printSourceStack(PrintStream err);

    //typechef specific for .dbgSrc and .macroDbg files
    void openDebugFiles(String lexOutputFile);
}
