package de.fosd.typechef.conditional

import de.fosd.typechef.featureexpr.FeatureExprFactory.{False, True}
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}

/**
 * maintains a map
 * a name may be mapped to alternative entries with different feature expressions
 */
object ConditionalLib {

    /**
     * explodes a list of optional entries to choice of lists.
     *
     * use carefully, can be very expensive and can explode to huge choices
     */
    def explodeOptList[T](l: List[Opt[T]]): Conditional[List[T]] =
        vfoldRightS(l, One(Nil), (e: T, list: List[T]) => e :: list)


    /**
     * conditional implementation of a fold operation on lists with optional entries.
     *
     * on optional elements in the list the computation is split (if necessary) and
     * the result is computed separately under all possible contexts
     *
     * @param list a list of conditional values
     * @param init an initial result value (conditional)
     * @param op a function performing the operation
     * @return the result of the fold
     */
    def vfoldRightS[A, B](list: List[Opt[A]], init: Conditional[B], op: (A, B) => B): Conditional[B] =
        vfoldRightR(list, init, (a: A, b: B) => One(op(a, b)))

    /**
     * conditional implementation of a fold operation on lists with optional entries.
     *
     * on optional elements in the list the computation is split (if necessary) and
     * the result is computed separately under all possible contexts
     *
     * in contrast to foldRight, this version may introduce variation in a single implementation
     *
     * @param list a list of conditional values
     * @param init an initial result value (conditional)
     * @param op a function performing the operation
     * @return the result of the fold
     */
    def vfoldRightR[A, B](list: List[Opt[A]], init: Conditional[B], op: (A, B) => Conditional[B]): Conditional[B] =
        vfoldRight(list, init, True, (f, a: A, b: B) => op(a, b))

    /**
     * conditional implementation of a fold operation on lists with optional entries.
     *
     * on optional elements in the list the computation is split (if necessary) and
     * the result is computed separately under all possible contexts
     *
     * in contrast to foldRight, this version may introduce variation in a single implementation and propagates 
     * a variability context
     *
     * @param list a list of conditional values
     * @param init an initial result value (conditional)
     * @param op a function performing the operation
     * @return the result of the fold
     */
    def vfoldRight[A, B](list: List[Opt[A]], init: Conditional[B], ctx: FeatureExpr, op: (FeatureExpr, A, B) => Conditional[B]): Conditional[B] =
        list.foldRight(init)(
            (next: Opt[A], intermediateResults: Conditional[B]) => {
                intermediateResults.vflatMap(ctx,
                    (intermediateFeature, intermediateResult) =>
                        if ((intermediateFeature implies next.condition).isTautology) op(intermediateFeature, next.entry, intermediateResult)
                        else if ((intermediateFeature mex next.condition).isTautology) One(intermediateResult)
                        else Choice(next.condition, op(intermediateFeature and next.condition, next.entry, intermediateResult), One(intermediateResult))
                ) simplify (ctx)
            }
        )

    /**
     * conditional fold, see vfoldRight
     */
    def vfoldLeft[A, B](list: List[Opt[A]], init: Conditional[B], featureExpr: FeatureExpr, op: (FeatureExpr, B, A) => Conditional[B]): Conditional[B] =
        vfoldRight[A, B](list.reverse, init, featureExpr, (o, b, a) => op(o, a, b))




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
        vfoldRightS(list, One(None), (e: T, result: Option[T]) => if (result.isDefined) result else Some(e)) simplify


    /**
     * combines entries from two conditional values to a conditional pair of values
     *
     * this explodes variability and may repeat values as needed
     */
    def zip[A, B](a: Conditional[A], b: Conditional[B]): Conditional[(A, B)] =
        a.vflatMap(True, (feature, x) => zipSubcondition(feature, x, b))

    private def zipSubcondition[A, B](ctx: FeatureExpr, entry: A, other: Conditional[B]): Conditional[(A, B)] =
        findSubtree(ctx, other).map(otherEntry => (entry, otherEntry))


    /**
     * convenience function to map a function over all possible combinations of
     * two conditional values
     *
     * uses a common flatmap-map combination
     */
    def mapCombination[A, B, C](a: Conditional[A], b: Conditional[B], f: (A, B) => C): Conditional[C] =
        a.flatMap(aa => b.map(bb => f(aa, bb)))

    /**
     * same as mapCombination, but additionally preserves a context during the computation
     */
    def vmapCombination[A, B, C](a: Conditional[A], b: Conditional[B], ctx: FeatureExpr, f: (FeatureExpr, A, B) => C): Conditional[C] =
        a.vflatMap(ctx, (ctx, a) => b.vmap(ctx, (ctx, b)=> f(ctx, a, b)))

    /**
     * same as vmapCombination, but computes only feasible combinations
     */
    def vmapCombinationOp[A, B, C](a: Conditional[A], b: Conditional[B], ctx: FeatureExpr, f: (FeatureExpr, A, B) => C): Conditional[C] =
        zip(a, b).simplify(ctx).vmap(ctx, (fexpr, x) => f(fexpr, x._1, x._2))

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



    @deprecated("renamed to vfoldRightS for consistency", "0.4.0")
    def conditionalFoldRight = vfoldRightS _
    @deprecated("renamed to vfoldRightR for consistency", "0.4.0")
    def conditionalFoldRightR = vfoldRightR _
    @deprecated("renamed to vfoldRight for consistency", "0.4.0")
    def conditionalFoldRightFR = vfoldRight _
    @deprecated("renamed to vfoldRight for consistency", "0.4.0")
    def conditionalFoldLeftFR = vfoldLeft _
    @deprecated("renamed to vmapCombination for consistency", "0.4.0")
    def mapCombinationF = vmapCombinationOp _
}

