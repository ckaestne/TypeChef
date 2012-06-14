package de.fosd.typechef.crewrite

import org.junit.{Ignore, Test}
import de.fosd.typechef.parser.c.{FunctionDef, PrettyPrinter, TestHelper}

class SignAnalysisTest extends TestHelper with SignAnalysis with ConditionalControlFlow with Liveness {

  @Test def test_simple() {
    val a = parseFunctionDef("""
     void m() {
        int x = 0;
        #ifdef A
        x++;
        #endif
        #ifdef B
        x--;
        #endif
     }
       """)

    val env = CASTEnv.createASTEnv(a)
    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
    println("preds: " + DotGraph.map2file(getAllPred(a, env), env))

    val ss = getAllSucc(a.stmt.innerStatements.head.entry, env).map(_._1).filterNot(_.isInstanceOf[FunctionDef])

    println("#################################################")

    for (s <- ss)
      println(PrettyPrinter.print(s) + "  out: " + out(s, env) + "   in: " + in(s, env))
  }
}
