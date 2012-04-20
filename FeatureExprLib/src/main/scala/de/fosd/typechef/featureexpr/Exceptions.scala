package de.fosd.typechef.featureexpr

class FeatureException(msg: String) extends RuntimeException(msg)

class FeatureArithmeticException(msg: String) extends FeatureException(msg)
