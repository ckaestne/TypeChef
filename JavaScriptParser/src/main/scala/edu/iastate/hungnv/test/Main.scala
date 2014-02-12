package edu.iastate.hungnv.test

import java.io.{ FileReader, FileWriter }
import de.fosd.typechef.parser.html._
import de.fosd.typechef.parser.common._
import de.fosd.typechef.parser.javascript._
import de.fosd.typechef.featureexpr.{ FeatureExprFactory, FeatureExpr }
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.TokenReader
import de.fosd.typechef.parser.common.CharacterLexer
import de.fosd.typechef.error.NoPosition
import java.util

import edu.iastate.hungnv.test.Util._

/**
 * @author HUNG
 */
object Main extends App {

  /*
   * Sets to true to pretty print JS with position information
   */
  val printPositionInfoForJS = false

  /*
   * Sets absolute path of the test subject system
   */
  val projectAbsolutePath =
//    "/Work/To-do/Data/Web Projects/Server Code/addressbookv6.2.12/"
//    "/Work/To-do/Data/Web Projects/Server Code/SchoolMate-1.5.4/"
    "/Work/To-do/Data/Web Projects/Server Code/UPB-2.2.7/"

  /*
   * Sets the SAT solver library
   */
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
   * EVALUATION
   */
  
//  val testFile = "src/main/resources/test.xml"
    
//  val testFile = "src/main/resources/addressbook/data_model.xml"
//  val testFile = "src/main/resources/schoolmate/data_model.xml"
//  val testFile = "src/main/resources/upb/index_php_data_model.xml"
  val testFile = "src/main/resources/upb/admin_forums_php_data_model.xml"
    

  val reader = new FileReader(testFile)
  val writer = new FileWriter(testFile.replace("/resources/", "/resources/output/").replace("data_model.xml", "transformed_html.html"))

  val domResult = HtmlParser.parse(reader)
  
  def printDomResult(f: FeatureExpr, x: DElement) {
    val doc = HtmlPrinter.prettyPrint(x)
    val layout = new java.io.StringWriter()
    HtmlPrinter.layoutW(doc, layout)

    log("HtmlPrinter: (" + f + ")")
    log(layout.toString, true)
    log()

    writer.append(layout.toString())
  }

  for (ele <- domResult) {
    ele match {
      case Opt(f, x) => { printDomResult(f, x) }
    }
  }
  
  writer.close();

//  JumpOpeningClosingTags.run(domResult)
  
//  JumpCSSClasses.run(domResult)

}
