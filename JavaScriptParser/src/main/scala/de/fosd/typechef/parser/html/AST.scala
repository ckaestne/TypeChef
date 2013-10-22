package de.fosd.typechef.parser.html

import de.fosd.typechef.error.WithPosition
import de.fosd.typechef.conditional.Opt

abstract class AST extends Product with Cloneable with WithPosition

abstract class HElement extends AST

case class HTag(name: String, closing: Boolean, attributes: List[Opt[HAttribute]]) extends HElement

case class HAttribute(name: String, value: Option[String]) extends AST

case class HText(value: List[Opt[CharacterToken]]) extends HElement