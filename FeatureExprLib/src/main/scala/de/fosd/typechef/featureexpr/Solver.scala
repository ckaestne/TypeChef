package de.fosd.typechef.featureexpr

trait Solver {
  def isSatisfiable(exprCNF: FeatureExprTree): Boolean
//  def isTautology(expr: FeatureExprTree): Boolean = !isSatisfiable(Not(expr).simplify)
//  def isContradiction(expr: FeatureExprTree): Boolean = {
//    val v = !isSatisfiable(expr)
//    //	  if (v) println("FOUND CONTRADICTION: "+expr)
//    v
//  }
}
