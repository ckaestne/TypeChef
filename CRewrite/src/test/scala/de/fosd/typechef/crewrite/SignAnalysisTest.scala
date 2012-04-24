package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.TestHelper
import org.junit.{Ignore, Test}

class SignAnalysisTest extends TestHelper with SignAnalysis with ConditionalControlFlow {

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
   }
}
