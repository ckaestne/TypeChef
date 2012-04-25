package de.fosd.typechef.featureexpr

import junit.framework._;
import junit.framework.Assert._
import org.junit.Test
import FeatureExprFactory.sat._

class TestNumericConditions extends TestCase {

    @Test
    def testSimpleConditions() {
        //1==1
        assertTrue(
            createEquals(createInteger(1), createInteger(1)).isTautology());
        //1==2
        assertTrue(
            createEquals(createInteger(1), createInteger(2)).isContradiction());
        //1+1==2
        assertTrue(
            createEquals(
                createPlus(
                    createInteger(1),
                    createInteger(1)),
                createInteger(2)).isTautology());
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
            createGreaterThan(
                createPlus(
                    createMinus(
                        createIf((createDefinedExternal("forcemaxzone").not), createInteger(11), createInteger(0)),
                        (createInteger(1))),
                    (createInteger(12))),
                createIf(
                    createDefinedExternal("3stuff"),
                    (createInteger(29)),
                    createIf(
                        createDefinedExternal("3!morestuff"),
                        (createInteger(26)),
                        createIf(
                            createDefinedExternal("2!stuff"),
                            (createInteger(27)),
                            (createInteger(0)))))).and(createDefinedExternal("sparsemem"));

        assertTrue(longExpr.isSatisfiable());

        //!	__IF__		CONFIG_64BIT	__THEN__			1		<=			64	__ELSE__				1		<=		32
        assertTrue(
            createBooleanIf(createDefinedExternal("CONFIG_64BIT"),
                createLessThanEquals(
                    (createInteger(1)),
                    (createInteger(64))),
                createLessThanEquals(
                    (createInteger(1)),
                    (createInteger(32)))).not.isContradiction())

        //if !( 1<=	__IF__		CONFIG_64BIT	__THEN__		64	__ELSE__		32)
        assertTrue(
            createLessThanEquals(
                (createInteger(1)),
                createIf(
                    createDefinedExternal("CONFIG_64BIT"),
                    createInteger(64),
                    createInteger(32))).not.isContradiction());

        //a?1:2==2
        assertTrue(
            createEquals(
                createIf(
                    createDefinedExternal("a"),
                    createInteger(1),
                    createInteger(2)),
                createInteger(2)).isSatisfiable());

    }
    def testMixIntChar = {
        assertTrue(createEquals(createInteger(1), createCharacter('1')).isContradiction())
        assertTrue(createEquals(createInteger(49), createCharacter('1')).isTautology())
    }

}
