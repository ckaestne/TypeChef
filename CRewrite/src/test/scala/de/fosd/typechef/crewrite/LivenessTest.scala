package de.fosd.typechef.crewrite

import org.junit.Test
import de.fosd.typechef.parser.c.{TestHelper, PrettyPrinter, FunctionDef}

class LivenessTest extends TestHelper with ConditionalControlFlow with Liveness {

  private def runExample(code: String) {
    val a = parseFunctionDef(code)

    val env = CASTEnv.createASTEnv(a)
    val ss = getAllSucc(a.stmt.innerStatements.head.entry, env).map(_._1).filterNot(_.isInstanceOf[FunctionDef])

    for (s <- ss)
      println(PrettyPrinter.print(s) + "  uses: " + usesVar(s, env) + "   defines: " + definesVar(s, env) +
        "  in: " + in(s, env) + "   out: " + out(s, env))
    println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
  }


  @Test def test_standard_liveness_example() {
    runExample("""
      void foo() {
        a = 0;
        l1: b = a + 1;
        c = c + b;
        a = b + 2;
        if (a < 20) goto l1;
        return c;
    }
    """)
  }

  @Test def test_standard_liveness_variability_f() {
    runExample("""
      void foo(int a, int b, int c) {
        a = 0;
        l1: b = a + 1;
        c = c + b;
        a = b + 2;
        if (a < 20)
          goto l1;
        return c;
    }
    """)
  }

  @Test def test_standard_liveness_variability_notf() {
    runExample("""
      void foo() {
        a = 0;
        l1: b = a + 1;
        c = c + b;
        a = b + 2;
        return c;
    }
    """)
  }

  @Test def test_standard_liveness_variability() {
    runExample("""
      void foo() {
        a = 0;
        l1: b = a + 1;
        c = c + b;
        a = b + 2;
        #ifdef F
        if (a < 20)
          goto l1;
        #endif
        return c;
    }
    """)
  }

  @Test def test_simple() {
    runExample("""
      int foo(int a, int b) {
        int c = a;
        if (c) {
          c = c + a;
          #if definedEx(A)
          c = c + b;
          #endif
        }
        return c;
    }
    """)
  }
}
