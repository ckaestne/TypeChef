package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.c.TestHelper
import de.fosd.typechef.conditional._

@RunWith(classOf[JUnitRunner])
class ChoiceTypesTest extends FunSuite with ShouldMatchers with CTypes with CExprTyping with CStmtTyping with TestHelper {

    val fx = FeatureExpr.createDefinedExternal("X")
    val fy = FeatureExpr.createDefinedExternal("Y")

    test("alternatives in declarations") {t()}
    def t() {
        val ast = getAST("""
         #ifdef X
         int a;
         #else
         double a;
         #endif

         #ifdef X
         int x;
         #endif
         #ifdef Y
         double x;
         #endif

         double
         #ifdef X
         b
         #else
         c
         #endif
         ;""")
        println(ast)
        val env = ast.defs.last.entry -> varEnv

        env("a") should be(TChoice(fx.not, TOne(CDouble()), TOne(CSigned(CInt()))))
        env("x") should be(TChoice(fy, TOne(CDouble()), TChoice(fx, TOne(CSigned(CInt())), TOne(CUndefined()))))
        env("b") should be(TChoice(fx, TOne(CDouble()), TOne(CUndefined())))
    }


}