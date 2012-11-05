package de.fosd.typechef.crefactor.backend.refactor

import de.fosd.typechef.parser.c._
import de.fosd.typechef.crewrite._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import java.util
import de.fosd.typechef.parser.c.PostfixExpr
import de.fosd.typechef.parser.c.CompoundStatement
import de.fosd.typechef.parser.c.Id
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.c.FunctionCall
import de.fosd.typechef.parser.c.AtomicNamedDeclarator
import de.fosd.typechef.parser.c.FunctionDef
import de.fosd.typechef.parser.c.VoidSpecifier

/**
 * Implements the process of extracting a method.
 */
object ExtractMethod extends ConditionalNavigation with ASTNavigation with Liveness {

  /**
   * Retrieves if selected statements are part of a function.
   */
  def isPartOfAFunction(selection: List[AST], ast: AST): Boolean = {
    val env = CASTEnv.createASTEnv(ast)
    for (entry <- selection) {
      /** Helper.getFunctionDefOpt(entry, env) match {
        case null => return false
        case _ =>
      }  */
    }
    getAllUsedIds(selection)
    true
  }

  /**
   * Retrieves if selection contains conditional compilation directives.
   */
  def isConditional(selection: List[AST], ast: AST): Boolean = {
    val env = CASTEnv.createASTEnv(ast)
    for (entry <- selection) {
      val feature = parentOpt(entry, env).feature
      if (!feature.equivalentTo(FeatureExprFactory.True)) {
        return true
      }
    }
    false
  }

  /**
   * Retrieves all externally used ids.
   */
  def getAllExternallyReferencedIds(usedIds: List[Id], defUse: util.IdentityHashMap[Id, List[Id]], ast: AST): List[Id] = {
    var env = CASTEnv.createASTEnv(ast)
    var ids = usedIds
    // Remove FunctionCalls first
    def removeFunctionCallIds(usedIds: List[Id], defUse: util.IdentityHashMap[Id, List[Id]], env: ASTEnv): List[Id] = {
      var filtered = List[Id]()
      for (id <- usedIds) {
        env.parent(id) match {
          case PostfixExpr(_, FunctionCall(_)) =>
          case _ => filtered = id :: filtered
        }
      }
      filtered
    }
    ids = removeFunctionCallIds(usedIds, defUse, env)
    var result = List[Id]()
    def filterUsedIds(usedIds: List[Id], defUse: util.IdentityHashMap[Id, List[Id]], result: List[Id]): (List[Id], List[Id]) = {
      val decl = Helper.findDecl(defUse, usedIds.head)
      val defUseIds = decl :: defUse.get(decl)
      (usedIds.diff(defUseIds).diff(List(usedIds.head)), result ::: defUseIds.diff(usedIds))
    }

    while (!ids.isEmpty) {
      var (filtered, newResult) = filterUsedIds(ids, defUse, result)
      result = newResult
      ids = filtered
    }
    result
  }

  /**
   * Retrieves all used ids of the selection.
   */
  def getAllUsedIds(selection: List[AST]): List[Id] = {
    var result = List[Id]()
    for (entry <- selection) {
      result = result ::: filterASTElems[Id](entry)
    }
    result
  }

  /**
   * Checks if id is declared in the selection.
   */
  private def idIsDeclaredInSelection(id: Id, usedIds: List[Id], defuse: util.IdentityHashMap[Id, List[Id]]): Boolean = {
    val decl = Helper.findDecl(defuse, id)
    usedIds.foreach(x => if (x.eq(decl)) true)
    false
  }


  /**
   * Retrieves all ids to be returned by the function.
   */
  def returnIds(parameterIds: List[Id], refIds: List[Id]): List[Id] = {
    refIds.diff(parameterIds)
  }

  /**
   * Perform the extract method refactoring. Note preconditions must be checked first!
   */
  def doExtract(functionName: String, selection: List[AST], ast: AST, defuse: util.IdentityHashMap[Id, List[Id]]): AST = {
    // TODO Conditional
    def buildFunctionSpecifiers(void: Boolean): List[Opt[Specifier]] = {
      List(Opt(FeatureExprFactory.True, VoidSpecifier()))
    }
    def generateFunction(functionName: String, selection: List[AST]): Opt[_] = {
      val opt = Opt(FeatureExprFactory.True, FunctionDef(buildFunctionSpecifiers(true), AtomicNamedDeclarator(List(), Id(functionName), List()), List(), CompoundStatement(List())))
      opt
    }
    println("genFunc" + generateFunction(functionName, selection))
    ast
  }
}
