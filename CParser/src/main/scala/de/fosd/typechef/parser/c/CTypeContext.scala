package de.fosd.typechef.parser.c

import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureModel, FeatureExpr}
import FeatureExprFactory._

/**
 * case class to have straightforward hashvalue and equals
 *
 * the type context maps identifiers to the conditions under which they are known as types
 */
case class CTypeContext(val types: Map[String, FeatureExpr] = Map()) {
    def addType(newtype: String, condition: FeatureExpr) = new CTypeContext(types + (newtype -> (types.getOrElse(newtype, False) or condition)))

    def knowsType(typename: String, condition: FeatureExpr): Boolean = knowsType(typename, condition, FeatureExprFactory.default.featureModelFactory.empty)

    def knowsType(typename: String, condition: FeatureExpr, fm: FeatureModel): Boolean =
        (types contains typename) && (condition and types.getOrElse(typename, False)).isSatisfiable(fm)

    def join(that: CTypeContext) =
        new CTypeContext(mergeMap(List(this.types, that.types))(_ or _))


    //copied from http://stackoverflow.com/questions/1262741/scala-how-to-merge-a-collection-of-maps/1264772#1264772
    private def mergeMap[A, B](ms: List[Map[A, B]])(f: (B, B) => B): Map[A, B] =
        (Map[A, B]() /: (for (m <- ms; kv <- m) yield kv)) {
            (a, kv) =>
                a + (if (a.contains(kv._1)) kv._1 -> f(a(kv._1), kv._2) else kv)
        }
}