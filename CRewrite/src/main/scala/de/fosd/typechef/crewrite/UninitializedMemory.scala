package de.fosd.typechef.crewrite

import org.kiama.rewriting.Rewriter._

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{DeclUseMap, UseDeclMap}
import de.fosd.typechef.featureexpr.FeatureModel

// implements a simple analysis of uninitialized memory
// https://www.securecoding.cert.org/confluence/display/seccode/EXP33-C.+Do+not+reference+uninitialized+memory
// EXP33
//
// major limitations:
//   - we use intraprocedural control flow (IntraCFG) which
//     is a conservative analysis for program flow
//     so the analysis will likely produce a lot
//     of false positives, because memory can be initialized
//     in a different function
//   - this analysis does not cover use of dynamically allocated
//     memory which is usually covered by other analysis tools.
//
// L  = P((Var* x Lab*))
// ⊑  = ⊆             // see MonotoneFW
// ∐  = ⋃            // combinationOperator
// ⊥  = ∅             // b
// i  = ∅             // empty is ok
// E  = {FunctionDef} // see MonotoneFW
// F  = flow
class UninitializedMemory(env: ASTEnv, dum: DeclUseMap, udm: UseDeclMap, fm: FeatureModel) extends MonotoneFWIdLab(env, dum, udm, fm) with IntraCFG with CFGHelper with ASTNavigation with UsedDefinedDeclaredVariables {

    // returns all arguments (no references!) for a given AST (CFGStmt)
    def getRelevantIdUsages(a: AST): L = {
        var resid = uses(a)
        var res = l
        val funccalls = filterAllASTElems[PostfixExpr](a)

        val filterids = manybu(query[AST] {
            // omit ids passed as a pointer to a function call
            case i: Id => if (findPriorASTElem[PointerCreationExpr](i, env).isDefined) resid = resid.filterNot(_.eq(i))
            // omit function-call identifiers
            case PostfixExpr(i: Id, _: FunctionCall) => resid = resid.filterNot(_.eq(i))
        })

        funccalls.map(filterids)

        for (c <- resid)
            res ++= fromCache(c)

        res
    }

    // get all uninitialized variables
    def gen(a: AST): L = {
        var res = l
        val uninitializedVariables = manybu(query[AST] {
            case InitDeclaratorI(AtomicNamedDeclarator(_, i: Id, _), _, None) => res ++= fromCache(i)
        })

        uninitializedVariables(a)
        res
    }

    // get variables that get an assignment
    def kill(a: AST): L = {
        var res = l

        val assignments = manytd(query[AST] {
            case InitDeclaratorI(AtomicNamedDeclarator(_, i: Id, _), _, Some(_)) => res ++= fromCache(i, true)
            case AssignExpr(i: Id, "=", _) => res ++= fromCache(i, true)
        })

        val pointerids = manytd(query[AST] {
            case i: Id => if (findPriorASTElem[PointerCreationExpr](i, env).isDefined) res ++= fromCache(i, true)
        })

        assignments(a)
        pointerids(a)

        res
    }

    protected def F(e: AST) = flow(e)

    protected val i = l
    protected def b = l
    protected def combinationOperator(l1: L, l2: L) = union(l1, l2)

    protected def infunction(a: AST): L = combinator(a)
    protected def outfunction(a: AST): L = f_l(a)
}
