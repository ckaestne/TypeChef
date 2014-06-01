package de.fosd.typechef

import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.c.CFGStmt

package object crewrite {
    // result of pred/succ computation
    // classic control flow computation returns List[CFGStmt]
    // the Opt stores the condition under which the CFGStmt element is the predecessor/successor of the input element
    type CFG = List[Opt[CFGStmt]]
}
