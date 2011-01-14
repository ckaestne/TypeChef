package de.fosd.typechef.parser.java15


import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser._

//Expressions
trait AST

class Choice(feature:FeatureExpr,left:Any,right:Any)
object Choice {
	def join(feature:FeatureExpr,left:Any,right:Any) = new Choice(feature, left, right)
}