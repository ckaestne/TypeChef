package de.fosd.typechef.crefactor.backend

import de.fosd.typechef.Frontend
import de.fosd.typechef.typesystem._
import de.fosd.typechef.parser.c.{Id, AST, TranslationUnit}
import java.util.IdentityHashMap

/**
 * Connector class to interact between typechef and the editorwindow as frontend.
 */
object Connector extends CDefUse with CTypeEnv with CTypeSystem {

  def parse(args: Array[String]): (AST, IdentityHashMap[Id, List[Id]]) = {
    // init, get ast and typecheck
    Frontend.main(args)
    var ast = Frontend.getAST()
    if (ast != null) {
      typecheckTranslationUnit(ast.asInstanceOf[TranslationUnit])
      return (ast, getDefUseMap)
    }
    (ast, null)
  }

  def doTypeCheck(ast: AST): (AST, IdentityHashMap[Id, List[Id]]) = {
    typecheckTranslationUnit(ast.asInstanceOf[TranslationUnit])
    (ast, getDefUseMap)
  }


}
