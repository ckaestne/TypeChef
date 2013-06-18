package de.fosd.typechef.typesystem

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.featureexpr.FeatureExprFactory._
import de.fosd.typechef.featureexpr.FeatureExprFactory.False
import de.fosd.typechef.parser.c._

@RunWith(classOf[JUnitRunner])
class GotoTest extends FunSuite with ShouldMatchers with CTypeSystem with CEnvCache with CTypeCache with TestHelperTS {


    private def compileCode(code: String) = {
        val ast = getAST(code)
        typecheckTranslationUnit(ast)
        ast
    }

    private def ast = (compileCode( """
           void foo() {
             int i;
             i: foo(); i++; goto x;
        #ifdef X
             y: ;
        #endif
                                           #ifdef Y
                                                   y: ;
                                              #endif
             x: ;
           }
           void bar() {}
                                    """))

    val fun = ast.defs.head.entry
    val fun2 = ast.defs.last.entry

    test("label environment") {

        val env = getLabelEnv(fun.asInstanceOf[FunctionDef].stmt)

        env.get("i").get should be(True)
        env.get("x").get should be(True)
        env.get("y").get should be(createDefinedExternal("X") or createDefinedExternal("Y"))

        val env2 = getLabelEnv(fun2.asInstanceOf[FunctionDef].stmt)
        env2.get("i").getOrElse(False) should be(False)

    }

    test("goto type checking") {
        correctExpr(
            """
              |int x;
              |label:
              |  x++;
              |  goto label;
            """.stripMargin)

        correctExpr(
            """
              |int x;
              |#ifdef X
              |label:
              |#endif
              |#ifndef X
              |label:
              |#endif
              |  x++;
            """.stripMargin)

        errorExpr(
            """
              |int x;
              |label:
              |label:
              |  x++;
            """.stripMargin)

        errorExpr(
            """
              |int x;
              |label:
              |  x++;
              |  goto otherlabel;
            """.stripMargin)

        errorExpr(
            """
              |int x;
              |#ifdef X
              |label:
              |  x++;
              |#endif
              |  goto label;
            """.stripMargin)
    }


}