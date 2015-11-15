package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import org.junit.Test
import org.scalatest.Matchers

class CCFGTest extends TestHelper with Matchers with CCFG with EnforceTreeHelper {

    // determine recursively all succs check
    def getAllSucc(i: AST, env: ASTEnv) = {
        var r = List[(AST, CFG)]()
        var s = List(i)
        var d = List[AST]()
        var c: AST = null

        while (s.nonEmpty) {
            c = s.head
            s = s.drop(1)

            if (!d.exists(_.eq(c))) {
                r = (c, succ(env)(c)) :: r
                s = s ++ r.head._2.map(x => x.entry)
                d = d ++ List(c)
            }
        }
        r
    }

    def cfgtest(code: String): Boolean = {
        val f = prepareAST[FunctionDef](parseFunctionDef(code))
        val env = CASTEnv.createASTEnv(f)
//        println(f + "====>")
//        succ(env)(f).foreach {
//            println(_)
//        }
//        println("############")
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
              while (a) {
                #ifdef B
                b;
                #endif
              }
              return c;
              }
                    """.stripMargin) should be(true)
    }

}
