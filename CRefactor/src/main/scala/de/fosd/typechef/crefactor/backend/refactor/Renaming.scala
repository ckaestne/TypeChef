package de.fosd.typechef.crefactor.backend.refactor

import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.parser.c.{AST, Id}
import java.util


/**
 * Implements the process for renaming a function or an id.
 *
 * @author Andreas Janker
 */
object Renaming {

  // TODO Implement

  def renamingIsPossible(ast: AST, defUSE: util.IdentityHashMap[Id, List[Id]], newId: String, oldId: Id): Boolean = {
    // check name first
    if (!Helper.isValidName(newId)) {
      return false
    }
    false
  }

  def renameId(ast: AST, defUSE: util.IdentityHashMap[Id, List[Id]], newId: String, oldId: Id): AST = {
    // check first if id is declaration
    var key = oldId
    var result = ast
    if (!defUSE.containsKey(key)) {
      key = findDecl(defUSE, oldId)
    }
    // replace declaration first
    result = replaceIDinAST(result, key, key.copy(name = newId))

    // replace uses
    defUSE.get(key).foreach(use => {
      result = replaceIDinAST(result, use, use.copy(name = newId))
    })
    result
  }

  private def findDecl(defUSE: util.IdentityHashMap[Id, List[Id]], id: Id): Id = {
    for (currentKey <- defUSE.keySet().toArray())
      for (key <- defUSE.get(currentKey))
        if (key.eq(id))
          return currentKey.asInstanceOf[Id]
    id
  }

  private def replaceIDinAST[T <: Product](t: T, e: Id, n: Id): T = {
    val r = manybu(rule {
      case i: Id => if (i.eq(e)) n else i
    })
    r(t).get.asInstanceOf[T]
  }
}
