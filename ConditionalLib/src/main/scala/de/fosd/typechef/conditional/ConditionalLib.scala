package de.fosd.typechef.conditional

import de.fosd.typechef.featureexpr.FeatureExpr


/**
 * maintains a map
 * a name may be mapped to alternative entries with different feature expressions
 */
object ConditionalLib {
    /**
     * explodes an optlist. use carefully, can be very expensive
     */
    def explodeOptList[T](l: List[Opt[T]]): TConditional[List[T]] =
        conditionalFoldRight(l, TOne(Nil), (e: T, list: List[T]) => e :: list)


    def conditionalFoldRight[A, B](list: List[Opt[A]], init: TConditional[B], op: (A, B) => B): TConditional[B] =
        conditionalFoldRightR(list, init, (a: A, b: B) => TOne(op(a, b)))

    /**
     * folds a conditional list. if an entry is optional, the result is split and the entry
     * affects the result only partially
     */
    def conditionalFoldRightR[A, B](list: List[Opt[A]], init: TConditional[B], op: (A, B) => TConditional[B]): TConditional[B] =
        list.foldRight(init)(
            (opt: Opt[A], b: TConditional[B]) =>
                b.mapfr(FeatureExpr.base,
                    (choiceFeature, x) =>
                        if ((choiceFeature implies opt.feature).isTautology) op(opt.entry, x)
                        else if ((choiceFeature mex opt.feature).isTautology) TOne(x)
                        else TChoice(opt.feature, op(opt.entry, x), TOne(x))

                )

        )

    def equals[T](a: TConditional[T], b: TConditional[T]): Boolean =
        compare(a, b, (x: T, y: T) => x equals y).simplify.forall(a => a)

    def compare[T, R](a: TConditional[T], b: TConditional[T], f: (T, T) => R): TConditional[R] = {
        def compareSubcondition(context: FeatureExpr, entry: T, other: TConditional[T]) =
            findSubtree(context, other).map(otherEntry => f(entry, otherEntry))




        a.mapfr(FeatureExpr.base, (feature, x) => compareSubcondition(feature, x, b))
    }


    def findSubtree[T](context: FeatureExpr, tree: TConditional[T]): TConditional[T] = tree match {
        case o@TOne(_) => o
        case TChoice(feature, a, b) =>
            lazy val aa = findSubtree(context and feature, a)
            lazy val bb = findSubtree(context andNot feature, b)
            if ((context and feature).isContradiction()) bb
            else if ((context andNot feature).isContradiction()) aa
            else TChoice(feature, aa, bb)
    }


    /**
     * returns the last element (which may differ in different contexts)
     * or None if the list is empty
     */
    def lastEntry[T](list: List[Opt[T]]): TConditional[Option[T]] =
        conditionalFoldRight(list, TOne(None), (e: T, result: Option[T]) => if (result.isDefined) result else Some(e)) simplify

}

