package de.fosd.typechef.crefactor.backend.refactor

import de.fosd.typechef.parser.c.{Expr, Id, AST}
import de.fosd.typechef.crewrite.ASTEnv
import java.util

/**
 * Implementation of the refactoring extract method.
 */
object ExtractMethod extends CRefactor {

  def refactorIsPossible(userSelection: Any, ast: AST, astEnv: ASTEnv, declUse: util.IdentityHashMap[Id, List[Id]], useDecl: util.IdentityHashMap[Id, List[Id]], name: String): Boolean = {
    userSelection match {
      case l: List[AST] => l.foreach(entry => println(entry.range))
      case _ => false
    }
    false
  }

  def performRefactor(selection: Any, ast: AST, astEnv: ASTEnv, declUse: util.IdentityHashMap[Id, List[Id]], useDecl: util.IdentityHashMap[Id, List[Id]], name: String): AST = {
    selection match {
      case l: List[Expr] => performExtractMethodOnExpression(l, ast, astEnv, declUse, useDecl, name)
      case l: List[AST] => ast
      case _ => ast
    }
  }

  private def performExtractMethodOnExpression(selection: List[Expr], ast: AST, astEnv: ASTEnv, declUse: util.IdentityHashMap[Id, List[Id]], useDecl: util.IdentityHashMap[Id, List[Id]], name: String): AST = {
    null
  }
}
