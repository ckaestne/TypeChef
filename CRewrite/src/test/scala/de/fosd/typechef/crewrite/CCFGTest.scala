package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{CDeclUse, CTypeCache, CTypeSystemFrontend}
import org.junit.Test
import org.scalatest.Matchers

class CCFGTest extends TestHelper with Matchers with CCFG with EnforceTreeHelper {

    def cfgtest(code: String): Boolean = {
        val f = prepareAST[FunctionDef](parseFunctionDef(code))
        val env = CASTEnv.createASTEnv(f)
        println(f + "====>")
        succ(env)(f).foreach {
            println(_)
        }
        println("############")
//        getAllSucc(f, env).foreach {
//            case (e, s) =>
//                println(e + "====>")
//                s.foreach { println(_) }
//                println("############")
//        }
        true
    }

    @Test def test_fdef() {
        cfgtest( """
              void foo() {
              #ifdef A
                a;
                b;
                c;
              #endif
              }
                    """.stripMargin) should be(true)
    }

}
