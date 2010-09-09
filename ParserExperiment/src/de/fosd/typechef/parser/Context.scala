package de.fosd.typechef.parser
import de.fosd.typechef.featureexpr._

object Context {
	def base = FeatureExpr.base 
}
//case class Context(val parent: Context, val feature: Int) {
//
//    def split(feature: Int): (Context, Context) =
//        (Context(this, feature), Context(this, -feature))
//
//    def allFeatures: Set[Int] =
//        if (parent == null) Set(feature) else Set(feature) ++ parent.allFeatures
//        
//    def subsetOf(that:Context) =
//    	this.allFeatures subsetOf that.allFeatures
//    	
//    def contains(feature:Int) = 
//    	allFeatures contains feature
//    	
//    def complement =
//    	Context(parent, -feature)
//
//    override def toString =
//        feature + (if (parent == null) "" else "/" + parent.toString)
//}