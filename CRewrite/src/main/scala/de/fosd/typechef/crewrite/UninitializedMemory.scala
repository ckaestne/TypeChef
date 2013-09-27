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
class UninitializedMemory(env: ASTEnv, dum: DeclUseMap, udm: UseDeclMap, fm: FeatureModel, f: FunctionDef) extends MonotoneFWIdLab(env, dum, udm, fm, f) with IntraCFG with CFGHelper with ASTNavigation {

    // get all function-call arguments
    def getFunctionCallArguments(a: AST): L = {
        var res = l

        for (c <- getRelevantFunctionCallArguments(a))
            res ++= fromCache(c)
        res
    }

    // returns all arguments (no references!) for a given AST (CFGStmt)
    private def getRelevantFunctionCallArguments(a: AST): List[Id] = {
        var resid = List[Id]()
        val fcs = filterAllASTElems[PostfixExpr](a)

        // get all ids except function names (should be extended to other ids, such as typedefs, too)
        val functionCallArguments = manybu(query {
            case i: Id => resid ::= i
            case PointerCreationExpr(i: Id) => resid = resid.filterNot(_.eq(i))
            // remove function-call identifiers, that have been previously added by bottom-up traversal
            case PostfixExpr(i: Id, _: FunctionCall) => resid = resid.filterNot(_.eq(i))
        })

        fcs.map(functionCallArguments)

        resid
    }

    // get all uninitialized variables
    def gen(a: AST): L = {
        var res = l
        val uninitializedVariables = manybu(query {
            case InitDeclaratorI(AtomicNamedDeclarator(_, i: Id, _), _, None) => res ++= fromCache(i)
        })

        uninitializedVariables(a)
        res
    }

    // get variables that get an assignment
    def kill(a: AST): L = {
        var res = l

        val assignments = manytd(query {
            case InitDeclaratorI(AtomicNamedDeclarator(_, i: Id, _), _, Some(_)) => res ++= fromCache(i, true)
            case AssignExpr(i: Id, "=", _) => res ++= fromCache(i, true)
        })

        assignments(a)

        res
    }

    protected def F(e: AST) = flow(e)

    protected val i = l
    protected def b = l
    protected def combinationOperator(l1: L, l2: L) = union(l1, l2)

    protected def incached(a: AST): L = combinatorcached(a)
    protected def outcached(a: AST): L = f_lcached(a)
}
