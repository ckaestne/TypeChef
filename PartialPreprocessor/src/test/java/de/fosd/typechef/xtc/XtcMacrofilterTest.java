package de.fosd.typechef.xtc;

import de.fosd.typechef.jcpp.AbstractCheckTests;
import de.fosd.typechef.lexer.LexerException;
import de.fosd.typechef.lexer.macrotable.MacroFilter;
import org.junit.Test;

import java.io.IOException;

/**
 * ensure that macrofilters are respected and only CONFIG_ values are evaluated
 */
public class XtcMacrofilterTest extends AbstractCheckTests {

    private boolean isXtc;

    @Override
    protected boolean useXtc() {
        return isXtc;
    }

    @Override
    protected MacroFilter useMacroFilter() {
        return new MacroFilter().setPrefixOnlyFilter("CONFIG_");
    }

    @Test
    public void testMacrofilterXtc() throws LexerException, IOException {
        isXtc=true;
        testFile("macrofilter/test.c");
    }
    @Test
    public void testMacrofilterJcpp() throws LexerException, IOException {
        isXtc=false;
        testFile("macrofilter/test.c");
    }
}
