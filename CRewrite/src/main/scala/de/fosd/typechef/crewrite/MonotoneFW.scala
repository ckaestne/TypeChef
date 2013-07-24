package de.fosd.typechef.crewrite

import org.kiama.attribution.AttributionBase

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.UseDeclMap
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureModel, FeatureExpr}

// this abstract class provides a standard implementation of
// the monotone framework, a general framework for dataflow analyses
// such as liveness, available expression, double-free, ...
// in contrast to the original, this implementation is variability-aware
// and is able to handle variable shadowing
//
// the analysis itself is not precise, i.e., during the computation the feature
// model is not used, especially for the predecessor and successor determination.
// the reason is, the predecessor/successor determination requires a lot of sat
// calls. for larger case studies (e.g., linux) a single sat-call takes 1/2 second
// and therefore predecessor/successor determination only takes the #ifdef annotations
// in the files into consideration
//
// if a proper feature model is passed to the constructor of this class, we use
// it to filter out false positives due to the imprecise predecessor and successor
// determination
//
// for more information about monotone frameworks
// see "Principles of Program Analysis" by (Nielson, Nielson, Hankin) [NNH99]

// env: ASTEnv; environment used for navigation in the AST during predecessor and
//              successor determination
// fm: FeatureModel; feature model used for filtering out false positives
abstract class MonotoneFW[T](val env: ASTEnv, val fm: FeatureModel) extends AttributionBase with IntraCFG {

    // dataflow, such as identifiers (type Id) may have different declarations
    // (alternative types). so we track alternative elements here using two
    // maps
    // t2FreshT is a 1:1 mapping of T declarations to fresh T elements for our analysis;
    //          fresh means we use a new identifier that does not exist yet.
    // freshT2T is the reverse of t2FreshT
    private val t2FreshT = new java.util.IdentityHashMap[T, Set[T]]()
    private val dId2Fresh = new java.util.IdentityHashMap[T, T]()
    private val freshT2T = new java.util.IdentityHashMap[T, T]()

    // create fresh T elements that we use in our analysis
    protected def createFresh(i: T) = {
        if (!dId2Fresh.containsKey(i)) {
            val nt = t2T(i)
            dId2Fresh.put(i, nt)
        }
        dId2Fresh.get(i)
    }

    protected def getFresh(i: T) = {
        if (!t2FreshT.containsKey(i)) {
            val nt = t2SetT(i)
            for (e <- nt)
                freshT2T.put(e, i)
            t2FreshT.put(i, nt)
        }
        t2FreshT.get(i)
    }

    protected def addFreshT(i: T) = {
        val nt = createFresh(i)
        freshT2T.put(nt, i)
        nt
    }

    // get the original T element of a freshly created T element
    protected def getOriginal(i: T) = freshT2T.get(i)

    // abstract function that creates the fresh T elements we use here
    // in our analysis. typical for analysis such as double free
    // we create new Id using the freshTctr counter.
    protected def t2SetT(i: T): Set[T]
    protected def t2T(i: T): T
    protected def createLFromSetT(s: L): L


    protected val exitcache = new IdentityHashMapCache[L]()
    protected val entrycache = new IdentityHashMapCache[L]()

    // gen and kill function, will be implemented by the concrete dataflow class
    def gen(a: AST): L
    def kill(a: AST): L

    // while monotone framework usually works on Sets
    // we use maps here for efficiency reasons:
    //   1. The obvious shift from non-variability-aware monotone framework to
    //      a variability-aware version is to change the type of the result set
    //      from Set[Id] to Set[Opt[Id]]. However this changes involves many lookups
    //      and changes to the set.
    //   2. We use Map[T, FeatureExpr] since T is our basic element of interest.
    //      FeatureExpr do not matter so far (they are prominent when using Opt
    //      nodes with List or Set). Since T matters operations on feature expression
    //      are easy and can be delayed to the point at which we *really* need
    //      the result. The delay also involves simplifications of feature
    //      expressions such as "a or (not a) => true".
    //   3. In general, Map[Id, FeatureExpr] is the generalization of Set[Id] with
    //      FeatureExpr in {True, False}. Therefore, we adapopt a Map here instead of a Set.
    //
    // property space L represents a complete lattice, i.e., it is a partially ordered set (L,⊑)
    // ⊑ is either ⊆ (subset) or ⊇ (superset)
    // according to the ascending chain condition (see [NNH99], Appendix A)
    // l1 ⊑ l2 ⊑ ... eventually stabilises (finite set of T elements in analysis), i.e., ∃n: ln = ln+1 = ...
    type L = Map[T, FeatureExpr]

    // preserve the generic type of the MonotonFW class, so that it is available in subclasses
    type PGT = T
    protected def l = Map[T, FeatureExpr]()

    private def diff(l: L, l2: L): L = {
        var curl = l
        for ((e, fexp) <- l2) {
            curl.get(e) match {
                case None =>
                case Some(x) => {
                    if (fexp.not and x isContradiction()) curl = curl -e
                    else curl = curl + ((e, fexp.not and x))
                }
            }
        }
        curl
    }

    protected def intersection(l1: L, l2: L): L = {
        var curl = l1
        val k1 = l1.keySet
        val k2 = l2.keySet
        curl --= ((k1 union k2) diff (k1 intersect k2))
        for ((e, fexp) <- l2) {
            curl.get(e) match {
                case None =>
                case Some(x) => curl = curl + ((e, fexp and x))
            }
        }
        curl
    }

    protected def union(l: L, l2: L): L = {
        var curl = l
        for (i@(e, fexp) <- l2) {
            curl.get(e) match {
                case None => curl = curl + i
                case Some(x) => curl = curl + ((e, fexp or x))
            }
        }
        curl
    }


    // finite flow F (flow => succ and flowR => pred)
    // beware [NNH99] define the analysis: Analysis_○(l) = ∐{Analysis_●(l') | (l',l) ∈ F} ⊔ i_E^l
    // so they reverse the order going from l to l' (notice that (l',l) ∈ F)
    //  flow  => predecessor (forward analysis)
    //           result elements flow from predecessor to successor
    //  flowR => successor (backward analysis);
    //           result elements flow from successor to predecessor
    // we don't reverse the order here but keep the names for flow and flowR consistent with the book
    protected def F(e: AST): CFG
    protected def flow(e: AST): CFG = pred(e, FeatureExprFactory.empty, env)
    protected def flowR(e: AST): CFG = succ(e, FeatureExprFactory.empty, env)

    // we compute the flow on the fly and FunctionDef represents our only element in E (extremal labels),
    // i.e., FunctionDef is the "last" and the "first" element (label) in flow resp. flowR (see F definition)
    private type E = FunctionDef

    // extremal value for E
    protected val i: L

    // bottom element of our lattice ⊥; we can't use ⊥ as a variable name
    // this needs to be a function since we have to create each time a new, fresh bottom element
    // otherwise the fix-point computation in circular ends up with a NullPointerException
    protected def b: L

    // dataflow computations based on the monotone framework use a fix-point algorithm
    // for the implementation we use kiama's circular function, which keeps track
    // of the results
    // the framework defines two functions:
    //   Analysis_○(l) = ∐{Analysis_●(l') | (l',l) ∈ F} ⊔ i_E^l
    //                   where i_E^l = i if l ∈ E
    //                     or  i_E^l = ⊥ if l ∉ E
    //   Analysis_●(l) = f_l(Analysis_○(l))
    //
    // for forward analysis such as available expressions, F is flow, Analysis_○ concerns entry, Analysis_● concerns exit
    // for backward analysis such as liveness analysis, F is flowR, Analysis_○ concerns exit, Analysis_● concerns entry
    // we name the two functions ∐ and f_l in Analysis_○ and Analysis_● combinator and f_l
    // we name Analysis_○ circle and Analysis_● point

    // ∐ is either ⋃ (n-ary union) or ⋂ (n-ary intersection) and has to be defined by the analysis instance
    protected def combinationOperator(l1: L, l2: L): L

    // depending on the kind of analysis circle and point are defined in a different way
    // forward analysis: F is flow; Analysis_○ (circle) concerns entry conditions; Analysis_● concerns exit conditions
    // backward analysis: F is flowR; Analysis_○ (circle) concerns exit conditions; Analysis_● concerns entry conditions
    // flow is always from ○ to ●; and therefore we map entry/exit (which are fix) either to ○ or ●
    //          entry   |  ○             ∧  ●
    //    x++;          | flow           | flowR
    //          exit    ∨  ●             |  ○
    // circle and point use entrycache and exitcache for efficiency reasons
    protected def circle(e: AST): L
    protected def point(e: AST): L

    protected val combinator: AST => L = {
        circular[AST, L](b) {
            case _: E => i
            case a => {
                val fl = F(a)

                var res = b
                for (s <- fl)
                    res = combinationOperator(res, point(s.entry))
                res
            }
        }
    }

    protected val f_l: AST => L = {
        circular[AST, L](b) {
            case _: E => i
            case a => {
                val g = gen(a)
                val k = kill(a)

                var res = circle(a)
                res = diff(res, createLFromSetT(k))

                res = union(res, createLFromSetT(g))
                res
            }
        }
    }

    // using caching for efficiency
    protected def entrycache(a: AST): L = {
        entrycache.lookup(a) match {
            case Some(v) => v
            case None => {
                val r = combinator(a)
                entrycache.update(a, r)
                r
            }
        }
    }

    protected def outcache(a: AST): L

    def out(a: AST) = {
        val o = outcache(a)
        var res = List[(T, FeatureExpr)]()
        for ((x, f) <- o) {
            val orig = getOriginal(x)
            res = (orig, f) :: res
        }
        // TODO the distinct call should not be necessary
        res.distinct.filter(_._2.isSatisfiable(fm))
    }

    protected def exitcache(a: AST): L = {
        exitcache.lookup(a) match {
            case Some(v) => v
            case None => {
                val r = f_l(a)
                exitcache.update(a, r)
                r
            }
        }
    }

    protected def incache(a: AST): L
    def in(a: AST) = {
        val o = incache(a)
        var res = List[(T, FeatureExpr)]()

        for ((x, f) <- o) {
            val orig = getOriginal(x)
            res = (orig, f) :: res
        }
        // TODO the distinct call should not be necessary
        res.distinct.filter(_._2.isSatisfiable(fm))
    }
}

// specialization of MonotoneFW for Ids (Var); helps to reduce code cloning, i.e., cloning of t2T, ...
abstract class MonotoneFWId(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel) extends MonotoneFW[Id](env, fm) {
    // add annotation to elements of a Set[PGT]
    // this function is used to add feature expression information to set generated by gen and kill
    protected def addAnnotations(in: Set[PGT]): L = {
        var res = l

        for (r <- in)
            res += ((r, env.featureExpr(r)))

        res
    }

    // we create fresh T elements (here Id) using a counter
    private var freshTctr = 0

    private def getFreshCtr: Int = {
        freshTctr = freshTctr + 1
        freshTctr
    }

    def t2T(i: Id) = Id(getFreshCtr + "_" + i.name)

    def t2SetT(i: Id) = {
        var freshidset = Set[Id]()

        if (udm.containsKey(i)) {
            for (vi <- udm.get(i)) {
                freshidset = freshidset.+(createFresh(vi))
            }
            freshidset
        } else {
            Set(addFreshT(i))
        }
    }

    protected def createLFromSetT(s: L): L = {
        var res = l

        for ((x, f) <- s)
            for (n <- getFresh(x))
                res += ((n, f))
        res
    }

    protected def circle(e: AST) = entrycache(e)
    protected def point(e: AST) = exitcache(e)

    protected def incache(a: AST): L = exitcache(a)
    protected def outcache(a: AST): L = entrycache(a)
}

// specialization of MonotoneFW for Ids with Labels (Var x Lab)
// we use a label System.identityHashCode of the tracked variables
abstract class MonotoneFWIdLab(env: ASTEnv, fm: FeatureModel) extends MonotoneFW[(Id, Int)](env, fm) {
    // add annotation to elements of a Set[PGT]
    // this function is used to add feature expression information to set generated by gen and kill
    protected def addAnnotations(in: Set[PGT]): L = {
        var res = l

        for (r <- in)
            res += ((r, env.featureExpr(r._1)))

        res
    }

    def t2T(i: PGT) = i

    def t2SetT(i: PGT) = Set(addFreshT(i))

    protected def createLFromSetT(s: L): L = {
        for ((x, _) <- s)
            getFresh(x)
        s
    }

    protected def circle(e: AST) = entrycache(e)
    protected def point(e: AST) = exitcache(e)

    protected def incache(a: AST): L = entrycache(a)
    protected def outcache(a: AST): L = exitcache(a)
}

class IdentityHashMapCache[A] {
    private val cache: java.util.IdentityHashMap[Any, A] = new java.util.IdentityHashMap[Any, A]()
    def update(k: Any, v: A) { cache.put(k, v) }
    def lookup(k: Any): Option[A] = {
        val v = cache.get(k)
        if (v != null) Some(v)
        else None
    }
}
