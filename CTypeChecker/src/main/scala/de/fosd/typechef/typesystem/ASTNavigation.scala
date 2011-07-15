package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.c._
import org.kiama.attribution.Attribution._
import org.kiama._
import attribution.Attributable
import de.fosd.typechef.parser.{Conditional, One, Opt, Choice}
import javax.management.remote.rmi._RMIConnection_Stub

/**
 * Simplified navigation support
 *
 * prevAST, nextAST, and parentAST provide navigation between
 * AST nodes not affected by Opt and Choice nodes
 * (those are just flattened)
 *
 * prevOpt, nextOpt, and parentOpt provide navigation between
 * Opt nodes not affected by AST nodes
 * (those are just flattened)
 */
trait ASTNavigation {

  val DEBUG = true

  val parentOpt: Attributable ==> Attributable = attr {case a: Attributable => findParentOpt(a)}
  // return type is too generic; Opt and Choice preferable
  private def findParentOpt(a: Attributable): Attributable = {
    a.parent match {
      case o: Opt[Attributable] if (!o.feature.isTautology()) => o
      case c: Choice[Attributable] if (!c.feature.isTautology()) => c
      case a: AST => findParentOpt(a)
      case _ => null
    }
  }

  val prevOpt: Attributable ==> Attributable = attr {
    case a =>
      a.prev[Attributable] match {
        case c: Conditional[_] => null
        case o: Opt[_] if (o.feature.isTautology) => null
        case o: Opt[_] if (!o.feature.isTautology) => {
          a match {
            case no: Opt[Attributable] if (o.feature.equals(no.feature)) => o
            case _ => null
          }
        }
        case null => null
        case _ => assert(false, "cannot call prevOpt on instances other than Choice, One, or Opt"); null
      }
  }

  val nextOpt: Attributable ==> Attributable = attr {
    case a =>
      a.next[Attributable] match {
        case c: Conditional[_] => null
        case o: Opt[_] if (o.feature.isTautology) => null
        case o: Opt[_] if (!o.feature.isTautology) => {
          a match {
            case po: Opt[_] if (o.feature.equals(po.feature)) => o
            case _ => null
          }
        }
        case null => null
        case _ => assert(false, "cannot call nextOpt on instances other than Choice, One, or Opt"); null
      }
  }

  val isVariable: Attributable ==> Boolean = attr {
    case a =>
      a match {
        case _: Conditional[_] => true
        case o: Opt[_] if (o.feature.isTautology) => a.parent[Attributable]->isVariable
        case o: Opt[_] if (!o.feature.isTautology) => true
        case e: AST if (a.isRoot) => false
        case e: AST => a.parent[Attributable]->isVariable
        case _ => assert(false, "invalid element"); false
      }
  }

  val parentAST: Attributable ==> AST = attr {case a: Attributable => findParent(a)}
  private def findParent(a: Attributable): AST =
    a.parent match {
      case o: Opt[_] => findParent(o)
      case c: Conditional[_] => findParent(c)
      case a: AST => a
      case _ => null
    }

  val prevAST: Attributable ==> AST = attr {
    case a =>
      (a.prev[Attributable]: @unchecked) match {
        case c: Choice[AST] => lastChoice(c)
        case o: One[AST] => o.value
        case a: AST => a
        case Opt(_, v: Choice[AST]) => lastChoice(v)
        case Opt(_, v: One[AST]) => v.value
        case Opt(_, v: AST) => v
        case null => {
          a.parent match {
            case o: Opt[_] => o -> prevAST
            case c: Choice[AST] => c -> prevAST
            case c: One[AST] => c -> prevAST
            case _ => null
          }
        }
      }
  }

  /**try first prev and if that does not exist, then parent*/
  val prevOrParentAST: Attributable ==> AST = {case a: Attributable => val p = a -> prevAST; if (p != null) p else a -> parentAST}

  private def prevOfChoice(c: Choice[AST]): AST = c -> prevAST match {
    case x: Choice[AST] => lastChoice(x)
    case x: AST => x
    case null => c.parent match {
      case x: Choice[AST] => prevOfChoice(x)
      case _ => null
    }
  }

  private def lastChoice[T <: AST](x: Choice[T]): T =
    x.elseBranch match {
      case c: Choice[T] => lastChoice(c)
      case One(c) => c
    }

  private def firstChoice[T <: AST](x: Choice[T]): T =
    x.thenBranch match {
      case c: Choice[T] => firstChoice(c)
      case One(c) => c
    }

  protected def outer[T](f: AST ==> T, init: () => T, e: AST): T =
    if (e -> prevOrParentAST != null) f(e -> prevOrParentAST)
    else
      init()
}