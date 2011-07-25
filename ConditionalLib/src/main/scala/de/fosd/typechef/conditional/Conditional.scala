package de.fosd.typechef.conditional

import de.fosd.typechef.featureexpr.FeatureExpr
import org.kiama.attribution.Attributable

case class Opt[+T](val feature: FeatureExpr, val entry: T) extends Attributable {
    override def equals(x: Any) = x match {
    //XXX: use feature equality instead of equivalence for performance! this may not always be what is expected.
        case Opt(f, e) => (f == feature) && (entry == e)
        case _ => false
    }
    //helper function
    def and(f: FeatureExpr) = if (f == null) this else new Opt(feature.and(f), entry)
    def andNot(f: FeatureExpr) = if (f == null) this else new Opt(feature.and(f.not), entry)
    override def toString = if (feature == FeatureExpr.base) entry.toString else "Opt(" + feature + "," + entry + ")"
}


//conditional is either Choice or One
abstract class Conditional[+T] extends Attributable {
    def flatten[U >: T](f: (FeatureExpr, U, U) => U): U

    //simplify rewrites Choice Types; requires reasoning about variability
    def simplify = _simplify(FeatureExpr.base)
    def simplify(ctx: FeatureExpr) = _simplify(ctx)
    protected[conditional] def _simplify(context: FeatureExpr) = this
}

case class Choice[+T](feature: FeatureExpr, thenBranch: Conditional[T], elseBranch: Conditional[T]) extends Conditional[T] {
    def flatten[U >: T](f: (FeatureExpr, U, U) => U): U = f(feature, thenBranch.flatten(f), elseBranch.flatten(f))
    override def equals(x: Any) = x match {
        case Choice(f, t, e) => f.equivalentTo(feature) && (thenBranch == t) && (elseBranch == e)
        case _ => false
    }
    override def hashCode = thenBranch.hashCode + elseBranch.hashCode
    protected[conditional] override def _simplify(context: FeatureExpr) = {
        val aa = thenBranch._simplify(context and feature)
        val bb = elseBranch._simplify(context andNot feature)
        if ((context and feature).isContradiction) bb
        else if ((context andNot feature).isContradiction) aa
        else Choice(feature, aa, bb)
    }
}

case class One[+T](value: T) extends Conditional[T] {
    override def toString = value.toString
    def flatten[U >: T](f: (FeatureExpr, U, U) => U): U = value
}

object Conditional {
    //collapse double conditionals Cond[Cond[T]] to Cond[T]
    def combine[T](r: Conditional[Conditional[T]]): Conditional[T] = r match {
        case One(t) => t
        case Choice(e, a, b) => Choice(e, combine(a), combine(b))
    }
    //flatten optlists of conditionals into optlists without conditionals
    def flatten[T](optList: List[Opt[Conditional[T]]]): List[Opt[T]] = {
        var result: List[Opt[T]] = List()
        for (e <- optList.reverse) {
            e.entry match {
                case Choice(f, a, b) =>
                    result = flatten(List(Opt(e.feature and f, a))) ++ flatten(List(Opt(e.feature and (f.not), b))) ++ result;
                case One(a) =>
                    result = Opt(e.feature, a) :: result;
            }
        }
        result
    }
    def toOptList[T](c: Conditional[T]): List[Opt[T]] = flatten(List(Opt(FeatureExpr.base, c)))

}