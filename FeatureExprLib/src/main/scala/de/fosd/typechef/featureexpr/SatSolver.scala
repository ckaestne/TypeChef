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

class SatSolver extends Solver {

  val baseFeatureName = "BASE"

  private def countClauses(expr: FeatureExprTree) = {
    var clauses = 0
    expr.accept(e => clauses = clauses + 1)
    clauses
  }
  private def countFlags(expr: FeatureExprTree) = {
    var flags = Set[String]()
    expr.accept(e => e match { case DefinedExternal(m) => flags = flags + m; case e => ; })
    flags.size
  }

  val PROFILING = true;

  def isSatisfiable(expr: FeatureExprTree): Boolean = {
    if (expr == DeadFeature()) return false;
    if (expr == BaseFeature()) return true;

    val startTime = System.currentTimeMillis();

    if (PROFILING)
      println("<toCNF " + countClauses(expr) + " with " + countFlags(expr) + " flags>")
    val exprs = expr.toCnfEquiSat.simplify;
    if (PROFILING)
      println("</toCNF " + countClauses(exprs) + " in " + (System.currentTimeMillis() - startTime) + " ms>")

    val startTimeSAT = System.currentTimeMillis();
    try {

      val solver = SolverFactory.newDefault();
      //        solver.setTimeoutMs(1000);
      solver.setTimeoutOnConflicts(100000)

      var uniqueFlagIds: Map[String, Int] = Map();
      uniqueFlagIds = uniqueFlagIds + ((baseFeatureName, uniqueFlagIds.size + 1))
      exprs.accept(_ match {
        case DefinedExternal(name: String) =>
          if (!uniqueFlagIds.contains(name))
            uniqueFlagIds = uniqueFlagIds + ((name, uniqueFlagIds.size + 1))
        case _ => ;
      })

      solver.newVar(uniqueFlagIds.size)

      def addClauses(expr: FeatureExprTree): Boolean = {
        try {
          expr match {
            case And(children) => for (child <- children) addClause(child);
            case e => addClause(e);
          }
        } catch {
          case e: ContradictionException => return true;
        }
        false
      }
      def addClause(expr: FeatureExprTree): Unit = {
        val children = expr match {
          case Or(c) => c
          case IntegerLit(i) => if (i != 0) Set(BaseFeature()) else Set(DeadFeature())
          case Not(IntegerLit(i)) => if (i != 0) Set(DeadFeature()) else Set(BaseFeature())
          case DefinedExternal(n) => Set(DefinedExternal(n))
          case Not(DefinedExternal(n)) => Set(Not(DefinedExternal(n)))
          case e => throw new NoCnfException(e)
        }
        val clauseArray: Array[Int] = new Array(children.size)
        var i = 0
        for (child <- children) {
          child match {
            case DeadFeature() => clauseArray(i) = -uniqueFlagIds(baseFeatureName)
            case IntegerLit(_) => clauseArray(i) = uniqueFlagIds(baseFeatureName)
            case DefinedExternal(name) => clauseArray(i) = uniqueFlagIds(name)
            case Not(DefinedExternal(name)) => clauseArray(i) = -uniqueFlagIds(name)
            case e => throw new NoCnfException(e)
          }
          i = i + 1;
        }
        solver.addClause(new VecInt(clauseArray));
      }

      try {
        addClause(BaseFeature())
        val contradiction = addClauses(exprs)
        return !contradiction && solver.isSatisfiable();
      } catch {
        case e: NoCnfException => e.printStackTrace; return true;
      }

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
