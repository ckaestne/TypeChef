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
