package de.fosd.typechef.parser

import junit.framework._;
import junit.framework.Assert._

class MultiFeatureParserTest extends TestCase {

    def t(text: String): Token = t(text, 0)
    def t(text: String, feature: Int): Token = new Token(text, feature)

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

    def testParseSimple() {
        //"(3+5)*(4+2+1)"
        val input = List(t("("), t("1"), t("+"), t("5"), t(")"), t("*"), t("("), t("4"), t("+"), t("2"), t("+"), t("1"), t(")"))
        val expected = Mul(Plus(Lit(1), Lit(5)), Plus(Lit(4), Plus(Lit(2), Lit(1))))
        assertParseResult(expected, new MultiExpressionParser().parse(input))
    }

    def testMultiParseSimple() {
        val input = List(t("1"), t("+"), t("5"))
        val expected = Plus(Lit(1), Lit(5))
        assertParseResult(expected, new MultiExpressionParser().parse(input))
    }
    def testMultiParseSimpleMinus() {
        val input = List(t("1"), t("-"), t("5"))
        val expected = Minus(Lit(1), Lit(5))
        assertParseResult(expected, new MultiExpressionParser().parse(input))
    }
    def testMultiParseSimplePlusMinus() {
        val input = List(t("1"), t("+"), t("-"), t("5"))
        val result = new MultiExpressionParser().parse(input)
        result match {
            case Success(ast, unparsed) => {
            	if (unparsed.atEnd)
            		fail("expected parse error did not occur")
            }
            case _ =>
        }
    }

    def testMultiParseAlternative() {
        val input = List(t("1", 2), t("2", -2), t("+"), t("5"))
        val expected = Plus(Alt(2, Lit(1), Lit(2)), Lit(5))
        assertParseResult(expected, new MultiExpressionParser().parse(input))
    }

    def testMultiParseAlternativePlusMinus() {
        val input = List(t("1", 2), t("2", -2), t("+", 1), t("-", -1), t("5"))
        val expected = Alt(1, Plus(Alt(2, Lit(1), Lit(2)), Lit(5)), Minus(Alt(2, Lit(1), Lit(2)), Lit(5)))
        assertParseResult(expected, new MultiExpressionParser().parse(input))
    }
    def testMultiParseAlternativeTwoPlus() {
        val input = List(t("1", 2), t("2", -2), t("+", 1), t("+", -1), t("5"))
        val expected = Plus(Alt(2, Lit(1), Lit(2)), Lit(5))
        assertParseResult(expected, new MultiExpressionParser().parse(input))
    }
    def testMultiParseAlternativeOverBrakets() {
        //IFDEF (3+5 ELSE (3 ENDIF ) *(IFDEF 4 ELSE 1 ENDIF +2+1)
        val input = List(t("(", 1), t("3", 1), t("+", 1), t("5", 1),
            t("(", -1), t("3", -1),
            t(")"), t("*"), t("("),
            t("4", 2), t("1", -2), t("+"), t("2"), t("+"), t("1"), t(")"))
        val expected = Mul(Alt(1, Plus(Lit(3), Lit(5)), Lit(3)), Plus(Alt(2, Lit(4), Lit(1)), Plus(Lit(2), Lit(1))))
        assertParseResult(expected, new MultiExpressionParser().parse(input))
    }

    def testDesignInterface() {
        //(IFDEF 3+5) ELSE 3) ENDIF  *(IFDEF 4 ELSE 1 ENDIF +2+1)
        val input = List(t("(", 1), t("3", 1), t("+", 1), t("5", 1),
            t("(", -1), t("3", -1),
            t(")"), t("*"), t("("),
            t("4", 2), t("1", -2), t("+"), t("2"), t("+"), t("1"), t(")"))

        val expected = Mul(Alt(1, Plus(Lit(3), Lit(5)), Lit(3)), Plus(Alt(2, Lit(4), Lit(1)), Plus(Lit(2), Lit(1))))
        assertParseResult(expected, new MultiExpressionParser().parse(input))
    }

}
