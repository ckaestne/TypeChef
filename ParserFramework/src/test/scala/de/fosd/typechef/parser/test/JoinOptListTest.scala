package de.fosd.typechef.parser.test

import org.junit._
import org.junit.Assert._
import de.fosd.typechef.parser._
import de.fosd.typechef.featureexpr.FeatureExprFactory._
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.test.parsers._
import de.fosd.typechef.conditional._

class JoinOptListTest extends MultiFeatureParser {
    type Elem = MyToken
    type TypeContext = Any

    val fa = createDefinedExternal("a")
    val fb = createDefinedExternal("b")


    @Test def testJoinOptList {
        val o = Opt(fa, "CommonA") :: Opt(fb, "CommonB") :: Nil
        val o1 = Opt(fa, "OnlyA") :: o

        val j = joinOptLists(o1, o)
        assertEquals(o1, j)
    }
    @Test def testJoinOptList2 {
        val o = Opt(fa, "CommonA") :: Opt(fb, "CommonB") :: Nil
        val o1 = Opt(fa, "OnlyA") :: o
        val o2 = Opt(fb, "OnlyB") :: o

        val j = joinOptLists(o1, o2)
        assertEquals(Opt(fb, "OnlyB") :: Opt(fa, "OnlyA") :: o, j)
    }


}