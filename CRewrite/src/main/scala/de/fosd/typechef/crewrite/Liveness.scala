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
class Liveness(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel) extends MonotoneFW[Id](env, udm, fm) with IntraCFG with UsedDefinedDeclaredVariables {

    // returns all declared variables with their annotation
    val declaresVar: PartialFunction[(Any), Map[FeatureExpr, Set[Id]]] = {
        case a => addAnnotation2ResultSet(declares(a))
    }

    def gen(a: AST): Map[FeatureExpr, Set[Id]] = { addAnnotation2ResultSet(uses(a)) }
    def kill(a: AST): Map[FeatureExpr, Set[Id]] = { addAnnotation2ResultSet(defines(a)) }

    protected def flow(e: AST) = flowSucc(e)

    protected def unionio(e: AST) = incached(e)
    protected def genkillio(e: AST) = outcached(e)

    // we create fresh T elements (here Id) using a counter
    private var freshTctr = 0

    private def getFreshCtr: Int = {
        freshTctr = freshTctr + 1
        freshTctr
    }

    def t2T(i: Id) = Id(getFreshCtr + "_" + i.name)

    def t2SetT(i: Id) = {
        var freshidset = Set[Id]()

        if (udm.containsKey(i)) {
            for (vi <- udm.get(i)) {
                freshidset = freshidset.+(createFresh(vi))
            }
            freshidset
        } else {
            Set(addFreshT(i))
        }
    }
}
