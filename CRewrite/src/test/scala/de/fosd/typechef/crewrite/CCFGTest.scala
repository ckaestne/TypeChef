package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import org.junit.Test
import org.scalatest.Matchers

class CCFGTest extends TestHelper with Matchers with CCFG with EnforceTreeHelper {

    def cfgtest(code: String): Boolean = {
        val f = prepareAST[FunctionDef](parseFunctionDef(code))
        val env = CASTEnv.createASTEnv(f)
        getAllSucc(f, env).foreach {
            case (e, s) =>
                println(e + "====>")
                s.foreach { println(_) }
                println("############")
        }
        true
    }

    @Test def test_fdef() {
        cfgtest( """
              void foo() {
              #ifdef A
                int i;
                int k;
              #endif
                while (j) {
                  k;  // dead store
                }
                l;
              }
                    """.stripMargin) should be(true)
    }

}
