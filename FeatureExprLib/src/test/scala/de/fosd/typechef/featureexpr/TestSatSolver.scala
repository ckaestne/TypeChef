package de.fosd.typechef.featureexpr

import junit.framework._
import junit.framework.Assert._
import org.junit.Test

import FeatureExpr._

class TestSatSolver extends TestCase {
    def a = createDefinedExternal("a")
    def b = createDefinedExternal("b")
    def h = createDefinedExternal("h")

    @Test
    def testSATSolver() {
        assertEquals(true, (a or b) and (a or b) isSatisfiable ())
        assertEquals(false, (a or b) and (a or b) isTautology ())

        assertEquals(true, a and (a.not) isContradiction ())
        assertEquals(true, a or (a.not) isTautology ())
        assertEquals(false, a and (a.not) isSatisfiable ())
        assertEquals(true, createIf(a, a, a.not) isSatisfiable ())
        assertEquals(true, createIf(a, a.not, a) isContradiction ())
        assertEquals(true, dead isContradiction ())
        assertEquals(true, base isTautology ())
        assertEquals(true, createInteger(2) isTautology ())
        assertEquals(true, a.not isSatisfiable ())
    }

    @Test
    def testX() {
        assertEquals(true, b and (h.not) and (h or ((h.not) and b)).not isContradiction ())
    }

    //(A||B) && (!B|| !A)

}
