package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c.AST
import java.util.IdentityHashMap

trait CDefUse {
  protected val defuse: IdentityHashMap[AST, List[AST]] = new IdentityHashMap()


}
