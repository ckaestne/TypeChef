package de.fosd.typechef.featureexpr

trait Solver {
  def isSatisfiable(macroTable: FeatureProvider, exprCNF: NF): Boolean
}
