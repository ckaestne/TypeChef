package de.fosd.typechef.parser.c

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.AST

abstract class Expr extends AST
abstract class PrimaryExpr extends AST
case class Id(name:String) extends PrimaryExpr
case class Constant(value:String) extends PrimaryExpr
case class StringLit(name:String) extends PrimaryExpr

abstract class PostfixSuffix extends AST
case class SimplePostfixSuffix(t:String) extends PostfixSuffix
case class PointerPostfixSuffix(kind:String,id:Id) extends PostfixSuffix

case class PostfixExpr(p:PrimaryExpr,s:List[PostfixSuffix]) extends Expr

//case class Plus(left: AST, right: AST) extends Expr
//case class Minus(left: AST, right: AST) extends Expr
//case class Mul(left: AST, right: AST) extends Expr
//case class Lit(value: Int) extends Expr
//case class ExprList(list: List[Expr]) extends AST

