package de.fosd.typechef.featureexpr.bdd

import java.io.Writer
import net.sf.javabdd._
import collection.mutable.{HashMap, WeakHashMap, Map}
import de.fosd.typechef.featureexpr._


object FeatureExprHelper {
    private var freshFeatureNameCounter = 0
    def calcFreshFeatureName(): String = {
        freshFeatureNameCounter = freshFeatureNameCounter + 1;
        "__fresh" + freshFeatureNameCounter;
    }
}


//trait FeatureExpr {
//    /**
//     * x.isSatisfiable(fm) is short for x.and(fm).isSatisfiable
//     * but is faster because FM is cached
//     */
//    def isSatisfiable(fm: FeatureModel): Boolean
//    protected def calcSize: Int
//    def toTextExpr: String //or other ToString variations for debugging etc
//    protected def collectDistinctFeatures: Set[String]
//
//    def or(that: FeatureExpr): FeatureExpr = FExprBuilder.or(this, that)
//    def and(that: FeatureExpr): FeatureExpr = FExprBuilder.and(this, that)
//    def not(): FeatureExpr = FExprBuilder.not(this)
//    def implies(that: FeatureExpr) = FExprBuilder.imp(this, that)
//    def xor(that: FeatureExpr) = FExprBuilder.xor(this, that)
//    def equiv(that: FeatureExpr) = FExprBuilder.biimp(this, that)

//
//    //equals, hashcode
//
//
//
//
//
//    def unary_! = not
//    def &(that: FeatureExpr) = and(that)
//    def |(that: FeatureExpr) = or(that)
//
//    def orNot(that: FeatureExpr) = this or (that.not)
//    def andNot(that: FeatureExpr) = this and (that.not)
//    def mex(that: FeatureExpr): FeatureExpr = (this and that).not
//
//    def isContradiction(): Boolean = isContradiction(NoFeatureModel)
//    def isTautology(): Boolean = isTautology(NoFeatureModel)
//    def isDead(): Boolean = isContradiction(NoFeatureModel)
//    def isBase(): Boolean = isTautology(NoFeatureModel)
//    def isSatisfiable(): Boolean = isSatisfiable(NoFeatureModel)
//    /**
//     * FM -> X is tautology if FM.implies(X).isTautology or
//     * !FM.and.(x.not).isSatisfiable
//     *
//     **/
//    def isTautology(fm: FeatureModel): Boolean = !this.not.isSatisfiable(fm)
//    def isContradiction(fm: FeatureModel): Boolean = !isSatisfiable(fm)
//
//
//    /**
//     * uses a SAT solver to determine whether two expressions are
//     * equivalent.
//     *
//     * for performance reasons, it checks pointer
//     * equivalence first, but won't use the recursive equals on aexpr
//     * (there should only be few cases when equals is more
//     * accurate than eq, which are not worth the performance
//     * overhead)
//     */
//    def equivalentTo(that: FeatureExpr): Boolean = (this eq that) || (this equiv that).isTautology();
//    def equivalentTo(that: FeatureExpr, fm: FeatureModel): Boolean = (this eq that) || (this equiv that).isTautology(fm);
//
//    protected def indent(level: Int): String = "\t" * level
//
//    final lazy val size: Int = calcSize
//
//    /**
//     * heuristic to determine whether a feature expression is small
//     * (may be used to decide whether to inline it or not)
//     *
//     * use with care
//     */
//    def isSmall(): Boolean = size <= 10
//
//    //    /**
//    //     * map function that applies to all leafs in the feature expression (i.e. all DefinedExpr nodes)
//    //     */
//    //    def mapDefinedExpr(f: DefinedExpr => FeatureExpr, cache: Map[FeatureExpr, FeatureExpr]): FeatureExpr
//
//    /**
//     * Converts this formula to a textual expression.
//     */
//    override def toString: String = toTextExpr
//
//
//    /**
//     * Prints the textual representation of this formula on a Writer. The result shall be equivalent to
//     * p.print(toTextExpr), but it should avoid consuming so much temporary space.
//     * @param p the output Writer
//     */
//    def print(p: Writer) = p.write(toTextExpr)
//    def debug_print(indent: Int): String = toTextExpr
//
//
//    /**
//     * simple translation into a FeatureExprValue if needed for some reason
//     * (creates IF(expr, 1, 0))
//     */
//    def toFeatureExprValue: FeatureExprValue =
//        FExprBuilder.createIf(this, FExprBuilder.createValue(1l), FExprBuilder.createValue(0l))
//
//}


/**
 * Propositional (or boolean) feature expressions.
 *
 * Feature expressions are compared on object identity (comparing them for equivalence is
 * an additional but expensive operation). Connectives such as "and", "or" and "not"
 * memoize results, so that the operation yields identical results on identical parameters.
 * Classes And, Or and Not are made package-private, and their constructors wrapped
 * through companion objects, to prevent the construction of formulas in any other way.
 *
 * However, this is not yet enough to guarantee the 'maximal sharing' property, because the
 * and/or operators are also associative, but the memoization cannot be associative.
 * Papers on hash-consing explain that one needs to perform a further normalization step.
 *
 * More in general, one can almost prove a theorem called the weak-canonicalization guarantee:
 *
 * If at a given time during program execution, two formula objects represent structurally
 * equal formulas, i.e. which are deeply equal modulo the order of operands of "and" and "or",
 * then they are represented by the same object.
 *
 * XXX: HOWEVER, that the associative property does not hold with pointer equality:
 * (a and b) and c ne a and (b and c). Hopefully this is fixable through different caching.
 *
 * Note that this is not related to formula equivalence, rather to pointer equality.
 * This does not hold for formulas existing at different moments, because caches
 * are implemented using weak references, so if a formula disappears from the heap
 * it is recreated. However, this is not observable for the code.
 *
 * The weak canonicalization property, if true, should allows also ensuring the strong-canonicalization guarantee:
 * If at a given time during program execution, two formula objects a and b
 * represent equivalent formulas, then a.toCNF eq b.toCNF (where eq denotes pointer equality).
 *
 * CNF canonicalization, by construction, ensures that a.toCNF and b.toCNF are structurally equal.
 * The weak canonicalization property would also ensure that they are the same object.
 *
 * It would be interesting to see what happens for toEquiCNF.
 */
class BDDFeatureExpr(private[featureexpr] val bdd: BDD) extends FeatureExpr {

    import CastHelper._

    def or(that: FeatureExpr): FeatureExpr = FExprBuilder.or(this, asBDDFeatureExpr(that))
    def and(that: FeatureExpr): FeatureExpr = FExprBuilder.and(this, asBDDFeatureExpr(that))
    def not(): FeatureExpr = FExprBuilder.not(this)

    override def implies(that: FeatureExpr) = FExprBuilder.imp(this, asBDDFeatureExpr(that))
    override def xor(that: FeatureExpr) = FExprBuilder.xor(this, asBDDFeatureExpr(that))

    // According to advanced textbooks, this representation is not always efficient:
    // not (a equiv b) generates 4 clauses, of which 2 are tautologies.
    // In positions of negative polarity (i.e. contravariant?), a equiv b is best transformed to
    // (a and b) or (!a and !b). However, currently it seems that we never construct not (a equiv b).
    // Be careful if that changes, though.
    override def equiv(that: FeatureExpr) = FExprBuilder.biimp(this, asBDDFeatureExpr(that))

    /**
     * x.isSatisfiable(fm) is short for x.and(fm).isSatisfiable
     * but is faster because FM is cached
     */
    def isSatisfiable(f: FeatureModel): Boolean = {
        val fm = asBDDFeatureModel(f)

        if (bdd.isOne) true //assuming a valid feature model
        else if (bdd.isZero) false
        else if (fm == BDDNoFeatureModel || fm == null) bdd.satOne() != FExprBuilder.FALSE
        //combination with a small FeatureExpr feature model
        else if (fm.clauses.isEmpty) (bdd and fm.extraConstraints.bdd and fm.assumptions.bdd).satOne() != FExprBuilder.FALSE
        //combination with SAT
        else cacheIsSatisfiable.getOrElseUpdate(fm,
            SatSolver.isSatisfiable(fm, toDnfClauses(toScalaAllSat((bdd and fm.extraConstraints.bdd).not().allsat())), FExprBuilder.lookupFeatureName)
        )
    }

    /**
     * Check structural equality, assuming that all component nodes have already been canonicalized.
     * The default implementation checks for pointer equality.
     */
    private[featureexpr] def equal1Level(that: FeatureExpr) = this eq that

    final override def equals(that: Any) = that match {
        case x: BDDFeatureExpr => bdd.equals(x.bdd)
        case _ => super.equals(that)
    }
    override def hashCode = bdd.hashCode


    protected def calcSize: Int = bddAllSat.foldLeft(0)(_ + _.filter(_ >= 0).size)

    /**
     * heuristic to determine whether a feature expression is small
     * (may be used to decide whether to inline it or not)
     *
     * use with care
     */
    def isSmall(): Boolean = size <= 10

    //    /**
    //     * map function that applies to all leafs in the feature expression (i.e. all DefinedExpr nodes)
    //     */
    //    def mapDefinedExpr(f: DefinedExpr => FeatureExpr, cache: Map[FeatureExpr, FeatureExpr]): FeatureExpr

    /**
     * Converts this formula to a textual expression.
     */
    def toTextExpr: String =
        printbdd(bdd, "1", "0", " && ", " || ", i => "definedEx(" + FExprBuilder.lookupFeatureName(i) + ")")
    override def toString: String =
        printbdd(bdd, "True", "False", " & ", " | ", FExprBuilder.lookupFeatureName(_))

    private def printbdd(bdd: BDD, one: String, zero: String, and: String, or: String, toName: (Int) => String): String =
        if (bdd.isOne()) one
        else if (bdd.isZero()) zero
        else {
            def clause(d: Array[Byte]): String = d.zip(0 to (d.length - 1)).filter(_._1 >= 0).map(
                x => (if (x._1 == 0) "!" else "") + toName(x._2)
            ).mkString(and)

            return bddAllSat.map(clause(_)).mkString(or)
        }

    private def bddAllSat: Iterator[Array[Byte]] = toScalaAllSat(bdd.allsat())

    private def toScalaAllSat(allsat: java.util.List[_]): Iterator[Array[Byte]] =
        scala.collection.JavaConversions.asScalaIterator(allsat.asInstanceOf[java.util.List[Array[Byte]]].iterator())

    /**
     * input allsat format
     *
     * output clausel format with sets of variable ids (negative means negated)
     */
    private def toDnfClauses(allsat: Iterator[Array[Byte]]): Iterator[Seq[Int]] = {
        def clause(d: Array[Byte]): Seq[Int] = d.zip(0 to (d.length - 1)).filter(_._1 >= 0).map(
            x => (if (x._1 == 0) -1 else 1) * x._2
        )
        allsat.map(clause(_))
    }


    private val cacheIsSatisfiable: WeakHashMap[FeatureModel, Boolean] = WeakHashMap()


    /**
     * helper function for statistics and such that determines which
     * features are involved in this feature expression
     */
    private def collectDistinctFeatureIds: collection.immutable.Set[Int] =
        bddAllSat.flatMap(clause => clause.zip(0 to (clause.length - 1)).filter(_._1 >= 0).map(_._2)).toSet

    def collectDistinctFeatures: Set[String] =
        collectDistinctFeatureIds.map(FExprBuilder lookupFeatureName _)


    /**
     * counts the number of features in this expression for statistic
     * purposes
     */
    def countDistinctFeatures: Int = collectDistinctFeatureIds.size
}


//// XXX: this should be recognized by the caller and lead to clean termination instead of a stack trace. At least,
//// however, this is only a concern for erroneous input anyway (but isn't it our point to detect it?)
//case class ErrorFeature(msg: String) extends BDDFeatureExpr(FExprBuilder.FALSE) {
//    private def error: Nothing = throw new FeatureArithmeticException(msg)
//    override def toTextExpr = error
//    //    override def mapDefinedExpr(f: DefinedExpr => FeatureExpr, cache: Map[FeatureExpr, FeatureExpr]) = error
//    override def debug_print(x: Int) = error
//}


/**
 * Central builder class, responsible for simplification of expressions during creation
 * and for extensive caching.
 */
private[bdd] object FExprBuilder {


    val bddCacheSize = 100000
    var bddValNum = 4194304
    var bddVarNum = 100
    var maxFeatureId = 0 //start with one, so we can distinguish -x and x for sat solving and tostring
    var bddFactory: BDDFactory = null
    try {
        bddFactory = BDDFactory.init(bddValNum, bddCacheSize)
    } catch {
        case e: OutOfMemoryError =>
            println("running with low memory. consider increasing heap size.")
            var bddValNum = 524288
            bddFactory = BDDFactory.init(bddValNum, bddCacheSize)
    }
    bddFactory.setIncreaseFactor(.5) //50% increase each time
    bddFactory.setMaxIncrease(0) //no upper limit on increase size
    bddFactory.setVarNum(bddVarNum)

    val TRUE: BDD = bddFactory.one()
    val FALSE: BDD = bddFactory.zero()


    private val featureIds: Map[String, Int] = Map()
    private val featureNames: Map[Int, String] = Map()
    private val featureBDDs: Map[Int, BDD] = Map()


    /*
    * It seems that with the four patterns to optimize and/or, it's more difficult to
    * produce a formula with duplicated literals - you need two levels of nesting for that to happen.
    * Duplications is removed in variations of:
    * a && (b || !a), where a and b can be any formula, and && and || can be replaced by any connective.
    * Examples where duplication appears:
    * a && !(a || b) - fixable by implementing DeMorgan laws in Not.
    * (a || b) && (b || c)
    * a && (b || (c && a)) - note the two levels of nesting. The inner a could
    * be removed in this particular case using shortcircuiting, but there does
    * not seem to be an easy way of implementing this.
    *
    *
    * Please note that due to duality, the code below is essentially
    * duplicated (andOr() is dual to orAnd(), and() to or(), and so on).
    * Remember that to dualize this code, one must swap or with and, and False with True.
    * Please check that it does not go out of sync.
    */
    // XXX: in various places, we merge sets with an O(N) reduction. It allows looking up the memoized results, but now
    // that we later canonicalize everything, it is not clear whether we should still do it. Such occurrences are marked
    // below with "XXX: O(N) set rebuild". Note however that to make them O(1) one needs to write a O(1) hash calculator
    // for them as well - very simple, just some work to do. See specialized constructors in AndOrUnExtractor.


    def and(a: BDDFeatureExpr, b: BDDFeatureExpr): BDDFeatureExpr = new BDDFeatureExpr(a.bdd and b.bdd)
    def or(a: BDDFeatureExpr, b: BDDFeatureExpr): BDDFeatureExpr = new BDDFeatureExpr(a.bdd or b.bdd)
    def imp(a: BDDFeatureExpr, b: BDDFeatureExpr): BDDFeatureExpr = new BDDFeatureExpr(a.bdd imp b.bdd)
    def biimp(a: BDDFeatureExpr, b: BDDFeatureExpr): BDDFeatureExpr = new BDDFeatureExpr(a.bdd biimp b.bdd)
    def xor(a: BDDFeatureExpr, b: BDDFeatureExpr): BDDFeatureExpr = new BDDFeatureExpr(a.bdd xor b.bdd)

    def not(a: BDDFeatureExpr): BDDFeatureExpr = new BDDFeatureExpr(a.bdd.not())

    def definedExternal(name: String): BDDFeatureExpr = {
        val id: Int = featureIds.get(name) match {
            case Some(id) => id
            case _ =>
                maxFeatureId = maxFeatureId + 1
                if (maxFeatureId >= bddVarNum) {
                    bddVarNum = bddVarNum * 2
                    bddFactory.setVarNum(bddVarNum)
                }
                featureIds.put(name, maxFeatureId)
                featureNames.put(maxFeatureId, name)
                featureBDDs.put(maxFeatureId, bddFactory.ithVar(maxFeatureId))
                maxFeatureId
        }
        new BDDFeatureExpr(featureBDDs(id))
    }

    def lookupFeatureName(id: Int): String = featureNames(id)

    //create a macro definition (which expands to the current entry in the macro table; the current entry is stored in a closure-like way).
    //a form of caching provided by MacroTable, which we need to repeat here to create the same FeatureExpr object
    def definedMacro(name: String, macroTable: FeatureProvider): BDDFeatureExpr = {
        val f = macroTable.getMacroCondition(name)
        CastHelper.asBDDFeatureExpr(f)
    }
}


////////////////////////////
// propositional formulas //
////////////////////////////
/**
 * True and False. They are represented as special cases of And and Or
 * with no clauses, as it is common practice, for instance in the resolution algorithm.
 *
 * True is the zero element for And, while False is the zero element for Or.
 * Therefore, True can be represented as an empty conjunction, while False
 * by an empty disjunction.
 *
 * One can imagine to build a disjunction incrementally, by having an empty one
 * be considered as false, so that each added operand can make the resulting
 * formula true. Dually, an empty conjunction is true, and and'ing clauses can
 * make it false.
 *
 * This avoids introducing two new leaf nodes to handle (avoiding some bugs causing NoLiteralException).
 *
 * Moreover, since those are valid formulas, they would otherwise be valid but
 * non-canonical representations, and avoiding the very existence of such things
 * simplifies ensuring that our canonicalization algorithms actually work.
 *
 * The use of only canonical representations for True and False is ensured thanks to the
 * apply methods of the And and Or companion objects, which convert any empty set of
 * clauses into the canonical True or False object.
 */
object True extends BDDFeatureExpr(FExprBuilder.TRUE) with DefaultPrint {
    override def toString = "True"
    override def toTextExpr = "1"
    override def debug_print(ind: Int) = indent(ind) + toTextExpr + "\n"
    override def isSatisfiable(fm: FeatureModel) = true
}

object False extends BDDFeatureExpr(FExprBuilder.FALSE) with DefaultPrint {
    override def toString = "False"
    override def toTextExpr = "0"
    override def debug_print(ind: Int) = indent(ind) + toTextExpr + "\n"
    override def isSatisfiable(fm: FeatureModel) = false
}

object CastHelper {
    def asBDDFeatureExpr(fexpr: FeatureExpr): BDDFeatureExpr = if (fexpr == null) null
    else {
        assert(fexpr.isInstanceOf[BDDFeatureExpr], "Expected BDDFeatureExpr but found " + fexpr.getClass.getCanonicalName + "; do not mix implementations of FeatureExprLib.") //FMCAST
        fexpr.asInstanceOf[BDDFeatureExpr]
    }
    def asBDDFeatureModel(fm: FeatureModel): BDDFeatureModel =
        if (fm == null) null
        else {
            assert(fm.isInstanceOf[BDDFeatureModel], "Expected BDDFeatureModel but found " + fm.getClass.getCanonicalName + "; do not mix implementations of FeatureExprLib.") //FMCAST
            fm.asInstanceOf[BDDFeatureModel]
        }
}

