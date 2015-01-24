package de.fosd.typechef.crewrite

import org.kiama.rewriting.Rewriter._

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{DeclUseMap, UseDeclMap}
import de.fosd.typechef.featureexpr.FeatureModel

// implements a simple analysis of freeing memory that was not dynamically allocated
// https://www.securecoding.cert.org/confluence/display/seccode/MEM34-C.+Only+free+memory+allocated+dynamically
//
// major limitations:
//   - we use intraprocedural control flow (IntraCFG) which
//     is a conservative analysis for program flow
//     so the analysis will likely produce a lot
//     of false positives
//
// L  = P((Var* x Lab*))
// ⊑  = ⊆            // see MonotoneFW
// ∐  = ⋃            // combinationOperator
// ⊥  = ∅            // b
// i  = ∅            // should be {(x,?)|x ∈ FV(S*)}
// E  = {FunctionDef} // see MonotoneFW
// F  = flow
class XFree(env: ASTEnv, dum: DeclUseMap, udm: UseDeclMap, fm: FeatureModel, casestudy: String) extends MonotoneFWIdLab(env, dum, udm, fm) with IntraCFG with CFGHelper with ASTNavigation with UsedDefinedDeclaredVariables {

    private val freecalls = {
        if (casestudy == "linux") List("free", "kfree")
        else if (casestudy == "openssl") List("free", "CRYPTO_free")
        else List("free")
    }

    private val memcalls = {
        if (casestudy == "linux") List("malloc", "kmalloc")
        else List("malloc")
    }

    // get all declared variables without an initialization
    def gen(a: AST): L = {
        var res = l
        val variables = manytd(query[AST] {
            case InitDeclaratorI(AtomicNamedDeclarator(_, i: Id, _), _, None) => res ++= fromCache(i)
            case InitDeclaratorI(AtomicNamedDeclarator(_, i: Id, _), _, Some(initializer)) => {
                val pmallocs = filterASTElems[PostfixExpr](initializer)

                if (pmallocs.isEmpty) res ++= fromCache(i)
                else pmallocs.map {
                    case PostfixExpr(m: Id, _) if memcalls.contains(m.name) =>
                        if (! env.featureExpr(m) equivalentTo env.featureExpr(i)) res ++= fromCache(i)
                    case _ =>
                }
            }
        })

        variables(a)
        res
    }

    // get variables that get an assignment with malloc
    def kill(a: AST): L = {
        var res = l
        val assignments = manytd(query[AST] {
            case AssignExpr(target: Id, "=", source) => {
                val pmallocs = filterASTElems[PostfixExpr](source)

                pmallocs.map {
                    case PostfixExpr(i: Id, _) if memcalls.contains(i.name) =>
                        if (! env.featureExpr(i) equivalentTo env.featureExpr(target)) res ++= fromCache(target, true)
                    case _ =>
                }
            }
        })

        assignments(a)
        res
    }

    // returns a list of Ids with names of variables that a freed
    // by call to free or realloc
    // using the terminology of liveness we return pointers that have that are in use
    def freedVariables(a: AST) = {

        var res = List[Id]()

        // add a free target independent of & and *
        def addFreeTarget(e: Expr) {
            // free(a->b)
            val sp = filterAllASTElems[PointerPostfixSuffix](e)
            if (!sp.isEmpty) {
                for (spe <- filterAllASTElems[Id](sp.reverse.head))
                    res ::= spe

                return
            }

            // free(a[b])
            val ap = filterAllASTElems[ArrayAccess](e)
            if (!ap.isEmpty) {
                for (ape <- filterAllASTElems[PostfixExpr](e)) {
                    ape match {
                        case PostfixExpr(i@Id(_), ArrayAccess(_)) => res ::= i
                        case _ =>
                    }
                }

                return
            }

            // free(a)
            val fp = filterAllASTElems[Id](e)

            for (ni <- fp)
                res ::= ni
        }


        val freedvariables = manytd(query[AST] {
            // realloc(*ptr, size) is used for reallocation of memory
            case PostfixExpr(i@Id("realloc"), FunctionCall(l)) => {
                // realloc has two arguments but more than two elements may be passed to
                // the function. this is the case when elements form alternative groups, such as,
                // realloc(#ifdef A aptr #else naptr endif, ...)
                // so we check from the start whether parameter list elements
                // form alternative groups. if so we look for Ids in each
                // of the alternative elements. if not we stop, because then we encounter
                // a size element.
                var actx = List(l.exprs.head.condition)
                var finished = false

                for (ni <- filterAllASTElems[Id](l.exprs.head.entry))
                    res ::= ni

                for (ce <- l.exprs.tail) {
                    if (actx.reduce(_ or _) isTautology fm)
                        finished = true

                    if (!finished && actx.forall(_ and ce.condition isContradiction fm)) {
                        for (ni <- filterAllASTElems[Id](ce.entry))
                            res ::= ni
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

        freedvariables(a)
        res
    }

    protected def F(e: AST) = flow(e)

    protected val i = l
    protected def b = l
    protected def combinationOperator(l1: L, l2: L) = union(l1, l2)

    protected def infunction(a: AST): L = f_l(a)
    protected def outfunction(a: AST): L = combinator(a)
}
