package de.fosd.typechef.crewrite

import org.kiama.attribution.AttributionBase

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.UseDeclMap
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureModel, FeatureExpr}
import scala.collection.immutable.HashMap

// this abstract class provides a standard implementation of
// the monotone framework, a general framework for dataflow analyses
// such as liveness, available expression, double-free, ...
// in contrast to the original, this implementation is variability-aware
// and is able to handle variable shadowing
//
// the analysis itself is not precise, i.e., during the computation the feature
// model is not used, especially for the predecessor and successor determintion.
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
// see "Principles of Program Analysis" by (Nielson, Nielson, Hankin)

// env: ASTEnv; environment used for navigation in the AST during predecessor and
//              successor determination
// udm: UseDeclMap; map of variable usages to their corresponding declarations
//                  a variable here can have different declarations (alternative types)
// fm: FeatureModel; feature model used for filtering out false positives
abstract class MonotoneFW[T](val env: ASTEnv, val udm: UseDeclMap, val fm: FeatureModel) extends AttributionBase with IntraCFG {

    // dataflow, such as identifiers (type Id) may have different declarations
    // (alternative types). so we track alternative elements here using two
    // maps
    // t2FreshT is a 1:N mapping of T usages to fresh elements in out analysis
    // freshT2T is the reverse of t2FreshT
    private val t2FreshT = new java.util.IdentityHashMap[T, Set[T]]()
    private val freshT2T = new java.util.IdentityHashMap[T, T]()

    // get fresh T elements that we use in our analysis for an incoming T element
    def getFresh(i: T) = {
        if (! t2FreshT.containsKey(i)) {
            var freshidset = t2SetT(i)
            for (fi <- freshidset)
                freshT2T.put(fi, i)
            t2FreshT.put(i, freshidset)
        }
        t2FreshT.get(i)
    }

    // get the original T element of a freshly created T element
    def getOriginal(i: T) = freshT2T.get(i)

    // abstract function that creates the fresh T elements we use here
    // in our analysis. typical for analysis such as double free
    // we create new Id using the freshTctr counter.
    protected def t2SetT(i: T): Set[T]

    class IdentityHashMapCache[A] {
        private val cache: java.util.IdentityHashMap[Any, A] = new java.util.IdentityHashMap[Any, A]()
        def update(k: Any, v: A) { cache.put(k, v) }
        def lookup(k: Any): Option[A] = {
            val v = cache.get(k)
            if (v != null) Some(v)
            else None
        }
    }

    protected val entry_cache = new IdentityHashMapCache[ResultMap]()
    protected val exit_cache = new IdentityHashMapCache[ResultMap]()

    // add annotation to elements of a Set[Id]
    protected def addAnnotation2ResultSet(in: Set[Id]): Map[FeatureExpr, Set[Id]] = {
        var res = Map[FeatureExpr, Set[Id]]()

        for (r <- in) {
            val rfexp = env.featureExpr(r)

            val key = res.find(_._1 equivalentTo rfexp)
            key match {
                case None => res = res.+((rfexp, Set(r)))
                case Some((k, v)) => res = res.+((k, v ++ Set(r)))
            }
        }

        res
    }

    // gen and kill function, will be implemented by the concrete dataflow classes
    def gen(a: AST): Map[FeatureExpr, Set[T]]
    def kill(a: AST): Map[FeatureExpr, Set[T]]

    // while monotone framework usually works on Sets
    // we use maps here for efficiency reasons:
    //   1. the obvious shift from non-variability-aware monotone framework to
    //      a variability-aware version is to change the type of the result set
    //      from Set[Id] to Set[Opt[Id]]. However this changes involves many lookups
    //      and changes to the set.
    //   2. We use Map[T, FeatureExpr] since T is our basic element of interest.
    //      FeatureExpr do not matter so far (they are prominent when using Opt
    //      nodes with List or Set). Since T matters operations on feature expression
    //      are easy and can be delayed to the point at which we *really* need
    //      the result. The delay also involves simplifications of feature
    //      expressions such as "a or (not a) => true".
    type ResultMap = HashMap[T, FeatureExpr]

    private def diff(map: ResultMap, d: Set[T]) = {
        var curmap = map
        for (e <- d)
            curmap = curmap.-(e)
        curmap
    }

    private def join(map: ResultMap, fexp: FeatureExpr, j: Set[T]) = {
        var curmap = map
        for (e <- j) {
            curmap.get(e) match {
                case None => curmap = curmap.+((e, fexp))
                case Some(x) => curmap = curmap.+((e, fexp or x))
            }
        }
        curmap
    }

    private val analysis_entry: AST => ResultMap = {
        circular[AST, ResultMap](HashMap[T, FeatureExpr]()) {
            case FunctionDef(_, _, _, _) => HashMap[T, FeatureExpr]()
            case t => {
                val g = gen(t)
                val k = kill(t)

                var res = exit(t)
                for ((_, v) <- k)
                    for (x <- v)
                        res = diff(res, getFresh(x))

                for ((fexp, v) <- g)
                    for (x <- v)
                        res = join(res, fexp, getFresh(x))
                res
            }
        }
    }

    // flow functions (flow => succ and flow_R) functions of the
    // framework directly encoded in analysis_exit_backward
    // and analysis_exit_forward
    protected val analysis_exit: AST => ResultMap

    protected val analysis_exit_backward: AST => ResultMap =
        circular[AST, ResultMap](HashMap[T, FeatureExpr]()) {
            case e => {
                var ss = succ(e, FeatureExprFactory.empty, env)

                ss = ss.filterNot(x => x.entry.isInstanceOf[FunctionDef])

                var res = HashMap[T, FeatureExpr]()
                for (s <- ss) {
                    for ((r, f) <- entry(s.entry))
                        res = join(res, f and s.feature, Set(r))
                }
                res
            }
        }

    protected val analysis_exit_forward: AST => ResultMap =
        circular[AST, ResultMap](HashMap[T, FeatureExpr]()) {
            case e => {
                var ss = pred(e, FeatureExprFactory.empty, env)

                ss = ss.filterNot(x => x.entry.isInstanceOf[FunctionDef])
                var res = HashMap[T, FeatureExpr]()
                for (s <- ss) {
                    for ((r, f) <- entry(s.entry))
                        res = join(res, f and s.feature, Set(r))
                }
                res
            }
        }

    // using caching for efficiency and filtering out false positives
    // using the feature model
    private def exit(a: AST) = {
        exit_cache.lookup(a) match {
            case Some(v) => v
            case None => {
                val r = analysis_exit(a)
                exit_cache.update(a, r)
                r
            }
        }
    }

    def out(a: AST) = {
        val o = exit(a)
        var res = List[(T, FeatureExpr)]()

        for ((x, f) <- o) {
            val orig = getOriginal(x)
            res = (orig, f) :: res
        }
        res.filter(_._2.isSatisfiable(fm)).distinct
    }

    private def entry(a: AST) = {
        entry_cache.lookup(a) match {
            case Some(v) => v
            case None => {
                val r = analysis_entry(a)
                entry_cache.update(a, r)
                r
            }
        }
    }

    def in(a: AST) = {
        val o = entry(a)
        var res = List[(T, FeatureExpr)]()

        for ((x, f) <- o) {
            val orig = getOriginal(x)
            res = (orig, f) :: res
        }
        res.filter(_._2.isSatisfiable(fm)).distinct
    }
}
