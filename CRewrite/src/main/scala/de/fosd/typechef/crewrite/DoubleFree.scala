package de.fosd.typechef.crewrite

import org.kiama.rewriting.Rewriter._

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{DeclUseMap, UseDeclMap}
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
// similar to reaching definition computation: we check whether the variable passed to free is
// a "reach in" from another free call.
// L  = P(Var* x Lab*)
// ⊑  = ⊆             // see MonotoneFW
// ∐  = ⋃             // combinationOperator
// ⊥  = ∅             // b
// i  = ∅             // is empty because we are only interested in free/realloc and assignments
// E  = {FunctionDef} // see MonotoneFW
// F  = flow
class DoubleFree(env: ASTEnv, dum: DeclUseMap, udm: UseDeclMap, fm: FeatureModel, casestudy: String) extends MonotoneFWIdLab(env, dum, udm, fm) with IntraCFG with CFGHelper with ASTNavigation {

    val freecalls = {
        if (casestudy == "linux") List("free", "kfree")
        else if (casestudy == "openssl") List("free", "CRYPTO_free")
        else List("free")
    }

    def kill(a: AST): L = {
        var res = l
        val assignments = manytd(query[AST] {
            case AssignExpr(target: Id, "=", _) => res ++= fromCache(target, true)
        })

        assignments(a)
        res
    }

    def gen(a: AST): L = {
        var res = l

        // add a free target independent of & and *
        def addFreeTarget(e: Expr) {
            // free(_->b)
            val sp = filterAllASTElems[PointerPostfixSuffix](e)
            if (!sp.isEmpty) {
                for (spe <- filterAllASTElems[Id](sp.reverse.head)) res ++= fromCache(spe)

                return
            }

            // free(a[_])
            val ap = filterAllASTElems[ArrayAccess](e)
            if (!ap.isEmpty) {
                for (ape <- filterAllASTElems[PostfixExpr](e)) {
                    ape match {
                        case PostfixExpr(i: Id, ArrayAccess(_)) => res ++= fromCache(i)
                        case _ =>
                    }
                }

                return
            }

            // free(a)
            val fp = filterAllASTElems[Id](e)

            for (ni <- fp) res ++= fromCache(ni)
        }


        val freedpointers = manytd(query[AST] {
            // realloc(*ptr, size) is used for reallocation of memory
            case PostfixExpr(Id("realloc"), FunctionCall(l)) => {
                // realloc has two arguments but more than two elements may be passed to
                // the function. this is the case when elements form alternative groups, such as,
                // realloc(#ifdef A aptr #else naptr endif, ...)
                // so we check from the start whether parameter list elements
                // form alternative groups. if so we look for Ids in each
                // of the alternative elements. if not we stop, because then we encounter
                // a size element.
                var actx = List(l.exprs.head.condition)
                var finished = false

                for (ni <- filterAllASTElems[Id](l.exprs.head.entry)) res ++= fromCache(ni)

                for (ce <- l.exprs.tail) {
                    if (actx.reduce(_ or _) isTautology fm)
                        finished = true

                    if (!finished && actx.forall(_ and ce.condition isContradiction fm)) {
                        for (ni <- filterAllASTElems[Id](ce.entry)) res ++= fromCache(ni)
                        actx ::= ce.condition
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
        res
    }

    protected def F(e: AST) = flow(e)

    protected val i = l
    protected def b = l
    protected def combinationOperator(l1: L, l2: L) = union(l1, l2)

    protected def infunction(a: AST): L = combinator(a)
    protected def outfunction(a: AST): L = f_l(a)
}
