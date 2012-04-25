package de.fosd.typechef.featureexpr

import junit.framework._
import junit.framework.Assert._
import org.junit.Test

import FeatureExprFactory.sat._

class TestSatSolver extends TestCase {
    def a = createDefinedExternal("a")
    def b = createDefinedExternal("b")
    def h = createDefinedExternal("h")
    //Leave these as def, not val, maybe (???) to test caching more.
    def c = createDefinedExternal("c")

    def d = createDefinedExternal("d")
    def e = createDefinedExternal("e")
    def f = createDefinedExternal("f")

    @Test
    def testSATSolver() {
        assertEquals(true, a.not isSatisfiable())
        assertEquals(true, (a or b) and (a or b) isSatisfiable())
        assertEquals(false, (a or b) and (a or b) isTautology())

        assertEquals(true, a and (a.not) isContradiction())
        assertEquals(true, a or (a.not) isTautology())
        assertEquals(false, a and (a.not) isSatisfiable())
        assertEquals(true, createBooleanIf(a, a, a.not) isSatisfiable())
        assertEquals(true, createBooleanIf(a, a.not, a) isContradiction())
        assertEquals(true, False isContradiction())
        assertEquals(true, True isTautology())
        assertEquals(true, FeatureExprValue.toFeatureExpr(createInteger(2), FeatureExprFactory.sat).isTautology())
    }

    @Test def testBrokenSat {
        val j = (((((d and (c not)) not) or (d and (a)) or (((d not) or c) not)) not) and ((((c or (b not)) and (c or a or (d not) or (c and d) or (a and c))) not) or (((d) not) and ((e or c) not))))
        val k = d

        assert((j or k).isSatisfiable)

        val f1 = (!(d & c) | c | (b & e & (e | d)) | !(a | (c & f)) | !(e | a) | !(e | d))
        val f2 = False
        /* You might be surprised by the not: surprises of equisatisfiable transformations, if you negate the result
         * it might be no more equisatisfiable. */
        //assert(!equiCNFIdentityNot(f1))
    }

    @Test
    def testX() {
        assertEquals(true, b and (h.not) and (h or ((h.not) and b)).not isContradiction())
    }


    //(A||B) && (!B|| !A)

}
