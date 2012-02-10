package de.fosd.typechef.featureexpr

import org.sat4j.core.VecInt
import collection.mutable.WeakHashMap
import org.sat4j.specs.IConstr;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;

/**
 * connection to the SAT4j SAT solver
 *
 * may reuse instance
 */

object SatSolver {
    /**
     * checks whether (fm and dnf.not) is satisfiable
     *
     * dnf is the disjunctive normal form of the negated(!) expression
     */
    def isSatisfiable(featureModel: FeatureModel, dnf: Iterator[Seq[Int]], lookupName: (Int) => String): Boolean = {
        assert(featureModel != null)


        val PROFILING = false

        /**init / constructor */
        val solver = SolverFactory.newDefault();
        //        solver.setTimeoutMs(1000);
        solver.setTimeoutOnConflicts(100000)


        val startTime = System.currentTimeMillis();

        if (PROFILING)
            print("<SAT " + dnf.length + "; ")

        val startTimeSAT = System.currentTimeMillis();
        try {
            solver.addAllClauses(featureModel.clauses)
            var uniqueFlagIds: Map[String, Int] = featureModel.variables


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
                    solver.addClause(new VecInt(clauseArray))
                }
            } catch {
                case e: ContradictionException => return false;
            }

            //update max size (nothing happens if smaller than previous setting)
            solver.newVar(uniqueFlagIds.size)

            if (PROFILING)
                print(";")

            val result = solver.isSatisfiable()
            if (PROFILING)
                print(result + ";")
            return result
        } finally {
            if (PROFILING)
                println(" in " + (System.currentTimeMillis() - startTimeSAT) + " ms>")
        }
    }
}
