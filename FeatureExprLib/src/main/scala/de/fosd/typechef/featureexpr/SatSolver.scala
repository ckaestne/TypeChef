package de.fosd.typechef.featureexpr

import org.sat4j.core.VecInt
import collection.mutable.WeakHashMap

import org.sat4j.minisat.SolverFactory
import org.sat4j.specs.{IVecInt, IConstr, ContradictionException}
;


/**
 * connection to the SAT4j SAT solver
 *
 * may reuse instance
 */

object SatSolver {
    /**
     * caching can reuse SAT solver instances, but experience
     * has shown that it can lead to incorrect results,
     * hence caching is currently disabled
     */
    val CACHING = true
    def isSatisfiable(featureModel: FeatureModel, dnf: Iterator[Seq[Int]], lookupName: (Int) => String): Boolean = {
        (if (CACHING && (nfm(featureModel) != NoFeatureModel))
            SatSolverCache.get(nfm(featureModel))
        else
            new SatSolverImpl(nfm(featureModel))).isSatisfiable(dnf, lookupName)
    }

    private def nfm(fm: FeatureModel) = if (fm == null) NoFeatureModel else fm
}

private object SatSolverCache {
    val cache: WeakHashMap[FeatureModel, SatSolverImpl] = new WeakHashMap()
    def get(fm: FeatureModel) =
    /*if (fm == NoFeatureModel) new SatSolverImpl(fm)
   else */ cache.getOrElseUpdate(fm, new SatSolverImpl(fm))
}

class SatSolverImpl(featureModel: FeatureModel) {
    assert(featureModel != null)

    /**init / constructor */
    val solver = SolverFactory.newDefault();
    //        solver.setTimeoutMs(1000);
    solver.setTimeoutOnConflicts(100000)

    solver.addAllClauses(featureModel.clauses)
    var uniqueFlagIds: Map[String, Int] = featureModel.variables


    /**
     * checks whether (fm and dnf.not) is satisfiable
     *
     * dnf is the disjunctive normal form of the negated(!) expression
     */
    def isSatisfiable(dnf: Iterator[Seq[Int]], lookupName: (Int) => String): Boolean = {


        val PROFILING = false



        val startTime = System.currentTimeMillis();

        if (PROFILING)
            print("<SAT " + dnf.length + "; ")

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

            val dfnl = dnf.toList

            try {
                for (clause <- dfnl) {
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


            if (PROFILING)
                print(result + ";")
            return result
        } finally {
            for (constr <- constraintGroup.filter(_ != null))
                assert(solver.removeConstr(constr))

            if (PROFILING)
                println(" in " + (System.currentTimeMillis() - startTimeSAT) + " ms>")
        }
    }
}
