package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c.TestHelper
import org.junit.Test

class DefUseTest extends CTypeSystem with CEnvCache with TestHelper {
  private def compileCode(code: String) = {
    val ast = getAST(code)
    typecheckTranslationUnit(ast)
    ast
  }

  @Test
  def test_def_use_chain() {
    val a = compileCode("""
      int b;
      int foo(); // forward declaration
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

    val lastdecl = lookupEnv(a.defs.last.entry)
    println(a)
    println(getDefUseMap)
  }
}
