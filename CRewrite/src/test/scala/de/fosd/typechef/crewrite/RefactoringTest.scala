package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.parser.Opt
import org.kiama.attribution.Attributable
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class RefactoringTest extends FunSuite with TestHelper {

  private def applyTest(s: String) {
    val ast = getAST(s)
    println("old:\n" + ast)
    println("old:\n" + PrettyPrinter.print(ast))
    val newast = new CRefactorings().rewrite(ast)
    println("new: \n" + ast)
    println("new: \n" + PrettyPrinter.print(newast))
  }

  test("completeFunctionPattern") {
    applyTest("""
      #ifdef X
      int foo() {
        int k = 2;
      }
      #endif
    """)
  }

  test("oneOptOneManFunctioncall") {
    applyTest("""
      int foo() {
        spam(k);
        #ifdef X
        egg(l);
        #endif
      }
    """)
  }

  test("moveOptToCompoundStatement") {
    applyTest("""
      int foo() {
       int k;
       #ifdef X
         k = 2;
       #endif
      }
    """)
  }

  test("noVariableDefinitions") {
    applyTest("""
    #if defined(X) && defined(Z)
      int k;
    #endif
      int l;
    """)
  }

  test("functionDefinitionAndTypeDefinition") {
    applyTest("""
      int foo() {
        int k = 3;
      }
      int l = 2;
      #ifdef Z
      int m;
      #endif
    """)
  }

  test("noVariableStatements") {
    applyTest("""
      int foo() {
        int k;
        int l;
        k = 3;
        l = 4;
      }
    """)
  }

  test("multipeFunctionParameters") {
    applyTest("""
      int foo() {
        #ifdef X
        foo(k, l, m);
        #endif
      }
    """)
  }

  test("multipleFunctionCalls") {
    applyTest("""
        int foo() {
          int k, l;
          #ifdef X
          k = 3;
          l = 4;
          foo1();
          #endif
          foo2();
        }
      """)
  }

  test("alternativeFunctionCalls") {
    applyTest("""
        int foo() {
            #ifdef X
            foo();
            #else
            bar();
            #endif
            #ifdef Y
            spam();
            #endif
        }
        """)
  }

  test("refactorVariabilityIf") {
    applyTest("""
        int foo() {
            #ifdef Y
            if (x) printf("hi there\n");
            #endif
        }
        """)
  }

  private def assertNotVariability(ast: Attributable) {
    ast match {
      case Opt(f, _) => assert(f.isTautology, "Optional children not expected: " + ast)
      case c: Choice[_] => assert(false, "Choice nodes not expected: " + ast)
      case _ =>
    }

    for (c <- ast.children)
      assertNotVariability(c)
  }
}