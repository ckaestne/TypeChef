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


}

