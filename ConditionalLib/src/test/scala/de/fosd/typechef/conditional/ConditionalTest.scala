package de.fosd.typechef.conditional

import org.junit._
import Assert._
import ConditionalLib._
import de.fosd.typechef.featureexpr.FeatureExpr
import FeatureExpr._

class ConditionalTest {

    val fa = createDefinedExternal("a")
    val fb = createDefinedExternal("b")

    @Test
    def testMap {
        assertEquals(TOne(1), TOne("a").map(_.length))
        assertEquals(TChoice(fa, TOne(2), TOne(1)), TChoice(fa, TOne("bb"), TOne("a")).map(_.length))
        assertEquals(TChoice(fa, TOne(2), TOne(1)), TChoice(fa, TOne("bb"), TOne("a")).mapfr(base, (f, x) => TOne(x.length)))
        assertEquals(TChoice(fa, TOne(2), TChoice(fb, TOne(3), TOne(5))), TChoice(fa, TOne("bb"), TOne("a")).mapfr(base, (f, x) => if (f == fa) TOne(x.length) else TChoice(fb, TOne(3), TOne(5))))

    }

    @Test
    def testFold {
        assertEquals(
            TChoice(fa, TOne("ab"), TOne("b")),
            conditionalFoldRight(List(Opt(fa, "a"), Opt(base, "b")), TOne(""), (a: String, b: String) => a + b))
        assertEquals(
            TChoice(fb, TChoice(fa, TOne(7), TOne(5)), TChoice(fa, TOne(3), TOne(1))),
            conditionalFoldRight(List(Opt(fa, 2), Opt(base, 1), Opt(fb, 4)), TOne(0), (a: Int, b: Int) => a + b))
    }


    @Test
    def testExplode {
        assertEquals(
            TChoice(fa, TOne(List("a", "b")), TOne(List("b"))),
            explodeOptList(List(Opt(fa, "a"), Opt(base, "b"))))
        assertEquals(
            TChoice(fa, TOne(List("a")), TOne(List("b"))),
            explodeOptList(List(Opt(fa.not, "b"), Opt(fa, "a"))))
        assertEquals(
            TChoice(fa, TChoice(fb, TOne(List("b", "a")), TOne(List("a"))), TChoice(fb, TOne(List("b")), TOne(List()))),
            explodeOptList(List(Opt(fb, "b"), Opt(fa, "a"))))
    }

    @Test
    def testFindSubtree {
        assertEquals(TOne(1), findSubtree(fa, TChoice(fa, TOne(1), TOne(2))))
        assertEquals(TOne(2), findSubtree(fa.not, TChoice(fa, TOne(1), TOne(2))))
        assertEquals(TChoice(fa, TOne(1), TOne(2)), findSubtree(fb, TChoice(fa, TOne(1), TOne(2))))
    }

    @Test
    def testEquals {
        assert(ConditionalLib.equals(TOne(1), TOne(1)))
        assert(ConditionalLib.equals(TChoice(fa, TOne(1), TOne(2)), TChoice(fa.not, TOne(2), TOne(1))))
        assert(ConditionalLib.equals(
            TChoice(fa, TChoice(fb, TOne(1), TOne(2)), TOne(3)),
            TChoice(fb.not, TChoice(fa, TOne(2), TOne(3)), TChoice(fa, TOne(1), TOne(3)))))
    }

    @Test
    def testCompare {
        assertEquals(
            TChoice(fa, TOne(true), TOne(false)),
            compare(
                TChoice(fa, TOne(1), TOne(2)),
                TChoice(fa, TOne(1), TOne(3)),
                (x: Int, y: Int) => x equals y
            ))
        assertEquals(
            TOne(true),
            compare(
                TChoice(fa, TChoice(fb, TOne(1), TOne(2)), TOne(3)),
                TChoice(fb.not, TChoice(fa, TOne(2), TOne(3)), TChoice(fa, TOne(1), TOne(3))),
                (x: Int, y: Int) => x equals y
            ).simplify)
        assertEquals(
            TChoice(fa, TOne(true), TChoice(fb.not, TOne(false), TOne(true))),
            compare(
                TChoice(fa, TChoice(fb, TOne(1), TOne(2)), TOne(3)),
                TChoice(fb.not, TChoice(fa, TOne(2), TOne(5)), TChoice(fa, TOne(1), TOne(3))),
                (x: Int, y: Int) => x equals y
            ).simplify)

    }

    @Test
    def testLast {
        assertEquals(TOne(None), lastEntry(List()))
        assertEquals(TOne(Some(2)), lastEntry(List(Opt(fa, 1), Opt(base, 2))))
        assertEquals(TChoice(fa, TOne(Some(2)), TOne(None)), lastEntry(List(Opt(fa, 2))))
        assertEquals(
            TChoice(fa, TOne(Some(2)), TOne(Some(1))),
            lastEntry(List(Opt(base, 1), Opt(fa, 2))))
        assertEquals(
            TChoice(fa, TOne(Some(2)), TChoice(fb, TOne(Some(1)), TOne(None))),
            lastEntry(List(Opt(fb, 1), Opt(fa, 2))))

    }

    @Test
    def testZip {
        assertEquals(
            TOne((1, "a")),
            zip(TOne(1), TOne("a"))
        )
        assertEquals(
            TChoice(fa, TOne((1, "a")), TOne((2, "a"))),
            zip(TChoice(fa, TOne(1), TOne(2)), TOne("a"))
        )
        assertEquals(
            TChoice(fa, TOne((1, "a")), TOne((1, "b"))),
            zip(TOne(1), TChoice(fa, TOne("a"), TOne("b")))
        )
        assertEquals(
            TChoice(fa, TOne((1, "a")), TOne((2, "b"))),
            zip(TChoice(fa, TOne(1), TOne(2)), TChoice(fa, TOne("a"), TOne("b")))
        )
        assertEquals(
            TChoice(fa, TChoice(fb, TOne((1, "a")), TOne((1, "b"))), TChoice(fb, TOne((2, "a")), TOne((2, "b")))),
            zip(TChoice(fa, TOne(1), TOne(2)), TChoice(fb, TOne("a"), TOne("b")))
        )
        assertEquals(
            TChoice(fa, TOne((1, "a")), TChoice(fa or fb, TOne((2, "a")), TOne((2, "b")))),
            zip(TChoice(fa, TOne(1), TOne(2)), TChoice(fa or fb, TOne("a"), TOne("b")))
        )


    }


}