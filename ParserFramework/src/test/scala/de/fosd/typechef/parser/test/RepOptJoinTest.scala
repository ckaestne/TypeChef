package de.fosd.typechef.parser.test

import junit.framework.TestCase
import org.junit._
import org.junit.Assert._
import de.fosd.typechef.parser.test.parsers._
import de.fosd.typechef.parser.~
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.conditional._


/**
 *            some additional tests for repopt and joining
 */
class RepOptJoinTest extends TestCase with DigitListUtilities {

    val p = new CharDigitParser()

    def charAndRepDigit = p.char ~ p.repOpt(p.digit)

    @Test def testCharAndRepDigit = {
        val l = List(t("a"), t("2", f1))
        val in = p.tr(l)
        val r = charAndRepDigit(in, FeatureExpr.base)
        println("----")
        println("tokens: " + l + " -- " + in.tokens.size)
        println("parse result: " + r)

        r match {
            case p.Success(r, rest) =>
                assert(rest.atEnd)
                assertEquals(
                    new ~(Char("a"), List(Opt(f1, Lit(2)))), r
                )
            case _ => fail("unsuccessful result " + r)
        }
        //        assert()
    }


}