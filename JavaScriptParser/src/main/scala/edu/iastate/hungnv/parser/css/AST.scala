package edu.iastate.hungnv.parser.css

import de.fosd.typechef.error.{Position, WithPosition}
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.AbstractToken
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.common.CharacterToken
import de.fosd.typechef.parser.common.JPosition

/**
 * @author HUNG
 * Modified from TypeChef
 */
abstract class AST extends Product with Cloneable with WithPosition

case class CStyleSheet(ruleSets: List[Opt[CRuleSet]]) extends AST

case class CRuleSet(selectors: List[CSelector], declarations: String) extends AST

abstract class CSelector() extends AST

case class CSimpleSelector(name: DString, nestedSelector: Option[CSelector]) extends CSelector

case class CClassSelector(name: DString) extends CSelector

case class CHashSelector(name: DString) extends CSelector

case class COtherSelector(name: DString) extends CSelector

case class DString(name: String) extends AST

