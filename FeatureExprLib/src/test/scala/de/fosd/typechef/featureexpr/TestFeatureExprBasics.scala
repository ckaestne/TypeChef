package de.fosd.typechef.featureexpr

import junit.framework._
import junit.framework.Assert._
import org.junit.Test

import FeatureExpr._

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
        check(FeatureExpr.base, t)
        check(a and a.not, c)
        check(False, c)
        check(FeatureExpr.dead, c)
        check(a and b or (a and b.not))
        check((a.not and b.not and h) or (a) or (a.not and b))
        check(FeatureExpr.dead and FeatureExpr.dead, c)
        check(FeatureExpr.dead or FeatureExpr.dead, c)
        check(FeatureExpr.base.not or FeatureExpr.base.not, c)
        check(FeatureExpr.base.not and FeatureExpr.base.not, c)
        check(FeatureExpr.base or FeatureExpr.dead, t)
        check(FeatureExpr.base and FeatureExpr.dead, c)
        check(FeatureExpr.base and FeatureExpr.base, t)
        check(FeatureExpr.base or FeatureExpr.base, t)
        check(FeatureExpr.dead or FeatureExpr.base, t)
        check(FeatureExpr.dead and FeatureExpr.base, c)
        check(FeatureExpr.dead and a, c)
        check(FeatureExpr.dead or a, s)
        check(a and FeatureExpr.dead, c)
        check(a or FeatureExpr.dead, s)
        check(FeatureExpr.base and a, s)
        check(FeatureExpr.base or a, t)
        check(a and FeatureExpr.base, s)
        check(a or FeatureExpr.base, t)
        check(FeatureExpr.base implies FeatureExpr.base, t)
        check(FeatureExpr.base and (FeatureExpr.base not), c)
    }

}
