package de.fosd.typechef.crewrite

import org.junit.Test
import de.fosd.typechef.parser.c.{TestHelper, PrettyPrinter, FunctionDef}

class LivenessTest extends TestHelper with ConditionalControlFlow with Liveness {
  @Test def test_standard_liveness_example() {
    val a = parseFunctionDef("""
      void foo() {
        a = 0;
        l1: b = a + 1;
        c = c + b;
        a = b + 2;
        if (a < 20) goto l1;
        return c;
    }
    """)

    val env = CASTEnv.createASTEnv(a)
    val ss = getAllSucc(a.stmt.innerStatements.head.entry, env).map(_._1).filterNot(_.isInstanceOf[FunctionDef])

    for (s <- ss)
      println(PrettyPrinter.print(s) + "  out: " + outsimple(s, env) + "   in: " + insimple(s, env))

    println("#################################################")

    for (s <- ss)
      println(PrettyPrinter.print(s) + "  out: " + out(s, env) + "   in: " + in(s, env))
    // println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
  }

  @Test def test_standard_liveness_variability_f() {
    val a = parseFunctionDef("""
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

    val env = CASTEnv.createASTEnv(a)
    val ss = getAllSucc(a.stmt.innerStatements.head.entry, env).map(_._1).filterNot(_.isInstanceOf[FunctionDef])

    println("#################################################")

    for (s <- ss)
      println(PrettyPrinter.print(s) + "  out: " + out(s, env) + "   in: " + in(s, env))
    // println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
  }

  @Test def test_standard_liveness_variability_notf() {
    val a = parseFunctionDef("""
      void foo() {
        a = 0;
        l1: b = a + 1;
        c = c + b;
        a = b + 2;
        return c;
    }
    """)

    val env = CASTEnv.createASTEnv(a)
    val ss = getAllSucc(a.stmt.innerStatements.head.entry, env).map(_._1).filterNot(_.isInstanceOf[FunctionDef])

    println("#################################################")

    for (s <- ss)
      println(PrettyPrinter.print(s) + "  out: " + out(s, env) + "   in: " + in(s, env))
    // println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
  }

  @Test def test_standard_liveness_variability() {
    val a = parseFunctionDef("""
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

    val env = CASTEnv.createASTEnv(a)
    val ss = getAllSucc(a.stmt.innerStatements.head.entry, env).map(_._1).filterNot(_.isInstanceOf[FunctionDef])

    println("#################################################")

    for (s <- ss)
      println(PrettyPrinter.print(s) + "  out: " + out(s, env) + "   in: " + in(s, env))
    // println("succs: " + DotGraph.map2file(getAllSucc(a, env), env))
  }

}
