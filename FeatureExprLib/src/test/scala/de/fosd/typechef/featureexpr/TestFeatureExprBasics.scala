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
        check(base, t)
        check(a and a.not, c)
        check(False, c)
        check(dead, c)
        check(a and b or (a and b.not))
        check((a.not and b.not and h) or (a) or (a.not and b))
        check(dead and dead, c)
        check(dead or dead, c)
        check(base.not or base.not, c)
        check(base.not and base.not, c)
        check(base or dead, t)
        check(base and dead, c)
        check(base and base, t)
        check(base or base, t)
        check(dead or base, t)
        check(dead and base, c)
        check(dead and a, c)
        check(dead or a, s)
        check(a and dead, c)
        check(a or dead, s)
        check(base and a, s)
        check(base or a, t)
        check(a and base, s)
        check(a or base, t)
        check(base implies base, t)
        check(base and (base not), c)
    }

}
