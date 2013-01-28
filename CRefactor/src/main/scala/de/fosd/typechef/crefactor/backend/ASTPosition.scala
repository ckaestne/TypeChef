package de.fosd.typechef.crefactor.backend

import de.fosd.typechef.parser.c._
import de.fosd.typechef.crewrite.{ConditionalNavigation, ASTEnv, ASTNavigation}
import de.fosd.typechef.parser.c.Id
import scala.Some
import de.fosd.typechef.conditional.Opt
import java.util.Collections
import java.util


object ASTPosition extends ASTNavigation with ConditionalNavigation {

  /**
   * Retrieves all ids in the selected range.
   */
  def getSelectedIDs(ast: AST, file: String, startLine: Int, endLine: Int, startRow: Int, endRow: Int): List[Id] = {
    val result = filterASTElems[Id](ast).par.filter(x => isIdOfSelectionRange(x, startLine, endLine, startRow, endRow)).toList
    filterASTElementsForFile[Id](result, file)
  }

  /**
   * Retrieves all ast elements of the selected range.
   */
  def getElementsInRange(ast: AST, astEnv: ASTEnv, file: String, startLine: Int, endLine: Int, startRow: Int, endRow: Int): List[AST] = {
    val result = astEnv.keys().par.filter(x => {
      x match {
        case key: AST => isElementOfSelectionRange(key, startLine, endLine, startRow, endRow)
        case _ => false
      }
    })
    filterASTElementsForFile[AST](result.toList.asInstanceOf[List[AST]], file)
  }

  /**
   * Retrieves the selected statements or selected nested expression
   */
  def getSelectedExprOrStatements(ast: AST, astEnv: ASTEnv, file: String, startLine: Int, endLine: Int, startRow: Int, endRow: Int): List[AST] = {
    val ids = getSelectedIDs(ast, file, startLine, endLine, startRow, endRow)

    def findMostUpwardExpr(element: Expr): Expr = {
      parentAST(element, astEnv) match {
        case e: Id => findMostUpwardExpr(e)
        case e: Constant => findMostUpwardExpr(e)
        case e: StringLit => findMostUpwardExpr(e)
        case e: UnaryExpr => findMostUpwardExpr(e)
        case e: PostfixExpr => findMostUpwardExpr(e)
        case e: SizeOfExprT => findMostUpwardExpr(e)
        case e: SizeOfExprU => findMostUpwardExpr(e)
        case e: CastExpr => findMostUpwardExpr(e)
        case e: PointerDerefExpr => findMostUpwardExpr(e)
        case e: PointerCreationExpr => findMostUpwardExpr(e)
        case e: UnaryOpExpr => findMostUpwardExpr(e)
        case e: NAryExpr => findMostUpwardExpr(e)
        case e: ExprList => findMostUpwardExpr(e)
        case e: ConditionalExpr => findMostUpwardExpr(e)
        case e: AssignExpr => findMostUpwardExpr(e)
        case _ => element
      }
    }

    def findParent(id: Id): Some[AST] = {
      val priorElement = findPriorASTElem[Statement](id, astEnv)
      priorElement.get match {
        case ifState: IfStatement => Some(findMostUpwardExpr(id))
        case elIf: ElifStatement => Some(findMostUpwardExpr(id))
        case doState: DoStatement => Some(findMostUpwardExpr(id))
        case whileState: WhileStatement => Some(findMostUpwardExpr(id))
        case forState: ForStatement => Some(findMostUpwardExpr(id))
        case returnState: ReturnStatement => Some(findMostUpwardExpr(id))
        case switch: SwitchStatement => Some(findMostUpwardExpr(id))
        case caseState: CaseStatement => Some(findMostUpwardExpr(id))
        case s => Some(s)
      }

    }

    def exploitStatements(statement: Statement): Statement = {
      try {
        val parentStatement = parentAST(statement, astEnv)
        parentStatement match {
          case null =>
            println("shit")
            statement
          case f: FunctionDef => statement
          case nf: NestedFunctionDef => statement
          case p =>
            if (isElementOfSelectionRange(p, startLine, endLine, startRow, endRow)) {
              exploitStatements(p.asInstanceOf[Statement])
            } else {
              statement
            }
        }
      } catch {
        case _ =>
          statement
      }

    }
    val uniqueSelectedStatements = Collections.newSetFromMap[Statement](new util.IdentityHashMap())
    val uniqueSelectedExpressions = Collections.newSetFromMap[Expr](new util.IdentityHashMap())

    ids.foreach(id => {
      val parent = findParent(id)
      if (parent.get.isInstanceOf[Statement]) {
        uniqueSelectedStatements.add(parent.get.asInstanceOf[Statement])
      } else if (parent.get.isInstanceOf[Expr]) {
        uniqueSelectedExpressions.add(parent.get.asInstanceOf[Expr])
      }
    })
    var parents: List[AST] = List()
    if (!uniqueSelectedStatements.isEmpty) {
      parents = uniqueSelectedStatements.toArray(Array[Statement]()).toList
      uniqueSelectedStatements.clear()
      parents.foreach(statement => uniqueSelectedStatements.add(exploitStatements(statement.asInstanceOf[Statement])))
      parents = uniqueSelectedStatements.toArray(Array[Statement]()).toList
    } else {
      parents = uniqueSelectedExpressions.toArray(Array[Expr]()).toList
    }
    println("parents" + parents.sortWith(comparePosition))


    parents.sortWith(comparePosition)
  }

  /**
   * Retrieves all selected opts.
   */
  def getSelectedOpts(ast: AST, astEnv: ASTEnv, file: String, startLine: Int, endLine: Int, startRow: Int, endRow: Int): List[Opt[_]] = {
    var optElements = List[Opt[_]]()
    for (element <- getSelectedStatements(ast, astEnv, file, startLine, endLine, startRow, endRow)) {
      optElements = parentOpt(element, astEnv) :: optElements
    }
    optElements.reverse
  }

  /**
   * Retrieves all selected statements.
   */
  def getSelectedStatements(ast: AST, astEnv: ASTEnv, file: String, startLine: Int, endLine: Int, startRow: Int, endRow: Int): List[Statement] = {
    var statements = List[Statement]()

    /**
     * Work around to avoid duplicates, but also add statements with same hashvalues - only difference is their position
     */
    def addToList(statement: Statement, statements: List[Statement]): List[Statement] = {

      if (statements.contains(statement)) {
        /**
         * Check first if statements are the same - if so, check if position range is the same.
         */
        val sameStatements = statements.filter(x => (x.eq(statement) && x.range.eq(statement.range)))
        if (sameStatements.isEmpty) {
          statement :: statements
        } else {
          statements
        }
      } else {
        statement :: statements
      }
    }

    for (element <- getElementsInRange(ast, astEnv, file, startLine, endLine, startRow, endRow)) {
      if (element.isInstanceOf[Statement]) {
        statements = addToList(element.asInstanceOf[Statement], statements)
      } else {
        findPriorASTElem[Statement](element, astEnv) match {
          case Some(x) => statements = addToList(x, statements)
          case _ => return List[Statement]()

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
   * Checks if an ast element is in a certain range.
   */
  private def isInRange(pos: Int, start: Int, end: Int) = ((start <= pos) && (pos <= end))

  private def isIdOfSelectionRange(id: Id, startLine: Int, endLine: Int, startRow: Int, endRow: Int): Boolean = {
    /**
     * Annotated ids have often the same starting line. As workaround we only identify the id by its end value.
     */
    (isInRange(id.getPositionTo.getLine, startLine, endLine)) // && ((endRow <= id.getPositionTo.getColumn) || (startRow <= id.getPositionTo.getColumn)))
  }

  /**
   * Retrieves if element is in the selection range.
   */
  private def isElementOfSelectionRange(element: AST, startLine: Int, endLine: Int, startRow: Int, endRow: Int): Boolean = {
    if (!((isInRange(element.getPositionFrom.getLine, startLine, endLine) && isInRange(element.getPositionTo.getLine, startLine, endLine)))) {
      return false
    } else if (element.getPositionFrom.getLine == startLine) {
      return isInRange(element.getPositionFrom.getColumn, scala.math.min(startRow, endRow), scala.math.max(startRow, endRow))
    } else if (element.getPositionTo.getLine == endLine) {
      return isInRange(element.getPositionTo.getColumn, scala.math.min(startRow, endRow), scala.math.max(startRow, endRow))
    }
    true
  }

  /**
   * Remove all ast elements except those from the specified file.
   */
  private def filterASTElementsForFile[T <: AST](selection: List[T], file: String): List[T] = {
    // offset 5 because file path of id contains the string "file "
    val offset = 5
    selection.filter(p => p.getFile.get.regionMatches(true, offset, file, 0, file.length())).toList
  }
}
