package edu.iastate.hungnv.test

import java.io._
import de.fosd.typechef.conditional._
import de.fosd.typechef.error._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import de.fosd.typechef.parser.common._
import de.fosd.typechef.parser.html._
import edu.iastate.hungnv.parser.css._
import edu.iastate.hungnv.test.Util._

/**
 * @author HUNG
 */
object CSSParser {
  
	type ElementList = List[Opt[HElement]]
    type DomType = List[Opt[DElement]]
	
	def parse(r: Reader): CStyleSheet = {
		val tokenReader = CharacterLexer.lex(r)
		parse(tokenReader)
	}
	
	def parse(tokens: List[CharacterToken]): CStyleSheet = {
		val tokenReader = new TokenReader[CharacterToken, Null](tokens, 0, null, new CharacterToken(-1, FeatureExprFactory.True, new JPosition("", -1, -1)))
		parse(tokenReader)
	}
	
	def parse(tokenReader: TokenReader[CharacterToken, Null]): CStyleSheet = {
		/*
		 * Step 1: Get tokens
		 */
		val tokens = tokenReader.tokens.slice(0, tokenReader.tokens.size)
        log("1. CSS tokens:")
        log(prettyPrintCSSTokens(tokens, 50))
        log()

//        java.lang.System.exit(0)

        /*
         * Step 2: Parsing result
         */
          val parser = new CSSParser()
          val parseResult = parser.phrase(parser.StyleSheet)(tokenReader, FeatureExprFactory.True)

        log("2. CSS parsed result:")
        log(parseResult.toString)
        
        def printResult[T](f: FeatureExpr, x: parser.MultiParseResult[CStyleSheet]): Unit = x match {
            case parser.Success(y, _) => println("succeeded[" + f + "] \n" + y) //y.take(4))
            case e@parser.Failure(y, r, _) => println("failed[" + f + "] \n" + e + "\n  @" + r.first.getPosition())
            case parser.SplittedParseResult(fx, x, y) =>
                printResult(f and fx, x)
                printResult(f andNot fx, y)
        }
        try {
            printResult(FeatureExprFactory.True, parseResult)
        } catch {
            case e: Throwable => log("Error: " + e.getClass().toString()); e.printStackTrace()
        }
        
        
        // Take the longest SplittedParser Result
        def getLongestSuccessResult(f: FeatureExpr, x: parser.MultiParseResult[CStyleSheet]): Option[parser.Success[CStyleSheet]] = x match {
            case e: parser.Success[CStyleSheet] => Some(e)
            case parser.Failure(y, _, _) => None
            case parser.SplittedParseResult(fx, x, y) => {
            	Some(y.asInstanceOf[parser.Success[CStyleSheet]]);
            	
//                var firstSuccessResult = getLongestSuccessResult(f and fx, x)
//                var secondSuccessResult = getLongestSuccessResult(f andNot fx, y)
//                if (!firstSuccessResult.isDefined) return secondSuccessResult
//                if (!secondSuccessResult.isDefined) return firstSuccessResult
//                if (firstSuccessResult.get.result.sourceElements.size > secondSuccessResult.get.result.sourceElements.size)
//                    firstSuccessResult
//                else
//                    secondSuccessResult
            }
        }

        var success = getLongestSuccessResult(FeatureExprFactory.True, parseResult)
        
        var result = success.get.result
        
        if (result == null) {
        	log("parseRresult is null" + parseResult)
        	System.exit(0)
        }
        
        result
  }
  
	/*
     * Utility methods
     */

    /*
     * CSS tokens
     */
    def prettyPrintCSSTokens(r: List[CharacterToken], tokensToPrint: Int): String = {
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