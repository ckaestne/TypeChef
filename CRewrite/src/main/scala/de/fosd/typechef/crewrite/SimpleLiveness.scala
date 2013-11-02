package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.{Id, AST, ASTEnv}
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.kiama.attribution.AttributionBase

/**
 * Created with IntelliJ IDEA.
 * User: jl
 * Date: 02.11.13
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */
class SimpleLiveness(env: ASTEnv) extends IntraCFG with UsedDefinedDeclaredVariables with AttributionBase {
    val in: AST => List[Id] =
    circular (List[Id]()) {
        case s => uses(s) ++ out(s).diff(defines(s))
    }

    val out: AST => List[Id] =
    circular (List[Id]()) {
        case s => succ(s, FeatureExprFactory.empty, env).map(_.entry).flatMap(in)
    }
}