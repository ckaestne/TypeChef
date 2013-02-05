package de.fosd.typechef.crefactor.backend.refactor

import de.fosd.typechef.parser.c.{TranslationUnit, Id, AST}
import de.fosd.typechef.crewrite.{ConditionalNavigation, ASTNavigation, ASTEnv}
import de.fosd.typechef.conditional.{One, Opt}
import org.kiama.rewriting.Rewriter._
import java.util
import util.Collections
import de.fosd.typechef.typesystem.{CEnvCache, CUnknown}
import scala.NoSuchElementException
import de.fosd.typechef.crefactor.backend.Cache


trait CRefactor extends CEnvCache with ASTNavigation with ConditionalNavigation {

  private val languageKeywords = List(
    "auto",
    "break",
    "case",
    "char",
    "const",
    "continue",
    "default",
    "do",
    "double",
    "else",
    "enum",
    "extern",
    "float",
    "for",
    "goto",
    "if",
    "inline",
    "int",
    "long",
    "register",
    "restrict",
    "return",
    "short",
    "signed",
    "sizeof",
    "static",
    "struct",
    "switch",
    "typedef",
    "union",
    "unsigned",
    "void",
    "volatile",
    "while",
    "_Alignas",
    "_Alignof",
    "_Atomic",
    "_Bool",
    "_Complex",
    "_Generic",
    "_Imaginary",
    "_Noreturn",
    "_Static_assert",
    "_Thread_local"
  )

  def refactorIsPossible(selection: Any, ast: AST, astEnv: ASTEnv, declUse: util.IdentityHashMap[Id, List[Id]], useDecl: util.IdentityHashMap[Id, List[Id]], name: String): Boolean

  def performRefactor(selection: Any, ast: AST, astEnv: ASTEnv, declUse: util.IdentityHashMap[Id, List[Id]], useDecl: util.IdentityHashMap[Id, List[Id]], name: String): AST

  /**
   * Checks if the name of a variable is compatible to the iso c standard. See 6.4.2 of the iso standard
   *
   * @param name name to check
   * @return <code>true</code> if valid, <code>false</code> if not
   */
  def isValidName(name: String): Boolean = {
    if (!name.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
      return false
    }
    if (name.startsWith("__")) {
      // reserved
      return false
    }
    !isReservedLanguageKeyword(name)
  }

  /**
   * Checks if the name is a language keyword.
   *
   * @param name the name to check
   * @return <code>true</code> if language keyword
   */
  private def isReservedLanguageKeyword(name: String): Boolean = {
    languageKeywords.contains(name)
  }

  def isDeclaredVarInScope(ast: TranslationUnit, defUSE: util.IdentityHashMap[Id, List[Id]], newId: String, oldID: Id): Boolean = {
    var env = null.asInstanceOf[Env]
    try {
      env = Cache.getEnv(oldID).asInstanceOf[Env]
      // declared
    } catch {
      case e: NoSuchElementException => env = Cache.getEnv(ast.defs.last.entry).asInstanceOf[Env]
      case _ =>
    }
    env.varEnv(newId) match {
      case One(CUnknown(_)) => return false
      case _ => return true
    }
    false
  }

  def isDeclaredTypeDef(ast: TranslationUnit, defUSE: util.IdentityHashMap[Id, List[Id]], newId: String, oldID: Id): Boolean = {
    var env = null.asInstanceOf[Env]
    try {
      env = Cache.getEnv(oldID).asInstanceOf[Env]
      // declared
    } catch {
      case e: NoSuchElementException => env = Cache.getEnv(ast.defs.last.entry).asInstanceOf[Env]
      case _ => return false
    }
    env.typedefEnv(newId) match {
      case One(CUnknown(_)) => return false
      case _ => return true
    }
    false
  }

  def isDeclaredStructOrUnionDef(ast: TranslationUnit, defUSE: util.IdentityHashMap[Id, List[Id]], newId: String, oldID: Id): Boolean = {
    var env = null.asInstanceOf[Env]
    try {
      env = Cache.getEnv(oldID).asInstanceOf[Env]
      // declared
    } catch {
      case e: NoSuchElementException => env = Cache.getEnv(ast.defs.last.entry).asInstanceOf[Env]
      case _ => return false
    }
    env.structEnv.someDefinition(newId, true) || env.structEnv.someDefinition(newId, false)
  }

  def findAllConnectedIds(lookup: Id, declUse: util.IdentityHashMap[Id, List[Id]], useDecl: util.IdentityHashMap[Id, List[Id]]) = {
    val occurrences = Collections.newSetFromMap[Id](new util.IdentityHashMap())

    // find all uses of an callId
    def addOccurrence(occurrence: Id) {
      if (!occurrences.contains(occurrence)) {
        occurrences.add(occurrence)
        if (!declUse.containsKey(occurrence)) {
          // workaround to avoid null pointer @ wrong forward declarations
          occurrences.clear()
          return
        }
        declUse.get(occurrence).foreach(use => {
          occurrences.add(use)
          if (useDecl.containsKey(use)) {
            useDecl.get(use).foreach(entry => addOccurrence(entry))
          }
        })
      }
    }

    if (useDecl.containsKey(lookup)) {
      // lookup declarations and search for further referenced declarations
      useDecl.get(lookup).foreach(id => addOccurrence(id))
    } else {
      // callId is decl - search for further referenced declarations
      addOccurrence(lookup)
    }

    occurrences.toArray(Array[Id]()).toList
  }

  def insertInAstBefore[T <: Product](t: T, mark: Opt[_], insert: Opt[_]): T = {
    val r = oncetd(rule {
      case l: List[Opt[_]] => l.flatMap(x => if (x.eq(mark)) insert :: x :: Nil else x :: Nil)
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceInAST[T <: Product](t: T, e: Opt[_], n: Opt[_]): T = {
    val r = manybu(rule {
      case l: List[Opt[_]] => l.flatMap(x => if (x.eq(e)) n :: Nil else x :: Nil)
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceInAST[T <: Product](t: T, e: T, n: T): T = {
    val r = manybu(rule {
      case i: T => if (i.eq(e)) n else i
    })
    r(t).get.asInstanceOf[T]
  }

  def removeFromAST[T <: Product](t: T, remove: Opt[_]): T = {
    val r = manybu(rule {
      case l: List[Opt[_]] => l.flatMap(x => if (x.eq(remove)) Nil else x :: Nil)
    })
    r(t).get.asInstanceOf[T]
  }

}
