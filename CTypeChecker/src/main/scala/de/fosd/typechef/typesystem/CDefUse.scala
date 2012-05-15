package de.fosd.typechef.typesystem

import java.util.IdentityHashMap
import de.fosd.typechef.parser.c.{AtomicNamedDeclarator, Declarator, Id}

trait CDefUse {
  protected val defuse: IdentityHashMap[Id, List[Id]] = new IdentityHashMap()

  def addDeclaratorDefinition(d: Declarator) = {
    d match {
      case AtomicNamedDeclarator(_, i, _) => defuse.put(i, List())
      case x => assert(false, x + " is not supported yet by defuse!")
    }
  }
}
