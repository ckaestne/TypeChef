package de.fosd.typechef.crewrite

import org.kiama.rewriting.Rewriter._

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.UseDeclMap
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureModel}

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

    private var defs = Set[PGT]()

    private def initb(f: FunctionDef) = {
        var res = l

        val getdefs = manytd{ query {
            case AssignExpr(i: Id, "=", _) => res = union(res, createLFromSetT(addAnnotations(Set((i, -1)))))
            case InitDeclaratorI(AtomicNamedDeclarator(_, i: Id, _), _, _) => res = union(res, createLFromSetT(addAnnotations(Set((i, -1)))))
        }}

        getdefs(f)
        res
    }

    private def initdefs(f: FunctionDef) = {
        val getdefs = manytd{ query {
            case AssignExpr(i: Id, "=", _) => defs += ((i, System.identityHashCode(i)))
            case InitDeclaratorI(AtomicNamedDeclarator(_, i: Id, _), _, _) => defs += ((i, System.identityHashCode(i)))
        }}

        getdefs(f)
    }

    def gen(a: AST) = {
        var res = Set[PGT]()

        for (d <- defines(a))
            res ++= defs.filter(_ == (d, System.identityHashCode(d)))

        addAnnotations(res)
    }

    def kill(a: AST) = {
        var res = Set[PGT]()

        // res contains all but the current definition
        for (d <- defines(a))
            res ++= defs.filter(r => r._1 == d && r._2 != System.identityHashCode(d))

        addAnnotations(res)
    }

    protected def F(e: AST) = flow(e)
    protected def circle(e: AST) = entrycache(e)
    protected def point(e: AST) = exitcache(e)

    protected val i = initb(f)
    initdefs(f)
    println(defs)
    println(i)
    protected def b = l
    protected def combinationOperator(l1: L, l2: L) = union(l1, l2)
}
