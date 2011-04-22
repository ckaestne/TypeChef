package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.Opt
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr._
import org.kiama.attribution.DynamicAttribution._
import org.kiama._
import attribution.Attributable
import FeatureExpr.base

/**
 * kiama attributes for type analysis
 */

trait CTypeAnalysis {
    val gccBuiltins = List(
        "constant_p",
        "expect",
        "memcpy",
        "memset",
        "return_address", "va_start", "va_end")

    def initTable() = {
        var table = new LookupTable()
        for (name <- gccBuiltins) {
            table = table.add(new LFunctionDef("__builtin_" + name, "", 0, FeatureExpr.base))
        }
        table
    }

    val isStatementLevel: Attributable ==> Boolean = attr {
        case _: Statement => true
        case e => if (e.parent == null) false else (e.parent -> isStatementLevel)
    }

    val env: AST ==> LookupTable = attr {
        case e@FunctionDef(specifiers, DeclaratorId(pointers, Id(name), extensions), params, stmt) =>
            e -> outerEnv add (new LFunctionDef(name, "", 1, e -> presenceCondition))

        //function declaration and other declarations
        case e@ADeclaration(specifiers, initDecls) if (!(e -> isStatementLevel)) => {
            var table = e -> outerEnv
            for (initDecl <- initDecls.toList.flatten)
                initDecl.entry match {
                    case InitDeclaratorI(DeclaratorId(_, Id(name), _), _, _) => table = table add (new LDeclaration(name, "", 1, e -> presenceCondition))
                    case InitDeclaratorE(DeclaratorId(_, Id(name), _), _, _) => table = table add (new LDeclaration(name, "", 1, e -> presenceCondition))
                    case _ =>
                }
            table
        }
        case _: TranslationUnit => initTable
        case e: AST => e -> outerEnv

    }
    private val outerEnv: AST ==> LookupTable = attr {
        case e: AST =>
            if (e -> prevAST != null) e -> prevAST -> env
            else
            if (e -> parentAST != null) e -> parentAST -> env
            else
                initTable //should not occur when checking entire TranslationUnits
    }

    /**
     * prevAST and parentAST provide navigation between
     * AST nodes not affected by Opt and Choice nodes
     * (those are just flattened)
     */
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

    private def ppc(e: Attributable): FeatureExpr = if (e.parent == null) base else e.parent -> presenceCondition

    val presenceCondition: Attributable ==> FeatureExpr = childAttr {
        case e => {
            case c: Choice[_] if (e == c.thenBranch) => ppc(e) and c.feature
            case c: Choice[_] if (e == c.elseBranch) => ppc(e) andNot c.feature
            case o: Opt[_] => ppc(e) and o.feature
            case e => ppc(e)
        }
    }
}