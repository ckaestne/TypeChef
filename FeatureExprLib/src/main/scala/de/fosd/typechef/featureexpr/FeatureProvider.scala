package de.fosd.typechef.featureexpr

import sat.LazyLib.Susp


abstract class FeatureProvider {
    //def isFeatureDefined(feature:String):Boolean
    /**
     * returns true if feature is certainly included (no external variability)
     */
    def isFeatureDead(feature: String): Boolean

    /**
     * returns true if feature is certainly excluded (no external variability)
     */
    def isFeatureBase(feature: String): Boolean

    /**
     * returns the feature expression that is necessary to include this macro (may only reference to external definitons)
     */
    def getMacroCondition(feature: String): FeatureExpr
    def getMacroConditionCNF(feature: String): (String, Susp[FeatureExpr])
}
