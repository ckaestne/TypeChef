package de.fosd.typechef.conditional

import org.kiama.attribution.Attribution._
import org.kiama._
import attribution.Attributable

/**
 * Simplified navigation support
 *
 * parentOpt, prevOpt, and nextOpt provide navigation between
 * Attributable nodes
 */
trait ConditionalNavigation {

  val parentOpt: Attributable ==> Opt[_] = attr { case a: Attributable => findParentOpt(a)}

  private def findParentOpt(a: Attributable): Opt[_] =
    a.parent match {
      case o: Opt[_] => o
      case c: Conditional[_] => Conditional.toOptList(c).head
      case a: Attributable => findParentOpt(a)
      case null => null
      case _ => assert(false, "cannot call findParent on instances other than Conditional or Opt"); null
    }

  val prevOpt: Attributable ==> Opt[_] = attr {
    case a =>
      a.prev[Attributable] match {
        case c: Conditional[_] => null
        case o: Opt[_] if (o.feature.isTautology) => null
        case o: Opt[_] if (!o.feature.isTautology) => {
          a match {
            case no: Opt[_] if (o.feature.equals(no.feature)) => o
            case _ => null
          }
        }
        case null => {
          a.parent match {
            case o: Opt[_] => o->prevOpt
            case _ => null
          }
        }
        case _ => assert(false, "cannot call prevOpt on instances other than Choice, One, or Opt"); null
      }
  }

  val nextOpt: Attributable ==> Opt[_] = attr {
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
        case null => {
          a.parent match {
            case o: Opt[_] => o->nextOpt
            case _ => null
          }
        }
        case _ => assert(false, "cannot call nextOpt on instances other than Choice, One, or Opt"); null
      }
  }

  val isVariable: Attributable ==> Boolean = attr {
    case a =>
      a match {
        case _: Conditional[_] => true
        case o: Opt[_] if (o.feature.isTautology) => a.parent[Attributable]->isVariable
        case o: Opt[_] if (!o.feature.isTautology) => true
        case e: Attributable if (a.isRoot) => false
        case e: Attributable => a.parent[Attributable]->isVariable
        case _ => assert(false, "invalid element"); false
      }
  }

  private def lastChoice[T <: Attributable](x: Choice[T]): T =
    x.elseBranch match {
      case c: Choice[T] => lastChoice(c)
      case One(c) => c
    }

  private def firstChoice[T <: Attributable](x: Choice[T]): T =
    x.thenBranch match {
      case c: Choice[T] => firstChoice(c)
      case One(c) => c
    }
}

