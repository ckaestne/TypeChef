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

 
 def assertSimplify(exprA:FeatureExpr,expectedResult:FeatureExpr){
   println(exprA.simplify()+" == "+ expectedResult)
   assert(exprA.simplify()==expectedResult)
 }
 
 
 def assertCNF(exprA:FeatureExpr,expectedResult:FeatureExpr){
   val cnf=exprA.toCNF().simplify()
   println(cnf+" == "+ expectedResult.simplify())
   assertIsCNF(cnf)
   assert(cnf==expectedResult.simplify())
 }
 def assertIsCNF(expr:FeatureExpr){
   _assertIsCNF(expr.toCNF);
   _assertIsCNF(expr.toCnfEquiSat);
 }   

 def _assertIsCNF(cnf:FeatureExpr){
   println("CNF: "+cnf)
   cnf match {
     case And(children) => for (child<-children) checkLevelOr(child);
     case e=>checkLevelOr(e);
   }
 }
 def checkLevelOr(expr:FeatureExpr) {
   expr match {
     case Or(children) => for (child<-children) checkLevelLiteral(child);
     case e=>checkLevelLiteral(e);
   }
   
 }
 def checkLevelLiteral(expr:FeatureExpr) {
   expr match {
     case DefinedExternal(name) =>
     case IntegerLit(v) =>
     case Not(DefinedExternal(name)) =>
     case e=>assert(false, expr+" is not a literal")
   }
 }
 
 assertSimplify(And(Set(DefinedExternal("a"))),DefinedExternal("a"))
 assertSimplify(And(Set(DefinedExternal("a"),DefinedExternal("a"))),DefinedExternal("a"))
 assertSimplify(And(Set(DefinedExternal("a"),DefinedExternal("b"))),And(Set(DefinedExternal("a"),DefinedExternal("b"))))//except the order
 assertSimplify(And(Set(BaseFeature(),DefinedExternal("a"))),DefinedExternal("a"))
 assertSimplify(And(Set(DeadFeature(),DefinedExternal("a"),DefinedExternal("b"))),DeadFeature())
 assertSimplify(And(Set(Not(DefinedExternal("a")),DefinedExternal("a"),DefinedExternal("b"))),DeadFeature())

  assertSimplify(Or(Set(DefinedExternal("a"))),DefinedExternal("a"))
 assertSimplify(Or(Set(DefinedExternal("a"),DefinedExternal("a"))),DefinedExternal("a"))
 assertSimplify(Or(Set(DefinedExternal("a"),DefinedExternal("b"))),Or(Set(DefinedExternal("a"),DefinedExternal("b"))))//except the order
 assertSimplify(Or(Set(BaseFeature(),DefinedExternal("a"))),BaseFeature())
 assertSimplify(Or(Set(DeadFeature(),DefinedExternal("a"))),DefinedExternal("a"))
 assertSimplify(Or(Set(Not(DefinedExternal("a")),DefinedExternal("a"),DefinedExternal("b"))),BaseFeature())

 assertSimplify(new And(DefinedExternal("a"),new And(DefinedExternal("b"),DefinedExternal("c"))),And(Set(DefinedExternal("a"),DefinedExternal("b"),DefinedExternal("c"))))
 assertSimplify(new Or(DefinedExternal("a"),new Or(DefinedExternal("b"),DefinedExternal("c"))),Or(Set(DefinedExternal("a"),DefinedExternal("b"),DefinedExternal("c"))))

 
 assertCNF( Not(new And(Not(new Or(DefinedExternal("a"),Not(DefinedExternal("b")))),DefinedExternal("c"))),
		 Or(Set(DefinedExternal("a"),Not(DefinedExternal("b")),Not(DefinedExternal("c"))))
 );
 
 assertCNF(new Or(DefinedExternal("a"),new And(DefinedExternal("b"),DefinedExternal("c"))),
 	new And(new Or(DefinedExternal("a"),DefinedExternal("b")),new Or(DefinedExternal("a"),DefinedExternal("c"))))
 assertCNF(new Or(new And(DefinedExternal("a1"),DefinedExternal("a2")),new And(DefinedExternal("b"),DefinedExternal("c"))),
 	And(Set(new Or(DefinedExternal("a1"),DefinedExternal("b")),
          new Or(DefinedExternal("a2"),DefinedExternal("b")),
          new Or(DefinedExternal("a1"),DefinedExternal("c")),
          new Or(DefinedExternal("a2"),DefinedExternal("c")))))

 assertIsCNF(And(Set(new Or(DefinedExternal("a1"),DefinedExternal("b")),
          new Or(new And(DefinedExternal("a2"),new Or(DefinedExternal("b"),DefinedExternal("c"))),
          new Or(DefinedExternal("a1"),DefinedExternal("c"))),
          new Or(DefinedExternal("a2"),DefinedExternal("c")))))
 
 val v=new Or(DefinedExternal("a"),new And(DefinedExternal("b"),DefinedExternal("c"))).toCnfEquiSat;
 println(v)
 val vs=v.simplify
 println(vs)
 
}
