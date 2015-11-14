package de.fosd.typechef.featureexpr.bdd

import org.sat4j.core.VecInt
import collection.mutable.WeakHashMap

import org.sat4j.minisat.SolverFactory
import org.sat4j.specs.{IConstr, ContradictionException}
import de.fosd.typechef.featureexpr.SingleFeatureExpr
;


/**
 * connection to the SAT4j SAT solver
 *
 * may reuse instance
 */

object SatSolver {


    /**
     * takes two sets of strings as parameter (one for true variables and one for false variables).
     * Computes an assignment that satisfies the configuration.
     * Assuming that all variable names appear in the featureModel.
     *
     */
    def getSatisfiableAssignmentFromStringSets(fm: BDDFeatureModel, interestingFeatures: Set[SingleFeatureExpr],
                                               defEnabledFeatures: Set[String], defDisabledFeatures: Set[String],
                                               preferDisabledFeatures: Boolean): Option[(List[SingleFeatureExpr], List[SingleFeatureExpr])] = {
        val bddDNF = Set(
            (defEnabledFeatures.map(fm.variables(_)) ++ defDisabledFeatures.map(-fm.variables(_)))
                .toSeq).iterator

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
     * caching can reuse SAT solver instances, but experience
     * has shown that it can lead to incorrect results,
     * hence caching is currently disabled
     */
    val CACHING = true

    def isSatisfiable(featureModel: BDDFeatureModel, dnf: Iterator[Seq[Int]], lookupName: (Int) => String): Boolean = {
        (if (CACHING && (nfm(featureModel) != BDDNoFeatureModel))
            SatSolverCache.get(nfm(featureModel))
        else
            new SatSolverImpl(nfm(featureModel))).isSatisfiable(dnf, lookupName)
    }

    /**
     * Basically a clone of isSatisfiable(..) that also returns the satisfying assignment (if available).
     * The return value is a Pair where the first element is a list of the feature names set to true.
     * The second element is a list of feature names set to false.
     */
    def getSatAssignment(featureModel: BDDFeatureModel, dnf: Iterator[Seq[Int]], lookupName: (Int) => String): Option[(List[String], List[String])] = {
        val solver =
            (if (CACHING && (nfm(featureModel) != BDDNoFeatureModel))
                SatSolverCache.get(nfm(featureModel))
            else
                new SatSolverImpl(nfm(featureModel)))

        if (solver.isSatisfiable(dnf, lookupName)) {
            return Some(solver.getLastModel())
        } else {
            return None
        }
    }


    private def nfm(fm: BDDFeatureModel) = if (fm == null) BDDNoFeatureModel else fm
}

private object SatSolverCache {
    val cache: WeakHashMap[BDDFeatureModel, SatSolverImpl] = new WeakHashMap()

    def get(fm: BDDFeatureModel) = {
        val c = cache.get(fm)
        if (c.isDefined && !c.get.invalid)
            c.get
        else {
            val s = new SatSolverImpl(fm)
            cache.put(fm, s)
            s
        }
    }
}

class SatSolverImpl(featureModel: BDDFeatureModel) {
    assert(featureModel != null)
    val PROFILING = false

    /** init / constructor */
    val solver = SolverFactory.newDefault()
    solver.setTimeoutMs(10000)
//    solver.setTimeoutOnConflicts(100000)

    solver.addAllClauses(featureModel.clauses)
    var uniqueFlagIds: Map[String, Int] = featureModel.variables

    /**
     * checks whether (fm and dnf.not) is satisfiable
     *
     * dnf is the disjunctive normal form of the negated(!) expression
     */
    def isSatisfiable(dnf: Iterator[Seq[Int]], lookupName: (Int) => String): Boolean = {
        this.lastModel = null // remove model from last satisfiability check


        val startTime = System.currentTimeMillis();

        val dnfl = dnf.toList
        if (PROFILING)
            print("<SAT " + dnfl.length + "; ")

        val startTimeSAT = System.currentTimeMillis();
        var constraintGroup: List[IConstr] = Nil
        try {

            def bddId2fmId(id: Int) = {
                val featureName = lookupName(id)
                uniqueFlagIds.get(featureName) match {
                    case Some(fmId) => fmId
                    case None =>
                        uniqueFlagIds = uniqueFlagIds + ((featureName, uniqueFlagIds.size + 1))
                        uniqueFlagIds.size
                }
            }


            try {
                for (clause <- dnfl) {
                    val clauseArray: Array[Int] = new Array(clause.size)
                    var i = 0
                    for (literal <- clause) {
                        //look up real ids. negate values in the process (dnf to cnf)
                        if (literal < 0)
                            clauseArray(i) = bddId2fmId(-literal)
                        else
                            clauseArray(i) = -bddId2fmId(literal)
                        i = i + 1;
                    }
                    val constr = solver.addClause(new VecInt(clauseArray))
                    constraintGroup = constr :: constraintGroup
                }
            } catch {
                case e: ContradictionException => return false;
            }

            val assumptions: VecInt = new VecInt(featureModel.assumedFalse.size + featureModel.assumedTrue.size)
            for (f <- featureModel.assumedTrue)
                uniqueFlagIds.get(f).map(assumptions.push(_))
            for (f <- featureModel.assumedFalse)
                uniqueFlagIds.get(f).map(id => assumptions.push(-id))

            //update max size (nothing happens if smaller than previous setting)
            solver.newVar(uniqueFlagIds.size)

            if (PROFILING)
                print(";")
            val result = solver.isSatisfiable(assumptions)
            if (result == true) {
                // scanning the model (storing the satisfiable assignment for later retrieval)
                val model = solver.model()
                var trueList: List[String] = List()
                var falseList: List[String] = List()
                for ((fName, modelID) <- uniqueFlagIds) {
                    if (solver.model(modelID))
                        trueList ::= fName
                    else
                        falseList ::= fName
                }
                lastModel = (trueList, falseList)
            }

            if (PROFILING)
                print(result + ";")
            return result
        } finally {
            for (constr <- constraintGroup.filter(_ != null)) {
                try {
                    val succeeded = solver.removeConstr(constr)
                    if (!succeeded)
                        invalidateCache()
                } catch {
                    case e: AssertionError => invalidateCache()
                    case e: NullPointerException => invalidateCache()
                }
            }

            if (PROFILING)
                println(" in " + (System.currentTimeMillis() - startTimeSAT) + " ms>")
        }
    }

    /**
     * This pair contains the model that was constructed during the last isSatisfiable call (if the result was true).
     * The first element contains the names of the features set to true, the second contains the names of the false features.
     */
    var lastModel: (List[String], List[String]) = null

    def getLastModel() = lastModel

    var invalid: Boolean = false

    def invalidateCache() {
        if (PROFILING)
            println("<invalidating SAT cache>")
        invalid = true
    }
}
