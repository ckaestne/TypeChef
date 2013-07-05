package de.fosd.typechef.featureexpr

import junit.framework.TestCase
import org.junit.Test


/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 05.01.11
 * Time: 17:17
 * To change this template use File | Settings | File Templates.
 */

class FeatureExprTest extends TestCase {

  import org.junit.Assert._
  import FeatureExprFactory._

  @Test
  def testBasics {
    assertEquals(feature("a"), feature("a"))
    assertEquals(feature("a") and True, feature("a"))
    assertEquals(feature("a") or True, True)
    assertEquals(feature("a") and False, False)
    assertEquals(feature("a") or False, feature("a"))
    assertEquals(a and b and False, False)
    assertEquals(False and a and b, False)
    assertEquals(feature("a") orNot feature("a"), True)
    assertEquals(feature("a") andNot feature("a"), False)
    assertEquals(feature("a") and feature("a"), feature("a"))
    assertEquals(feature("a") or feature("a"), feature("a"))
    assertEquals(feature("a") or feature("a") and feature("a"), feature("a"))
    assertEquals(feature("a") and feature("b"), feature("a") and feature("b"))
    assertEquals(feature("a") and feature("b"), feature("b") and feature("a"))
    assertEquals(feature("a") or feature("b"), feature("a") or feature("b"))
    assertEquals(feature("a") or feature("b"), feature("b") or feature("a"))
    assertEquals(feature("a").not.not, feature("a"))
    assertEquals(True.not, False)
    assertEquals(False.not, True)
    assertEquals(a and b and a, a and b)
    assertEquals((a and b and c) and (a and b and c), a and b and c)
    assertEquals(a or b or a, a or b)
    assertEquals((a and b) and (b.not and a), False)
    assertEquals((a or b) or (b.not or a), True)
    assertEquals((a andNot b) or b, (a andNot b) or b)

    assertEquals(a, True and a)
    assertEquals(a, a and True)
    assertEquals(False, a and False)
    assertEquals(False, False and a)

    assertEquals(a, a or False)
    assertEquals(a, False or a)
    assertEquals(True, True or a)
    assertEquals(True, a or True)


  }

  val d = FeatureExprFactory.default

  @Test def testIf {
    assertEquals(d.createBooleanIf(feature("a"), True, False), feature("a"))
    assertEquals(d.createBooleanIf(feature("a"), False, True), feature("a").not)
    assertEquals(d.createBooleanIf(feature("a"), feature("b") orNot feature("b"), False), feature("a"))
    assertEquals(d.createBooleanIf(True, True, False), True)
    assertEquals(d.createBooleanIf(True, feature("a"), feature("b")), feature("a"))
    assertEquals(d.createBooleanIf(False, feature("a"), feature("b")), feature("b"))
  }

  @Test def testComparison {
    assertEquals(createLT(v(2), v(4)), True)
    assertEquals(createLT(d.createIf(feature("a"), v(1), v(2)), v(4)), True)
    assertEquals(createLT(d.createIf(feature("a"), v(1), v(5)), v(4)), feature("a"))
    assertEquals(createLT(d.createIf(a, v(1), v(5)), d.createIf(b, v(2), v(6))), a or (a.not andNot b))
    assertEquals(createLT(d.createIf(feature("a"), v(1), v(5)), d.createIf(feature("a"), v(2), v(6))), True)
    assertEquals(createLT(d.createIf(a, d.createIf(b, v(1), v(2)), d.createIf(b, v(3), v(4))), v(5)), True)
    assertEquals(createLT(d.createIf(a, d.createIf(b, v(1), v(2)), d.createIf(b, v(3), v(4))), v(2)), a and b)
  }

  @Test def testOperations {
    assertEquals(d.createPlus(v(2), v(4)), v(6))
    assertEquals(d.createPlus(d.createIf(feature("a"), v(1), v(2)), v(4)), d.createIf(feature("a"), v(5), v(6)))
    assertEquals(createLT(d.createIf(a, d.createPlus(d.createIf(b, v(1), v(2)), v(10)), d.createIf(b, v(3), v(4))), v(5)), a.not)
  }

  @Test def testParserPrecedenceTest {
    val p = new FeatureExprParser()
    assertEquals(p.parse("def(a) && def(b) || def(c)"), p.parse("(def(a) && def(b)) || def(c)"))

    assertTrue(p.parse("def(a) && def(b) || def(c)").equivalentTo((a and b) or c))


  }

  @Test def testAtMostLeastOne {
    val (a, b, c) = (feature("a"), feature("b"), feature("c"))

    val alo = new FeatureExprParser().atLeastOne(List(a, b, c))

    assert(!alo.isTautology())
    assert(alo.and(a).isSatisfiable())
    assert(alo.andNot(a).isSatisfiable())
    assert(alo.and(a).and(b).isSatisfiable())
    assert(!alo.andNot(a).andNot(b).andNot(c).isSatisfiable())

    val amo = new FeatureExprParser().atMostOne(List(a, b, c))
    assert(!amo.isTautology())
    assert(!amo.and(a).isTautology())
    assert(amo.and(a).isSatisfiable())
    assert(amo.andNot(a).isSatisfiable())
    assert(amo.and(a).and(b).isContradiction())
    assert(amo.and(a).and(c).isContradiction())
    assert(amo.and(b).and(c).isContradiction())
    assert(amo.and(a).andNot(b).andNot(c).isSatisfiable())
    assert(amo.and(c).andNot(b).andNot(a).isSatisfiable())
    assert(amo.andNot(a).andNot(b).andNot(c).isSatisfiable())

    val one = new FeatureExprParser().oneOf(List(a, b, c))
    assert(!one.isTautology())
    assert(!one.and(a).isTautology())
    assert(one.andNot(a).isSatisfiable())
    assert(one.and(a).and(b).isContradiction())
    assert(one.and(a).and(c).isContradiction())
    assert(one.and(b).and(c).isContradiction())
    assert(one.and(a).andNot(b).andNot(c).isSatisfiable())
    assert(one.and(c).andNot(b).andNot(a).isSatisfiable())
    assert(one.andNot(a).andNot(b).andNot(c).isContradiction())

  }

  @Test def testPairs {
    val (a, b, c, d) = (feature("a"), feature("b"), feature("c"), feature("d"))

    val pairs = new FeatureExprParser().pairs(List(a, b, c, d)).toList
    //        println(pairs)
    assertEquals(List((a, b), (a, c), (a, d), (b, c), (b, d), (c, d)), pairs)
  }


  def v(value: Int): FeatureExprValue = d.createInteger(value)

  def not(v: FeatureExpr) = v.not

  //Leave these as def, not val, maybe (???) to test caching more.
  def a = feature("a")

  def b = feature("b")

  def c = feature("c")

  def feature(n: String) = createDefinedExternal(n)

  def createLT(a: FeatureExprValue, b: FeatureExprValue) = d.createLessThan(a, b)


}