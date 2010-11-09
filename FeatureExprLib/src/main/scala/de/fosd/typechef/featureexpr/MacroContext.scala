package de.fosd.typechef.featureexpr

object MacroContext {
    private var flagFilters = List((x: String) => true) //return true means flag can be specified by user, false means it is undefined initially
    def setPrefixFilter(prefix: String) {
        flagFilters = ((x: String) => !x.startsWith(prefix)) :: flagFilters
    }
    def setPostfixFilter(postfix: String) {
        flagFilters = ((x: String) => !x.endsWith(postfix)) :: flagFilters
    }
    def setPrefixOnlyFilter(prefix: String) {
        flagFilters = ((x: String) => x.startsWith(prefix)) :: flagFilters
    }
    def flagFilter(x: String) = flagFilters.forall(_(x))

}

import FeatureExpr.createDefinedExternal
/**
 * represents the knowledge about macros at a specific point in time time
 * 
 * knownMacros contains all macros but no duplicates
 * 
 * by construction, all alternatives are mutually exclusive (but do not necessarily add to BASE)
 */
class MacroContext(knownMacros: Map[String, Macro], var cnfCache:Map[String, NF]) extends FeatureProvider {
    /**
     * when true, only CONFIG_ flags can be defined externally (simplifies the handling signficiantly)
     */

    def this() = { this(Map(),Map()) }
    def define(name: String, infeature: FeatureExpr, other: Any): MacroContext = {
        val feature = infeature.resolveToExternal(this)
        val newMC=new MacroContext(
            knownMacros.get(name) match {
                case Some(macro) => knownMacros.updated(name, macro.addNewAlternative(new MacroExpansion(feature, other)))
                case None => {
                    val initialFeatureExpr = if (MacroContext.flagFilter(name))
                        feature.or(createDefinedExternal(name))
                    else
                        feature
                    knownMacros + ((name, new Macro(name, initialFeatureExpr, List(new MacroExpansion(feature, other)))))
                }
            }, cnfCache - name)
        println(newMC.getMacro(name))
        newMC
    }

    def undefine(name: String, infeature: FeatureExpr): MacroContext = {
        val feature = infeature.resolveToExternal(this)
        new MacroContext(
            knownMacros.get(name) match {
                case Some(macro) => knownMacros.updated(name, macro.andNot(feature))
                case None => knownMacros + ((name, new Macro(name, feature.not().and(createDefinedExternal(name)), List())))
            }, cnfCache - name)
    }

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

    /**
     * this returns a condition for the SAT solver in CNF in the following
     * form
     * 
     * DefinedMacro <=> getMacroCondition
     * 
     * the result is cached
     */
    def getMacroSATCondition(feature: String): NF = {
    	if (cnfCache.contains(feature))
    		return cnfCache(feature)
    	
    	val c=getMacroCondition(feature)
    	val d=FeatureExpr.createDefinedMacro(feature) 
    	val a1=(c implies d) 
    	val a2=(d implies c)
    	val condition=a1 and a2  
    	val cnf=condition.toCNF
    	cnfCache = cnfCache + ((feature, cnf))
    	cnf
    }
    
    
    def isFeatureDead(feature: String): Boolean = getMacroCondition(feature).isDead(this)

    def isFeatureBase(feature: String): Boolean = getMacroCondition(feature).isBase(this)

    def getMacroExpansions(identifier: String): Array[MacroExpansion] =
        knownMacros.get(identifier) match {
            case Some(macro) => macro.getOther().toArray.filter(!_.getFeature().isDead(this))
            case None => Array()
        }
    def getApplicableMacroExpansions(identifier: String, currentPresenceCondition: FeatureExpr): Array[MacroExpansion] =
        getMacroExpansions(identifier).filter(m => !currentPresenceCondition.and(m.getFeature()).isDead(this));

    override def toString() = { knownMacros.values.mkString("\n\n\n") }
    
    private def getMacro(name:String) = knownMacros(name)
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
    def isBase(macroTable: MacroContext): Boolean = feature.isBase(macroTable);
    def isDead(macroTable: MacroContext): Boolean = feature.isDead(macroTable);
    def addNewAlternative(exp: MacroExpansion): Macro =
        //note addExpansion changes presence conditions of existing expansions
        new Macro(name, feature.or(exp.getFeature()), addExpansion(exp))

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
                other.andNot(exp.getFeature())) //.filter(!_.getFeature.isContradiction(this))
        if (found) modifiedExpansions else exp :: modifiedExpansions
    }
    def andNot(expr: FeatureExpr): Macro =
        new Macro(name, feature and (expr.not), featureExpansions.map(_.andNot(expr))) //.filter(!_.getFeature.isContradiction(this)));
    //  override def equals(that:Any) = that match { case m:Macro => m.getName() == name; case _ => false; }
    override def toString() = "#define " + name + " if " + feature.toString + " \n\texpansions \n" + featureExpansions.mkString("\n")
}

class MacroExpansion(feature: FeatureExpr, expansion: Any /* Actually, MacroData from PartialPreprocessor*/ ) {
    def getFeature(): FeatureExpr = feature
    def getExpansion(): Any = expansion
    def andNot(expr: FeatureExpr): MacroExpansion = new MacroExpansion(feature and (expr.not), expansion)
    override def toString() = "\t\t" + expansion.toString() + " if " + feature.toString
    //if the other has the same expansion, merge features as OR
    def extend(other: MacroExpansion): MacroExpansion =
        if (expansion == other.getExpansion())
            new MacroExpansion(feature.or(other.getFeature()), expansion)
        else this
}

