package de.fosd.typechef.parser
import de.fosd.typechef.featureexpr.FeatureExpr

sealed abstract class MultiParseResult[+T, Token <: AbstractToken] {
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T, Token]) => MultiParseResult[U, Token]): MultiParseResult[U, Token]
    def replaceAllUnsuccessful[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U, Token]): MultiParseResult[U, Token]
    def map[U](f: T => U): MultiParseResult[U, Token]
    /** 
     * joins multiple cases, ensures only one result remains. 
     * if any result is unsuccessful, everything is unsuccessful. 
     * this should only be used at the last step when presenting a single result to the user 
     **/
    def forceJoin[U >: T](f: (FeatureExpr, U, U) => U): ParseResult[U, Token]
    /** 
     * joins as far as possible. joins all successful ones but maintains partially successful results.
     * keeping partially unsucessful results is necessary to consider multiple branches for an alternative on ASTs
     **/
    def join[U >: T](f: (FeatureExpr, U, U) => U): MultiParseResult[U, Token]
    def allFailed: Boolean
    def toList: List[ParseResult[T, Token]]
    def toErrorList: List[NoSuccess[Token]]
}
/**
 * split into two parse results (all calls are propagated to the individual results)
 * @author kaestner
 */
case class SplittedParseResult[+T, Token <: AbstractToken](feature: FeatureExpr, resultA: MultiParseResult[T, Token], resultB: MultiParseResult[T, Token]) extends MultiParseResult[T, Token] {
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T, Token]) => MultiParseResult[U, Token]): MultiParseResult[U, Token] = {
        SplittedParseResult(feature, resultA.seqAllSuccessful(context.and(feature), f), resultB.seqAllSuccessful(context.and(feature.not), f))
    }
    def replaceAllUnsuccessful[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U, Token]): MultiParseResult[U, Token] = {
        SplittedParseResult(feature, resultA.replaceAllUnsuccessful(context.and(feature), f), resultB.replaceAllUnsuccessful(context.and(feature.not), f))
    }
    def map[U](f: T => U): MultiParseResult[U, Token] =
        SplittedParseResult(feature, resultA.map(f), resultB.map(f))
    def forceJoin[U >: T](f: (FeatureExpr, U, U) => U): ParseResult[U, Token] = {
        (resultA.join(f), resultB.join(f)) match {
            case (Success(rA, inA), Success(rB, inB)) =>
                if (inA == inB || inA == inB.skipHidden(feature.not))
                    Success(f(feature, rA, rB), inA)
                else if (inA.skipHidden(feature) == inB || inA.skipHidden(feature) == inB.skipHidden(feature.not))
                    Success(f(feature, rA, rB), inA.skipHidden(feature))
                else
                    NoSuccess("Incompatible ends for joining two results: " + rA + " (" + inA + ") - " + rB + " (" + inB + ")", feature, inA, List())
            case (nA@NoSuccess(mA, fA, inA, iA), nB@NoSuccess(mB, fB, inB, iB)) => NoSuccess("joined error", fA.or(fB), inA, List(nA, nB))
            case (nos@NoSuccess(_, _, _, _), _) => nos
            case (_, nos@NoSuccess(_, _, _, _)) => nos
        }
    }
    def join[U >: T](f: (FeatureExpr, U, U) => U): MultiParseResult[U, Token] = {
        (resultA.join(f), resultB.join(f)) match {
            //both successful
            case (Success(rA, inA), Success(rB, inB)) =>
                if (inA == inB || inA == inB.skipHidden(feature.not))
                    Success(f(feature, rA, rB), inA)
                else if (inA.skipHidden(feature) == inB || inA.skipHidden(feature) == inB.skipHidden(feature.not))
                    Success(f(feature, rA, rB), inA.skipHidden(feature))
                else
                    this //NoSuccess("Incompatible ends for joining two results: " + rA + " (" + inA + ") - " + rB + " (" + inB + ")", feature, inA,List())
            //both not sucessful
            case (nA@NoSuccess(mA, fA, inA, iA), nB@NoSuccess(mB, fB, inB, iB)) => NoSuccess("joined error", fA.or(fB), inA, List(nA, nB))
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
//case class OptListParseResult[+T](entries:List[MultiParseResult[T,Token]]) extends MultiParseResult[T,Token]

/**
 * contains the recognized parser result (including recognized alternatives?)
 * @author kaestner
 */
sealed abstract class ParseResult[+T, Token <: AbstractToken](nextInput: TokenReader[Token]) extends MultiParseResult[T, Token] {
    def map[U](f: T => U): ParseResult[U, Token]
    def next = nextInput
    def isSuccess: Boolean
    def forceJoin[U >: T](f: (FeatureExpr, U, U) => U): ParseResult[U, Token] = this
    def join[U >: T](f: (FeatureExpr, U, U) => U): MultiParseResult[U, Token] = this
    def toList = List(this)
}
case class NoSuccess[Token <: AbstractToken](msg: String, val context: FeatureExpr, nextInput: TokenReader[Token], val innerErrors: List[NoSuccess[Token]]) extends ParseResult[Nothing, Token](nextInput) {
    def map[U](f: Nothing => U) = this
    def isSuccess: Boolean = false
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[Nothing, Token]) => MultiParseResult[U, Token]): MultiParseResult[U, Token] = this
    def replaceAllUnsuccessful[U >: Nothing](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U, Token]): MultiParseResult[U, Token] = f(context)
    def allFailed = true
    def toErrorList = List(this)
}
case class Success[+T, Token <: AbstractToken](val result: T, nextInput: TokenReader[Token]) extends ParseResult[T, Token](nextInput) {
    def map[U](f: T => U): ParseResult[U, Token] = Success(f(result), next)
    def isSuccess: Boolean = true
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T, Token]) => MultiParseResult[U, Token]): MultiParseResult[U, Token] = f(context, this)
    def seq[U](context: FeatureExpr, thatResult: MultiParseResult[U, Token]): MultiParseResult[~[T, U], Token] =
        thatResult.seqAllSuccessful[~[T, U]](context, (fs: FeatureExpr, x: Success[U, Token]) => Success(new ~(result, x.result), x.next))
    def replaceAllUnsuccessful[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U, Token]): MultiParseResult[U, Token] = this
    def allFailed = false
    def toErrorList = List()
}

