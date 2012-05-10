package de.fosd.typechef.typesystem

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.c._
import de.fosd.typechef.parser.c._
import FeatureExprFactory._

@RunWith(classOf[JUnitRunner])
class DeadCodeTest extends CTypeSystem with FunSuite with ShouldMatchers with TestHelper {


    def e(s: String) = {
        val r = parseExpr(s)
        println(r)
        r
    }
    def evalExpr(s: String): Conditional[VValue] = evalExpr(e(s.replace("[[", "\n#ifdef A\n").replace("][", "\n#else\n").replace("]]", "\n#endif\n")), True)


    test("get expression bounds") {
        analyzeExprBounds(Constant("0"), True) should be((True, False))
        analyzeExprBounds(Constant("1"), True) should be((False, True))
        analyzeExprBounds(e("1+0"), True) should be((False, True))
    }
    test("eval expression") {
        evalExpr("1") should be(One(VInt(1)))
        evalExpr("0") should be(One(VInt(0)))
        evalExpr("i") should be(One(VUnknown()))
        evalExpr("1+2") should be(One(VInt(3)))
        evalExpr("1+[[1][0]]") should be(Choice(fa.not, One(VInt(1)), One(VInt(2))))
        evalExpr("i || 1") should be(One(VInt(1)))
        evalExpr("0 && i") should be(One(VInt(0)))
        evalExpr("!0") should be(One(VInt(1)))
    }

}