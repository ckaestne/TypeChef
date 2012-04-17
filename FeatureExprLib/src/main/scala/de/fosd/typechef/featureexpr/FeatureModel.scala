package de.fosd.typechef.featureexpr

trait FeatureModel {
       def and(expr: FeatureExpr): FeatureModel
   }