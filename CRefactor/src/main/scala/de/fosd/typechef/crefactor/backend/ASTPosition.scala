package de.fosd.typechef.crefactor.backend

import de.fosd.typechef.parser.c.{Statement, Id, AST}
import de.fosd.typechef.crewrite.{ConditionalNavigation, ASTEnv, ASTNavigation}
import collection.immutable.HashSet
import de.fosd.typechef.conditional.Opt


object ASTPosition extends ASTNavigation with ConditionalNavigation {

  /**
   * Retrieves all ids in the selected range.
   */
  def getSelectedIDs(ast: AST, file: String, startLine: Int, endLine: Int, startRow: Int, endRow: Int): List[Id] = {
    var result = List[Id]()
    filterASTElems[Id](ast).foreach(x => {
      if ((isInRange(x.getPositionFrom.getLine, startLine, endLine) || isInRange(x.getPositionTo.getLine, startLine, endLine))
        && (isInRange(x.getPositionFrom.getColumn, startRow, endRow) || isInRange(x.getPositionTo.getColumn, startRow, endRow))) {
        result = x :: result
      }
    })
    filterFileId[Id](result, file)
  }

  /**
   * Retrieves all ast elements of the selected range.
   */
  private def getElementsInRange(astEnv: ASTEnv, file: String, startLine: Int, endLine: Int, startRow: Int, endRow: Int): List[AST] = {
    var astElementsInRange: List[AST] = List[AST]()
    for (key <- astEnv.keys()) {
      if (key.isInstanceOf[AST]) {
        val x = key.asInstanceOf[AST]
        if (isElementOfSelectionRange(x, startLine, endLine, startRow, endRow)) {
          astElementsInRange = x :: astElementsInRange
        }
      }
    }
    filterFileId(astElementsInRange, file)
  }

  /**
   * Retrieves all selected opts.
   */
  def getSelectedOpts(ast: AST, astEnv: ASTEnv, file: String, startLine: Int, endLine: Int, startRow: Int, endRow: Int): List[Opt[_]] = {
    var optElements = List[Opt[_]]()
    for (element <- getSelectedStatements(ast, astEnv, file, startLine, endLine, startRow, endRow)) {
      optElements = parentOpt(element, astEnv) :: optElements
    }
    optElements
  }

  /**
   * Retrieves all selected statements.
   */
  def getSelectedStatements(ast: AST, astEnv: ASTEnv, file: String, startLine: Int, endLine: Int, startRow: Int, endRow: Int): List[Statement] = {
    var statements = HashSet[Statement]()
    for (element <- getElementsInRange(astEnv, file, startLine, endLine, startRow, endRow)) {
      if (element.isInstanceOf[Statement]) {
        statements = statements + element.asInstanceOf[Statement]
      } else {
        findPriorASTElem[Statement](element, astEnv) match {
          case Some(x) => statements = statements + x
          case _ => println("Debug - This should not have happend ")
        }
      }
    }
    statements.toList.sortWith(comparePosition)
  }

  /**
   * Compares the position between two ast elements.
   */
  private def comparePosition(e1: AST, e2: AST) = (e1.getPositionFrom < e2.getPositionFrom)

  /**
   * Checks if an ast element is in a certain position range.
   */
  private def isInRange(pos: Int, start: Int, end: Int) = ((start <= pos) && (pos <= end))

  /**
   * Retrieves if element is in the selection range.
   */
  private def isElementOfSelectionRange(element: AST, startLine: Int, endLine: Int, startRow: Int, endRow: Int): Boolean = {
    if (!((isInRange(element.getPositionFrom.getLine, startLine, endLine) && isInRange(element.getPositionTo.getLine, startLine, endLine)))) {
      return false
    } else if (element.getPositionFrom.getLine == startLine) {
      return isInRange(element.getPositionFrom.getColumn, startRow, endRow)
    } else if (element.getPositionTo.getLine == endLine) {
      return isInRange(element.getPositionTo.getColumn, startRow, endRow)
    }
    true
  }

  /**
   * Remove all ids except those from the specified file.
   */
  private def filterFileId[T <: AST](selection: List[T], file: String): List[T] = {
    // offset 5 because file path of id contains the string "file "
    val offset = 5
    selection.filter(p => p.getFile.get.regionMatches(true, offset, file, 0, file.length()))
  }
}
