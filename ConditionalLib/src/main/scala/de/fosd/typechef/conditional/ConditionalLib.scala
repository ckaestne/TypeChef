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
        var result=One(List())
//        for (Opt(feature,entry)<-l.reverseIterator) {
//            if (feature==FeatureExpr.base)
//                result=result.map(entry :: _)
//        }
        result
    }
}

