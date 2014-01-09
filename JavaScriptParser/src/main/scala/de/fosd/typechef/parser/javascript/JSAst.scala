package de.fosd.typechef.parser.javascript

import de.fosd.typechef.conditional._
import de.fosd.typechef.error.WithPosition

/**
 * an incomplete AST for JavaScript
 */

trait AST extends Product with Cloneable with WithPosition {
    override def clone(): AST.this.type = super.clone().asInstanceOf[AST.this.type]
}

case class JSProgram(sourceElements: List[Opt[JSSourceElement]]) extends AST

abstract class JSSourceElement extends AST

abstract class JSExpression extends AST

abstract class JSStatement extends JSSourceElement

case class JSFunctionDeclaration(name: JSIdentifier, param: Any, funBody: JSProgram) extends JSSourceElement

case class JSFunctionExpression(name: Option[JSIdentifier], param: Any, funBody: Any) extends JSExpression

case class JSBlock(sourceElements: List[Opt[JSStatement]]) extends JSStatement

case class JSAnyStatement() extends JSStatement

case class JSVariableStatement(s: List[Opt[JSVariableDeclaration]]) extends JSStatement

case class JSVariableDeclaration(name: JSIdentifier, init: Option[JSExpression]) extends AST

case class JSEmptyStatement() extends JSStatement

case class JSExprStatement(expr: JSExpression) extends JSStatement

case class JSOtherStatement() extends JSStatement

case class JSExpr() extends JSExpression

case class JSBinaryOp(e1: JSExpression, op: String, e2: JSExpression) extends JSExpression

case class JSAssignment(e1: JSExpression, op: String, e2: JSExpression) extends JSExpression

case class JSThis() extends JSExpression

case class JSId(n: String) extends JSExpression

case class JSLit(n: String) extends JSExpression

case class JSFunctionCall(target: JSExpression, arguments: List[JSExpression]) extends JSExpression

case class JSExprList(exprs: List[JSExpression]) extends JSExpression


case class JSIdentifier(name: String) extends JSExpression

case class JSComment(n: String) extends AST

case class JSIfStatement(e: JSExpression, s1: JSStatement, s2: Option[JSStatement]) extends JSStatement

case class JSForStatement(statement: JSStatement) extends JSStatement

case class JSUnaryExpr(e: JSExpression, op: String) extends JSExpression

case class JSPostfixExpr(e: JSExpression, op: String) extends JSExpression

case class JSFieldAccess(e: JSExpression, field: JSIdentifier) extends JSExpression

case class JSArrayAccess(e: JSExpression, index: JSExpression) extends JSExpression
