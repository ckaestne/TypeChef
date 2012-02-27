package de.fosd.typechef.featureexpr

import collection.mutable.Map
import collection.mutable.WeakHashMap
import collection.mutable.HashMap
import collection.mutable.ArrayBuffer
import scala.ref.WeakReference
import java.io.Writer
import net.sf.javabdd._;

/**
 * External interface for construction of non-boolean feature expressions
 * (mostly delegated to FExprBuilder)
 *
 * Also provides access to the primitives base and dead (for true and false)
 * and allows to create DefinedExternal nodes
 */
object FeatureExpr extends FeatureExprValueOps {

    def createComplement(expr: FeatureExprValue): FeatureExprValue = FExprBuilder.applyUnaryOperation(expr)(~_)
    def createNeg(expr: FeatureExprValue) = FExprBuilder.applyUnaryOperation(expr)(-_)
    def createBitAnd(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right)(_ & _)
    def createBitOr(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right)(_ | _)
    def createDivision(left: FeatureExprValue, right: FeatureExprValue): FeatureExprValue = FExprBuilder.applyBinaryOperation(left, right)(
        (l, r) => if (r == 0) ErrorValue[Long]("division by zero") else FExprBuilder.createValue(l / r))
    def createModulo(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right)(_ % _)
    def createEquals(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.evalRelation(left, right)(_ == _)
    def createNotEquals(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.evalRelation(left, right)(_ != _)
    def createLessThan(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.evalRelation(left, right)(_ < _)
    def createLessThanEquals(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.evalRelation(left, right)(_ <= _)
    def createGreaterThan(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.evalRelation(left, right)(_ > _)
    def createGreaterThanEquals(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.evalRelation(left, right)(_ >= _)
    def createMinus(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right)(_ - _)
    def createMult(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right)(_ * _)
    def createPlus(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right)(_ + _)
    def createPwr(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right)(_ ^ _)
    def createShiftLeft(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right)(_ << _)
    def createShiftRight(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right)(_ >> _)

    def createInteger(value: Long): FeatureExprValue = FExprBuilder.createValue(value)
    def createCharacter(value: Char): FeatureExprValue = FExprBuilder.createValue(value)
    def createValue[T](v: T): FeatureExprTree[T] = FExprBuilder.createValue(v)


    def createDefinedExternal(name: String): FeatureExpr = FExprBuilder.definedExternal(name)
    def createDefinedMacro(name: String, macroTable: FeatureProvider): FeatureExpr = FExprBuilder.definedMacro(name, macroTable)


    //helper
    def createIf(condition: FeatureExpr, thenBranch: FeatureExpr, elseBranch: FeatureExpr): FeatureExpr = FExprBuilder.createBooleanIf(condition, thenBranch, elseBranch)
    def createIf[T](condition: FeatureExpr, thenBranch: FeatureExprTree[T], elseBranch: FeatureExprTree[T]): FeatureExprTree[T] = FExprBuilder.createIf(condition, thenBranch, elseBranch)

    def createImplies(left: FeatureExpr, right: FeatureExpr) = left implies right
    def createEquiv(left: FeatureExpr, right: FeatureExpr) = left equiv right

    val base: FeatureExpr = de.fosd.typechef.featureexpr.True
    val dead: FeatureExpr = de.fosd.typechef.featureexpr.False

    def True = base
    def False = dead


}

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
class FeatureExpr(private[featureexpr] val bdd: BDD) {

    def or(that: FeatureExpr): FeatureExpr = FExprBuilder.or(this, that)
    def and(that: FeatureExpr): FeatureExpr = FExprBuilder.and(this, that)
    def not(): FeatureExpr = FExprBuilder.not(this)

    def unary_! = not
    def &(that: FeatureExpr) = and(that)
    def |(that: FeatureExpr) = or(that)

    def orNot(that: FeatureExpr) = this or (that.not)
    def andNot(that: FeatureExpr) = this and (that.not)
    def implies(that: FeatureExpr) = FExprBuilder.imp(this, that)
    def mex(that: FeatureExpr): FeatureExpr = (this and that).not
    def xor(that: FeatureExpr) = FExprBuilder.xor(this, that)

    // According to advanced textbooks, this representation is not always efficient:
    // not (a equiv b) generates 4 clauses, of which 2 are tautologies.
    // In positions of negative polarity (i.e. contravariant?), a equiv b is best transformed to
    // (a and b) or (!a and !b). However, currently it seems that we never construct not (a equiv b).
    // Be careful if that changes, though.
    def equiv(that: FeatureExpr) = FExprBuilder.biimp(this, that)

    def isContradiction(): Boolean = isContradiction(NoFeatureModel)
    def isTautology(): Boolean = isTautology(NoFeatureModel)
    def isDead(): Boolean = isContradiction(NoFeatureModel)
    def isBase(): Boolean = isTautology(NoFeatureModel)
    def isSatisfiable(): Boolean = isSatisfiable(NoFeatureModel)
    /**
     * FM -> X is tautology if FM.implies(X).isTautology or
     * FM.and.(x.not).isSatisfiable
     *
     **/
    def isTautology(fm: FeatureModel): Boolean = !this.not.isSatisfiable(fm)
    def isContradiction(fm: FeatureModel): Boolean = !isSatisfiable(fm)
    /**
     * x.isSatisfiable(fm) is short for x.and(fm).isSatisfiable
     * but is faster because FM is cached
     */
    def isSatisfiable(fm: FeatureModel): Boolean =
        if (bdd.isOne) true //assuming a valid feature model
        else if (bdd.isZero) false
        else if (fm == NoFeatureModel || fm == null) bdd.satOne() != FExprBuilder.FALSE
        //combination with a small FeatureExpr feature model
        else if (fm.clauses.isEmpty) (bdd and fm.extraConstraints.bdd and fm.assumptions.bdd).satOne() != FExprBuilder.FALSE
        //combination with SAT
        else cacheIsSatisfiable.getOrElseUpdate(fm,
            SatSolver.isSatisfiable(fm, toDnfClauses(toScalaAllSat((bdd and fm.extraConstraints.bdd).not().allsat())), FExprBuilder.lookupFeatureName)
        )

    /**
     * Check structural equality, assuming that all component nodes have already been canonicalized.
     * The default implementation checks for pointer equality.
     */
    private[featureexpr] def equal1Level(that: FeatureExpr) = this eq that

    final override def equals(that: Any) = that match {
        case x: FeatureExpr => bdd.equals(x.bdd)
        case _ => super.equals(that)
    }
    override def hashCode = bdd.hashCode

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

    /**
     * Prints the textual representation of this formula on a Writer. The result shall be equivalent to
     * p.print(toTextExpr), but it should avoid consuming so much temporary space.
     * @param p the output Writer
     */
    def print(p: Writer) = p.write(toTextExpr)
    def debug_print(indent: Int): String = toTextExpr

    private var cache_cnf: FeatureExpr = null
    private var cache_cnfEquiSat: FeatureExpr = null

    private val cacheIsSatisfiable: WeakHashMap[FeatureModel, Boolean] = WeakHashMap()
    //only access these caches from FExprBuilder
    private[featureexpr] val andCache: WeakHashMap[FeatureExpr, WeakReference[FeatureExpr]] = new WeakHashMap()
    private[featureexpr] val orCache: WeakHashMap[FeatureExpr, WeakReference[FeatureExpr]] = new WeakHashMap()
    private[featureexpr] var notCache: Option[NotReference[FeatureExpr]] = None

    /**
     * Retrieves the memoized representation of this.not, if any exists; it is useful only to look for duplicated
     * elements in a clause. Note that two non-trivial formula might be
     * opposite, still using retrieveMemoizedNot for the test might
     * fail; e.g., a and b and !a or !b might be built independently.
     * XXX: Tillmann suggested transforming this into a boolean predicate,
     * rather than using null as a meaningful value.
     */
    private[featureexpr] def retrieveMemoizedNot = notCache match {
        case Some(NotRef(x)) => x
        case _ => null
    }

    /**
     * simple translation into a FeatureExprValue if needed for some reason
     * (creates IF(expr, 1, 0))
     */
    def toFeatureExprValue: FeatureExprValue =
        FExprBuilder.createIf(this, FExprBuilder.createValue(1l), FExprBuilder.createValue(0l))


    /**
     * helper function for statistics and such that determines which
     * features are involved in this feature expression
     */
    private def collectDistinctFeatureIds: Set[Int] =
        bddAllSat.flatMap(clause => clause.zip(0 to (clause.length - 1)).filter(_._1 >= 0).map(_._2)).toSet

    def collectDistinctFeatures: Set[String] =
        collectDistinctFeatureIds.map(FExprBuilder lookupFeatureName _)


    /**
     * counts the number of features in this expression for statistic
     * purposes
     */
    def countDistinctFeatures: Int = collectDistinctFeatureIds.size
}

class FeatureException(msg: String) extends RuntimeException(msg)

class FeatureArithmeticException(msg: String) extends FeatureException(msg)

// XXX: this should be recognized by the caller and lead to clean termination instead of a stack trace. At least,
// however, this is only a concern for erroneous input anyway (but isn't it our point to detect it?)
case class ErrorFeature(msg: String) extends FeatureExpr(FExprBuilder.FALSE) {
    private def error: Nothing = throw new FeatureArithmeticException(msg)
    override def toTextExpr = error
    //    override def mapDefinedExpr(f: DefinedExpr => FeatureExpr, cache: Map[FeatureExpr, FeatureExpr]) = error
    override def debug_print(x: Int) = error
}


/**
 * Central builder class, responsible for simplification of expressions during creation
 * and for extensive caching.
 */
private[featureexpr] object FExprBuilder {


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
    private val ifCache: HashMap[(FeatureExpr, FeatureExprTree[_], FeatureExprTree[_]), WeakReference[FeatureExprTree[_]]] = new HashMap()
    private val valCache: Map[Any, WeakReference[Value[_]]] = Map()

    private def cacheGetOrElseUpdate[A, B <: AnyRef](map: Map[A, WeakReference[B]], key: A, op: => B): B = {
        def update() = {
            val d = op;
            map(key) = new WeakReference[B](d);
            d
        }
        map.get(key) match {
            case Some(WeakRef(value)) => value
            case _ => update()
        }
    }

    private def getFromCache(cache: WeakHashMap[FeatureExpr, WeakReference[FeatureExpr]], key: FeatureExpr): Option[FeatureExpr] = {
        cache.get(key) match {
            case Some(WeakRef(f)) => Some(f)
            case _ => None
        }
    }
    private def binOpCacheGetOrElseUpdate(a: FeatureExpr,
                                          b: FeatureExpr,
                                          getCache: FeatureExpr => WeakHashMap[FeatureExpr, WeakReference[FeatureExpr]],
                                          featThunk: => FeatureExpr): FeatureExpr = {
        var result = getFromCache(getCache(a), b)
        if (result == None)
            result = getFromCache(getCache(b), a)
        if (result == None) {
            val feat = featThunk
            result = Some(feat)
            val weakRef = new WeakReference(feat)
            getCache(a).update(b, weakRef)
            //XXX it is enough to update one object, because we are searching in both of them anyway
            //            getCache(b).update(a, weakRef)
        }
        result.get
    }


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


    def and(a: FeatureExpr, b: FeatureExpr): FeatureExpr = new FeatureExpr(a.bdd and b.bdd)
    def or(a: FeatureExpr, b: FeatureExpr): FeatureExpr = new FeatureExpr(a.bdd or b.bdd)
    def imp(a: FeatureExpr, b: FeatureExpr): FeatureExpr = new FeatureExpr(a.bdd imp b.bdd)
    def biimp(a: FeatureExpr, b: FeatureExpr): FeatureExpr = new FeatureExpr(a.bdd biimp b.bdd)
    def xor(a: FeatureExpr, b: FeatureExpr): FeatureExpr = new FeatureExpr(a.bdd xor b.bdd)

    def not(a: FeatureExpr): FeatureExpr = new FeatureExpr(a.bdd.not())

    def definedExternal(name: String): FeatureExpr = {
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
        new FeatureExpr(featureBDDs(id))
    }

    def lookupFeatureName(id: Int): String = featureNames(id)

    //create a macro definition (which expands to the current entry in the macro table; the current entry is stored in a closure-like way).
    //a form of caching provided by MacroTable, which we need to repeat here to create the same FeatureExpr object
    def definedMacro(name: String, macroTable: FeatureProvider): FeatureExpr = {
        macroTable.getMacroCondition(name)
    }


    private def propagateError[T](left: FeatureExprTree[T], right: FeatureExprTree[T]): Option[ErrorValue[T]] = {
        (left, right) match {
            case (msg1: ErrorValue[_], msg2: ErrorValue[_]) => Some(ErrorValue(msg1.msg + ";" + msg2.msg))
            case (msg: ErrorValue[_], _) => Some(ErrorValue(msg.msg))
            case (_, msg: ErrorValue[_]) => Some(ErrorValue(msg.msg))
            case _ => None
        }
    }

    def evalRelation[T](smaller: FeatureExprTree[T], larger: FeatureExprTree[T])(relation: (T, T) => Boolean): FeatureExpr = {
        propagateError(smaller, larger) match {
            case Some(ErrorValue(msg)) => return ErrorFeature(msg)
            case _ =>
                (smaller, larger) match {
                    case (a: Value[_], b: Value[_]) => if (relation(a.value.asInstanceOf[T], b.value.asInstanceOf[T])) True else False
                    case (i1: If[_], i2: If[_]) =>
                        createBooleanIf(i1.expr,
                            createBooleanIf(i2.expr, evalRelation(i1.thenBr, i2.thenBr)(relation), evalRelation(i1.thenBr, i2.elseBr)(relation)),
                            createBooleanIf(i2.expr, evalRelation(i1.elseBr, i2.thenBr)(relation), evalRelation(i1.elseBr, i2.elseBr)(relation)))
                    case (i: If[_], x) => createBooleanIf(i.expr, evalRelation(i.thenBr, x)(relation), evalRelation(i.elseBr, x)(relation))
                    case (x, i: If[_]) => createBooleanIf(i.expr, evalRelation(x, i.thenBr)(relation), evalRelation(x, i.elseBr)(relation))
                    case _ => throw new Exception("evalRelation: unexpected " +(smaller, larger))
                }
        }
    }

    def applyBinaryOperation[T, U <% FeatureExprTree[T]](left: FeatureExprTree[T], right: FeatureExprTree[T])(operation: (T, T) => U): FeatureExprTree[T] = {
        propagateError(left, right) match {
            case Some(err) => return err
            case _ =>
                (left, right) match {
                    case (a: Value[_], b: Value[_]) => operation(a.value.asInstanceOf[T], b.value.asInstanceOf[T])
                    case (i1: If[_], i2: If[_]) =>
                        createIf[T](i1.expr,
                            createIf[T](i2.expr, applyBinaryOperation(i1.thenBr.asInstanceOf[FeatureExprTree[T]], i2.thenBr.asInstanceOf[FeatureExprTree[T]])(operation), applyBinaryOperation(i1.thenBr.asInstanceOf[FeatureExprTree[T]], i2.elseBr.asInstanceOf[FeatureExprTree[T]])(operation)),
                            createIf[T](i2.expr, applyBinaryOperation(i1.elseBr.asInstanceOf[FeatureExprTree[T]], i2.thenBr.asInstanceOf[FeatureExprTree[T]])(operation), applyBinaryOperation(i1.elseBr.asInstanceOf[FeatureExprTree[T]], i2.elseBr.asInstanceOf[FeatureExprTree[T]])(operation)))
                    case (i: If[_], x) => createIf(i.expr, applyBinaryOperation(i.thenBr.asInstanceOf[FeatureExprTree[T]], x)(operation), applyBinaryOperation(i.elseBr.asInstanceOf[FeatureExprTree[T]], x)(operation))
                    case (x, i: If[_]) => createIf(i.expr, applyBinaryOperation(x, i.thenBr.asInstanceOf[FeatureExprTree[T]])(operation), applyBinaryOperation(x, i.elseBr.asInstanceOf[FeatureExprTree[T]])(operation))
                    case _ => throw new Exception("applyBinaryOperation: unexpected " +(left, right))
                }
        }
    }

    def applyUnaryOperation[T](expr: FeatureExprTree[T])(operation: T => T): FeatureExprTree[T] = expr match {
        case a: Value[_] => createValue(operation(a.value.asInstanceOf[T]))
        case i: If[_] => createIf(i.expr, applyUnaryOperation(i.thenBr.asInstanceOf[FeatureExprTree[T]])(operation), applyUnaryOperation(i.elseBr.asInstanceOf[FeatureExprTree[T]])(operation))
        case _ => throw new Exception("applyUnaryOperation: unexpected " + expr)
    }

    def createIf[T](expr: FeatureExpr, thenBr: FeatureExprTree[T], elseBr: FeatureExprTree[T]): FeatureExprTree[T] = expr match {
        case True => thenBr
        case False => elseBr
        case _ => {
            if (thenBr == elseBr) thenBr
            else cacheGetOrElseUpdate(ifCache, (expr, thenBr, elseBr), new If(expr, thenBr, elseBr)).asInstanceOf[FeatureExprTree[T]]
        }
    }

    def createBooleanIf(expr: FeatureExpr, thenBr: FeatureExpr, elseBr: FeatureExpr): FeatureExpr = (expr and thenBr) or (expr.not and elseBr)

    def createValue[T](v: T): FeatureExprTree[T] = cacheGetOrElseUpdate(valCache, v, new Value[T](v)).asInstanceOf[FeatureExprTree[T]]

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
object True extends FeatureExpr(FExprBuilder.TRUE) with DefaultPrint {
    override def toString = "True"
    override def toTextExpr = "1"
    override def debug_print(ind: Int) = indent(ind) + toTextExpr + "\n"
    override def isSatisfiable(fm: FeatureModel) = true
}

object False extends FeatureExpr(FExprBuilder.FALSE) with DefaultPrint {
    override def toString = "False"
    override def toTextExpr = "0"
    override def debug_print(ind: Int) = indent(ind) + toTextExpr + "\n"
    override def isSatisfiable(fm: FeatureModel) = false
}

trait DefaultPrint extends FeatureExpr {
    override def print(p: Writer) = p.write(toTextExpr)
}
