package de.fosd.typechef.lexer.macrotable

import java.io.PrintWriter
import de.fosd.typechef.featureexpr._


object MacroContext {
    private var flagFilters = List((x: String) => true)
    //return true means flag can be specified by user, false means it is undefined initially
    def setPrefixFilter(prefix: String) {
        flagFilters = ((x: String) => !x.startsWith(prefix)) :: flagFilters
    }
    def setPostfixFilter(postfix: String) {
        flagFilters = ((x: String) => !x.endsWith(postfix)) :: flagFilters
    }
    def setPrefixOnlyFilter(prefix: String) {
        flagFilters = ((x: String) => x.startsWith(prefix)) :: flagFilters
    }
    def setListFilter(openFeaturesPath: String) {
        val openFeatures = io.Source.fromFile(openFeaturesPath).getLines.toSet
        flagFilters = (x => openFeatures.contains(x)) :: flagFilters
    }
    /**
     * Returns whether the macro x represents a feature.
     * It checks if any flag filters classify this as non-feature - equivalently, if all
     *  flag filters classify this as feature
     *
     *  If a macro does not represent a feature, it is not considered variable,
     *  and if it is not defined it is assumed to be always undefined, and it
     *  becomes thus easier to handle.
     *
     *  This method must not be called for macros known to be defined!
     */
    def flagFilter(x: String) = flagFilters.forall(_(x))
}

import FeatureExpr.createDefinedExternal

/**
 * represents the knowledge about macros at a specific point in time
 *
 * knownMacros contains all macros but no duplicates
 *
 * by construction, all alternatives are mutually exclusive (but do not necessarily add to BASE)
 */
class MacroContext[T](knownMacros: Map[String, Macro[T]], featureModel: FeatureModel) extends FeatureProvider {
    def this(fm: FeatureModel) = {
        this(Map(), fm)
    }
    def this() = {
        this(null)
    }
    def define(name: String, infeature: FeatureExpr, other: T): MacroContext[T] = {
        val feature = infeature //.resolveToExternal()
        val newMC = new MacroContext(
            knownMacros.get(name) match {
                case Some(macro) => knownMacros.updated(name, macro.addNewAlternative(new MacroExpansion[T](feature, other)))
                case None => {
                    //XXX createDefinedExternal should simply check
                    //MacroContext.flagFilter and
                    //evaluate to false if it is not defined.
                    val initialFeatureExpr = if (MacroContext.flagFilter(name))
                        feature.or(createDefinedExternal(name))
                    else
                        feature
                    knownMacros + ((name, new Macro[T](name, initialFeatureExpr, List(new MacroExpansion[T](feature, other)))))
                }
            }, featureModel)
        //        println("#define " + name)
        newMC
    }

    def undefine(name: String, infeature: FeatureExpr): MacroContext[T] = {
        val feature = infeature //.resolveToExternal()
        new MacroContext(
            knownMacros.get(name) match {
                case Some(macro) => knownMacros.updated(name, macro.andNot(feature))
                case None =>
                    val initialFeatureExpr = if (MacroContext.flagFilter(name))
                        createDefinedExternal(name)
                    else
                        FeatureExpr.dead
                    knownMacros + ((name, new Macro[T](name, initialFeatureExpr andNot feature, List())))
            }, featureModel)
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


    def isFeatureDead(feature: String): Boolean = getMacroCondition(feature).isContradiction(featureModel)

    def isFeatureBase(feature: String): Boolean = getMacroCondition(feature).isTautology(featureModel)

    def getMacroExpansions(identifier: String): Array[MacroExpansion[T]] =
        knownMacros.get(identifier) match {
            case Some(macro) => macro.getOther(featureModel).toArray
            case None => Array()
        }
    def getApplicableMacroExpansions(identifier: String, currentPresenceCondition: FeatureExpr): Array[MacroExpansion[T]] =
        getMacroExpansions(identifier).filter(m => !currentPresenceCondition.and(m.getFeature()).isContradiction(featureModel));

    override def toString() = {
        knownMacros.values.mkString("\n\n\n") + printStatistics
    }
    def debugPrint(writer: PrintWriter) {
        knownMacros.values.foreach(x => {
            writer print x;
            writer print "\n\n\n"
        })
        writer print printStatistics
    }
    def printStatistics =
        "\n\n\nStatistics (macros,macros with >1 alternative expansions,>2,>3,>4,non-trivial presence conditions):\n" +
            knownMacros.size + ";" +
            knownMacros.values.filter(_.numberOfExpansions > 1).size + ";" +
            knownMacros.values.filter(_.numberOfExpansions > 2).size + ";" +
            knownMacros.values.filter(_.numberOfExpansions > 3).size + ";" +
            knownMacros.values.filter(_.numberOfExpansions > 4).size + ";" +
            knownMacros.values.filter(!_.getFeature.isTautology(featureModel)).size + "\n"
    //,number of distinct configuration flags
    //    	+getNumberOfDistinctFlagsStatistic+"\n";
    //    private def getNumberOfDistinctFlagsStatistic = {
    //    	var flags:Set[String]=Set()
    //    	for (macro<-knownMacros.values)
    //    		macro.getFeature.accept(node=>{
    //    			node match {
    //    				case DefinedExternal(name) => flags=flags+name
    //    				case _=>
    //    			}
    //    		})
    //    	flags.size
    //    }

    private def getMacro(name: String) = knownMacros(name)
}

/**
 * name: name of the macro
 * feature: condition under which any of the macro definitions is visible
 * featureExpansions: a list of macro definions and the condition under which they are visible (should be mutually exclusive by construction)
 */
private class Macro[T](name: String, feature: FeatureExpr, var featureExpansions: List[MacroExpansion[T]]) {
    def getName() = name;
    def getFeature() = feature;
    def getOther(fm: FeatureModel) = {
        //lazy filtering
        featureExpansions = featureExpansions.filter(!_.getFeature().isContradiction(fm))
        featureExpansions;
    }
    def addNewAlternative(exp: MacroExpansion[T]) =
    //note addExpansion changes presence conditions of existing expansions
        new Macro[T](name, feature.or(exp.getFeature()), addExpansion(exp))

    /**
     * add an expansion (either by extending an existing one or by adding a new one).
     * the scope of all others is restricted accordingly
     */
    private def addExpansion(exp: MacroExpansion[T]): List[MacroExpansion[T]] = {
        var found = false;
        val modifiedExpansions = featureExpansions.map(other =>
            if (exp.getExpansion() == other.getExpansion()) {
                found = true
                other.extend(exp)
            } else
                other.andNot(exp.getFeature()))
        if (found) modifiedExpansions else exp :: modifiedExpansions
    }
    def andNot(expr: FeatureExpr) =
        new Macro[T](name, feature and (expr.not), featureExpansions.map(_.andNot(expr)));
    //  override def equals(that:Any) = that match { case m:Macro => m.getName() == name; case _ => false; }
    override def toString() = "#define " + name + " if " + feature.toString + " \n\texpansions \n" + featureExpansions.mkString("\n")
    def numberOfExpansions = featureExpansions.size
}

class MacroExpansion[T](feature: FeatureExpr, expansion: T /* Actually, MacroData from PartialPreprocessor*/) {
    def getFeature(): FeatureExpr = feature
    def getExpansion(): T = expansion
    def andNot(expr: FeatureExpr): MacroExpansion[T] = new MacroExpansion[T](feature and (expr.not), expansion)
    override def toString() = "\t\t" + expansion.toString() + " if " + feature.toString
    //if the other has the same expansion, merge features as OR
    def extend(other: MacroExpansion[T]) =
        new MacroExpansion[T](feature.or(other.getFeature()), expansion)
}

object MacroIdGenerator {
    var macroId = 0
    def nextMacroId = {
        macroId = macroId + 1
        macroId
    }
}
