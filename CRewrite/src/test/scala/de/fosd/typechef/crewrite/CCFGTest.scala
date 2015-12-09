package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import org.junit.Test
import org.scalatest.Matchers

class CCFGTest extends TestHelper with Matchers with IntraCFG with EnforceTreeHelper {

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

    // determine recursively all pred
    def getAllPred(i: AST, env: ASTEnv) = {
        var r = List[(AST, CFG)]()
        var s = List(i)
        var d = List[AST]()
        var c: AST = null

        while (s.nonEmpty) {
            c = s.head
            s = s.drop(1)

            if (!d.exists(_.eq(c))) {
                r = (c, pred(env)(c)) :: r
                s = s ++ r.head._2.map(x => x.entry)
                d = d ++ List(c)
            }
        }
        r
    }

    def cfgtest(code: String): Boolean = {
        val f = prepareAST[FunctionDef](parseFunctionDef(code))
        val env = CASTEnv.createASTEnv(f)
        getAllSucc(f, env).foreach {
            case (e, s) =>
                println(e + "\n====>")
                s.foreach { println(_) }
                println("############")
        }
        true
    }

    @Test def test_fdef3() {
        cfgtest( """
                   |void foo() {
                   |    a;
                   |    do {
                   |    #ifdef A
                   |    b;
                   |    #endif
                   |    } while (c);
                   |}
                 """.stripMargin) should be(true)
    }

    @Test def test_fdef2() {
        cfgtest( """
                   |void foo() {
                   |    while (a) {
                   |        b;
                   |        if (c) {
                   |            d;
                   |        } else if (e) {
                   |            f;
                   |        } else if (g) {
                   |            h;
                   |            break;
                   |        }
                   |    }
                   |}
                 """.stripMargin) should be(true)
    }

    @Test def test_fdef() {
        cfgtest( """
                   |void foo() {
                   |    e;
                   |    do {
                   |        #if definedEx(A)
                   |        if (f) {
                   |            h;
                   |        }
                   |        #endif
                   |    } while (i);
                   |    j;
                   |}
                    """.stripMargin) should be(true)
    }

}
