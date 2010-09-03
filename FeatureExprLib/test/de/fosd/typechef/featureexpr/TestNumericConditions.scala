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
    //	  &&
    //	>
    //		+
    //			-
    //				__IF__
    //					!
    //						CONFIG_FORCE_MAX_ZONEORDER
    //				__THEN__
    //					11
    //				__ELSE__
    //					0
    //				1
    //			12
    //		__IF__
    //			&&
    //				&&
    //					CONFIG_X86_PAE
    //					CONFIG_X86_32
    //				CONFIG_SPARSEMEM
    //		__THEN__
    //			29
    //		__ELSE__
    //			__IF__
    //				&&
    //					&&
    //						!
    //							CONFIG_X86_PAE
    //						CONFIG_X86_32
    //					CONFIG_SPARSEMEM
    //			__THEN__
    //				26
    //			__ELSE__
    //				__IF__
    //					&&
    //						!
    //							CONFIG_X86_32
    //						CONFIG_SPARSEMEM
    //				__THEN__
    //					27
    //				__ELSE__
    //					0
    //	CONFIG_SPARSEMEM
    val longExpr=
      FeatureExpr.createGreaterThan(
        FeatureExpr.createPlus(
          FeatureExpr.createMinus(
            FeatureExpr.createIf(Not(DefinedExternal("forcemaxzone")), IntegerLit(11), IntegerLit(0)),
            new FeatureExpr(IntegerLit(1))
            ),
          new FeatureExpr(IntegerLit(12))
          ),
        FeatureExpr.createIf(
          FeatureExpr.createDefinedExternal("3stuff"),
          new FeatureExpr(IntegerLit(29)),
          FeatureExpr.createIf(
            FeatureExpr.createDefinedExternal("3!morestuff"),
            new FeatureExpr(IntegerLit(26)),
            FeatureExpr.createIf(
              FeatureExpr.createDefinedExternal("2!stuff"),
              new FeatureExpr(IntegerLit(27)),
              new FeatureExpr(IntegerLit(0))
              )
            )
          )
        ).and(FeatureExpr.createDefinedExternal("sparsemem"));
    
    assertTrue(new SatSolver().isSatisfiable(longExpr.expr));

    //!	__IF__		CONFIG_64BIT	__THEN__			1		<=			64	__ELSE__				1		<=		32
    assertTrue(new SatSolver().isContradiction(
      FeatureExpr.createIf(new FeatureExpr(DefinedExternal("CONFIG_64BIT")),
        FeatureExpr.createLessThanEquals(
          new FeatureExpr(IntegerLit(1)),
          new FeatureExpr(IntegerLit(64))),
        FeatureExpr.createLessThanEquals(
          new FeatureExpr(IntegerLit(1)),
          new FeatureExpr(IntegerLit(32)))).not.expr)
      )

    //if !( 1<=	__IF__		CONFIG_64BIT	__THEN__		64	__ELSE__		32)
    assertTrue(new SatSolver().isContradiction(
      FeatureExpr.createLessThanEquals(
        new FeatureExpr(IntegerLit(1)),
        FeatureExpr.createIf(
          DefinedExternal("CONFIG_64BIT"),
          IntegerLit(64),
          IntegerLit(32))).not.simplify.expr));

    //a?1:2==2
    assertTrue(new SatSolver().isSatisfiable(
      FeatureExpr.createEquals(
        FeatureExpr.createIf(
          DefinedExternal("a"),
          IntegerLit(1),
          IntegerLit(2)),
        FeatureExpr.createInteger(2)).simplify.expr));

  }

}