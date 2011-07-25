package de.fosd.typechef.conditional

import de.fosd.typechef.featureexpr.FeatureExpr

//TODO refactor into conditional library together with Conditional, One, Choice and Opt

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
    //    {
    //        var result: Conditional[List[T]] = One(List())
    //        for (Opt(entryFeature, entry) <- l.reverseIterator) {
    //            result = result.mapfr(FeatureExpr.base,
    //                (choiceFeature, x) =>
    //                    if ((choiceFeature implies entryFeature).isTautology) One(entry :: x)
    //                    else if ((choiceFeature mex entryFeature).isTautology) One(x)
    //                    else Choice(entryFeature, One(entry :: x), One(x))
    //            )
    //        }
    //        result
    //    }

    def conditionalFoldRight[A, B](list: List[Opt[A]], init: Conditional[B], op: (A, B) => B): Conditional[B] =
        conditionalFoldRightR(list, init, (a: A, b: B) => One(op(a, b)))

    /**
     * folds a conditional list. if an entry is optional, the result is split and the entry
     * affects the result only partially
     */
    def conditionalFoldRightR[A, B](list: List[Opt[A]], init: Conditional[B], op: (A, B) => Conditional[B]): Conditional[B] =
        list.foldRight(init)(
            (opt: Opt[A], b: Conditional[B]) =>
                b.mapfr(FeatureExpr.base,
                    (choiceFeature, x) =>
                        if ((choiceFeature implies opt.feature).isTautology) op(opt.entry, x)
                        else if ((choiceFeature mex opt.feature).isTautology) One(x)
                        else Choice(opt.feature, op(opt.entry, x), One(x))

                )

        )


}

