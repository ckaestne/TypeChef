package de.fosd.typechef.jcpp;

import de.fosd.typechef.lexer.LexerException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class NestedMacroTest extends AbstractCheckTests {

    @Test
    public void test1() throws LexerException, IOException {
        checkStr("#define f(x) f(f(x))\n" + "f(1)\n" + "f(f(1))", "f(f(1))\n"
                + "f(f(f(f(1))))");
    }

    @Test
    public void test2() throws LexerException, IOException {
        checkStr("#define f(x) f(f(x))\n" + "f(1)\n" + "f(f(1))", "f(f(1))\n"
                + "f(f(f(f(1))))");
    }

    @Test
    public void test3() throws LexerException, IOException {
        checkStr("#define f(a) f(x * (a))\n" + "f(f(y+1))",
                "f(x * (f(x * (y+1))))");
    }

    @Test
    public void test4() throws LexerException, IOException {
        checkStr("#define x 2\n" + "#define f(a) f(x * (a))\n" + "f(f(y+1))",
                "f(2 * (f(2 * (y+1))))");
    }

    @Test
    public void test5() throws LexerException, IOException {
        checkStr("#define x f(1)\n" + "#define f(x) t(2*x)\n" + "x y",
                "t(2*1) y");
    }

    @Test
    public void test6() throws LexerException, IOException {
        checkStr("#define f(x) f(x+1)\n" + "f(2)", "f(2+1)");

    }

    @Test
    public void test7() throws LexerException, IOException {
        checkStr("#define z z[0]\n#define f(a) f(x * (a))\n" + "f(z)",
                "f(x * (z[0]))");

    }

    @Test
    public void test8() throws LexerException, IOException {
        checkStr("#define z z[0]\n#define f(a) f(x * (a))\n" + "f(z)",
                "f(x * (z[0]))");

    }

    private void checkStr(String orig, String expected)
            throws LexerException, IOException {
        String result = preprocessCodeFragment(orig);

        Assert.assertTrue("found " + result + ", but expected " + expected,
                result.trim().endsWith(expected));
    }

}
