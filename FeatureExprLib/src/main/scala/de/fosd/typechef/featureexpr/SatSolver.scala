package de.fosd.typechef.featureexpr
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ConstrGroup;
import org.sat4j.tools.ModelIterator;
import org.sat4j.tools.RemiUtils;
import org.sat4j.tools.SolutionCounter;

//object SatSolver {
//    lazy val baseFeatureClause = new Clause(Set(baseFeature),Set())
//    lazy val notBaseFeatureClause = new Clause(Set(),Set(baseFeature))
//}
class SatSolver extends Solver {
//    val baseFeatureName = "$$BASE$$"
//    lazy val baseFeature = DefinedExternal(baseFeatureName)

    private def countClauses(expr: NF) = expr.clauses.size

    private def countFlags(expr: NF) = {
        var flags = Set[String]()
        for (clause <- expr.clauses)
            for (literal <- (clause.posLiterals ++ clause.negLiterals))
                flags = flags + literal.feature
        flags.size
    }

    val PROFILING = false;

    def isSatisfiable(exprCNF: NF): Boolean = {
//    	println("SAT "+exprCNF.printCNF)
        if (exprCNF.isEmpty) return true
        if (exprCNF.isFull) return false

        val startTime = System.currentTimeMillis();

        if (PROFILING)
            println("<toCNF " + countClauses(exprCNF) + " with " + countFlags(exprCNF) + " flags>")

        val startTimeSAT = System.currentTimeMillis();
        try {

            val solver = SolverFactory.newDefault();
            //        solver.setTimeoutMs(1000);
            solver.setTimeoutOnConflicts(100000)

            var uniqueFlagIds: Map[String, Int] = Map();
//            uniqueFlagIds = uniqueFlagIds + ((SatSolver.baseFeatureName, uniqueFlagIds.size + 1))
            for (clause <- exprCNF.clauses)
                for (literal <- (clause.posLiterals ++ clause.negLiterals))
                    if (!uniqueFlagIds.contains(literal.feature))
                        uniqueFlagIds = uniqueFlagIds + ((literal.feature, uniqueFlagIds.size + 1))

            solver.newVar(uniqueFlagIds.size)

            def addClauses(expr: NF): Boolean = {
                try {
                    for (clause <- expr.clauses) addClause(clause);
                } catch {
                    case e: ContradictionException => return true;
                }
                false
            }
            def addClause(clause: Clause): Unit = {
                val clauseArray: Array[Int] = new Array(clause.size)
                var i = 0
                for (literal <- clause.posLiterals) {
                    clauseArray(i) = uniqueFlagIds(literal.feature)
                    i = i + 1;
                }
                for (literal <- clause.negLiterals) {
                    clauseArray(i) = -uniqueFlagIds(literal.feature)
                    i = i + 1;
                }
                solver.addClause(new VecInt(clauseArray));
            }

//            addClause(SatSolver.baseFeatureClause)
            val contradiction = addClauses(exprCNF)
            return !contradiction && solver.isSatisfiable();

        } finally {
            if (PROFILING)
                println("<SAT in " + (System.currentTimeMillis() - startTimeSAT) + " ms>")
        }
    }

    //	protected void addClause(Node node) throws ContradictionException {
    //		try {
    //			if (node instanceof Or) {
    //				int[] clause = new int[node.children.length];
    //				int i = 0;
    //				for (Node child : node.getChildren())
    //					clause[i++] = getIntOfLiteral(child);
    //				solver.addClause(new VecInt(clause));
    //			}
    //			else {
    //				int literal = getIntOfLiteral(node);
    //				solver.addClause(new VecInt(new int[] {literal}));
    //			}
    //		} catch (ClassCastException e) {
    //			throw new RuntimeException("expression is not in cnf", e);
    //		}
    //	}

}
