package de.fosd.typechef;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.lexer.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * interface to the variability-aware lexer (or partial preprocessor)
 *
 * used to abstract over differences between typechef and xtc/superc
 */
public interface VALexer {

    void addInput(LexerInput source) throws IOException;

    public static interface LexerInput {}
    public static class TextSource implements  LexerInput{
              public final String code;
        public TextSource(String code) {
            this.code=code;

        }
    }
    public static class FileSource implements  LexerInput {

        final public File file;

        public FileSource(File file) {
            this.file=file;
        }
    }
    public static class StreamSource implements  LexerInput {
        final public String filename;
        final public InputStream inputStream;

        /**
         *
         * @param inputStream
         * @param fileName for error reporting only
         */
        public StreamSource(InputStream inputStream, String fileName) {
            this.filename=fileName;
            this.inputStream=inputStream;
        }
    }


    void debugPreprocessorDone();

    void addFeature(Feature digraphs);

    void addWarnings(Collection<Warning> warnings);

    void addMacro(String jcpp__, FeatureExpr aTrue)  throws LexerException ;

    void addSystemIncludePath(String folder);

    Token getNextToken() throws IOException, LexerException;

    void setListener(PreprocessorListener preprocessorListener);
}
