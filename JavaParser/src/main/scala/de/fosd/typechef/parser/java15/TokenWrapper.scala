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
            new JPosition(file, javaToken.beginLine, javaToken.beginColumn))
    }
}

class TokenWrapper(
    image: String,
    featureExpr: FeatureExpr,
    position: Position) extends AbstractToken {

    def getFeature(): FeatureExpr = featureExpr

    def getText(): String = image

    def getPosition(): Position = { null }

}

class JPosition(file: String, line: Int, col: Int) extends Position {
    def getFile: String = file
    def getLine: Int = line
    def getColumn: Int = col
}