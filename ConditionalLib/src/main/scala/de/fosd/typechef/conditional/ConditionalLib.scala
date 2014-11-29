package de.fosd.typechef.conditional

import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import FeatureExprFactory.{True, False}

/**
 * maintains a map
 * a name may be mapped to alternative entries with different feature expressions
 */
object ConditionalLib {
    /**
     * explodes an optlist. use carefully, can be very expensive
     */
    def explodeOptList[T](l: List[Opt[T]]): Conditional[List[T]] =
        conditionalFoldRight(l, One(Nil), (e: T, list: List[T]) => e :: list)


    def conditionalFoldRight[A, B](list: List[Opt[A]], init: Conditional[B], op: (A, B) => B): Conditional[B] =
        conditionalFoldRightR(list, init, (a: A, b: B) => One(op(a, b)))

    /**
     * folds a conditional list. if an entry is optional, the result is split and the entry
     * affects the result only partially
     */
    def conditionalFoldRightR[A, B](list: List[Opt[A]], init: Conditional[B], op: (A, B) => Conditional[B]): Conditional[B] =
        conditionalFoldRightFR(list, init, True, (f, a: A, b: B) => op(a, b))

    def conditionalFoldRightFR[A, B](list: List[Opt[A]], init: Conditional[B], featureExpr: FeatureExpr, op: (FeatureExpr, A, B) => Conditional[B]): Conditional[B] =
        list.foldRight(init)(
            (next: Opt[A], intermediateResults: Conditional[B]) => {
                intermediateResults.vflatMap(featureExpr,
                    (intermediateFeature, intermediateResult) =>
                        if ((intermediateFeature implies next.condition).isTautology) op(intermediateFeature, next.entry, intermediateResult)
                        else if ((intermediateFeature mex next.condition).isTautology) One(intermediateResult)
                        else Choice(next.condition, op(intermediateFeature and next.condition, next.entry, intermediateResult), One(intermediateResult))
                ) simplify (featureExpr)
            }
        )

    def conditionalFoldLeftFR[A, B](list: List[Opt[A]], init: Conditional[B], featureExpr: FeatureExpr, op: (FeatureExpr, B, A) => Conditional[B]): Conditional[B] =
        conditionalFoldRightFR[A, B](list.reverse, init, featureExpr, (o, b, a) => op(o, a, b))

    def equals[T](a: Conditional[T], b: Conditional[T]): Boolean =
        compare(a, b, (x: T, y: T) => x equals y).simplify.forall(a => a)

    def compare[T, R](a: Conditional[T], b: Conditional[T], f: (T, T) => R): Conditional[R] =
        zip(a, b).map(x => f(x._1, x._2))

    def findSubtree[T](context: FeatureExpr, tree: Conditional[T]): Conditional[T] = tree match {
        case o@One(_) => o
        case Choice(feature, a, b) =>
            lazy val aa = findSubtree(context and feature, a)
            lazy val bb = findSubtree(context andNot feature, b)
            if ((context and feature).isContradiction()) bb
            else if ((context andNot feature).isContradiction()) aa
            else Choice(feature, aa, bb)
    }


    /**
     * returns the last element (which may differ in different contexts)
     * or None if the list is empty
     */
    def lastEntry[T](list: List[Opt[T]]): Conditional[Option[T]] =
        conditionalFoldRight(list, One(None), (e: T, result: Option[T]) => if (result.isDefined) result else Some(e)) simplify


    /**
     * combines entries from two conditional values to a conditional pair of values
     *
     * this explodes variability and may repeat values as needed
     */
    def zip[A, B](a: Conditional[A], b: Conditional[B]): Conditional[(A, B)] =
        a.vflatMap(True, (feature, x) => zipSubcondition(feature, x, b))

    private def zipSubcondition[A, B](context: FeatureExpr, entry: A, other: Conditional[B]): Conditional[(A, B)] =
        findSubtree(context, other).map(otherEntry => (entry, otherEntry))


    /**
     * convenience function, zip two conditional values and map the result
     */
    def mapCombination[A, B, C](a: Conditional[A], b: Conditional[B], f: (A, B) => C): Conditional[C] =
        zip(a, b).simplify.map(x => f(x._1, x._2))
    /**
     * convenience function, zip two conditional values and map the result
     */
    def mapCombinationF[A, B, C](a: Conditional[A], b: Conditional[B], featureExpr: FeatureExpr, f: (FeatureExpr, A, B) => C): Conditional[C] =
        zip(a, b).simplify(featureExpr).vmap(featureExpr, (fexpr, x) => f(fexpr, x._1, x._2))


    /**
     * convenience function to add an element (e) with feature expression (f) to an conditional tree (t) with the initial
     * context (ctx)
     */
    def insert[T](t: Conditional[T], ctx: FeatureExpr, f: FeatureExpr, e: T): Conditional[T] = {
        t match {
            case o@One(value) => if ((f.isTautology()) || (ctx equivalentTo f)) One(e)
            else
            if (ctx isTautology()) Choice(f, One(e), o)
            else Choice(ctx, o, One(e))
            case Choice(feature, thenBranch, elseBranch) =>
                if ((ctx and feature) and f isContradiction())
                    Choice(feature, thenBranch, insert(elseBranch, ctx and (feature.not()), f, e))
                else
                    Choice(feature, insert(thenBranch, ctx and feature, f, e), elseBranch)
        }
    }

    // collects all leaves of the conditional tree
    def leaves[T](t: Conditional[T]): List[T] = {
        t match {
            case One(value) => List(value)
            case Choice(_, thenBranch, elseBranch) => leaves(thenBranch) ++ leaves(elseBranch)
        }
    }

    // returns all elements of the tree as a list of tuples containing the collected feature expression and the leaves
    def items[T](t: Conditional[T], ctx: FeatureExpr = FeatureExprFactory.True): List[(FeatureExpr, T)] = {
        t match {
            case One(value) => List((ctx, value))
            case Choice(feature, thenBranch, elseBranch) =>
                items(thenBranch, ctx and feature) ++ items(elseBranch, ctx and (feature.not()))
        }
    }

    def isTrue(t: Conditional[Boolean]): FeatureExpr = t match {
        case One(v) => if (v) True else False
        case Choice(f, a, b) => (f and isTrue(a)) or (f.not and isTrue(b))
    }


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
                    result = flatten(List(Opt(e.condition and f, a))) ++ flatten(List(Opt(e.condition and (f.not), b))) ++ result;
                case One(a) =>
                    result = Opt(e.condition, a) :: result;
            }
        }
        result
    }
    //old, only for compatibility
    def toOptList[T](c: Conditional[T]): List[Opt[T]] = c.toOptList
    def toList[T](c: Conditional[T]): List[(FeatureExpr, T)] = c.toList
}

