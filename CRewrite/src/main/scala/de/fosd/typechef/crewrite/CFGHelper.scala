package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.{AST}
import de.fosd.typechef.featureexpr.FeatureModel
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.crewrite.asthelper.ASTEnv

trait CFGHelper extends IntraCFG {


    // determine recursively all succs check
    def getAllSucc(i: AST, env: ASTEnv): SuccessorRelationship = {
        var result = List[(AST, NextNodeList)]()
        var workingList = List(i)
        var finishedNodes = List[AST]()

        while (!workingList.isEmpty) {
            val node = workingList.head
            workingList = workingList.tail

            if (!finishedNodes.exists(_.eq(node))) {
                val successors = succ(node, env)
                result = (node, successors)  :: result
                workingList = workingList ++ successors.map(x => x.entry)
                finishedNodes = node :: finishedNodes
            }
        }
        result
    }

    // determine recursively all pred
    def getAllPred(i: AST, env: ASTEnv): SuccessorRelationship = {
        var result = List[(AST, NextNodeList)]()
        var workingList = List(i)
        var finishedNodes = List[AST]()

        while (!workingList.isEmpty) {
            val curNode = workingList.head
            workingList = workingList.tail

            if (finishedNodes.filter(_.eq(curNode)).isEmpty) {
                result = (curNode, pred(curNode, env)) :: result
                workingList = workingList ++ result.head._2.map(x => x.entry)
                finishedNodes = finishedNodes ++ List(c)
            }
        }
        result
    }
}
