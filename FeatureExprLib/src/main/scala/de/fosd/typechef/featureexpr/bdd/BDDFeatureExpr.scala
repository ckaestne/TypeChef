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


/**
 * Propositional (or boolean) feature expressions., based on a BDD library
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


/**
 * Central builder class, responsible for simplification of expressions during creation
 * and for extensive caching.
 */
private[bdd] object FExprBuilder {


    val bddCacheSize = 100000
    var bddValNum = 4194304
    var bddVarNum = 100
    var maxFeatureId = 0
    //start with one, so we can distinguish -x and x for sat solving and tostring
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

