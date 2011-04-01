package de.fosd.typechef.parser.java15


import de.fosd.typechef.featureexpr.FeatureExpr

//Expressions
trait AST

class Choice(val feature: FeatureExpr, val left: Any, val right: Any) {
    override def toString() = "Choice(" + feature + "," + left + "," + right + ")"
}

object Choice {
    def join(feature: FeatureExpr, left: Any, right: Any) = new Choice(feature, left, right)

}