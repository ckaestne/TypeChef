package de.fosd.typechef

import featureexpr.FeatureExpr
import java.io.PrintWriter

/**
 * abstract representation of tokens in the lexers
 *
 *
 * lexer tokens are a superset of tokens used later in the parser, they include layout and
 * preprocessor directives. only those tokens with isLanguageToken are processed further by the
 * parser
 *
 * for the C parser, we also distinguish a number of token kinds (relevant for language tokens only)
 */
trait LexerToken {
  // used to determine splitting and joining
  def getFeature: FeatureExpr

  // used by ParserFramework only to produce error messages
  def getText: String

  //position information (line may be changed after lexing to adjust for .pi file line numbers)
  def getLine: Int

  def setLine(line: Int)

  def getColumn: Int

  def getSourceName: String

  /**
   * properties of tokens used by other clients like the C parser
   *
   * (EOF is a languageToken)
   */
  def isLanguageToken: Boolean

  def isEOF: Boolean


  /**
   * is a language identifier (or type in C)
   *
   * essentially only excludes brackets, commas, literals, and such
   */
  def isKeywordOrIdentifier: Boolean

  def isNumberLiteral: Boolean

  def isStringLiteral: Boolean

  def isCharacterLiteral: Boolean

  /**
   * "Lazily print" this token, i.e. print it without constructing a full in-memory representation. This is just a
   * default implementation, override it for tokens with a potentially huge string representation.
   *
   * @param writer The { @link java.io.PrintWriter} to print onto.
   */
  def lazyPrint(writer: PrintWriter) {
    writer.append(getText)
  }
}
