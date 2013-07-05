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
    // t2FreshT is a 1:1 mapping of T declarations to fresh T elements for our analysis
    // freshT2T is the reverse of t2FreshT
    private val t2FreshT = new java.util.IdentityHashMap[T, Set[T]]()
    private val dId2Fresh = new java.util.IdentityHashMap[T, T]()
    private val freshT2T = new java.util.IdentityHashMap[T, T]()

    // create fresh T elements that we use in our analysis the declaration
    // of an incoming element
    protected def createFresh(i: T) = {
        if (! dId2Fresh.containsKey(i)) {
            val nt = t2T(i)
            dId2Fresh.put(i, nt)
        }
        dId2Fresh.get(i)
    }

    protected def getFresh(i: T) = {
        if (! t2FreshT.containsKey(i)) {
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



    protected val incache = new IdentityHashMapCache[ResultMap]()
    protected val outcache = new IdentityHashMapCache[ResultMap]()

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
    type ResultMap = Map[T, FeatureExpr]

    private def diff(map: ResultMap, fexp: FeatureExpr, d: Set[T]) = {
        var curmap = map
        for (e <- d) {
            curmap.get(e) match {
                case None =>
                case Some(x) => {
                    if (fexp.not and x isContradiction())
                        curmap = curmap.-(e)
                    else
                        curmap = curmap + ((e, fexp.not and x))
                }
            }
        }
        curmap
    }

    private def union(map: ResultMap, fexp: FeatureExpr, j: Set[T]) = {
        var curmap = map
        for (e <- j) {
            curmap.get(e) match {
                case None => curmap = curmap.+((e, fexp))
                case Some(x) => curmap = curmap.+((e, fexp or x))
            }
        }
        curmap
    }



    // flow functions (flow => succ and flow => pred) functions of the
    // framework
    protected def flow(e: AST): CFG
    protected def flowSucc(e: AST): CFG = succ(e, FeatureExprFactory.empty, env)
    protected def flowPred(e: AST): CFG = pred(e, FeatureExprFactory.empty, env)

    protected def unionio(e: AST): ResultMap
    protected def genkillio(e: AST): ResultMap

    protected val uniononly: AST => ResultMap = {
        circular[AST, ResultMap](Map[T, FeatureExpr]()) {
            case e => {
                var fl = flow(e)

                fl = fl.filterNot(x => x.entry.isInstanceOf[FunctionDef])

                var res = Map[T, FeatureExpr]()
                for (s <- fl) {
                    for ((r, f) <- unionio(s.entry))
                        res = union(res, f and s.feature, Set(r))
                }
                res
            }
        }
    }

    protected val genkill: AST => ResultMap = {
        circular[AST, ResultMap](Map[T, FeatureExpr]()) {
            case FunctionDef(_, _, _, _) => Map[T, FeatureExpr]()
            case t => {
                val g = gen(t)
                val k = kill(t)

                var res = genkillio(t)
                for ((fexp, v) <- k)
                    for (x <- v)
                        res = diff(res, fexp, getFresh(x))

                for ((fexp, v) <- g)
                    for (x <- v)
                        res = union(res, fexp, getFresh(x))
                res
            }
        }
    }

    // using caching for efficiency and filtering out false positives
    // using the feature model
    protected def outcached(a: AST) = {
        outcache.lookup(a) match {
            case Some(v) => v
            case None => {
                val r = uniononly(a)
                outcache.update(a, r)
                r
            }
        }
    }

    def out(a: AST) = {
        val o = outcached(a)
        var res = List[(T, FeatureExpr)]()

        for ((x, f) <- o) {
            val orig = getOriginal(x)
            res = (orig, f) :: res
        }
        res.filter(_._2.isSatisfiable(fm))
    }

    protected def incached(a: AST) = {
        incache.lookup(a) match {
            case Some(v) => v
            case None => {
                val r = genkill(a)
                incache.update(a, r)
                r
            }
        }
    }

    def in(a: AST) = {
        val o = incached(a)
        var res = List[(T, FeatureExpr)]()

        for ((x, f) <- o) {
            val orig = getOriginal(x)
            res = (orig, f) :: res
        }
        res.filter(_._2.isSatisfiable(fm))
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
}
