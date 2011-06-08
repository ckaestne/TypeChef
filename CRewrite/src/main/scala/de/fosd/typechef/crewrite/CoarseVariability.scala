package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.parser.Opt
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.featureexpr.FeatureExpr.base
import de.fosd.typechef.featureexpr.{DefinedExternal, FeatureExpr}
import org.kiama.attribution.Attributable

/**
 * strategies to rewrite the parsed variability to use only Opt-nodes at Top-Level and at Statement-Level
 * all other Opt or Choice nodes are removed in the process
 * (the be precise we cannot remove Opt nodes, but we can ensure that their condition is *base*)
 */

class CoarseVariability  {

//    private val rewriteStrategy=everywherebu(rule {

//         })

    def rewrite(ast:AST):AST = {
//         rewriteStrategy(ast).get.asInstanceOf[AST]
        ast
    }


//    /** returns a choice of nodes, that should not contain inner variability */
//    def explodeVariability(ast:AST):Choice[AST] = {
////        ast.children
//
//    }


    /*** ensure the result is correct. returns true if at most the desired variability is left */
    def check(ast:Attributable): Boolean = ast match {
        case Opt(_,a:ExternalDef)=> check(a)//do not require feature=base
        case Opt(_,a:Statement)=> check(a)//do not require feature=base
        case Opt(f,a:Attribute) => (f==base) && check(a)
        case c:Choice[_]=>false//no choice nodes allowed
        case o => o.children.forall(check(_))
    }


}