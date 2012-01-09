package de.fosd.typechef.crewrite

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import de.fosd.typechef.conditional.One
import de.fosd.typechef.parser.c._

@RunWith(classOf[JUnitRunner])
class CASTEnvTest extends ConditionalControlFlow with FunSuite with ShouldMatchers with TestHelper {


  test("simpletest") {
    val a = getAST("""
      int k;
    """)

    val id = createASTEnv(a)
    for (e <- id.astc.keySet().toArray)
      println(e + "(" + System.identityHashCode(e) + ")")
  }


}