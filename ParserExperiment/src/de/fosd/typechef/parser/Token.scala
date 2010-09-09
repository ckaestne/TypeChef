package de.fosd.typechef.parser
import de.fosd.typechef.featureexpr.FeatureExpr

class Token(val text: String, val feature: FeatureExpr) {
    def t() = text
    def f = feature

    override def toString = "\"" + text + "\"" + (if (!f.isBase) f else "")
}
object EofToken extends Token("EOF", FeatureExpr.base)