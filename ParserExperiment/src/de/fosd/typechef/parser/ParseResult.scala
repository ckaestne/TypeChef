package de.fosd.typechef.parser
import scala.util.parsing.input.Reader
import de.fosd.typechef.featureexpr.FeatureExpr

sealed abstract class MultiParseResult[+T] {
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T]) => MultiParseResult[U]): MultiParseResult[U]
    def replaceAllUnsuccessful[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U]): MultiParseResult[U]
    def map[U](f: T => U): MultiParseResult[U]
    /** 
     * joins multiple cases, ensures only one result remains. 
     * if any result is unsuccessful, everything is unsuccessful. 
     * this should only be used at the last step when presenting a single result to the user 
     **/
    def forceJoin[U >: T](f: (FeatureExpr, U, U) => U): ParseResult[U]
    /** 
     * joins as far as possible. joins all successful ones but maintains partially successful results.
     * keeping partially unsucessful results is necessary to consider multiple branches for an alternative on ASTs
     **/
    def join[U >: T](f: (FeatureExpr, U, U) => U): MultiParseResult[U]
    def allFailed: Boolean
    def toList:List[ParseResult[T]]
    def toErrorList:List[NoSuccess]
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
    def forceJoin[U >: T](f: (FeatureExpr, U, U) => U): ParseResult[U] = {
        (resultA.join(f), resultB.join(f)) match {
            case (Success(rA, inA), Success(rB, inB)) =>
                if (inA == inB || inA == inB.skipHidden(feature.not))
                    Success(f(feature, rA, rB), inA)
                else if (inA.skipHidden(feature) == inB || inA.skipHidden(feature) == inB.skipHidden(feature.not))
                    Success(f(feature, rA, rB), inA.skipHidden(feature))
                else
                    NoSuccess("Incompatible ends for joining two results: " + rA + " (" + inA + ") - " + rB + " (" + inB + ")", feature, inA,List())
            case (nA@NoSuccess(mA, fA, inA, iA), nB@NoSuccess(mB, fB, inB, iB)) => NoSuccess("joined error",fA.or(fB),inA,List(nA,nB))
            case (nos@NoSuccess(_, _, _, _), _) => nos
            case (_, nos@NoSuccess(_, _, _,_)) => nos
        }
    }
    def join[U >: T](f: (FeatureExpr, U, U) => U): MultiParseResult[U] = {
        (resultA.join(f), resultB.join(f)) match {
            //both successful
            case (Success(rA, inA), Success(rB, inB)) =>
                if (inA == inB || inA == inB.skipHidden(feature.not))
                    Success(f(feature, rA, rB), inA)
                else if (inA.skipHidden(feature) == inB || inA.skipHidden(feature) == inB.skipHidden(feature.not))
                    Success(f(feature, rA, rB), inA.skipHidden(feature))
                else
                    this//NoSuccess("Incompatible ends for joining two results: " + rA + " (" + inA + ") - " + rB + " (" + inB + ")", feature, inA,List())
            //both not sucessful
            case (nA@NoSuccess(mA, fA, inA, iA), nB@NoSuccess(mB, fB, inB, iB)) => NoSuccess("joined error",fA.or(fB),inA,List(nA,nB))
            //partially successful
            case (a, b) => SplittedParseResult(feature, a, b)
        }
    }
    def allFailed = resultA.allFailed && resultB.allFailed
    def toList = resultA.toList ++ resultB.toList
    def toErrorList = resultA.toErrorList ++ resultB.toErrorList
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
sealed abstract class ParseResult[+T](nextInput: TokenReader) extends MultiParseResult[T] {
    def map[U](f: T => U): ParseResult[U]
    def next = nextInput
    def isSuccess: Boolean
    def forceJoin[U >: T](f: (FeatureExpr, U, U) => U): ParseResult[U] = this
    def join[U >: T](f: (FeatureExpr, U, U) => U): MultiParseResult[U] = this
    def toList = List(this)
}
case class NoSuccess(msg: String, val context: FeatureExpr, nextInput: TokenReader, val innerErrors:List[NoSuccess]) extends ParseResult[Nothing](nextInput) {
    def map[U](f: Nothing => U) = this
    def isSuccess: Boolean = false
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[Nothing]) => MultiParseResult[U]): MultiParseResult[U] = this
    def replaceAllUnsuccessful[U >: Nothing](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U]): MultiParseResult[U] = f(context)
    def allFailed = true
    def toErrorList = List(this)
}
case class Success[+T](val result: T, nextInput: TokenReader) extends ParseResult[T](nextInput) {
    def map[U](f: T => U): ParseResult[U] = Success(f(result), next)
    def isSuccess: Boolean = true
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T]) => MultiParseResult[U]): MultiParseResult[U] = f(context, this)
    def seq[U](context: FeatureExpr, thatResult: MultiParseResult[U]): MultiParseResult[~[T, U]] =
        thatResult.seqAllSuccessful[~[T, U]](context, (fs: FeatureExpr, x: Success[U]) => Success(new ~(result, x.result), x.next))
    def replaceAllUnsuccessful[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U]): MultiParseResult[U] = this
    def allFailed = false
    def toErrorList = List()
}

