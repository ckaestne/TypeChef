package de.fosd.typechef.crefactor.backend.refactor

import de.fosd.typechef.parser.c.{Id, AST}
import de.fosd.typechef.crewrite.ASTEnv
import java.util
import de.fosd.typechef.conditional.Opt

/**
 * Implementation of the refactoring extract method.
 */
object ExtractMethod extends CRefactor {

  def refactorIsPossible(selection: Any, ast: AST, astEnv: ASTEnv, declUse: util.IdentityHashMap[Id, List[Id]], useDecl: util.IdentityHashMap[Id, List[Id]], name: String): Boolean = {
    selection match {
      case l: List[Opt[_]] => println(selection)
      case _ => return false
    }
    // first step statements are completly marked
    false
  }

  def performRefactor(selection: Any, ast: AST, astEnv: ASTEnv, declUse: util.IdentityHashMap[Id, List[Id]], useDecl: util.IdentityHashMap[Id, List[Id]], name: String): AST = null
}
