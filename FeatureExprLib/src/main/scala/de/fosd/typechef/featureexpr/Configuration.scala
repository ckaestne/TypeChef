package de.fosd.typechef.featureexpr

// this class represents the configuration of a product
// and config holds all defined features
class Configuration(val config: List[DefinedExternal], fm: FeatureModel = NoFeatureModel) extends SatSolver {
  val sfm = fm.and(config.fold(FeatureExpr.base)(_ and _))
  def valid(fexp: FeatureExpr) = isSatisfiable(fexp, sfm)
}

object EmptyConfiguration extends Configuration(List(), NoFeatureModel)
