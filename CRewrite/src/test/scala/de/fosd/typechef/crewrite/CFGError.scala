package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.{ASTEnv, AST}
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.conditional.Opt
import java.io.StringWriter

sealed abstract class CFGError

case class CFGErrorDir(msg: String, s: AST, sfexp: FeatureExpr, t: AST, tfexp: FeatureExpr) extends CFGError {
    override def toString =
        "[" + sfexp + "]" + s.getClass + "(" + s.getPositionFrom + "--" + s.getPositionTo + ")" + // print source
            "--> " +
            "[" + tfexp + "]" + t.getClass + "(" + t.getPositionFrom + "--" + t.getPositionTo + ")" + // print target
            "\n" + msg + "\n\n\n"
}

case class CFGErrorMis(msg: String, s: AST, sfexp: FeatureExpr) extends CFGError {
    override def toString =
        "[" + sfexp + "]" + s.getClass + "(" + s.getPositionFrom + "--" + s.getPositionTo + ")" + "\n" + msg + "\n\n\n"
}

object CFGErrorOutput {
    def printCFGErrors(s: List[(AST, List[Opt[AST]])], p: List[(AST, List[Opt[AST]])], errors: List[CFGError], env: ASTEnv) = {
        val sw = new StringWriter()
        val dot = new DotGraphWithErrors(sw)
        dot.writeHeader("CFGErrorDump")

        if (errors.nonEmpty) {
            val errNodes = errors.flatMap {
                case e: CFGErrorMis => Some(e.s)
                case _ => None
            }
            val errEdges = errors.flatMap {
                case e: CFGErrorDir => Some((e.s, e.t))
                case _ => None
            }

            println("succs: " + dot.writeNodes(s.map(_._1), env, errNodes))
            println("preds: " + dot.writeNodes(p.map(_._1), env, errNodes))
            println("edges: " + dot.writeFlows(s ++ p, errEdges))
            println(errors.fold("")(_.toString + _.toString))
        }

        dot.writeFooter()
        dot.close()
        println("CFGDump: \n\n" + sw.toString)
    }
}