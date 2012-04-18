package de.fosd.typechef.conditional

import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import FeatureExprFactory.base


case class Opt[+T](val feature: FeatureExpr, val entry: T) {
    override def equals(x: Any) = x match {
        //XXX: use feature equality instead of equivalence for performance! this may not always be what is expected.
        case Opt(f, e) => (f == feature) && (entry == e)
        case _ => false
    }
    //helper function
    def and(f: FeatureExpr) = if (f == null) this else new Opt(feature.and(f), entry)
    def andNot(f: FeatureExpr) = if (f == null) this else new Opt(feature.and(f.not), entry)
    // jl: overriding Opt always causes trouble when looking at the output of AST directly should not be here!
    //override def toString = if (feature == FeatureExpr.base) entry.toString else "Opt(" + feature + "," + entry + ")"
}

//Conditional is either Choice or One
abstract class Conditional[+T] extends Product {
    def flatten[U >: T](f: (FeatureExpr, U, U) => U): U

    //simplify rewrites Choice Types; requires reasoning about variability
    def simplify = _simplify(base)
    def simplify(ctx: FeatureExpr) = _simplify(ctx)
    protected[conditional] def _simplify(context: FeatureExpr) = this

    def map[U](f: T => U): Conditional[U] = mapr(x => One(f(x)))
    def mapf[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => U): Conditional[U] = mapfr(inFeature, (c, x) => One(f(c, x)))
    def mapr[U](f: T => Conditional[U]): Conditional[U] = mapfr(base, (c, x) => f(x))
    def mapfr[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => Conditional[U]): Conditional[U]

    def forall(f: T => Boolean): Boolean
    def exists(f: T => Boolean): Boolean = !this.forall(!f(_))
    def toOptList: List[Opt[T]] = Conditional.flatten(List(Opt(base, this)))
    def toList: List[(FeatureExpr, T)] = this.toOptList.map(o => (o.feature, o.entry))
}

case class Choice[+T](feature: FeatureExpr, thenBranch: Conditional[T], elseBranch: Conditional[T]) extends Conditional[T] {
    def flatten[U >: T](f: (FeatureExpr, U, U) => U): U = f(feature, thenBranch.flatten(f), elseBranch.flatten(f))
    override def equals(x: Any) = x match {
        case Choice(f, t, e) => f.equivalentTo(feature) && (thenBranch == t) && (elseBranch == e)
        case _ => false
    }
    override def hashCode = thenBranch.hashCode + elseBranch.hashCode
    protected[conditional] override def _simplify(context: FeatureExpr) = {
        lazy val aa = thenBranch._simplify(context and feature)
        lazy val bb = elseBranch._simplify(context andNot feature)
        if ((context and feature).isContradiction) bb
        else if ((context andNot feature).isContradiction) aa
        else if (aa == bb) aa
        else Choice(feature, aa, bb)
    }

    def mapfr[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => Conditional[U]): Conditional[U] = {
        val newResultA = thenBranch.mapfr(inFeature and feature, f)
        val newResultB = elseBranch.mapfr(inFeature and (feature.not), f)
        Choice(feature, newResultA, newResultB)
    }
    def forall(f: T => Boolean): Boolean = thenBranch.forall(f) && elseBranch.forall(f)
}

case class One[+T](value: T) extends Conditional[T] {
    //override def toString = value.toString
    def flatten[U >: T](f: (FeatureExpr, U, U) => U): U = value
    def mapfr[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => Conditional[U]): Conditional[U] = f(inFeature, value)
    def forall(f: T => Boolean): Boolean = f(value)
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
    //old, only for compatibility
    def toOptList[T](c: Conditional[T]): List[Opt[T]] = c.toOptList
    def toList[T](c: Conditional[T]): List[(FeatureExpr, T)] = c.toList
}