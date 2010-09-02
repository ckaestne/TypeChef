package de.fosd.typechef.featureexpr

import junit.framework._
import junit.framework.Assert._

class TestSatSolver extends TestCase {

  def testSATSolver() {
    assertEquals(true, new SatSolver().isSatisfiable(
      new And(
        new Or(DefinedExternal("a"), DefinedExternal("b")),
        new Or(DefinedExternal("a"), DefinedExternal("b"))
        )));
    assertEquals(false, new SatSolver().isTautology(
      new And(
        new Or(DefinedExternal("a"), DefinedExternal("b")),
        new Or(DefinedExternal("a"), DefinedExternal("b"))
        )));
    assertEquals(true, new SatSolver().isContradiction(
      new And(DefinedExternal("a"), Not(DefinedExternal("a")))));
    assertEquals(true, new SatSolver().isTautology(
      new Or(DefinedExternal("a"), Not(DefinedExternal("a")))));
    assertEquals(false, new SatSolver().isSatisfiable(
      new And(DefinedExternal("a"), Not(DefinedExternal("a")))));

    assertEquals(true, new SatSolver().isSatisfiable(
      IfExpr(DefinedExternal("a"), DefinedExternal("a"), Not(DefinedExternal("a")))));
    assertEquals(true, new SatSolver().isContradiction(
      IfExpr(DefinedExternal("a"), Not(DefinedExternal("a")), DefinedExternal("a"))));

    assertEquals(true, new SatSolver().isContradiction(
      DeadFeature()));
    assertEquals(true, new SatSolver().isTautology(
      BaseFeature()))
    assertEquals(true, new SatSolver().isTautology(
      IntegerLit(2)))
  }

  def x() {
    assertEquals(true, new SatSolver().isContradiction(
      And(Set(DefinedExternal("B"), Not(DefinedExternal("H")), Not(new Or(DefinedExternal("H"), new And(Not(DefinedExternal("H")), DefinedExternal("B"))))))
      ));
  }

  def testX() {
    x();
  }

  //(A||B) && (!B|| !A)

}
