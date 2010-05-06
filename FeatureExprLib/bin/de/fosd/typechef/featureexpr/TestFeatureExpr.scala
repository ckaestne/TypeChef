package de.fosd.typechef.featureexpr

import org.junit._;
import Assert._



//class TmpFeatureProvider extends FeatureProvider {
//  def isFeatureDefined(feature:String):Boolean = feature=="def"
//  /**
//   returns true if feature is certainly included (no external variability)
//   */
//  def isFeatureDead(feature:String):Boolean = feature=="dead"
//  /**
//   returns true if feature is certainly excluded (no external variability)
//   */
//  def isFeatureBase(feature:String):Boolean = feature=="base"
//}

object TestFeatureExpr  extends Application {
//     assert(Defined("foo").possibleValues(new TmpFeatureProvider())==Set(0,1))
//     assert(Defined("def").possibleValues(new TmpFeatureProvider())==Set(0,1))
//     assert(Defined("dead").possibleValues(new TmpFeatureProvider())==Set(0))
//     assert(Defined("base").possibleValues(new TmpFeatureProvider())==Set(1))
//     assert(Or(Defined("foo"),Defined("bar")).possibleValues(new TmpFeatureProvider())==Set(0,1))
//     assert(Or(Defined("foo"),Defined("base")).possibleValues(new TmpFeatureProvider())==Set(1))
//     assert(And(Defined("foo"),Defined("dead")).possibleValues(new TmpFeatureProvider())==Set(0))
//     assert(And(Defined("foo"),Defined("bar")).possibleValues(new TmpFeatureProvider())==Set(0,1))
//     assert(And(IntegerLit(3),Defined("base")).possibleValues(new TmpFeatureProvider())==Set(1))
     //println(And(Defined("foo"),Not(Defined("foo"))).possibleValues(new TmpFeatureProvider()))//should not happen. need SAT solver?
     
     var context = new MacroContext(Map());
     println(FeatureExpr.createDefined("foo",context))//.possibleValues(context));
     context = context.define("bar", Not(FeatureExpr.createDefined("bar",context)), null);
     println(FeatureExpr.createDefined("bar",context))//.possibleValues(context));
//     println(Defined("foo").possibleValues(context));
     
//     println(BinaryFeatureExpr(IntegerLit(1),IntegerLit(2),"==",_==_));
}
