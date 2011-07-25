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
    def testExplode {
        assertEquals(
            Choice(fa, One(List("a", "b")), One(List("b"))),
            explodeOptList(List(Opt(fa, "a"), Opt(base, "b"))))
    }


}