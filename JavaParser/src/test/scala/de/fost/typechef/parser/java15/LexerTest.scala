package de.fost.typechef.parser.java15


import org.junit.Test
import org.junit.Assert._
import de.fosd.typechef.parser.java15.lexer._
import de.fosd.typechef.parser.java15._
import de.fosd.typechef.parser._
import java.io._
import de.fosd.typechef.featureexpr.FeatureExprFactory

class LexerTest {

    private def createLexer(str: String) = {
        val input = str.getBytes
        val a = new ByteArrayInputStream(input)
        val o = new OffsetCharStream(a)
        new Java15ParserTokenManager(o)
    }

    @Test
    def testLexerBasic() {
        val lexer = createLexer( """/* aa */
        	class
        	//#ifdef X
        	test {}
        	//#endif
                                 	""")
        var next = lexer.getNextToken
        while (next.kind != Java15ParserConstants.EOF) {
            println(next)
            println(next.specialToken)
            next = lexer.getNextToken
        }
    }

    @Test
    def testJavaLexer() {
        val result: TokenReader[TokenWrapper, Null] = JavaLexer.lex("class Test {}")
        assertEquals(4, result.tokens.size)
        assertTrue(result.tokens.forall(_.getFeature().isTautology))
    }

    @Test
    def testJavaLexerIfdef1() {
        val result: TokenReader[TokenWrapper, Null] = JavaLexer.lex( """//#ifdef X
class Test {}
//#endif
                                                                     """)
        assertEquals(4, result.tokens.size)
        assertTrue(result.tokens.forall(_.getFeature().equivalentTo(FeatureExprFactory.createDefinedExternal("X"))))
    }
    @Test
    def testJavaLexerIfdef2() {
        val result: TokenReader[TokenWrapper, Null] = JavaLexer.lex( """//#ifdef X
//docu
class Test {}
//#endif
                                                                     """)
        assertEquals(4, result.tokens.size)
        assertTrue(result.tokens.forall(_.getFeature().equivalentTo(FeatureExprFactory.createDefinedExternal("X"))))
    }

    @Test
    def unsupportedPreprocessorDirective1 = expectUnsupported( """//#define x 1
x""")
    @Test
    def unsupportedPreprocessorDirective2 = expectUnsupported( """//#if x==1" +
    		x""")
    @Test
    def errorOnIllformedNesting = expectUnsupported( """//#ifdef X
    		class x {}""")
    @Test
    def errorOnIllformedNesting2 = expectUnsupported( """//#endif
    		class x{}""")

    @Test
    def innerParserTest = {
        val tokens = PreprocessorParser.lex("#ifdef X")
        println(PreprocessorParser.pifdef(tokens))
        assertEquals(FeatureExprFactory.createDefinedExternal("X"), PreprocessorParser.pifdef(tokens).get)

    }

    private def expectUnsupported(code: String) {
        try {
            JavaLexer.lex(code)
            fail("succeeded without exception unexpectedly")

        } catch {
            case e: PreprocessorException => println(e) //ok
        }
    }

}