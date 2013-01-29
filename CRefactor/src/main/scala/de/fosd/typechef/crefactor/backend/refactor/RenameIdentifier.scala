package de.fosd.typechef.crefactor.backend.refactor

import de.fosd.typechef.crefactor.backend.ASTSelection
import de.fosd.typechef.parser.c.{Id, AST}
import de.fosd.typechef.crewrite.ASTEnv
import de.fosd.typechef.crefactor.frontend.util.Selection
import de.fosd.typechef.crefactor.Morpheus
import de.fosd.typechef.crefactor.util.Configuration

/**
 * Implements the technique of correctly renaming an identifier.
 */
object RenameIdentifier extends ASTSelection with Refactor {

  def getSelectedElements(ast: AST, astEnv: ASTEnv, selection: Selection): List[AST] = getAvailableIdentifiers(ast, astEnv, selection)

  def getAvailableIdentifiers(ast: AST, astEnv: ASTEnv, selection: Selection): List[Id] = {
    filterASTElems[Id](ast).par.filter(x => isInSelectionRange(x, selection)).toList.flatMap(x => {
      if (isElementOfFile(x, selection.getFilePath)) Some(x)
      else None
    })
  }

  def rename(id: Id, newName: String, morpheus: Morpheus): AST = {
    assert(isValidName(newName), Configuration.getInstance().getConfig("default.error.invalidName"))
    findAllConnectedIds(id, morpheus.getDeclUseMap(), morpheus.getUseDeclMap).foldLeft(morpheus.getAST())((ast, id) => {
      assert(!isShadowed(newName, id, morpheus), Configuration.getInstance().getConfig("refactor.rename.failed.shadowing"))
      replaceInAST(ast, id, id.copy(name = newName))
    })
  }
}
