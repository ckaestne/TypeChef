package de.fosd.typechef.crefactor.backend.refactor

import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.parser.c.{TranslationUnit, AST}
import java.util
import de.fosd.typechef.typesystem.{CUnknown, CEnvCache}
import de.fosd.typechef.crewrite.{ConditionalNavigation, ASTNavigation}
import de.fosd.typechef.parser.c.Id
import de.fosd.typechef.crefactor.backend.Connector
import util.NoSuchElementException
import de.fosd.typechef.conditional.One


/**
 * Implements the process for renaming a function or an id.
 *
 * @author Andreas Janker
 */
object Renaming extends CEnvCache with ASTNavigation with ConditionalNavigation {

  def renamingIsPossible(ast: AST, defUSE: util.IdentityHashMap[Id, List[Id]], newId: String, oldId: Id): Boolean = {

    /**
     * Checks with the following steps if renaming is possible.
     * 1. Retrieve if name is valid to the c standard
     * 2. Check if variable name is already used.
     * 3. If already used, check for shadowing in file scope, function scope and block scope.
     */

    // check name first
    if (!Helper.isValidName(newId)) {
      return false
    }

    ((!isDeclaredVarInScope(ast.asInstanceOf[TranslationUnit], defUSE, newId, oldId)) &&
      (!isDeclaredTypeDef(ast.asInstanceOf[TranslationUnit], defUSE, newId, oldId)) &&
      (!isDeclaredStructOrUnionDef(ast.asInstanceOf[TranslationUnit], defUSE, newId, oldId)))
  }

  /**
   * Rename an variable according its uses in the ast.
   */
  def renameId(ast: AST, defUSE: util.IdentityHashMap[Id, List[Id]], newId: String, oldId: Id): AST = {

    def rename(ast: AST, decl: Id, decls: util.IdentityHashMap[Id, Boolean] = new util.IdentityHashMap[Id, Boolean]()): AST = {
      // mark declaration as replaced
      decls.put(decl, true)

      // replace declaration first
      var result = replaceIDinAST(ast, decl, decl.copy(name = newId))

      if (defUSE.containsKey(decl)) {
        // replace uses
        defUSE.get(decl).foreach(use => {
          result = replaceIDinAST(result, use, use.copy(name = newId))
          // Look for further occurring declarations
          Helper.findDecls(defUSE, use).foreach(foundDecl => if (!decls.containsKey(foundDecl)) decls.put(foundDecl, false))
        })
      }

      // Recursively replace all further occurred declarations and uses
      decls.keySet().toArray(Array[Id]()).foreach(key => if (!decls.get(key)) {
        result = rename(result, key, decls)
      })
      result
    }

    rename(ast, Helper.findFirstDecl(defUSE, oldId))
  }

  private def replaceIDinAST[T <: Product](t: T, e: Id, n: Id): T = {
    val r = manybu(rule {
      case i: Id => if (i.eq(e)) n else i
    })
    r(t).get.asInstanceOf[T]
  }

  def validName(name: String) = Helper.isValidName(name)

  def isDeclaredVarInScope(ast: TranslationUnit, defUSE: util.IdentityHashMap[Id, List[Id]], newId: String, oldID: Id): Boolean = {
    var env = null.asInstanceOf[Connector.Env]
    try {
      env = Connector.getEnv(oldID)
      // declared
    } catch {
      case e: NoSuchElementException => env = Connector.getEnv(ast.defs.last.entry)
      case _ =>
    }
    env.varEnv(newId) match {
      case One(CUnknown(_)) => return false
      case _ => return true
    }
    false
  }

  def isDeclaredTypeDef(ast: TranslationUnit, defUSE: util.IdentityHashMap[Id, List[Id]], newId: String, oldID: Id): Boolean = {
    var env = null.asInstanceOf[Connector.Env]
    try {
      env = Connector.getEnv(oldID)
      // declared
    } catch {
      case e: NoSuchElementException => env = Connector.getEnv(ast.defs.last.entry)
      case _ => return false
    }
    env.typedefEnv(newId) match {
      case One(CUnknown(_)) => return false
      case _ => return true
    }
    false
  }

  def isDeclaredStructOrUnionDef(ast: TranslationUnit, defUSE: util.IdentityHashMap[Id, List[Id]], newId: String, oldID: Id): Boolean = {
    var env = null.asInstanceOf[Connector.Env]
    try {
      env = Connector.getEnv(oldID)
      // declared
    } catch {
      case e: NoSuchElementException => env = Connector.getEnv(ast.defs.last.entry)
      case _ => return false
    }
    env.structEnv.someDefinition(newId, true) || env.structEnv.someDefinition(newId, false)
  }
}
