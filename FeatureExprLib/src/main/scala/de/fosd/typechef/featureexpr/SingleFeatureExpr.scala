package de.fosd.typechef.featureexpr

/**
 * Created with IntelliJ IDEA.
 * User: rhein
 * Date: 5/3/12
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */

trait SingleFeatureExpr extends FeatureExpr {
    def feature : String
    override def getConfIfSimpleAndExpr() : Option[(Set[SingleFeatureExpr],Set[SingleFeatureExpr])] = Option(Set(this),Set())
    override def getConfIfSimpleOrExpr() : Option[(Set[SingleFeatureExpr],Set[SingleFeatureExpr])] = Option(Set(this),Set())
}
