package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.crewrite.CASTEnv._
import de.fosd.typechef.typesystem.{CTypeSystem, CDefUse}


class PositionMapper() extends ASTNavigation with CDefUse with CTypeSystem {

  def getElementForPosition(ast: AST, column: Int, line: Int): String = {
    val env = createASTEnv(ast)
    val keys = env.keys()
    var result = "No AST"
    for (key <- keys) {
      if (key.isInstanceOf[AST]) {
        val entry = key.asInstanceOf[AST]
        val from = entry.getPositionFrom
        val to = entry.getPositionTo
        if (isInRange(column, from.getColumn, to.getColumn) && isInRange(line, from.getLine, to.getLine)) {
          result = entry.toString
        }
      }
    }
    return result
  }

  def getElementAtPosition(ast: AST, column: Int, line: Int): List[AST] = {
    val env = createASTEnv(ast)
    val keys = env.keys()
    var result = List.newBuilder[AST].result()
    for (key <- keys) {
      if (key.isInstanceOf[AST]) {
        val entry = key.asInstanceOf[AST]
        val from = entry.getPositionFrom
        val to = entry.getPositionTo
        if (isInRange(column, from.getColumn, to.getColumn) && isInRange(line, from.getLine, to.getLine)) {
          entry :: result
        }
      }
    }
    return result
  }

  def getSelectedElements(ast: AST, startColumn: Int, startLine: Int, endColumn: Int, endLine: Int): List[AST] = {
    val env = createASTEnv(ast)
    val keys = env.keys()
    var result = List[AST]()
    for (key <- keys) {
      if (key.isInstanceOf[AST]) {
        val entry = key.asInstanceOf[AST]
        val from = entry.getPositionFrom
        val to = entry.getPositionTo
        if ((isInRange(from.getColumn, startColumn, endColumn) && isInRange(from.getLine, startLine, endLine))
          || (isInRange(to.getColumn - 1, startColumn, endColumn) && isInRange(to.getLine, startLine, endLine))) {
          result = entry :: result
        }
      }
    }

    result = result.sortWith(compareLength)

    // TODO extract

    result = buildASTResult(result)
    return result
  }

  private def isInRange(pos: Int, start: Int, end: Int): Boolean = {
    return ((start <= pos) && (pos <= end))
  }

  private def buildASTResult(astList: List[AST]): List[AST] = {
    var result = List[AST]()
    for (entry <- astList) {
      var toAdd = true
      for (resultEntry <- result; if toAdd) {
        if (entry.getPositionFrom.equals(resultEntry.getPositionFrom) && entry.getPositionTo.equals(resultEntry.getPositionTo)) {
          if ((entry.productArity > resultEntry.productArity)) {
            result = removeFromList(resultEntry, result)
          } else {
            toAdd = false
          }
        } else if (positionCut(entry, resultEntry) && (entry.getPositionFrom.getLine == resultEntry.getPositionFrom.getLine)) {
          val entryLength = entry.getPositionTo.getColumn - entry.getPositionFrom.getColumn
          val resultEntryLength = resultEntry.getPositionTo.getColumn - resultEntry.getPositionFrom.getColumn
          if (((entryLength == resultEntryLength) && (entry.productArity > resultEntry.productArity)) || (entryLength > resultEntryLength)) {
            result = removeFromList(resultEntry, result)
          } else {
            toAdd = false
          }
        }
      }
      if (toAdd) {
        result = entry :: result
      }
    }
    println("RESULT")
    println(result)
    return result;
  }

  private def positionCut(e1: AST, e2: AST): Boolean = {
    if ((e1.getPositionFrom < e2.getPositionFrom) && (e2.getPositionFrom < e1.getPositionTo)) {
      return true
    } else if ((e2.getPositionFrom < e1.getPositionFrom) && (e2.getPositionFrom < e1.getPositionTo)) {
      return true
    }
    return false
  }

  private def compareLength(e1: AST, e2: AST) = (e1.getPositionTo.getColumn - e1.getPositionFrom.getColumn) > (e2.getPositionTo.getColumn - e2.getPositionFrom.getColumn)

  private def removeFromList(element: AST, astList: List[AST]) = astList diff (List(element))

}
