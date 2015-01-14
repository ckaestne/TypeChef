package de.fosd.typechef.featureexpr.sat

import org.sat4j.core.VecInt
import collection.mutable.WeakHashMap
import org.sat4j.specs.{IConstr, ContradictionException}
;
import org.sat4j.minisat.SolverFactory;
import scala.Predef._
;

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

  def isSatisfiable(exprCNF: SATFeatureExpr, featureModel: SATFeatureModel = SATNoFeatureModel): Boolean = {
    (if (CACHING && (nfm(featureModel) != SATNoFeatureModel))
      SatSolverCache.get(nfm(featureModel))
    else
      new SatSolverImpl(nfm(featureModel), false)).isSatisfiable(exprCNF)
  }

  /**
   * Basically a clone of isSatisfiable(..) that also returns the satisfying assignment (if available).
   * The return value is a Pair where the first element is a list of the feature names set to true.
   * The second element is a list of feature names set to false.
   */
  def getSatAssignment(featureModel: SATFeatureModel, exprCNF: SATFeatureExpr): Option[(List[String], List[String])] = {
    val solver =
      (if (CACHING && (nfm(featureModel) != SATNoFeatureModel))
        SatSolverCache.get(nfm(featureModel))
      else
        new SatSolverImpl(nfm(featureModel), false))

    if (solver.isSatisfiable(exprCNF, exprCNF != True)) {
      return Some(solver.getLastModel())
    } else {
      return None
    }
  }

  private def nfm(fm: SATFeatureModel) = if (fm == null) SATNoFeatureModel else fm
}

private object SatSolverCache {
  val cache: WeakHashMap[SATFeatureModel, SatSolverImpl] = new WeakHashMap()

  def get(fm: SATFeatureModel) =
  /*if (fm == NoFeatureModel) new SatSolverImpl(fm)
 else */ cache.getOrElseUpdate(fm, new SatSolverImpl(fm, true))
}

private class SatSolverImpl(featureModel: SATFeatureModel, isReused: Boolean) {

  import SatSolver._

  /**Type Aliases for Readability */
  type CNF = SATFeatureExpr
  type OrClause = SATFeatureExpr
  type Literal = SATFeatureExpr
  type Flag = DefinedExpr

  val PROFILING = false

  /**init / constructor */
  val solver = SolverFactory.newDefault();
  solver.setTimeoutMs(10000);
//  solver.setTimeoutOnConflicts(100000)

  assert(featureModel != null)
  solver.addAllClauses(featureModel.clauses)
  var uniqueFlagIds: Map[String, Int] = featureModel.variables

  /**
   * determines whether
   * (exprCNF AND featureModel) is satisfiable
   *
   * featureModel is optional
   */
  def isSatisfiable(exprCNF: CNF, optimizeSimpleExpression: Boolean = true): Boolean = {
    this.lastModel = null // remove model from last satisfiability check
    assert(CNFHelper.isCNF(exprCNF))

    if (optimizeSimpleExpression) {
      if (exprCNF == True) {
        // if the expression is True, then the result is true, and we need a model. The model is cached lazily.
        if (this.trueModel == null) {
          isSatisfiable(exprCNF, false)
          this.trueModel = getLastModel()
        } else {
          lastModel = trueModel
        }
        return true
      }
      if (exprCNF == False) return false
    }
    //as long as we do not consider feature models, expressions with a single variable
    //are always satisfiable
    if ((featureModel == SATNoFeatureModel) && (CNFHelper.isLiteralExternal(exprCNF))) {
      exprCNF match {
        //one of these cases has to match, because we have a literal expression
        case x: DefinedExternal => lastModel = (List(x.satName), List())
        case Not(x: DefinedExternal) => lastModel = (List(), List(x.satName))
        case _ => sys.error("This really should not be possible")
      }
      return true
    }

    val startTime = System.currentTimeMillis();

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

      var constraintGroup: Set[IConstr] = Set()
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
        // print reason for unsatisfiability
        /*
        if (result == false) {
            println("Unsat Explanation:")
            val ex:IVecInt = solver.unsatExplanation()
            for ((fName, modelID) <- uniqueFlagIds) {
                if (ex.contains(modelID))
                    println(fName + " &&")
                else if (ex.contains(-modelID))
                    println("! " + fName + " &&")
            }
        }
        */
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
        if (isReused)
          for (constr <- constraintGroup)
            assert(solver.removeConstr(constr))
      }
    } finally {
      if (PROFILING)
        println(" in " + (System.currentTimeMillis() - startTimeSAT) + " ms>")
    }
  }

  /**
   * This pair contains the model that was constructed during the last isSatisfiable call (if the result was true).
   * The first element contains the names of the features set to true, the second contains the names of the false features.
   */
  var lastModel: (List[String], List[String]) = null
  // model that satisfies the FM (when a TRUE Expression is passed to the solver)
  // this is cached after first creation
  var trueModel: (List[String], List[String]) = null

  def getLastModel() = lastModel
}

private object SatSolver {
  /**Type Aliases for Readability */
  type CNF = SATFeatureExpr
  type OrClause = SATFeatureExpr
  type Literal = SATFeatureExpr
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
    var macroExpansions: Map[String, SATFeatureExpr] = Map()
    val cache: Map[SATFeatureExpr, SATFeatureExpr] = Map()

    def prepareLiteral(literal: DefinedExpr): DefinedExpr = {
      literal match {
        case DefinedMacro(name, _, expansionName, expansionNF) => {
          if (!macroExpansions.contains(expansionName)) {
            if (PROFILING)
              print(expansionName)
            val f = expansionNF.apply()
            assert(f.isInstanceOf[SATFeatureExpr])
            val e: CNF = f.asInstanceOf[SATFeatureExpr]
            if (PROFILING)
              print(":")
            //recursively expand formula (dummy is necessary to avoid accidental recursion)
            macroExpansions = macroExpansions + (expansionName -> False /*dummy*/)
            val preparedExpansion = prepareFormulaInner(e)
            macroExpansions = macroExpansions + (expansionName -> preparedExpansion)

            if (PROFILING)
              print(".")
          }
          FExprBuilder.definedExternal(expansionName)
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