package de.fosd.typechef.featureexpr

import java.io._


/**
  * A feature expression represents a propositional formula
  * over a set of feature names (strings).
  *
  * Extra functionality is provided to efficiently check
  * satisfiablility of expressions.
  *
  * Feature expressions are never created directly but either
  * through the FeatureExprFactory or through operations on
  * other feature expressions.
  *
  * Feature expressions may internally represented in different
  * ways, for example as BDDs.
  */
trait FeatureExpr extends Serializable {

    // *****************************************************
    // * operations (default implementations may be overwritten for efficiency)
    // *****************************************************

    def or(that: FeatureExpr): FeatureExpr
    def and(that: FeatureExpr): FeatureExpr
    def not(): FeatureExpr
    def implies(that: FeatureExpr) = this.not.or(that)
    def xor(that: FeatureExpr) = (this or that) andNot (this and that)
    //mutual exclusion:
    def mex(that: FeatureExpr): FeatureExpr = (this and that).not
    //biimplication:
    def equiv(that: FeatureExpr) = (this and that) or (this.not and that.not)
    /**
      * unique existential quantification over feature "feature".
      *
      * This has the effect of substituting the feature by true and false respectively and returning the xor of both:
      * this[feature->True] xor this[feature->False]
      *
      * It can be seen as identifying under which condition the feature matters for the result of the formula
      */
    def unique(feature: SingleFeatureExpr): FeatureExpr


    //shorthand operations
    final def orNot(that: FeatureExpr) = this or (that.not)
    final def andNot(that: FeatureExpr) = this and (that.not)
    final def unary_! = not
    final def &(that: FeatureExpr) = and(that)
    final def |(that: FeatureExpr) = or(that)


    //special operations

    /**
      * Informal: Returns all the information in this that is not present in b.
      * this.simplify(b) is equivialent to this if the context b is guaranteed
      * Formal: (b implies (this.simplify(b) equiv this))
      *
      * @param b
      * @return
      */
    def simplify(b: FeatureExpr): FeatureExpr


    // *****************************************************
    // * satisfiability and various shorthands
    // *****************************************************

    /**
      * x.isSatisfiable(fm) is short for x.and(fm).isSatisfiable
      * but is faster because FM is cached
      */
    def isSatisfiable(fm: FeatureModel): Boolean
    /**
      * FM -> X is tautology if FM.implies(X).isTautology or
      * !FM.and.(x.not).isSatisfiable
      *
      * not final for optimization purposes
      **/
    def isTautology(fm: FeatureModel): Boolean = !this.not.isSatisfiable(fm)
    def isContradiction(fm: FeatureModel): Boolean = !isSatisfiable(fm)

    final def isContradiction(): Boolean = isContradiction(null)
    final def isTautology(): Boolean = isTautology(null)
    final def isSatisfiable(): Boolean = isSatisfiable(null)

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


    def getSatisfiableAssignment(featureModel: FeatureModel, interestingFeatures: Set[SingleFeatureExpr], preferDisabledFeatures: Boolean): Option[(List[SingleFeatureExpr], List[SingleFeatureExpr])]

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

    // *****************************************************
    // * various operations for specific purposes
    // *****************************************************

    def collectDistinctFeatures: Set[String]
    def collectDistinctFeatureObjects: Set[SingleFeatureExpr]


    protected def calcSize: Int

    /**
      * Converts this formula to a textual expression.
      */
    override def toString: String = toTextExpr

    //alternative to toString for debugging etc (may be implemented differently)
    def toTextExpr: String
    /**
      * Prints the textual representation of this formula on a Writer. The result shall be equivalent to
      * p.print(toTextExpr), but it should avoid consuming so much temporary space.
      *
      * @param p the output Writer
      */
    def print(p: Writer) = p.write(toTextExpr)
    def debug_print(indent: Int): String = toTextExpr

    final lazy val size: Int = calcSize


    /**
      * If this expr is a simple concatenation of SingleFeatureExpressions (and their negations),
      * then this method returns the expression as a set of singleFeatureExpr that occur as enabled (disabled).
      * If the expression is more complex, None is returned.
      *
      * @return
      */
    def getConfIfSimpleAndExpr(): Option[(Set[SingleFeatureExpr], Set[SingleFeatureExpr])]
    def getConfIfSimpleOrExpr(): Option[(Set[SingleFeatureExpr], Set[SingleFeatureExpr])]


    //this method needs to be copied into all concrete subclasses!
    private def writeReplace(): Object = new FeatureExprSerializationProxy(this.toTextExpr)
}


class FeatureExprSerializationProxy(fexpr: String) extends Serializable {
    private def readResolve(): Object = {
        new FeatureExprParser().parse(fexpr)
    }
}