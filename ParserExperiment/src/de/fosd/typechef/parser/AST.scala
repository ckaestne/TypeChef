package de.fosd.typechef.parser
import de.fosd.typechef.featureexpr.FeatureExpr

sealed abstract class AST
case class Plus(left: AST, right: AST) extends AST
case class Minus(left: AST, right: AST) extends AST
case class Mul(left: AST, right: AST) extends AST
case class Lit(value: Int) extends AST
case class Alt(feature: FeatureExpr, thenBranch: AST, elseBranch: AST) extends AST