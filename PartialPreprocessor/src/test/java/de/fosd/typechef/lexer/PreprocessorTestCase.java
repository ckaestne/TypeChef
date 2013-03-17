package de.fosd.typechef.lexer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import static de.fosd.typechef.lexer.Token.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PreprocessorTestCase {
    private OutputStreamWriter writer;
    private Preprocessor p;

    @Before
    public void setUp() throws Exception {
        final PipedOutputStream po = new PipedOutputStream();
        writer = new OutputStreamWriter(po);

        p = new Preprocessor();
        p.addInput(new LexerSource(new InputStreamReader(new PipedInputStream(
                po)), true));
        p.addFeature(Feature.GNUCEXTENSIONS);
    }

    private static class I {
        private String t;

        public I(String t) {
            this.t = t;
        }

        public String getText() {
            return t;
        }

        public String toString() {
            return getText();
        }
    }

    private static I I(String t) {
        return new I(t);
    }

    /*
      * When writing tests in this file, remember the preprocessor stashes NLs,
      * so you won't see an immediate NL at the end of any input line. You will
      * see it right before the next nonblank on the following input line.
      */
    @Test
    public void testPreprocessor() throws Exception {
        /* Magic macros */
        testInput("line = __LINE__\n", I("line"), WHITESPACE, '=', WHITESPACE,
                INTEGER
                /* , NL - all nls deferred so as not to block the reader */
        );
        testInput("file = __FILE__\n", NL, /* from before, etc */
                I("file"), WHITESPACE, '=', WHITESPACE, STRING);

        /* Simple definitions */
        testInput("#define A a /* a defined */\n", NL);
        testInput("#define B b /* b defined */\n", NL);
        testInput("#define C c /* c defined */\n", NL);

        /* Expansion of arguments */
        testInput("#define EXPAND(x) x\n", NL);
        testInput("EXPAND(a)\n", NL, I("a"));
        testInput("EXPAND(A)\n", NL, I("a"));

        /* Stringification */
        testInput("#define _STRINGIFY(x) #x\n", NL);
        testInput("_STRINGIFY(A)\n", NL, "A");
        testInput("#define STRINGIFY(x) _STRINGIFY(x)\n", NL);
        testInput("STRINGIFY(b)\n", NL, "b");
        testInput("STRINGIFY(A)\n", NL, "a");

        /* Concatenation */
        testInput("#define _CONCAT(x, y) x ## y\n", NL);
        testInput("_CONCAT(A, B)\n", NL, I("AB"));

        testInput("#define _CONCAT_X(y) X ## y\n", NL);
        testInput("_CONCAT_X(A)\n", NL, I("XA"));
        testInput("#define _CONCAT_INT_X(y) int X ## y\n", NL);
        testInput("_CONCAT_INT_X(A)\n", NL, I("int"), WHITESPACE, I("XA"));

        testInput("#define _CONCAT_X2(y) y ## X\n", NL);
        testInput("_CONCAT_X2(A)\n", NL, I("AX"));
        testInput("#define _CONCAT_INT_X2(y) int y ## X\n", NL);
        testInput("_CONCAT_INT_X2(A)\n", NL, I("int"), WHITESPACE, I("AX"));

        testInput("#define A_CONCAT done_a_concat\n", NL);
        testInput("_CONCAT(A, _CONCAT(B, C))\n", NL, I("done_a_concat"), '(',
                I("b"), ',', WHITESPACE, I("c"), ')');
        testInput("#define CONCAT(x, y) _CONCAT(x, y)\n", NL);
        testInput("CONCAT(A, CONCAT(B, C))\n", NL, I("abc"));
        testInput("#define _CONCAT3(x, y, z) x ## y ## z\n", NL);
        testInput("_CONCAT3(a, b, c)\n", NL, I("abc"));
        testInput("_CONCAT3(A, B, C)\n", NL, I("ABC"));

        /* Redefinitions, undefinitions. */
        testInput("#define two three\n", NL);
        testInput("one /* one */\n", NL, I("one"), WHITESPACE, CCOMMENT);
        testInput("#define one two\n", NL);
        testInput("one /* three */\n", NL, I("three"), WHITESPACE, CCOMMENT);
        testInput("#undef two\n", NL);
        testInput("#define two five\n", NL);
        testInput("one /* five */\n", NL, I("five"), WHITESPACE, CCOMMENT);
        testInput("#undef two\n", NL);
        testInput("one /* two */\n", NL, I("two"), WHITESPACE, CCOMMENT);
        testInput("#undef one\n", NL);
        testInput("#define one four\n", NL);
        testInput("one /* four */\n", NL, I("four"), WHITESPACE, CCOMMENT);
        testInput("#undef one\n", NL);
        testInput("#define one one\n", NL);
        testInput("one /* one */\n", NL, I("one"), WHITESPACE, CCOMMENT);

        testInput("#define FNAME(name) paging##64_##name\n", NL);
        testInput("#define gpte_to_gfn_lvl FNAME(gpte_to_gfn_lvl)\n", NL);
        //The kernel sources expect this result:
        //testInput("gpte_to_gfn_lvl\n", NL, I("paging64_gpte_to_gfn_lvl"));
        testInput("gpte_to_gfn_lvl\n", NL, I("paging64"), I("_gpte_to_gfn_lvl"));

        testInput("#undef FNAME\n", NL);
        testInput("#undef gpte_to_gfn_lvl\n", NL);

        //Variants expanding to the expected name:
        testInput("#define FNAME(name) paging64_##name\n", NL);
        testInput("#define gpte_to_gfn_lvl FNAME(gpte_to_gfn_lvl)\n", NL);
        testInput("gpte_to_gfn_lvl\n", NL, I("paging64_gpte_to_gfn_lvl"));

        testInput("#define FNAME(name) paging##64##_##name\n", NL);
        testInput("#define gpte_to_gfn_lvl FNAME(gpte_to_gfn_lvl)\n", NL);
        testInput("gpte_to_gfn_lvl\n", NL, I("paging64_gpte_to_gfn_lvl"));

        testInput("#undef FNAME\n", NL);
        testInput("#undef gpte_to_gfn_lvl\n", NL);

        testInput("#define FNAME(name) pagin##g64_##name\n", NL);
        testInput("#define gpte_to_gfn_lvl FNAME(gpte_to_gfn_lvl)\n", NL);
        testInput("gpte_to_gfn_lvl\n", NL, I("paging64_gpte_to_gfn_lvl"));


        testInput("#define _Widen(x) L ## x\n", NL);
        testInput("#define Widen(x) _Widen(x)\n", NL);
        testInput("#define LStr(x) _Widen(#x)\n", NL);
//		testInput("LStr(x);\n", NL, I("L"), "x");
        /*}

      public void testVariadicMacrosGnuC() throws Exception {*/
        testInput("#define var(x...) a x b\n", NL);
        testInput("var(e, f, g)\n", NL, I("a"), WHITESPACE, I("e"), ',',
                WHITESPACE, I("f"), ',', WHITESPACE, I("g"), WHITESPACE, I("b"));
        testInput("var()\n", NL, I("a"), WHITESPACE, WHITESPACE, I("b"));
        testInput("#define var2(p1, args...) p1 a args b\n", NL);
        testInput("var2(firstParam, e, f, g)\n", NL, I("firstParam"), WHITESPACE, I("a"), WHITESPACE, I("e"), ',',
                WHITESPACE, I("f"), ',', WHITESPACE, I("g"), WHITESPACE, I("b"));
        testInput("var2(p1)\n", NL, I("p1"), WHITESPACE, I("a"), WHITESPACE, WHITESPACE, I("b"));
        /*}

      public void testVariadicMacrosC99() throws Exception {*/
        testInput("#define varC99(x, ...) a __VA_ARGS__ b\n", NL);
        testInput("varC99(e, f, g)\n", NL, I("a"),
                WHITESPACE, I("f"), ',', WHITESPACE, I("g"), WHITESPACE, I("b"));
        testInput("varC99()\n", NL, I("a"), WHITESPACE, WHITESPACE, I("b"));

        testInput("#define varC99_2(firstParam, ...) a firstParam, __VA_ARGS__ b\n", NL);
        testInput("varC99_2(e, f, g)\n", NL, I("a"), WHITESPACE, I("e"), ',',
                WHITESPACE, I("f"), ',', WHITESPACE, I("g"), WHITESPACE, I("b"));
        testInput("#define eprintf(format, ...) fprintf stderr, format, ##__VA_ARGS__\n", NL);
        testInput("eprintf (a)\n", NL, I("fprintf"), WHITESPACE, I("stderr"), ',', WHITESPACE, I("a"));

        //XXX: should not crash, but should cause an error! How to test an expected error?
        try {
            testInput("#define BUGGY_CONCAT(y) ## y\n", NL);
            //Flush the remaining NL
            testInput("", NL);
            fail("expected exception");
        } catch (de.fosd.typechef.lexer.LexerException e) {
            //expected
        }
    }

    @After
    public void tearDown() throws Exception {
        writer.close();

        Token t;
        do {
            t = p.getNextToken();
            System.out.println("Remaining token " + t);
        } while (t.getType() != EOF);
    }

    private void testInput(String in, Object... out) throws Exception {
        System.out.print("Input: " + in);
        writer.write(in);
        writer.flush();
        for (int i = 0; i < out.length; i++) {
            Token t = p.getNextToken();
            System.out.println(t);
            Object v = out[i];
            if (v instanceof String) {
                if (t.getType() != STRING)
                    fail("Expected STRING, but got " + t);
                assertEquals((String) v, (String) t.getValue());
            } else if (v instanceof I) {
                if (t.getType() != IDENTIFIER)
                    fail("Expected IDENTIFIER " + v + ", but got " + t);
                assertEquals(((I) v).getText(), (String) t.getText());
            } else if (v instanceof Character)
                assertEquals((int) ((Character) v).charValue(), t.getType());
            else if (v instanceof Integer)
                assertEquals(((Integer) v).intValue(), t.getType());
            else
                fail("Bad object " + v.getClass());
        }
    }
}
