package de.fosd.typechef.featureexpr

import java.net.URI

/**
 * Factory interface to create feature models
 *
 * A classname implementing this interface can be provided as command line option to the frontend
 */

trait FeatureModelFactory {
  def empty: FeatureModel

  /**
   * create a feature model from a feature expression
   */
  def create(expr: FeatureExpr): FeatureModel

  /**
   * load a standard Dimacs file as feature model, where comments map
   * variable ids to names
   *
   * does prefix all loaded names with CONFIG_ by default
   */
  def createFromDimacsFile(file: String, variablePrefix: String = "CONFIG_"): FeatureModel

  /**
   * special reader for the -2var model used by the LinuxAnalysis tools from waterloo
   *
   * prefixes all loaded names with CONFIG_ by default and translates _2 postfix in variable
   * names to _MODULE
   */
  def createFromDimacsFile_2Var(file: String): FeatureModel

  def createFromDimacsFile_2Var(file: URI): FeatureModel

}