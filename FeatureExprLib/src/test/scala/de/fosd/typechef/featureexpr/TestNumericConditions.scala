package de.fosd.typechef.featureexpr

import junit.framework._;
import junit.framework.Assert._
import org.junit.Test
import FeatureExpr._

class TestNumericConditions extends TestCase {

    @Test
    def testSimpleConditions() {
        //1==1
        assertTrue(
            FeatureExpr.createEquals(FeatureExpr.createInteger(1), FeatureExpr.createInteger(1)).isTautology());
        //1==2
        assertTrue(
            FeatureExpr.createEquals(FeatureExpr.createInteger(1), FeatureExpr.createInteger(2)).isContradiction());
        //1+1==2
        assertTrue(
            FeatureExpr.createEquals(
                FeatureExpr.createPlus(
                    FeatureExpr.createInteger(1),
                    FeatureExpr.createInteger(1)),
                FeatureExpr.createInteger(2)).isTautology());
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
        import FeatureExpr._
        val longExpr =
            FeatureExpr.createGreaterThan(
                FeatureExpr.createPlus(
                    FeatureExpr.createMinus(
                        FeatureExpr.createIf((createDefinedExternal("forcemaxzone").not), createInteger(11), createInteger(0)),
                        (FeatureExpr.createInteger(1))),
                    (FeatureExpr.createInteger(12))),
                FeatureExpr.createIf(
                    FeatureExpr.createDefinedExternal("3stuff"),
                    (FeatureExpr.createInteger(29)),
                    FeatureExpr.createIf(
                        FeatureExpr.createDefinedExternal("3!morestuff"),
                        (FeatureExpr.createInteger(26)),
                        FeatureExpr.createIf(
                            FeatureExpr.createDefinedExternal("2!stuff"),
                            (FeatureExpr.createInteger(27)),
                            (FeatureExpr.createInteger(0)))))).and(FeatureExpr.createDefinedExternal("sparsemem"));

        assertTrue(longExpr.isSatisfiable());

        //!	__IF__		CONFIG_64BIT	__THEN__			1		<=			64	__ELSE__				1		<=		32
        assertTrue(
            FeatureExpr.createIf(FeatureExpr.createDefinedExternal("CONFIG_64BIT"),
                FeatureExpr.createLessThanEquals(
                    (FeatureExpr.createInteger(1)),
                    (FeatureExpr.createInteger(64))),
                FeatureExpr.createLessThanEquals(
                    (FeatureExpr.createInteger(1)),
                    (FeatureExpr.createInteger(32)))).not.isContradiction())

        //if !( 1<=	__IF__		CONFIG_64BIT	__THEN__		64	__ELSE__		32)
        assertTrue(
            FeatureExpr.createLessThanEquals(
                (FeatureExpr.createInteger(1)),
                FeatureExpr.createIf(
                    createDefinedExternal("CONFIG_64BIT"),
                    createInteger(64),
                    createInteger(32))).not.isContradiction());

        //a?1:2==2
        assertTrue(
            FeatureExpr.createEquals(
                FeatureExpr.createIf(
                    createDefinedExternal("a"),
                    createInteger(1),
                    createInteger(2)),
                FeatureExpr.createInteger(2)).isSatisfiable());

    }
    def testMixIntChar = {
        assertTrue(createEquals(createInteger(1), createCharacter('1')).isContradiction())
        assertTrue(createEquals(createInteger(49), createCharacter('1')).isTautology())
    }

}
