package de.fosd.typechef.crefactor.backend.refactor

import de.fosd.typechef.crefactor.backend.Cache
import de.fosd.typechef.crewrite.{ConditionalNavigation, ASTNavigation}
import java.util
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.typesystem.CUnknown
import de.fosd.typechef.parser.c.Id
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.conditional.One
import util.Collections

/**
 * Helper object providing some useful functions for refactorings.
 */
object Helper extends ASTNavigation with ConditionalNavigation {

  val languageKeywords = List(
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
  def isReservedLanguageKeyword(name: String): Boolean = {
    languageKeywords.contains(name)
  }

  def isStructOrUnion(id: Id): Boolean = {
    try {
      val env = Cache.getEnv(id)
      env.structEnv.someDefinition(id.name, true) || env.structEnv.someDefinition(id.name, false)
    } catch {
      case e: Exception => return false
    }
  }

  def isTypedef(id: Id): Boolean = {
    try {
      val env = Cache.getEnv(id)
      env.typedefEnv(id.name) match {
        case One(CUnknown(_)) => return false
        case _ => return true
      }
    } catch {
      case e: Exception => return false
    }
  }

  def findAllConnectedIds(lookup: Id, declUse: util.IdentityHashMap[Id, List[Id]], useDecl: util.IdentityHashMap[Id, List[Id]]) = {
    val occurrences = Collections.newSetFromMap[Id](new util.IdentityHashMap())

    // find all uses of an callId
    def addOccurrence(occurrence: Id) {
      if (!occurrences.contains(occurrence)) {
        occurrences.add(occurrence)
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

  def findDecls(defUSE: util.IdentityHashMap[Id, List[Id]], id: Id): List[Id] = {
    defUSE.keySet().toArray(Array[Id]()).filter(
      entry => ((entry.name.equals(id.name) && (entry.eq(id) || eqContains(defUSE.get(entry), id))))
    ).toList
  }

  def eqContains[T <: AnyRef](seq: Seq[T], toCheck: T): Boolean = {
    seq.foreach(x => if (x.eq(toCheck)) return true)
    false
  }

  def findFirstDecl(defUSE: util.IdentityHashMap[Id, List[Id]], id: Id): Id = {
    if (defUSE.containsKey(Id)) {
      return id
    }
    defUSE.keySet().toArray().foreach(currentKey => {
      defUSE.get(currentKey).foreach(key => if (key.eq(id)) return currentKey.asInstanceOf[Id])
    })
    id
  }

  def insertInAstBeforeTD[T <: Product](t: T, mark: Opt[_], insert: Opt[_]): T = {
    val r = oncetd(rule {
      case l: List[Opt[_]] => l.flatMap(x => if (x.eq(mark)) insert :: x :: Nil else x :: Nil)
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceInAST[T <: Product](t: T, e: Opt[_], n: Opt[_]): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] => l.flatMap(x => if (x.eq(e)) n :: Nil else x :: Nil)
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
