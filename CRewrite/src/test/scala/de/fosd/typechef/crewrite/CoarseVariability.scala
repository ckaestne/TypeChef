/**
 *
 * Deprecated by new framework
 *
 */
//package de.fosd.typechef.crewrite
//
//import de.fosd.typechef.parser.c._
//import de.fosd.typechef.conditional._
//import de.fosd.typechef.featureexpr.FeatureExpr.base
//import de.fosd.typechef.featureexpr.FeatureExpr
//import org.junit.Test
//
///**
// * testing possible strategies
// *
// * strategies to rewrite the parsed variability to use only Opt-nodes at Top-Level and at Statement-Level
// * all other Opt or Choice nodes are removed in the process
// * (the be precise we cannot remove Opt nodes, but we can ensure that their condition is *base*)
// */
//
//abstract class Choice[+T] {
//    def map[B](f: (T) => B): Choice[B]
//    def toList(f: FeatureExpr): List[Opt[T]]
//}
//
//case class CChoice[+T](feat: FeatureExpr, a: Choice[T], b: Choice[T]) extends Choice[T] {
//    def map[B](f: (T) => B): Choice[B] = CChoice(feat, a map f, b map f)
//    def toList(f: FeatureExpr) = a.toList(feat and f) ++ b.toList(feat andNot f)
//}
//
//case class COne[+T](a: T) extends Choice[T] {
//    def map[B](f: (T) => B): Choice[B] = COne(f(a))
//    def toList(f: FeatureExpr) = List(Opt(f, a))
//}
//
//class CoarseVariability {
//
//    //    private val rewriteStrategy=everywherebu(rule {
//
//    //         })
//
//    def rewrite(ast: AST): AST = {
//        //         rewriteStrategy(ast).get.asInstanceOf[AST]
//        ast
//    }
//
//
//    /**returns a choice of nodes, that should not contain inner variability */
//    def explodeVariability(ast: E): Choice[E] = COne(ast)
//    def explodeVariability(ast: C): Choice[C] = {
//        val choiceList: List[Opt[Choice[E]]] = ast.t.map(o => Opt(o.feature, explodeVariability(o.entry)))
//        val flatList: List[Opt[E]] = flatten(choiceList)
//        split(flatList).map(C(_))
//    }
//    //    def explodeVariability(ast:CC):Choice[CC] = {
//    //
//    //    }
//
//    //        case e@CC(t) =>
//
//
//    private def flatten[T](l: List[Opt[Choice[T]]]): List[Opt[T]] = {
//        l.foldLeft(List[Opt[T]]())({
//            case (optList, Opt(f, choice)) => optList ++ choice.toList(f)
//        })
//    }
//
//    private def split[T](l: List[Opt[T]]): Choice[List[Opt[T]]] = {
//        var r: Choice[List[Opt[T]]] = COne(l)
//        var known = Set[FeatureExpr]()
//        for (Opt(f, _) <- l)
//            if (f != base && !(known contains f)) {
//                r = CChoice(f, r, r)
//                known = known + f
//            }
//
//        cleanChoice(base, r)
//    }
//    private def cleanChoice[T](f: FeatureExpr, c: Choice[List[Opt[T]]]): Choice[List[Opt[T]]] = c match {
//        case COne(l) => COne(cleanList(f, l))
//        case CChoice(feat, a, b) =>
//            if ((f and feat) isContradiction) cleanChoice(f, b)
//            else if ((f andNot feat) isContradiction) cleanChoice(f, a)
//            CChoice(feat, cleanChoice(f and feat, a), cleanChoice(f andNot feat, b))
//    }
//    private def cleanList[T](f: FeatureExpr, c: List[Opt[T]]): List[Opt[T]] =
//        c.filter(o => (f implies o.feature).isTautology).map(o => Opt(base, o.entry))
//
//
//    abstract class TAST
//
//    case class E(i: Int) extends TAST
//
//    case class C(t: List[Opt[E]]) extends TAST
//
//    case class CC(t: TAST) extends TAST
//
//
//    //    /*** ensure the result is correct. returns true if at most the desired variability is left */
//    //    def check(ast:Attributable): Boolean = ast match {
//    //        case Opt(_,a:ExternalDef)=> check(a)//do not require feature=base
//    //        case Opt(_,a:Statement)=> check(a)//do not require feature=base
//    //        case Opt(f,a:Attribute) => (f==base) && check(a)
//    //        case c:Choice[_]=>false//no choice nodes allowed
//    //        case o => o.children.forall(check(_))
//    //    }
//
//
//    val fx = FeatureExpr.createDefinedExternal("X")
//    val fy = FeatureExpr.createDefinedExternal("Y")
//    @Test def explode() = {
//        println(explodeVariability(C(List(Opt(fx, E(1)), Opt(fy, E(2)), Opt(fy, E(3)), Opt(base, E(4))))))
//    }
//
//
//}