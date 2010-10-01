package de.fosd.typechef.jcpp;

import java.io.IOException;
import java.util.List;

import org.anarres.cpp.LexerException;
import org.anarres.cpp.PartialPPLexer;
import org.anarres.cpp.StringLexerSource;
import org.anarres.cpp.Token;
import org.junit.Test;

public class MultiParserInputTest {

	@Test
	public void testParserInput() throws LexerException, IOException {
		checkStr("#ifdef X\n" + "#define foo f\n" + "#else\n"
				+ "#define foo b\n" + "#endif\n" + "bar\n" + "#ifdef B\n"
				+ "foo\n" + "#endif\n", 3);
	}

	private void checkStr(String orig, int expectedNumber)
			throws LexerException, IOException {
		List<Token> tokens = new PartialPPLexer().parse(new StringLexerSource(
				orig, true), null);
		for (Token t : tokens)
			System.out.println(t);
		assert (tokens.size() == expectedNumber);

		// Assert.assertTrue("found " + result + ", but expected " + expected,
		// result.trim().endsWith(expected));
	}
}
