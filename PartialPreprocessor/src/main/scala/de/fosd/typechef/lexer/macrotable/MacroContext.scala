package de.fosd.typechef.lexer.macrotable

import java.io.PrintWriter
import de.fosd.typechef.featureexpr._
import sat.LazyLib.Susp
import sat.{CastHelper, LazyLib}


/**
 * the macrofilter decides which macros are used as symbolic variables and which are assumed to be initially
 * undefined.
 */

class MacroFilter(flagFilters: List[(String) => Boolean]) {
  def this() = this(Nil)

  //return true means flag can be specified by user, false means it is undefined initially
  def setPrefixFilter(prefix: String) = new MacroFilter(
    ((x: String) => !x.startsWith(prefix)) :: flagFilters
  )

  def setPostfixFilter(postfix: String) = new MacroFilter(
    ((x: String) => !x.endsWith(postfix)) :: flagFilters
  )

  def setPrefixOnlyFilter(prefix: String) = new MacroFilter(
    ((x: String) => x.startsWith(prefix)) :: flagFilters
  )

  //* file with valid feature names, separated by empty linebreaks
  def setListFilter(openFeaturesFile: String) = {
    val openFeatures = io.Source.fromFile(openFeaturesFile).getLines.toSet
    new MacroFilter(
      (x => openFeatures.contains(x)) :: flagFilters
    )
  }

  /**
   * Returns whether the macro x represents a feature.
   * It checks if any flag filters classify this as non-feature - equivalently, if all
   * flag filters classify this as feature
   *
   * If a macro does not represent a feature, it is not considered variable,
   * and if it is not defined it is assumed to be always undefined, and it
   * becomes thus easier to handle.
   *
   * This method must not be called for macros known to be defined!
   */
  def flagFilter(x: String) = flagFilters.forall(_(x))
}

import FeatureExprFactory._

/**
 * represents the knowledge about macros at a specific point in time
 *
 * knownMacros contains all macros but no duplicates
 *
 * by construction, all alternatives are mutually exclusive (but do not necessarily add to BASE)
 *
 * CNFCache is only used by SAT implementation of FeatureExprLib
 */
class MacroContext[T](knownMacros: Map[String, Macro[T]], var cnfCache: Map[String, (String, Susp[FeatureExpr])], featureModel: FeatureModel, macroFilter: MacroFilter) extends FeatureProvider {
  def this(fm: FeatureModel, macroFilter: MacroFilter) = {
    this(Map(), Map(), fm, macroFilter)
  }

  def this(macroFilter: MacroFilter) = {
    this(null, macroFilter: MacroFilter)
  }

  def this() = {
    this(new MacroFilter())
  }

  def define(name: String, infeature: FeatureExpr, other: T): MacroContext[T] = {
    val feature = infeature //.resolveToExternal()
    val newMC = new MacroContext(
        knownMacros.get(name) match {
          case Some(mcr) => knownMacros.updated(name, mcr.addNewAlternative(new MacroExpansion[T](feature, other)))
          case None => {
            //XXX createDefinedExternal should simply check
            //MacroContext.flagFilter and
            //evaluate to false if it is not defined.
            val initialFeatureExpr = if (macroFilter.flagFilter(name))
              feature.or(createDefinedExternal(name))
            else
              feature
            knownMacros + ((name, new Macro[T](name, initialFeatureExpr, List(new MacroExpansion[T](feature, other)))))
          }
        }, cnfCache - name, featureModel, macroFilter)
    //        println("#define " + name)
    newMC
  }

  def undefine(name: String, infeature: FeatureExpr): MacroContext[T] = {
    val feature = infeature //.resolveToExternal()
    new MacroContext(
      knownMacros.get(name) match {
        case Some(mcr) => knownMacros.updated(name, mcr.andNot(feature))
        case None =>
          val initialFeatureExpr = if (macroFilter.flagFilter(name))
            createDefinedExternal(name)
          else
            False
          knownMacros + ((name, new Macro[T](name, initialFeatureExpr andNot feature, List())))
      }, cnfCache - name, featureModel, macroFilter)
  }

  def getMacroCondition(feature: String): FeatureExpr = {
    knownMacros.get(feature) match {
      case Some(mcr) => mcr.getFeature()
      case None =>
        if (macroFilter.flagFilter(feature))
          createDefinedExternal(feature)
        else
          False
    }
  }

  /**
   * this returns a condition for the SAT solver in CNF in the following
   * form
   *
   * (newMacroName, DefinedExternal(newMacroName) <=> getMacroCondition)
   *
   * This means that the MacroCondition appears twice in the output.
   * [Probably wrong:]
   * //on nested macro definitions, this could cause a blowup exponential in the
   * //nesting depth of macro definitions (if each new definition is small).
   * [Real explanation:]
   * there is no reason to duplicate the new clauses if the
   * definition is used twice, or the formula is renamed!
   *
   * It could be safe to create just one implication, solving this problem, as we do in the
   * equisatisfiable transformation. However, that depends on whether the macro is used in a covariant or
   * contravariant position, i.e. if there is respectively an even or an odd number of Not above the formula
   * when represented as a parse tree. If a macro is used both ways (as it often happens), we need both implications.
   *
   * The result is cached. $$ is later replaced by a name for the SAT solver
   */
  def getMacroConditionCNF(name: String): (String, Susp[FeatureExpr]) = {
    if (cnfCache.contains(name))
      return cnfCache(name)

    val newMacroName = name + "$$" + MacroIdGenerator.nextMacroId
    val c = getMacroCondition(name)
    val d = createDefinedExternal(newMacroName)
    val condition = c equiv d
    val cnf = LazyLib.delay(CastHelper.asSATFeatureExpr(condition).toCnfEquiSat)
    val result = (newMacroName, cnf)
    cnfCache = cnfCache + (name -> result)
    result
  }

  def isFeatureDead(feature: String): Boolean = getMacroCondition(feature).isContradiction(featureModel)

  def isFeatureBase(feature: String): Boolean = getMacroCondition(feature).isTautology(featureModel)

  def getMacroExpansions(identifier: String): Array[MacroExpansion[T]] =
    knownMacros.get(identifier) match {
      case Some(mcr) => mcr.getOther(featureModel).toArray
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
