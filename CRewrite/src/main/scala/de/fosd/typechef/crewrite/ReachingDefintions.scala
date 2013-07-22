package de.fosd.typechef.crewrite

import org.kiama.rewriting.Rewriter._

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.UseDeclMap
import de.fosd.typechef.featureexpr.FeatureModel

// implements reaching definitions (rd) dataflow analysis
// see http://en.wikipedia.org/wiki/Reaching_definition
// rd uses labels to distinguish between definitions; since we do not have
// labels we use the hashcodes of cfg statements for that
//
// instance of the reaching-definitions analysis using the monotone framework
// L  = P(Var*)
// ⊑  = ⊆             // see MonotoneFW
// ∐  = ⋃            // combinationOperator
// ⊥  = ∅             // b
// i  = ∅
// E  = {FunctionDef} // see MonotoneFW
// F  = flow
// Analysis_○ = entry
// Analysis_● = exit


// major limitations:
//   - we use intraprocedural control flow (IntraCFG) which
//     is a conservative analysis for program flow
//     so the analysis will likely produce a lot
//     of false positives, because memory can be initialized
//     in a different function
class ReachingDefintions(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel, f: FunctionDef) extends MonotoneFWIdLab(env, udm, fm) with IntraCFG with CFGHelper with ASTNavigation with UsedDefinedDeclaredVariables {



    private def initdefs(f: FunctionDef) = {

    }

    private def initi(f: FunctionDef): L = {
        var res = l

        val getdefs = manytd{ query {
            case AssignExpr(i: Id, "=", _) => {
                for (d <- udm.get(i))
                    res += ((Id(d.name + "_" + System.identityHashCode(d).toString), env.featureExpr(d)))
            }
            case InitDeclaratorI(AtomicNamedDeclarator(_, i: Id, _), _, _) => {
                for (d <- udm.get(i))
                    res += ((Id(d.name + "_" + System.identityHashCode(d).toString), env.featureExpr(d)))
            }
        }}

        getdefs(f)
        res
    }

    def gen(a: AST) = addAnnotations(defines(a))
    def kill(a: AST) = addAnnotations(defines(a))

    protected def F(e: AST) = flow(e)
    protected def circle(e: AST) = entrycache(e)
    protected def point(e: AST) = exitcache(e)

    protected val i = initi(f)
    println(i)
    protected def b = l
    protected def combinationOperator(l1: L, l2: L) = union(l1, l2)
}
