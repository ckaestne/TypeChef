package de.fosd.typechef.parser.java15

import de.fosd.typechef.parser.java15.lexer.Token

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.{AbstractToken, ProfilingToken, Position}

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
                    kind: Int) extends AbstractToken with ProfilingToken {

  def getFeature(): FeatureExpr = featureExpr

  def getText(): String = image

  def getKind(): Int = kind

  def getPosition(): Position = position

  override def toString = "\"" + image + "\"" + (if (!getFeature.isTautology()) getFeature else "")

  def isInteger: Boolean = false

  def isIdentifier: Boolean = false

  def isString: Boolean = false

  def isCharacter: Boolean = false
}

class JPosition(file: String, line: Int, col: Int) extends Position {
  def getFile: String = file

  def getLine: Int = line

  def getColumn: Int = col
}