package de.fosd.typechef.parser.common


import de.fosd.typechef.error.Position
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.{ProfilingToken, AbstractToken}

class CharacterToken(
                        image: Int,
                        featureExpr: FeatureExpr,
                        position: JPosition) extends AbstractToken with ProfilingToken {


    def getFeature(): FeatureExpr = featureExpr

    def getText(): String = "" + image.toChar

    def getKind(): Int = image
    def getKindChar(): Char = image.toChar

    def getPosition(): JPosition = position

    override def toString = getText() + (if (!getFeature.isTautology()) getFeature else "")

    def and(expr: FeatureExpr): CharacterToken = new CharacterToken(image, featureExpr and expr, position)
}