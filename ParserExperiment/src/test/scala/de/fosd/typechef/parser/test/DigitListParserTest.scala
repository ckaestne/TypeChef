package de.fosd.typechef.parser.test

import de.fosd.typechef.parser._
import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import org.junit.Test;

class DigitListParserTest extends TestCase {

    val f1 = FeatureExpr.createDefinedExternal("a")
    val f2 = FeatureExpr.createDefinedExternal("b")

    def t(text: String): MyToken = t(text, FeatureExpr.base)
    def t(text: String, feature: FeatureExpr): MyToken = new MyToken(text, feature)

    def assertParseResult(expected: AST, actual: ParseResult[AST,MyToken,Any]) {
        System.out.println(actual)
        actual match {
            case Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                assertEquals("incorrect parse result", expected, ast)
            }
            case NoSuccess(msg, context, unparsed, inner) =>
                fail(msg + " at " + unparsed + " with context " + context+ " "+inner)
        }
    }

    @Test
    def testParseSimpleList() {
        {
            val input = List(t("("), t("1"), t(")"))
            val expected = DigitList(List(Lit(1)))
            assertParseResult(expected, new DigitListParser().parse(input))
        }
        {
            val input = List(t("("), t("1"), t("2"), t(")"))
            val expected = DigitList(List(Lit(1), Lit(2)))
            assertParseResult(expected, new DigitListParser().parse(input))
        }
    }

    def testParseOptSimpleList1() {

        val input = List(t("("), t("1", f1), t("2", f1.not), t(")"))
        val expected = Alt(f1, DigitList(List(Lit(1))), DigitList(List(Lit(2))))
        // DigitList(List(Alt(f1,Lit(1),Lit(2))))
        assertParseResult(expected, new DigitListParser().parse(input))
    }
    def testParseOptSimpleList2() {
        val input = List(t("("), t("1", f1), t("1"), t("2"), t(")"))
        val expected = Alt(f1, DigitList(List(Lit(1), Lit(1), Lit(2))), DigitList(List(Lit(1), Lit(2))))
        // DigitList(List(Alt(f1,Lit(1),Nil),Lit(1),Lit(2))
        assertParseResult(expected, new DigitListParser().parse(input))
    }
    def testParseOptSimpleList3() {
        val input = List(t("("), t("1"), t("2"), t("3", f1), t(")"))
        val expected = Alt(f1, DigitList(List(Lit(1), Lit(2), Lit(3))), DigitList(List(Lit(1), Lit(2))))
        // DigitList(List(Lit(1),Lit(2),Alt(f1,Lit(3),Nil))
        assertParseResult(expected, new DigitListParser().parse(input))
    }
    def testParseOptSimpleList4() {
        val input = List(t("1"), t("3", f1))
        val expected = Alt(f1, DigitList(List(Lit(1), Lit(3))), DigitList(List(Lit(1))))
        // DigitList(List(Lit(1),Lit(2),Alt(f1,Lit(3),Nil))
        val v=(new DigitListParser().digits ^^!(Alt.join, { e => e }))(new TokenReader[MyToken,Any](input, 0), FeatureExpr.base)
        println(v)
        assertParseResult(expected, v.forceJoin(Alt.join))
    }

}
