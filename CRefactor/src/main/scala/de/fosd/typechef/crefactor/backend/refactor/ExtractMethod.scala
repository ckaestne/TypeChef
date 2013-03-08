package de.fosd.typechef.crefactor.backend.refactor

import de.fosd.typechef.crefactor.backend.ASTSelection
import de.fosd.typechef.parser.c._
import de.fosd.typechef.crefactor.frontend.util.Selection
import de.fosd.typechef.parser.c.PostfixExpr
import de.fosd.typechef.parser.c.Id
import de.fosd.typechef.parser.c.Constant
import de.fosd.typechef.parser.c.ReturnStatement
import de.fosd.typechef.parser.c.SwitchStatement
import de.fosd.typechef.parser.c.PointerDerefExpr
import de.fosd.typechef.parser.c.ElifStatement
import de.fosd.typechef.parser.c.ExprList
import scala.Some
import de.fosd.typechef.parser.c.FunctionDef
import de.fosd.typechef.parser.c.SizeOfExprU
import de.fosd.typechef.parser.c.NAryExpr
import de.fosd.typechef.parser.c.DoStatement
import de.fosd.typechef.parser.c.PointerCreationExpr
import de.fosd.typechef.parser.c.AssignExpr
import de.fosd.typechef.parser.c.CastExpr
import de.fosd.typechef.parser.c.ConditionalExpr
import de.fosd.typechef.parser.c.CaseStatement
import de.fosd.typechef.parser.c.ForStatement
import de.fosd.typechef.parser.c.IfStatement
import de.fosd.typechef.parser.c.WhileStatement
import de.fosd.typechef.parser.c.StringLit
import de.fosd.typechef.parser.c.SizeOfExprT
import de.fosd.typechef.parser.c.UnaryOpExpr
import de.fosd.typechef.parser.c.UnaryExpr
import java.util.Collections
import java.util
import de.fosd.typechef.crefactor.Morpheus
import de.fosd.typechef.crefactor.util.Configuration

/**
 * Implements the strategy of extracting a function.
 */
// TODO Replace original ExtractFunction -> Delete and Refactor
object ExtractMethod extends ASTSelection with Refactor {

  def getSelectedElements(morpheus: Morpheus, selection: Selection): List[AST] = {
    // TODO Missed Control Statements
    val ids = filterASTElementsForFile[Id](filterASTElems[Id](morpheus.getAST).par.filter(x => isInSelectionRange(x, selection)).toList, selection.getFilePath)

    def findMostUpwardExpr(element: Expr): Expr = {
      parentAST(element, morpheus.getASTEnv) match {
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
      val priorElement = findPriorASTElem[Statement](id, morpheus.getASTEnv)
      priorElement match {
        case None => null
        case _ => priorElement.get match {
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

    }

    def exploitStatements(statement: Statement): Statement = {
      try {
        // val parentStatement = parentAST(statement, astEnv) // debug purpose only
        parentAST(statement, morpheus.getASTEnv) match {
          case null =>
            assert(false, "An error during determine the preconditions occured.")
            statement
          case f: FunctionDef => statement
          case nf: NestedFunctionDef => statement
          case p =>
            if (isElementOfSelectionRange(p, selection)) exploitStatements(p.asInstanceOf[Statement])
            else statement
        }
      } catch {
        case _ => statement
      }
    }
    val uniqueSelectedStatements = Collections.newSetFromMap[Statement](new util.IdentityHashMap())
    val uniqueSelectedExpressions = Collections.newSetFromMap[Expr](new util.IdentityHashMap())

    ids.foreach(id => {
      val parent = findParent(id)
      parent match {
        case null =>
        case _ =>
          if (parent.get.isInstanceOf[Statement]) uniqueSelectedStatements.add(parent.get.asInstanceOf[Statement])
          else if (parent.get.isInstanceOf[Expr]) uniqueSelectedExpressions.add(parent.get.asInstanceOf[Expr])
      }
    })

    var parents: List[AST] = List()
    // TODO Optimize expensive array and list conversions
    if (!uniqueSelectedStatements.isEmpty) {
      parents = uniqueSelectedStatements.toArray(Array[Statement]()).toList
      uniqueSelectedStatements.clear()
      parents.foreach(statement => uniqueSelectedStatements.add(exploitStatements(statement.asInstanceOf[Statement])))
      parents = uniqueSelectedStatements.toArray(Array[Statement]()).toList
    } else parents = uniqueSelectedExpressions.toArray(Array[Expr]()).toList

    logger.info("InlineFuncOptionSelector " + parents.sortWith(comparePosition))
    parents.sortWith(comparePosition)
  }

  def getAvailableIdentifiers(morpheus: Morpheus, selection: Selection): List[Id] = getSelectedElements(morpheus, selection).isEmpty match {
    case true => null
    case false => List[Id]() // returns a empty list to signalize a valid selection was found
  }

  def isAvailable(morpheus: Morpheus, selection: Selection): Boolean = {
    val selectedElements = getSelectedElements(morpheus, selection)
    // Validate selection
    if (selectedElements.isEmpty) return false
    if (!selectedElements.par.forall(element => isPartOfAFunction(element, morpheus))) return false

    // retrieve if selected elements are part of the same compound stmts
    findPriorASTElem[CompoundStatement](selectedElements.head, morpheus.getASTEnv) match {
      case Some(c) => if (!selectedElements.par.forall(element => isElementOfEqCompStmt(element, c, morpheus))) return false
      case _ => return false // not element of an ccStmt
    }
    true
  }

  def extract(morpheus: Morpheus, selection: List[AST], funcName: String): AST = {
    verifyFunctionName(funcName, selection, morpheus)

    morpheus.getAST
  }

  private def isPartOfAFunction(toValidate: AST, morpheus: Morpheus): Boolean = {
    findPriorASTElem[FunctionDef](toValidate, morpheus.getASTEnv) match {
      case Some(f) => true
      case _ => false
    }
  }

  private def isElementOfEqCompStmt(element: AST, compStmt: CompoundStatement, morpheus: Morpheus): Boolean = getCompoundStatement(element, morpheus).eq(compStmt)

  private def getCompoundStatement(element: AST, morpheus: Morpheus): CompoundStatement = {
    findPriorASTElem[CompoundStatement](element, morpheus.getASTEnv) match {
      case Some(c) => c
      case _ => null
    }
  }

  private def verifyFunctionName(funcName: String, selection: List[AST], morpheus: Morpheus) {
    assert(isValidName(funcName), Configuration.getInstance().getConfig("refactor.extractFunction.failed.shadowing"))
    // Check for shadowing with last statement of the extraction compound statement
    assert(!isShadowed(funcName, getCompoundStatement(selection.head, morpheus).innerStatements.last.entry, morpheus), Configuration.getInstance().getConfig("default.error.invalidName"))
  }
}
