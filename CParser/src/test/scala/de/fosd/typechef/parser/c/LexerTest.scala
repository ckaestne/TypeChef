package de.fosd.typechef.parser.c

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._


class LexerTest extends TestCase {

	def testLexerSimple() {
		println(CLexer.lex("#ifdef X\n" + "#define foo f\n" + "#else\n"
				+ "#define foo b\n" + "#endif\n" + "bar\n" + "#ifdef B\n"
				+ "foo\n" + "#endif\n"))
	}
	
}