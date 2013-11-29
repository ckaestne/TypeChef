package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.{ASTEnv, AST}

trait CFGHelper extends IntraCFG {

    // determine recursively all succs check
    def getAllSucc(i: AST, env: ASTEnv) = {
        var r = List[(AST, CFG)]()
        var s = List(i)
        var d = List[AST]()
        var c: AST = null

        while (!s.isEmpty) {
            c = s.head
            s = s.drop(1)

            if (d.filter(_.eq(c)).isEmpty) {
                r = (c, succ(c, env)) :: r
                s = s ++ r.head._2.map(x => x.entry)
                d = d ++ List(c)
            }
        }
        r
    }

    // determine recursively all pred
    def getAllPred(i: AST, env: ASTEnv) = {
        var r = List[(AST, CFG)]()
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
