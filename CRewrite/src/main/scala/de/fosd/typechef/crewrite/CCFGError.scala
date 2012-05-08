package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.featureexpr.FeatureExpr

sealed abstract class CCFGError

case class CCFGErrorDir(msg: String, s: AST, sfexp: FeatureExpr, t: AST, tfexp: FeatureExpr) extends CCFGError {
  override def toString =
    "[" + sfexp + "]" + s.getClass + "(" + s.getPositionFrom + "--" + s.getPositionTo + ")" + // print source
      "--> " +
      "[" + tfexp + "]" + t.getClass + "(" + t.getPositionFrom + "--" + t.getPositionTo + ")" + // print target
      "\n" + msg + "\n\n\n"
}

case class CCFGErrorMis(msg: String, s: AST, sfexp: FeatureExpr) extends CCFGError {
  override def toString =
    "[" + sfexp + "]" + s.getClass + "(" + s.getPositionFrom + "--" + s.getPositionTo + ")" + "\n" + msg + "\n\n\n"
}

object CCFGErrorOutput {
  def printCFGErrors(s: List[(AST, scala.List[AST])], p: List[(AST, scala.List[AST])], errors: List[CCFGError], env: ASTEnv) = {
    if (errors.size > 0) {

      val (nodeErrorsOcc, connectionErrorsOcc) = errors.span({_ match { case _: CCFGErrorMis => true; case _ => false}})
      val nodeErrors = nodeErrorsOcc.map(_.asInstanceOf[CCFGErrorMis].s)
      val connectionErrors = connectionErrorsOcc.map({x => (x.asInstanceOf[CCFGErrorDir].s, x.asInstanceOf[CCFGErrorDir].t)})

      println("succs: " + DotGraph.map2file(s, env, nodeErrors, connectionErrors))
      println("preds: " + DotGraph.map2file(p, env, nodeErrors, connectionErrors))
      println(errors.fold("")(_.toString + _.toString))
    }
  }
}