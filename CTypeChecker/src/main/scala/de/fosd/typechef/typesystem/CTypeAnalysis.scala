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

trait CTypeAnalysis extends ASTNavigation {
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

    val env: AST ==> LookupTable = /*attr*/ {
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
    private val outerEnv: AST ==> LookupTable = /*attr*/ {
        case e: AST =>
            if (e -> prevAST != null) e -> prevAST -> env
            else
            if (e -> parentAST != null) e -> parentAST -> env
            else
                initTable //should not occur when checking entire TranslationUnits
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