package de.fosd.typechef.featureexpr

// this class represents the configuration of a product
// and config holds all defined features
class Configuration(val config: FeatureExpr, fm: FeatureModel = NoFeatureModel) extends SatSolver {
  val sfm = fm and config
  def valid(fexp: FeatureExpr) = isSatisfiable(fexp, sfm)
}

object EmptyConfiguration extends Configuration(FeatureExpr.base, NoFeatureModel)
