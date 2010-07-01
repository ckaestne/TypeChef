package de.fosd.typechef.featureexpr

trait Solver {
	def isSatisfiable(expr:FeatureExprTree):Boolean
	def isTautology(expr:FeatureExprTree):Boolean = !isSatisfiable(Not(expr))	
	def isContradiction(expr:FeatureExprTree):Boolean = {
	  val v = !isSatisfiable(expr)
//	  if (v) println("FOUND CONTRADICTION: "+expr)
	  v
	}
}
