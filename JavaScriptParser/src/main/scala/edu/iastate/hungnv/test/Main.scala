package edu.iastate.hungnv.test

import java.io.FileReader
import de.fosd.typechef.parser.html._
import de.fosd.typechef.parser.common._
import de.fosd.typechef.parser.javascript._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.TokenReader
import de.fosd.typechef.parser.common.CharacterLexer
import de.fosd.typechef.error.NoPosition
import java.util

/**
 * @author HUNG
 */
object Main extends App {
  
  FeatureExprFactory.setDefault(FeatureExprFactory.bdd);
  
  /*
   * Test lexer1
   */
  //val testFile = "src/main/resources/testlexer1/test-ifelse.html";
  
  /*
   * Test lexer2
   */
  //val testFile = "src/main/resources/testlexer2/test-AttributeName.html"
  //val testFile = "src/main/resources/testlexer2/test-Comment.html"
  //val testFile = "src/main/resources/testlexer2/test-Comment2.html"
  //val testFile = "src/main/resources/testlexer2/test-DocTypeTag.html"
  //val testFile = "src/main/resources/testlexer2/test-Text.html"
    
  /*
   * Test DOM
   */
  //val testFile = "src/main/resources/testdom/test-CSS-Ifdef-BreakInAttrList.html"
  //val testFile = "src/main/resources/testdom/test-CSS-Ifdef-BreakInAttrValue.html"
  //val testFile = "src/main/resources/testdom/test-HTML-Ifdef-BreakInAttrList.html" // Not the best but correct
  //val testFile = "src/main/resources/testdom/test-HTML-Ifdef-BreakInAttrValue.html"
  //val testFile = "src/main/resources/testdom/test-HTML-Ifdef-BreakInContent.html"
  //val testFile = "src/main/resources/testdom/test-JS-Ifdef-BreakInScript.html"
  //val testFile = "src/main/resources/testdom/test-JS-Ifdef-BreakInStrings.html"
  
  /*
   * Test JS
   */
  //val testFile = "src/main/resources/testjs/test-JS-Ifdef-BreakInStrings.html"
    
  /*
   * Test syntax errors
   */
  //val testFile = "src/main/resources/testsyntaxerrors/test-HTML-SyntaxError.html"
    
  /*
   * Pending
   */
  //val testFile = "src/main/resources/test-HTML-SymbolicValue.html"
  //val testFile = "src/main/resources/test-PrettyPrint.html"
  //val testFile = "src/main/resources/test-addressbook.html"
  //val testFile = "src/main/resources/test-addressbook-simplified.html"
  //val testFile = "src/main/resources/test-addressbook-ex1.html"	// Handle ill-formed
  //val testFile = "src/main/resources/test-addressbook-ex2.html"
  //val testFile = "src/main/resources/test-addressbook-ex3.html" // Handle ;
  
  val testFile = "src/main/resources/addressbook/complete.html"
  //val testFile = "src/main/resources/addressbook/Part1.html"
  //val testFile = "src/main/resources/addressbook/Part2.html"
  //val testFile = "src/main/resources/addressbook_old/Part2-1.html" // 8 minutes
  //val testFile = "src/main/resources/addressbook_old/Part2-1-test.html" // 2 minutes
  //val testFile = "src/main/resources/addressbook_old/Part2-1-test2.html" // few seconds
//   val testFile = "src/main/resources/addressbook_old/Part2-2.html" // fragmented parsed results
//  val testFile = "src/main/resources/addressbook_old/Part3.html"
  //val testFile = "src/main/resources/addressbook/Part3-1.html"
  //val testFile = "src/main/resources/addressbook/Part3-1-1.html"
  //val testFile = "src/main/resources/addressbook/Part3-1-2.html"
  //val testFile = "src/main/resources/addressbook/Part4.html"
  
//  val testFile = "src/main/resources/addressbook/css.html"
//  val testFile = "src/main/resources/addressbook/test.html"
  
  val reader = new FileReader(testFile)
  HtmlTransformer.transform(reader)

}
