package de.fosd.typechef.parser.java15


import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser._

//Expressions
trait AST 

abstract class Expr extends AST
abstract class PrimaryExpr extends Expr
case class Id(name: String) extends PrimaryExpr
