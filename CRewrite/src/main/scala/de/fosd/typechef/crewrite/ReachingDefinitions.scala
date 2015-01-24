package de.fosd.typechef.crewrite

import org.kiama.rewriting.Rewriter._

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{DeclUseMap, UseDeclMap}
import de.fosd.typechef.featureexpr.FeatureModel

// implements reaching definitions (rd) dataflow analysis
// see http://en.wikipedia.org/wiki/Reaching_definition
// rd uses labels to distinguish between definitions; since we do not have
// labels we use HashCodes of identifiers for that
//
// instance of the reaching-definitions analysis using the monotone framework
// L  = P(Var*)
// ⊑  = ⊆             // see MonotoneFW
// ∐  = ⋃            // combinationOperator
// ⊥  = ∅             // b
// i  = ∅
// E  = {FunctionDef} // see MonotoneFW
// F  = flow

// major limitations:
//   - we use intraprocedural control flow (IntraCFG) which
//     is a conservative analysis for program flow
//     so the analysis will likely produce a lot
//     of false positives, because memory can be initialized
//     in a different function
class ReachingDefinitions(env: ASTEnv, dum: DeclUseMap, udm: UseDeclMap, fm: FeatureModel, f: FunctionDef) extends MonotoneFWIdLab(env, null, null, fm) with IntraCFG with CFGHelper with ASTNavigation with UsedDefinedDeclaredVariables {

    // we store all elements that can be created with gen and kill
    // in a cache, so that we pass each time the same object to the monotonefw
    // this is crucial as (Id("a"), 1234) != (Id("a"), 1234), since () is an operator
    // for tuple creation. all elements are checked internally for reference equality!
    private val cachePGT = new IdentityHashMapCache[PGT]()

    // set of free variables in f
    // {(x,?) x ∈ FV(S*)}
    // stored with identityHashCode instead of ?
    private var fvs = List[PGT]()

    // initialize caches with elements that are returned by gen/kill
    private def init(f: FunctionDef) = {

        def add2Caches(i: Id) = {
            cachePGT.update(i, (i, System.identityHashCode(i)))
            getFreshDefinitionFromUsage(cachePGT.lookup(i).get)

            if (udm.containsKey(i))
                for (x <- udm.get(i)) {
                    cachePGT.update(x, (x, System.identityHashCode(x)))
                    if (! isPartOf(x, f.stmt))
                        fvs ::= cachePGT.lookup(x).get
                    getFreshDefinitionFromUsage(cachePGT.lookup(x).get)
                }
        }

        // all patterns where definitions can occur in the code
        // !! keep consistent with defines in UsedDefinedDeclaredVariables trait !!
        val getdefs = manytd( query[AST] {
            case AssignExpr(PointerDerefExpr(i: Id), "=", _) => add2Caches(i)
            case AssignExpr(i: Id, "=", _) => add2Caches(i)
            case InitDeclaratorI(AtomicNamedDeclarator(_, i: Id, _), _, _) => add2Caches(i)
            case PostfixExpr(i: Id, SimplePostfixSuffix(_)) => add2Caches(i)
            case UnaryExpr(kind, i: Id) if kind == "++" || kind == "--" => add2Caches(i)
        })

        getdefs(f)
    }

    // get all definitions and return corresponding tuples (PGT) from cache
    // gen(d: y = f(x_1, ..., x_n) = {d}
    def gen(a: AST) = addAnnotations(defines(a).flatMap(cachePGT.lookup))

    // get all definitions that are killed in a; requires global knowledge of the function (defs(y));
    // to do so, we get all definitions in a, get their declarations and all possible usages,
    // finally, we get their tuples from the cache
    // kill(d: y = f(x_1, ..., x_n) = defs(y) \ {d}
    // the annotation here belongs to the original definition we get from a
    def kill(a: AST) = {
        var res = l

        for (d <- defines(a)) {
            // get all declarations of a definition, ...
            if (udm.containsKey(d)) {
                for (de <- udm.get(d)) {
                    // ... add it to the result set, ... defs(y)
                    res = res + ((cachePGT.lookup(de).get, env.featureExpr(d)))

                    // ... and traverse all usages of the declaration and add it to the result set.
                    for (u <- dum.get(de)) {
                        // ensure that we don't add the definition itself "\ {d}"
                        if (cachePGT.lookup(u).isDefined && !d.eq(u))
                            res = res + ((cachePGT.lookup(u).get, env.featureExpr(d)))
                    }
                }
            } else {
                // in case we already found a declaration
                res = res + ((cachePGT.lookup(d).get, env.featureExpr(d)))
            }
        }
        res
    }

    protected def F(e: AST) = flow(e)

    init(f)
    protected val i = addAnnotations(fvs)
    protected def b = l
    protected def combinationOperator(l1: L, l2: L) = union(l1, l2)

    //  in(a) = for p in pred(a) r = r + out(p)
    // out(a) = gen(a) + (in(a) - kill(a))
    protected def infunction(a: AST): L = combinator(a)
    protected def outfunction(a: AST): L = f_l(a)
}
