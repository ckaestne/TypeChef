package de.fosd.typechef.parser.c
import java.io.InputStream

import de.fosd.typechef.parser._
import de.fosd.typechef.lexer._
import scala.collection.mutable.ListBuffer
import de.fosd.typechef.featureexpr.FeatureModel

/**
 * wrapper for the partial preprocessor, which does most of the lexing for us
 * @author kaestner
 *
 */
object CLexer {

    def lexFile(fileName: String, directory: String, featureModel: FeatureModel): TokenReader[TokenWrapper, CTypeContext] =
        prepareTokens(new PartialPPLexer().parseFile(fileName, directory,featureModel))

    def lexStream(stream: InputStream, filePath: String, directory: String, featureModel: FeatureModel): TokenReader[TokenWrapper, CTypeContext] =
        prepareTokens(new PartialPPLexer().parseStream(stream, filePath, directory,featureModel))

    def lex(text: String, featureModel: FeatureModel): TokenReader[TokenWrapper, CTypeContext] =
        prepareTokens(new PartialPPLexer().parse(text, null,featureModel))

    def prepareTokens(tokenList: java.util.List[Token]): TokenReader[TokenWrapper, CTypeContext] = {
        val tokens = tokenList.iterator
        val result = new ListBuffer[TokenWrapper]
        var tokenNr: Int = 0
        while (tokens.hasNext) {
            val t = tokens.next
            result += TokenWrapper(t, tokenNr)
            tokenNr = tokenNr + 1
        }
        new TokenReader(result.toList, 0, new CTypeContext(),TokenWrapper.EOF)
    }

    /** used to recognize identifiers in the token implementation **/
    val keywords = Set(
        "auto",
        "break",
        "case",
        "char",
        "const",
        "continue",
        "default",
        "do",
        "double",
        "else",
        "enum",
        "extern",
        "float",
        "for",
        "goto",
        "if",
        "int",
        "long",
        "register",
        "return",
        "short",
        "signed",
        "sizeof",
        "static",
        "struct",
        "switch",
        "typedef",
        "union",
        "unsigned",
        "void",
        "volatile",
        "while")

}