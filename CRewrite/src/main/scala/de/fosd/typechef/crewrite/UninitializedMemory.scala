package de.fosd.typechef.crewrite

import org.kiama.rewriting.Rewriter._

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{DeclUseMap, UseDeclMap}
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
// L  = P((Var* x Lab*))
// ⊑  = ⊆             // see MonotoneFW
// ∐  = ⋃            // combinationOperator
// ⊥  = ∅             // b
// i  = ∅             // empty is ok
// E  = {FunctionDef} // see MonotoneFW
// F  = flow
class UninitializedMemory(env: ASTEnv, dum: DeclUseMap, udm: UseDeclMap, fm: FeatureModel, f: FunctionDef) extends MonotoneFWIdLab(env, fm) with IntraCFG with CFGHelper with ASTNavigation {

    private val cachePGT = new IdentityHashMapCache[PGT]()

    private def init(f: FunctionDef) = {
        for (k <- getRelevantKillIds(f.stmt)) cachePGT.update(k, (k, System.identityHashCode(k)))
        for (g <- getRelevantGenIds(f.stmt)) cachePGT.update(g, (g, System.identityHashCode(g)))
        for (c <- getRelevantFunctionCallArguments(f.stmt)) cachePGT.update(c, (c, System.identityHashCode(c)))

    }

    // get all function-call arguments
    def getFunctionCallArguments(a: AST): L = {
        var res = l

        for (c <- getRelevantFunctionCallArguments(a))
            res += ((cachePGT.lookup(c).get, env.featureExpr(c)))
        res
    }

    private def getRelevantFunctionCallArguments(a: AST): List[Id] = {
        var resid = List[Id]()
        val fcs = filterAllASTElems[PostfixExpr](a)

        // get all ids except function names (should be extended to other ids, such as typedefs, too)
        // since we traverse bottom up, one result set is sufficient.
        val functionCallArguments = manybu(query {
            case i: Id => resid ::= i
            case PostfixExpr(i: Id, _: FunctionCall) => resid = resid.filterNot(_.eq(i))
        })

        fcs.map(functionCallArguments)

        resid
    }

    // get all uninitialized variables
    private def getRelevantGenIds(a: AST): List[Id] = {
        var res = List[Id]()
        val uninitializedVariables = manybu(query {
            case InitDeclaratorI(AtomicNamedDeclarator(_, i: Id, _), _, None) => res ::= i
        })

        uninitializedVariables(a)
        res
    }

    // get variables that get an assignment
    private def getRelevantKillIds(a: AST): List[Id] = {
        var res = List[Id]()

        def addDeclUse(i: Id) = {
            res ::= i
            if (udm != null && udm.containsKey(i)) {
                for (td <- udm.get(i)) {
                    res ::= td
                    if (dum != null && dum.containsKey(td))
                        for (tu <- dum.get(td))
                            res ::= tu
                }
            }
        }

        val assignments = manytd(query {
            case InitDeclaratorI(AtomicNamedDeclarator(_, i: Id, _), _, Some(_)) => addDeclUse(i)
            case AssignExpr(i: Id, "=", _) => addDeclUse(i)
        })

        assignments(a)

        res
    }

    def kill(a: AST): L = {
        var res = l

        for (k <- getRelevantKillIds(a)) res += ((cachePGT.lookup(k).get, env.featureExpr(k)))
        res
    }

    def gen(a: AST): L = {
        var res = l

        for (g <- getRelevantGenIds(a)) res += ((cachePGT.lookup(g).get, env.featureExpr(g)))
        res
    }

    protected def F(e: AST) = flow(e)

    init(f)
    protected val i = l
    protected def b = l
    protected def combinationOperator(l1: L, l2: L) = union(l1, l2)

    protected def incached(a: AST): L = combinatorcached(a)
    protected def outcached(a: AST): L = f_lcached(a)
}
