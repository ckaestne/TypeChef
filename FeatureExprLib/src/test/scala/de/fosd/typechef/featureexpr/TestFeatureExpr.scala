package de.fosd.typechef.featureexpr

import org.junit.Ignore

import junit.framework._;
import junit.framework.Assert._
import org.junit.Test
import FeatureExpr._

class TestFeatureExpr extends TestCase {

    @Test
    def assertSimplify(exprA: FeatureExprTree, expectedResult: FeatureExprTree) {
        println("simplify(" + exprA.print + ") = " + exprA.simplify().print() + ", expected " + expectedResult.print())
        assert(exprA.simplify() == expectedResult, "Simplification failed. Found " + exprA.simplify() + " expected " + expectedResult)
    }
    @Test
    def assertCNF(exprA: FeatureExprTree, expectedResult: FeatureExprTree) {
        println("toCNF(" + exprA.print + ") = " + exprA.toCNF.print() + ", expected " + expectedResult.print())
        assert(exprA.toCNF == expectedResult, "Simplification failed. Found " + exprA.simplify() + " expected " + expectedResult)
    }

    def assertIsCNF(expr: FeatureExprTree) {
        _assertIsCNF(expr.toCNF);
    }

    def _assertIsCNF(cnf: FeatureExprTree) {
        println("CNF: " + cnf)
        cnf match {
            case And(children) => for (child <- children) checkLevelOr(child);
            case e => checkLevelOr(e);
        }
    }
    def checkLevelOr(expr: FeatureExprTree) {
        expr match {
            case Or(children) => for (child <- children) checkLevelLiteral(child);
            case e => checkLevelLiteral(e);
        }

    }
    def checkLevelLiteral(expr: FeatureExprTree) {
        expr match {
            case DefinedExternal(name) =>
            case IntegerLit(v) =>
            case Not(DefinedExternal(name)) =>
            case e => assert(false, expr + " is not a literal")
        }
    }

    def testSimplify() {
        assertSimplify(And(List(DefinedExternal("a"))), DefinedExternal("a"))
        //        assertSimplify(And(List(DefinedExternal("a"), DefinedExternal("a"))), DefinedExternal("a"))//deactivated this simplification, due to high costs
        //        assertSimplify(And(List(DefinedExternal("a"), DefinedExternal("b"))), And(List(DefinedExternal("a"), DefinedExternal("b")))) //except the order
        assertSimplify(And(List(BaseFeature(), DefinedExternal("a"))), DefinedExternal("a"))
        assertSimplify(And(List(DeadFeature(), DefinedExternal("a"), DefinedExternal("b"))), DeadFeature())
        //        assertSimplify(And(List(Not(DefinedExternal("a")), DefinedExternal("a"), DefinedExternal("b"))), DeadFeature())

        assertSimplify(Or(List(DefinedExternal("a"))), DefinedExternal("a"))
        //        assertSimplify(Or(List(DefinedExternal("a"), DefinedExternal("a"))), DefinedExternal("a"))
        //        assertSimplify(Or(List(DefinedExternal("a"), DefinedExternal("b"))), Or(List(DefinedExternal("a"), DefinedExternal("b")))) //except the order
        assertSimplify(Or(List(BaseFeature(), DefinedExternal("a"))), BaseFeature())
        assertSimplify(Or(List(DeadFeature(), DefinedExternal("a"))), DefinedExternal("a"))
        //        assertSimplify(Or(List(Not(DefinedExternal("a")), DefinedExternal("a"), DefinedExternal("b"))), BaseFeature())

        //        assertSimplify(new And(DefinedExternal("a"), new And(DefinedExternal("b"), DefinedExternal("c"))), And(List(DefinedExternal("a"), DefinedExternal("b"), DefinedExternal("c"))))
        //        assertSimplify(new Or(DefinedExternal("a"), new Or(DefinedExternal("b"), DefinedExternal("c"))), Or(List(DefinedExternal("a"), DefinedExternal("b"), DefinedExternal("c"))))

        //        assertSimplify(new Or(new Or(DefinedExternal("a"), DefinedExternal("b")), Not(DefinedExternal("b"))), BaseFeature())
        //TODO currently not insisting on too much optimization
        //    assertSimplify(new Or(new Or(DefinedExternal("a"), new Or(DefinedExternal("b"), DefinedExternal("c"))), Not(new Or(DefinedExternal("b"), DefinedExternal("c")))), BaseFeature())
        //    assertSimplify(new Or(List(DefinedExternal("a"), DefinedExternal("b"), DefinedExternal("c"), Not(new Or(DefinedExternal("b"), DefinedExternal("c"))))), BaseFeature())
        //    assertSimplify(new And(And(List(DefinedExternal("a"), DefinedExternal("b"), DefinedExternal("c"))), Not(new And(DefinedExternal("b"), DefinedExternal("c")))), DeadFeature())

        //        assertSimplify(new Or(DeadFeature(), new And(Not(DefinedExternal("a")), BaseFeature())), Not(DefinedExternal("a")))
        //        assertSimplify(new Or(new And(DefinedExternal("a"), DeadFeature()), Not(DefinedExternal("a"))), Not(DefinedExternal("a")))
        //        assertSimplify(new Or(new And(DefinedExternal("a"), DeadFeature()), new And(Not(DefinedExternal("a")), BaseFeature())), Not(DefinedExternal("a")))

    }
    //  def testAdvancedSimplify() {
    //    //(!A & B) v A => A v B
    //    assertSimplify(new Or(new And(Not(DefinedExternal("a")), DefinedExternal("b")), DefinedExternal("a")),
    //      new Or(DefinedExternal("a"), DefinedExternal("b")))
    //    //(!A & B & C)| A => (B & C) | A
    //    assertSimplify(new Or(And(List(Not(DefinedExternal("a")), DefinedExternal("b"), DefinedExternal("c"))), DefinedExternal("a")),
    //      new Or(DefinedExternal("a"), new And(DefinedExternal("b"), DefinedExternal("c"))))
    //    //A&B | !A => B|!A
    //    assertSimplify(new Or(new And(DefinedExternal("a"), DefinedExternal("b")), Not(DefinedExternal("a"))),
    //      new Or(Not(DefinedExternal("a")), DefinedExternal("b")))
    //  }

    def testSimplifyIf() {
        assertSimplify(FeatureExpr.createLessThanEquals(
            new FeatureExprImpl(IntegerLit(1)),
            FeatureExpr.createIf(
                DefinedExternal("CONFIG_64BIT"),
                IntegerLit(64),
                IntegerLit(32))).expr,
            IntegerLit(1))

        // (1 + (1 + (1 + __IF__(defined(a),1,2))))) = __IF__(defined(a),4,5), results overall in BaseFeature because both are true
        assertTrue(FeatureExpr.createPlus(
            new FeatureExprImpl(IntegerLit(1)),
            FeatureExpr.createPlus(
                new FeatureExprImpl(IntegerLit(1)),
                FeatureExpr.createPlus(
                    new FeatureExprImpl(IntegerLit(1)),
                    FeatureExpr.createIf(
                        DefinedExternal("a"),
                        IntegerLit(1),
                        IntegerLit(2))))).isTautology)
        //            FeatureExpr.createIf(
        //                DefinedExternal("a"),
        //                IntegerLit(4),
        //                IntegerLit(5)).expr)
    }

    def testSimplifyNumeric() {
        //&&	<=		<<			1			__IF__				CONFIG_NODES_SHIFT			__THEN__				0			__ELSE__				0		__IF__			CONFIG_64BIT		__THEN__			64		__ELSE__			32	1	  
        assertSimplify(FeatureExpr.createLessThanEquals(
            FeatureExpr.createShiftLeft(
                new FeatureExprImpl(IntegerLit(1)),
                FeatureExpr.createIf(DefinedExternal("s"), IntegerLit(0), IntegerLit(0))),
            FeatureExpr.createIf(DefinedExternal("b"), IntegerLit(64), IntegerLit(32))).and(createInteger(1)).expr, BaseFeature());

        assertSimplify(
            FeatureExpr.createIf(
                new FeatureExprImpl(DefinedExternal("a")),
                FeatureExpr.createLessThanEquals(new FeatureExprImpl(IntegerLit(1)), new FeatureExprImpl(IntegerLit(64))),
                FeatureExpr.createLessThanEquals(new FeatureExprImpl(IntegerLit(1)), new FeatureExprImpl(IntegerLit(32)))).not.expr, DeadFeature());
    }

    def testCFN() {
        assertIsCNF(And(List(new Or(DefinedExternal("a1"), DefinedExternal("b")),
            new Or(new And(DefinedExternal("a2"), new Or(DefinedExternal("b"), DefinedExternal("c"))),
                new Or(DefinedExternal("a1"), DefinedExternal("c"))),
            new Or(DefinedExternal("a2"), DefinedExternal("c")))))

        assertEquals(Not(DefinedExternal("X")), Not(DefinedExternal("X")).toCNF);

        val v = new Or(DefinedExternal("a"), new And(DefinedExternal("b"), DefinedExternal("c"))).toCNF;
        println(v)
        val vs = v.simplify
        println(vs)

        //(!((defined(a) && defined(b))) || defined(b))
        assertIsCNF(new Or(Not(new And(DefinedExternal("a"), DefinedExternal("b"))), DefinedExternal("b")).toCNF)

        val a = DefinedExternal("a")
        val b = DefinedExternal("b")
        val c = DefinedExternal("c")
        val expr2 = new FeatureExprImpl(Or(Not(And(a, b)), c))
        expr2.toCNF
        expr2.toEquiCNF
        val expr = new FeatureExprImpl(Or(Not(And(a, a)), a))
        expr.toCNF
        expr.toEquiCNF

    }

    def testCNFIf() {
        assertCNF(FeatureExpr.createEquals(
            FeatureExpr.createIf(
                DefinedExternal("a"),
                IntegerLit(1),
                IntegerLit(0)),
            FeatureExpr.createInteger(0)).expr, Not(DefinedExternal("a")))

        //(__IF__(!(defined(CONFIG_NR_CPUS)),1,0) > 1)
        assertSimplify(FeatureExpr.createGreaterThan(
            FeatureExpr.createIf(
                Not(DefinedExternal("a")),
                IntegerLit(1),
                IntegerLit(0)),
            FeatureExpr.createInteger(1)).expr, DeadFeature())

    }

    @Ignore
    def testEquality() {
        assertEquals(FeatureExpr.createDefinedExternal("a"), FeatureExpr.createDefinedExternal("a"))
        assertEquals(FeatureExpr.createDefinedExternal("a"), FeatureExpr.createDefinedExternal("a").or(FeatureExpr.createDefinedExternal("a")))
        assertTrue(FeatureExpr.createDefinedExternal("a").or(FeatureExpr.createDefinedExternal("a").not) equivalentTo FeatureExpr.base)
        assertFalse(FeatureExpr.createDefinedExternal("a").or(FeatureExpr.createDefinedExternal("a").not) equals FeatureExpr.base)
        assertTrue(FeatureExpr.createDefinedExternal("a").and(FeatureExpr.createDefinedExternal("b")) equivalentTo FeatureExpr.createDefinedExternal("b").and(FeatureExpr.createDefinedExternal("a")))
        assertFalse(FeatureExpr.createDefinedExternal("a").and(FeatureExpr.createDefinedExternal("b")) equals (FeatureExpr.createDefinedExternal("b").and(FeatureExpr.createDefinedExternal("a"))))
    }

    @Test
    def testFeatureModel = {

        val a = FeatureExpr.createDefinedExternal("a")
        val b = FeatureExpr.createDefinedExternal("b")
        val c = FeatureExpr.createDefinedExternal("c")
        val d = FeatureExpr.createDefinedExternal("d")
        val e = FeatureExpr.createDefinedExternal("e")

        val fm = FeatureModel.create((a implies b) and (a mex c) and d)
        val fma = FeatureModel.create(a)

        assertTrue(a.isTautology(fma))


        assertFalse((a implies b).isTautology())
        assertTrue((a implies b).isTautology(fm))
        assertTrue(d.isTautology(fm))
        assertFalse(e.isTautology(fm))
    }

}
