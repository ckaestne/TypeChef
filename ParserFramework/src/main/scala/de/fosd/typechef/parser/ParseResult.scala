package de.fosd.typechef.parser
import de.fosd.typechef.featureexpr.FeatureExpr

/**
 * 
 * Note:
 * TypeContext is an object that can be passed during parsing
 * it is not the feature expression of the current parser/result (which is only encoded
 * as feature in SplitParseResult)
 * 
 * It is currently used for C to pass an object that contains all defined Types
 */
sealed abstract class MultiParseResult[+T, Token <: AbstractToken, TypeContext] {
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T, Token, TypeContext]) => MultiParseResult[U, Token, TypeContext]): MultiParseResult[U, Token, TypeContext]
    def replaceAllFailure[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U, Token, TypeContext]): MultiParseResult[U, Token, TypeContext]
    def map[U](f: T => U): MultiParseResult[U, Token, TypeContext]
    /** 
     * joins multiple cases, ensures only one result remains. 
     * if any result is unsuccessful, everything is unsuccessful. 
     * this should only be used at the last step when presenting a single result to the user 
     **/
    def forceJoin[U >: T](f: (FeatureExpr, U, U) => U): ParseResult[U, Token, TypeContext]
    /** 
     * joins as far as possible. joins all successful ones but maintains partially successful results.
     * keeping partially unsucessful results is necessary to consider multiple branches for an alternative on ASTs
     **/
    def join[U >: T](f: (FeatureExpr, U, U) => U): MultiParseResult[U, Token, TypeContext]
    def allFailed: Boolean
    def toList: List[ParseResult[T, Token, TypeContext]]
    def toErrorList: List[Error[Token, TypeContext]]
    def changeContext(contextModification: (T, TypeContext) => TypeContext): MultiParseResult[T, Token, TypeContext]
    //replace all failures by errors (non-backtracking!)
    def commit: MultiParseResult[T, Token, TypeContext]
}
/**
 * split into two parse results (all calls are propagated to the individual results)
 * @author kaestner
 */
case class SplittedParseResult[+T, Token <: AbstractToken, TypeContext](feature: FeatureExpr, resultA: MultiParseResult[T, Token, TypeContext], resultB: MultiParseResult[T, Token, TypeContext]) extends MultiParseResult[T, Token, TypeContext] {
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T, Token, TypeContext]) => MultiParseResult[U, Token, TypeContext]): MultiParseResult[U, Token, TypeContext] = {
        SplittedParseResult(feature, resultA.seqAllSuccessful(context.and(feature), f), resultB.seqAllSuccessful(context.and(feature.not), f))
    }
    def replaceAllFailure[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U, Token, TypeContext]): MultiParseResult[U, Token, TypeContext] = {
        SplittedParseResult(feature, resultA.replaceAllFailure(context.and(feature), f), resultB.replaceAllFailure(context.and(feature.not), f))
    }
    def map[U](f: T => U): MultiParseResult[U, Token, TypeContext] =
        SplittedParseResult(feature, resultA.map(f), resultB.map(f))
    def forceJoin[U >: T](f: (FeatureExpr, U, U) => U): ParseResult[U, Token, TypeContext] = {
        (resultA.join(f), resultB.join(f)) match {
            case (Success(rA, inA), Success(rB, inB)) =>
                if (inA == inB || inA == inB.skipHidden(feature.not))
                    Success(f(feature, rA, rB), inA)
                else if (inA.skipHidden(feature) == inB || inA.skipHidden(feature) == inB.skipHidden(feature.not))
                    Success(f(feature, rA, rB), inA.skipHidden(feature))
                else
                    Failure("Incompatible ends for joining two results: " + rA + " (" + inA + ") - " + rB + " (" + inB + ")", feature, inA, List())
            case (nA@NoSuccess(mA, fA, inA, iA), nB@NoSuccess(mB, fB, inB, iB)) => Failure("joined error", fA.or(fB), inA, List(nA, nB))
            case (nos@NoSuccess(_, _, _, _), _) => nos
            case (_, nos@NoSuccess(_, _, _, _)) => nos
            case _ => throw new Exception("unsupported match")
        }
    }
    def join[U >: T](f: (FeatureExpr, U, U) => U): MultiParseResult[U, Token, TypeContext] = {
        (resultA.join(f), resultB.join(f)) match {
            //both successful
            case (Success(rA, inA), Success(rB, inB)) =>
                if (inA == inB || inA == inB.skipHidden(feature.not)) {
                    DebugSplitting("join  at \"" + inA.first.getText + "\" at " + inA.first.getPosition + " from " + feature)
                    Success(f(feature, rA, rB), inA)
                } else if (inA.skipHidden(feature) == inB || inA.skipHidden(feature) == inB.skipHidden(feature.not)) {
                    DebugSplitting("join  at \"" + inB.first.getText + "\" at " + inB.first.getPosition + " from " + feature)
                    Success(f(feature, rA, rB), inA.skipHidden(feature))
                } else
                    this //NoSuccess("Incompatible ends for joining two results: " + rA + " (" + inA + ") - " + rB + " (" + inB + ")", feature, inA,List())
            //both not sucessful
            case (nA@NoSuccess(mA, fA, inA, iA), nB@NoSuccess(mB, fB, inB, iB)) => {
                DebugSplitting("joinf at \"" + inA.first.getText + "\" at " + inA.first.getPosition + " from " + feature)
                Failure("joined error", fA.or(fB), inA, List(nA, nB))
            }
            //partially successful
            case (a, b) => SplittedParseResult(feature, a, b)
        }
    }
    def allFailed = resultA.allFailed && resultB.allFailed
    def toList = resultA.toList ++ resultB.toList
    def toErrorList = resultA.toErrorList ++ resultB.toErrorList
    def changeContext(contextModification: (T, TypeContext) => TypeContext) =
        SplittedParseResult(feature, resultA.changeContext(contextModification), resultB.changeContext(contextModification))
    def commit: MultiParseResult[T, Token, TypeContext] =
        SplittedParseResult(feature, resultA.commit, resultB.commit)
}
/**
 * stores a list of results of which individual entries can belong to a specific feature
 * @author kaestner
 *
 */
//case class OptListParseResult[+T](entries:List[MultiParseResult[T,Token,TypeContext]]) extends MultiParseResult[T,Token,TypeContext]

/**
 * contains the recognized parser result (including recognized alternatives?)
 * @author kaestner
 */
sealed abstract class ParseResult[+T, Token <: AbstractToken, TypeContext](nextInput: TokenReader[Token, TypeContext]) extends MultiParseResult[T, Token, TypeContext] {
    def map[U](f: T => U): ParseResult[U, Token, TypeContext]
    def next = nextInput
    def isSuccess: Boolean
    def forceJoin[U >: T](f: (FeatureExpr, U, U) => U): ParseResult[U, Token, TypeContext] = this
    def join[U >: T](f: (FeatureExpr, U, U) => U): MultiParseResult[U, Token, TypeContext] = this
    def toList = List(this)
}
abstract class NoSuccess[Token <: AbstractToken, TypeContext](val msg: String, val context: FeatureExpr, val nextInput: TokenReader[Token, TypeContext], val innerErrors: List[NoSuccess[Token, TypeContext]]) extends ParseResult[Nothing, Token, TypeContext](nextInput) {
    def map[U](f: Nothing => U) = this
    def isSuccess: Boolean = false
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[Nothing, Token, TypeContext]) => MultiParseResult[U, Token, TypeContext]): MultiParseResult[U, Token, TypeContext] = this
    def allFailed = true
    def changeContext(contextModification: (Nothing, TypeContext) => TypeContext) = this
}
/** An extractor so NoSuccess(msg, next) can be used in matches.
 */
object NoSuccess {
    def unapply[T <: AbstractToken, C](x: NoSuccess[T, C]) = x match {
        case Failure(msg, context, next, inner) => Some(msg, context, next, inner)
        case Error(msg, context, next, inner) => Some(msg, context, next, inner)
        case _ => None
    }
}
/**
 * see original parser comb. framework. noncritical error, caught in alternatives
 */
case class Failure[Token <: AbstractToken, TypeContext](override val msg: String, override val context: FeatureExpr, override val nextInput: TokenReader[Token, TypeContext], override val innerErrors: List[NoSuccess[Token, TypeContext]]) extends NoSuccess(msg, context, nextInput, innerErrors) {
    def replaceAllFailure[U >: Nothing](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U, Token, TypeContext]) = f(context)
    def commit = Error(msg, context, nextInput, innerErrors)
    def toErrorList = List()
}
/**
 * see original parser comb. framework. non-backtracking error
 */
case class Error[Token <: AbstractToken, TypeContext](override val msg: String, override val context: FeatureExpr, override val nextInput: TokenReader[Token, TypeContext], override val innerErrors: List[NoSuccess[Token, TypeContext]]) extends NoSuccess(msg, context, nextInput, innerErrors) {
    def replaceAllFailure[U >: Nothing](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U, Token, TypeContext]) = this
    def commit = this
    def toErrorList = List(this)
}

case class Success[+T, Token <: AbstractToken, TypeContext](val result: T, nextInput: TokenReader[Token, TypeContext]) extends ParseResult[T, Token, TypeContext](nextInput) {
    def map[U](f: T => U): ParseResult[U, Token, TypeContext] = Success(f(result), next)
    def isSuccess: Boolean = true
    def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T, Token, TypeContext]) => MultiParseResult[U, Token, TypeContext]): MultiParseResult[U, Token, TypeContext] = f(context, this)
    def seq[U](context: FeatureExpr, thatResult: MultiParseResult[U, Token, TypeContext]): MultiParseResult[~[T, U], Token, TypeContext] =
        thatResult.seqAllSuccessful[~[T, U]](context, (fs: FeatureExpr, x: Success[U, Token, TypeContext]) => Success(new ~(result, x.result), x.next))
    def replaceAllFailure[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U, Token, TypeContext]): MultiParseResult[U, Token, TypeContext] = this
    def allFailed = false
    def toErrorList = List()
    def changeContext(contextModification: (T, TypeContext) => TypeContext): MultiParseResult[T, Token, TypeContext] = Success(result, nextInput.setContext(contextModification(result, nextInput.context)))
    def commit: MultiParseResult[T, Token, TypeContext] = this
}

