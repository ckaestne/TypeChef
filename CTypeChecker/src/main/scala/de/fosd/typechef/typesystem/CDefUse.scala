package de.fosd.typechef.typesystem

import java.util.IdentityHashMap
import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional.One

// store def use chains
// we store Id elements of AST structures that represent a definition (key element of defuse)
// and a use (value element of defuse)
//
// the creation of defuse chains relies on the typesystem and it's data that is stored
// in Env instances; during the traversal of the typesystem visitor Env instances get filled
// with information about names, AST entries and their corresponding types
trait CDefUse extends CEnv {
  protected val defuse: IdentityHashMap[Id, List[Id]] = new IdentityHashMap()

  private[typesystem] def clear() {defuse.clear()}

  private def addSimpleDeclaratorDef(decl: Declarator) = {
    decl match {
      case AtomicNamedDeclarator(_, i, _) => defuse.put(i, List())
    }
  }

  private def getSimpleDeclaratorDef(decl: Declarator): Id = {
    decl match {
      case AtomicNamedDeclarator(_, i, _) => i
    }
  }

  def addDef(f: FunctionDef) = {
    f match {
      // TODO specifiers and parameters
      // parameters are definitions for uses in stmt
      case FunctionDef(specifiers, declarator, oldStyleParameters, _) => addSimpleDeclaratorDef(declarator)
    }
  }

  def addUse(f: PostfixExpr, env: Env) = {
    f match {
      // TODO params
      // params are uses of local or global variables
      case PostfixExpr(i@Id(name), FunctionCall(params)) => {
        env.varEnv.getAstOrElse(name, null) match {
          case One(FunctionDef(_, declarator, _, _)) => {
            val key = getSimpleDeclaratorDef(declarator)
            defuse.put(key, defuse.get(key) ++ List(i))
          }
        }
      }
    }
  }
}
