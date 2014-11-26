package de.fosd.typechef.featureexpr

import junit.framework.TestCase
import org.junit.{Assert, Test}
import scala.io.Source


/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 05.01.11
 * Time: 17:17
 * To change this template use File | Settings | File Templates.
 */

class FeatureModelTest extends TestCase {

  import FeatureExprFactory._

  val dimacsFile = this.getClass.getResource("/small.dimacs").toURI

  @Test
  def testBasics {
    testFm(bdd)
  }

  @Test
  def testBasicsSat {
    testFm(sat)
  }


  def testFm(f: AbstractFeatureExprFactory) {
    def d(n: String) = f.createDefinedExternal(n)
    val a = d("CONFIG_A")
    val b = d("CONFIG_B")
    val c = d("CONFIG_C")
    val x = d("CONFIG_X")

    //A -> B, B->C
    val fmDimacs = f.featureModelFactory.createFromDimacsFile(Source.fromURI(dimacsFile))
    val approx = (x implies b) and (a implies b)
    val fmApprox = f.featureModelFactory.create(approx)

    Assert.assertFalse((a implies b).isTautology())
    Assert.assertTrue((a implies b).isTautology(fmDimacs))
    Assert.assertTrue((a implies b).isTautology(fmApprox))
    Assert.assertFalse((a implies c).isTautology())
    Assert.assertTrue((a implies c).isTautology(fmDimacs))

    val fmCombined = fmDimacs.and(approx)


    Assert.assertTrue((x implies c).isTautology(fmCombined))



    val fmWithX = fmCombined.assumeTrue("CONFIG_X")
    Assert.assertTrue(x.isTautology(fmWithX))
    Assert.assertTrue(c.isTautology(fmWithX))


  }

}