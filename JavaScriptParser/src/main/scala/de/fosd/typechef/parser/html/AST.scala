package de.fosd.typechef.parser.html

import de.fosd.typechef.error.{Position, WithPosition}
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.AbstractToken
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.common.CharacterToken
import de.fosd.typechef.parser.common.JPosition

abstract class AST extends Product with Cloneable with WithPosition

abstract class HElement extends AST

case class HTag(name: DString, closingTag: Boolean, selfClosing: Boolean, attributes: List[Opt[HAttribute]]) extends HElement

case class HAttribute(name: DString, value: Option[String]) extends AST

case class HText(value: List[Opt[CharacterToken]]) extends HElement {
  override def toString = "HText("+ value.map(_.entry.getText).mkString("") +")"
}


case class HElementToken(v: Opt[HElement]) extends AbstractToken {
    def getFeature: FeatureExpr = v.feature
    def getText: String = v.entry.toString
    def getPosition: Position = v.entry.getPositionFrom
    def element = v.entry
}

abstract class DElement extends AST

case class DNode(name: DString, attributes: List[Opt[HAttribute]], children: List[Opt[DElement]], openTag: HTag, closingTag: HTag) extends DElement

case class DText(value: List[Opt[CharacterToken]]) extends DElement

case class DString(name: String) extends AST

