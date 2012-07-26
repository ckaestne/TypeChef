package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.conditional._

/**
 * strategies to rewrite ifdefs to ifs
 */

class IfdefToIf {

    val CONFIGPREFIX = "v_"


//    private val rewriteStrategy = everywherebu(rule {
//        case Opt(f, stmt: Statement) if (!f.isTautology) =>
//            Opt(base, IfStatement(featureToCExpr(f), One(stmt), List(), None))
//        case Choice(f, One(a: Statement), One(b: Statement)) =>
//            One(IfStatement(featureToCExpr(f), One(a), List(), Some(One(b))))
//    })
//
//    def rewrite(ast: AST): AST = {
//        rewriteStrategy(ast).get.asInstanceOf[AST]
//    }

    // this method replaces a given element e within a structure t with the elements
    // n; we only match elements inside lists (case l: List[Opt[T]] => )
    def replace[T <: Product](t: T, e: Opt[_], n: List[Opt[_]]): T = {
      val r = all(rule {
        case l: List[Opt[_]] => l.flatMap({x => if (x.eq(e)) n else x::Nil})
      })

      r(t).get.asInstanceOf[T]
    }

//    def featureToCExpr(feature: FeatureExpr): Expr = feature match {
//        case d: DefinedExternal => Id(CONFIGPREFIX + d.feature)
//        case a: And =>
//            val l = a.clauses.toList
//            var del = List[Opt[NArySubExpr]]()
//            for (e <- l.tail)
//                del = del ++ List(Opt(FeatureExpr.base, NArySubExpr("&&", featureToCExpr(e))))
//            NAryExpr(featureToCExpr(l.head), del)
//        case o: Or =>
//            val l = o.clauses.toList
//            var del = List[Opt[NArySubExpr]]()
//            for (e <- l.tail)
//                del = del ++ List(Opt(FeatureExpr.base, NArySubExpr("||", featureToCExpr(e))))
//            NAryExpr(featureToCExpr(l.head), del)
//        case Not(n) => UnaryOpExpr("!", featureToCExpr(n))
//    }
}