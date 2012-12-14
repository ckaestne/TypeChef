package de.fosd.typechef.xtclexer;


import de.fosd.typechef.VALexer;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.lexer.Feature;
import de.fosd.typechef.lexer.PreprocessorListener;
import de.fosd.typechef.lexer.Token;
import de.fosd.typechef.lexer.Warning;

import java.io.IOException;
import java.util.Collection;

/**
 * wrapper class around the Xtc/SuperC preprocessor to have a somewhat compatible interface with
 * the original Typechef Preprocessor
 */
public class XtcPreprocessor  implements VALexer {
    @Override
    public void addInput(LexerInput source) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void debugPreprocessorDone() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addFeature(Feature digraphs) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addWarnings(Collection<Warning> warnings) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addMacro(String jcpp__, FeatureExpr aTrue) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addSystemIncludePath(String folder) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Token getNextToken() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setListener(PreprocessorListener preprocessorListener) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
