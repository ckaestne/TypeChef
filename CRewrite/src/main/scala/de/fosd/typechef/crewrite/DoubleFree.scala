package de.fosd.typechef.crewrite

import org.kiama.rewriting.Rewriter._

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.UseDeclMap
import de.fosd.typechef.featureexpr.FeatureModel

// implements a simple analysis of double-free
// freeing memory multiple times
// https://www.securecoding.cert.org/confluence/display/seccode/MEM31-C.+Free+dynamically+allocated+memory+exactly+once
// MEM31-C
//
// major limitations:
//   - without an alias analysis we are not capable of
//     detecting double frees called on different pointers
//     directing to the same memory location
//   - we use intraprocedural control flow (IntraCFG) which
//     is a conservative analysis for program flow
//     so the analysis will likely produce a lot
//     of false positives
//   - the analysis has several limitations regarding pointer arithmetic
//     and produces a false positives for example for:
//     free(a[0]) vs. free(a[1]) and so on.

// in different projects developers use different implementations for
// the function free, e.g.:
// linux: kfree for kernel memory deallocation
// openssl: OPENSSL_free (actually CRYPTO_free; OPENSSL_free is a CPP macro)
//
// instance of the monotone framework
// similar to liveness computation: we check whether the variable passed to free, lives out to another free call
// assignments in between kill our gen property of a call to free
// L  = P(Var*)
// ⊑  = ⊆             // see MonotoneFW
// ∐  = ⋃            // combinationOperator
// ⊥  = ∅             // b
// i  = ∅
// E  = {FunctionDef} // see MonotoneFW
// F  = flowR
// Analysis_○ = exit
// Analysis_● = entry
class DoubleFree(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel, casestudy: String) extends MonotoneFWId(env, udm, fm) with IntraCFG with CFGHelper with ASTNavigation {

    val freecalls = {
        if (casestudy == "linux") List("free", "kfree")
        else if (casestudy == "openssl") List("free", "CRYPTO_free")
        else List("free")
    }

    // Returns a set of Ids that have been reassigned with a new memory location.
    // We don't go for calls to standard memory allocation functions, such as
    // calloc, malloc, and realloc, since we do get a lot of false positives, when
    // neglecting reassignments. It doesn't matter where the pointer allocation comes
    // from, we only care about double freeing.
    def kill(a: AST): L = {
        var res = Set[Id]()
        val mempointers = manytd(query {
            case AssignExpr(target@Id(_), "=", _) => {
                res += target
            }
        })

        mempointers(a)
        addAnnotations(res)
    }

    // returns a list of Ids with names of variables that a freed
    // by call to free or realloc
    // using the terminology of liveness we return pointers that have that are in use
    def gen(a: AST): L = {

        var res = Set[Id]()

        // add a free target independent of & and *
        def addFreeTarget(e: Expr) {
            // free(a->b)
            val sp = filterAllASTElems[PointerPostfixSuffix](e)
            if (!sp.isEmpty) {
                for (spe <- filterAllASTElems[Id](sp.reverse.head))
                    res += spe

                return
            }

            // free(a[b])
            val ap = filterAllASTElems[ArrayAccess](e)
            if (!ap.isEmpty) {
                for (ape <- filterAllASTElems[PostfixExpr](e)) {
                    ape match {
                        case PostfixExpr(i@Id(_), ArrayAccess(_)) => res += i
                        case _ =>
                    }
                }

                return
            }

            // free(a)
            val fp = filterAllASTElems[Id](e)

            for (ni <- fp)
                res += ni
        }


        val freedpointers = manytd(query {
            // realloc(*ptr, size) is used for reallocation of memory
            case PostfixExpr(i@Id("realloc"), FunctionCall(l)) => {
                // realloc has two arguments but more than two elements may be passed to
                // the function. this is the case when elements form alternative groups, such as,
                // realloc(#ifdef A aptr #else naptr endif, ...)
                // so we check from the start whether parameter list elements
                // form alternative groups. if so we look for Ids in each
                // of the alternative elements. if not we stop, because then we encounter
                // a size element.
                var actx = List(l.exprs.head.feature)
                var finished = false

                for (ni <- filterAllASTElems[Id](l.exprs.head.entry))
                    res += ni

                for (ce <- l.exprs.tail) {
                    if (actx.reduce(_ or _) isTautology fm)
                        finished = true

                    if (!finished && actx.forall(_ and ce.feature isContradiction fm)) {
                        for (ni <- filterAllASTElems[Id](ce.entry))
                            res += ni
                        actx ::= ce.feature
                    } else {
                        finished = true
                    }

                }

            }
            // calls to free or to derivatives of free
            case PostfixExpr(Id(n), FunctionCall(l)) => {

                if (freecalls.contains(n)) {
                    for (e <- l.exprs) {
                        addFreeTarget(e.entry)
                    }
                }
            }

        })

        freedpointers(a)
        addAnnotations(res)
    }

    protected def F(e: AST) = flowR(e)

    protected val i = l
    protected def b = l
    protected def combinationOperator(l1: L, l2: L) = union(l1, l2)

    protected def incache(a: AST): L = f_lcached(a)
    protected def outcache(a: AST): L = combinatorcached(a)
}
