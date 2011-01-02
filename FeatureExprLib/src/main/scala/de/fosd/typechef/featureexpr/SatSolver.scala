package de.fosd.typechef.featureexpr

import org.sat4j.core.VecInt
import collection.mutable.WeakHashMap
import org.sat4j.tools.ConstrGroup
;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;


/**
 * connection to the SAT4j SAT solver
 *
 * may reuse instance
 */

class SatSolver {
    def isSatisfiable(exprCNF: NF, featureModel: FeatureModel = NoFeatureModel): Boolean = {
        SatSolverCache.get(nfm(featureModel)).isSatisfiable(exprCNF)
    }

    private def nfm(fm: FeatureModel) = if (fm == null) NoFeatureModel else fm
}

private object SatSolverCache {
    val cache: WeakHashMap[FeatureModel, SatSolverImpl] = new WeakHashMap()
    def get(fm: FeatureModel) =
    /*if (fm == NoFeatureModel) new SatSolverImpl(fm)
   else */ cache.getOrElseUpdate(fm, new SatSolverImpl(fm))
}

private class SatSolverImpl(featureModel: FeatureModel) {
    val PROFILING = false

    /**init / constructor */
    val solver = SolverFactory.newDefault();
    //        solver.setTimeoutMs(1000);
    solver.setTimeoutOnConflicts(100000)

    assert(featureModel != null)
    solver.addAllClauses(featureModel.clauses)
    var uniqueFlagIds: Map[String, Int] = featureModel.variables


    /**
     * determines whether
     * (exprCNF AND featureModel) is satisfiable
     *
     * featureModel is optional
     */
    def isSatisfiable(exprCNF: NF): Boolean = {
        if (exprCNF.isEmpty) return true
        if (exprCNF.isFull) return false
        //as long as we do not consider feature models, expressions with a single variable 
        //are always satisfiable
        if ((featureModel == NoFeatureModel) && (exprCNF.isAtomic)) return true

        val startTime = System.currentTimeMillis();

        if (PROFILING)
            print("<SAT " + countClauses(exprCNF) + " with " + countFlags(exprCNF) + " flags ")

        val startTimeSAT = System.currentTimeMillis();
        try {


            //find used macros, combine them by common expansion
            val cnfs: List[NF] = prepareFormula(exprCNF)

            if (PROFILING)
                print(";")
            for (cnf <- cnfs; clause <- cnf.clauses)
                for (literal <- (clause.posLiterals ++ clause.negLiterals))
                    if (!uniqueFlagIds.contains(literal.satName))
                        uniqueFlagIds = uniqueFlagIds + ((literal.satName, uniqueFlagIds.size + 1))
            if (PROFILING)
                print(";" + cnfs.size + "-" + uniqueFlagIds.size)

            //update max size (nothing happens if smaller than previous setting)
            solver.newVar(uniqueFlagIds.size)

            /**
             * to reuse SAT solver state, use the following strategy for adding
             * (adopted from Thomas Thuems solution in FeatureIDE):
             *
             * clauses with only a single literal are added to "unit" and can be
             * checked as paramter to isSatisfiable
             * all other clauses are added to the Solver but remembered in "constraintGroup"
             * which is removed from the solver at the end
             */


            val constraintGroup = new ConstrGroup();
            try {
                val unit = new VecInt();
                try {
                    for (cnf <- cnfs; clause <- cnf.clauses; if !clause.isEmpty) {
                        if (clause.isAtomic)
                            unit.push(getAtomicId(uniqueFlagIds, clause))
                        else {
                            val constr = solver.addClause(getClauseVec(uniqueFlagIds, clause))
                            if (constr != null)
                                constraintGroup.add(constr)
                        }
                    }
                } catch {
                    case e: ContradictionException => return false;
                }

                if (PROFILING)
                    print(";")

                return solver.isSatisfiable(unit)
            } finally {
                constraintGroup.removeFrom(solver)
            }
        } finally {
            if (PROFILING)
                println(" in " + (System.currentTimeMillis() - startTimeSAT) + " ms>")
        }
    }

    private def countClauses(expr: NF) = expr.clauses.size

    private def countFlags(expr: NF) = {
        var flags = Set[String]()
        for (clause <- expr.clauses)
            for (literal <- (clause.posLiterals ++ clause.negLiterals))
                flags = flags + literal.satName
        flags.size
    }

    private def getClauseVec(uniqueFlagIds: Map[String, Int], clause: Clause) = {
        val clauseArray: Array[Int] = new Array(clause.size)
        var i = 0
        for (literal <- clause.posLiterals) {
            clauseArray(i) = uniqueFlagIds(literal.satName)
            i = i + 1;
        }
        for (literal <- clause.negLiterals) {
            clauseArray(i) = -uniqueFlagIds(literal.satName)
            i = i + 1;
        }
        new VecInt(clauseArray)
    }

    private def getAtomicId(uniqueFlagIds: Map[String, Int], clause: Clause) =
        if (clause.posLiterals.isEmpty)
            -uniqueFlagIds(clause.negLiterals(0).satName)
        else uniqueFlagIds(clause.posLiterals(0).satName)


    /**
     * DefinedExternal translates directly into a flag for the SAT solver
     *
     * DefinedMacro is more complicated, because it is equivalent to a whole formula.
     * to avoid transforming a large formula into CNF, we use the following strategy
     *
     * DefinedMacro("X",expr) expands to the following
     * DefinedExternal(freshName) -- and a new formula DefinedExternal(freshName) <=> expr
     * Both are independent clauses fed to the SAT solver
     *
     * Actually, DefinedMacro already contains an expression name <=> expr as CNF, where we
     * just need to replace the Macro name by a fresh name. 
     *
     * We first collect all expansions and detect identical ones             * 
     */
    def prepareFormula(expr: NF): List[NF] = {

        var macroExpansions: Map[String, NF] = Map()

        def prepareLiteral(literal: DefinedExpr): DefinedExpr = {
            literal match {
                case DefinedMacro(name, _, expansionName, expansionNF) => {
                    if (!macroExpansions.contains(expansionName)) {
                        if (PROFILING)
                            print(expansionName)
                        val e: NF = expansionNF.apply()
                        if (PROFILING)
                            print(":")
                        //recursively expand formula (dummy is necessary to avoid accidental recursion)
                        macroExpansions = macroExpansions + (expansionName -> new NF(true) /*dummy*/)
                        val preparedExpansion = prepareFormulaInner(e)
                        macroExpansions = macroExpansions + (expansionName -> preparedExpansion)

                        if (PROFILING)
                            print(".")
                    }
                    DefinedExternal(expansionName)
                }
                case e => e
            }
        }
        def prepareClause(clause: Clause): Clause = {
            clause.substitute(prepareLiteral(_))
        }
        def prepareFormulaInner(formula: NF): NF = {
            new NF(formula.clauses.map(prepareClause(_)), formula.isFull)
        }

        val targetExpr = prepareFormulaInner(expr)
        List(targetExpr) ++ macroExpansions.values
    }

}
