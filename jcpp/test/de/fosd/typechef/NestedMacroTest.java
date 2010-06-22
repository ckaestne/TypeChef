package de.fosd.typechef;

import java.io.IOException;

import org.anarres.cpp.LexerException;
import org.junit.Assert;
import org.junit.Test;

public class NestedMacroTest extends AbstractCheckTests {

	@Test
	public void test1() throws LexerException, IOException {
		checkStr("#define f(x) f(f(x))\n" + "f(1)\n" + "f(f(1))", "f(f(1))\n"
				+ "f(f(f(f(1))))");

		checkStr("#define f(x) f(f(x))\n" + "f(1)\n" + "f(f(1))", "f(f(1))\n"
				+ "f(f(f(f(1))))");

		checkStr("#define f(a) f(x * (a))\n" + "f(f(y+1))",
				"f(x * (f(x * (y+1))))");

		checkStr("#define x 2\n" + "#define f(a) f(x * (a))\n" + "f(f(y+1))",
				"f(2 * (f(2 * (y+1))))");

		checkStr("#define x f(1)\n" + "#define f(x) t(2*x)\n" + "x y",
				"f(2*1) y");

		checkStr("#define f(x) f(x+1)\n" + "f(2)", "f(2 +1)");

	}

	private void checkStr(String orig, String expected) throws LexerException,
			IOException {
		String result = parseCodeFragment(orig);

		Assert.assertTrue("found " + result + ", but expected " + expected,
				result.trim().endsWith(expected));
	}
}
