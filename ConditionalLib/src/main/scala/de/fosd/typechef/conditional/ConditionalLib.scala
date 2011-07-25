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
    def explodeOptList[T](l: List[Opt[T]]): Conditional[List[T]] = {
        var result: Conditional[List[T]] = One(List())
        for (Opt(entryFeature, entry) <- l.reverseIterator) {
            result = result.mapfr(FeatureExpr.base,
                (choiceFeature, x) =>
                    if ((choiceFeature implies entryFeature).isTautology) One(entry :: x)
                    else if ((choiceFeature mex entryFeature).isTautology) One(x)
                    else Choice(entryFeature, One(entry :: x), One(x))
            )
        }
        result
    }
}

