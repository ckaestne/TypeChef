package de.fosd.typechef.parser

import de.fosd.typechef.featureexpr.FeatureExpr

trait AbstractToken {
    def getFeature: FeatureExpr
    def getText: String
    def getPosition: Position

    // profiling
    def countSuccess(context: FeatureExpr) = {}
    def countFailure = {}
    def countSplit = {}
}

trait Position {
    def getFile: String
    def getLine: Int
    def getColumn: Int
    def <(that: Position) = (this.getLine < that.getLine) || ((this.getLine == that.getLine) && (this.getColumn < that.getColumn))
    override def toString = getFile + ":" + getLine + ":" + getColumn
}

/**
 * counts the number of times this token was consumed during parsing
 * also remembers the context each to distinguish between backtracking
 * and replicated parsing in different parse contexts
 */
trait ProfilingToken extends AbstractToken {

    var profile_consumed: Int = 0
    var profile_consumed_backtracking: Int = 0
    var profile_consumedContexts: Set[FeatureExpr] = Set()
    def profile_consumed_replicated(): Int = if (profile_consumed>0) profile_consumed - profile_consumed_backtracking - 1 else 0
    override def countSuccess(context: FeatureExpr) = {
        super.countSuccess(context)

//        println("consuming "+this.getText+" - "+context)

        profile_consumed += 1
        if (profile_consumedContexts.contains(context))
            profile_consumed_backtracking += 1
        else {
            profile_consumedContexts += context
        }
    }

}

object ProfilingTokenHelper {
    def totalConsumed[T<:ProfilingToken,U](in: TokenReader[T,U]) = in.tokens.foldLeft(0)((sum, token) => sum + token.profile_consumed)
    def totalBacktracked[T<:ProfilingToken,U](in: TokenReader[T,U]) = in.tokens.foldLeft(0)((sum, token) => sum + token.profile_consumed_backtracking)
    def totalRepeated[T<:ProfilingToken,U](in: TokenReader[T,U]) = in.tokens.foldLeft(0)((sum, token) => sum + token.profile_consumed_replicated())
}