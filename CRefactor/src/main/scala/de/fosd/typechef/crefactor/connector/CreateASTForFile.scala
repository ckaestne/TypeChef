package de.fosd.typechef.crefactor.connector

import java.io.File
import de.fosd.typechef.lexer.Main
import de.fosd.typechef.parser.c.{CLexer, CParser, ParserMain, DefaultParserOptions}
import de.fosd.typechef.parser.c.PrettyPrinter._


/**
 * Creates an complete ast for a file.
 */

class CreateASTForFile(file: File) {

  private val tokenStream = new Main().run(Array(file.getAbsolutePath), true, true, null)
  private val in = CLexer.prepareTokens(tokenStream)
  private val parserMain = new ParserMain(new CParser(null, false))
  private val ast = parserMain.parserMain(in, DefaultParserOptions)

  def prettyAnalyse: String = {
    val pretty = layout(print(ast))
    pretty.asInstanceOf[String]
  }

  def analyse: String = {
    if (ast != null) {
      ast.toString.asInstanceOf[String]
    } else {
      "AST is invalid!".asInstanceOf[String]
    }
  }

}
