package de.fosd.typechef.parser.c
import java.io.InputStream

import de.fosd.typechef.parser._
import org.anarres.cpp._
import scala.collection.mutable.ListBuffer

/**
 * wrapper for the partial preprocessor, which does most of the lexing for us
 * @author kaestner
 *
 */
object CLexer {

    def lexFile(fileName: String, directory: String): TokenReader[TokenWrapper, CTypeContext] = {
        val tokens = new PartialPPLexer().parseFile(fileName, directory).iterator
        val result = new ListBuffer[TokenWrapper]
        while (tokens.hasNext){
        	val t=tokens.next
        	if (t.getText!="__extension__")
        		result += new TokenWrapper(t)
        }
        new TokenReader(result.toList, 0, new CTypeContext())
    }
    def lexStream(stream: InputStream, directory: String): TokenReader[TokenWrapper, CTypeContext] = {
        val tokens = new PartialPPLexer().parseStream(stream, directory).iterator
        val result = new ListBuffer[TokenWrapper]
        while (tokens.hasNext){
        	val t=tokens.next
        	if (t.getText!="__extension__")
        		result += new TokenWrapper(t)
        }
        new TokenReader(result.toList, 0, new CTypeContext())
    }
    def lex(text: String): TokenReader[TokenWrapper, CTypeContext] = {
        val tokens = new PartialPPLexer().parse(text, null).iterator
        val result = new ListBuffer[TokenWrapper]
        while (tokens.hasNext){
        	val t=tokens.next
        	if (t.getText!="__extension__")
        		result += new TokenWrapper(t)
        }
        new TokenReader(result.toList, 0, new CTypeContext())
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