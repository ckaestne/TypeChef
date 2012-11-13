package de.fosd.typechef.crefactor.backend

import de.fosd.typechef.Frontend
import de.fosd.typechef.typesystem._
import de.fosd.typechef.parser.c.{Id, AST, TranslationUnit}
import java.util.IdentityHashMap
import de.fosd.typechef.crewrite.{CASTEnv, ASTEnv}

/**
 * Connector class to interact between typechef and the editorwindow as frontend.
 */
object Connector extends CDefUse with CTypeEnv with CEnvCache with CTypeCache with CTypeSystem {

  private var astCached: AST = null
  private var astEnvCached: ASTEnv = null

  def parse(args: Array[String]): AST = {
    // init, get ast and typecheck
    Frontend.main(args)
    val ast = Frontend.getAST()
    if (ast != null) {
      // var featureModel = Frontend.getFeatureModel()
      // TODO Feature Model
      typecheckTranslationUnit(ast.asInstanceOf[TranslationUnit])
      astCached = ast
      astEnvCached = CASTEnv.createASTEnv(astCached)
      return ast
    }
    ast
  }

  def update(ast: AST): (AST, IdentityHashMap[Id, List[Id]], Env) = {
    astCached = ast
    astEnvCached = CASTEnv.createASTEnv(astCached)
    typecheckTranslationUnit(ast.asInstanceOf[TranslationUnit])
    // TODO Update env?
    (ast, getDefUseMap, lookupEnv(ast.asInstanceOf[TranslationUnit].defs.last.entry))
  }

  def getEnv(ast: AST): Env = lookupEnv(ast)

  def getAST(): AST = astCached

  def getASTEnv(): ASTEnv = astEnvCached

}
