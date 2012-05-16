package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c.TestHelper
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

@RunWith(classOf[JUnitRunner])
class DefUseTest extends CTypeSystem with CEnvCache with FunSuite with TestHelper {
  private def compileCode(code: String) = {
    val ast = getAST(code)
    typecheckTranslationUnit(ast)
    ast
  }

  test("def use chain") {
    val a = compileCode("""
      int foo() {return 0;}
      int bar() {foo(); foo(); return 0;}
      struct a {
        int x;
      };
      union b;
      int spam() {
        struct a k;
        k.x = 2;
      }
    """)

    val lastdecl = a.defs.last.entry
    lookupEnv(lastdecl)
    println(a)
    println(defuse)
  }
}
