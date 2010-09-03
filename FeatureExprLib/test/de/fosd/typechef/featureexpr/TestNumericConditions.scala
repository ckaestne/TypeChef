package de.fosd.typechef.featureexpr

import junit.framework._;
import junit.framework.Assert._

class TestNumericConditions extends TestCase {

  def testSimpleConditions() {
    //1==1
    assertTrue(new SatSolver().isTautology(
      FeatureExpr.createEquals(FeatureExpr.createInteger(1), FeatureExpr.createInteger(1)).expr));
    //1==2
    assertTrue(new SatSolver().isContradiction(
      FeatureExpr.createEquals(FeatureExpr.createInteger(1), FeatureExpr.createInteger(2)).expr));
    //1+1==2
    assertTrue(new SatSolver().isTautology(
      FeatureExpr.createEquals(
        FeatureExpr.createPlus(
          FeatureExpr.createInteger(1),
          FeatureExpr.createInteger(1)),
        FeatureExpr.createInteger(2)).expr));
  }
  def testConditions() {
    //a?1:2==2
    assertTrue(new SatSolver().isSatisfiable(
      FeatureExpr.createEquals(
        FeatureExpr.createIf(
          DefinedExternal("a"),
          IntegerLit(1),
          IntegerLit(2)),
        FeatureExpr.createInteger(2)).simplify.expr));

    System.out.println(
      FeatureExpr.createEquals(
        FeatureExpr.createIf(
          DefinedExternal("a"),
          IntegerLit(1),
          IntegerLit(2)),
        FeatureExpr.createInteger(2)).print
      );
  }

}