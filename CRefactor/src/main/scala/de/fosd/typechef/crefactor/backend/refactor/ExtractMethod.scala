package de.fosd.typechef.crefactor.backend.refactor

import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.crewrite.{ConditionalNavigation, CASTEnv}
import de.fosd.typechef.featureexpr.bdd.True

/**
 * Implements the process of extracting a method.
 */
object ExtractMethod extends ConditionalNavigation {

  /**
   * Retrieves if selected statements are part of a function.
   */
  def isPartOfAFunction(selection: List[AST], ast: AST): Boolean = {
    val env = CASTEnv.createASTEnv(ast)
    for (entry <- selection) {
      Helper.getFunctionDefOpt(entry, env) match {
        case null => return false
        case _ =>
      }
    }
    true
  }

  /**
   * Retrieves if selection contains conditional compilation directives.
   */
  def isConditional(selection: List[AST], ast: AST): Boolean = {
    val env = CASTEnv.createASTEnv(ast)
    for (entry <- selection) {
      println(parentOpt(entry, env).feature)
      parentOpt(entry, env).feature match {
        case True => return true
        case _ =>
      }
    }
    false
  }
}
