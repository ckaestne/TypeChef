package de.fosd.typechef.featureexpr

abstract class NFException(msg:String) extends Exception(msg) 
class NoNFException(
    e: FeatureExprTree,
    fullExpr: FeatureExprTree,
    expectCNF: Boolean)
    extends NFException("expression is not in " + (if (expectCNF) "cnf" else "dnf") + " " + e + " (" + fullExpr + ")")
class NoLiteralException(e: FeatureExprTree) extends NFException("expression is not a literal " + e)
