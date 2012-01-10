package de.fosd.typechef.crewrite

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Tag, FunSuite}
import de.fosd.typechef.parser.c._

@RunWith(classOf[JUnitRunner])
class ConditionalControlFlowGraphTest extends FunSuite with TestHelper with ShouldMatchers with ConditionalControlFlow {
  object simpletest extends Tag("simpletest")
  object totest extends Tag("totest")

  private def parsePrintAST(code: String) = {
    val ast = getAST(code)
    println("AST: " + ast)
    println(PrettyPrinter.print(ast))
  }

  private def parsePrintASTGetAST(code: String) = {
    val ast = getAST(code)
    println("AST: " + ast)
    println(PrettyPrinter.print(ast))
    ast
  }

  private def parsePrintGetDefines(code: String) = {
    val ast = getAST(code)
    println("AST: " + ast)
    //defines(ast.get.asInstanceOf[One[AST]].value)
  }

  private def parsePrintGetSucc(code: String) = {
    val ast = getAST(code)
    println("AST: " + ast)
//    succ(ast)
  }

  test("if-then-else", totest) {
    val a = parseCompoundStmt("""
    {
      #ifdef A
      int a;
      #elif defined(B)
      int b;
      #else
      int c;
      #endif
    }
    """)

    val env = createASTEnv(a)
    //succ(a, env)
  }
}