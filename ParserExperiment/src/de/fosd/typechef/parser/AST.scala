package de.fosd.typechef.parser
import de.fosd.typechef.featureexpr.FeatureExpr

sealed abstract class AST
abstract class Expr extends AST
case class Plus(left: AST, right: AST) extends Expr
case class Minus(left: AST, right: AST) extends Expr
case class Mul(left: AST, right: AST) extends Expr
case class Lit(value: Int) extends Expr
case class Alt(feature: FeatureExpr, thenBranch: AST, elseBranch: AST) extends Expr
case class OptAST(feature: FeatureExpr, optBranch: AST) extends Expr
case class ExprList(list: List[Expr]) extends AST