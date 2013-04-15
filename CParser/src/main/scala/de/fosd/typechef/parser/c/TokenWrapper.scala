package de.fosd.typechef.parser.c

import de.fosd.typechef.parser._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.lexer._
import de.fosd.typechef.LexerToken


/**
 * thin wrapper around jccp tokens to make them accessible to MultiFeatureParser
 * @author kaestner
 *
 */
class CToken(token: LexerToken, number: Int) extends ProfilingToken with AbstractToken {

  def getFeature = token.getFeature

  def isInteger = token.isNumberLiteral

  def isKeywordOrIdentifier = token.isKeywordOrIdentifier

  def getText: String = token.getText

  def isString: Boolean = token.isStringLiteral

  def isCharacter: Boolean = token.isCharacterLiteral

  override def toString = "\"" + token.getText + "\"" + (if (!getFeature.isTautology) getFeature else "")

  private lazy val pos = new TokenPosition(
    if (token.getSourceName == null) null else token.getSourceName,
    token.getLine,
    token.getColumn,
    number
  )

  def getPosition = pos
}

class TokenPosition(file: String, line: Int, column: Int, tokenNr: Int) extends Position {
  def getFile = file

  def getLine = line

  def getColumn = column

  //    override def toString = "token no. " + tokenNr + " (line: " + getLine + ")"
}


object CToken {

  /**
   * Factory method for the creation of TokenWrappers.
   */
  def apply(token: LexerToken, number: Int) = {
    new CToken(token, number)
  }

  val EOF = new CToken(new EOFToken(), -1) {
    override def getFeature = FeatureExprFactory.False
  }
}