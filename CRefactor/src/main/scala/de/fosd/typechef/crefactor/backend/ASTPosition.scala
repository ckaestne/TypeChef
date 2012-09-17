package de.fosd.typechef.crefactor.backend

import de.fosd.typechef.parser.c.{Id, AST}
import de.fosd.typechef.crewrite.ASTNavigation


object ASTPosition extends ASTNavigation {


  def getSelectedIDs(ast: AST, startLine: Int, endLine: Int, startRow: Int, endRow: Int): List[Id] = {
    var result = List[Id]()
    filterASTElems[Id](ast).foreach(x => {
      if (isInRange(x.getPositionFrom.getLine, startLine, endLine)
        && isInRange(x.getPositionFrom.getColumn, startRow, endRow)) {
        result = x :: result
      }
    })
    result
  }

  private def compareLength(e1: AST, e2: AST) = (e1.getPositionTo.getColumn - e1.getPositionFrom.getColumn) > (e2.getPositionTo.getColumn - e2.getPositionFrom.getColumn)

  private def isInRange(pos: Int, start: Int, end: Int): Boolean = ((start <= pos) && (pos <= end))


}
