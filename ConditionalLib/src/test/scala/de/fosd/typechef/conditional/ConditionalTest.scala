package de.fosd.typechef.conditional

import org.junit._
import Assert._
import ConditionalLib._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import de.fosd.typechef.featureexpr.FeatureExprFactory._

class ConditionalTest {

    val fa = createDefinedExternal("a")
    val fb = createDefinedExternal("b")

    @Test
    def testMap {
        assertEquals(One(1), One("a").map(_.length))
        assertEquals(Choice(fa, One(2), One(1)), Choice(fa, One("bb"), One("a")).map(_.length))
        assertEquals(Choice(fa, One(2), One(1)), Choice(fa, One("bb"), One("a")).mapfr(True, (f, x) => One(x.length)))
        assertEquals(Choice(fa, One(2), Choice(fb, One(3), One(5))), Choice(fa, One("bb"), One("a")).mapfr(True, (f, x) => if (f == fa) One(x.length) else Choice(fb, One(3), One(5))))

    }

    @Test
    def testFold {
        assertEquals(
            Choice(fa, One("ab"), One("b")),
            conditionalFoldRight(List(Opt(fa, "a"), Opt(True, "b")), One(""), (a: String, b: String) => a + b))
        assertEquals(
            Choice(fb, Choice(fa, One(7), One(5)), Choice(fa, One(3), One(1))),
            conditionalFoldRight(List(Opt(fa, 2), Opt(True, 1), Opt(fb, 4)), One(0), (a: Int, b: Int) => a + b))
    }

    @Test
    def testFoldExplosion {
        val ol = List.fill(1000)(Opt(fa, 1))

        assertEquals(
            Choice(fa, One(1000), One(0)),
            conditionalFoldRight(ol, One(0), (a: Int, b: Int) => a + b))
        assertEquals(
            Choice(fa, One(1000), One(1)),
            conditionalFoldRight(ol, Choice(fa, One(0), One(1)), (a: Int, b: Int) => a + b))

        val vt = Choice(fa, One("X"), One("U"))

        assertEquals(
            vt,
            conditionalFoldRightR(ol, vt, (oentry: Int, v: String) => vt map (ot => if (v == ot) "X" else "U")))


        assertEquals(
            Choice(fa, One(List.fill(1000)(1)), One(List())),
            ConditionalLib.explodeOptList(ol))
    }


    @Test
    def testExplode {
        assertEquals(
            Choice(fa, One(List("a", "b")), One(List("b"))),
            explodeOptList(List(Opt(fa, "a"), Opt(True, "b"))))
        assertEquals(
            Choice(fa, One(List("a")), One(List("b"))),
            explodeOptList(List(Opt(fa.not, "b"), Opt(fa, "a"))))
        assertEquals(
            Choice(fa, Choice(fb, One(List("b", "a")), One(List("a"))), Choice(fb, One(List("b")), One(List()))),
            explodeOptList(List(Opt(fb, "b"), Opt(fa, "a"))))
    }

    @Test
    def testFindSubtree {
        assertEquals(One(1), findSubtree(fa, Choice(fa, One(1), One(2))))
        assertEquals(One(2), findSubtree(fa.not, Choice(fa, One(1), One(2))))
        assertEquals(Choice(fa, One(1), One(2)), findSubtree(fb, Choice(fa, One(1), One(2))))
    }

    @Test
    def testEquals {
        assert(ConditionalLib.equals(One(1), One(1)))
        assert(ConditionalLib.equals(Choice(fa, One(1), One(2)), Choice(fa.not, One(2), One(1))))
        assert(ConditionalLib.equals(
            Choice(fa, Choice(fb, One(1), One(2)), One(3)),
            Choice(fb.not, Choice(fa, One(2), One(3)), Choice(fa, One(1), One(3)))))
    }

    @Test
    def testCompare {
        assertEquals(
            Choice(fa, One(true), One(false)),
            compare(
                Choice(fa, One(1), One(2)),
                Choice(fa, One(1), One(3)),
                (x: Int, y: Int) => x equals y
            ))
        assertEquals(
            One(true),
            compare(
                Choice(fa, Choice(fb, One(1), One(2)), One(3)),
                Choice(fb.not, Choice(fa, One(2), One(3)), Choice(fa, One(1), One(3))),
                (x: Int, y: Int) => x equals y
            ).simplify)
        assertEquals(
            Choice(fa, One(true), Choice(fb.not, One(false), One(true))),
            compare(
                Choice(fa, Choice(fb, One(1), One(2)), One(3)),
                Choice(fb.not, Choice(fa, One(2), One(5)), Choice(fa, One(1), One(3))),
                (x: Int, y: Int) => x equals y
            ).simplify)

    }

    @Test
    def testLast {
        assertEquals(One(None), lastEntry(List()))
        assertEquals(One(Some(2)), lastEntry(List(Opt(fa, 1), Opt(True, 2))))
        assertEquals(Choice(fa, One(Some(2)), One(None)), lastEntry(List(Opt(fa, 2))))
        assertEquals(
            Choice(fa, One(Some(2)), One(Some(1))),
            lastEntry(List(Opt(True, 1), Opt(fa, 2))))
        assertEquals(
            Choice(fa, One(Some(2)), Choice(fb, One(Some(1)), One(None))),
            lastEntry(List(Opt(fb, 1), Opt(fa, 2))))

    }

    @Test
    def testZip {
        assertEquals(
            One((1, "a")),
            zip(One(1), One("a"))
        )
        assertEquals(
            Choice(fa, One((1, "a")), One((2, "a"))),
            zip(Choice(fa, One(1), One(2)), One("a"))
        )
        assertEquals(
            Choice(fa, One((1, "a")), One((1, "b"))),
            zip(One(1), Choice(fa, One("a"), One("b")))
        )
        assertEquals(
            Choice(fa, One((1, "a")), One((2, "b"))),
            zip(Choice(fa, One(1), One(2)), Choice(fa, One("a"), One("b")))
        )
        assertEquals(
            Choice(fa, Choice(fb, One((1, "a")), One((1, "b"))), Choice(fb, One((2, "a")), One((2, "b")))),
            zip(Choice(fa, One(1), One(2)), Choice(fb, One("a"), One("b")))
        )
        assertEquals(
            Choice(fa, One((1, "a")), Choice(fa or fb, One((2, "a")), One((2, "b")))),
            zip(Choice(fa, One(1), One(2)), Choice(fa or fb, One("a"), One("b")))
        )


    }


    @Test
    def testFoldCondition {
        val l = List(Opt(fa, 1), Opt(fa.not(), 2), Opt(fa.not(), 3))
        val r = conditionalFoldRightFR(l, One("-"), True, (f: FeatureExpr, a: Int, b: String) => {
            assert(!f.isTautology());
            println(f + ", " + a + ", " + b);
            One(a.toString + b)
        })
        println(r)

        assertEquals(Choice(fa.not, One("23-"), One("1-")), r)
    }

    @Test
    def testConditionalMap {
        var a = new ConditionalMap[String, Int]()

        a = a.+("a", fa, 3)
        assertEquals(Choice(fa, One(3), One(-1)), a.getOrElse("a", -1))

        a = a +("a", fa.not(), 2)
        assertEquals(Choice(fa.not, One(2), One(3)), a.getOrElse("a", -1))

        a = a.+("a", fa, 4)
        val v2 = Choice(fa, One(4), One(2))
        assertEquals(v2, a.getOrElse("a", -1))

        a = a.+("a", fb, 5)
        assertEquals(Choice(fb, One(5), v2), a.getOrElse("a", -1))
    }

    @Test
    def testConditionalMapF {
        val v1: Choice[Set[Int]] = Choice(fa, One(Set(1, 2, 3)), One(Set(-1, -2, -3)))
        val v2: Choice[Set[Int]] = Choice(fa, One(Set(4, 5, 6)), One(Set()))
        val v3 = v2.mapf[Set[Int]](
        fa, {
            (f, x) => if (fa equivalentTo f) x + 10 else x
        })

        println(ConditionalLib.zip(v1, v2))
        println(ConditionalLib.mapCombination[Set[Int], Set[Int], Set[Int]](v1, v2, {
            (x, y) => x ++ y
        }))
        println(v3)
    }

    @Test
    def testConditionalInsert {
        var t1: Conditional[String] = One("true")

        t1 = ConditionalLib.insert(t1, FeatureExprFactory.True, fa, "a")
        t1 = ConditionalLib.insert(t1, FeatureExprFactory.True, fa not(), "na")

        var t2: Conditional[String] = One("true")
        t2 = ConditionalLib.insert(t1, FeatureExprFactory.True, fa not(), "na")
        t2 = ConditionalLib.insert(t1, FeatureExprFactory.True, fa, "a")

        var t3: Conditional[String] = One("true")
        t3 = ConditionalLib.insert(t3, FeatureExprFactory.True, fa, "a")
        t3 = ConditionalLib.insert(t3, FeatureExprFactory.True, fa and fb, "ab")

        println(t1)
        println(t2)
        println(t3)

    }

    @Test
    def testConditionalSimplify {
        var t1: Conditional[Option[String]] = Choice(fa, One(Some("fa")), One(None))

        var t2 = Choice(fa, t1, One(Some("nfa")))

        println(t2)
        println(t2.simplify)
    }

    @Test def testForLiveness {
        var t1: Conditional[Set[String]] = One(Set())

        println(t1)
        t1 = ConditionalLib.conditionalFoldRight[Set[String], Set[String]](List(Opt(fa.not, Set("c"))), t1, _ ++ _)
        println(t1)
    }


    @Test def testIfTrue {
        import ConditionalLib.isTrue
        assertEquals(fa and fb, isTrue(Choice(fa, Choice(fb, One(true), One(false)), One(false))))
        assertEquals(False, isTrue(Choice(fa, Choice(fb, One(false), One(false)), One(false))))
        assertEquals((fa and fb) or (fa.not and fb.not), isTrue(Choice(fa, Choice(fb, One(true), One(false)), Choice(fb, One(false), One(true)))))
    }
}