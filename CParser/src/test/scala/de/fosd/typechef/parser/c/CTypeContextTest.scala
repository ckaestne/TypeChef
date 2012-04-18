package de.fosd.typechef.parser.c

import org.junit.Test
import de.fosd.typechef.featureexpr.FeatureExprFactory
import FeatureExprFactory._

class CTypeContextTest {

    val fa = createDefinedExternal("fa")
    val fb = createDefinedExternal("fb")

    @Test
    def testContext {
        var c = CTypeContext(Map())
        c = c.addType("a", base)

        println(c)
        assert(c.knowsType("a", base))
        assert(c.knowsType("a", fa))

        c = c.addType("b", fb)

        println(c)
        assert(c.knowsType("b", fb))
        //        assert(!c.knowsType("b", base))

        c = c.addType("b", fb.not)
        println(c)
        assert(c.knowsType("b", base))

        var d = CTypeContext(Map())
        d = d.addType("c", fa)
        d = d.addType("a", base)
        c = c.addType("c", fb)

        val j = c join d
        println(j)
        assert(j.knowsType("c", fa or fb))
        assert(j.knowsType("b", base))
        assert(j.knowsType("a", base))

    }


}