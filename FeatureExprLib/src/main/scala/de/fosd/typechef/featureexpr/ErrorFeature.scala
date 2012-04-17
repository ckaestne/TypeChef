package de.fosd.typechef.featureexpr

import bdd.FeatureArithmeticException

/**
 * Created with IntelliJ IDEA.
 * User: kaestner
 * Date: 17.04.12
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */

class ErrorFeature(msg:String, f:FeatureExpr) extends FeatureExpr {
    def isSatisfiable(fm: FeatureModel) = f.isSatisfiable(fm)
    protected def calcSize = 0
    def collectDistinctFeatures = f.collectDistinctFeatures
    def or(that: FeatureExpr) = f.or(that)
    def and(that: FeatureExpr) = f.and(that)
    def not() = f.not

    private def error: Nothing = throw new FeatureArithmeticException(msg)
    override def toTextExpr = error
    //    override def mapDefinedExpr(f: DefinedExpr => FeatureExpr, cache: Map[FeatureExpr, FeatureExpr]) = error
    override def debug_print(x: Int) = error

}
