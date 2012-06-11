package de.fosd.typechef.crefactor.connector

import de.fosd.typechef.parser.c._
import de.fosd.typechef.crewrite.PositionMapper
import de.fosd.typechef.Frontend


/**
 * CreateASTForCode object between the gui and type chef.
 */

object CreateASTForCode {

  private val p = new CParser()
  private var ast = null.asInstanceOf[AST]

  def prettyAnalyse: String = {
    if (ast != null) {
      PrettyPrinter.print(ast)
    } else {
      "AST is invalid!".asInstanceOf[String]
    }
  }

  def analyse: String = {
    if (ast != null) {
      ast.toString.asInstanceOf[String]
    } else {
      "AST is invalid!".asInstanceOf[String]
    }
  }

  def positionAnalyse(column:Int, line:Int): String = {
    if (ast != null) {
    val positions = new PositionMapper()
    positions.getElementForPosition(ast, column, line)
    } else {
      "AST is invalid!".asInstanceOf[String]
    }
  }

  def extendedPosAnalyse(columnStart:Int, columnEnd:Int, lineStart:Int, lineEnd:Int) {
    if (ast != null) {
      val positions = new PositionMapper()
      positions.getSelectedElements(ast, columnStart, lineStart, columnEnd, lineEnd)
    }
  }



   def getAST(args: Array[String]): AST = {
    var frontend = Frontend.main(args)
    ast = Frontend.getAST()
    println(ast)
    return ast
  }




}
