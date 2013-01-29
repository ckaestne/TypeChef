package de.fosd.typechef.crefactor.backend

import de.fosd.typechef.crewrite.ASTEnv
import de.fosd.typechef.parser.c.{Id, AST}
import de.fosd.typechef.crefactor.frontend.util.Selection

trait ASTSelection {

  def getSelectedElements(ast: AST, astEnv: ASTEnv, selection: Selection): List[AST]

  def getAvailableIdentifiers(ast: AST, astEnv: ASTEnv, selection: Selection): List[Id]

  protected def isInSelectionRange(value: AST, selection: Selection): Boolean = {
    /**
     * Annotated ast elements have often the same starting line. As workaround we only identify the element by its end value.
     */
    (isInRange(value.getPositionTo.getLine, selection.getLineStart, selection.getLineEnd))
    // TODO ROW!
    // && ((selection.getRowEnd <= value.getPositionTo.getColumn) || (selection.getRowEnd <= value.getPositionTo.getColumn)))
  }

  /**
   * Compares the position between two ast elements.
   */
  protected def comparePosition(e1: AST, e2: AST) = (e1.getPositionFrom < e2.getPositionFrom)

  /**
   * Checks if an ast element is in a certain range.
   */
  protected def isInRange(pos: Int, start: Int, end: Int) = ((start <= pos) && (pos <= end))

  /**
   * Remove all ast elements except those from the specified file.
   */
  protected def filterASTElementsForFile[T <: AST](selection: List[T], file: String): List[T] = {
    // offset 5 because file path of id contains the string "file "
    val offset = 5
    selection.filter(p => p.getFile.get.regionMatches(true, offset, file, 0, file.length())).toList
  }

  def isElementOfFile[T <: AST](element: T, file: String) = element.getFile.get.regionMatches(true, 5, file, 0, file.length())

}
