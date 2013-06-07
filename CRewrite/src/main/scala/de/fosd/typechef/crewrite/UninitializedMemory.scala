package de.fosd.typechef.crewrite

import org.kiama.rewriting.Rewriter._

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.UseDeclMap
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureModel}

// implements a simple analysis of uninitalized memory
// see http://www.open-std.org/jtc1/sc22/wg14/www/docs/n1669.pdf 5.34
//
// major limitations:
//   - we use intraprocedural control flow (IntraCFG) which
//     is a conservative analysis for program flow
//     so the analysis will likely produce a lot
//     of false positives, because memory can be initialized
//     in a different function
//   - this analysis does not cover use of dynamically allocated
//     memory which is usually covered by other analysis tools.
class UninitializedMemory(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel) extends MonotoneFW[Id](env, udm, fm) with IntraCFG with CFGHelper with ASTNavigation {
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

    // get all Id's passed to a function
    def getFunctionCallArguments(e: AST) = {
        var res = Set[Id]()
        val fcs = filterAllASTElems[FunctionCall](e)
        val arguments = manybu(query{
            case i: Id => res += i
            case PostfixExpr(i@Id(_), FunctionCall(_)) => res -= i
        })

        fcs.map(arguments(_))
        addAnnotation2ResultSet(res)
    }

    // get all declared variables without an initialization
    def gen(a: AST): Map[FeatureExpr, Set[Id]] = {
        var res = Set[Id]()
        val variables = manytd(query{
            case InitDeclaratorI(AtomicNamedDeclarator(_, i, _), _, None) => res += i
        })

        variables(a)
        addAnnotation2ResultSet(res)
    }

    // get variables that get an assignment
    def kill(a: AST): Map[FeatureExpr, Set[Id]] = {
        var res = Set[Id]()
        val assignments = manytd(query{
            case AssignExpr(target@Id(_), "=", _) => res += target
        })

        assignments(a)
        addAnnotation2ResultSet(res)
    }

    // flow functions (flow => succ and flowR => pred)
    protected def F(e: AST) = flowR(e)
}
