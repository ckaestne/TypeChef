package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c.TestHelper
import de.fosd.typechef.conditional._

@RunWith(classOf[JUnitRunner])
class ChoiceTypesTest extends FunSuite with ShouldMatchers with CTypeSystem with CEnvCache with TestHelper {


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
         ;
         int end;""")
        typecheckTranslationUnit(ast)
        println(ast)
        val env = lookupEnv(ast.defs.last.entry).varEnv
        println(env)

        env("a") should be(Choice(fx.not, One(CDouble()), One(CSigned(CInt()))))
        env("x") should be(Choice(fy, One(CDouble()), Choice(fx, One(CSigned(CInt())), One(CUndefined()))))
        env("b") should be(Choice(fx, One(CDouble()), One(CUndefined())))
    }


}