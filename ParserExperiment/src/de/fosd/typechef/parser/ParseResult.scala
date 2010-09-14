package de.fosd.typechef.parser
import scala.util.parsing.input.Reader
import de.fosd.typechef.featureexpr.FeatureExpr

sealed abstract class MultiParseResult[+T] {
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T]) => MultiParseResult[U]): MultiParseResult[U]
    def replaceAllUnsuccessful[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U]): MultiParseResult[U]
    def map[U](f: T => U): MultiParseResult[U]
    def join[U >: T](f: (FeatureExpr, U, U) => U): ParseResult[U]
    def allFailed:Boolean
}
/**
 * split into two parse results (all calls are propagated to the individual results)
 * @author kaestner
 */
case class SplittedParseResult[+T](feature: FeatureExpr, resultA: MultiParseResult[T], resultB: MultiParseResult[T]) extends MultiParseResult[T] {
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T]) => MultiParseResult[U]): MultiParseResult[U] = {
        SplittedParseResult(feature, resultA.seqAllSuccessful(context.and(feature), f), resultB.seqAllSuccessful(context.and(feature.not), f))
    }
    def replaceAllUnsuccessful[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U]): MultiParseResult[U] = {
        SplittedParseResult(feature, resultA.replaceAllUnsuccessful(context.and(feature), f), resultB.replaceAllUnsuccessful(context.and(feature.not), f))
    }
    def map[U](f: T => U): MultiParseResult[U] =
        SplittedParseResult(feature, resultA.map(f), resultB.map(f))
    def join[U >: T](f: (FeatureExpr, U, U) => U): ParseResult[U] = {
        (resultA.join(f), resultB.join(f)) match {
            case (Success(rA, inA), Success(rB, inB)) =>
                if (inA == inB)
                    Success(f(feature, rA, rB), inA)
                else
                    NoSuccess("Incompatible ends for joining two results: " + rA + " (" + inA + ") - " + rB + " (" + inB + ")", inA)
            case (nos@NoSuccess(_, _), _) => nos
            case (_, nos@NoSuccess(_, _)) => nos
        }
    }
    def allFailed = resultA.allFailed && resultB.allFailed
}
/**
 * stores a list of results of which individual entries can belong to a specific feature
 * @author kaestner
 *
 */
//case class OptListParseResult[+T](entries:List[MultiParseResult[T]]) extends MultiParseResult[T]

/**
 * contains the recognized parser result (including recognized alternatives?)
 * @author kaestner
 */
sealed abstract class ParseResult[+T](nextInput: Reader[Token]) extends MultiParseResult[T] {
    def map[U](f: T => U): ParseResult[U]
    def next = nextInput
    def isSuccess: Boolean
    def join[U >: T](f: (FeatureExpr, U, U) => U): ParseResult[U] = this
}
case class NoSuccess(msg: String, nextInput: Reader[Token]) extends ParseResult[Nothing](nextInput) {
    def map[U](f: Nothing => U) = this
    def isSuccess: Boolean = false
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[Nothing]) => MultiParseResult[U]): MultiParseResult[U] = this
    def replaceAllUnsuccessful[U >: Nothing](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U]): MultiParseResult[U] = f(context)
    def allFailed = true
}
case class Success[+T](val result: T, nextInput: Reader[Token]) extends ParseResult[T](nextInput) {
    def map[U](f: T => U): ParseResult[U] = Success(f(result), next)
    def isSuccess: Boolean = true
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T]) => MultiParseResult[U]): MultiParseResult[U] = f(context, this)
    def seq[U](context: FeatureExpr, thatResult: MultiParseResult[U]): MultiParseResult[~[T, U]] =
        thatResult.seqAllSuccessful[~[T, U]](context, (fs: FeatureExpr, x: Success[U]) => Success(new ~(result, x.result), x.next))
    def replaceAllUnsuccessful[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U]): MultiParseResult[U] = this
    def allFailed = false
}

