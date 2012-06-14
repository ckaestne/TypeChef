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
    println("Last declaration is:\n" + lastdecl + "\n")
    lookupEnv(lastdecl)
    println("Ast is:\n" + a + "\n")
    println("Def use map is:\n" + getDefUseMap + "\n")
  }
}