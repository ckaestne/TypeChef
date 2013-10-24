package de.fosd.typechef.parser.html

import de.fosd.typechef.error.{Position, WithPosition}
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.AbstractToken
import de.fosd.typechef.featureexpr.FeatureExpr

abstract class AST extends Product with Cloneable with WithPosition

abstract class HElement extends AST

case class HTag(name: String, closingTag: Boolean, selfClosing: Boolean, attributes: List[Opt[HAttribute]]) extends HElement

case class HAttribute(name: String, value: Option[String]) extends AST

case class HText(value: List[Opt[CharacterToken]]) extends HElement


case class HElementToken(v: Opt[HElement]) extends AbstractToken {
    def getFeature: FeatureExpr = v.feature
    def getText: String = v.entry.toString
    def getPosition: Position = v.entry.getPositionFrom
    def element = v.entry
}

abstract class DElement extends AST

case class DNode(name: String, attributes: List[Opt[HAttribute]], children: List[Opt[DElement]]) extends DElement

case class DText(value: List[Opt[CharacterToken]]) extends DElement
