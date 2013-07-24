package de.fosd.typechef.crewrite

import org.kiama.rewriting.Rewriter._

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{DeclUseMap, UseDeclMap}
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
class ReachingDefintions(env: ASTEnv, dum: DeclUseMap, udm: UseDeclMap, fm: FeatureModel, f: FunctionDef) extends MonotoneFWIdLab(env, fm) with IntraCFG with CFGHelper with ASTNavigation with UsedDefinedDeclaredVariables {

    private val cachePGT = new IdentityHashMapCache[PGT]()
    private var fvs = Set[PGT]()


    private def init(f: FunctionDef) = {
        val getdefs = manytd( query {
            case AssignExpr(i: Id, "=", _) => {
                cachePGT.update(i, (i, System.identityHashCode(i)))
                getFresh(cachePGT.lookup(i).get)

                if (udm.containsKey(i))
                    for (x <- udm.get(i)) {
                        cachePGT.update(x, (x, System.identityHashCode(x)))
                        if (! isPartOf(x, f.stmt))
                            fvs += cachePGT.lookup(x).get
                        getFresh(cachePGT.lookup(x).get)
                    }
            }
            case InitDeclaratorI(AtomicNamedDeclarator(_, i: Id, _), _, _) => {
                cachePGT.update(i, (i, System.identityHashCode(i)))
                getFresh(cachePGT.lookup(i).get)

                if (udm.containsKey(i))
                    for (x <- udm.get(i)) {
                        cachePGT.update(x, (x, System.identityHashCode(x)))
                        if (! isPartOf(x, f.stmt))
                            fvs += cachePGT.lookup(x).get
                        getFresh(cachePGT.lookup(x).get)
                    }
            }
        })

        getdefs(f)
    }

    def gen(a: AST) = addAnnotations(defines(a).flatMap(cachePGT.lookup))
    def kill(a: AST) = {
        var res = l

        for (d <- defines(a)) {
            if (udm.containsKey(d)) {
                for (de <- udm.get(d)) {
                    res += ((cachePGT.lookup(de).get, env.featureExpr(d)))
                    for (u <- dum.get(de))
                        if (cachePGT.lookup(u).isDefined && !d.eq(u))
                            res += ((cachePGT.lookup(u).get, env.featureExpr(d)))
                }
            }
        }
        res
    }

    protected def F(e: AST) = flow(e)

    init(f)
    println(fvs)
    protected val i = addAnnotations(fvs)
    protected def b = l
    protected def combinationOperator(l1: L, l2: L) = union(l1, l2)
}
