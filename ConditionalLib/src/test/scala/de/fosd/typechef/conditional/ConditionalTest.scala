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
        assertEquals(One(1), One("a").map(_.length))
        assertEquals(Choice(fa, One(2), One(1)), Choice(fa, One("bb"), One("a")).map(_.length))
        assertEquals(Choice(fa, One(2), One(1)), Choice(fa, One("bb"), One("a")).mapfr(base, (f, x) => One(x.length)))
        assertEquals(Choice(fa, One(2), Choice(fb, One(3), One(5))), Choice(fa, One("bb"), One("a")).mapfr(base, (f, x) => if (f == fa) One(x.length) else Choice(fb, One(3), One(5))))

    }

    @Test
    def testFold {
        assertEquals(
            Choice(fa, One("ab"), One("b")),
            conditionalFoldRight(List(Opt(fa, "a"), Opt(base, "b")), One(""), (a: String, b: String) => a + b))
        assertEquals(
            Choice(fb, Choice(fa, One(7), One(5)), Choice(fa, One(3), One(1))),
            conditionalFoldRight(List(Opt(fa, 2), Opt(base, 1), Opt(fb, 4)), One(0), (a: Int, b: Int) => a + b))
    }


    @Test
    def testExplode {
        assertEquals(
            Choice(fa, One(List("a", "b")), One(List("b"))),
            explodeOptList(List(Opt(fa, "a"), Opt(base, "b"))))
        assertEquals(
            Choice(fa, One(List("a")), One(List("b"))),
            explodeOptList(List(Opt(fa.not, "b"), Opt(fa, "a"))))
        assertEquals(
            Choice(fa, Choice(fb, One(List("b", "a")), One(List("a"))), Choice(fb, One(List("b")), One(List()))),
            explodeOptList(List(Opt(fb, "b"), Opt(fa, "a"))))
    }


}