package de.fosd.typechef.crefactor.backend.refactor

import de.fosd.typechef.parser.c.{TranslationUnit, Id, AST}
import de.fosd.typechef.crewrite.ASTEnv
import java.util

/**
 * Implements the process for renaming a function or an id.
 *
 * @author Andreas Janker
 */
object Rename extends Refactor {

  def refactorIsPossible(selection: Any, ast: AST, astEnv: ASTEnv, declUse: util.IdentityHashMap[Id, List[Id]], useDecl: util.IdentityHashMap[Id, List[Id]], name: String): Boolean = {

    var oldId: Id = null
    selection match {
      case i@Id(_) => oldId = i
      case _ => return false
    }

    /**
     * Checks with the following steps if renaming is possible.
     * 1. Retrieve if name is valid to the c standard
     * 2. Check if variable name is already used.
     * 3. If already used, check for shadowing in file scope, function scope and block scope.
     */


    // check name first
    if (!isValidName(name)) {
      return false
    }

    // check for shadowing
    ((!isDeclaredVarInScope(ast.asInstanceOf[TranslationUnit], declUse, name, oldId)) &&
      (!isDeclaredTypeDef(ast.asInstanceOf[TranslationUnit], declUse, name, oldId)) &&
      (!isDeclaredStructOrUnionDef(ast.asInstanceOf[TranslationUnit], declUse, name, oldId)))
  }

  def performRefactor(selection: Any, ast: AST, astEnv: ASTEnv, declUse: util.IdentityHashMap[Id, List[Id]], useDecl: util.IdentityHashMap[Id, List[Id]], newName: String): AST = {
    var oldId: Id = null
    selection match {
      case i@Id(_) => oldId = i
      case _ => return ast
    }
    var refactored = ast
    findAllConnectedIds(oldId, declUse, useDecl).foreach(entry =>
      refactored = replaceInAST(refactored, entry, entry.copy(name = newName)))
    refactored
  }
}
