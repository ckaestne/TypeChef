package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.UseDeclMap
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureModel}

// implements reaching definitions dataflow analysis
// see http://en.wikipedia.org/wiki/Reaching_definition
//
// major limitations:
//   - we use intraprocedural control flow (IntraCFG) which
//     is a conservative analysis for program flow
//     so the analysis will likely produce a lot
//     of false positives, because memory can be initialized
//     in a different function
//   - uses flowR (pred) which does not seem to perform well
//     with large functions and a lot of variability
class ReachingDefintions(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel) extends MonotoneFW(env, udm, fm) with IntraCFG with CFGHelper with ASTNavigation with UsedDefinedDeclaredVariables {
    def gen(a: AST): Map[FeatureExpr, Set[Id]] = {
        addAnnotation2ResultSet(defines(a))
    }

    def kill(a: AST): Map[FeatureExpr, Set[Id]] = {
        addAnnotation2ResultSet(defines(a))
    }

    // flow functions (flow => succ and flowR => pred)
    protected def flow(e: AST) = flowPred(e)

    protected def unionio(e: AST) = outcached(e)
    protected def genkillio(e: AST) = incached(e)

    override def outcached(a: AST) = {
        outcache.lookup(a) match {
            case Some(v) => v
            case None => {
                val r = genkill(a)
                outcache.update(a, r)
                r
            }
        }
    }

    override def incached(a: AST) = {
        incache.lookup(a) match {
            case Some(v) => v
            case None => {
                val r = uniononly(a)
                incache.update(a, r)
                r
            }
        }
    }
}
