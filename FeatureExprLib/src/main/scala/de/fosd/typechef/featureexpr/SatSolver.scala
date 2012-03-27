package de.fosd.typechef.featureexpr

import org.sat4j.core.VecInt
import collection.mutable.WeakHashMap

import org.sat4j.minisat.SolverFactory
import org.sat4j.specs.{IConstr, ContradictionException, TimeoutException}
import org.sat4j.tools.{ModelIterator, SolutionCounter}


/**
 * connection to the SAT4j SAT solver
 *
 * may reuse instance
 */

class SatSolver {
    /**
     * caching can reuse SAT solver instances, but experience
     * has shown that it can lead to incorrect results,
     * hence caching is currently disabled
     */
    val CACHING = false
    def isSatisfiable(exprCNF: FeatureExpr, featureModel: FeatureModel = NoFeatureModel): Boolean = {
        (if (CACHING && (nfm(featureModel) != NoFeatureModel))
            SatSolverCache.get(nfm(featureModel))
        else
            new SatSolverImpl(nfm(featureModel), false)).isSatisfiable(exprCNF)
    }

    protected def nfm(fm: FeatureModel) = if (fm == null) NoFeatureModel else fm
}

class SatSolverProducts(val featureModel: FeatureModel = NoFeatureModel) extends SatSolver {
  private val ss = new SatSolverImpl(featureModel, false)
  def getAllProducts = ss.getAllProducts
}


private object SatSolverCache {
    val cache: WeakHashMap[FeatureModel, SatSolverImpl] = new WeakHashMap()
    def get(fm: FeatureModel) =
    /*if (fm == NoFeatureModel) new SatSolverImpl(fm)
   else */ cache.getOrElseUpdate(fm, new SatSolverImpl(fm, true))
}

private class SatSolverImpl(featureModel: FeatureModel, isReused: Boolean) {

    import SatSolver._

    /**Type Aliases for Readability */
    type CNF = FeatureExpr
    type OrClause = FeatureExpr
    type Literal = FeatureExpr
    type Flag = DefinedExpr

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
    def isSatisfiable(exprCNF: CNF): Boolean = {
        assert(CNFHelper.isCNF(exprCNF))

        if (exprCNF == True) return true
        if (exprCNF == False) return false
        //as long as we do not consider feature models, expressions with a single variable
        //are always satisfiable
        if ((featureModel == NoFeatureModel) && (CNFHelper.isLiteralExternal(exprCNF))) return true

        if (PROFILING)
            print("<SAT " + countClauses(exprCNF) + " with " + countFlags(exprCNF) + " flags; ")

        val startTimeSAT = System.currentTimeMillis();
        try {

            //find used macros, combine them by common expansion
            val cnfs: List[CNF] = prepareFormula(exprCNF, PROFILING)

            if (PROFILING)
                print(";")
            for (cnf <- cnfs; clause <- CNFHelper.getCNFClauses(cnf))
                for (literal <- CNFHelper.getDefinedExprs(clause))
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
             * clauses with only a single literal are added to "assumptions" and can be
             * checked as paramter to isSatisfiable
             * all other clauses are added to the Solver but remembered in "constraintGroup"
             * which is removed from the solver at the end
             */

            var constraintGroup:Set[IConstr] = Set()
            try {
                val assumptions = new VecInt();
                try {
                    for (cnf <- cnfs; clause <- CNFHelper.getCNFClauses(cnf)) {
                        if (CNFHelper.isLiteral(clause))
                            assumptions.push(getAtomicId(uniqueFlagIds, clause))
                        else {
                            val constr = solver.addClause(getClauseVec(uniqueFlagIds, clause))
                            if (isReused && constr != null)
                                constraintGroup = constraintGroup + constr
                        }
                    }
                } catch {
                    case e: ContradictionException => return false;
                }

                if (PROFILING)
                    print(";")

                val result = solver.isSatisfiable(assumptions)
                if (PROFILING)
                    print(result + ";")
                result
            } finally {
                if (isReused)
                    for (constr<-constraintGroup)
                        assert(solver.removeConstr(constr))
            }
        } finally {
            if (PROFILING)
                println(" in " + (System.currentTimeMillis() - startTimeSAT) + " ms>")
        }
    }

  val isolg = new ModelIterator(solver)
  val mIdFlagg = Map() ++ uniqueFlagIds.toList.map(_.swap)

  // creates a stream with all configurations for the given featuremodel
  // beware!!! calculating all configurations can be time-consuming and
  // your machine can possibly run out of memory
  def getAllProducts: Stream[List[(DefinedExternal, Boolean)]] = {
    if (isolg.isSatisfiable) {
      val product = isolg.model()
      var pconfiguration: List[(DefinedExternal, Boolean)] = List()

      for (iflag <- product.toList)
        pconfiguration ::= (FeatureExpr.createDefinedExternal(mIdFlagg.get(iflag.abs).get), iflag > 0)

      Stream.cons(pconfiguration, getAllProducts)
    } else {
      Stream.Empty
    }
  }

  // return the number of solutions
  // taken from:
  // https://faracvs.cs.uni-magdeburg.de/projects/tthuem-FeatureIDE/browser/trunk/plugins/de.ovgu.featureide.fm.core/src/org/prop4j/SatSolver.java
  // beware!!! the determination of countSolutions involves generating all solutions,
  // which is a time- and resource-consuming task
  def countSolutions = {
    var numsolutions: Long = 0
    val counter = new SolutionCounter(solver)

    try {
      numsolutions = counter.countSolutions()
    } catch {
      case _: TimeoutException => numsolutions = -1 - counter.lowerBound();
    }

    numsolutions
  }
}

private object SatSolver {
    /**Type Aliases for Readability */
    type CNF = FeatureExpr
    type OrClause = FeatureExpr
    type Literal = FeatureExpr
    type Flag = DefinedExpr


    def countClauses(expr: CNF) = CNFHelper.getCNFClauses(expr).size

    def countFlags(expr: CNF) = {
        var flags = Set[String]()
        for (clause <- CNFHelper.getCNFClauses(expr))
            for (literal <- CNFHelper.getDefinedExprs(clause))
                flags = flags + literal.satName
        flags.size
    }

    def getClauseVec(uniqueFlagIds: Map[String, Int], orClause: OrClause) = {
        val literals = CNFHelper.getLiterals(orClause)
        val clauseArray: Array[Int] = new Array(literals.size)
        var i = 0
        for (literal <- literals) {
            literal match {
                case x: DefinedExpr => clauseArray(i) = uniqueFlagIds(x.satName)
                case Not(x: DefinedExpr) => clauseArray(i) = -uniqueFlagIds(x.satName)
                case _ => throw new NoLiteralException(literal)
            }
            i = i + 1;
        }
        new VecInt(clauseArray)
    }

    def getAtomicId(uniqueFlagIds: Map[String, Int], literal: Literal) = literal match {
        case x: DefinedExpr => uniqueFlagIds(x.satName)
        case Not(x: DefinedExpr) => -uniqueFlagIds(x.satName)
        case _ => throw new NoLiteralException(literal)
    }

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
     * We first collect all expansions and detect identical ones
     */
    def prepareFormula(expr: CNF, PROFILING: Boolean): List[CNF] = {
        import scala.collection.mutable.Map
        var macroExpansions: Map[String, FeatureExpr] = Map()
        val cache: Map[FeatureExpr, FeatureExpr] = Map()

        def prepareLiteral(literal: DefinedExpr): DefinedExpr = {
            literal match {
                case DefinedMacro(name, _, expansionName, expansionNF) => {
                    if (!macroExpansions.contains(expansionName)) {
                        if (PROFILING)
                            print(expansionName)
                        val e: CNF = expansionNF.apply()
                        if (PROFILING)
                            print(":")
                        //recursively expand formula (dummy is necessary to avoid accidental recursion)
                        macroExpansions = macroExpansions + (expansionName -> False /*dummy*/)
                        val preparedExpansion = prepareFormulaInner(e)
                        macroExpansions = macroExpansions + (expansionName -> preparedExpansion)

                        if (PROFILING)
                            print(".")
                    }
                    FeatureExpr.createDefinedExternal(expansionName)
                }
                case e => e
            }
        }
        def prepareFormulaInner(formula: CNF): CNF = {
            formula.mapDefinedExpr(prepareLiteral, cache)
        }

        val targetExpr = prepareFormulaInner(expr)

        List(targetExpr) ++ macroExpansions.values
    }

}
