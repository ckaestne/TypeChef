package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.TestHelper
import org.junit.Test

class DefUseChainsTest extends TestHelper with DefUseChains with ConditionalControlFlow with NoFunctionLookup{
  @Test def test_ggt() {
    val a = parseFunctionDef("""
    int ggt(int a, int b) {
      int c = a;
      int d = b;
      if (c == 0) return 0;
      while (d != 0) {
        if (c > d)
          c = c - d;
        else
          d = d -c;
      }
      return c;
    }
    """)

    val env = CASTEnv.createASTEnv(a)

    // println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
    // println("preds: " + DotGraph.map2file(getAllPred(a, env), env))
  }

}
