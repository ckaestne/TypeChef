package de.fosd.typechef.crefactor.backend

import de.fosd.typechef.parser.c.{Id, AST}
import de.fosd.typechef.crewrite.{ASTEnv, CASTEnv, ASTNavigation}


object ASTPosition extends ASTNavigation {

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
    result.filter(p => p.getFile.get.regionMatches(true, 5, file, 0, file.length()))
  }

  /**
   * Retrieves the ast of the selected statement.
   */
  def getSelectedAST(ast: AST, startLine: Int, endLine: Int, startRow: Int, endRow: Int): List[AST] = {
    val astEnv = CASTEnv.createASTEnv(ast)
    var astElementsInRange = List[AST]()
    // retrieve all ast elements in range
    for (key <- astEnv.keys()) {
      if (key.isInstanceOf[AST]) {
        val x = key.asInstanceOf[AST]
        if (isElementOfSelectionRange(x, startLine, endLine, startRow, endRow)) {
          astElementsInRange = x :: astElementsInRange
        }
      }
    }
    // sort ast elements for their length
    astElementsInRange.sortWith(compareProductArity)

    // eliminate duplicates
    var result = List[AST]()
    for (entry <- astElementsInRange) {
      if (!parentASTIsInRange(entry, astEnv, startLine, endLine, startRow, endRow)) {
        result = entry :: result
      }
    }
    result.sortWith(comparePosition)
  }

  /**
   * Compares the product length of two ast elements
   */
  private def compareProductArity(e1: AST, e2: AST) = e1.productArity < e2.productArity

  private def comparePosition(e1: AST, e2: AST) = e1.getPositionFrom < e2.getPositionFrom

  /**
   * Checks if an ast element is in a certain position range
   */
  private def isInRange(pos: Int, start: Int, end: Int): Boolean = ((start <= pos) && (pos <= end))

  /**
   * Checks if the parent of an ast element is in the same position range
   */
  private def parentASTIsInRange(entry: AST, astENV: ASTEnv, startLine: Int, endLine: Int, startRow: Int, endRow: Int): Boolean = {
    val parent = parentAST(entry, astENV)
    if ((parent != null) && isInRange(parent.getPositionFrom.getLine, startLine, endLine)
      && isInRange(parent.getPositionFrom.getColumn, startRow, endRow)) {
      return true
    }
    false
  }

  /**
   * Retrieves if element is in the selection range.
   */
  private def isElementOfSelectionRange(element: AST, startLine: Int, endLine: Int, startRow: Int, endRow: Int): Boolean = {
    if ((isInRange(element.getPositionFrom.getLine, startLine, endLine) && isInRange(element.getPositionTo.getLine - 1, startLine, endLine))
      && (isInRange(element.getPositionFrom.getColumn, startRow, endRow) || isInRange(element.getPositionTo.getColumn, startRow, endRow))) {
      return true
    }
    false
  }

  /**
   * Remove all ids except those from the specified file.
   */
  def filterFileId(selection: List[Id], file: String): List[Id] = {
    // offset 5 because file path of id contains the string "file "
    selection.filter(p => p.getFile.get.regionMatches(true, 5, file, 0, file.length()))
  }
}
