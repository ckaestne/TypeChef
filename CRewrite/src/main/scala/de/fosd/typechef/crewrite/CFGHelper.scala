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
        var r = List[(AST, NextNodeList)]()
        var s = List(i)
        var d = List[AST]()
        var c: AST = null

        while (!s.isEmpty) {
            c = s.head
            s = s.drop(1)

            if (d.filter(_.eq(c)).isEmpty) {
                r = (c, pred(c, env)) :: r
                s = s ++ r.head._2.map(x => x.entry)
                d = d ++ List(c)
            }
        }
        r
    }
}
