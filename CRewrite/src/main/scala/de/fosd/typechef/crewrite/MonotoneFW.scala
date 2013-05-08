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

    // since C allows variable shadowing we need to track variable usages
    // to their corresponding declarations

    protected def id2SetT(i: Id): Set[T]

    protected val entry_cache = new IdentityHashMapCache[Map[T, FeatureExpr]]()
    protected val exit_cache = new IdentityHashMapCache[Map[T, FeatureExpr]]()

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
                case None    => curmap = curmap.+((e, fexp))
                case Some(x) => curmap = curmap.+((e, fexp or x))
            }
        }
        curmap
    }

    private val analysis_entry: AST => Map[T, FeatureExpr] = {
        circular[AST, Map[T, FeatureExpr]](Map[T, FeatureExpr]()) {
            case FunctionDef(_, _, _, _) => Map()
            case t => {
                val g = gen(t)
                val k = kill(t)

                var res = out(t)
                for ((_, v) <- k) res = diff(res, v)

                for ((fexp, v) <- g) res = join(res, fexp, v)
                res
            }
        }
    }

    // flow functions (flow => succ and flow_R) functions of the
    // framework directly encoded in analysis_exit_backward
    // and analysis_exit_forward
    protected val analysis_exit: AST => Map[T, FeatureExpr]

    protected val analysis_exit_backward: AST => Map[T, FeatureExpr] =
        circular[AST, Map[T, FeatureExpr]](Map[T, FeatureExpr]()) {
            case e => {
                var ss = succ(e, FeatureExprFactory.empty, env)

                ss = ss.filterNot(x => x.entry.isInstanceOf[FunctionDef])

                var res = Map[T, FeatureExpr]()
                for (s <- ss) {
                    for ((r, f) <- in(s.entry))
                        res = join(res, f and s.feature, Set(r))
                }
                res
            }
        }

    protected val analysis_exit_forward: AST => Map[T, FeatureExpr] =
        circular[AST, Map[T, FeatureExpr]](Map[T, FeatureExpr]()) {
            case e => {
                var ss = pred(e, FeatureExprFactory.empty, env)

                ss = ss.filterNot(x => x.entry.isInstanceOf[FunctionDef])
                var res = Map[T, FeatureExpr]()
                for (s <- ss) {
                    for ((r, f) <- in(s.entry))
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

    def out(a: AST) = exit(a).filter(_._2.isSatisfiable(fm))

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

    def in(a: AST) = entry(a).filter(_._2.isSatisfiable(fm))
}
