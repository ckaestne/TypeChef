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
case class JSSourceElement() extends AST