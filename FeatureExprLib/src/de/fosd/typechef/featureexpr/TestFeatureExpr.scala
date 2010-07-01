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
     
//     var context = new MacroContext(Map());
//     println(FeatureExpr.createDefined("foo",context))//.possibleValues(context));
//     context = context.define("bar", Not(FeatureExpr.createDefined("bar",context)), null);
//     println(FeatureExpr.createDefined("bar",context))//.possibleValues(context));

 
 def assertSimplify(exprA:FeatureExprTree,expectedResult:FeatureExprTree){
   println(exprA.simplify().print()+" == "+ expectedResult.print())
   assert(exprA.simplify()==expectedResult)
 }
 
 
 def assertIsCNF(expr:FeatureExprTree){
   _assertIsCNF(expr.toCnfEquiSat);
 }   

 def _assertIsCNF(cnf:FeatureExprTree){
   println("CNF: "+cnf)
   cnf match {
     case And(children) => for (child<-children) checkLevelOr(child);
     case e=>checkLevelOr(e);
   }
 }
 def checkLevelOr(expr:FeatureExprTree) {
   expr match {
     case Or(children) => for (child<-children) checkLevelLiteral(child);
     case e=>checkLevelLiteral(e);
   }
   
 }
 def checkLevelLiteral(expr:FeatureExprTree) {
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

 assertSimplify(new Or(new Or(DefinedExternal("a"),DefinedExternal("b")),Not(DefinedExternal("b"))),BaseFeature())
 assertSimplify(new Or(new Or(DefinedExternal("a"),new Or(DefinedExternal("b"),DefinedExternal("c"))),Not(new Or(DefinedExternal("b"),DefinedExternal("c")))),BaseFeature())
 assertSimplify(new Or(Set(DefinedExternal("a"),DefinedExternal("b"),DefinedExternal("c"),Not(new Or(DefinedExternal("b"),DefinedExternal("c"))))),BaseFeature())
 assertSimplify(new And(And(Set(DefinedExternal("a"),DefinedExternal("b"),DefinedExternal("c"))),Not(new And(DefinedExternal("b"),DefinedExternal("c")))),DeadFeature())
 
 assertIsCNF(And(Set(new Or(DefinedExternal("a1"),DefinedExternal("b")),
          new Or(new And(DefinedExternal("a2"),new Or(DefinedExternal("b"),DefinedExternal("c"))),
          new Or(DefinedExternal("a1"),DefinedExternal("c"))),
          new Or(DefinedExternal("a2"),DefinedExternal("c")))))
 
 val v=new Or(DefinedExternal("a"),new And(DefinedExternal("b"),DefinedExternal("c"))).toCnfEquiSat;
 println(v)
 val vs=v.simplify
 println(vs)
 
}
