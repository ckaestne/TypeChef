package de.fosd.typechef.parser.test

import org.junit.Test
import org.junit.Assert._
import parsers.CharDigitParser
import de.fosd.typechef.featureexpr.FeatureExprFactory._
import de.fosd.typechef.parser.~
import de.fosd.typechef.featureexpr.FeatureExpr

/**
 * Created with IntelliJ IDEA.
 * User: ckaestne
 * Date: 2/15/13
 * Time: 9:13 AM
 * To change this template use File | Settings | File Templates.
 */
class ParseResultsTest {
  val fa = createDefinedExternal("A")
  val fb = createDefinedExternal("B")
  val p = new CharDigitParser()
  val s1 = new p.Success[Int](1, null)
  val s2 = new p.Success[Int](2, null)
  val s3 = new p.Success[Int](3, null)
  val s4 = new p.Success[Int](4, null)
  val f = new p.Failure("e", null, Nil)

  @Test
  def testParseResults() {
    val s12 = s1.seq(True, s2)

    println(s12)
    assertEquals(new p.Success[Int ~ Int](new ~(1, 2), null), s12)
  }

  @Test
  def testParseResultsSplit() {
    val sp = new p.SplittedParseResult(fa, s1, f) // SPLIT(A, 1, ERR)
    assertEquals("SplittedParseResult(def(A),Success(1,null),Failure(e,null,List()))", sp.toString)

    val sp2 = s2.seq(True, sp) // expecting SPLIT(A, 1~2, ERR)

    assertEquals("SplittedParseResult(def(A),Success((2~1),null),Failure(e,null,List()))", sp2.toString)
  }


  @Test
  def testParseResultsSplit2() {
    val sp = new p.SplittedParseResult(fa, s1, s2) // SPLIT(A, 1, 2)
    val sp2 = s3.seq(True, sp) // expecting SPLIT(A, 3~1, 3~2)

    assertEquals("SplittedParseResult(def(A),Success((3~1),null),Success((3~2),null))", sp2.toString)
  }

  @Test
  def testParseResultsSplit3() {
    val sp12 = new p.SplittedParseResult(fa, s1, s2) // SPLIT(A, 1, 2)

    val r = sp12.seqAllSuccessful(True, (f: FeatureExpr, suc: p.Success[Int]) => new p.Success(new ~(suc.result, 0), null))

    assertEquals("SplittedParseResult(def(A),Success((1~0),null),Success((2~0),null))", r.toString)

    //        val r2=sp12.seqAllSuccessful(fa, (f:FeatureExpr,suc:p.Success[Int])=> new p.Success(new ~(suc.result,0),null))
    //
    //        assertEquals("SplittedParseResult(def(A),Success((1~0),null),Success(2,null))", r.toString)
  }

  //    @Test
  //    def testParseResultsSplit4() {
  //        //this is poor API design, giving to much power to the seqAllSuc parameter allowing stupid concatenations
  //        val sp12 = new p.SplittedParseResult(fa, s1, s2) // SPLIT(A, 1, 2)
  //        val r2=sp12.seqAllSuccessful(True, (f:FeatureExpr,suc:p.Success[Int])=> new p.SplittedParseResult(fb, s3, s4))
  //
  //        assertEquals("SplittedParseResult(def(A),Success((1~0),null),Success((2~0),null))", r2.toString)
  //    }

  @Test
  def testParseResultsSeq2() {
    val r1 = s1.seq2(True, (next: Any, f: FeatureExpr) => new p.SplittedParseResult(fb, s3, s4))

    assertEquals("SplittedParseResult(def(B),Success((1~3),null),Success((1~4),null))", r1.toString)

    val sp12 = new p.SplittedParseResult(fa, s1, s2) // SPLIT(A, 1, 2)
    val r3 = sp12.seq2(True, (next: Any, f: FeatureExpr) => s3)
    assertEquals("SplittedParseResult(def(A),Success((1~3),null),Success((2~3),null))", r3.toString)


    val r2 = sp12.seq2(True, (next: Any, f: FeatureExpr) => new p.SplittedParseResult(fb, s3, s4))
    assertEquals("SplittedParseResult(def(A),SplittedParseResult(def(B),Success((1~3),null),Success((1~4),null)),SplittedParseResult(def(B),Success((2~3),null),Success((2~4),null)))", r2.toString)
  }

  @Test
  def testParseResultsSeq2Fails() {
    var r: p.MultiParseResult[Any] = f.seq2(True, (next: Any, f: FeatureExpr) => new p.SplittedParseResult(fb, s3, s4))

    assertEquals("Failure(e,null,List())", r.toString)

    val sp = new p.SplittedParseResult(fa, s1, f)
    r = sp.seq2(True, (next: Any, f: FeatureExpr) => s3)
    assertEquals("SplittedParseResult(def(A),Success((1~3),null),Failure(e,null,List()))", r.toString)

    r = sp.seq2(True, (next: Any, f: FeatureExpr) => new p.SplittedParseResult(fb, s3, s4))
    assertEquals("SplittedParseResult(def(A),SplittedParseResult(def(B),Success((1~3),null),Success((1~4),null)),Failure(e,null,List()))", r.toString)

    r = sp.seq2(True, (next: Any, _: FeatureExpr) => f)
    assertEquals("SplittedParseResult(def(A),Failure(e,null,List()),Failure(e,null,List()))", r.toString)

    r = r.joinTree(True)
    assert(r.toString.startsWith("Failure(joined error,"))

  }


}
