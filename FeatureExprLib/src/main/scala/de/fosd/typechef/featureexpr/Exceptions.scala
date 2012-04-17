package de.fosd.typechef.featureexprUtil

class FeatureException(msg: String) extends RuntimeException(msg)

class FeatureArithmeticException(msg: String) extends FeatureException(msg)
