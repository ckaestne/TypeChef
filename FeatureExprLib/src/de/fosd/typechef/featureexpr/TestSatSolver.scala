package de.fosd.typechef.featureexpr
import org.junit._;
import Assert._
object TestSatSolver extends Application {
  
 assertEquals(true, new SatSolver().isSatisfiable(
		  new And(
		  	new Or(DefinedExternal("a"),DefinedExternal("b")),
		  	new Or(DefinedExternal("a"),DefinedExternal("b"))
		  )));
 assertEquals(false, new SatSolver().isTautology(
		  new And(
		  	new Or(DefinedExternal("a"),DefinedExternal("b")),
		  	new Or(DefinedExternal("a"),DefinedExternal("b"))
		  )));
 assertEquals(true, new SatSolver().isContradiction(
		  new And(	DefinedExternal("a"),Not(DefinedExternal("a")) )));
 assertEquals(true, new SatSolver().isTautology(
		  new Or(	DefinedExternal("a"),Not(DefinedExternal("a")) )));
 assertEquals(false, new SatSolver().isSatisfiable(
		  new And(	DefinedExternal("a"),Not(DefinedExternal("a")) )));
 
 //(A||B) && (!B|| !A)

}
