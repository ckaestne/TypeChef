package de.fosd.typechef.featureexpr.bdd

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.featureexpr.bdd.FExprBuilder._
import net.sf.javabdd._

import scala.annotation.tailrec
import scala.collection.mutable.{Map, WeakHashMap}


object FeatureExprHelper {
    private var freshFeatureNameCounter = 0
    def calcFreshFeatureName(): String = {
        freshFeatureNameCounter = freshFeatureNameCounter + 1;
        "__fresh" + freshFeatureNameCounter;
    }
    /** map for caching of already solved SAT checks
      * Different objects A and B of class BDDFeatureExpr may refer to the same internal BDD and have the same hashcode and have equal-equality.
      * If a result was stored for A, this result will also be returned for B, because A.equals(B).
      * We use BDD here, because in contrast to BDDFeatureExpr BDD is never freed by GC and so we avoid redundant SAT checks.
      */
    val cacheIsSatisfiable: WeakHashMap[(BDD, FeatureModel), Boolean] = WeakHashMap()
}



/**
 * Propositional (or boolean) feature expressions., based on a BDD library
 */
class BDDFeatureExpr(private[featureexpr] val bdd: BDD) extends FeatureExpr {

    import CastHelper._

    def or(that: FeatureExpr): FeatureExpr = {
        if (that == FeatureExprFactory.True) FeatureExprFactory.True
        else FExprBuilder.or(this, asBDDFeatureExpr(that))
    }
    def and(that: FeatureExpr): FeatureExpr = {
        if (that == FeatureExprFactory.True) this
        else if (that == FeatureExprFactory.False) FeatureExprFactory.False
        else FExprBuilder.and(this, asBDDFeatureExpr(that))
    }
    def not(): FeatureExpr = FExprBuilder.not(this)

    def simplify(b: FeatureExpr): FeatureExpr = FExprBuilder.synchronized {new BDDFeatureExpr(this.bdd.simplify(asBDDFeatureExpr(b).bdd))}

    /**
     * frees the space occupied by this bdd in the bdd library.
     * This is done without any safety-measures!
     * If there is any reference to this FeatureExpression left, and its reference to
     * the (cleared) bdd is used, there might be an exception or undefined behavior.
     */
    def freeBDD() {
        this.bdd.free()
    }

    override def implies(that: FeatureExpr) = FExprBuilder.imp(this, asBDDFeatureExpr(that))
    override def xor(that: FeatureExpr) = FExprBuilder.xor(this, asBDDFeatureExpr(that))

    override def unique(feature: SingleFeatureExpr): FeatureExpr = FExprBuilder.unique(this, asSingleBDDFeatureExpr(feature))


    // According to advanced textbooks, this representation is not always efficient:
    // not (a equiv b) generates 4 clauses, of which 2 are tautologies.
    // In positions of negative polarity (i.e. contravariant?), a equiv b is best transformed to
    // (a and b) or (!a and !b). However, currently it seems that we never construct not (a equiv b).
    // Be careful if that changes, though.
    override def equiv(that: FeatureExpr) = FExprBuilder.biimp(this, asBDDFeatureExpr(that))

    def getSatisfiableAssignment(featureModel: FeatureModel, interestingFeatures: Set[SingleFeatureExpr], preferDisabledFeatures: Boolean): Option[(List[SingleFeatureExpr], List[SingleFeatureExpr])] = FExprBuilder.synchronized {
        val fm = asBDDFeatureModel(featureModel)
        // optimization: if the interestingFeatures-Set is empty and this FeatureExpression is TRUE, we will always return empty sets
        // here we assume that the featureModel is satisfiable (which is checked at FM-instantiation)
        if (this.bdd.equals(TRUE) && interestingFeatures.isEmpty) {
            return Some((List(), List())) // is satisfiable, but no interesting features in solution
        }
        val bddDNF = toDnfClauses(toScalaAllSat((bdd and fm.extraConstraints.bdd).not().allsat()))
        // get one satisfying assignment (a list of features set to true, and a list of features set to false)
        val assignment: Option[(List[String], List[String])] = SatSolver.getSatAssignment(fm, bddDNF, FExprBuilder.lookupFeatureName)
        // we will subtract from this set until all interesting features are handled
        // the result will only contain interesting features. Even parts of this expression will be omitted if uninteresting.
        var remainingInterestingFeatures = interestingFeatures
        assignment match {
            case Some((trueFeatures, falseFeatures)) => {
                if (preferDisabledFeatures) {
                    var enabledFeatures: Set[SingleFeatureExpr] = Set()
                    for (f <- trueFeatures) {
                        val elem = remainingInterestingFeatures.find({
                            fex: SingleFeatureExpr => fex.feature.equals(f)
                        })
                        elem match {
                            case Some(fex: SingleFeatureExpr) => {
                                remainingInterestingFeatures -= fex
                                enabledFeatures += fex
                            }
                            case None => {}
                        }
                    }
                    return Some(enabledFeatures.toList, remainingInterestingFeatures.toList)
                } else {
                    var disabledFeatures: Set[SingleFeatureExpr] = Set()
                    for (f <- falseFeatures) {
                        val elem = remainingInterestingFeatures.find({
                            fex: SingleFeatureExpr => fex.feature.equals(f)
                        })
                        elem match {
                            case Some(fex: SingleFeatureExpr) => {
                                remainingInterestingFeatures -= fex
                                disabledFeatures += fex
                            }
                            case None => {}
                        }
                    }
                    return Some(remainingInterestingFeatures.toList, disabledFeatures.toList)
                }
            }
            case None => return None
        }
    }

    /**
     * x.isSatisfiable(fm) is short for x.and(fm).isSatisfiable
     * but is faster because FM is cached
     */
    def isSatisfiable(f: FeatureModel): Boolean = FExprBuilder.synchronized {
        val fm = asBDDFeatureModel(f)

        if (bdd.isOne) true //assuming a valid feature model
        else if (bdd.isZero) false
        else if (fm == BDDNoFeatureModel || fm == null) bdd.satOne() != FExprBuilder.FALSE
        //combination with a small FeatureExpr feature model
        else if (fm.clauses.isEmpty) (bdd and fm.extraConstraints.bdd and fm.assumptions.bdd).satOne() != FExprBuilder.FALSE
        //combination with SAT
        else FeatureExprHelper.cacheIsSatisfiable.getOrElseUpdate((this.bdd, fm),
            SatSolver.isSatisfiable(fm, toDnfClauses(toScalaAllSat((bdd and fm.extraConstraints.bdd).not().allsat())), FExprBuilder.lookupFeatureName)
        )
    }

    /**
     * alternative implementation of issatisfiable that uses a different CNF conversion
     * with more variables but possibly fewer clauses
     *
     * the difference is only relevant when dimacs-feature models are involved
     */
    def isSatisfiable2(f: FeatureModel): Boolean = FExprBuilder.synchronized {
        val fm = asBDDFeatureModel(f)

        if (bdd.isOne) true //assuming a valid feature model
        else if (bdd.isZero) false
        else if (fm == BDDNoFeatureModel || fm == null) bdd.satOne() != FExprBuilder.FALSE
        //combination with a small FeatureExpr feature model
        else if (fm.clauses.isEmpty) (bdd and fm.extraConstraints.bdd and fm.assumptions.bdd).satOne() != FExprBuilder.FALSE
        //combination with SAT
        else FeatureExprHelper.cacheIsSatisfiable.getOrElseUpdate((this.bdd, fm),
            SatSolver.isSatisfiable(fm, toCNFClauses((bdd and fm.extraConstraints.bdd)), FExprBuilder.lookupFeatureName)
        )
    }

    /**
     * Check structural equality, assuming that all component nodes have already been canonicalized.
     * The default implementation checks for pointer equality.
     */
    private[featureexpr] def equal1Level(that: FeatureExpr) = this eq that

    final override def equals(that: Any) = that match {
        case x: BDDFeatureExpr => FExprBuilder.synchronized {bdd.equals(x.bdd)}
        case _ => super.equals(that)
    }
    override def hashCode = FExprBuilder.synchronized {bdd.hashCode}


    protected def calcSize: Int = FExprBuilder.synchronized {bdd.nodeCount}

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
        FExprBuilder.synchronized {toSATFeatureExpr().toTextExpr}

    /**
     * Iterator[Array[(Byte,String)]]
     * Returns a iterator. Each element of the iterator is a clause of the CNF formula.
     * Each element of the clause-array is a single feature.
     * The feature is given as tuple (a,b): a==0 means the feature is negated, b is the name of the feature.
     */
    def getBddAllSat: Iterator[Array[(Byte, String)]] = FExprBuilder.synchronized {
        def clause(d: Array[Byte]): Array[(Byte, String)] = d.zip(0 to (d.length - 1)).filter(_._1 >= 0).map(
            x => (x._1, FExprBuilder.lookupFeatureName(x._2))
        )
        bddAllSat.map(clause(_))
    }

    override def toString: String =
        FExprBuilder.synchronized {toSATFeatureExpr().toString}

    def toTextExprDNF: String =
        printbdd(bdd, "1", "0", " && ", " || ", i => "definedEx(" + FExprBuilder.lookupFeatureName(i) + ")")
    def toStringDNF: String =
        printbdd(bdd, "True", "False", " & ", " | ", FExprBuilder.lookupFeatureName(_))

    private[featureexpr] def toSATFeatureExpr(): FeatureExpr = toSATFeatureExpr(bdd)
    private[featureexpr] def toSATFeatureExpr(bdd: BDD): FeatureExpr =
        if (bdd.isOne) sat.True
        else if (bdd.isZero) sat.False
        else {
            val v = sat.SATFeatureExprFactory.createDefinedExternal(FExprBuilder.lookupFeatureName(bdd.`var`()))
            (v and toSATFeatureExpr(bdd.high())) or (v.not and toSATFeatureExpr(bdd.low()))
        }

    private def printbdd(bdd: BDD, one: String, zero: String, and: String, or: String, toName: (Int) => String): String = FExprBuilder.synchronized {
        if (bdd.isOne()) one
        else if (bdd.isZero()) zero
        else {
            def clause(d: Array[Byte]): String = d.zip(0 to (d.length - 1)).filter(_._1 >= 0).map(
                x => (if (x._1 == 0) "!" else "") + toName(x._2)
            ).mkString(and)

            return bddAllSat.map(clause(_)).mkString(or)
        }
    }

    private def bddAllSat: Iterator[Array[Byte]] = FExprBuilder.synchronized {toScalaAllSat(bdd.allsat())}

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
     * new (more efficient) translation to CNF that does not use allsat
     *
     * output clauses format with sets of variable ids (negative means negated)
     *
     * introduces 3 additional variables for every node in the BDD
     */
    private def toCNFClauses(topbdd: BDD): Iterator[Seq[Int]] = FExprBuilder.synchronized {
        var maxId = FExprBuilder.maxFeatureId
        def genId = { maxId += 1; maxId }

        var result: List[Seq[Int]] = Nil
        var bddIds: Map[BDD, Int] = Map()
        var incomingEdges: Map[BDD, Set[Int]] = Map()
        def addIncomingEdge(bdd: BDD, source: Int) {
            incomingEdges += (bdd -> incomingEdges.getOrElse(bdd, Set()).+(source))
        }

        var bddsTodo: Set[BDD] = Set(topbdd)
        var bddsDone: Set[BDD] = Set()

        while (!bddsTodo.isEmpty) {
            val bddnode = bddsTodo.head
            bddsTodo = bddsTodo.tail
            bddsDone += bddnode

            //for each internal node, create three fresh SAT variables
            val bddIn = genId

            bddIds += (bdd -> bddIn)

            if (!(bddnode.isOne || bddnode.isZero)) {
                val bddTrue = genId
                val bddFalse = genId
                val bddFeature = bdd.`var`()
                //bddTrue <=> bddIn and bdd.feature
                // ( ==> (!bddTrue || bddIn) && (!bddTrue || bdd.feature) && (bddTrue || !bddIn || !bdd.feature)
                result = Seq(-bddTrue, bddIn) :: Seq(-bddTrue, bddFeature) :: Seq(bddTrue, -bddIn, -bddFeature) :: result
                //bddFalse <=> bddIn and !bdd.feature
                result = Seq(-bddFalse, bddIn) :: Seq(-bddFalse, -bddFeature) :: Seq(bddFalse, -bddIn, bddFeature) :: result

                addIncomingEdge(bddnode.low(), bddFalse)
                addIncomingEdge(bddnode.high(), bddTrue)

                if (!(bddsDone contains bddnode.low()))
                    bddsTodo += bddnode.low()
                if (!(bddsDone contains bddnode.high()))
                    bddsTodo += bddnode.high()
            }
        }

        for ((bddnode, incomingEdgeSet) <- incomingEdges) {
            if (bdd.isOne) {
                //require one of the incoming edges
                result ::= incomingEdgeSet.toSeq
            }
            else if (bdd.isZero) {
                //require no incoming edge
                result ++= incomingEdgeSet.map(e => Seq(-e))
            } else {
                val bddIn = bddIds(bddnode)

                //a bddnode with incomming edges from A, B, C (which are the extra variables representing true or false of a decision node)
                //is encoded as
                // bddIn <=> A or B or C
                result ::= incomingEdgeSet.toSeq :+ (-bddIn)
                for (edge <- incomingEdgeSet)
                    result ::= Seq(bddIn, -edge)
            }
        }


        result.iterator
    }


    /**
     * helper function for statistics and such that determines which
     * features are involved in this feature expression
     *
     * new implementation without allsat
     */
    private def collectDistinctFeatureIds: collection.immutable.Set[Int] = FExprBuilder.synchronized {
        var bddKnown: Set[BDD] = Set()
        var bddTodo: Set[BDD] = Set(bdd)

        while (!bddTodo.isEmpty) {
            val bddnode = bddTodo.head
            bddTodo = bddTodo.tail
            bddKnown += bddnode

            if (!(bddKnown contains bddnode.low()))
                bddTodo += bddnode.low()
            if (!(bddKnown contains bddnode.high()))
                bddTodo += bddnode.high()
        }
        (bddKnown - FExprBuilder.TRUE - FExprBuilder.FALSE).map(_.`var`())
    }

    def collectDistinctFeatures: Set[String] =
        collectDistinctFeatureIds.map(FExprBuilder lookupFeatureName _)

    def collectDistinctFeatureObjects: Set[SingleFeatureExpr] =
        collectDistinctFeatureIds.map({
            id: Int => new SingleBDDFeatureExpr(id)
        })

    /**
     * counts the number of features in this expression for statistic
     * purposes
     */
    def countDistinctFeatures: Int = collectDistinctFeatureIds.size
    def evaluate(selectedFeatures: Set[String]): Boolean = FExprBuilder.synchronized {FExprBuilder.evalBdd(bdd, selectedFeatures)}
    def getConfIfSimpleAndExpr(): Option[(Set[SingleFeatureExpr], Set[SingleFeatureExpr])] = FExprBuilder.synchronized {
        // this should be no simpleBDDFeatureExpr, because there the function is inherited from SingleFeatureExpr
        assert(!this.isInstanceOf[SingleFeatureExpr])
        var enabled: Set[SingleFeatureExpr] = Set()
        var disabled: Set[SingleFeatureExpr] = Set()
        def isTrue(x: BDD) = x.isOne
        def isFalse(x: BDD) = x.isZero

        var currentBDD: BDD = this.bdd
        while (!isTrue(currentBDD)) {
            if (isFalse(currentBDD)) {
                // unsatisfiable
                return None
            }
            // bdd.var should be the same int as in lookup-functions, hopefully
            val predicate = new SingleBDDFeatureExpr(currentBDD.`var`())
            val thenbranch: BDD = currentBDD.high()
            val elsebranch: BDD = currentBDD.low()
            if (isFalse(elsebranch)) {
                enabled += predicate
                currentBDD = thenbranch
            } else if (isFalse(thenbranch)) {
                disabled += predicate
                currentBDD = elsebranch
            } else {
                // expression is more complex
                return None
            }
        }
        return Some(enabled, disabled)
    }
    def getConfIfSimpleOrExpr(): Option[(Set[SingleFeatureExpr], Set[SingleFeatureExpr])] = FExprBuilder.synchronized {
        // this should be no simpleBDDFeatureExpr, because there the function is inherited from SingleFeatureExpr
        assert(!this.isInstanceOf[SingleFeatureExpr])
        var enabled: Set[SingleFeatureExpr] = Set()
        var disabled: Set[SingleFeatureExpr] = Set()
        def isTrue(x: BDD) = x.isOne
        def isFalse(x: BDD) = x.isZero

        var currentBDD: BDD = this.bdd
        while (!isFalse(currentBDD)) {
            if (isTrue(currentBDD)) {
                // this is a tautology, but i have problems expressing it
                return None
            }
            // bdd.var should be the same int as in lookup-functions, hopefully
            val predicate = new SingleBDDFeatureExpr(currentBDD.`var`())
            val thenbranch: BDD = currentBDD.high()
            val elsebranch: BDD = currentBDD.low()
            if (isTrue(thenbranch)) {
                enabled += predicate
                currentBDD = elsebranch
            } else if (isTrue(elsebranch)) {
                disabled += predicate
                currentBDD = thenbranch
            } else {
                // expression is more complex
                return None
            }
        }
        return Some(enabled, disabled)
    }
    private def writeReplace(): Object = new FeatureExprSerializationProxy(this.toTextExpr)
}

class SingleBDDFeatureExpr(id: Int) extends BDDFeatureExpr(lookupFeatureBDD(id)) with SingleFeatureExpr {
    def feature = lookupFeatureName(id)
    private def writeReplace(): Object = new FeatureExprSerializationProxy(this.toTextExpr)
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
    var bddValNum: Int = 524288 / 2
    var bddVarNum = 100
    var maxFeatureId = 0
    //start with one, so we can distinguish -x and x for sat solving and tostring
    System.setProperty("bdd", "j")
    var bddFactory: BDDFactory = null
    try {
        bddFactory = BDDFactory.init("j", bddValNum, bddCacheSize)
    } catch {
        case e: OutOfMemoryError =>
            println("running with low memory. consider increasing heap size.")
            bddValNum = 524288
            bddFactory = BDDFactory.init("j", bddValNum, bddCacheSize)
    }
    bddFactory.setIncreaseFactor(2) //200% increase each time
    bddFactory.setMaxIncrease(0) //no upper limit on increase size
    bddFactory.setVarNum(bddVarNum)

    val TRUE: BDD = bddFactory.one()
    val FALSE: BDD = bddFactory.zero()


    private val featureIds: Map[String, Int] = Map()
    private val featureNames: Map[Int, String] = Map()
    private val featureBDDs: Map[Int, BDD] = Map()


    def and(a: BDDFeatureExpr, b: BDDFeatureExpr): BDDFeatureExpr = this.synchronized {new BDDFeatureExpr(a.bdd and b.bdd)}
    def or(a: BDDFeatureExpr, b: BDDFeatureExpr): BDDFeatureExpr = this.synchronized {new BDDFeatureExpr(a.bdd or b.bdd)}
    def imp(a: BDDFeatureExpr, b: BDDFeatureExpr): BDDFeatureExpr = this.synchronized {new BDDFeatureExpr(a.bdd imp b.bdd)}
    def biimp(a: BDDFeatureExpr, b: BDDFeatureExpr): BDDFeatureExpr = this.synchronized {new BDDFeatureExpr(a.bdd biimp b.bdd)}
    def xor(a: BDDFeatureExpr, b: BDDFeatureExpr): BDDFeatureExpr = this.synchronized {new BDDFeatureExpr(a.bdd xor b.bdd)}
    def unique(a: BDDFeatureExpr, b: SingleBDDFeatureExpr): BDDFeatureExpr = this.synchronized {new BDDFeatureExpr(a.bdd.unique(b.bdd))}

    def not(a: BDDFeatureExpr): BDDFeatureExpr = this.synchronized {new BDDFeatureExpr(a.bdd.not())}

    def definedExternal(name: String): SingleBDDFeatureExpr = this.synchronized {
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
        new SingleBDDFeatureExpr(id)
    }

    def containsFeatureID(id: Int): Boolean = featureNames.contains(id)
    def lookupFeatureName(id: Int): String = {
        if (featureNames.contains(id)) featureNames(id) else "unknown"
    }
    def lookupFeatureID(name: String): Int = {
        if (!featureIds.contains(name))
            definedExternal(name)
        featureIds(name)
    }
    private[bdd] def lookupFeatureBDD(id: Int): BDD = featureBDDs(id)

    //create a macro definition (which expands to the current entry in the macro table; the current entry is stored in a closure-like way).
    //a form of caching provided by MacroTable, which we need to repeat here to create the same FeatureExpr object
    def definedMacro(name: String, macroTable: FeatureProvider): BDDFeatureExpr = {
        val f = macroTable.getMacroCondition(name)
        CastHelper.asBDDFeatureExpr(f)
    }


    @tailrec
    def evalBdd(bdd: BDD, set: Set[String]): Boolean =
        if (bdd.isOne) true
        else if (bdd.isZero) false
        else {
            val featureId = bdd.`var`()
            val featureName = featureNames(featureId)
            evalBdd(if (set contains featureName) bdd.high() else bdd.low(), set)
        }
}


object True extends BDDFeatureExpr(FExprBuilder.TRUE) with DefaultPrint with SingleFeatureExpr {
    override def toString = "True"
    override def toTextExpr = "1"
    override def debug_print(ind: Int) = indent(ind) + toTextExpr + "\n"
    override def isSatisfiable(fm: FeatureModel) = true
    override def feature = toString
    private def writeReplace(): Object = new FeatureExprSerializationProxy(this.toTextExpr)
}

object False extends BDDFeatureExpr(FExprBuilder.FALSE) with DefaultPrint with SingleFeatureExpr {
    override def toString = "False"
    override def toTextExpr = "0"
    override def debug_print(ind: Int) = indent(ind) + toTextExpr + "\n"
    override def isSatisfiable(fm: FeatureModel) = false
    override def feature = toString
    private def writeReplace(): Object = new FeatureExprSerializationProxy(this.toTextExpr)
}

object CastHelper {
    def asBDDFeatureExpr(fexpr: FeatureExpr): BDDFeatureExpr = if (fexpr == null) null
    else {
        assert(fexpr.isInstanceOf[BDDFeatureExpr], "Expected BDDFeatureExpr but found " + fexpr.getClass.getCanonicalName + "; do not mix implementations of FeatureExprLib.") //FMCAST
        fexpr.asInstanceOf[BDDFeatureExpr]
    }
    def asSingleBDDFeatureExpr(fexpr: SingleFeatureExpr): SingleBDDFeatureExpr = if (fexpr == null) null
    else {
        assert(fexpr.isInstanceOf[SingleBDDFeatureExpr], "Expected SingleBDDFeatureExpr but found " + fexpr.getClass.getCanonicalName + "; do not mix implementations of FeatureExprLib.") //FMCAST
        fexpr.asInstanceOf[SingleBDDFeatureExpr]
    }
    def asBDDFeatureModel(fm: FeatureModel): BDDFeatureModel =
        if (fm == null) null
        else {
            assert(fm.isInstanceOf[BDDFeatureModel], "Expected BDDFeatureModel but found " + fm.getClass.getCanonicalName + "; do not mix implementations of FeatureExprLib.") //FMCAST
            fm.asInstanceOf[BDDFeatureModel]
        }
}

