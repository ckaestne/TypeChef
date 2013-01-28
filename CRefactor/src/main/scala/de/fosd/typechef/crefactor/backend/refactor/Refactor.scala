package de.fosd.typechef.crefactor.backend.refactor

import java.util
import util.Collections
import de.fosd.typechef.typesystem.CEnvCache
import de.fosd.typechef.crefactor.Morpheus
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.parser.c.{AST, TranslationUnit, Id}
import de.fosd.typechef.typesystem.CUnknown
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.conditional.One

trait Refactor extends CEnvCache {

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
  def isReservedLanguageKeyword(name: String) = languageKeywords.contains(name)

  def findAllConnectedIds(lookup: Id, declUse: util.IdentityHashMap[Id, List[Id]], useDecl: util.IdentityHashMap[Id, List[Id]]) = {
    val occurrences = Collections.newSetFromMap[Id](new util.IdentityHashMap())

    // find all uses of an id
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
      // id is decl - search for further referenced declarations
      addOccurrence(lookup)
    }

    occurrences.toArray(Array[Id]()).toList
  }

  def isDeclaredVarInScope(morph: Morpheus, newId: String, oldID: Id, scopeEnd: AST): Boolean = {
    // TODO Optimize!
    lookupEnv(morph, oldID).varEnv(newId) match {
      case One(CUnknown(_)) =>
        lookupEnv(morph, scopeEnd).varEnv(newId) match {
          case One(CUnknown(_)) => false
          case _ => true
        }
      case _ => true
    }
  }

  def isDeclaredTypeDef(morph: Morpheus, newId: String, oldID: Id): Boolean = {
    val env = lookupEnv(morph, oldID)
    env.typedefEnv(newId) match {
      case One(CUnknown(_)) => return false
      case _ => return true
    }
    false
  }

  def isDeclaredStructOrUnionDef(morph: Morpheus, newId: String, oldID: Id): Boolean = {
    val env = lookupEnv(morph, oldID)
    env.structEnv.someDefinition(newId, true) || env.structEnv.someDefinition(newId, false)
  }

  private def lookupEnv(morph: Morpheus, oldID: AST): Env = {
    var env: Env = null
    val ast = morph.getAST().asInstanceOf[TranslationUnit]
    try {
      env = morph.getEnv(oldID).asInstanceOf[Env]
      // declared
    } catch {
      case e: NoSuchElementException => env = morph.getEnv(ast.defs.last.entry).asInstanceOf[Env]
      case _ => null
    }
    env
  }

  def insertInAstBefore[T <: Product](t: T, mark: Opt[_], insert: Opt[_]): T = {
    val r = oncetd(rule {
      case l: List[Opt[_]] => l.flatMap(x => if (x.eq(mark)) insert :: x :: Nil else x :: Nil)
    })
    r(t).get.asInstanceOf[T]
  }

  def insertInAstBefore[T <: Product](t: T, mark: Opt[_], insert: List[Opt[_]]): T = {
    val r = oncetd(rule {
      case l: List[Opt[_]] => l.flatMap(x => if (x.eq(mark)) insert ::: x :: Nil else x :: Nil)
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceInAST[T <: Product](t: T, mark: Opt[_], replace: Opt[_]): T = {
    val r = manybu(rule {
      case l: List[Opt[_]] => l.flatMap(x => if (x.eq(mark)) replace :: Nil else x :: Nil)
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceInASTOnceTD[T <: Product](t: T, mark: Opt[_], replace: Opt[_]): T = {
    val r = oncetd(rule {
      case l: List[Opt[_]] => l.flatMap(x => if (x.eq(mark)) replace :: Nil else x :: Nil)
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
    val r = oncetd(rule {
      case l: List[Opt[_]] => l.flatMap(x => if (x.eq(remove)) Nil else x :: Nil)
    })
    r(t).get.asInstanceOf[T]
  }
}
