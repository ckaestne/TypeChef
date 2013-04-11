package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.conditional.Opt
import java.io.{StringWriter, File, FileWriter}

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
  def printCFGErrors(s: List[(AST, List[Opt[AST]])], p: List[(AST, List[Opt[AST]])], errors: List[CFGError], env: ASTEnv) {
    val sw = new StringWriter()
    val dot = new DotGraphWithErrors(sw)
    dot.writeHeader("CFGErrorDump")

    if (errors.size > 0) {
      val nodeErrorsOcc = errors.filter({_ match { case _: CFGErrorMis => true; case _ => false}})
      val connectionErrorsOcc = errors.filter({_ match { case _: CFGErrorDir => true; case _ => false}})
      val nodeErrors = nodeErrorsOcc.map(_.asInstanceOf[CFGErrorMis].s)
      val connectionErrors = connectionErrorsOcc.map({x => (x.asInstanceOf[CFGErrorDir].s, x.asInstanceOf[CFGErrorDir].t)})

      println("succs: " + dot.writeMethodGraphWithErrors(s, env, nodeErrors, connectionErrors))
      println("preds: " + dot.writeMethodGraphWithErrors(p, env, nodeErrors, connectionErrors))
      println(errors.fold("")(_.toString + _.toString))
    }

    dot.writeFooter()
    dot.close()
    println("CFGDump: \n\n" + sw.toString)
  }
}