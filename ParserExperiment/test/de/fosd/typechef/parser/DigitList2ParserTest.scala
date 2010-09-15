package de.fosd.typechef.parser

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._

class DigitList2ParserTest extends TestCase {

    val f1 = FeatureExpr.createDefinedExternal("a")
    val f2 = FeatureExpr.createDefinedExternal("b")

    def t(text: String): MyToken = t(text, FeatureExpr.base)
    def t(text: String, feature: FeatureExpr): MyToken = new MyToken(text, feature)

    def assertParseResult(expected: AST, actual: ParseResult[AST,MyToken]) {
        System.out.println(actual)
        actual match {
            case Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                assertEquals("incorrect parse result", expected, ast)
            }
            case NoSuccess(msg, context, unparsed, inner) =>
                fail(msg + " at " + unparsed + " with context " + context + " " + inner)
        }
    }

    def o(ast: AST) = Opt(FeatureExpr.base, ast)

    def testParseSimpleList() {
        {
            val input = List(t("("), t(")"))
            val expected = DigitList2(List())
            assertParseResult(expected, new DigitList2Parser().parse(input))
        }
        {
            val input = List(t("("), t("1"), t(")"))
            val expected = DigitList2(List(o(Lit(1))))
            assertParseResult(expected, new DigitList2Parser().parse(input))
        }
        {
            val input = List(t("("), t("1"), t("2"), t(")"))
            val expected = DigitList2(List(o(Lit(1)), o(Lit(2))))
            assertParseResult(expected, new DigitList2Parser().parse(input))
        }
    }

    def testParseOptSimpleList1() {

        val input = List(t("("), t("1", f1), t("2", f1.not), t(")"))
        val expected = DigitList2(List(o(Alt(f1, Lit(1), Lit(2)))))
        assertParseResult(expected, new DigitList2Parser().parse(input))
    }
    def testParseOptSimpleListFirst() {
        val input = List(t("("), t("1", f1), t("1"), t("2"), t(")"))
        val expected = DigitList2(List(Opt(f1, Lit(1)), o(Lit(1)), o(Lit(2))))
        assertParseResult(expected, new DigitList2Parser().parse(input))
    }
    def testParseOptSimpleListLast() {
        val input = List(t("("), t("1"), t("2"), t("3", f1), t(")"))
        val expected = DigitList2(List(o(Lit(1)), o(Lit(2)), Opt(f1, Lit(3))))
        assertParseResult(expected, new DigitList2Parser().parse(input))
    }
    def testParseOptSimpleListMid() {
        val input = List(t("("), t("1"), t("2", f1), t("3"), t(")"))
        val expected = DigitList2(List(o(Lit(1)), Opt(f1, Lit(2)), o(Lit(3))))
        assertParseResult(expected, new DigitList2Parser().parse(input))
    }
    def testParseOptSimpleListCompl1() {
        val input = List(t("("), t("1"), t("2", f1), t("3", f2), t(")"))
        val expected = DigitList2(List(o(Lit(1)), Opt(f1, Lit(2)), Opt(f2, Lit(3))))
        assertParseResult(expected, new DigitList2Parser().parse(input))
    }
    def testParseOptSimpleListCompl2() {
        val input = List(t("("), t("1", f2), t("2", f1), t("3", f2), t(")"))
        val expected = DigitList2(List(Opt(f2, Lit(1)), Opt(f1, Lit(2)), Opt(f2, Lit(3))))
        assertParseResult(expected, new DigitList2Parser().parse(input))
    }
    def testParseOptSimpleListCompl3() {
        val input = List(t("("), t("1", f2), t("2", f1), t("3", f2.not), t(")"))
        val expected = DigitList2(List(Opt(f2, Lit(1)), Opt(f1, Lit(2)), Opt(f2.not, Lit(3))))
        assertParseResult(expected, new DigitList2Parser().parse(input))
    }
    def testParseOptSimpleListCompl4() {
        val input = List(t("("), t("1", f2), t("2", f2.not), t("3", f2.not), t(")"))
        val expected = DigitList2(List(Opt(f2, Lit(1)), Opt(f2.not, Lit(2)), Opt(f2.not, Lit(3))))
        assertParseResult(expected, new DigitList2Parser().parse(input))
    }
    def testParseInterleaved1() {
        val input = List(t("("), t("("), t("1"), t("2"), t(")"), t("3"), t(")"))
        val expected = DigitList2(List(o(DigitList2(List(o(Lit(1)), o(Lit(2))))), o(Lit(3))))
        assertParseResult(expected, new DigitList2Parser().parse(input))
    }
    def testParseInterleaved2() {
        val input = List(t("("), t("(",f1), t("1"), t("2"), t(")",f1), t("3"), t(")"))
        val expected = Alt(f1,DigitList2(List(o(DigitList2(List(Opt(f1,Lit(1)), Opt(f1,Lit(2))))), o(Lit(3)))),DigitList2(List(o(Lit(1)), o(Lit(2)), o(Lit(3)))))
        assertParseResult(expected, new DigitList2Parser().parse(input))
    }

}
