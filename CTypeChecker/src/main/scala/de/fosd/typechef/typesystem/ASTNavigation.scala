package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.c._
import org.kiama.attribution.Attribution._
import org.kiama._
import attribution.Attributable
import de.fosd.typechef.conditional._

/**
 * Simplified navigation support
 *
 * prevAST and parentAST provide navigation between
 * AST nodes not affected by Opt and Choice nodes
 * (those are just flattened)
 */
trait ASTNavigation {

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
            a.prev[Attributable] match {
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


    protected def outer[T](f: AST ==> T, init: () => T, e: AST): T =
        if (e -> prevOrParentAST != null) f(e -> prevOrParentAST)
        else
            init()
}