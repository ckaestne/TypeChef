package de.fosd.typechef.parser.javascript

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.{AbstractToken, ProfilingToken}
import de.fosd.typechef.error.Position


class JSToken(
                 image: String,
                 featureExpr: FeatureExpr,
                 position: Position,
                 kind: Int,
                 val afterNewLine: Boolean) extends AbstractToken with ProfilingToken {

    def getFeature(): FeatureExpr = featureExpr

    def getText(): String = image

    def getKind(): Int = kind

    def getPosition(): Position = position

    override def toString = "\"" + image + "\"" + (if (!getFeature.isTautology()) getFeature else "")
}



