package de.fosd.typechef.featureexpr

/**
 * represents the knowledge about macros at a specific point in time time
 * 
 * knownMacros contains all macros but no duplicates
 * 
 * by construction, all alternatives are mutually exclusive (but do not necessarily add to BASE)
 */
class MacroContext(knownMacros: Map[String, Macro]) extends FeatureProvider {
  def this() = { this(Map()) }
  def define(name: String, feature: FeatureExpr, other: Any):MacroContext = new MacroContext(
    knownMacros.get(name) match {
      case Some(macro) => knownMacros.update(name,macro.andNot(feature).or(feature).addExpansion(new MacroExpansion(feature, other)))
      case None => knownMacros + ((name, new Macro(name, new Or(DefinedExternal(name),feature), List(new MacroExpansion(feature, other)))))
    }
  )
  
  def undefine(name: String, feature: FeatureExpr):MacroContext = new MacroContext(
    knownMacros.get(name) match {
      case Some(macro) => knownMacros.update(name,macro.andNot(feature))
      case None => knownMacros + ((name, new Macro(name, new And(DefinedExternal(name),Not(feature)), List())))
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
  
  def getMacroExpansions(identifier: String):Array[MacroExpansion] =
    knownMacros.get(identifier) match {
      case Some(macro) => macro.getOther().toArray.filter(!_.getFeature().isDead())
      case None => Array()
    }
    
  override def toString() = { knownMacros.values.mkString("\n\n\n") }
}

/**
 * name: name of the macro
 * feature: condition under which any of the macro definitions is visible
 * featureExpansions: a list of macro definions and the condition under which they are visible (should be mutually exclusive by construction)
 */
private class Macro(name: String, feature: FeatureExpr, featureExpansions: List[MacroExpansion]) {
  def getName() = name;
  def getFeature() = feature;
  def getOther() = featureExpansions;
  def isBase():Boolean = feature.isBase();
  def isDead():Boolean = feature.isDead();
  def or(expr: FeatureExpr):Macro = new Macro(name, new Or(feature, expr).simplify(), featureExpansions) 
  def andNot(expr: FeatureExpr):Macro = 
    new Macro(name, new And(feature, Not(expr)).simplify(), featureExpansions.map(_.andNot(expr)));
  def addExpansion(exp:MacroExpansion):Macro = {
    if (featureExpansions.exists(_.getExpansion() == exp.getExpansion())) 
    	new Macro(name, feature, featureExpansions.map(_.extend(exp)))
    else
    	new Macro(name, feature, exp :: featureExpansions)
   }
//  override def equals(that:Any) = that match { case m:Macro => m.getName() == name; case _ => false; }
  override def toString() = "#define "+name+" if "+feature.toString+" \n\texpansions \n"+featureExpansions.mkString("\n")
}

class MacroExpansion(feature: FeatureExpr, expansion:Any) {
  def getFeature():FeatureExpr = feature
  def getExpansion():Any = expansion
  def andNot(expr: FeatureExpr):MacroExpansion = new MacroExpansion(new And(feature,Not(expr)).simplify, expansion)
  override def toString() = "\t\t"+expansion.toString()+" if "+feature.toString
  //if the other has the same expansion, merge features as OR
  def extend(other:MacroExpansion):MacroExpansion =
    if (expansion==other.getExpansion()) 
      new MacroExpansion(new Or(feature,other.getFeature()),expansion)
    else this
}

