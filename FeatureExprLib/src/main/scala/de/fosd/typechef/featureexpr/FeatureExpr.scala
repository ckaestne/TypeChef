package de.fosd.typechef.featureexpr

import java.io.Writer


trait FeatureExpr {

  /**
   * x.isSatisfiable(fm) is short for x.and(fm).isSatisfiable
   * but is faster because FM is cached
   */
  def isSatisfiable(fm: FeatureModel): Boolean

  protected def calcSize: Int

  def toTextExpr: String

  //or other ToString variations for debugging etc
  def collectDistinctFeatures: Set[String]

  def collectDistinctFeatureObjects: Set[SingleFeatureExpr]

  def getSatisfiableAssignment(featureModel: FeatureModel, interestingFeatures: Set[SingleFeatureExpr], preferDisabledFeatures: Boolean): Option[Pair[List[SingleFeatureExpr], List[SingleFeatureExpr]]]

  def or(that: FeatureExpr): FeatureExpr

  def and(that: FeatureExpr): FeatureExpr

  def not(): FeatureExpr


  //equals, hashcode


  final def unary_! = not

  final def &(that: FeatureExpr) = and(that)

  final def |(that: FeatureExpr) = or(that)

  //not final for potential optimizations
  def implies(that: FeatureExpr) = this.not.or(that)

  def xor(that: FeatureExpr) = (this or that) andNot (this and that)

  def equiv(that: FeatureExpr) = (this and that) or (this.not and that.not)

  /**
   * If this expr is a simple concatenation of SingleFeatureExpressions (and their negations),
   * then this method returns the expression as a set of singleFeatureExpr that occur as enabled (disabled).
   * If the expression is more complex, None is returned.
   * @return
   */
  def getConfIfSimpleAndExpr(): Option[(Set[SingleFeatureExpr], Set[SingleFeatureExpr])]

  def getConfIfSimpleOrExpr(): Option[(Set[SingleFeatureExpr], Set[SingleFeatureExpr])]

  final def orNot(that: FeatureExpr) = this or (that.not)

  final def andNot(that: FeatureExpr) = this and (that.not)

  def mex(that: FeatureExpr): FeatureExpr = (this and that).not

  final def isContradiction(): Boolean = isContradiction(null)

  final def isTautology(): Boolean = isTautology(null)

  final def isSatisfiable(): Boolean = isSatisfiable(null)

  /**
   * FM -> X is tautology if FM.implies(X).isTautology or
   * !FM.and.(x.not).isSatisfiable
   *
   * not final for optimization purposes
   **/
  def isTautology(fm: FeatureModel): Boolean = !this.not.isSatisfiable(fm)

  def isContradiction(fm: FeatureModel): Boolean = !isSatisfiable(fm)


  /**
   * uses a SAT solver to determine whether two expressions are
   * equivalent.
   *
   * for performance reasons, it checks pointer
   * equivalence first, but won't use the recursive equals on aexpr
   * (there should only be few cases when equals is more
   * accurate than eq, which are not worth the performance
   * overhead)
   */
  def equivalentTo(that: FeatureExpr): Boolean = (this eq that) || (this equiv that).isTautology();

  def equivalentTo(that: FeatureExpr, fm: FeatureModel): Boolean = (this eq that) || (this equiv that).isTautology(fm);

  protected def indent(level: Int): String = "\t" * level

  final lazy val size: Int = calcSize

  /**
   * Converts this formula to a textual expression.
   */
  override def toString: String = toTextExpr


  /**
   * Prints the textual representation of this formula on a Writer. The result shall be equivalent to
   * p.print(toTextExpr), but it should avoid consuming so much temporary space.
   * @param p the output Writer
   */
  def print(p: Writer) = p.write(toTextExpr)

  def debug_print(indent: Int): String = toTextExpr


  /**
   * evaluate the expression for a given feature selection
   * (all features not provided are assumed deselected)
   *
   * features provided as a list of names (how they would be created
   * with createDefinedExternal)
   *
   * evaluates to true or false
   */
  def evaluate(selectedFeatures: Set[String]): Boolean

}