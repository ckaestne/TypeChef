package de.fosd.typechef.featureexpr

class NoNFException(e: FeatureExprTree, expectCNF: Boolean) extends Exception("expression is not in " + (if (expectCNF) "cnf" else "dnf") + " " + e)
class NoLiteralException(e: FeatureExprTree) extends Exception("expression is not a literal " + e)
