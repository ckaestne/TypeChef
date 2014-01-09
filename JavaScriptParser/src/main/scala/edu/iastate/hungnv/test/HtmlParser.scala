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
object HtmlParser {
  
	type ElementList = List[Opt[HElement]]
    type DomType = List[Opt[DElement]]
  
	def parse(r: Reader): List[Opt[DElement]] = {
        /*
         * Step 1: SAX tokens
         */
//        val tokenReader = CharacterLexer.lex(r)
        val tokenReader = CharacterLexer.lexForXml(r)
        val tokens = tokenReader.tokens.slice(0, tokenReader.tokens.size)

        log("1. SAX tokens:")
        log(prettyPrintSaxTokens(tokens, 50))
        log()

//        java.lang.System.exit(0)

        /*
         * Step 2: SAX sequence
         */
        val p = new HTMLSAXParser
        var saxResult = p.phrase(p.HtmlSequence)(tokenReader, FeatureExprFactory.True)

        //    log(saxResult.toString)

        def printResult[T](f: FeatureExpr, x: p.MultiParseResult[List[T]]): Unit = x match {
            case p.Success(y, _) => println("succeeded[" + f + "] \n" + y.size) //y.take(4))
            case e@p.Failure(y, r, _) => println("failed[" + f + "] \n" + e + "\n  @" + r.first.getPosition())
            case p.SplittedParseResult(fx, x, y) =>
                printResult(f and fx, x)
                printResult(f andNot fx, y)
        }
        try {
            printResult(FeatureExprFactory.True, saxResult)
        } catch {
            case e: Throwable => log("Error: " + e.getClass().toString()); e.printStackTrace()
        }
        
//        java.lang.System.exit(0)


        // Take the longest SplittedParser Result
        def getLongestSuccessResult(f: FeatureExpr, x: p.MultiParseResult[List[Opt[HElement]]]): Option[p.Success[ElementList]] = x match {
            case e: p.Success[ElementList] => Some(e)
            case p.Failure(y, _, _) => None
            case p.SplittedParseResult(fx, x, y) => {
                var firstSuccessResult = getLongestSuccessResult(f and fx, x)
                var secondSuccessResult = getLongestSuccessResult(f andNot fx, y)
                if (!firstSuccessResult.isDefined) return secondSuccessResult
                if (!secondSuccessResult.isDefined) return firstSuccessResult
                if (firstSuccessResult.get.result.size > secondSuccessResult.get.result.size)
                    firstSuccessResult
                else
                    secondSuccessResult
            }
        }

        var success = getLongestSuccessResult(FeatureExprFactory.True, saxResult)

        //var success = saxResult.asInstanceOf[p.Success[List[de.fosd.typechef.conditional.Opt[de.fosd.typechef.parser.html.HElement]]]]
        var saxSequence = success.get.result

        println("2. SAX sequence:")
        println(prettyPrintSaxSequence(saxSequence, 50))
        println()

//        java.lang.System.exit(0)
        
        
        /*
         * Step 3: DOM tokens
         */
        val eoftoken = new HElementToken(Opt(FeatureExprFactory.True, HText(List())))
        var domTokens : List[HElementToken]=saxSequence.map(t=>new HElementToken(t))
        val tokenStream = new TokenReader[HElementToken, Null](domTokens, 0, null, eoftoken)

        println("3. DOM tokens:")
//        println(prettyPrintDomTokens(domTokens))
        println()

        /*
         * Step 4: DOM tree
         */
        val p2 = new HTMLDomParser
        val dom :p2.MultiParseResult[DomType] = p2.phrase(p2.Document)(tokenStream, FeatureExprFactory.True)

        log("Parse result: ");
        def printResult2[T](f: FeatureExpr, x: p2.MultiParseResult[List[T]]): Unit = x match {
            case p2.Success(y, _) => println("succeeded[" + f + "] \n  " + y.size) //y.take(4))
            case e@p2.NoSuccess(y, r, _) => println("failed[" + f + "] \n  " + y + "\n  @" + r.first.getPosition+": "+r.first)
            case p2.SplittedParseResult(fx, x, y) =>
                printResult2(f and fx, x)
                printResult2(f andNot fx, y)
        }
        printResult2(FeatureExprFactory.True,dom)

        var domResult: DomType =
        dom match {
            case p2.Success(result, next) => {log("Good"); result;}
            case p2.Error(_, next, _) => {log(next.first.getPosition.toString); null}
            case _ => {null}
        }

        if (domResult == null)
        	System.exit(0)

        log(domResult.toString)

//        var s = dom.asInstanceOf[p.Success[DNode]]
//        var rootNode = s.result

        println("4. DOM tree:")
//        println(prettyPrintDNode(rootNode, 0))
//        println()

        
        
        def printDomResult(f: FeatureExpr, x: DElement) {
          var doc = DomPrinter.prettyPrint(x)
          var layout = DomPrinter.layout(doc)
          log("PrettyPrinter: (" + f + ")")
          log(layout, true)
          log()
        }
        
        var ele = null
        for (ele <- domResult) {
          ele match {
            case Opt(f, x) => {printDomResult(f, x)}
          }
        }
        
        domResult
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

    /*
     * SAX sequence
     */
    def prettyPrintSaxSequence(r: List[de.fosd.typechef.conditional.Opt[de.fosd.typechef.parser.html.HElement]], elementsToPrint: Int): String = {
        val out = new StringBuilder

        for (e <- r.takeRight(elementsToPrint)) {
            out ++= Util.standardize(e.toString) + "\n"
        }

        out.toString
    }

    /*
     * DOM tokens
     */
    def prettyPrintDomTokens(l: List[de.fosd.typechef.parser.html.HElementToken]): String = {
        val out = new StringBuilder

        for (e <- l) {
            out ++= Util.standardize(e.toString) + "\n"
        }

        out.toString
    }
    
}