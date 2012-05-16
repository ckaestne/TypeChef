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
          case x => assert(false, x + " not supported here; defuse")
        }
      }
      case x => assert(false, x + " not supported here; defuse")
    }
  }

  def addDeclaratorDef(decl: Declarator) = {
    decl match {
      case AtomicNamedDeclarator(_, i, _) => defuse.put(i, List())
      case x => assert(false, x + " not supported here; defuse")
    }
  }

  def addSpecifierDef(spec: Specifier) = {
    spec match {
      case StructOrUnionSpecifier(_, Some(x), _) => defuse.put(x, List())
      case _ => ;
    }
  }

  def addIdDef(i: Id) = {defuse.put(i, List())}
}
