package de.fosd.typechef.parser.test

import de.fosd.typechef.parser._
import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import org.junit.Test

class DigitList2ParserRepTest extends TestCase with DigitListUtilities {
    def newParser = {
        val res = new DigitList2Parser() {
                override type OptResult[T] = T
                override def myRepOpt[T](p: => MultiParser[T],
                        joinFunction: (FeatureExpr, T, T) => T,
                        productionName: String): MultiParser[List[OptResult[T]]] =
                    repPlain(p)
                def digits: MultiParser[AST] =
                    myRepOpt(digitList | digit, Alt.join, "digitList") ^^! (Alt.join,
                    //List[this.OptResult[AST]] -> DigitList[List[Opt[AST]]]
                            ((x: List[this.OptResult[AST]]) =>
                                (DigitList2(x.map((y: AST) => Opt(FeatureExpr.base, y))))))
        }
        res
    }

    def testError1() {
        val input = List(t("("), t("3", f1),t(")", f1.not), t(")"))
        val actual = newParser.parse(input)
        System.out.println(actual)
        actual match {
            case Success(ast, unparsed) => {
                fail("should not parse " + input + " but result was " + actual)
            }
            case NoSuccess(msg, context, unparsed, inner) =>

        }
    }

    @Test
    def testParseSimpleList() {
        {
            val input = List(t("("), t(")"))
            val expected = DigitList2(List())
            assertParseResult(expected, newParser.parse(input))
        }
        {
            val input = List(t("("), t("1"), t(")"))
            val expected = DigitList2(List(o(Lit(1))))
            assertParseResult(expected, newParser.parse(input))
        }
        {
            val input = List(t("("), t("1"), t("2"), t(")"))
            val expected = DigitList2(List(o(Lit(1)), o(Lit(2))))
            assertParseResult(expected, newParser.parse(input))
        }
    }

    @Test
    def testParseOptSimpleList1() {
        val input = List(t("("), t("1", f1), t("2", f1.not), t(")"))
        val expected =
            Alt(f1, outer(Lit(1)), outer(Lit(2)))
        assertParseResult(expected, newParser.parse(input))
    }
    def testParseOptSimpleListFirst() {
        //expected:<DigitList2(List(Opt(1,DigitList2(List(Opt(definedEx(a),Lit(1)), Opt(1,Lit(1)), Opt(1,Lit(2)))))))>
        //was:<Alt(definedEx(a),DigitList2(List(Opt(1,Lit(1)), Opt(1,Lit(1)), Opt(1,Lit(2)))),DigitList2(List(Opt(1,Lit(1)), Opt(1,Lit(2)))))>
        //val createNode = (x: AST) => DigitList2(List(o(x)))

        val input = List(t("("), t("1", f1), t("1"), t("2"), t(")"))
        val trail = List(Lit(1), Lit(2))
        val expected = Alt(f1, wrapList(Lit(1) :: trail), wrapList(trail))
            //DigitList2(List(Opt(f1, Lit(1)), o(Lit(1)), o(Lit(2))))
        assertParseResult(expected, newParser.parse(input))
    }
    def testParseOptSimpleListLast() {
        val input = List(t("("), t("1"), t("2"), t("3", f1), t(")"))
        val begin = List(Lit(1), Lit(2))
        val expected = Alt(f1, wrapList(begin ++ List(Lit(3))), wrapList(begin))
            //DigitList2(List(o(Lit(1)), o(Lit(2)), Opt(f1, Lit(3))))
        assertParseResult(expected, newParser.parse(input))
    }
    def testParseOptSimpleListMid() {
        val input = List(t("("), t("1"), t("2", f1), t("3"), t(")"))
        val l1 = Lit(1)
        val l3 = Lit(3)
        val expected = Alt(f1, wrapList(List(l1, Lit(2), l3)), wrapList(List(l1, l3)))
            //DigitList2(List(o(Lit(1)), Opt(f1, Lit(2)), o(Lit(3))))
        assertParseResult(expected, newParser.parse(input))
    }
    def testParseOptSimpleListCompl1() {
        val input = List(t("("), t("1"), t("2", f1), t("3", f2), t(")"))
        val expected = DigitList2(List(o(Lit(1)), Opt(f1, Lit(2)), Opt(f2, Lit(3))))
        assertParseResult(expected, newParser.parse(input))
    }
    def testParseOptSimpleListCompl2() {
        val input = List(t("("), t("1", f2), t("2", f1), t("3", f2), t(")"))
        val expected = DigitList2(List(Opt(f2, Lit(1)), Opt(f1, Lit(2)), Opt(f2, Lit(3))))
        assertParseResult(expected, newParser.parse(input))
    }
    def testParseOptSimpleListCompl3() {
        val input = List(t("("), t("1", f2), t("2", f1), t("3", f2.not), t(")"))
        val expected = DigitList2(List(Opt(f2, Lit(1)), Opt(f1, Lit(2)), Opt(f2.not, Lit(3))))
        assertParseResult(expected, newParser.parse(input))
    }
    def testParseOptSimpleListCompl4() {
        val input = List(t("("), t("1", f2), t("2", f2.not), t("3", f2.not), t(")"))
        val expected = DigitList2(List(Opt(f2, Lit(1)), Opt(f2.not, Lit(2)), Opt(f2.not, Lit(3))))
        assertParseResult(expected, newParser.parse(input))
    }
    def testParseInterleaved1() {
        val input = List(t("("), t("("), t("1"), t("2"), t(")"), t("3"), t(")"))
        val expected = DigitList2(List(o(DigitList2(List(o(Lit(1)), o(Lit(2))))), o(Lit(3))))
        assertParseResult(expected, newParser.parse(input))
    }
    def testParseInterleaved2() {
        val input = List(t("("), t("(", f1), t("1"), t("2"), t(")", f1), t("3"), t(")"))
        val expected = Alt(f1, DigitList2(List(o(DigitList2(List(Opt(f1, Lit(1)), Opt(f1, Lit(2))))), o(Lit(3)))), DigitList2(List(o(Lit(1)), o(Lit(2)), o(Lit(3)))))
        assertParseResult(expected, newParser.parse(input))
    }

    def testNoBacktrace {
        val input = List(t("1"), t("("))
        val expected = Lit(1)
        var actual = newParser.parse(input)
        println(actual)
        actual match {
            case Success(ast, unparsed) => fail("expected error, found " + ast + " - " + unparsed)
            case NoSuccess(msg, context, unparsed, inner) =>
        }
    }
}
