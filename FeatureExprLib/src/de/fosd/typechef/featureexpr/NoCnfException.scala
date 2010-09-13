package de.fosd.typechef.featureexpr

class NoCnfException(e:FeatureExprTree) extends Exception("expression is not in cnf "+e) 
