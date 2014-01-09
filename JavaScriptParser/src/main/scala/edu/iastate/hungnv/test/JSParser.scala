package edu.iastate.hungnv.test

import java.io._
import de.fosd.typechef.conditional._
import de.fosd.typechef.error._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import de.fosd.typechef.parser.common._
import de.fosd.typechef.parser.html._
import de.fosd.typechef.parser.javascript._
import edu.iastate.hungnv.test.Util._

/**
 * @author HUNG
 */
object JSParser {
  
	type ElementList = List[Opt[HElement]]
    type DomType = List[Opt[DElement]]
	
	def parse(r: Reader): Conditional[JSProgram] = {
		val tokenReader = CharacterLexer.lex(r)
		parse(tokenReader)
	}
	
	def parse(tokens: List[CharacterToken]): Conditional[JSProgram] = {
		val tokenReader = new TokenReader[CharacterToken, Null](tokens, 0, null, new CharacterToken(-1, FeatureExprFactory.True, new JPosition("", -1, -1)))
		parse(tokenReader)
	}
	
	def parse(tokenReader: TokenReader[CharacterToken, Null]): Conditional[JSProgram] = {
		/*
		 * Step 1: Get tokens
		 */
		val tokens = tokenReader.tokens.slice(0, tokenReader.tokens.size)
        log("1. SAX tokens:")
        log(prettyPrintSaxTokens(tokens, 50))
        log()

//        java.lang.System.exit(0)

        /*
         * Step 2: Parsing result
         */
        val parser = new JSParser()
        val result = parser.phrase(parser.Program)(tokenReader, FeatureExprFactory.True)

        log(result.toString)
        
        def printResult[T](f: FeatureExpr, x: parser.MultiParseResult[Conditional[JSProgram]]): Unit = x match {
            case parser.Success(y, _) => println("succeeded[" + f + "] \n" + y) //y.take(4))
            case e@parser.Failure(y, r, _) => println("failed[" + f + "] \n" + e + "\n  @" + r.first.getPosition())
            case parser.SplittedParseResult(fx, x, y) =>
                printResult(f and fx, x)
                printResult(f andNot fx, y)
        }
        try {
            printResult(FeatureExprFactory.True, result)
        } catch {
            case e: Throwable => log("Error: " + e.getClass().toString()); e.printStackTrace()
        }
        
        var jsResult =
        result match {
            case parser.Success(r, next) => {r;}
            case parser.Error(_, next, _) => {log(next.first.getPosition.toString); null}
            case _ => {null}
        }

        if (jsResult == null)
        	System.exit(0)
        
//        java.lang.System.exit(0)
        
        /*
        val jsTree = result match {
		  case parser.Success(r, rest) => printJS(r, FeatureExprFactory.True, 0)
		}
        log(jsTree)
        
        result match {
		  case parser.Success(r, rest) => "\n" + transformJS(r, FeatureExprFactory.True, 0)
		}
        */

        jsResult
  }
  
	/*
     * Utility methods
     */

    /*
     * SAX tokens
     */
    def prettyPrintSaxTokens(r: List[CharacterToken], tokensToPrint: Int): String = {
        val out = new StringBuilder

        var currFeat: FeatureExpr = FeatureExprFactory.True
        for (tok <- r.takeRight(tokensToPrint)) {
            var newFeat: FeatureExpr = tok.getFeature
            if (newFeat != currFeat) {
                out ++= "\n[PC = " + newFeat.toString + "] "
                currFeat = newFeat
            }
            out ++= Util.standardize(tok.getText);
            out ++= " "
        }

        out.toString
    }

}