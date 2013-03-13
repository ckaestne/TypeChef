package de.fosd.typechef.parser.test

import org.junit._
import org.junit.Assert._
import de.fosd.typechef.parser._
import de.fosd.typechef.featureexpr.FeatureExprFactory._
import de.fosd.typechef.parser.test.parsers._
import de.fosd.typechef.conditional._

class JoinOptListTest extends MultiFeatureParser {
  type Elem = MyToken
  type TypeContext = Any

  val fa = createDefinedExternal("a")
  val fb = createDefinedExternal("b")
  val fx = createDefinedExternal("x")


  @Test def testJoinOptList {
    val o = Opt(fa, "CommonA") :: Opt(fb, "CommonB") :: Nil
    val o1 = Opt(fa, "OnlyA") :: o

    val j = joinOptLists(o1, o, fx)
    assertEquals(Opt(fa and fx, "OnlyA") :: o, j)
  }

  @Test def testJoinOptList2 {
    val o = Opt(fa, "CommonA") :: Opt(fb, "CommonB") :: Nil
    val o1 = Opt(fa, "OnlyA") :: o
    val o2 = Opt(fb, "OnlyB") :: o

    val j = joinOptLists(o1, o2, fx)
    assertEquals(Opt(fb andNot fx, "OnlyB") :: Opt(fa and fx, "OnlyA") :: o, j)
  }

  @Test def testJoinOptListEq {
    val o = Opt(fa, "CommonA") :: Opt(fb, "CommonB") :: Nil
    val o1 = Opt(fa, "Only") :: o
    val o2 = Opt(fb, "Only") :: o

    val j = joinOptLists(o1, o2, fx)
    assertEquals(Opt((fa and fx) or (fb andNot fx), "Only") :: o, j)
  }

  //    @Test def testJoinOptList3 {
  //        val o = Opt(fa, "CommonA") :: Opt(fb, "CommonB") :: Nil
  //        val o1 = Opt(fa, "OnlyA") :: o
  //        val o2 = Opt(fb, "OnlyB") :: o
  //
  //        assertEquals(o1 , joinOptLists(o1, o2, True))
  //        assertEquals(o2 , joinOptLists(o1, o2, False))
  //    }

}