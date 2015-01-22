package de.fosd.typechef.featureexpr


/**
 * Created with IntelliJ IDEA.
 * User: kaestner
 * Date: 17.04.12
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */

class ErrorFeature(msg: String, f: FeatureExpr) extends FeatureExpr {
    def isSatisfiable(fm: FeatureModel) = error
    def getSatisfiableAssignment(featureModel: FeatureModel, interestingFeatures: Set[SingleFeatureExpr],preferDisabledFeatures:Boolean) = error
    protected def calcSize = error
    def collectDistinctFeatures = error
  	def collectDistinctFeatures2 = error
    def collectDistinctFeatureObjects = error
    def or(that: FeatureExpr) = error
    def and(that: FeatureExpr) = error
    def not() = error
    def simplify(that: FeatureExpr) = error
    def unique(x:SingleFeatureExpr) = error
    override def evaluate(selectedFeatures: Set[String]) = false

    private def error: Nothing = throw new FeatureArithmeticException(msg)
    override def toTextExpr = error
    //    override def mapDefinedExpr(f: DefinedExpr => FeatureExpr, cache: Map[FeatureExpr, FeatureExpr]) = error
    override def debug_print(x: Int) = error

    def getConfIfSimpleAndExpr() = error
    def getConfIfSimpleOrExpr() = error
    private def writeReplace(): Object = new FeatureExprSerializationProxy(this.toTextExpr)
}
