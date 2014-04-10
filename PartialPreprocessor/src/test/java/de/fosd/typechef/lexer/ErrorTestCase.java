package de.fosd.typechef.lexer;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static de.fosd.typechef.lexer.Token.EOF;
import static de.fosd.typechef.lexer.Token.INVALID;
import static org.junit.Assert.*;

public class ErrorTestCase {

    private boolean testError(Preprocessor p)
            throws LexerException,
            IOException {
        for (; ; ) {
            Token tok = p.getNextToken();
            if (tok.getType() == EOF)
                break;
            if (tok.getType() == INVALID)
                return true;
        }
        return false;
    }

    private void testError(String input) throws Exception {
        StringLexerSource sl;
        PreprocessorListener pl;
        Preprocessor p;

        /* Without a PreprocessorListener, throws an exception. */
        sl = new StringLexerSource(input, true);
        p = new Preprocessor();
        p.addFeature(Feature.CSYNTAX);
        p.addInput(sl);
        try {
            assertTrue(testError(p));
            fail("Lexing unexpectedly succeeded without listener.");
        } catch (LexerException e) {
            /* required */
        }

        /* With a PreprocessorListener, records the error. */
        sl = new StringLexerSource(input, true);
        p = new Preprocessor();
        p.addFeature(Feature.CSYNTAX);
        p.addInput(sl);
        pl = new PreprocessorListener(p, new LexerFrontend.DefaultLexerOptions(null,true,null));
        p.setListener(pl);
        assertNotNull("CPP has listener", p.getListener());
        assertTrue(testError(p));
        assertTrue("Listener has errors", pl.getErrors() > 0);

        /* Without CSYNTAX, works happily. */
        sl = new StringLexerSource(input, true);
        p = new Preprocessor();
        p.addInput(sl);
        assertTrue(testError(p));
    }

    @Test
    @Ignore
    public void testErrors() throws Exception {
        testError("\"");
        testError("'");
        testError("''");
    }

    @Test
    public void testNone() {
    }

}
