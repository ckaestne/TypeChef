package de.fosd.typechef.crewrite

import org.junit.Test
import de.fosd.typechef.parser.c.{AST, TestHelper, PrettyPrinter, FunctionDef}
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.conditional.Opt

class LivenessTest extends TestHelper with ConditionalControlFlow with Liveness {

  private def runExample(code: String) {
    val a = parseFunctionDef(code)

    val env = CASTEnv.createASTEnv(a)
    val ss = getAllSucc(a.stmt.innerStatements.head.entry, FeatureExprFactory.empty, env).filterNot(x => x._1.isInstanceOf[FunctionDef])

    for (o:Opt[AST] <- ss)
      println(PrettyPrinter.print(o.entry) + "  uses: " + usesVar(o.entry, env) + "   defines: " + definesVar(o.entry, env) +
        "  in: " + in((o.entry, FeatureExprFactory.empty, env)) + "   out: " + out((o.entry, FeatureExprFactory.empty, env)))
    println("succs: " + DotGraph.map2file(getAllSucc(a, FeatureExprFactory.empty, env), env))
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

  @Test def test_simle2() {
    runExample("""
      int foo() {
        int a;
        int b;
        #if definedEx(A)
        int c = c + b;
        #endif
        return c;
    }
               """)
  }
}
