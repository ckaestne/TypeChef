package de.fosd.typechef.parser.java15
import scala.util.parsing.combinator.syntactical.StandardTokenParsers

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.java15.lexer._
import de.fosd.typechef.parser.java15.lexer.Java15ParserConstants._
import java.io._
import de.fosd.typechef.parser._
import scala.collection.mutable.ListBuffer

/**
 * builds on top of a standard lexer (generated as part of CIDE
 * from a gcide grammer (internally using JavaCC))
 * 
 * the main extensions are:
 * 	(1) we look at special tokens and recognize Antenna 
 * 		IFDEF commands inside comments 
 * 
 */
object JavaLexer {

    def lexFile(fileName: String): TokenReader[TokenWrapper, Null] =
        prepareTokens(new Java15ParserTokenManager(new OffsetCharStream(new FileReader(fileName))), fileName)

    //
    //    def lexStream(stream: InputStream, filePath: String, directory: String): TokenReader[TokenWrapper, CTypeContext] =
    //        prepareTokens(new PartialPPLexer().parseStream(stream, filePath, directory))
    //
    def lex(text: String): TokenReader[TokenWrapper, Null] =
        prepareTokens(new Java15ParserTokenManager(new OffsetCharStream(new ByteArrayInputStream(text.getBytes))), "text input")

    private class PresenceConditionStack() {
        var stack: List[List[FeatureExpr]] = List()
        //add new layer
        def addIf(condition: FeatureExpr) {
            stack = List(condition) :: stack
        }
        //add innermost layer
        def addElse(condition: FeatureExpr) {
            if (stack.isEmpty)
                throw new PreprocessorException("#el[se|if] before #if")
            if (stack.head.isEmpty) throw new PreprocessorException("#el[se|if] before #if - ")
            stack = (condition :: stack.head) :: stack.tail
        }
        //pop
        def addEndif() {
            if (stack.isEmpty) throw new PreprocessorException("#endif before #if")
            stack = stack.tail
        }

        def getPresenceCondition = //should be cached eventually
            getCondition(stack)
        private def getConditionS(s: List[FeatureExpr]) =
            s.tail.foldRight(s.head)(_.not and _)
        private def getCondition(s: List[List[FeatureExpr]]): FeatureExpr =
            if (s.isEmpty) FeatureExpr.base
            else getConditionS(s.head) and getCondition(s.tail)
        def isEmpty = stack.isEmpty
    }

    def prepareTokens(lexer: Java15ParserTokenManager, fileName: String): TokenReader[TokenWrapper, Null] = {
        var tokenStream: List[TokenWrapper] = List()
        var next = lexer.getNextToken
        //stack of lists of feature expressions (inner lists represent multiple conditions with if-else branches)
        val presenceConditionStack = new PresenceConditionStack

        def processPreprocessor(javaToken: Token) {
            if (javaToken.specialToken != null) {
                processPreprocessor(javaToken.specialToken)

                //search of #ifdef etc in comments
                if (javaToken.specialToken.kind == SINGLE_LINE_COMMENT)
                    processComment(javaToken.specialToken.image.trim.substring(2))
            }
        }
        def processComment(comment: String) {
            if (comment.trim.startsWith("#")) {
                //update presence conditions for ifdef etc.
                val tokens = PreprocessorParser.lex(comment)
                if (PreprocessorParser.pifdef(tokens).successful)
                    presenceConditionStack.addIf(PreprocessorParser.pifdef(tokens).get)
                else if (PreprocessorParser.pelifdef(tokens).successful)
                    presenceConditionStack.addElse(PreprocessorParser.pelifdef(tokens).get)
                else if (PreprocessorParser.pelse(tokens).successful)
                    presenceConditionStack.addElse(FeatureExpr.base)
                else if (PreprocessorParser.pendif(tokens).successful)
                    presenceConditionStack.addEndif
                else
                    throw new PreprocessorException("Preprocessor directive " + comment + " not understood (possibly not implemented yet)")
            }
        }

        while (next.kind != Java15ParserConstants.EOF) {
            processPreprocessor(next)
            tokenStream = TokenWrapper.create(next, presenceConditionStack.getPresenceCondition, fileName) :: tokenStream
            next = lexer.getNextToken
        }
        processPreprocessor(next)
        if (!presenceConditionStack.isEmpty) throw new PreprocessorException("less #endif than #if")
        new TokenReader(tokenStream.reverse, 0, null, TokenWrapper.create(next, FeatureExpr.base, fileName))
    }

}

object PreprocessorParser extends StandardTokenParsers {
    lexical.delimiters ++= List("#", "(", ")", "&&", "||", "!")
    lexical.reserved ++= List("ifdef", "ifndef", "elifdef", "elifndef", "else", "endif", "if", "elif")
    //ifdef and ifndef
    def pifdef =
        phrase("#" ~> ("ifdef" ~> atomicFeature
            | "ifndef" ~> atomicFeature ^^ { _.not }
            | "if" ~> featureExpr))
    //elifdef and elifndef
    def pelifdef = phrase("#" ~> ("elifdef" ~> atomicFeature
        | "elifndef" ~> atomicFeature ^^ { _.not }
        | "elif" ~> featureExpr))
    def pelse = phrase("#" ~ "else")
    def pendif = phrase("#" ~ "endif")

    def featureExpr: Parser[FeatureExpr] =
        andExpr

    def andExpr = orExpr ~ opt("&&" ~> featureExpr) ^^ {
        case a ~ None => a
        case a ~ Some(b) => a and b
    }
    def orExpr = literal ~ (opt("||" ~> featureExpr)) ^^ {
        case a ~ None => a
        case a ~ Some(b) => a or b
    }
    def literal =
        ("(" ~> featureExpr <~ ")" |
            "!" ~> featureExpr ^^ { _.not }
            | atomicFeature)
    def atomicFeature = ident ^^ { FeatureExpr.createDefinedExternal(_) }

    def lex(s: String) =
        new lexical.Scanner(s)
}

class PreprocessorException(msg: String) extends Exception(msg)