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
abstract class JSSourceElement() extends AST
abstract class JSExpression() extends AST
case class JSStatement() extends JSSourceElement()
case class JSFunctionDeclaration(name:String,param:Any, funBody:JSProgram) extends JSSourceElement()
case class JSFunctionExpression(name:Option[String],param:Any, funBody:Any) extends JSExpression
case class JSBlock(sourceElements: List[Opt[JSStatement]]) extends AST
