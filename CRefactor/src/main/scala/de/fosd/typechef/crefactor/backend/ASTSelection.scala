package de.fosd.typechef.crefactor.backend

import de.fosd.typechef.parser.c.{Id, AST}
import de.fosd.typechef.crefactor.frontend.util.Selection
import de.fosd.typechef.crefactor.{Logging, Morpheus}
import de.fosd.typechef.conditional.Opt

trait ASTSelection extends Logging {

  def getSelectedElements(morpheus: Morpheus, selection: Selection): List[AST]

  def getAvailableIdentifiers(morpheus: Morpheus, selection: Selection): List[Id]

  def isInSelectionRange(value: AST, selection: Selection): Boolean = {
    /**
     * Annotated ast elements have often the same starting line. As workaround we only identify the element by its end value.
     */
    (isInRange(value.getPositionTo.getLine, selection.getLineStart + 1, (selection.getLineEnd - 1)))
    // TODO FIX IT -> Broken!
    // && ((selection.getRowEnd <= value.getPositionTo.getColumn) || (selection.getRowEnd <= value.getPositionTo.getColumn)))
  }

  /**
   * Compares the position between two ast elements.
   */
  def comparePosition(e1: AST, e2: AST) = (e1.getPositionFrom < e2.getPositionFrom)

  def comparePosition(e1: Opt[AST], e2: Opt[AST]): Boolean = comparePosition(e1.entry, e2.entry)

  /**
   * Checks if an ast element is in a certain range.
   */
  def isInRange(pos: Int, start: Int, end: Int) = ((start <= pos) && (pos <= end))

  /**
   * Remove all ast elements except those from the specified file.
   */
  def filterASTElementsForFile[T <: AST](selection: List[T], file: String): List[T] = {
    // offset 5 because file path of callId contains the string "file "
    val offset = 5
    selection.filter(p => p.getFile.get.regionMatches(true, offset, file, 0, file.length())).toList
  }

  def isElementOfFile[T <: AST](element: T, file: String) = element.getFile.get.regionMatches(true, 5, file, 0, file.length())

  def isElementOfSelectionRange(element: AST, selection: Selection): Boolean = {
    val startLine = selection.getLineStart
    val endLine = selection.getLineEnd
    val startRow = selection.getRowStart
    val endRow = selection.getRowEnd

    if (!((isInRange(element.getPositionFrom.getLine, startLine, endLine) && isInRange(element.getPositionTo.getLine, startLine, endLine)))) false
    else if (element.getPositionFrom.getLine == startLine) isInRange(element.getPositionFrom.getColumn, scala.math.min(startRow, endRow), scala.math.max(startRow, endRow))
    else if (element.getPositionTo.getLine == endLine) isInRange(element.getPositionTo.getColumn, scala.math.min(startRow, endRow), scala.math.max(startRow, endRow))
    else true
  }

  def compareByName(id1: Id, id2: Id) = (id1.name < id2.name)

}
