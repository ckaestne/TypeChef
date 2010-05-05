package de.fosd.typechef.featureexpr

/**
 * represents the knowledge about macros at a specific point in time time
 * 
 * knownMacros contains all macros but no duplicates
 */
class MacroContext(knownMacros: Map[String, Macro]) extends FeatureProvider {
  def this() = { this(Map()) }
  def define(name: String, feature: FeatureExpr, other: Any):MacroContext = new MacroContext(
    knownMacros.get(name) match {
      case Some(macro) => knownMacros.update(name,macro.or(feature).addExpansion(new FeatureExpansion(feature, other)))
      case None => knownMacros + ((name, new Macro(name, Or(DefinedExternal(name),feature), List(new FeatureExpansion(feature, other)))))
    }
  )
  
  def undefine(name: String, feature: FeatureExpr):MacroContext = new MacroContext(
    knownMacros.get(name) match {
      case Some(macro) => knownMacros.update(name,macro.andNot(feature))
      case None => knownMacros + ((name, new Macro(name, And(DefinedExternal(name),Not(feature)), List())))
    }
  )
  
  def getMacroCondition(feature: String): FeatureExpr = {
    knownMacros.get(feature) match {
      case Some(macro) => macro.getFeature()
      case None => DefinedExternal(feature)
    }
  } 
    
  def isFeatureDead(feature:String):Boolean = getMacroCondition(feature).isDead() 
    
  def isFeatureBase(feature:String):Boolean = getMacroCondition(feature).isBase()
    
  override def toString() = { var r:String =""; for (macro<-knownMacros) r+=macro.toString + "\n"; r }
}

/**
 * name: name of the macro
 * feature: condition under which any of the macro definitions is visible
 * featureExpansions: a list of macro definions and the condition under which they are visible (should be mutually exclusive by construction)
 */
private class Macro(name: String, feature: FeatureExpr, featureExpansions: List[FeatureExpansion]) {
  def getName() = name;
  def getFeature() = feature;
  def getOther() = featureExpansions;
  def isBase():Boolean = feature.isBase();
  def isDead():Boolean = feature.isDead();
  def or(expr: FeatureExpr):Macro = new Macro(name, Or(feature, expr).simplify(), featureExpansions) 
  def andNot(expr: FeatureExpr):Macro = 
    new Macro(name, And(feature, Not(expr)).simplify(), featureExpansions.map(_.andNot(expr)));
  def addExpansion(exp:FeatureExpansion):Macro = new Macro(name, feature, exp :: featureExpansions)
//  override def equals(that:Any) = that match { case m:Macro => m.getName() == name; case _ => false; }
  override def toString() = "#define "+name+" if "+feature.toString
}

private class FeatureExpansion(feature: FeatureExpr, expansion:Any) {
  def andNot(expr: FeatureExpr):FeatureExpansion = new FeatureExpansion(And(feature,Not(expr)).simplify, expansion)
}

