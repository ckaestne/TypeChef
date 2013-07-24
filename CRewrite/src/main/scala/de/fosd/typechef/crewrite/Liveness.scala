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
// ⊑  = ⊆             // see MonotoneFW
// ∐  = ⋃            // combinationOperator
// ⊥  = ∅             // b
// i  = ∅
// E  = {FunctionDef} // see MonotoneFW
// F  = flowR
// Analysis_○ = exit
// Analysis_● = entry
class Liveness(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel) extends MonotoneFWId(env, udm, fm) with IntraCFG with UsedDefinedDeclaredVariables {

    // returns all declared variables with their annotation
    val declaresVar: PartialFunction[(Any), L] = {
        case a => addAnnotations(declares(a))
    }

    def gen(a: AST): L = {
        addAnnotations(uses(a))
    }

    def kill(a: AST):L = {
        addAnnotations(defines(a))
    }

    protected val i = l
    protected def b = l
    protected def combinationOperator(l1: L, l2: L) = union(l1, l2)

    // liveness analysis is a backward analysis (flowR)
    // so circle concerns exit conditions
    // and point concerns entry conditions
    protected def F(e: AST) = flowR(e)
    protected def circle(e: AST) = entrycache(e)
    protected def point(e: AST) = exitcache(e)
}
