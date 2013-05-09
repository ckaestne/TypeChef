package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.conditional._
import linker.CInferInterface

/**
 * checks an AST (from CParser) for type errors (especially dangling references)
 *
 * performs type checking in a single tree-walk, uses lookup functions from various traits
 *
 * @author kaestner
 *
 */

class CTypeSystemFrontend(iast: TranslationUnit, featureModel: FeatureModel = FeatureExprFactory.default.featureModelFactory.empty) extends CTypeSystem with CInferInterface {


  def prettyPrintType(ctype: Conditional[CType]): String =
    Conditional.toOptList(ctype).map(o => o.feature.toString + ": \t" + o.entry).mkString("\n")

  private def indentAllLines(s: String): String =
    s.lines.map("\t\t" + _).foldLeft("")(_ + "\n" + _)

  var errors: List[TypeError] = List()

  var isSilent = false

  val DEBUG_PRINT = false

  def dbgPrint(o: Any) = if (DEBUG_PRINT) print(o)

  def dbgPrintln(o: Any) = if (DEBUG_PRINT) println(o)

  val verbose = false


  var externalDefCounter: Int = 0

  override def checkingExternal(externalDef: ExternalDef) = {
    externalDefCounter = externalDefCounter + 1
    if (verbose)
      println("check " + externalDefCounter + "/" + iast.defs.size + ". line " + externalDef.getPositionFrom.getLine + ". err " + errors.size)
  }

  override def issueTypeError(severity: Severity.Severity, condition: FeatureExpr, msg: String, where: AST) =
  //first check without feature model for performance reasons
    if (condition.isSatisfiable() && condition.isSatisfiable(featureModel)) {
      val e = new TypeError(severity, condition, msg, where)
      errors = e :: errors
      if (!isSilent) {
        println("  - " + e)
      }
    }


  /**
   * Returns true iff no errors were found.
   * @return
   */
  def checkAST: Boolean = {

    errors = List() // clear error list
    typecheckTranslationUnit(iast)
    if (errors.isEmpty)
      println("No type errors found.")
    else {
      println("Found " + errors.size + " type errors: ");
    }
    //println("\n")
    return errors.isEmpty
  }

  def checkASTSilent: Boolean = {
    isSilent = true
    errors = List() // clear error list
    typecheckTranslationUnit(iast)
    return errors.isEmpty
  }
}


class TypeError(val severity: Severity.Severity, val condition: FeatureExpr, val msg: String, val where: AST) {
  override def toString =
    severity.toString.take(1) + " [" + condition + "] " +
      (if (where == null) "" else where.getPositionFrom + "--" + where.getPositionTo) + "\n\t" + msg
}