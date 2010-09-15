package de.fosd.typechef.parser.c

import de.fosd.typechef.parser._
import org.anarres.cpp._
import scala.collection.mutable.ListBuffer

/**
 * wrapper for the partial preprocessor, which does most of the lexing for us
 * @author kaestner
 *
 */
object CLexer {

    def lex(text: String): TokenReader[TokenWrapper] = {
        val tokens = new PartialPPLexer().parse(text, null).iterator
        val result = new ListBuffer[TokenWrapper]
        while (tokens.hasNext)
            result += new TokenWrapper(tokens.next)
        new TokenReader(result.toList, 0)
    }

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
        "while"
        )

}