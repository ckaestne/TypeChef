package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.{FunctionDef, Id, AST, ASTEnv}
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.kiama.attribution.Attribution._

/**
 * Created with IntelliJ IDEA.
 * User: jl
 * Date: 02.11.13
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */
class SimpleLiveness(env: ASTEnv) extends IntraCFG with UsedDefinedDeclaredVariables {
    val in: AST => Set[Id] =
    circular (Set[Id]()) {
        case _: FunctionDef => Set()
        case s => uses(s).toSet ++ out(s).diff(defines(s).toSet)
    }

    val out: AST => Set[Id] =
    circular (Set[Id]()) {
        case _: FunctionDef => Set()
        case s => succ(s, FeatureExprFactory.empty, env).map(_.entry).toSet.flatMap(in)
    }
}