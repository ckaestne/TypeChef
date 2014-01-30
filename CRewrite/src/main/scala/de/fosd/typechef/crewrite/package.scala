package de.fosd.typechef

import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.c.{AST, CFGStmt}

package object crewrite {
    // result of pred/succ computation
    // classic control flow computation returns List[CFGStmt]
    // the Opt stores the condition under which the CFGStmt element is the predecessor/successor of the input element
    type NextNodeList = List[Opt[CFGStmt]]

    //list of pairs from ASTNode to a list of conditional successors/predecessors (multiple possible)
    type SuccessorRelationship= List[(AST, NextNodeList)]

}
