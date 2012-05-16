package de.fosd.typechef.typesystem

import java.util.IdentityHashMap
import de.fosd.typechef.conditional.One
import de.fosd.typechef.parser.c._

// store def use chains
// we store Id elements of AST structures that represent a definition (key element of defuse)
// and a use (value element of defuse)
trait CDefUse extends CEnv {
  protected val defuse: IdentityHashMap[Id, List[Id]] = new IdentityHashMap()

  private[typesystem] def clear() {defuse.clear()}

  def addExprUse(expr: Expr, env: Env) = {
    expr match {
      case i@Id(name) => {
        env.varEnv.getAstOrElse(name, null) match {
          case o: One[_] if (o.value.isInstanceOf[Id]) => {
            val key = o.value.asInstanceOf[Id]
            defuse.put(key, defuse.get(key) ++ List(i))
          }
          case x => assert(false, x + " not expected here; defuse")
        }
      }
      case x => assert(false, x + " not expected here; defuse")
    }
  }

  def addDeclaratorDef(decl: Declarator, env: Env) = {
    decl match {
      case AtomicNamedDeclarator(_, i, _) => defuse.put(i, List())
      case x => assert(false, x + " not expected here; defuse")
    }
  }
}
