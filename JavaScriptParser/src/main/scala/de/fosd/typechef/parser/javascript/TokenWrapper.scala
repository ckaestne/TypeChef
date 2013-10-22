package de.fosd.typechef.parser.javascript

import org.mozilla.javascript.Token

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.{AbstractToken, ProfilingToken}
import de.fosd.typechef.error.Position

//object TokenWrapper {
//    def create(rhinoToken: Token, afeatureExpr: FeatureExpr, file: String) = {
//        new TokenWrapper(
//            rhinoToken.,
//            afeatureExpr,
//            new JPosition(file, javaToken.beginLine, javaToken.beginColumn),
//            javaToken.kind)
//    }
//}

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

class JPosition(file: String, line: Int, col: Int) extends Position {
    def getFile: String = file

    def getLine: Int = line

    def getColumn: Int = col
}

