package de.fosd.typechef.featureexpr

object MacroContext {
  var flagFilter = (x: String) => true;
  def setPrefixFilter(prefix: String) {
    flagFilter = (x: String) => !x.startsWith(prefix);
  }
  def setPrefixOnlyFilter(prefix: String) {
    flagFilter = (x: String) => x.startsWith(prefix);
  }
}


import FeatureExpr.createDefinedExternal
/**
 * represents the knowledge about macros at a specific point in time time
 * 
 * knownMacros contains all macros but no duplicates
 * 
 * by construction, all alternatives are mutually exclusive (but do not necessarily add to BASE)
 */
class MacroContext(knownMacros: Map[String, Macro]) extends FeatureProvider {
  /**
   * when true, only CONFIG_ flags can be defined externally (simplifies the handling signficiantly)
   */

  def this() = { this(Map()) }
  def define(name: String, feature: FeatureExpr, other: Any): MacroContext = new MacroContext(
    knownMacros.get(name) match {
      case Some(macro) => knownMacros.updated(name, macro.addNewAlternative(new MacroExpansion(feature, other)))
      //      case Some(macro) => knownMacros.update(name,macro.andNot(feature).or(feature).addExpansion(new MacroExpansion(feature, other)))
      case None => {
        //    	  val initialFeatureExpr = if (!MacroContext.EXTERNAL_CONFIG_ONLY || name.startsWith("CONFIG_")) 
        val initialFeatureExpr = if (MacroContext.flagFilter(name))
          feature.or(createDefinedExternal(name))
        else
          feature
        knownMacros + ((name, new Macro(name, initialFeatureExpr, List(new MacroExpansion(feature, other)))))
      }
    }
    )

  def undefine(name: String, feature: FeatureExpr): MacroContext = new MacroContext(
    knownMacros.get(name) match {
      case Some(macro) => knownMacros.updated(name, macro.andNot(feature))
      case None => knownMacros + ((name, new Macro(name, feature.not().and(createDefinedExternal(name)), List())))
    }
    )

  /**
   *  //ChK: this is domain knowledge for linux
   *  we assume that everything not starting with CONFIG_ is initially undefined!
   *  everything with CONFIG_ is unknown (defined when externally defined)
   */
  def getMacroCondition(feature: String): FeatureExpr = {
    knownMacros.get(feature) match {
      case Some(macro) => macro.getFeature()
      case None =>
        if (MacroContext.flagFilter(feature))
          createDefinedExternal(feature)
        else
          FeatureExpr.dead
    }
  }

  def isFeatureDead(feature: String): Boolean = getMacroCondition(feature).isDead()

  def isFeatureBase(feature: String): Boolean = getMacroCondition(feature).isBase()

  def getMacroExpansions(identifier: String): Array[MacroExpansion] =
    knownMacros.get(identifier) match {
      case Some(macro) => macro.getOther().toArray.filter(!_.getFeature().isDead())
      case None => Array()
    }
  def getApplicableMacroExpansions(identifier: String, currentPresenceCondition: FeatureExpr): Array[MacroExpansion] =
    getMacroExpansions(identifier).filter(m => !currentPresenceCondition.and(m.getFeature()).isDead());

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
  def isBase(): Boolean = feature.isBase();
  def isDead(): Boolean = feature.isDead();
  def addNewAlternative(exp: MacroExpansion): Macro = {
    new Macro(name, feature.or(exp.getFeature()), addExpansion(exp))
  }
  /**
   * add an expansion (either by extending an existing one or by adding a new one). 
   * the scope of all others is restricted accordingly
   */
  private def addExpansion(exp: MacroExpansion): List[MacroExpansion] = {
    var found = false;
    val modifiedExpansions = featureExpansions.map(other =>
      if (exp.getExpansion() == other.getExpansion()) {
        found = true
        other.extend(exp)
      } else
        other.andNot(exp.getFeature())
      )
    if (found) modifiedExpansions else exp :: featureExpansions
  }
  def andNot(expr: FeatureExpr): Macro =
    new Macro(name, feature.and(expr.not), featureExpansions.map(_.andNot(expr)));
  //  override def equals(that:Any) = that match { case m:Macro => m.getName() == name; case _ => false; }
  override def toString() = "#define " + name + " if " + feature.toString + " \n\texpansions \n" + featureExpansions.mkString("\n")
}

class MacroExpansion(feature: FeatureExpr, expansion: Any /* Actually, MacroData from PartialPreprocessor*/) {
  def getFeature(): FeatureExpr = feature
  def getExpansion(): Any = expansion
  def andNot(expr: FeatureExpr): MacroExpansion = new MacroExpansion(feature.and(expr.not), expansion)
  override def toString() = "\t\t" + expansion.toString() + " if " + feature.toString
  //if the other has the same expansion, merge features as OR
  def extend(other: MacroExpansion): MacroExpansion =
    if (expansion == other.getExpansion())
      new MacroExpansion(feature.or(other.getFeature()), expansion)
    else this
}

