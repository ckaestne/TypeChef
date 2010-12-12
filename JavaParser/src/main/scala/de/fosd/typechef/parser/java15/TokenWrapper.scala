package de.fosd.typechef.parser.java15
import de.fosd.typechef.parser.Position

import de.fosd.typechef.parser.java15.lexer.Token

import de.fosd.typechef.parser.AbstractToken
import de.fosd.typechef.featureexpr.FeatureExpr

object TokenWrapper {
    def create(javaToken: Token, afeatureExpr: FeatureExpr, file: String) = {
        new TokenWrapper(
            javaToken.image,
            afeatureExpr,
            new JPosition(file, javaToken.beginLine, javaToken.beginColumn),
            javaToken.kind)
    }
}

class TokenWrapper(
    image: String,
    featureExpr: FeatureExpr,
    position: Position,
    kind:Int) extends AbstractToken {

    def getFeature(): FeatureExpr = featureExpr

    def getText(): String = image
    
    def getKind():Int = kind

    def getPosition(): Position = position

    override def toString = "\"" +image + "\"" + (if (!getFeature.isBase()) getFeature else "")
}

class JPosition(file: String, line: Int, col: Int) extends Position {
    def getFile: String = file
    def getLine: Int = line
    def getColumn: Int = col
}