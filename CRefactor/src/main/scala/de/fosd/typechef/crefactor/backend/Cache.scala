package de.fosd.typechef.crefactor.backend

import de.fosd.typechef.Frontend
import de.fosd.typechef.typesystem._
import de.fosd.typechef.parser.c.{AST, TranslationUnit}
import de.fosd.typechef.crewrite.{CASTEnv, ASTEnv}

/**
 * Connector class to interact between typechef and the editorwindow as frontend.
 */
object Cache extends CDeclUse with CTypeEnv with CEnvCache with CTypeCache with CTypeSystem {

  private var astCached: AST = null
  private var astEnvCached: ASTEnv = null

  def parse(args: Array[String]): AST = {
    // init, get ast and typecheck
    Frontend.main(args)
    val ast = Frontend.getAST
    if (ast != null) {
      typecheckTranslationUnit(ast.asInstanceOf[TranslationUnit])
      astCached = ast
      astEnvCached = CASTEnv.createASTEnv(astCached)
      return ast
    }
    astCached = ast
    ast
  }

  def update(ast: AST) = {
    astCached = ast
    astEnvCached = CASTEnv.createASTEnv(astCached)
    typecheckTranslationUnit(ast.asInstanceOf[TranslationUnit])
  }

  def getEnv(ast: AST): Env = lookupEnv(ast)

  def getAST(): AST = astCached

  def getASTEnv(): ASTEnv = astEnvCached

}
