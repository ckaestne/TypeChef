package de.fosd.typechef.conditional

import org.junit._
import Assert._
import ConditionalLib._
import de.fosd.typechef.featureexpr.FeatureExpr
import FeatureExpr._


class ConditionalTest {


    @Test
    def testMap {

    }

    val fa=createDefinedExternal("a")
    val fb=createDefinedExternal("b")

    @Test
    def testExplode {
        assertEquals(
            Choice(fa,One( List("a","b")),One(List("b"))),
            explodeOptList(List(Opt(fa,"a"),Opt(base,"b"))))
    }


}