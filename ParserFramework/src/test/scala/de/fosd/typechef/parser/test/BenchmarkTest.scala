package de.fosd.typechef.parser.test

import junit.framework.TestCase
import org.junit.{Assert, Test}

/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 22.12.10
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.test.parsers._

class BenchmarkTest extends TestCase with DigitListUtilities {


    val p = new CharDigitParser()

    @Test def testTokenCounter1() = expect(List(t("1"), t("2")), 2, 0, 0, p.twosymb)
    @Test def testTokenCounter2() = expect(List(t("a"), t("2")), 2, 0, 0, p.twosymb)
    //requires to backtrack once
    @Test def testTokenCounter3() = expect(List(t("a"), t("2")), 3, 1, 0, p.ab)

    //split parsing, but consume every token only once
    @Test def testSplitParsing1() = expect(List(t("1", f1), t("2", f1.not)), 2, 0, 0, p.digit)
    //split parsing, and consume last token twice
    @Test def testSplitParsing2() = expect(List(t("("), t("1", f1), t("2", f1.not), t(")")), 5, 0, 1, p.parenDigit)
    //split parsing, but backtracks only in one branch -- which is considered as replicate parsing
    @Test def testSplitParsing3() = expect(List(t("a"), t("b", f1), t("1", f1.not)), 4, 0, 1, p.ab)
    //split parsing, backtracking as in previous, and replicate parsing at closing bracket
    @Test def testSplitParsing4() = expect(List(t("("), t("a"), t("b", f1), t("1", f1.not), t(")")), 7, 0, 2, p.parenAb)

    //1 * 2 + 3_ 4_
    @Test def testExpr1 = expect(List(t("1"), t("*"), t("2", f1), t("3", f1.not), t("+"), t("3")), 6, 0, 0, p.expr)
    //1 * (_ 2 + 3 )_
    @Test def testExpr2 = expect(List(t("1"), t("*"), t("(", f1), t("2"), t("+"), t("3"), t(")", f1)), 10, 0, 3, p.expr)

    private def expect(l: List[MyToken], tokenCounter: Int, backtracking: Int, replicatedParsing: Int, parser: p.MultiParser[Any]) {
        val in = p.tr(l)
        println("----")
        println("tokens: " + l + " -- " + in.tokens.size)
        println("parse result: " + p.phrase(parser)(in, FeatureExpr.base))

        val totalConsumed = in.tokens.foldLeft(0)((sum, token) => sum + token.profile_consumed)
        val totalBacktracked = in.tokens.foldLeft(0)((sum, token) => sum + token.profile_consumed_backtracking)
        val totalRepeated = in.tokens.foldLeft(0)((sum, token) => sum + token.profile_consumed_replicated())

        println(totalConsumed)
        println(totalBacktracked)
        println(totalRepeated)
        //        println(p.debugReplicatedParsingCounter)
        Assert.assertEquals(tokenCounter, totalConsumed)
        Assert.assertEquals(backtracking, totalBacktracked)
        Assert.assertEquals(replicatedParsing, totalRepeated)
    }
}