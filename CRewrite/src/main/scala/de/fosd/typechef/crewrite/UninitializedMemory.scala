package de.fosd.typechef.crewrite

import org.kiama.rewriting.Rewriter._

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.UseDeclMap
import de.fosd.typechef.featureexpr.FeatureModel

// implements a simple analysis of uninitalized memory
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
//
// Should be rewritten to a ReachingDefinition problem.
// the properties specified below do not match one of the MonotoneFW instances in [NNH99] so far.
// However, the analysis works for the simple examples written in UninitializedMemoryTest.scala
// instance of the monotone framework
// L  = P(Var*)
// ⊑  = ⊆             // see MonotoneFW
// ∐  = ⋃            // combinationOperator
// ⊥  = ∅             // b
// i  = ∅             // should be {(x,?)|x ∈ FV(S*)}
// E  = {FunctionDef} // see MonotoneFW
// F  = flow
// Analysis_○ = exit  // should be entry
// Analysis_● = entry // should be exit
class UninitializedMemory(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel) extends MonotoneFWId(env, udm, fm) with IntraCFG with CFGHelper with ASTNavigation {

    // get all function-call arguments
    def getFunctionCallArguments(a: AST): L = {
        var resid = Set[Id]()
        val fcs = filterAllASTElems[PostfixExpr](a)

        // get all ids except function names (should be extended to other ids, such as typedefs, too)
        // since we traverse bottom up, one result set is sufficient.
        val functionCallArguments = manybu(query {
            case i: Id => resid += i
            case PostfixExpr(i: Id, _: FunctionCall) => resid -= i
        })

        fcs.map(functionCallArguments)
        addAnnotations(resid)
    }

    // get all uninitialized variables
    def gen(a: AST): L = {
        var res = Set[Id]()
        val uninitializedVariables = manybu(query {
            case InitDeclaratorI(AtomicNamedDeclarator(_, i: Id, _), _, None) => res += i
        })

        uninitializedVariables(a)
        addAnnotations(res)
    }

    // get variables that get an assignment
    def kill(a: AST): L = {
        var res = Set[Id]()
        val assignments = manytd(query {
            case InitDeclaratorI(AtomicNamedDeclarator(_, i: Id, _), _, Some(_)) => res += i
            case AssignExpr(i@Id(_), "=", _) => res += i
        })

        assignments(a)
        addAnnotations(res)
    }

//    private def filli(f: FunctionDef) = {
//        var res = Map[Id, FeatureExpr]()
//        val getvars = manytd(query {
//            case i: Id => {
//                for (u <- udm.get(i))
//                    res += ((addFreshT(u), env.featureExpr(u)))
//            }
//        })
//
//        getvars(f)
//        res
//    }

    protected def F(e: AST) = flow(e)

    protected val i = l
    protected def b = l
    protected def combinationOperator(l1: L, l2: L) = union(l1, l2)

    protected def circle(e: AST) = exitcache(e)
    protected def point(e: AST) = entrycache(e)
}
