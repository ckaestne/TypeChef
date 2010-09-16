package de.fosd.typechef.parser

import de.fosd.typechef.featureexpr.FeatureExpr

abstract class AST
case class Alt(feature: FeatureExpr, thenBranch: AST, elseBranch: AST) extends AST
case class OptAST(feature: FeatureExpr, optBranch: AST) extends AST
object Alt {
    def join = (f: FeatureExpr, x: AST, y: AST) => if (x == y) x else Alt(f, x, y)
}

case class ~[+a, +b](_1: a, _2: b) {
    override def toString = "(" + _1 + "~" + _2 + ")"
}
case class Opt[T](val feature: FeatureExpr, val entry: T)