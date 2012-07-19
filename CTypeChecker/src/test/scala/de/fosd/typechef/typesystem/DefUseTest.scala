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
      int foo() {
        struct d e;
        int a = 0;
        int b = a;
        if (b) {
          int a = b;
        }
        a;
      }
                        """)

    println(getDefUseMap)
  }
}
