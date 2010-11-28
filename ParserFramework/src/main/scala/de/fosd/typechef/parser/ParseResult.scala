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
     * joins as far as possible. joins all successful ones but maintains partially successful results.
     * keeping partially unsucessful results is necessary to consider multiple branches for an alternative on ASTs
     **/
    def join[U >: T](parserContext: FeatureExpr, f: (FeatureExpr, U, U) => U): MultiParseResult[U, Token, TypeContext]
    def forceJoin[U >: T](parserContext: FeatureExpr, f: (FeatureExpr, U, U) => U): ParseResult[U, Token, TypeContext] =
        join(parserContext, f) match {
            case s@Success(_, _) => s
            case s@NoSuccess(_, _, _, _) => s
            case _ => throw new Exception("Unsuccessful join")
        }

    def allFailed: Boolean
    /**
     * toList recursively flattens the tree structure and creates resulting feature expressions
     */
    def toList(baseFeatureExpr: FeatureExpr): List[(FeatureExpr, ParseResult[T, Token, TypeContext])]
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
    def join[U >: T](parserContext: FeatureExpr, f: (FeatureExpr, U, U) => U): MultiParseResult[U, Token, TypeContext] = {
        (resultA.join(parserContext and feature, f), resultB.join(parserContext and (feature.not), f)) match {
            //both successful
            case (sA@Success(rA, inA), sB@Success(rB, inB)) => {
                val nextA = inA.skipHidden(parserContext and feature)
                val nextB = inB.skipHidden(parserContext and (feature.not))

                if (nextA == nextB) {
                    DebugSplitting("join  at \"" + inA.first.getText + "\" at " + inA.first.getPosition + " from " + feature)
                    Success(f(feature, rA, rB),
                        if (inA.offst < inB.offst) inB else inA) //do not skip ahead, important for repOpt
                } else
                    SplittedParseResult(feature, sA, sB)
            }
            /**
             * foldtree (heuristic for earlier joins, when treelike joins not possible)
             * used for input such as <_{A&B} <_{A&!B} >_{A} x_{!A}
             * which creates a parse tree SPLIT(A&B, <>, SPLIT(A&!B, <>, x))
             * of which the former two can be merged. (occurs in expanded Linux headers...)
             */
            case (sA@Success(rA, inA), sB@SplittedParseResult(innerFeature, Success(rB, inB), otherParseResult@_)) => {
                val nextA = inA.skipHidden(parserContext and feature)
                val nextB = inB.skipHidden(parserContext and (feature.not) and innerFeature)

                if (nextA == nextB) {
                    DebugSplitting("joinT at \"" + inA.first.getText + "\" at " + inA.first.getPosition + " from " + feature)
                    SplittedParseResult(
                        parserContext and (feature or innerFeature),
                        Success(f(feature or innerFeature, rA, rB),
                            if (inA.offst < inB.offst) inB else inA),
                        otherParseResult)
                } else
                    SplittedParseResult(feature, sA, sB)
            }
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
    def toList(context: FeatureExpr) = resultA.toList(context and feature) ++ resultB.toList(context and (feature.not))
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
    def join[U >: T](parserContext: FeatureExpr, f: (FeatureExpr, U, U) => U): MultiParseResult[U, Token, TypeContext] = this
    def toList(context: FeatureExpr) = List((context, this))
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

