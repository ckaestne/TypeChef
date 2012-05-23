package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.AST


class PositionMapper() extends ASTNavigation    {

  def getElementForPosition(ast: AST, column: Int, line: Int) : String = {
    val env = createASTEnv(ast)
    val keys = env.keys()
    var result = "No AST"
    for (key <- keys) {
      if (key.isInstanceOf[AST]) {
        val entry = key.asInstanceOf[AST]
        println(entry.getPositionFrom.toString + " " + entry.toString)
        val from = entry.getPositionFrom
        val to = entry.getPositionTo
        if (isInRange(column, from.getColumn, to.getColumn) && isInRange(line, from.getLine, to.getLine)) {
          result = entry.toString
        }
      }
    }
    return result
  }

  private def isInRange(pos:Int, start:Int, end:Int) : Boolean = {
      return ((start <= pos) && (pos <= end))
  }

}
