package de.fosd.typechef.featureexpr

trait Solver {
  def isSatisfiable(exprCNF: NF): Boolean
}
