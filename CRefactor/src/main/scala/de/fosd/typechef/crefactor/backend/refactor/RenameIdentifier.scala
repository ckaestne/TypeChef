package de.fosd.typechef.crefactor.backend.refactor

import de.fosd.typechef.crefactor.backend.ASTSelection
import de.fosd.typechef.parser.c.{Id, AST}
import de.fosd.typechef.crefactor.frontend.util.Selection
import de.fosd.typechef.crefactor.Morpheus
import de.fosd.typechef.crefactor.util.Configuration
import java.io.File

/**
 * Implements the technique of correctly renaming an identifier.
 */
object RenameIdentifier extends ASTSelection with Refactor {

    def getSelectedElements(morpheus: Morpheus, selection: Selection): List[AST] = getAvailableIdentifiers(morpheus, selection)

    def getAvailableIdentifiers(morpheus: Morpheus, selection: Selection): List[Id] =
        filterASTElems[Id](morpheus.getAST).par.filter(x => isInSelectionRange(x, selection)).toList.filter(x => isElementOfFile(x, selection.getFilePath))

    def isAvailable(morpheus: Morpheus, selection: Selection): Boolean = !getAvailableIdentifiers(morpheus, selection).isEmpty

    def rename(id: Id, newName: String, morpheus: Morpheus): AST = {
        assert(isValidName(newName), Configuration.getInstance().getConfig("default.error.invalidName"))
        // TODO Optimize Performance by avoiding traversing the ast for each element
        getAllConnectedIdentifier(id, morpheus.getDeclUseMap, morpheus.getUseDeclMap).foldLeft(morpheus.getAST)((ast, id) => {
            assert(new File(id.getFile.get.replaceFirst("file ", "")).canWrite, "Not writeable: " + id.getPositionFrom.getFile)
            assert(!isShadowed(newName, id, morpheus), Configuration.getInstance().getConfig("refactor.rename.failed.shadowing"))
            replaceInAST(ast, id, id.copy(name = newName))
        })
    }
}
