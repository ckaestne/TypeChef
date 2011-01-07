package de.fosd.typechef.featureexpr

import junit.framework.TestCase
import org.junit.Test


/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 05.01.11
 * Time: 17:17
 * To change this template use File | Settings | File Templates.
 */

class FeatureExprTest extends TestCase {

    import org.junit.Assert._
    import FeatureExpr._


    @Test
    def testBasics {
        assertEquals(feature("a"), feature("a"))
        assertEquals(feature("a") and True, feature("a"))
        assertEquals(feature("a") or True, True)
        assertEquals(feature("a") and False, False)
        assertEquals(feature("a") or False, feature("a"))
        assertEquals(a and b and False, False)
        assertEquals(False and a and b, False)
        assertEquals(feature("a") orNot feature("a"), True)
        assertEquals(feature("a") andNot feature("a"), False)
        assertEquals(feature("a") and feature("a"), feature("a"))
        assertEquals(feature("a") or feature("a"), feature("a"))
        assertEquals(feature("a") or feature("a") and feature("a"), feature("a"))
        assertEquals(feature("a") and feature("b"), feature("a") and feature("b"))
        assertEquals(feature("a") and feature("b"), feature("b") and feature("a"))
        assertEquals(feature("a") or feature("b"), feature("a") or feature("b"))
        assertEquals(feature("a") or feature("b"), feature("b") or feature("a"))
        assertEquals(feature("a").not.not, feature("a"))
        assertEquals(True.not, False)
        assertEquals(False.not, True)
        assertEquals(a and b and a, a and b)
        assertEquals((a and b and c) and (a and b and c), a and b and c)
        assertEquals(a or b or a, a or b)
        assertEquals((a andNot b) or b, (a andNot b) or b)
        assertEquals(((a andNot b) or b).toCNF, a or b)
    }

    @Test def testIf {
        assertEquals(createIf(feature("a"), True, False), feature("a"))
        assertEquals(createIf(feature("a"), False, True), feature("a").not)
        assertEquals(createIf(feature("a"), feature("b") orNot feature("b"), False), feature("a"))
        assertEquals(createIf(True, True, False), True)
        assertEquals(createIf(True, feature("a"), feature("b")), feature("a"))
        assertEquals(createIf(False, feature("a"), feature("b")), feature("b"))
    }

    @Test def testComparison {
        assertEquals(createLT(v(2), v(4)), True)
        assertEquals(createLT(createIf(feature("a"), v(1), v(2)), v(4)), True)
        assertEquals(createLT(createIf(feature("a"), v(1), v(5)), v(4)), feature("a"))
        assertEquals(createLT(createIf(a, v(1), v(5)), createIf(b, v(2), v(6))), a or (a.not andNot b))
        assertEquals(createLT(createIf(feature("a"), v(1), v(5)), createIf(feature("a"), v(2), v(6))), True)
        assertEquals(createLT(createIf(a, createIf(b, v(1), v(2)), createIf(b, v(3), v(4))), v(5)), True)
        assertEquals(createLT(createIf(a, createIf(b, v(1), v(2)), createIf(b, v(3), v(4))), v(2)), a and b)
    }

    @Test def testOperations {
        assertEquals(createPlus(v(2), v(4)), v(6))
        assertEquals(createPlus(createIf(feature("a"), v(1), v(2)), v(4)), createIf(feature("a"), v(5), v(6)))
        assertEquals(createLT(createIf(a, createPlus(createIf(b, v(1), v(2)), v(10)), createIf(b, v(3), v(4))), v(5)), a.not)
    }

    def v(value: Int): FeatureExprValue = createInteger(value)
    def a = feature("a")
    def b = feature("b")
    def cgit = feature("c")
    def feature(n: String) = createDefinedExternal(n)
    def createLT(a: FeatureExprValue, b: FeatureExprValue) = createLessThan(a, b)
}