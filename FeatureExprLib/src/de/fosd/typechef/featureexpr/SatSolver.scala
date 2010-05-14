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
  
  
	def isSatisfiable(expr:FeatureExpr):Boolean = {
	    val exprs=expr.simplify.toCNF;
	    //println ("checking "+exprs)
		if (exprs==DeadFeature()) return false;
		if(exprs==BaseFeature()) return true;
	  
	  	val solver = SolverFactory.newDefault();
        solver.setTimeoutMs(1000);

        var uniqueFlagIds:Map[String,Int] = Map();
        exprs.accept(_ match {
        	case DefinedExternal(name:String) => 
			    if (!uniqueFlagIds.contains(name))
			    	uniqueFlagIds=uniqueFlagIds+((name,uniqueFlagIds.size+1))
        	case _=>;
        })
        
        solver.newVar(uniqueFlagIds.size)
    	
		def addClauses(expr:FeatureExpr):Boolean = {
			  try{
			    expr match {
			      case And(children) => for (child<-children)  addClause(child);
			      case e => addClause(e);
//			      case e=>throw new RuntimeException("expression is not in cnf "+e)
			    }
			  } catch {
			    case e:ContradictionException=>return true;
			  }
			  false
			}
			 def addClause(expr:FeatureExpr):Unit = {
			   val children=expr match {
			     case Or(c) => c
			     case DefinedExternal(n) => Set(DefinedExternal(n))
			     case Not(DefinedExternal(n)) => Set(Not(DefinedExternal(n)))
			     case e => throw new RuntimeException("expression is not in cnf "+e)
			   }
			  val clauseArray:Array[Int] = new Array(children.size)
			  var i=0
			  for (child<-children){
			    child match {
			      case DefinedExternal(name) => clauseArray(i) = uniqueFlagIds(name)
			      case Not(DefinedExternal(name)) => clauseArray(i) = -uniqueFlagIds(name)
			      case e => throw new RuntimeException("expression is not in cnf "+e)
			    }
			    i=i+1;
			  }
		      solver.addClause(new VecInt(clauseArray));
			}
		        
        val contradiction=addClauses(exprs)
        return !contradiction && solver.isSatisfiable();	  
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
