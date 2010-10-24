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
    def t = 2
    def c = 3

    def check(x: FeatureExpr, f: Int = 1) {
        println(x.expr)
        println("CNF: " + x.cnfExpr)
        println("DNF: " + x.dnfExpr)
        if (f == s) {
            assertTrue("expected satisfiable " + x, x.isSatisfiable)
            assertFalse("not expected tautology " + x, x.isTautology)
            assertFalse("not expected contradiction " + x, x.isContradiction)
        }
        if (f == t) {
            assertTrue("expected satisfiable " + x, x.isSatisfiable)
            assertTrue("expected tautology " + x, x.isTautology)
            assertFalse("not expected contradiction " + x, x.isContradiction)
        }
        if (f == c) {
            assertFalse("not expected satisfiable " + x, x.isSatisfiable)
            assertFalse("not expected tautology " + x, x.isTautology)
            assertTrue("expected contradiction " + x, x.isContradiction)
        }
    }

    def testSimpleCases() = {
        check(a or b)
        check((a or b).not)
        check(a or a)
        check(a and b)
        check(a and a)
        check(a and b and h)
        check(a and b or a and h or b.not)
        check(a or a.not, t)
        check(FeatureExpr.base, t)
        check(a and a.not, c)
        check(FeatureExpr.dead, c)
        check(a and b or (a and b.not))
        check((a.not and b.not and h) or (a) or (a.not and b))
    }

    def testToCnf {
        println(FeatureExpr.createEquals(FeatureExpr.createInteger(1), FeatureExpr.createInteger(2)).expr.toCNF)
    }
}
