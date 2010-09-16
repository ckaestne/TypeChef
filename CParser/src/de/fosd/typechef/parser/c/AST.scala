package de.fosd.typechef.parser.c

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser._

//Expressions
abstract class AST
case class Alt(feature: FeatureExpr, thenBranch: AST, elseBranch: AST) extends Expr
object Alt {
    def join = (f: FeatureExpr, x: AST, y: AST) => if (x == y) x else Alt(f, x, y)
}


abstract class Expr extends AST
abstract class PrimaryExpr extends Expr
case class Id(name:String) extends PrimaryExpr
case class Constant(value:String) extends PrimaryExpr
case class StringLit(name:String) extends PrimaryExpr

abstract class PostfixSuffix extends AST
case class SimplePostfixSuffix(t:String) extends PostfixSuffix
case class PointerPostfixSuffix(kind:String,id:Id) extends PostfixSuffix
case class FunctionCall(params:ExprList) extends PostfixSuffix
case class ArrayAccess(expr:Expr) extends PostfixSuffix

case class PostfixExpr(p:Expr,s:List[PostfixSuffix]) extends Expr
case class UnaryExpr(kind:String, e:Expr) extends Expr
case class SizeOfExprT(typeName:Id) extends Expr
case class SizeOfExprU(expr:Expr) extends Expr
case class CastExpr(typeName:Id,expr:Expr) extends Expr
case class UCastExpr(kind:String,castExpr:Expr) extends Expr

case class NAryExpr(e:Expr,others:List[(String,Expr)]) extends Expr
case class ConditionalExpr(condition:Expr,thenExpr:Expr,elseExpr:Expr) extends Expr
case class AssignExpr(target:Expr,operation:String,source:Expr) extends Expr
case class ExprList(exprs:List[Expr]) extends Expr

//Statements
abstract class Statement extends AST
case class CompoundStatement(/*decl:X,*/innerStatements:List[Opt[Statement]]) extends Statement
case class EmptyStatement extends Statement
case class ExprStatement(expr:Expr) extends Statement
case class WhileStatement(expr:Expr, s:Statement) extends Statement
case class DoStatement(expr:Expr, s:Statement) extends Statement
case class ForStatement(expr1:Option[Expr],expr2:Option[Expr],expr3:Option[Expr], s:Statement) extends Statement
case class GotoStatement(target:Id) extends Statement
case class ContinueStatement extends Statement
case class BreakStatement extends Statement
case class ReturnStatement(expr:Option[Expr]) extends Statement
case class LabelStatement(id:Id) extends Statement
case class CaseStatement(c:Expr,s:Statement) extends Statement
case class DefaultStatement(s:Statement) extends Statement
case class IfStatement(condition:Expr,thenBranch:Statement,elseBranch:Option[Statement]) extends Statement
case class SwitchStatement(expr:Expr,s:Statement) extends Statement
case class AltStatement(feature: FeatureExpr, thenBranch: Statement, elseBranch: Statement) extends Statement
object AltStatement {
    def join = (f: FeatureExpr, x: Statement, y: Statement) => if (x == y) x else AltStatement(f, x, y)
}

