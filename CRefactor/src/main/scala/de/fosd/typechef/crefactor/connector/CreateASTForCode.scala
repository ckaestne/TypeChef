package de.fosd.typechef.crefactor.connector

import de.fosd.typechef.parser.c.PrettyPrinter._
import de.fosd.typechef.parser.c._


/**
 * CreateASTForCode object between the gui and type chef.
 */

class CreateASTForCode (code: String) {

  private val p = new CParser()
  private val ast = getAST(code)

  def prettyAnalyse: String = {
    val pretty = PrettyPrinterDevClone.layout(PrettyPrinterDevClone.print(ast))
    //val pretty = layout(print(ast))
    pretty.asInstanceOf[String]
  }

  def analyse: String = {
    if (ast != null) {
      ast.toString.asInstanceOf[String]
    } else {
      "AST is invalid!".asInstanceOf[String]
    }
  }

  private def getAST(code: String): TranslationUnit = {
    val ast: AST = new ParserMain(new CParser).parserMain(
      () => CLexer.lex(code, null), new CTypeContext, SilentParserOptions)
    ast.asInstanceOf[TranslationUnit]
  }


}
