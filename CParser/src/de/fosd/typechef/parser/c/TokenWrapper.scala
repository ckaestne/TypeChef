package de.fosd.typechef.parser.c

import de.fosd.typechef.parser._
import org.anarres.cpp._

/**
 * thin wrapper around jccp tokens to make them accessible to MultiFeatureParser
 * @author kaestner
 *
 */
class TokenWrapper(token:Token) extends AbstractToken  {
	def getFeature = token.getFeature()
	def isInteger = token.getType == Token.INTEGER
	def isIdentifier = token.getType == Token.IDENTIFIER && !CLexer.keywords.contains(token.getText)
	def getText:String = token.getText
	def getType = token.getType
	override def toString = "\"" +  token.getText + "\"" + (if (!getFeature.isBase) getFeature else "")
}