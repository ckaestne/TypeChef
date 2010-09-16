package de.fosd.typechef.parser.test
import de.fosd.typechef.parser._
import de.fosd.typechef.featureexpr.FeatureExpr

abstract class Expr extends AST
case class Plus(left: AST, right: AST) extends Expr
case class Minus(left: AST, right: AST) extends Expr
case class Mul(left: AST, right: AST) extends Expr
case class Lit(value: Int) extends Expr
case class ExprList(list: List[Expr]) extends AST

