package de.fosd.typechef.parser.test

import junit.framework.TestCase
import org.junit._
import org.junit.Assert._
import de.fosd.typechef.parser.test.parsers._
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.featureexpr.FeatureExprFactory._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.conditional._


/**
 * specific test for the RepSepOpt problem
 *
 * repOpt (as well as any other rep combinators) fail to handle
 * lists in which elements are optional and separated by commas or
 * other separators
 *
 * such lists cause exponential effort, if annotations do not align
 * with the required separator structure
 *
 * example
 * 1 , 2 , 3 , 4
 * is easy parse with digit~repOpt(comma~digit) if annotated like
 * this
 * 1 ,_A 2_A ,_B 3_B ,_C 4_C
 * but not if annotated as
 * 1 , 2_A ,_A 3_B ,_B 4
 * the latter case is quite common, but also the former cannot be excluded
 *
 */
class RepSepOptTest extends TestCase with DigitListUtilities {

    val p = new CharDigitParser()

    def digitList = p.repSepOpt(p.number, p.comma)
    //    def digitListCommaChar = (p.repSepOptIntern(true, p.number, p.comma) sep_~ (p.opt(p.char))) ^^ (_._1)


    @Test def testPlainEmpty = expectDigitList(List(), List())
    @Test def testPlainOne = expectDigitList(List(t("1")), List(ol(1)))
    @Test def testPlainPair = expectDigitList(List(t("1"), t(","), t("2")), List(ol(1), ol(2)))
    @Test def testOneFeatureOne = expectDigitList(List(t("1", f1)), List(ol(1, f1)))
    @Test def testOneFeaturePair1 = expectDigitList(List(t("1"), t(",", f1), t("2", f1)), List(ol(1), ol(2, f1)))
    @Test def testOneFeaturePair2 = expectDigitList(List(t("1", f1), t(",", f1), t("2")), List(ol(1, f1), ol(2)))
    @Test def testOneFeatureTripple = expectDigitList(List(t("1", f1), t(",", f1), t("2"), t(","), t("3")), List(ol(1, f1), ol(2), ol(3)))

    @Test def testAltComma1 = expectDigitList(List(t("1"), t(",", f1), t(",", f1.not), t("2")), List(ol(1), ol(2)))
    @Test def testAltComma2 = expectDigitList(List(t("1"), t(",", f1), t(",", f1.not), t("2", f1), t("3", f1.not)), List(ol(1), ol(2, f1), ol(3, f1.not)))

    @Test def testIncompleteList1 = expectDigitList(List(t("1"), t(","), t("2"), t(",")), List(ol(1), ol(2)))
    @Test def testIncompleteList2 = expectDigitList(List(t("1"), t(","), t("2", f1), t(","), t("3")), List(ol(1), ol(2, f1)), 2)


    @Test def testEasylist = easyList(100)
    @Test def testHardlist = hardList(100)
    @Test def testHardlist2 = hardList2(100)
    @Test def testAnnotatedList = annotatedList(100)


    @Test def testAlternativeEntries = expectDigitList(List(t("1", f1), t("2", f1.not), t(","), t("3")), List(ol(1, f1), ol(2, f1.not), ol(3)))
    @Test def testAlternativeEntries2 = expectDigitList(List(t("1", f1), t("2", f2), t(",", f1 or f2), t("3")), List(ol(1, f1), ol(2, f2), ol(3)))
    @Test def testAlternativeEntries3 = expectDigitList(List(t("1", f1), t("2", f2), t(","), t("3")), List(ol(1, f1), ol(2, f2)), 2)
    //abort at comma
    @Test def testAlternativeEntries4 = expectDigitList(List(t("1", f1), t("2", f2), t(",", (f1 or f2)), t("3")), List(ol(1, f1), ol(2, f2), ol(3)))

    //    @Test def testCommaChar1 = expectDigitListCommaChar(List(t("1"), t(","), t("2"), t(","), t("a")), List(ol(1), ol(2)))
    //    @Test def testCommaChar2 = expectDigitListCommaChar(List(t("1"), t(","), t("2"), t("a")), List(ol(1), ol(2)), 1)
    //    @Test def testCommaChar3 = expectDigitListCommaChar(List(t("1"), t(",", f1), t("2", f1), t(","), t("a")), List(ol(1), ol(2, f1)))
    //    @Test def testCommaChar4 = expectDigitListCommaChar(List(t("1"), t(","), t("2", f1), t(",", f1), t("a")), List(ol(1), ol(2, f1)))
    //    @Test def testCommaChar5 = expectDigitListCommaChar(List(t("1"), t(",", f1), t("2", f1), t(",", f2), t("a", f2)), List(ol(1), ol(2, f1)))


    //TODO: are lists that end with a comma handled correctly?


    private def easyList(length: Int) = {
        var l = List(t("0"))
        var expected = List(ol(0))
        for (i <- 1 until length) {
            val f = FeatureExprFactory.createDefinedExternal("f" + i)
            l = l :+ t(",", f) :+ t(i.toString, f)
            expected = expected :+ ol(i, f)
        }

        println("in: " + l)
        expectDigitList(l, expected)
    }
    private def hardList(length: Int) = {
        var l = List(t("0"), t(","))
        var expected = List(ol(0))
        for (i <- 1 until length) {
            val f = FeatureExprFactory.createDefinedExternal("f" + i)
            l = l :+ t(i.toString, f) :+ t(",", f)
            expected = expected :+ ol(i, f)
        }
        l = l :+ t("0")
        expected = expected :+ ol(0)

        println("in: " + l)
        expectDigitList(l, expected)
    }
    /**
     * like hardList but immediately starts with an annotated element
     */
    private def hardList2(length: Int) = {
        var l: List[MyToken] = List()
        var expected: List[Opt[Lit]] = List()
        for (i <- 1 until length) {
            val f = FeatureExprFactory.createDefinedExternal("f" + i)
            l = l :+ t(i.toString, f) :+ t(",", f)
            expected = expected :+ ol(i, f)
        }
        l = l :+ t("0")
        expected = expected :+ ol(0)

        println("in: " + l)
        expectDigitList(l, expected)
    }

    /**
     * simple list, but inside an annotation
     */
    private def annotatedList(length: Int) = {
        val f = FeatureExprFactory.createDefinedExternal("f")

        var l: List[MyToken] = List()
        var expected: List[Opt[Lit]] = List()
        for (i <- 1 until length) {
            l = l :+ t(i.toString, f) :+ t(",", f)
            expected = expected :+ ol(i, f)
        }
        l = l :+ t("0", f)
        expected = expected :+ ol(0, f)

        println("in: " + l)
        expectDigitList(l, expected)
    }

    private def ol(v: Int) = Opt(True, Lit(v))
    private def ol(v: Int, f: FeatureExpr) = Opt(f, Lit(v))
    private def expectDigitList(providedList: List[MyToken], expectedEntries: List[Opt[Lit]], expectUnparsedTokens: Int = 0) {
        val baseFeature = createDefinedExternal("X")

        val in = p.tr(providedList.map(t => new MyToken(t.getText, t.getFeature and baseFeature)))
        val r = digitList(in, baseFeature)
        println("parse result: " + r)

        r match {
            case p.Success(r, rest) =>
                assertEquals("not at end " + rest, rest.tokens.size, expectUnparsedTokens)
                val exp: List[Opt[Lit]] = expectedEntries.map(o => Opt(o.condition and baseFeature, o.entry))
                val act: List[Opt[Lit]] = r.asInstanceOf[List[Opt[Lit]]]
                assert(
                    exp.size == act.size &&
                        (exp zip act).forall(p => (p._1.condition equivalentTo p._2.condition) && p._1.entry == p._2.entry),
                    "expected: " + exp + "\nactual   : " + act
                )
            case _ => fail("unsuccessful result " + r)
        }
    }
    //    private def expectDigitListCommaChar(providedList: List[MyToken], expectedEntries: List[Opt[Lit]], expectUnparsedTokens: Int = 0) {
    //        val in = p.tr(providedList)
    //        val r = digitListCommaChar(in, FeatureExprFactory.True)
    //        println("parse result: " + r)
    //
    //        r match {
    //            case p.Success(r, rest) =>
    //                assertEquals("not at end " + rest, rest.tokens.size, expectUnparsedTokens)
    //                assertEquals(
    //                    expectedEntries, r
    //                )
    //            case _ => fail("unsuccessful result " + r)
    //        }
    //    }

}