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
abstract class AConditional[+T] extends Attributable {
    def flatten[U >: T](f: (FeatureExpr, U, U) => U): U

    //simplify rewrites Choice Types; requires reasoning about variability
    def simplify = _simplify(FeatureExpr.base)
    def simplify(ctx: FeatureExpr) = _simplify(ctx)
    protected[conditional] def _simplify(context: FeatureExpr) = this

    def map[U](f: T => U): AConditional[U]
    def mapf[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => U): AConditional[U]
    def mapfr[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => AConditional[U]): AConditional[U]

    def forall(f: T => Boolean): Boolean
    def exists(f: T => Boolean): Boolean = !this.forall(!f(_))
}

case class AChoice[+T](feature: FeatureExpr, thenBranch: AConditional[T], elseBranch: AConditional[T]) extends AConditional[T] {
    def flatten[U >: T](f: (FeatureExpr, U, U) => U): U = f(feature, thenBranch.flatten(f), elseBranch.flatten(f))
    override def equals(x: Any) = x match {
        case AChoice(f, t, e) => f.equivalentTo(feature) && (thenBranch == t) && (elseBranch == e)
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

    def map[U](f: T => U): AConditional[U] =
        Choice(feature, thenBranch.map(f), elseBranch.map(f))
    def mapf[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => U): AConditional[U] =
        Choice(feature, thenBranch.mapf(inFeature and feature, f), elseBranch.mapf(inFeature and (feature.not), f))
    def mapfr[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => AConditional[U]): AConditional[U] = {
        val newResultA = thenBranch.mapfr(inFeature and feature, f)
        val newResultB = elseBranch.mapfr(inFeature and (feature.not), f)
        if ((newResultA eq thenBranch) && (newResultB eq elseBranch))
            this.asInstanceOf[AConditional[U]]
        else
            Choice(feature, newResultA, newResultB)
    }
    def forall(f: T => Boolean): Boolean = thenBranch.forall(f) && elseBranch.forall(f)
}

case class AOne[+T](value: T) extends AConditional[T] {
    override def toString = value.toString
    def flatten[U >: T](f: (FeatureExpr, U, U) => U): U = value
    def map[U](f: T => U): AConditional[U] = One(f(value))
    def mapf[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => U): AConditional[U] = One(f(inFeature, value))
    def mapfr[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => AConditional[U]): AConditional[U] = f(inFeature, value)
    def forall(f: T => Boolean): Boolean = f(value)
}

object AConditional {
    //collapse double conditionals Cond[Cond[T]] to Cond[T]
    def combine[T](r: AConditional[AConditional[T]]): AConditional[T] = r match {
        case AOne(t) => t
        case AChoice(e, a, b) => Choice(e, combine(a), combine(b))
    }
    //flatten optlists of conditionals into optlists without conditionals
    def flatten[T](optList: List[Opt[AConditional[T]]]): List[Opt[T]] = {
        var result: List[Opt[T]] = List()
        for (e <- optList.reverse) {
            e.entry match {
                case AChoice(f, a, b) =>
                    result = flatten(List(Opt(e.feature and f, a))) ++ flatten(List(Opt(e.feature and (f.not), b))) ++ result;
                case AOne(a) =>
                    result = Opt(e.feature, a) :: result;
            }
        }
        result
    }
    def toOptList[T](c: AConditional[T]): List[Opt[T]] = flatten(List(Opt(FeatureExpr.base, c)))

}