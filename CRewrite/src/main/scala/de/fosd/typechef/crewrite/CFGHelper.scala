package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.crewrite.asthelper.ASTEnv

trait CFGHelper extends IntraCFG {


    // determine recursively all succs check
    def getAllSucc(i: AST, env: ASTEnv): SuccessorRelationship = {
        var result = List[(AST, NextNodeList)]()
        var workingList = List(i)
        var finishedNodes = List[AST]()

        while (workingList.nonEmpty) {
            val curNode = workingList.head
            workingList = workingList.tail

            if (! finishedNodes.exists(_.eq(curNode))) {
                val successors = succ(curNode, env)
                result = (curNode, successors)  :: result
                workingList = workingList ++ successors.map(x => x.entry)
                finishedNodes = curNode :: finishedNodes
            }
        }
        result
    }

    // determine recursively all pred
    def getAllPred(i: AST, env: ASTEnv): SuccessorRelationship = {
        var result = List[(AST, NextNodeList)]()
        var workingList = List(i)
        var finishedNodes = List[AST]()

        while (workingList.nonEmpty) {
            val curNode = workingList.head
            workingList = workingList.tail

            if (! finishedNodes.exists(_.eq(curNode))) {
                val predecessors = pred(curNode, env)
                result = (curNode, predecessors) :: result
                workingList = workingList ++ predecessors.map(x => x.entry)
                finishedNodes = curNode :: finishedNodes
            }
        }
        result
    }
}
