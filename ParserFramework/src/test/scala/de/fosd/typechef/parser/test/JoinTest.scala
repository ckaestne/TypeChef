package de.fosd.typechef.parser.test

import org.junit._
import org.junit.Assert._
import de.fosd.typechef.parser._
import de.fosd.typechef.featureexpr.FeatureExpr._
import de.fosd.typechef.featureexpr.FeatureExpr

class JoinTest extends MultiFeatureParser {
    type Elem = MyToken
    type TypeContext = Any

    val tokenStream = new TokenReader[MyToken, TypeContext](List.fill(10)(new MyToken("_", base)), 0, null, EofToken)
    val in1 = tokenStream
    val in2 = in1.rest
    val in3 = in2.rest
    val fa = createDefinedExternal("a")
    val fb = createDefinedExternal("b")
    val joinV = (f, a, b) => V(f, a, b)

    abstract class A

    case class L(v: Int) extends A

    case class V(f: FeatureExpr, left: A, right: A) extends A

    //checks recursively that there is an according V with the required presence condition
    private def assertContainsL(result: MultiParseResult[A], f: FeatureExpr, l: L) {
        var found = false

        def find(feature: FeatureExpr, r: A, expectedFeature: FeatureExpr, expectedL: L): Boolean =
            ((r == expectedL) && (feature equivalentTo expectedFeature)) || (r match {
                case V(f, a, b) => find(feature and f, a, expectedFeature, expectedL) || find(feature and (f.not), b, expectedFeature, expectedL)
                case _ => false
            })

        for ((feature, r) <- result.toList(base))
            r match {
                case Success(r, in) => found |= find(feature, r, f, l)
                case _ =>
            }
        assertTrue("expected " + l + " with " + f + " in " + result, found)
    }
    //checks recursively that there is an according entry with the required presence condition as direct child of a Success
    private def assertContains(result: MultiParseResult[A], f: FeatureExpr, entry: ParseResult[A]) {
        var found = false
        for ((feature, r) <- result.toList(base))
            if ((r == entry) && (feature equivalentTo f))
                found = true
        assertTrue("expected " + entry + " with " + f + " in " + result, found)
    }

    @Test def testJoinBinary {
        val s1 = Success[A](L(1), in1)
        val s2 = Success[A](L(2), in1)
        val s = SplittedParseResult[A](fa, s1, s2)
        val joined = s.join(base, joinV)

        assertContainsL(joined, fa, L(1))
        assertContainsL(joined, fa.not, L(2))
        assertContains(joined, base, Success(V(fa, L(1), L(2)), in1))
    }

    @Test def testNoJoinBinary {
        val s1 = Success[A](L(1), in1)
        val s2 = Success[A](L(2), in2)
        val s = SplittedParseResult[A](fa, s1, s2)
        val joined = s.join(base, joinV)

        assertContainsL(joined, fa, L(1))
        assertContainsL(joined, fa.not, L(2))
        assertTrue(joined.isInstanceOf[SplittedParseResult[_]])
    }

    @Test def testJoinTree {
        val s1 = Success[A](L(1), in1)
        val s2 = Success[A](L(2), in1)
        val s3 = Success[A](L(3), in2)
        val s = SplittedParseResult[A](fa, SplittedParseResult(fb, s1, s3), s2)
        val joined = s.join(base, joinV)

        println(joined)

        assertContainsL(joined, fa and fb, L(1))
        assertContainsL(joined, fa and (fb.not), L(3))
        assertContainsL(joined, fa.not, L(2))
        assertTrue(joined.isInstanceOf[SplittedParseResult[_]])
        assertContains(joined, (fa and fb) or (fa.not), Success(V(fa and fb, L(1), L(2)), in1))
    }

}