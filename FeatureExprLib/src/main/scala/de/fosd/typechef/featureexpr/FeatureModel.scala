package de.fosd.typechef.featureexpr

trait FeatureModel {
    def and(expr: FeatureExpr): FeatureModel
    def assumeTrue(featurename: String): FeatureModel
    def assumeFalse(featurename: String): FeatureModel
}