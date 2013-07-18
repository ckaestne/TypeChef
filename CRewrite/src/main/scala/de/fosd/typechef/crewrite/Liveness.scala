package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.UseDeclMap

import de.fosd.typechef.conditional.Opt

// liveness analysis based on monotone framework
// liveness computes all variables that are used before their next write
//
// cf. http://www.cs.colostate.edu/~mstrout/CS553/slides/lecture03.pdf
// page 5
//  in(n) = gen(n) + (out(n) - kill(n))
// out(n) = for s in succ(n) r = r + in(s); r
//
// instance of the liveness analysis using the monotone framework
// L  = P(Var*)
// ⊑  = ⊆             // see MonotonFW
// ∐  = ⋃            // combinationOperator
// ⊥  = ∅             // b
// i  = ∅
// E  = {FunctionDef} // see MonotoneFW
// F  = flowR
class Liveness(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel) extends MonotoneFWId(env, udm, fm) with IntraCFG with UsedDefinedDeclaredVariables {

    // returns all declared variables with their annotation
    val declaresVar: PartialFunction[(Any), Map[FeatureExpr, Set[Id]]] = {
        case a => addAnnotations(declares(a))
    }

    def gen(a: AST): Map[FeatureExpr, Set[Id]] = { addAnnotations(uses(a)) }
    def kill(a: AST): Map[FeatureExpr, Set[Id]] = { addAnnotations(defines(a)) }

    protected val i = Map[Id, FeatureExpr]()
    protected def b = Map[Id, FeatureExpr]()
    protected def combinationOperator(r: L, f: FeatureExpr, s: Set[Id]) = union(r, f, s)

    // liveness analysis is a backward analysis (flowR)
    // so circle concerns exit conditions
    // and point concerns entry conditions
    protected def F(e: AST) = flowR(e)
    protected def circle(e: AST) = exitcache(e)
    protected def point(e: AST) = entrycache(e)
}
