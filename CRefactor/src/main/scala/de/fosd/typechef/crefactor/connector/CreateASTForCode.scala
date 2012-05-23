package de.fosd.typechef.crefactor.connector

import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.crewrite.PositionMapper
import de.fosd.typechef.lexer.options.LexerOptions
import de.fosd.typechef.{Frontend, lexer, FrontendOptions, FrontendOptionsWithConfigFiles}


/**
 * CreateASTForCode object between the gui and type chef.
 */

object CreateASTForCode {

  private val p = new CParser()
  private var ast = null.asInstanceOf[AST]

  def prettyAnalyse: String = {
    if (ast != null) {
      PrettyPrinter.print(ast)
    } else {
      "AST is invalid!".asInstanceOf[String]
    }
  }

  def analyse: String = {
    if (ast != null) {
      ast.toString.asInstanceOf[String]
    } else {
      "AST is invalid!".asInstanceOf[String]
    }
  }

  def positionAnalyse(column:Int, line:Int): String = {
    if (ast != null) {
    val positions = new PositionMapper()
    positions.getElementForPosition(ast, column, line)
    } else {
      "AST is invalid!".asInstanceOf[String]
    }
  }



   def getAST(args: Array[String]): AST = {

    //var args = new Array[String](3)
    //args.update(0, "/Users/andi/Desktop/HelloWorld.c")
    //args.update(1, "-xCONFIG_")
    //args.update(2, "-i/Users/andi/Dropbox/HiWi/busybox-1.19.4/Config.h")
    //args.update(3, "-I/Users/andi/Dropbox/HiWi/busybox-1.19.4/include")
    //args.update(2, "-c/Users/andi/Dropbox/HiWi/Andi/andi.properties")

    var frontend = Frontend.main(args)
    ast = Frontend.getAST()
    println(ast)
    //ast = new ParserMain(new CParser).parserMain(
    //  () => CLexer.lex(code, null), new CTypeContext, Frontend.getOptions())
    return ast
  }




}
