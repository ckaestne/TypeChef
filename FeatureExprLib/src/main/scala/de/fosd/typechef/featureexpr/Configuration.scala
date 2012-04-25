package de.fosd.typechef.featureexpr

class Configuration(val config: FeatureExpr, fm: FeatureModel = FeatureExprFactory.default.featureModelFactory.empty) {
  val sfm = fm and config
  def valid(fexp: FeatureExpr) = fexp.isSatisfiable(sfm)
}

object EmptyConfiguration extends Configuration(FeatureExprFactory.True, FeatureExprFactory.default.featureModelFactory.empty)