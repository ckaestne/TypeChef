package de.fosd.typechef.parser

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._

class MultiFeatureParserTest extends TestCase {

    val f1 = FeatureExpr.createDefinedExternal("a")
    val f2 = FeatureExpr.createDefinedExternal("b")

    def t(text: String): Token = t(text, FeatureExpr.base)
    def t(text: String, feature: FeatureExpr): Token = new Token(text, feature)

    def assertParseResult(expected: AST, actual: ParseResult[AST]) {
        System.out.println(actual)
        actual match {
            case Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                assertEquals("incorrect parse result", expected, ast)
            }
            case NoSuccess(msg, unparsed) =>
                fail(msg + " at " + unparsed)
        }
    }

    def testParseSimpleList() {
        {
            val input = List(t("("), t("1"), t(")"))
            val expected = DigitList(List(Lit(1)))
            assertParseResult(expected, new DigitListParser().parse(input))
        }
        {
            val input = List(t("("), t("1"),  t("2"), t(")"))
            val expected = DigitList(List(Lit(1),Lit(2)))
            assertParseResult(expected, new DigitListParser().parse(input))
        }
    }
    
        def testParseOptSimpleList() {
        {
            val input = List(t("("), t("1",f1), t("2",f1.not), t(")"))
            val expected = Alt(f1,DigitList(List(Lit(1))),DigitList(List(Lit(2))))
            assertParseResult(expected, new DigitListParser().parse(input))
        }
        {
            val input = List(t("("), t("1",f1),t("1"),  t("2"), t(")"))
            val expected = Alt(f1,DigitList(List(Lit(1),Lit(1),Lit(2))),DigitList(List(Lit(1),Lit(2))))
            assertParseResult(expected, new DigitListParser().parse(input))
        }
    }

}
