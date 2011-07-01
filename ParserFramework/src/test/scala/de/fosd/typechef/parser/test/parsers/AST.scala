package de.fosd.typechef.parser.test.parsers

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.Conditional

abstract class AST

abstract class Expr extends AST

case class Plus(left: Conditional[AST], right: Conditional[AST]) extends Expr

case class Minus(left: Conditional[AST], right: Conditional[AST]) extends Expr

case class Mul(left: Conditional[AST], right: Conditional[AST]) extends Expr

case class Lit(value: Int) extends Expr

case class ExprList(list: List[Expr]) extends AST

case class Char(value: String) extends Expr

