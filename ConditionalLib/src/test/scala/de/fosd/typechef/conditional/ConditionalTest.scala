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
        assertEquals(Choice(fa, One(2), One(1)), Choice(fa, One("bb"), One("a")).vflatMap(True, (f, x) => One(x.length)))
        assertEquals(Choice(fa, One(2), Choice(fb, One(3), One(5))), Choice(fa, One("bb"), One("a")).vflatMap(True, (f, x) => if (f == fa) One(x.length) else Choice(fb, One(3), One(5))))

    }



    @Test
    def testFold {
        assertEquals(
            Choice(fa, One("ab"), One("b")),
            vfoldRightS(List(Opt(fa, "a"), Opt(True, "b")), One(""), (a: String, b: String) => a + b))
        assertEquals(
            Choice(fb, Choice(fa, One(7), One(5)), Choice(fa, One(3), One(1))),
            vfoldRightS(List(Opt(fa, 2), Opt(True, 1), Opt(fb, 4)), One(0), (a: Int, b: Int) => a + b))
    }

    @Test
    def testFoldExplosion {
        val ol = List.fill(1000)(Opt(fa, 1))

        assertEquals(
            Choice(fa, One(1000), One(0)),
            vfoldRightS(ol, One(0), (a: Int, b: Int) => a + b))
        assertEquals(
            Choice(fa, One(1000), One(1)),
            vfoldRightS(ol, Choice(fa, One(0), One(1)), (a: Int, b: Int) => a + b))

        val vt = Choice(fa, One("X"), One("U"))

        assertEquals(
            vt,
            vfoldRightR(ol, vt, (oentry: Int, v: String) => vt map (ot => if (v == ot) "X" else "U")))


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
    def testSimplify {
        assertEquals(One(1), Choice(fa, One(1), One(2)).simplify(fa))
        assertEquals(One(2), Choice(fa, One(1), One(2)).simplify(fa.not))
        assertEquals(Choice(fa, One(1), One(2)), Choice(fa, One(1), One(2)).simplify(fb))
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
    def testEqualsOp {
        assert(ConditionalLib.equalsOp(One(1), One(1)))
        assert(ConditionalLib.equalsOp(Choice(fa, One(1), One(2)), Choice(fa.not, One(2), One(1))))
        assert(ConditionalLib.equalsOp(
            Choice(fa, Choice(fb, One(1), One(2)), One(3)),
            Choice(fb.not, Choice(fa, One(2), One(3)), Choice(fa, One(1), One(3)))))
    }
    @Test
    def testMapCombinationOp {
        assertEquals(
            Choice(fa, One(true), One(false)),
            mapCombinationOp(
                Choice(fa, One(1), One(2)),
                Choice(fa, One(1), One(3)),
                (x: Int, y: Int) => x equals y
            ))
        assertEquals(
            One(true),
            mapCombinationOp(
                Choice(fa, Choice(fb, One(1), One(2)), One(3)),
                Choice(fb.not, Choice(fa, One(2), One(3)), Choice(fa, One(1), One(3))),
                (x: Int, y: Int) => x equals y
            ).simplify)
        assertEquals(
            Choice(fa, One(true), Choice(fb.not, One(false), One(true))),
            mapCombinationOp(
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
            explode(One(1), One("a"))
        )
        assertEquals(
            Choice(fa, One((1, "a")), One((2, "a"))),
            explode(Choice(fa, One(1), One(2)), One("a"))
        )
        assertEquals(
            Choice(fa, One((1, "a")), One((1, "b"))),
            explode(One(1), Choice(fa, One("a"), One("b")))
        )
        assertEquals(
            Choice(fa, One((1, "a")), One((2, "b"))),
            explode(Choice(fa, One(1), One(2)), Choice(fa, One("a"), One("b")))
        )
        assertEquals(
            Choice(fa, Choice(fb, One((1, "a")), One((1, "b"))), Choice(fb, One((2, "a")), One((2, "b")))),
            explode(Choice(fa, One(1), One(2)), Choice(fb, One("a"), One("b")))
        )
        assertEquals(
            Choice(fa, One((1, "a")), Choice(fa or fb, One((2, "a")), One((2, "b")))),
            explode(Choice(fa, One(1), One(2)), Choice(fa or fb, One("a"), One("b")))
        )


    }


    @Test
    def testFoldCondition {
        val list = List(Opt(fa, 1), Opt(fa.not(), 2), Opt(fa.not(), 3))
        val r = vfoldRight(list, One("-"), True, (f: FeatureExpr, a: Int, b: String) => {
            assert(!f.isTautology());
            One(a.toString + b)
        })
        assertEquals(Choice(fa.not, One("23-"), One("1-")), r)


        val l = vfoldLeft(list, One("-"), True, (f: FeatureExpr, b: String, a: Int) => {
            assert(!f.isTautology());
            One(a.toString + b)
        })
        assertEquals(Choice(fa, One("1-"), One("32-")), l)
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
        val v3 = v2.vmap[Set[Int]](
        fa, {
            (f, x) => if (fa equivalentTo f) x + 10 else x
        })

        println(ConditionalLib.explode(v1, v2))
        println(ConditionalLib.mapCombination[Set[Int], Set[Int], Set[Int]](v1, v2, {
            (x, y) => x ++ y
        }))
        println(v3)
    }

//    @Test
//    def testConditionalInsert {
//        var t1: Conditional[String] = One("true")
//
//        t1 = ConditionalLib.insert(t1, FeatureExprFactory.True, fa, "a")
//        t1 = ConditionalLib.insert(t1, FeatureExprFactory.True, fa not(), "na")
//
//        var t2: Conditional[String] = One("true")
//        t2 = ConditionalLib.insert(t1, FeatureExprFactory.True, fa not(), "na")
//        t2 = ConditionalLib.insert(t1, FeatureExprFactory.True, fa, "a")
//
//        var t3: Conditional[String] = One("true")
//        t3 = ConditionalLib.insert(t3, FeatureExprFactory.True, fa, "a")
//        t3 = ConditionalLib.insert(t3, FeatureExprFactory.True, fa and fb, "ab")
//
//        println(t1)
//        println(t2)
//        println(t3)
//
//    }

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
        t1 = ConditionalLib.vfoldRightS[Set[String], Set[String]](List(Opt(fa.not, Set("c"))), t1, _ ++ _)
        println(t1)
    }


    @Test def testWhenTrue {
        assertEquals(fa and fb, (Choice(fa, Choice(fb, One(true), One(false)), One(false))).when(identity))
        assertEquals(False, (Choice(fa, Choice(fb, One(false), One(false)), One(false))).when(identity))
        assertEquals((fa and fb) or (fa.not and fb.not), (Choice(fa, Choice(fb, One(true), One(false)), Choice(fb, One(false), One(true)))).when(identity))
    }

    @Test def testEquality {
        assertEquals(One(1), One(1))
        assertNotEquals(One(1), One(2))
        assertEquals(Choice(fa, One(1), One(2)), Choice(fa, One(1), One(2)))
        assertNotEquals(Choice(fa, One(1), One(2)), Choice(fa, One(1), One(3)))
        assertNotEquals(Choice(fa, One(1), One(2)), Choice(fb, One(1), One(2)))

        assertEquals(Opt(fa, 1), Opt(fa, 1))
        assertNotEquals(Opt(fa, 2), Opt(fa, 1))
        assertNotEquals(Opt(fa.not, 1), Opt(fa, 1))
        assertTrue(Opt(fa equiv fb, 1) equivalentTo  Opt(fb equiv fa, 1))
    }

    @Test def testOpt: Unit = {
        assertEquals(Opt(fa and fb, 1), Opt(fa, 1) and fb)
        assertEquals(Opt(fa , 1), Opt(fa, 1) and null)
        assertEquals(Opt(fa andNot fb, 1), Opt(fa, 1) andNot fb)
        assertEquals(Opt(fa , 1), Opt(fa, 1) andNot null)
        assertEquals(Opt(fa, 2), Opt(fa, 1).map(_+1))
    }
    @Test def testExists {
        assertEquals(true, Choice(fa, One(1), One(2)).exists(_ > 1))
        assertEquals(false, Choice(fa, One(1), One(2)).exists(_ > 2))
    }
    @Test def testFlatten {
        assertEquals(6, Choice(fa, One(1), Choice(fb, One(2),One(3))).flatten((f,a,b)=>a+b))
    }

    @Test def testToList {
        assertEquals(List((fa, 1), (fa.not and fb, 2), (fa.not andNot fb, 3)), Choice(fa, One(1), Choice(fb, One(2),One(3))).toList)
        assertEquals(List(Opt(fa, 1), Opt(fa.not and fb, 2), Opt(fa.not andNot fb, 3)), Choice(fa, One(1), Choice(fb, One(2),One(3))).toOptList)
    }

    @Test def testCombine {
        assertEquals(One(1), ConditionalLib.combine(One(One(1))))
        assertEquals(Choice(fa, One(1), One(2)), ConditionalLib.combine(One(Choice(fa, One(1), One(2)))))
        assertEquals(Choice(fa, One(1), One(2)), ConditionalLib.combine(Choice(fa, One(One(1)), One(One(2)))))
        assertEquals(Choice(fb, Choice(fa, One(1), One(2)), Choice(fa, One(1), One(3))), ConditionalLib.combine(Choice(fb, One(Choice(fa, One(1), One(2))), One(Choice(fa, One(1), One(3))))))
    }


    @Test def testFlatten2 {
        assertEquals(List(Opt(True, 1), Opt(fb and fa, 2), Opt(fb andNot fa, 3)),
            ConditionalLib.flatten(List(Opt(True, One(1)), Opt(fb, Choice(fa, One(2), One(3))))))
    }


}