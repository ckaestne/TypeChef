package de.fosd.typechef.crefactor.backend

import de.fosd.typechef.Frontend
import de.fosd.typechef.typesystem._
import de.fosd.typechef.parser.c.{Id, AST, TranslationUnit}
import java.util.IdentityHashMap

/**
 * Connector class to interact between typechef and the editorwindow as frontend.
 */
object Connector extends CDefUse with CTypeEnv with CEnvCache with CTypeCache with CTypeSystem {

  def parse(args: Array[String]): (AST, IdentityHashMap[Id, List[Id]], Env) = {
    // init, get ast and typecheck
    Frontend.main(args)
    var ast = Frontend.getAST()
    if (ast != null) {
      // var featureModel = Frontend.getFeatureModel()
      // TODO Feature Model
      typecheckTranslationUnit(ast.asInstanceOf[TranslationUnit])
      return (ast, getDefUseMap, lookupEnv(ast.asInstanceOf[TranslationUnit].defs.last.entry))
    }
    (ast, null, null)
  }

  def doTypeCheck(ast: AST): (AST, IdentityHashMap[Id, List[Id]], Env) = {
    typecheckTranslationUnit(ast.asInstanceOf[TranslationUnit])
    // TODO Update env?
    (ast, getDefUseMap, lookupEnv(ast.asInstanceOf[TranslationUnit].defs.last.entry))
  }

  def getEnv(ast: AST): Env = lookupEnv(ast)
}
