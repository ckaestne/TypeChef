package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c.{TranslationUnit, AST, Id}
import java.util
import util.NoSuchElementException

// store def use chains
// we store Id elements of AST structures that represent a definition (key element of defuse)
// and a use (value element of defuse)
//
// the creation of defuse chains relies on the typesystem and it's data that is stored
// in Env instances; during the traversal of the typesystem visitor Env instances get filled
// with information about names, AST entries and their corresponding types
trait CDefUse2 extends CEnvCache {

  type useMap = util.IdentityHashMap[Id, _]
  type defUseMap = util.IdentityHashMap[Id, useMap]

  private val defUse = new defUseMap()

  def reset() {
    defUse.clear()
  }

  def fillMap(ast: TranslationUnit) {
    val ids = filterASTElemts[Id](ast)
    ids.foreach(id => {
      var env = null.asInstanceOf[Env]
      try {
        env = lookupEnv(id)
        // declared
      } catch {
        case e: NoSuchElementException => env = lookupEnv(ast.defs.last.entry)
        case _ =>
      }
      //println(id.name + " else " + env.varEnv.lookup(id.name))
      //println(id.name + " astORElse " + env.varEnv.getFullOrElse(id.name, null))
      //env.structEnv.
    })
  }

  // method recursively filters all AST elements for a given type T
  // Copy / Pasted from ASTNavigation -> unable to include ASTNavigation because of dependencies
  private def filterASTElemts[T <: AST](a: Any)(implicit m: ClassManifest[T]): List[T] = {
    a match {
      case p: Product if (m.erasure.isInstance(p)) => List(p.asInstanceOf[T])
      case l: List[_] => l.flatMap(filterASTElemts[T])
      case p: Product => p.productIterator.toList.flatMap(filterASTElemts[T])
      case _ => List()
    }
  }


}
