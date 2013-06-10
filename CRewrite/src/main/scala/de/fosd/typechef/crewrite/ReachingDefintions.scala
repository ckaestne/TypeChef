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
class ReachingDefintions(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel) extends MonotoneFW[Id](env, udm, fm) with IntraCFG with CFGHelper with ASTNavigation with UsedDefinedDeclaredVariables {
    // we create fresh T elements (here Id) using a counter
    private var freshTctr = 0

    private def getFreshCtr: Int = {
        freshTctr = freshTctr + 1
        freshTctr
    }

    def t2T(i: Id) = Id(getFreshCtr + "_" + i.name)

    def t2SetT(i: Id) = {
        var freshidset = Set[Id]()

        if (udm.containsKey(i)) {
            for (vi <- udm.get(i)) {
                freshidset = freshidset.+(createFresh(vi))
            }
            freshidset
        } else {
            Set(addFreshT(i))
        }
    }

    def gen(a: AST): Map[FeatureExpr, Set[Id]] = {
        addAnnotation2ResultSet(uses(a))
    }

    def kill(a: AST): Map[FeatureExpr, Set[Id]] = {
        addAnnotation2ResultSet(defines(a))
    }

    // flow functions (flow => succ and flowR => pred)
    protected def F(e: AST) = flowR(e)

    override def exit(a: AST) = {
        exit_cache.lookup(a) match {
            case Some(v) => v
            case None => {
                val r = analysis_entry(a)
                exit_cache.update(a, r)
                r
            }
        }
    }

    override def entry(a: AST) = {
        entry_cache.lookup(a) match {
            case Some(v) => v
            case None => {
                val r = analysis_exit(a)
                entry_cache.update(a, r)
                r
            }
        }
    }
}
