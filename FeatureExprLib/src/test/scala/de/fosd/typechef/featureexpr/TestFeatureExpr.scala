package de.fosd.typechef.featureexpr

import org.junit.Ignore

import junit.framework._;
import junit.framework.Assert._
import org.junit.Test
import FeatureExpr._

class TestFeatureExpr extends TestCase {

    @Test
    def assertSimplify(exprA: FeatureExpr, expectedResult: FeatureExpr) {
        println("simplify(" + exprA.toTextExpr + ") = " + exprA.toTextExpr + ", expected " + expectedResult.toTextExpr)
        assert(exprA == expectedResult, "Simplification failed. Found " + exprA + " expected " + expectedResult)
    }


    //adapter for old model
    def DefinedExternal(a: String) = FeatureExpr.createDefinedExternal(a)
    def IntegerLit(a: Int) = FeatureExpr.createInteger(a)
    def And(l: List[FeatureExpr]) = l.reduce(_ and _)
    def Or(l: List[FeatureExpr]) = l.reduce(_ or _)
    def Not(e: FeatureExpr) = e.not
    def Or(a: FeatureExpr, b: FeatureExpr) = a or b
    def And(a: FeatureExpr, b: FeatureExpr) = a and b
    def BaseFeature() = FeatureExpr.base
    def DeadFeature() = FeatureExpr.dead


    def testSimplifyIf() {
        assertSimplify(FeatureExpr.createLessThanEquals(
            (FeatureExpr.createInteger(1)),
            FeatureExpr.createIf(
                DefinedExternal("CONFIG_64BIT"),
                IntegerLit(64),
                IntegerLit(32))),
            IntegerLit(1).toFeatureExpr)

        // (1 + (1 + (1 + __IF__(defined(a),1,2))))) = __IF__(defined(a),4,5), results overall in BaseFeature because both are true
        assertTrue(FeatureExpr.createPlus(
            (FeatureExpr.createInteger(1)),
            FeatureExpr.createPlus(
                (FeatureExpr.createInteger(1)),
                FeatureExpr.createPlus(
                    (FeatureExpr.createInteger(1)),
                    FeatureExpr.createIf(
                        DefinedExternal("a"),
                        IntegerLit(1),
                        IntegerLit(2))))).toFeatureExpr.isTautology)
        //            FeatureExpr.createIf(
        //                DefinedExternal("a"),
        //                IntegerLit(4),
        //                IntegerLit(5)).expr)
    }

    def testSimplifyNumeric() {
        //&&	<=		<<			1			__IF__				CONFIG_NODES_SHIFT			__THEN__				0			__ELSE__				0		__IF__			CONFIG_64BIT		__THEN__			64		__ELSE__			32	1
        assertSimplify(FeatureExpr.createLessThanEquals(
            FeatureExpr.createShiftLeft(
                (FeatureExpr.createInteger(1)),
                FeatureExpr.createIf(DefinedExternal("s"), IntegerLit(0), IntegerLit(0))),
            FeatureExpr.createIf(DefinedExternal("b"), IntegerLit(64), IntegerLit(32))).and(createInteger(1).toFeatureExpr), BaseFeature());

        assertSimplify(
            FeatureExpr.createIf(
                (FeatureExpr.createDefinedExternal("a")),
                FeatureExpr.createLessThanEquals((FeatureExpr.createInteger(1)), (FeatureExpr.createInteger(64))),
                FeatureExpr.createLessThanEquals((FeatureExpr.createInteger(1)), (FeatureExpr.createInteger(32)))).not, DeadFeature());
    }

    @Ignore
    def testEquality() {
        assertEquals(FeatureExpr.createDefinedExternal("a"), FeatureExpr.createDefinedExternal("a"))
        assertEquals(FeatureExpr.createDefinedExternal("a"), FeatureExpr.createDefinedExternal("a").or(FeatureExpr.createDefinedExternal("a")))
        assertTrue(FeatureExpr.createDefinedExternal("a").or(FeatureExpr.createDefinedExternal("a").not) equivalentTo FeatureExpr.base)
        assertTrue(FeatureExpr.createDefinedExternal("a").or(FeatureExpr.createDefinedExternal("a").not) equals FeatureExpr.base)
        assertTrue(FeatureExpr.createDefinedExternal("a").and(FeatureExpr.createDefinedExternal("b")) equivalentTo FeatureExpr.createDefinedExternal("b").and(FeatureExpr.createDefinedExternal("a")))
    }

    //    @Test
    //    def testFeatureModel = {
    //
    //        val a = FeatureExpr.createDefinedExternal("a")
    //        val b = FeatureExpr.createDefinedExternal("b")
    //        val c = FeatureExpr.createDefinedExternal("c")
    //        val d = FeatureExpr.createDefinedExternal("d")
    //        val e = FeatureExpr.createDefinedExternal("e")
    //
    //        val fm = FeatureModel.create((a implies b) and (a mex c) and d)
    //        val fma = FeatureModel.create(a)
    //
    //        assertTrue(a.isTautology(fma))
    //
    //
    //        assertFalse((a implies b).isTautology())
    //        assertTrue((a implies b).isTautology(fm))
    //        assertTrue(d.isTautology(fm))
    //        assertFalse(e.isTautology(fm))
    //    }

    @Test
    def testAndNotOrPattern {
        val a = FeatureExpr.createDefinedExternal("A")
        val b = FeatureExpr.createDefinedExternal("B")
        val aAndB = (a and b)

        var expr: FeatureExpr = a
        for (i <- 1 until 100) {
            expr = expr andNot aAndB
            expr = expr or aAndB
        }
        println(expr)
        assertTrue(expr equivalentTo a)
        assertTrue(expr.size < 10)
        //        assertEquals(a,expr)
    }

    @Test
    def testAndNotOrPattern2 {
        val a = FeatureExpr.createDefinedExternal("A") //and FeatureExpr.createDefinedExternal("A2") or FeatureExpr.createDefinedExternal("A3")

        var expr: FeatureExpr = a
        for (i <- 1 until 100) {
            val b = FeatureExpr.createDefinedExternal("B" + i)
            expr = expr andNot (a and b)
            expr = expr or (a and b)
        }
        println(expr)
        assertTrue(expr equivalentTo a)
        //        assertTrue(expr.size < 10)
        //        assertEquals(a,expr)
    }

    @Test
    def testCNF {
        def d(s: String) = createDefinedExternal(s)

        val a = ((d("f31").not or (d("X") not)) and ((d("f8") and d("X")) or (d("f24") and d("X")) or (d("f21") and d("X")) or (d("f1") and d("X")) or (d("f14") and d("X")) or (d("f17") and d("X")) or (d("f5") and d("X")) or (d("X") and (d("f1").not)) or (d("f2") and d("X")) or (d("f4") and d("X")) or (d("f10") and d("X")) or (d("f23") and d("X")) or (d("f11") and d("X")) or (d("f22") and d("X")) or (d("f16") and d("X")) or (d("f30") and d("X")) or (d("f9") and d("X")) or (d("f29") and d("X")) or (d("f15") and d("X")) or (d("f18") and d("X")) or (d("f13") and d("X")) or (d("f7") and d("X")) or (d("f6") and d("X")) or (d("f19") and d("X")) or (d("f27") and d("X")) or (d("f26") and d("X")) or (d("f25") and d("X")) or (d("f12") and d("X")) or (d("f3") and d("X")) or (d("f28") and d("X")) or (d("f20") and d("X"))))
        val b = (d("f31") and d("X"))

        assert(a.isSatisfiable)
        assert(b.isSatisfiable)
        assert((a and b).isContradiction)
    }

}
