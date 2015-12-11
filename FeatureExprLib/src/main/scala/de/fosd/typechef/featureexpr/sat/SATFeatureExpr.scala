package de.fosd.typechef.featureexpr.sat

import java.io.Writer
import java.util.Collections

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.featureexpr.sat.LazyLib._

import scala.Some
import scala.collection.convert.decorateAsScala._
import scala.collection.immutable._
import scala.collection.mutable.{ArrayBuffer, Map}
import scala.ref.WeakReference


object FeatureExprHelper {
    def resolveDefined(mcro: DefinedMacro, macroTable: FeatureProvider): FeatureExpr =
        macroTable.getMacroCondition(mcro.feature)

    private var freshFeatureNameCounter = 0
    def calcFreshFeatureName(): String = {
        freshFeatureNameCounter = freshFeatureNameCounter + 1;
        "__fresh" + freshFeatureNameCounter;
    }
}

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
sealed abstract class SATFeatureExpr extends FeatureExpr {

    import CastHelper._

    // have not implemented yet
    /** NOT implemented, this method will always return None.*/
    def getConfIfSimpleAndExpr(): Option[(Set[SingleFeatureExpr], Set[SingleFeatureExpr])] = {
        None
    }
    /** NOT implemented, this method will always return None.*/
    def getConfIfSimpleOrExpr(): Option[(Set[SingleFeatureExpr], Set[SingleFeatureExpr])] = {
        None
    }

    def or(that: FeatureExpr): FeatureExpr = FExprBuilder.or(this, asSATFeatureExpr(that))
    def and(that: FeatureExpr): FeatureExpr = FExprBuilder.and(this, asSATFeatureExpr(that))
    def notS(): SATFeatureExpr = FExprBuilder.not(this)
    def not(): SATFeatureExpr = notS()

    // we dont have a good implementation in SAT, and a satisfies the contract
    def simplify(b:FeatureExpr): SATFeatureExpr = this

    override def unique(feature: SingleFeatureExpr): FeatureExpr = substitute(feature, False) xor substitute(feature, True)

    def substitute(feature: SingleFeatureExpr, replacement: SATFeatureExpr): SATFeatureExpr

    def getSatisfiableAssignment(featureModel: FeatureModel, interestingFeatures : Set[SingleFeatureExpr],preferDisabledFeatures:Boolean): Option[(List[SingleFeatureExpr], List[SingleFeatureExpr])] = {
        val fm = asSATFeatureModel(featureModel)
        // optimization: if the interestingFeatures-Set is empty and this FeatureExpression is TRUE, we will always return empty sets
        // here we assume that the featureModel is satisfiable (which is checked at FM-instantiation)
        if (this.equals(FeatureExprFactory.True) && interestingFeatures.isEmpty) {
            return Some((List(), List())) // is satisfiable, but no interesting features in solution
        }

        // get one satisfying assignment (a list of features set to true, and a list of features set to false)
        val assignment: Option[(List[String], List[String])] = new SatSolver().getSatAssignment(fm, toCnfEquiSat)
        // we will subtract from this set until all interesting features are handled
        var remainingInterestingFeatures = interestingFeatures
        assignment match {
            case Some((trueFeatures, falseFeatures)) => {
                if (preferDisabledFeatures) {
                    var enabledFeatures: Set[SingleFeatureExpr] = Set()
                    for (f <- trueFeatures) {
                        remainingInterestingFeatures.find({
                            fex: SingleFeatureExpr => fex.feature.equals(f)
                        }) match {
                            case Some(fex: SingleFeatureExpr) => {
                                remainingInterestingFeatures -= fex
                                enabledFeatures += fex
                            }
                            case None => {}
                        }
                    }
                    return Some((enabledFeatures.toList, remainingInterestingFeatures.toList))
                } else {
                    var disabledFeatures: Set[SingleFeatureExpr] = Set()
                    for (f <- falseFeatures) {
                        remainingInterestingFeatures.find({
                            fex: SingleFeatureExpr => fex.feature.equals(f)
                        }) match {
                            case Some(fex: SingleFeatureExpr) => {
                                remainingInterestingFeatures -= fex
                                disabledFeatures += fex
                            }
                            case None => {}
                        }
                    }
                    return Some((remainingInterestingFeatures.++(this.collectDistinctFeatureObjects).toList, disabledFeatures.toList))
                }

            }
            case None => return None
        }
    }

    /**
      * x.isSatisfiable(fm) is short for x.and(fm).isSatisfiable
      * but is faster because FM is cached
      */
    def isSatisfiable(fm: FeatureModel): Boolean = {
        val f = if (fm == null) SATNoFeatureModel
        else {
            assert(fm.isInstanceOf[SATFeatureModel])
            fm.asInstanceOf[SATFeatureModel]
        }

        // count total and cached sat calls for solving feature expressions with SAT
        // we only consider non-trivial (!= True, and != False) feature expressions
        if (this != FeatureExprFactory.True && this != FeatureExprFactory.False) {
            if (!cacheIsSatisfiable.isDefinedAt(f))
                FeatureExpr.incSatCalls
            else {
                FeatureExpr.incCachedSatCalls
                FeatureExpr.incSatCalls
            }
        }

        cacheIsSatisfiable.getOrElseUpdate(f, new SatSolver().isSatisfiable(toCnfEquiSat, f))
    }

    /**
      * Check structural equality, assuming that all component nodes have already been canonicalized.
      * The default implementation checks for pointer equality.
      */
    private[featureexpr] def equal1Level(that: SATFeatureExpr) = this eq that

    final override def equals(that: Any) = super.equals(that)

    protected def calcHashCode = super.hashCode

    /**
      * heuristic to determine whether a feature expression is small
      * (may be used to decide whether to inline it or not)
      *
      * use with care
      */
    def isSmall(): Boolean = size <= 10

    /**
      * replaces all DefinedMacro tokens by their full expansion.
      *
      * the resulting feature expression contains only DefinedExternal nodes as leafs
      * and can be printed and read again
      */
    lazy val resolveToExternal: SATFeatureExpr = FExprBuilder.resolveToExternal(this)

    /**
      * checks whether there is some unresolved macro (DefinedMacro) somewhere
      * in the expression tree
      */
    lazy val isResolved: Boolean = calcIsResolved
    private def calcIsResolved: Boolean = {
        //exception used to stop at the first found Macro
        //map used for caching (to not look twice at the same subtree)
        class FoundUnresolvedException extends Exception
        try {
            this.mapDefinedExpr({
                case e: DefinedMacro => {
                    throw new FoundUnresolvedException();
                    e
                }
                case e => e
            }, Map())
            return true
        } catch {
            case e: FoundUnresolvedException => return false
        }
    }

    /**
      * map function that applies to all leafs in the feature expression (i.e. all DefinedExpr nodes)
      */
    def mapDefinedExpr(f: DefinedExpr => SATFeatureExpr, cache: Map[SATFeatureExpr, SATFeatureExpr]): SATFeatureExpr

    private var cache_cnf: SATFeatureExpr = null
    private var cache_cnfEquiSat: SATFeatureExpr = null

    /**
      * creates an equivalent feature expression in CNF
      *
      * be aware of exponential explosion. consider using toCnfEquiSat instead if possible
      */
    def toCNF(): SATFeatureExpr = {
        if (cache_cnf == null) {
            cache_cnf = calcCNF;
            cache_cnfEquiSat = cache_cnf
        }
        assert(CNFHelper.isCNF(cache_cnf))
        //XXX: add and test!
        //cache_cnfEquiSat.cache_cnf = cache_cnf
        //cache_cnfEquiSat.cache_cnfEquiSat = cache_cnf
        cache_cnf
    }
    /**
      * creates an equisatisfiable feature expression in CNF
      *
      * the result is not equivalent but will yield the same result
      * in satisifiability tests with SAT solvers
      *
      * the algorithm introduces new variables and is faster than toCNF
      */
    def toCnfEquiSat(): SATFeatureExpr = {
        if (cache_cnfEquiSat == null) cache_cnfEquiSat = calcCNFEquiSat
        assert(CNFHelper.isCNF(cache_cnfEquiSat))
        //XXX: add and test!
        //cache_cnfEquiSat.cache_cnfEquiSat = cache_cnfEquiSat
        cache_cnfEquiSat
    }
    protected def calcCNF: SATFeatureExpr
    protected def calcCNFEquiSat: SATFeatureExpr

    private val cacheIsSatisfiable: Map[SATFeatureModel, Boolean] =
        Collections.synchronizedMap(new java.util.WeakHashMap[SATFeatureModel, Boolean]()).asScala
    //only access these caches from FExprBuilder
    private[sat] val andCache: Map[SATFeatureExpr, WeakReference[SATFeatureExpr]] =
        Collections.synchronizedMap(new java.util.WeakHashMap[SATFeatureExpr, WeakReference[SATFeatureExpr]]()).asScala
    private[sat] val orCache: Map[SATFeatureExpr, WeakReference[SATFeatureExpr]] =
        Collections.synchronizedMap(new java.util.WeakHashMap[SATFeatureExpr, WeakReference[SATFeatureExpr]]()).asScala
    private[sat] var notCache: Option[NotReference[SATFeatureExpr]] = None

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

    // This field keeps the wrapper referenced in a reference cycle, so that the lifecycle of this object and the wrapper match.
    // This is crucial to use the wrapper in a WeakHashMap!
    private[featureexpr] val wrap = StructuralEqualityWrapper(this)

    /**
      * helper function for statistics and such that determines which
      * features are involved in this feature expression
      */
    def collectDistinctFeatures: Set[String] = {
        var result: Set[String] = Set()
        this.mapDefinedExpr(_ match {
            case e: DefinedExternal => result += e.feature; e
            case e: DefinedMacro => result += e.feature; e
            case e => e
        }, Map())
        result
    }

    def collectDistinctFeatures2: Set[DefinedExternal] = {
        var result: Set[DefinedExternal] = Set()
        this.mapDefinedExpr(_ match {
            case e: DefinedExternal => result += e; e
            case e => e
        }, Map())
        result
    }

    def collectDistinctFeatureObjects: Set[SingleFeatureExpr] = {
        var result: Set[SingleFeatureExpr] = Set()
        this.mapDefinedExpr(_ match {
            case e: DefinedExternal => result += e; e
            case e: DefinedMacro => result += e; e
            case e => e
        }, Map())
        result
    }

    /**
      * counts the number of features in this expression for statistic
      * purposes
      */
    def countDistinctFeatures: Int = collectDistinctFeatures.size

    def evaluate(selectedFeatures: Set[String]): Boolean
}


// Cache the computed hashCode. Note that this can make sense only if you override calcHashCode,
// and the computation is complex enough. Currently this means only not, and even then I'm not sure it's the best.
abstract class HashCachingFeatureExpr extends SATFeatureExpr {
    protected val cachedHash = calcHashCode

    final override def hashCode = cachedHash
}


/**
  * Central builder class, responsible for simplification of expressions during creation
  * and for extensive caching.
  */
private[sat] object FExprBuilder {

    private val featureCache: Map[String, WeakReference[DefinedExternal]] = Map()
    private var macroCache: Map[String, WeakReference[DefinedMacro]] = Map()
    private val resolvedCache: Map[SATFeatureExpr, SATFeatureExpr] =
        Collections.synchronizedMap(new java.util.WeakHashMap[SATFeatureExpr, SATFeatureExpr]()).asScala
    private val hashConsingCache: Map[StructuralEqualityWrapper, WeakReference[StructuralEqualityWrapper]] =
        Collections.synchronizedMap(new java.util.WeakHashMap[StructuralEqualityWrapper, WeakReference[StructuralEqualityWrapper]]()).asScala

    private def cacheGetOrElseUpdate[A, B <: AnyRef](map: Map[A, WeakReference[B]], key: A, op: => B): B = {
        def update() = {
            val d = op
            map(key) = new WeakReference[B](d)
            d
        }
        val v = map.get(key)
        if (v == null) update
        else
            v match {
            case Some(WeakRef(value)) => value
            case _ => update()
        }
    }

    private def getFromCache(cache: Map[SATFeatureExpr, WeakReference[SATFeatureExpr]], key: SATFeatureExpr): Option[SATFeatureExpr] = {
        cache.get(key) match {
            case Some(WeakRef(f)) => Some(f)
            case _ => None
        }
    }
    private def binOpCacheGetOrElseUpdate(a: SATFeatureExpr,
                                          b: SATFeatureExpr,
                                          getCache: SATFeatureExpr => Map[SATFeatureExpr, WeakReference[SATFeatureExpr]],
                                          featThunk: => SATFeatureExpr): SATFeatureExpr = {
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

    private def canonical(f: SATFeatureExpr) = cacheGetOrElseUpdate(hashConsingCache, f.wrap, f.wrap).unwrap

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

    // Optimized representation of e and (o: Or)
    private def andOr(e: SATFeatureExpr, o: Or) =
        if (o.clauses contains e) //o = (e || o'), then e || o == e && (e || o') == e
            e
        else if (o.clauses contains (e.not))
            fastAnd(e, createOr(o.clauses - e.notS)) //XXX: O(N) set rebuild
        else
            And(Set(e, o))

    private def andOrOr(o1: Or, o2: Or) =
        if (o1.clauses contains o2) //o = (e || o'), then e || o == e && (e || o') == e
            o2
        else if (o2.clauses contains o1) //o = (e || o'), then e || o == e && (e || o') == e
            o1
        else if (o1.clauses contains (o2.not))
            fastAnd(o2, createOr(o1.clauses - o2.not)) //XXX: O(N) set rebuild
        else if (o2.clauses contains (o1.not))
            fastAnd(o1, createOr(o2.clauses - o1.not)) //XXX: O(N) set rebuild
        else if ((o1.clauses.size == 2) && (o2.clauses.size == 2)) {
            //simple but common pattern ((a or b) and (a or !b)) == a
            val i1 = o1.clauses.iterator
            val o11 = i1.next
            val o12 = i1.next
            val i2 = o2.clauses.iterator
            val o21 = i2.next
            val o22 = i2.next
            if (o11 == o21 && o12 == o22.not) o11
            else if (o12 == o21 && o11 == o22.not) o12
            else if (o11 == o22 && o12 == o21.not) o11
            else if (o12 == o22 && o11 == o21.not) o12
            else And(Set(o1, o2))
        }
        else
            And(Set(o1, o2))

    //Optimized representation of e and (a: And)
    private def andAnd(e: SATFeatureExpr, a: And) =
        if (a.clauses contains e)
            a
        else if (a.clauses contains (e.not))
            False
        else
            And(a.clauses + e, a, e)

    //Do not canonicalize the function at each step, do it only in the public wrappers.
    private def fastAnd(a: SATFeatureExpr, b: SATFeatureExpr): SATFeatureExpr =
        (a, b) match {
            case (e1, e2) if (e1 == e2) => e1
            case (_, False) => False
            case (False, _) => False
            case (True, e) => e
            case (e, True) => e
            case (e1, e2) if ((e1.retrieveMemoizedNot == e2) || (e1 == e2.retrieveMemoizedNot)) => False
            case other =>
                binOpCacheGetOrElseUpdate(a, b, _.andCache,
                    other match {
                        case (a1: And, a2: And) =>
                            a2.clauses.foldLeft[SATFeatureExpr](a1)(fastAnd(_, _)) //XXX: O(N) set rebuild
                        case (a: And, e) => andAnd(e, a)
                        case (e, a: And) => andAnd(e, a)
                        case (o: Or, e: Or) => andOrOr(e, o)
                        case (o: Or, e) => andOr(e, o)
                        case (e, o: Or) => andOr(e, o)
                        case (e1, e2) => And(Set(e1, e2))
                    })
        }

    def createAnd(clauses: Traversable[SATFeatureExpr]) =
        canonical(clauses.foldLeft[SATFeatureExpr](True)(fastAnd(_, _)))

    //Optimized representation of e or (a: And)
    private def orAnd(e: SATFeatureExpr, a: And) =
        if (a.clauses contains e) //a == (e && a'), then e || a == e || (e && a') == e
            e
        else if (a.clauses contains (e.not))
            fastOr(e, createAnd(a.clauses - e.not)) //XXX: O(N) set rebuild
        else
            Or(Set(e, a))

    private def orAndAnd(a1: And, a2: And) =
        if (a2.clauses contains a1) //a == (e && a'), then e || a == e || (e && a') == e
            a1
        else if (a1.clauses contains a2) //a == (e && a'), then e || a == e || (e && a') == e
            a2
        else if (a2.clauses contains (a1.not))
            fastOr(a1, createAnd(a2.clauses - a1.not)) //XXX: O(N) set rebuild
        else if (a1.clauses contains (a2.not))
            fastOr(a2, createAnd(a1.clauses - a2.not)) //XXX: O(N) set rebuild
        else if ((a1.clauses.size == 2) && (a2.clauses.size == 2)) {
            //simple but common pattern ((a and b) or (a and !b)) == a
            val i1 = a1.clauses.iterator
            val a11 = i1.next
            val a12 = i1.next
            val i2 = a2.clauses.iterator
            val a21 = i2.next
            val a22 = i2.next
            if (a11 == a21 && a12 == a22.not) a11
            else if (a12 == a21 && a11 == a22.not) a12
            else if (a11 == a22 && a12 == a21.not) a11
            else if (a12 == a22 && a11 == a21.not) a12
            else Or(Set(a1, a2))
        }
        else
            Or(Set(a1, a2))

    private def orOr(e: SATFeatureExpr, o: Or) =
        if (o.clauses contains e)
            o
        else if (o.clauses contains (e.not))
            True
        else
            Or(o.clauses + e, o, e)

    def fastOr(a: SATFeatureExpr, b: SATFeatureExpr): SATFeatureExpr =
    //simple cases without caching
        (a, b) match {
            case (e1, e2) if (e1 == e2) => e1
            case (_, True) => True
            case (True, _) => True
            case (False, e) => e
            case (e, False) => e
            case (e1, e2) if ((e1.retrieveMemoizedNot == e2) || (e1 == e2.retrieveMemoizedNot)) => True
            case other =>
                binOpCacheGetOrElseUpdate(a, b, _.orCache,
                    other match {
                        case (o1: Or, o2: Or) =>
                            o2.clauses.foldLeft[SATFeatureExpr](o1)(fastOr(_, _)) //XXX: O(N) set rebuild
                        case (o: Or, e) => orOr(e, o)
                        case (e, o: Or) => orOr(e, o)
                        case (e: And, a: And) => orAndAnd(e, a)
                        case (e, a: And) => orAnd(e, a)
                        case (a: And, e) => orAnd(e, a)
                        case (e1, e2) => Or(Set(e1, e2))
                    })
        }

    def createOr(clauses: Traversable[SATFeatureExpr]) =
        canonical(clauses.foldLeft[SATFeatureExpr](False)(fastOr(_, _)))

    //End of dualized code.


    def and(a: SATFeatureExpr, b: SATFeatureExpr): SATFeatureExpr = canonical(fastAnd(a, b))
    def or(a: SATFeatureExpr, b: SATFeatureExpr): SATFeatureExpr = canonical(fastOr(a, b))

    def not(a: SATFeatureExpr): SATFeatureExpr =
        a match {
            case True => False
            case False => True
            case n: Not => n.expr
            case e => {
                e.notCache match {
                    case Some(NotRef(res)) => res
                    case _ =>
                        def storeCache(e: SATFeatureExpr, neg: SATFeatureExpr) = {
                            e.notCache = Some(new NotReference(neg));
                            e
                        }
                        val res = canonical(e match {
                            /* This transformation is expensive, so we need to store
                            * a reference to e in the created expression. However,
                            * this enables more occasions for simplification and
                            * ensures that the result is in Negation Normal Form.
                            */
                            case And(clauses) => createOr(clauses.map(_.not))
                            case Or(clauses) => createAnd(clauses.map(_.not))
                            case _ => new Not(e) //Triggered by leaves.
                        })
                        storeCache(res, e)
                        //Store in the old expression a reference to the new one.
                        storeCache(e, res)
                        res
                }
            }
        }

    def definedExternal(name: String) = cacheGetOrElseUpdate(featureCache, name, new DefinedExternal(name))

    import CastHelper._

    //create a macro definition (which expands to the current entry in the macro table; the current entry is stored in a closure-like way).
    //a form of caching provided by MacroTable, which we need to repeat here to create the same SATFeatureExpr object
    def definedMacro(name: String, macroTable: FeatureProvider): SATFeatureExpr = {
        val macroCondition = asSATFeatureExpr(macroTable.getMacroCondition(name))
        if (macroCondition.isSmall) {
            macroCondition
        } else {
            val (conditionName, conditionDef) = macroTable.getMacroConditionCNF(name)

            /**
              * definedMacros are equal if they have the same Name and the same expansion! (otherwise they refer to
              * the macro at different points in time and should not be considered equal)
              * actually, we only check the expansion name which is unique for each DefinedMacro anyway
              */
            cacheGetOrElseUpdate(macroCache, conditionName,
                new DefinedMacro(
                    name,
                    asSATFeatureExpr(macroCondition),
                    conditionName,
                    conditionDef))
        }
    }

    def resolveToExternal(expr: SATFeatureExpr): SATFeatureExpr = expr.mapDefinedExpr({
        case e: DefinedMacro => e.presenceCondition.resolveToExternal
        case e => e
    }, resolvedCache)
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
object True extends And(Set()) with DefaultPrint {
    override def toString = "True"
    override def toTextExpr = "1"
    override def debug_print(ind: Int) = indent(ind) + toTextExpr + "\n"
    override def isSatisfiable(fm: FeatureModel) = true
    override def evaluate(selectedFeatures: Set[String]) = true
    override def substitute(feature: SingleFeatureExpr, replacement: SATFeatureExpr): SATFeatureExpr = this
    private def writeReplace(): Object = new FeatureExprSerializationProxy(this.toTextExpr)
}

object False extends Or(Set()) with DefaultPrint {
    override def toString = "False"
    override def toTextExpr = "0"
    override def debug_print(ind: Int) = indent(ind) + toTextExpr + "\n"
    override def isSatisfiable(fm: FeatureModel) = false
    override def evaluate(selectedFeatures: Set[String]) = false
    override def substitute(feature: SingleFeatureExpr, replacement: SATFeatureExpr): SATFeatureExpr = this
    private def writeReplace(): Object = new FeatureExprSerializationProxy(this.toTextExpr)
}


//The class name means And/Or (Un)Extractor.
abstract class AndOrUnExtractor[This <: BinaryLogicConnective[This]] {
    def identity: SATFeatureExpr
    def unapply(x: This) = Some(x.clauses)
    private def optBuild(clauses: Set[SATFeatureExpr], defaultRes: => This) = {
        clauses.size match {
            case 0 => identity
            /* The case below seems to not occur, but better include it
             * for extra robustness, to ensure the weak canonicalization property. */
            case 1 => clauses.head
            case _ => defaultRes
        }
    }

    private[featureexpr] def apply(clauses: Set[SATFeatureExpr]) = optBuild(clauses, createRaw(clauses))
    private[featureexpr] def apply(clauses: Set[SATFeatureExpr], old: This, newF: SATFeatureExpr) = optBuild(clauses, createRaw(clauses, old, newF))

    //Factory methods for the actual object type
    protected def createRaw(clauses: Set[SATFeatureExpr]): This
    protected def createRaw(clauses: Set[SATFeatureExpr], old: This, newF: SATFeatureExpr): This
}

//objects And and Or are just boilerplate instances of AndOrUnExtractor
object And extends AndOrUnExtractor[And] {
    def identity = True
    protected def createRaw(clauses: Set[SATFeatureExpr]) = new And(clauses)
    protected def createRaw(clauses: Set[SATFeatureExpr], old: And, newF: SATFeatureExpr) = new And(clauses, old, newF)
}

object Or extends AndOrUnExtractor[Or] {
    def identity = False
    protected def createRaw(clauses: Set[SATFeatureExpr]) = new Or(clauses)
    protected def createRaw(clauses: Set[SATFeatureExpr], old: Or, newF: SATFeatureExpr) = new Or(clauses, old, newF)
}

private[featureexpr]
abstract class BinaryLogicConnective[This <: BinaryLogicConnective[This]] extends SATFeatureExpr {
    private[featureexpr] def clauses: Set[SATFeatureExpr]

    def operName: String
    def create(clauses: Traversable[SATFeatureExpr]): SATFeatureExpr
    //Can't declare This as return type - the optimizations in FExprBuilder are such that it might build an object of
    //unexpected type.

    override def equal1Level(that: SATFeatureExpr) = that match {
        case e: BinaryLogicConnective[_] =>
            e.primeHashMult == primeHashMult && //check this as a class tag
                e.clauses.subsetOf(clauses) &&
                e.clauses.size == clauses.size
        case _ => false
    }

    def primeHashMult: Int
    override def calcHashCode = primeHashMult * clauses.map(_.hashCode).foldLeft(0)(_ + _)

    // We need to compute the hashCode lazily (and pay a penalty when accessing it) because too many temporaries are
    // created. We might want to change that, though (see comments above mentioning "XXX: O(N) set rebuild"), and
    // retest this choice.
    // In a few cases, however, we compute the hashcode eagerly and incrementally (to reuse old hashcode computations).
    protected var cachedHash: Option[Int] = None
    final override def hashCode =
        cachedHash match {
            case Some(hash) =>
                hash
            case None =>
                val hash = calcHashCode
                cachedHash = Some(hash)
                hash
        }

    //Constructors of subclasses must call either of these methods. Since the hash computation is reasonably cheap
    protected def presetHash(old: This, newF: SATFeatureExpr) =
    //This computation is O(1); throwing out the hashCode and recomputing it would be O(n), and when growing
    //a Set, one element at a time, the time complexity of hash updates would be potentially O(n^2).
        cachedHash = Some(old.hashCode + primeHashMult * newF.hashCode)


    override def toString = clauses.mkString("(", operName, ")")
    override def toTextExpr = clauses.map(_.toTextExpr).mkString("(", " " + operName + operName + " ", ")")
    override def print(p: Writer) = {
        trait PrintValue
        case object NoPrint extends PrintValue
        case object Printed extends PrintValue
        case class ToPrint[T](x: T) extends PrintValue
        p write "("
        clauses.map(x => ToPrint(x)).foldLeft[PrintValue](NoPrint)({
            case (NoPrint, ToPrint(c)) => {
                c.print(p);
                Printed
            }
            case (Printed, ToPrint(c)) => {
                p.write(" " + operName + operName + " ");
                c.print(p);
                Printed
            }
        })
        p write ")"
    }
    override def debug_print(ind: Int) = indent(ind) + operName + "\n" + clauses.map(_.debug_print(ind + 1)).mkString

    override def calcSize = clauses.foldLeft(0)(_ + _.size)
    override def mapDefinedExpr(f: DefinedExpr => SATFeatureExpr, cache: Map[SATFeatureExpr, SATFeatureExpr]): SATFeatureExpr = cache.getOrElseUpdate(this, {
        var anyChange = false
        val newClauses = clauses.map(x => {
            val y = x.mapDefinedExpr(f, cache)
            anyChange |= x != y
            y
        })
        if (anyChange)
            create(newClauses)
        else
            this
    })

    override def substitute(feature: SingleFeatureExpr, replacement: SATFeatureExpr): SATFeatureExpr = create(clauses.map(_.substitute(feature,replacement)))

}

//private[featureexpr]
class And(val clauses: Set[SATFeatureExpr]) extends BinaryLogicConnective[And] {
    //Use this constructor when adding newF to old, because it reuses the old hash.
    def this(clauses: Set[SATFeatureExpr], old: And, newF: SATFeatureExpr) = {
        this(clauses)
        presetHash(old, newF)
    }

    override def primeHashMult = 37
    override def operName = "&"
    override def create(clauses: Traversable[SATFeatureExpr]) = FExprBuilder.createAnd(clauses)

    override protected def calcCNF: SATFeatureExpr = FExprBuilder.createAnd(clauses.map(_.toCNF))
    override protected def calcCNFEquiSat: SATFeatureExpr = FExprBuilder.createAnd(clauses.map(_.toCnfEquiSat))
    override def evaluate(selectedFeatures: Set[String]) =
        clauses.foldLeft(true)(_ && _.evaluate(selectedFeatures))
    private def writeReplace(): Object = new FeatureExprSerializationProxy(this.toTextExpr)
}

//private[featureexpr]
class Or(val clauses: Set[SATFeatureExpr]) extends BinaryLogicConnective[Or] {
    //Use this constructor when adding newF to old, because it reuses the old hash.
    def this(clauses: Set[SATFeatureExpr], old: Or, newF: SATFeatureExpr) = {
        this(clauses)
        presetHash(old, newF)
    }

    override def primeHashMult = 97
    override def operName = "|"
    override def create(clauses: Traversable[SATFeatureExpr]) = FExprBuilder.createOr(clauses)

    override protected def calcCNF: SATFeatureExpr =
        combineCNF(clauses.map(_.toCNF))
    override protected def calcCNFEquiSat: SATFeatureExpr = {
        val cnfchildren = clauses.map(_.toCnfEquiSat)
        //XXX: There is no need to estimate the size this way, we could maybe
        //use the more precise size method. However, possibly this is the
        //correct calculation of the number of generated clauses. The name
        //predictedCNFClauses is maybe misleading, but I introduced it, and it
        //would be my fault then. PG
        //
        //heuristic: up to a medium size do not introduce new variables but use normal toCNF mechanism
        //rationale: we might actually simplify the formula by transforming it into CNF and in such cases it's not very expensive
        def size(child: SATFeatureExpr) = child match {
            case And(inner) => inner.size;
            case _ => 1
        }
        val predictedCNFClauses = cnfchildren.foldRight(1)((x, y) => if (y <= 16) size(x) * y else y)
        if (predictedCNFClauses <= 16)
            combineCNF(cnfchildren)
        else
            combineEquiCNF(cnfchildren)
    }


    /**
      * multiplies all clauses
      *
      * for n CNF expressions with e1, e2, .., en clauses
      * this mechanism produces e1*e2*...*en clauses
      */
    private def combineCNF(cnfchildren: Set[SATFeatureExpr]) =
        FExprBuilder.createAnd(
            if (cnfchildren.exists(_.isInstanceOf[And])) {
                var conjuncts = Set[SATFeatureExpr](False)
                for (child <- cnfchildren) {
                    child match {
                        case And(innerChildren) =>
                            //conjuncts@(a_1 & a_2) | innerChildren@(b_1 & b_2)
                            //becomes conjuncts'@(a_1 | b_1) & (a_1 | b_2) & (a_2 | b_1) & (a_2 | b_2).
                            /*conjuncts = conjuncts.flatMap(
                                conjunct => innerChildren.map(
                                    _ or conjunct))*/
                            conjuncts =
                                for (conjunct <- conjuncts; c <- innerChildren)
                                    yield FExprBuilder.or(c, conjunct)
                        case _ =>
                            conjuncts = conjuncts.map(x => FExprBuilder.or(x, child))
                    }
                }
                assert(conjuncts.forall(c => CNFHelper.isClause(c) || c == True || c == False))
                conjuncts
            } else {
                /* The context adds an extra And, because a canonical CNF is an conjunction of disjunctions.
                 * Currently this extra And is optimized away, but here I do not want to rely on this detail. */
                List(FExprBuilder.createOr(cnfchildren))
            })

    /**
      * Produce a CNF formula equiSatisfiable to the disjunction of @param cnfchildren.
      * Introduces new variables to avoid exponential behavior
      *
      * for n CNF expressions with e1, e2, .., en clauses
      * this mechanism produces n new variables and results
      * in e1+e2+..+en+1 clauses
      *
      * Algorithm: we need to represent Or(X_i, i=i..n), where X_i are subformulas in CNF. Each of them is a conjunction
      * (or a literal, as a degenerate case).
      * We produce a clause Or(Z_i) and clauses such that Z_i implies X_i, conjuncted together. For literals we just
      * reuse the literal; if X_i = And(Y_ij, j=1..e_i), we produce clauses (Z_i implies Y_ij) for j=1..e_i.
      */
    private def combineEquiCNF(cnfchildren: Set[SATFeatureExpr]) =
        if (cnfchildren.exists(_.isInstanceOf[And])) {
            var orClauses = ArrayBuffer[SATFeatureExpr]() //list of Or expressions
            var renamedDisjunction = ArrayBuffer[SATFeatureExpr]()
            for (child <- cnfchildren) {
                child match {
                    case And(innerChildren) =>
                        val freshFeature = FExprBuilder.definedExternal(FeatureExprHelper.calcFreshFeatureName())
                        orClauses ++= innerChildren.map(x => (freshFeature implies x).asInstanceOf[SATFeatureExpr])
                        renamedDisjunction += freshFeature
                    case e =>
                        renamedDisjunction += e
                }
            }
            orClauses += FExprBuilder.createOr(renamedDisjunction.toList)
            FExprBuilder.createAnd(orClauses.toList)
            /*val (orClauses, renamedDisjunction) = cnfchildren.map({
                case And(innerChildren) =>
                    val freshFeature = FExprBuilder.definedExternal(FeatureExprHelper.calcFreshFeatureName())
                    (innerChildren.map(freshFeature implies _), freshFeature)
                case e =>
                    (Set.empty, e)
            }).unzip
            FExprBuilder.createAnd(orClauses.flatten[SATFeatureExpr] + FExprBuilder.createOr(renamedDisjunction))*/
        } else FExprBuilder.createOr(cnfchildren)

    override def evaluate(selectedFeatures: Set[String]) =
        clauses.foldLeft(false)(_ || _.evaluate(selectedFeatures))
    private def writeReplace(): Object = new FeatureExprSerializationProxy(this.toTextExpr)
}

//private[featureexpr]
class Not(val expr: SATFeatureExpr) extends HashCachingFeatureExpr {
    override def calcHashCode = 701 * expr.hashCode
    override def equal1Level(that: SATFeatureExpr) = that match {
        case Not(expr2) => expr eq expr2
        case _ => false
    }

    override def toString = "!" + expr.toString
    override def toTextExpr = "!" + expr.toTextExpr
    override def print(p: Writer) = {
        p.write("!")
        expr.print(p)
    }
    override def debug_print(ind: Int) = indent(ind) + "!\n" + expr.debug_print(ind + 1)

    override def calcSize = expr.size
    override def mapDefinedExpr(f: DefinedExpr => SATFeatureExpr, cache: Map[SATFeatureExpr, SATFeatureExpr]): SATFeatureExpr = cache.getOrElseUpdate(this, {
        val newExpr = expr.mapDefinedExpr(f, cache)
        if (newExpr != expr) FExprBuilder.not(newExpr) else this
    })
    override protected def calcCNF: SATFeatureExpr = expr match {
        case And(children) => FExprBuilder.createOr(children.map(_.not.toCNF)).toCNF
        case Or(children) => FExprBuilder.createAnd(children.map(_.not.toCNF))
        case e => this
    }

    override protected def calcCNFEquiSat: SATFeatureExpr = expr match {
        case And(children) => FExprBuilder.createOr(children.map(_.not.toCnfEquiSat())).toCnfEquiSat()
        case Or(children) => FExprBuilder.createAnd(children.map(_.not.toCnfEquiSat()))
        case e => this
    }

    private[featureexpr] override def retrieveMemoizedNot = expr

    override def evaluate(selectedFeatures: Set[String]) =
        !expr.evaluate(selectedFeatures)
    override def substitute(feature: SingleFeatureExpr, replacement: SATFeatureExpr): SATFeatureExpr = expr.substitute(feature, replacement).not

    private def writeReplace(): Object = new FeatureExprSerializationProxy(this.toTextExpr)
}

object Not {
    def unapply(x: Not) = Some(x.expr)
}

/**
  * Leaf nodes of propositional feature expressions, either an external
  * feature defined by the user or another feature expression from a macro.
  */
abstract class DefinedExpr extends SATFeatureExpr with SingleFeatureExpr {
    /*
     * This method is overriden by children case classes to return the name.
     * It would be nice to have an actual field here, but that doesn't play nicely with case classes;
     * avoiding case classes and open-coding everything would take too much code.
     */
    def feature: String
    override def debug_print(level: Int): String = indent(level) + feature + "\n";
    def accept(f: SATFeatureExpr => Unit): Unit = f(this)
    def satName = feature
    //used for sat solver only to distinguish extern and macro
    def isExternal: Boolean
    override def calcSize = 1
    override def mapDefinedExpr(f: DefinedExpr => SATFeatureExpr, cache: Map[SATFeatureExpr, SATFeatureExpr]): SATFeatureExpr = cache.getOrElseUpdate(this, f(this))
    override def calcCNF = this
    override def calcCNFEquiSat = this
}

object DefinedExpr {
    def unapply(f: DefinedExpr): Option[DefinedExpr] = f match {
        case x: DefinedExternal => Some(x)
        case x: DefinedMacro => Some(x)
        case _ => None
    }
    def checkFeatureName(name: String) = assert(name != "1" && name != "0" && name != "", "invalid feature name: " + name)
}

/**
  * external definition of a feature (cannot be decided to Base or Dead inside this file)
  */
class DefinedExternal(name: String) extends DefinedExpr {
    DefinedExpr.checkFeatureName(name)

    def feature = name
    override def toTextExpr = "definedEx(" + name + ")";
    override def toString = "def(" + name + ")"
    def countSize() = 1
    def isExternal = true
    override def substitute(feature: SingleFeatureExpr, replacement: SATFeatureExpr): SATFeatureExpr =
        if (feature == this) replacement else this
    override def evaluate(selectedFeatures: Set[String]) = selectedFeatures contains name
    private def writeReplace(): Object = new FeatureExprSerializationProxy(this.toTextExpr)
}

/**
  * definition based on a macro, still to be resolved using the macro table
  * (the macro table may not contain DefinedMacro expressions, but only DefinedExternal)
  * assumption: expandedName is unique and may be used for comparison
  */
class DefinedMacro(val name: String, val presenceCondition: SATFeatureExpr, val expandedName: String, val presenceConditionCNF: Susp[FeatureExpr /*CNF*/ ]) extends DefinedExpr {
    DefinedExpr.checkFeatureName(name)

    def feature = name
    override def toTextExpr = "defined(" + name + ")"
    override def toString = "macro(" + name + "=" +presenceCondition.resolveToExternal.toString +  ")"
    override def satName = expandedName
    def countSize() = 1
    def isExternal = false
    /**TODO: This probably would be the correct way, but it breaks my product generation code, and i cannot fix it right now */
    //override def collectDistinctFeatures=presenceCondition.resolveToExternal.collectDistinctFeatures
    //override def collectDistinctFeatureObjects=presenceCondition.resolveToExternal.collectDistinctFeatureObjects
    override def evaluate(selectedFeatures: Set[String]) = presenceCondition.evaluate(selectedFeatures)
    override def substitute(feature: SingleFeatureExpr, replacement: SATFeatureExpr): SATFeatureExpr = this
    private def writeReplace(): Object = throw new RuntimeException("cannot serialize DefinedMacro")
}

object DefinedMacro {
    def unapply(x: DefinedMacro) = Some((x.name, x.presenceCondition, x.expandedName, x.presenceConditionCNF))
}


private[sat] case class StructuralEqualityWrapper(f: SATFeatureExpr) {
    final override def equals(that: Any) =
        super.equals(that) || (that match {
            case StructuralEqualityWrapper(thatF) => f.equal1Level(thatF)
            case _ => false
        })
    final override def hashCode = f.hashCode
    final def unwrap = f
}


object CastHelper {
    def asSATFeatureExpr(fexpr: FeatureExpr): SATFeatureExpr = if (fexpr == null) null
    else {
        assert(fexpr.isInstanceOf[SATFeatureExpr], "Expected SATFeatureExpr but found " + fexpr.getClass.getCanonicalName + "; do not mix implementations of FeatureExprLib.") //FMCAST
        fexpr.asInstanceOf[SATFeatureExpr]
    }
    def asSATFeatureModel(fm: FeatureModel): SATFeatureModel =
        if (fm == null) null
        else {
            assert(fm.isInstanceOf[SATFeatureModel], "Expected SATFeatureModel but found " + fm.getClass.getCanonicalName + "; do not mix implementations of FeatureExprLib.") //FMCAST
            fm.asInstanceOf[SATFeatureModel]
        }
}