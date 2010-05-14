package de.fosd.typechef.featureexpr

trait Solver {
	def isSatisfiable(expr:FeatureExpr):Boolean
	def isTautology(expr:FeatureExpr):Boolean = !isSatisfiable(Not(expr))	
	def isContradiction(expr:FeatureExpr):Boolean = {
	  val v = !isSatisfiable(expr)
	  if (v) println("FOUND CONTRADICTION"+expr)
	  v
	}
}
