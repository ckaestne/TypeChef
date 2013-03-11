package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import org.junit.Test

class CASTEnvTest extends ConditionalControlFlow with TestHelper with NoFunctionLookup{

  @Test def test_simple_test() {
    val a = parseCompoundStmt("""
    {
      while (k) {
        k--;
      }
    }
    """)

    val env = CASTEnv.createASTEnv(a)
    println(env)
  }
}