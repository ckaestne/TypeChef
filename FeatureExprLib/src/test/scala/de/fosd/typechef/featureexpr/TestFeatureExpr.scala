package de.fosd.typechef.featureexpr

import org.junit.Ignore

import junit.framework._;
import junit.framework.Assert._
import org.junit.Test
import FeatureExprFactory._

class TestFeatureExpr extends TestCase {

    @Test
    def assertSimplify(exprA: FeatureExpr, expectedResult: FeatureExpr) {
        println("simplify(" + exprA.toTextExpr + ") = " + exprA.toTextExpr + ", expected " + expectedResult.toTextExpr)
        assert(exprA == expectedResult, "Simplification failed. Found " + exprA + " expected " + expectedResult)
    }


    //adapter for old model
    def DefinedExternal(a: String) = FeatureExprFactory.createDefinedExternal(a)
    def IntegerLit(a: Int) = FeatureExprFactory.default.createInteger(a)
    def And(l: List[FeatureExpr]) = l.reduce(_ and _)
    def Or(l: List[FeatureExpr]) = l.reduce(_ or _)
    def Not(e: FeatureExpr) = e.not
    def Or(a: FeatureExpr, b: FeatureExpr) = a or b
    def And(a: FeatureExpr, b: FeatureExpr) = a and b
    def BaseFeature() = True
    def DeadFeature() = False


    def testSimplifyIf() {
        assertSimplify(FeatureExprFactory.default.createLessThanEquals(
            (FeatureExprFactory.default.createInteger(1)),
            FeatureExprFactory.default.createIf(
                DefinedExternal("CONFIG_64BIT"),
                IntegerLit(64),
                IntegerLit(32))),
            True)

        // (1 + (1 + (1 + __IF__(defined(a),1,2))))) = __IF__(defined(a),4,5), results overall in BaseFeature because both are true
        val expr = FeatureExprFactory.default.createPlus(
            (FeatureExprFactory.default.createInteger(1)),
            FeatureExprFactory.default.createPlus(
                (FeatureExprFactory.default.createInteger(1)),
                FeatureExprFactory.default.createPlus(
                    (FeatureExprFactory.default.createInteger(1)),
                    FeatureExprFactory.default.createIf(
                        DefinedExternal("a"),
                        IntegerLit(1),
                        IntegerLit(2)))))
        assertTrue(FeatureExprValue.toFeatureExpr(expr, FeatureExprFactory.default).isTautology)
        //            FeatureExprFactory.default.createIf(
        //                DefinedExternal("a"),
        //                IntegerLit(4),
        //                IntegerLit(5)).expr)
    }

    def testSimplifyNumeric() {
        //&&	<=		<<			1			__IF__				CONFIG_NODES_SHIFT			__THEN__				0			__ELSE__				0		__IF__			CONFIG_64BIT		__THEN__			64		__ELSE__			32	1
        assertSimplify(FeatureExprFactory.default.createLessThanEquals(
            FeatureExprFactory.default.createShiftLeft(
                (FeatureExprFactory.default.createInteger(1)),
                FeatureExprFactory.default.createIf(DefinedExternal("s"), IntegerLit(0), IntegerLit(0))),
            FeatureExprFactory.default.createIf(DefinedExternal("b"), IntegerLit(64), IntegerLit(32))).and(True), BaseFeature());

        val expr = FeatureExprFactory.default.createBooleanIf(
            (FeatureExprFactory.createDefinedExternal("a")),
            FeatureExprFactory.default.createLessThanEquals((FeatureExprFactory.default.createInteger(1)), (FeatureExprFactory.default.createInteger(64))),
            FeatureExprFactory.default.createLessThanEquals((FeatureExprFactory.default.createInteger(1)), (FeatureExprFactory.default.createInteger(32))))
        assertSimplify(expr.not, DeadFeature());
    }

    @Ignore
    def testEquality() {
        assertEquals(FeatureExprFactory.createDefinedExternal("a"), FeatureExprFactory.createDefinedExternal("a"))
        assertEquals(FeatureExprFactory.createDefinedExternal("a"), FeatureExprFactory.createDefinedExternal("a").or(FeatureExprFactory.createDefinedExternal("a")))
        assertTrue(FeatureExprFactory.createDefinedExternal("a").or(FeatureExprFactory.createDefinedExternal("a").not) equivalentTo FeatureExprFactory.default.True)
        assertTrue(FeatureExprFactory.createDefinedExternal("a").or(FeatureExprFactory.createDefinedExternal("a").not) equals FeatureExprFactory.default.True)
        assertTrue(FeatureExprFactory.createDefinedExternal("a").and(FeatureExprFactory.createDefinedExternal("b")) equivalentTo FeatureExprFactory.createDefinedExternal("b").and(FeatureExprFactory.createDefinedExternal("a")))
    }

    //    @Test
    //    def testFeatureModel = {
    //
    //        val a = FeatureExprFactory.createDefinedExternal("a")
    //        val b = FeatureExprFactory.createDefinedExternal("b")
    //        val c = FeatureExprFactory.createDefinedExternal("c")
    //        val d = FeatureExprFactory.createDefinedExternal("d")
    //        val e = FeatureExprFactory.createDefinedExternal("e")
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
        val a = FeatureExprFactory.createDefinedExternal("A")
        val b = FeatureExprFactory.createDefinedExternal("B")
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
        val a = FeatureExprFactory.createDefinedExternal("A") //and FeatureExprFactory.createDefinedExternal("A2") or FeatureExprFactory.createDefinedExternal("A3")

        var expr: FeatureExpr = a
        for (i <- 1 until 100) {
            val b = FeatureExprFactory.createDefinedExternal("B" + i)
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


    @Test
    def testExprEvaluationSAT {
        testEval(FeatureExprFactory.sat)
    }
    @Test
    def testExprEvaluationBDD {
        testEval(FeatureExprFactory.bdd)
    }


    private def testEval(f: AbstractFeatureExprFactory) {
        def d(x: String) = f.createDefinedExternal(x)

        val expr = d("a") and d("b")
        assert(expr.evaluate(Set("a", "b")))
        assert(!expr.evaluate(Set("a")))

        val expr2 = d("a").not
        assert(!expr2.evaluate(Set("a", "b")))
        assert(expr2.evaluate(Set("b")))

        val expr3 = d("a") orNot d("b")
        assert(expr3.evaluate(Set("a", "b")))
        assert(!expr3.evaluate(Set("b")))

        val expr4 = d("a") orNot d("b") or f.True
        assert(expr4.evaluate(Set("a", "b")))
    }


    private def assertEquivalent(exp:FeatureExpr, actual:FeatureExpr) = assert(exp equivalentTo  actual,"%s NOT equiv %s".format(exp ,  actual))
    @Test
    def testUniqueQuantificationBDD(){

        FeatureExprFactory.setDefault(FeatureExprFactory.bdd)
        val fb=FeatureExprFactory.createDefinedExternal("b")
        val fc=FeatureExprFactory.createDefinedExternal("c")
        val fd=FeatureExprFactory.createDefinedExternal("d")

        val x = fb and (fc or fd)

        assertEquivalent((fb xor (fb and fd)) ,  x.unique(fc))
        assertEquivalent((fb xor (fb and fc)) ,  x.unique(fd))
        assertEquivalent((fc or fd) xor False ,  x.unique(fb))
    }
    @Test
    def testUniqueQuantificationSAT(){
        FeatureExprFactory.setDefault(FeatureExprFactory.sat)
        val fb=FeatureExprFactory.createDefinedExternal("b")
        val fc=FeatureExprFactory.createDefinedExternal("c")
        val fd=FeatureExprFactory.createDefinedExternal("d")

        val x = fb and (fc or fd)

        assertEquivalent((fb xor (fb and fd)) ,  x.unique(fc))
        assertEquivalent((fb xor (fb and fc)) ,  x.unique(fd))
        assertEquivalent((fc or fd) xor False ,  x.unique(fb))
    }
}
