package de.fosd.typechef.parser
import de.fosd.typechef.featureexpr.FeatureExpr

trait AbstractToken {
    def getFeature: FeatureExpr
}