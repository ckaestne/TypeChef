package de.fosd.typechef.conditional

import de.fosd.typechef.featureexpr.FeatureExpr


//TConditional is either TChoice or TOne
abstract class TConditional[+T] {
    def flatten[U >: T](f: (FeatureExpr, U, U) => U): U

    //simplify rewrites TChoice Types; requires reasoning about variability
    def simplify = _simplify(FeatureExpr.base)
    def simplify(ctx: FeatureExpr) = _simplify(ctx)
    protected[conditional] def _simplify(context: FeatureExpr) = this

    def map[U](f: T => U): TConditional[U]
    def mapf[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => U): TConditional[U]
    def mapfr[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => TConditional[U]): TConditional[U]

    def forall(f: T => Boolean): Boolean
    def exists(f: T => Boolean): Boolean = !this.forall(!f(_))
}

case class TChoice[+T](feature: FeatureExpr, thenBranch: TConditional[T], elseBranch: TConditional[T]) extends TConditional[T] {
    def flatten[U >: T](f: (FeatureExpr, U, U) => U): U = f(feature, thenBranch.flatten(f), elseBranch.flatten(f))
    override def equals(x: Any) = x match {
        case TChoice(f, t, e) => f.equivalentTo(feature) && (thenBranch == t) && (elseBranch == e)
        case _ => false
    }
    override def hashCode = thenBranch.hashCode + elseBranch.hashCode
    protected[conditional] override def _simplify(context: FeatureExpr) = {
        val aa = thenBranch._simplify(context and feature)
        val bb = elseBranch._simplify(context andNot feature)
        if ((context and feature).isContradiction) bb
        else if ((context andNot feature).isContradiction) aa
        else if (aa == bb) aa
        else TChoice(feature, aa, bb)
    }

    def map[U](f: T => U): TConditional[U] =
        TChoice(feature, thenBranch.map(f), elseBranch.map(f))
    def mapf[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => U): TConditional[U] =
        TChoice(feature, thenBranch.mapf(inFeature and feature, f), elseBranch.mapf(inFeature and (feature.not), f))
    def mapfr[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => TConditional[U]): TConditional[U] = {
        val newResultA = thenBranch.mapfr(inFeature and feature, f)
        val newResultB = elseBranch.mapfr(inFeature and (feature.not), f)
        if ((newResultA eq thenBranch) && (newResultB eq elseBranch))
            this.asInstanceOf[TConditional[U]]
        else
            TChoice(feature, newResultA, newResultB)
    }
    def forall(f: T => Boolean): Boolean = thenBranch.forall(f) && elseBranch.forall(f)
}

case class TOne[+T](value: T) extends TConditional[T] {
    override def toString = value.toString
    def flatten[U >: T](f: (FeatureExpr, U, U) => U): U = value
    def map[U](f: T => U): TConditional[U] = TOne(f(value))
    def mapf[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => U): TConditional[U] = TOne(f(inFeature, value))
    def mapfr[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => TConditional[U]): TConditional[U] = f(inFeature, value)
    def forall(f: T => Boolean): Boolean = f(value)
}

object TConditional {
    //collapse double conditionals Cond[Cond[T]] to Cond[T]
    def combine[T](r: TConditional[TConditional[T]]): TConditional[T] = r match {
        case TOne(t) => t
        case TChoice(e, a, b) => TChoice(e, combine(a), combine(b))
    }
    //flatten optlists of conditionals into optlists without conditionals
    def flatten[T](optList: List[Opt[TConditional[T]]]): List[Opt[T]] = {
        var result: List[Opt[T]] = List()
        for (e <- optList.reverse) {
            e.entry match {
                case TChoice(f, a, b) =>
                    result = flatten(List(Opt(e.feature and f, a))) ++ flatten(List(Opt(e.feature and (f.not), b))) ++ result;
                case TOne(a) =>
                    result = Opt(e.feature, a) :: result;
            }
        }
        result
    }
    def toOptList[T](c: TConditional[T]): List[Opt[T]] = flatten(List(Opt(FeatureExpr.base, c)))

}