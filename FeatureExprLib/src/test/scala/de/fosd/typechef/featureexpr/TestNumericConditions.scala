package de.fosd.typechef.featureexpr

import junit.framework._;
import junit.framework.Assert._
import org.junit.Test

class TestNumericConditions extends TestCase {

    @Test
    def testSimpleConditions() {
        //1==1
        assertTrue(
            FeatureExpr.createEquals(FeatureExpr.createInteger(1), FeatureExpr.createInteger(1)).isTautology);
        //1==2
        assertTrue(
            FeatureExpr.createEquals(FeatureExpr.createInteger(1), FeatureExpr.createInteger(2)).isContradiction());
        //1+1==2
        assertTrue(
            FeatureExpr.createEquals(
                FeatureExpr.createPlus(
                    FeatureExpr.createInteger(1),
                    FeatureExpr.createInteger(1)),
                FeatureExpr.createInteger(2)).isTautology);
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
        val longExpr =
            FeatureExpr.createGreaterThan(
                FeatureExpr.createPlus(
                    FeatureExpr.createMinus(
                        FeatureExpr.createIf(Not(DefinedExternal("forcemaxzone")), IntegerLit(11), IntegerLit(0)),
                        new FeatureExprImpl(IntegerLit(1))),
                    new FeatureExprImpl(IntegerLit(12))),
                FeatureExpr.createIf(
                    FeatureExpr.createDefinedExternal("3stuff"),
                    new FeatureExprImpl(IntegerLit(29)),
                    FeatureExpr.createIf(
                        FeatureExpr.createDefinedExternal("3!morestuff"),
                        new FeatureExprImpl(IntegerLit(26)),
                        FeatureExpr.createIf(
                            FeatureExpr.createDefinedExternal("2!stuff"),
                            new FeatureExprImpl(IntegerLit(27)),
                            new FeatureExprImpl(IntegerLit(0)))))).and(FeatureExpr.createDefinedExternal("sparsemem"));

        assertTrue(new SatSolver().isSatisfiable(longExpr.expr));

        //!	__IF__		CONFIG_64BIT	__THEN__			1		<=			64	__ELSE__				1		<=		32
        assertTrue(
            FeatureExpr.createIf(new FeatureExprImpl(DefinedExternal("CONFIG_64BIT")),
                FeatureExpr.createLessThanEquals(
                    new FeatureExprImpl(IntegerLit(1)),
                    new FeatureExprImpl(IntegerLit(64))),
                FeatureExpr.createLessThanEquals(
                    new FeatureExprImpl(IntegerLit(1)),
                    new FeatureExprImpl(IntegerLit(32)))).not.isContradiction)

        //if !( 1<=	__IF__		CONFIG_64BIT	__THEN__		64	__ELSE__		32)
        assertTrue(
            FeatureExpr.createLessThanEquals(
                new FeatureExprImpl(IntegerLit(1)),
                FeatureExpr.createIf(
                    DefinedExternal("CONFIG_64BIT"),
                    IntegerLit(64),
                    IntegerLit(32))).not.isContradiction);

        //a?1:2==2
        assertTrue(
            FeatureExpr.createEquals(
                FeatureExpr.createIf(
                    DefinedExternal("a"),
                    IntegerLit(1),
                    IntegerLit(2)),
                FeatureExpr.createInteger(2)).simplify.isSatisfiable);

    }

}
