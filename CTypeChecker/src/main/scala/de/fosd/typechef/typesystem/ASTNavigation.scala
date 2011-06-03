package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.Opt
import de.fosd.typechef.parser.c._
import org.kiama.attribution.Attribution._
import org.kiama._
import attribution.Attributable

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
            case c: Choice[_] => findParent(c)
            case a: AST => a
            case _ => null
        }

    val prevAST: Attributable ==> AST = attr {
        case a =>
            a.prev[Attributable] match {
                case c: Choice[_] => lastChoice(c)
                case a: AST => a
                case Opt(_, v: Choice[AST]) => lastChoice(v)
                case Opt(_, v: AST) => v
                case null => {
                    a.parent match {
                        case o: Opt[_] => o -> prevAST
                        case c: Choice[AST] => c -> prevAST
                        case _ => null
                    }
                }
            }

    }
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
            case c => c
        }


    protected def outer[T](f: AST ==> T, init: () => T, e: AST): T =
        if (e -> prevAST != null) f(e -> prevAST)
        else
        if (e -> parentAST != null) f(e -> parentAST)
        else
            init()
}