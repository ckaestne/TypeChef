package de.fosd.typechef.parser.c

import java.io.InputStream

import de.fosd.typechef.parser._
import de.fosd.typechef.lexer._
import scala.collection.mutable.ListBuffer
import de.fosd.typechef.featureexpr.FeatureModel
import de.fosd.typechef.LexerToken

/**
 * wrapper for the partial preprocessor, which does most of the lexing for us
 * @author kaestner
 *
 */
object CLexer {
  type TokenWrapper = CToken

  def lexFile(fileName: String, systemIncludePath: java.util.List[String], featureModel: FeatureModel): TokenReader[TokenWrapper, CTypeContext] =
    prepareTokens(new PartialPPLexer().parseFile(fileName, systemIncludePath, featureModel))

  def lexStream(stream: InputStream, filePath: String, systemIncludePath: java.util.List[String], featureModel: FeatureModel): TokenReader[TokenWrapper, CTypeContext] =
    prepareTokens(new PartialPPLexer().parseStream(stream, filePath, systemIncludePath, featureModel))

  def lex(text: String, featureModel: FeatureModel): TokenReader[TokenWrapper, CTypeContext] =
    prepareTokens(new PartialPPLexer().parse(text, new java.util.ArrayList[String](), featureModel))

  def prepareTokens(tokenList: java.util.List[LexerToken]): TokenReader[TokenWrapper, CTypeContext] = {
    val tokens = tokenList.iterator
    val result = new ListBuffer[TokenWrapper]
    var tokenNr: Int = 0
    while (tokens.hasNext) {
      val t = tokens.next
      result += CToken(t, tokenNr)
      tokenNr = tokenNr + 1
    }
    new TokenReader(result.toList, 0, new CTypeContext(), CToken.EOF)
  }

  //
  //    /** used to recognize identifiers in the token implementation **/
  //
  //    def isKeyword(s: String): Boolean = keywords contains s
  //    val keywords = Set(
  //        "asm",
  //        "auto",
  //        "break",
  //        "case",
  //        "char",
  //        "const",
  //        "continue",
  //        "default",
  //        "do",
  //        "double",
  //        "else",
  //        "enum",
  //        "extern",
  //        "float",
  //        "for",
  //        "goto",
  //        "if",
  //        "int",
  //        "long",
  //        "register",
  //        "return",
  //        "short",
  //        "signed",
  //        "sizeof",
  //        "static",
  //        "struct",
  //        "switch",
  //        "typedef",
  //        "union",
  //        "unsigned",
  //        "void",
  //        "volatile",
  //        "while")

}