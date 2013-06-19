package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.UseDeclMap
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureModel}

// implements reaching definitions (rd) dataflow analysis
// see http://en.wikipedia.org/wiki/Reaching_definition
// rd uses labels to distinguish between definitions; since we do not have
// labels we use the hashcodes of cfg statements for that
//
// out(s)  = gen(s) u (in(s) - kill(s))
// in(s)   = for p in pred(s) u out(p)
// gen(s)  = {hashcode(s)}
// kill(s) = defs(defines(s)) - {hashcode(s)} // using trait UsedDefinesDeclaresTrait; basically x = ...
// defs(i) = // all assignments and declarations of a variable i in the program code


// major limitations:
//   - we use intraprocedural control flow (IntraCFG) which
//     is a conservative analysis for program flow
//     so the analysis will likely produce a lot
//     of false positives, because memory can be initialized
//     in a different function
//class ReachingDefintions(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel) extends MonotoneFW[Id](env, udm, fm) with IntraCFG with CFGHelper with ASTNavigation with UsedDefinedDeclaredVariables {
//
//    private val defs = new IdentityHashMapCache[Set[(Int, FeatureExpr)]]()
//
//    def initDefs(f: FunctionDef) {
//        val ss = getAllSucc(f, fm, env).map(_._1)
//
//        for (s <- ss) {
//            // for all uses in a control flow statement add the declarations to defs
//            for (ue <- uses(s)) {
//                for (ud <- udm.get(ue)) {
//                    defs.lookup(ud) match {
//                        case None => defs.update(ud, Set((System.identityHashCode(ud), env.featureExpr(ud))))
//                        case Some(x) => defs.update(ud, x union Set((System.identityHashCode(ud), env.featureExpr(ud))))
//                    }
//                }
//            }
//
//            // for all definitions in a control flow statement add the hashcode of the cfg statement to defs
//            for (de <- defines(s)) {
//                for (dd <- udm.get(de)) {
//                    defs.lookup(dd) match {
//                        case None => defs.update(dd, Set((System.identityHashCode(s), env.featureExpr(s))))
//                        case Some(x) => defs.update(dd, x union Set((System.identityHashCode(s), env.featureExpr(s))))
//                    }
//                }
//            }
//        }
//    }
//
//    def gen(a: AST) = {
//        var
//        val d = defines(a)
//
//        addAnnotation2ResultSet(defines(a))
//    }
//
//    def kill(a: AST) = {
//        addAnnotation2ResultSet(uses(a) diff defines(a))
//    }
//
//    // flow functions (flow => succ and flowR => pred)
//    protected def flow(e: AST) = flowPred(e)
//
//    protected def unionio(e: AST) = outcached(e)
//    protected def genkillio(e: AST) = incached(e)
//
//    override def outcached(a: AST) = {
//        outcache.lookup(a) match {
//            case Some(v) => v
//            case None => {
//                val r = genkill(a)
//                outcache.update(a, r)
//                r
//            }
//        }
//    }
//
//    override def incached(a: AST) = {
//        incache.lookup(a) match {
//            case Some(v) => v
//            case None => {
//                val r = uniononly(a)
//                incache.update(a, r)
//                r
//            }
//        }
//    }
//
//    // we create fresh T elements (here Id) using a counter
//    private var freshTctr = 0
//
//    private def getFreshCtr: Int = {
//        freshTctr = freshTctr + 1
//        freshTctr
//    }
//
//    def t2T(i: Id) = Id(getFreshCtr + "_" + i.name)
//
//    def t2SetT(i: Id) = {
//        var freshidset = Set[Id]()
//
//        if (udm.containsKey(i)) {
//            for (vi <- udm.get(i)) {
//                freshidset = freshidset.+(createFresh(vi))
//            }
//            freshidset
//        } else {
//            Set(addFreshT(i))
//        }
//    }
//}
