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
        assertEquals(true, (a or b) and (a or b) isSatisfiable(null))
        assertEquals(false, (a or b) and (a or b) isTautology(null))

        assertEquals(true, a and (a.not) isContradiction(null))
        assertEquals(true, a or (a.not) isTautology(null))
        assertEquals(false, a and (a.not) isSatisfiable(null))
        assertEquals(true, createIf(a, a, a.not) isSatisfiable(null))
        assertEquals(true, createIf(a, a.not, a) isContradiction(null))
        assertEquals(true, dead isContradiction(null))
        assertEquals(true, base isTautology(null))
        assertEquals(true, createInteger(2) isTautology(null))
        assertEquals(true, a.not isSatisfiable(null))
    }

    @Test
    def testX() {
        assertEquals(true, b and (h.not) and (h or ((h.not) and b)).not isContradiction(null))
    }

    //(A||B) && (!B|| !A)

}
