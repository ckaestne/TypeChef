package de.fosd.typechef.featureexpr

import junit.framework._
import junit.framework.Assert._
import org.junit.Test

import FeatureExprFactory._

class TestFeatureExprBasics extends TestCase {
    def a = createDefinedExternal("a")
    def b = createDefinedExternal("b")
    def h = createDefinedExternal("h")
    def s = 1
    //SATISFIABLE
    def t = 2
    //TAUTOLOGY
    def c = 3
    //CONTRADICTION

    def check(x: FeatureExpr, f: Int = s) {
        println(x)
        //        println("CNF: " + x.cnfExpr)
        //        println("DNF: " + x.dnfExpr)
        if (f == s) {
            assertTrue("expected satisfiable " + x, x.isSatisfiable())
            assertFalse("not expected tautology " + x, x.isTautology())
            assertFalse("not expected contradiction " + x, x.isContradiction())
        }
        if (f == t) {
            assertTrue("expected satisfiable " + x, x.isSatisfiable())
            assertTrue("expected tautology " + x, x.isTautology())
            assertFalse("not expected contradiction " + x, x.isContradiction())
        }
        if (f == c) {
            assertFalse("not expected satisfiable " + x, x.isSatisfiable())
            assertFalse("not expected tautology " + x, x.isTautology())
            assertTrue("expected contradiction " + x, x.isContradiction())
        }
    }

    @Test
    def testSimpleCases() = {
        check(a or b)
        check((a or b).not)
        check(a or a)
        check(a and b)
        check(a and a)
        check(a and b and h)
        check(a and b or a and h or b.not)
        check(a or a.not, t)
        check(True, t)
        check(True, t)
        check(a and a.not, c)
        check(False, c)
        check(False, c)
        check(a and b or (a and b.not))
        check((a.not and b.not and h) or (a) or (a.not and b))
        check(False and False, c)
        check(False or False, c)
        check(True.not or True.not, c)
        check(True.not and True.not, c)
        check(True or False, t)
        check(True and False, c)
        check(True and True, t)
        check(True or True, t)
        check(False or True, t)
        check(False and True, c)
        check(False and a, c)
        check(False or a, s)
        check(a and False, c)
        check(a or False, s)
        check(True and a, s)
        check(True or a, t)
        check(a and True, s)
        check(a or True, t)
        check(True implies True, t)
        check(True and (True not), c)
    }

}
