package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.UseDeclMap

// liveness analysis based on monotone framework
// liveness computes all variables that are used before their next write
//
// instance of the liveness analysis using the monotone framework
// L  = P(Var*)
// ⊑  = ⊆             // see MonotoneFW
// ∐  = ⋃            // combinationOperator
// ⊥  = ∅             // b
// i  = ∅
// E  = {FunctionDef} // see MonotoneFW
// F  = flowR
class Liveness(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel) extends MonotoneFWId(env, udm, fm) with IntraCFG with UsedDefinedDeclaredVariables {

    // returns all declared variables with their annotation
    val declaresVar: PartialFunction[(Any), L] = {
        case a => addAnnotations(declares(a.asInstanceOf[AnyRef]))
    }

    def gen(a: AST): L = {
        addAnnotations(uses(a))
    }

    def kill(a: AST): L = {
        addAnnotations(defines(a))
    }

    protected val i = l
    protected def b = l
    protected def combinationOperator(l1: L, l2: L) = union(l1, l2)

    // liveness analysis is a backward analysis (flowR)
    // so circle concerns exit conditions
    // and point concerns entry conditions
    protected def F(e: AST) = flowR(e)

    // cf. http://www.cs.colostate.edu/~mstrout/CS553/slides/lecture03.pdf
    // page 5
    //  in(a) = gen(a) + (out(a) - kill(a))
    // out(a) = for s in succ(n) r = r + in(s); r
    protected def infunction(a: AST): L = f_l(a)
    protected def outfunction(a: AST): L = combinator(a)
}
