package de.fosd.typechef.parser
import de.fosd.typechef.featureexpr.FeatureExpr

sealed abstract class MultiParseResult[+T, Token <: AbstractToken, Context] {
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T, Token, Context]) => MultiParseResult[U, Token, Context]): MultiParseResult[U, Token, Context]
    def replaceAllUnsuccessful[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U, Token, Context]): MultiParseResult[U, Token, Context]
    def map[U](f: T => U): MultiParseResult[U, Token, Context]
    /** 
     * joins multiple cases, ensures only one result remains. 
     * if any result is unsuccessful, everything is unsuccessful. 
     * this should only be used at the last step when presenting a single result to the user 
     **/
    def forceJoin[U >: T](f: (FeatureExpr, U, U) => U): ParseResult[U, Token, Context]
    /** 
     * joins as far as possible. joins all successful ones but maintains partially successful results.
     * keeping partially unsucessful results is necessary to consider multiple branches for an alternative on ASTs
     **/
    def join[U >: T](f: (FeatureExpr, U, U) => U): MultiParseResult[U, Token, Context]
    def allFailed: Boolean
    def toList: List[ParseResult[T, Token, Context]]
    def toErrorList: List[NoSuccess[Token, Context]]
    def changeContext(contextModification: (T, Context) => Context): MultiParseResult[T, Token, Context]
}
/**
 * split into two parse results (all calls are propagated to the individual results)
 * @author kaestner
 */
case class SplittedParseResult[+T, Token <: AbstractToken, Context](feature: FeatureExpr, resultA: MultiParseResult[T, Token, Context], resultB: MultiParseResult[T, Token, Context]) extends MultiParseResult[T, Token, Context] {
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T, Token, Context]) => MultiParseResult[U, Token, Context]): MultiParseResult[U, Token, Context] = {
        SplittedParseResult(feature, resultA.seqAllSuccessful(context.and(feature), f), resultB.seqAllSuccessful(context.and(feature.not), f))
    }
    def replaceAllUnsuccessful[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U, Token, Context]): MultiParseResult[U, Token, Context] = {
        SplittedParseResult(feature, resultA.replaceAllUnsuccessful(context.and(feature), f), resultB.replaceAllUnsuccessful(context.and(feature.not), f))
    }
    def map[U](f: T => U): MultiParseResult[U, Token, Context] =
        SplittedParseResult(feature, resultA.map(f), resultB.map(f))
    def forceJoin[U >: T](f: (FeatureExpr, U, U) => U): ParseResult[U, Token, Context] = {
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
    def join[U >: T](f: (FeatureExpr, U, U) => U): MultiParseResult[U, Token, Context] = {
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
    def changeContext(contextModification: (T, Context) => Context) =
    	SplittedParseResult(feature,resultA.changeContext(contextModification),resultB.changeContext(contextModification))
}
/**
 * stores a list of results of which individual entries can belong to a specific feature
 * @author kaestner
 *
 */
//case class OptListParseResult[+T](entries:List[MultiParseResult[T,Token,Context]]) extends MultiParseResult[T,Token,Context]

/**
 * contains the recognized parser result (including recognized alternatives?)
 * @author kaestner
 */
sealed abstract class ParseResult[+T, Token <: AbstractToken, Context](nextInput: TokenReader[Token, Context]) extends MultiParseResult[T, Token, Context] {
    def map[U](f: T => U): ParseResult[U, Token, Context]
    def next = nextInput
    def isSuccess: Boolean
    def forceJoin[U >: T](f: (FeatureExpr, U, U) => U): ParseResult[U, Token, Context] = this
    def join[U >: T](f: (FeatureExpr, U, U) => U): MultiParseResult[U, Token, Context] = this
    def toList = List(this)
}
case class NoSuccess[Token <: AbstractToken, Context](msg: String, val context: FeatureExpr, nextInput: TokenReader[Token, Context], val innerErrors: List[NoSuccess[Token, Context]]) extends ParseResult[Nothing, Token, Context](nextInput) {
    def map[U](f: Nothing => U) = this
    def isSuccess: Boolean = false
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[Nothing, Token, Context]) => MultiParseResult[U, Token, Context]): MultiParseResult[U, Token, Context] = this
    def replaceAllUnsuccessful[U >: Nothing](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U, Token, Context]): MultiParseResult[U, Token, Context] = f(context)
    def allFailed = true
    def toErrorList = List(this)
    def changeContext(contextModification: (Nothing, Context) => Context) = this
}
case class Success[+T, Token <: AbstractToken, Context](val result: T, nextInput: TokenReader[Token, Context]) extends ParseResult[T, Token, Context](nextInput) {
    def map[U](f: T => U): ParseResult[U, Token, Context] = Success(f(result), next)
    def isSuccess: Boolean = true
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T, Token, Context]) => MultiParseResult[U, Token, Context]): MultiParseResult[U, Token, Context] = f(context, this)
    def seq[U](context: FeatureExpr, thatResult: MultiParseResult[U, Token, Context]): MultiParseResult[~[T, U], Token, Context] =
        thatResult.seqAllSuccessful[~[T, U]](context, (fs: FeatureExpr, x: Success[U, Token, Context]) => Success(new ~(result, x.result), x.next))
    def replaceAllUnsuccessful[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U, Token, Context]): MultiParseResult[U, Token, Context] = this
    def allFailed = false
    def toErrorList = List()
    def changeContext(contextModification: (T, Context) => Context): MultiParseResult[T, Token, Context] = Success(result, nextInput.setContext(contextModification(result, nextInput.context)))
}

