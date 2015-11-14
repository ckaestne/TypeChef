package de.fosd.typechef.crewrite

import org.kiama.attribution.Attribution.circular

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{DeclUseMap, UseDeclMap}
import de.fosd.typechef.featureexpr.{FeatureModel, FeatureExpr}
import de.fosd.typechef.conditional.Opt

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
sealed abstract class MonotoneFW[T](env: ASTEnv, val fm: FeatureModel) extends IntraCFG with CFGHelper {

    // dataflow, such as identifiers (type Id) may have different declarations
    // (alternative types). so we track alternative elements here using two
    // maps
    // t2FreshT is a 1:1 mapping of T declarations to fresh T elements for our analysis;
    //          fresh means we use a new identifier that does not exist yet.
    // freshT2T is the reverse of t2FreshT
    private val t2FreshT = new java.util.IdentityHashMap[T, Set[T]]()
    private val freshT2T = new java.util.IdentityHashMap[T, T]()

    // due to shadowing and multiple types a single variable can have,
    // we assign each variable a new identifier
    // definition 2 fresh definition
    private val dId2Freshd = new java.util.IdentityHashMap[T, T]()

    // create a fresh definition for a given definition
    protected def createFreshDefinition(i: T) = {
        if (!dId2Freshd.containsKey(i)) {
            val nt = t2T(i)
            dId2Freshd.put(i, nt)
        }
        dId2Freshd.get(i)
    }

    // add all fresh definitions of a given (usage) variable to the caches
    protected def getFreshDefinitionFromUsage(i: T) = {
        if (!t2FreshT.containsKey(i)) {
            val nt = t2SetT(i)
            for (e <- nt)
                freshT2T.put(e, i)
            t2FreshT.put(i, nt)
        }
        t2FreshT.get(i)
    }

    // add a fresh definition for a given "old" definition
    protected def getFreshDefinition(i: T) = {
        val nt = createFreshDefinition(i)
        freshT2T.put(nt, i)
        nt
    }

    // get the original T element
    protected def getOriginal(i: T) = freshT2T.get(i)

    // abstract function that creates the fresh T elements we use here
    // in our analysis.
    protected def t2SetT(i: T): Set[T]
    protected def t2T(i: T): T

    // map given elements from gen/kill to those elements maintained by the framework
    protected def mapGenKillElements2MonotoneElements(s: L): L
    private def updateFeatureExprOfMonotoneElements(s: L, f: FeatureExpr): L = {
        var res = l

        for ((x, of) <- s)
            res = res + ((x, of and f))
        res
    }

    // gen and kill function, will be implemented by the concrete dataflow analysis
    def gen(a: AST): L
    def kill(a: AST): L

    // while monotone framework usually works on Sets
    // we use maps here for efficiency reasons:
    //   1. The obvious shift from non-variability-aware monotone framework to
    //      a variability-aware version is to change the type of the result set
    //      from Set[Id] to Set[Opt[Id]]. However this changes involves many look-ups
    //      and changes to the set.
    //   2. We use Map[T, FeatureExpr] since T is our basic element of interest.
    //      FeatureExpr do not matter so far (they are prominent when using Opt
    //      nodes with List or Set). Since T matters operations on feature expression
    //      are easy and can be delayed to the point at which we *really* need
    //      the result. The delay also involves simplifications of feature
    //      expressions such as "a or (not a) => true".
    //   3. In general, Map[Id, FeatureExpr] is the generalization of Set[Id] with
    //      FeatureExpr in {True, False}. Therefore, we adopt a Map here instead of a Set.
    //
    // property space L represents a complete lattice, i.e., it is a partially ordered set (L,⊑)
    // ⊑ is either ⊆ (subset) or ⊇ (superset)
    // according to the ascending chain condition (see [NNH99], Appendix A)
    // l1 ⊑ l2 ⊑ ... eventually stabilises (finite set of T elements in analysis), i.e., ∃n: ln = ln+1 = ...
    type L = Map[T, FeatureExpr]

    // preserve the generic type of the MonotoneFW class, so that it is available in subclasses
    type PGT = T
    protected def l = Map[T, FeatureExpr]()

    private def diff(l: L, l2: L): L = {
        var curl = l
        for ((e, fexp) <- l2) {
            curl.get(e) match {
                case None =>
                case Some(x) => curl = curl + ((e, x and fexp.not))
            }
        }
        curl
    }

    protected def union(l: L, l2: L): L = {
        var curl = l
        for (i@(e, fexp) <- l2) {
            curl.get(e) match {
                case None => curl = curl + i
                case Some(x) => curl = curl + ((e, x or fexp))
            }
        }
        curl
    }

    type CPR = (Boolean, L)
    type CIR = CPR
    type POI = CPR
    type R = (CIR, POI)

    val memo = new IdentityHashMapCache[R]()


    // finite flow F (flow => succ and flowR => pred)
    // beware [NNH99] define the analysis: Analysis_○(l) = ∐{Analysis_●(l') | (l',l) ∈ F} ⊔ i_E^l
    // so they reverse the order going from l to l' (notice that (l',l) ∈ F)
    //  flow  => predecessor (forward analysis)
    //           result elements flow from predecessor to successor
    //  flowR => successor (backward analysis);
    //           result elements flow from successor to predecessor
    // we don't reverse the order here but keep the names for flow and flowR consistent with the book
    protected def F(e: AST): CFG
    protected def flow(e: AST): CFG = pred(e, env)
    protected def flowR(e: AST): CFG = succ(e, env)

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

    // depending on the analysis in and out are defined differently, although they always represent the
    // results before a CFG statement resp. after a CFG statement
    // forward analysis: F is flow (results flow from predecessor to successor elements)
    // backward analysis: F is flowR (results flow from successor to predecessor elements)
    // the side (in/out) at which the flow function is called also calls combinator (we merge results)
    // the remaining side calls f_l
    //      in          |      (combinator)  ∧       (f_l)
    //    x++;          | flow (pred)        | flowR (succ)
    //      out         ∨      (f_l)         |       (combinator)
    protected def circle(e: AST): L = combinator(e)
    protected def point(e: AST): L = f_l(e)

    protected val combinator: AST => L = {
        circular[AST, L](b) {
            case _: E => i
            case a =>
                val fl = F(a)
                fl.foldLeft[L](b)((r: L, s: Opt[AST]) => combinationOperator(r, updateFeatureExprOfMonotoneElements(point(s.entry), s.condition)))
        }
    }

    protected val f_l: AST => L = {
        circular[AST, L](b) {
            case _: E => i
            case a =>
                var res = combinator(a)
                res = diff(res, mapGenKillElements2MonotoneElements(kill(a)))

                res = union(res, mapGenKillElements2MonotoneElements(gen(a)))
                res
        }
    }

    protected def outfunction(a: AST): L

    def out(a: AST) = {
        val o = outfunction(a)

        val res: List[(T, FeatureExpr)] = o.toList.map { x => (getOriginal(x._1), x._2) }

        // joining values from different paths can lead to duplicates.
        // remove them and filter out values from unsatisfiable paths.
        res.distinct.filter { case (_, fexp) => fexp.isSatisfiable(fm) }
    }

    protected def infunction(a: AST): L

    def in(a: AST) = {
        val i = infunction(a)

        val res: List[(T, FeatureExpr)] = i.toList.map { x => (getOriginal(x._1), x._2) }
        // joining values from different paths can lead to duplicates.
        // remove them and filter out values from unsatisfiable paths.
        res.distinct.filter { case (_, fexp) => fexp.isSatisfiable(fm) }
    }
}

// specialization of MonotoneFW for Ids (Var); helps to reduce code cloning, i.e., cloning of t2T, ...
abstract class MonotoneFWId(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel) extends MonotoneFW[Id](env, fm) {
    // add annotation to elements of a List[PGT]
    // this function is used to add feature expression information to set generated by gen and kill
    protected def addAnnotations(in: List[PGT]): L = {
        var res = l

        for (r <- in)
            res = res + ((r, env.featureExpr(r)))

        res
    }

    // we create fresh T elements (here Id) using a counter
    private var freshTctr = 0

    private def getFreshCtr: Int = {
        freshTctr = freshTctr + 1
        freshTctr
    }

    protected def t2T(i: PGT) = Id(getFreshCtr + "_" + i.name)

    protected def t2SetT(i: PGT) = {
        var freshidset = Set[PGT]()

        if (udm.containsKey(i)) {
            for (vi <- udm.get(i)) {
                freshidset = freshidset.+(createFreshDefinition(vi))
            }
            freshidset
        } else {
            Set(getFreshDefinition(i))
        }
    }

    protected def mapGenKillElements2MonotoneElements(s: L): L = {
        var res = l

        for ((x, f) <- s)
            for (n <- getFreshDefinitionFromUsage(x))
                res = res + ((n, f))
        res
    }
}

// specialization of MonotoneFW for Ids with Labels (Var* x Lab*)
// we use as label System.identityHashCode of the tracked variables
abstract class MonotoneFWIdLab(env: ASTEnv, dum: DeclUseMap, udm: UseDeclMap, fm: FeatureModel) extends MonotoneFW[(Id, Int)](env, fm) {
    // add annotation to elements of a List[PGT]
    // this function is used to add feature expression information to set generated by gen and kill
    protected def addAnnotations(in: List[PGT]): L = {
        var res = l

        for (r <- in)
            res = res + ((r, env.featureExpr(r._1)))

        res
    }

    // all three functions do basically nothing, since the PGT (Id x its HashCode) input
    // is already in a form (i.e., unique elements) that can be maintained by the monotone framework
    protected def t2T(i: PGT) = i

    protected def t2SetT(i: PGT) = Set(getFreshDefinition(i))

    protected def mapGenKillElements2MonotoneElements(s: L): L = {
        // we traverse the input so all elements from s are added
        // to our internal cache t2FreshT
        for ((x, _) <- s)
            getFreshDefinitionFromUsage(x)

        // the output remains the same since freshT == T
        s
    }

    // we store PGT elements in a cache so we can return each time the same PGT elements
    // is is crucial for the computations within the monotone framework, since
    // () is an operator and returns every time a new object, with a new IdentityHashCode.
    // so (1, "1") has not the same reference as (1, "1")
    private val cachePGT = new IdentityHashMapCache[(PGT, FeatureExpr)]()

    protected def fromCache(i: Id, isKill: Boolean = false): L = {
        var res = l
        if (cachePGT.lookup(i).isEmpty) cachePGT.update(i, ((i, System.identityHashCode(i)), env.featureExpr(i)))
        res = res + cachePGT.lookup(i).get

        if (isKill) {
            if (udm.containsKey(i))
                for (x <- udm.get(i)) {
                    if (cachePGT.lookup(x).isEmpty)
                        cachePGT.update(x, ((x, System.identityHashCode(x)), env.featureExpr(x)))
                    res = res + cachePGT.lookup(x).get

                    if (dum.containsKey(x))
                        for (tu <- dum.get(x)) {
                            if (cachePGT.lookup(tu).isEmpty)
                                cachePGT.update(tu, ((tu, System.identityHashCode(tu)), env.featureExpr(tu)))
                            res = res + cachePGT.lookup(tu).get
                        }
                }
        }

        res
    }
}

class IdentityHashMapCache[A] {
    private val cache: java.util.IdentityHashMap[Any, A] = new java.util.IdentityHashMap[Any, A]()
    def update(k: Any, v: A) { cache.put(k, v) }
    def lookup(k: Any): Option[A] = {
        val v = cache.get(k)
        if (v != null) Some(v)
        else None
    }
    def keySet = cache.keySet()
}
