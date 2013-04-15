package de.fosd.typechef.parser

import de.fosd.typechef.featureexpr.FeatureExpr

trait AbstractToken {
  // used to determine splitting and joining
  def getFeature: FeatureExpr

  // used by ParserFramework only to produce error messages
  def getText: String

  // used to propagate position information to AST elements
  def getPosition: Position

  // profiling
  def countSuccess(context: FeatureExpr) = {}

  def countFailure = {}

  def countSplit = {}
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

  def profile_consumed_replicated(): Int = if (profile_consumed > 0) profile_consumed - profile_consumed_backtracking - 1 else 0

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
  def totalConsumed[T <: ProfilingToken, U](in: TokenReader[T, U]) = in.tokens.foldLeft(0)((sum, token) => sum + token.profile_consumed)

  def totalBacktracked[T <: ProfilingToken, U](in: TokenReader[T, U]) = in.tokens.foldLeft(0)((sum, token) => sum + token.profile_consumed_backtracking)

  def totalRepeated[T <: ProfilingToken, U](in: TokenReader[T, U]) = in.tokens.foldLeft(0)((sum, token) => sum + token.profile_consumed_replicated())

  def repeatedTokens[T <: ProfilingToken, U](in: TokenReader[T, U]) = in.tokens.filter(_.profile_consumed_replicated() > 0).map(_.getPosition.getLine).toSet

  /**
   * counts how many tokens have 0 repeatition, 1 repetition etc.
   */
  def repeatedDistribution[T <: ProfilingToken, U](in: TokenReader[T, U]) = {
    var map: Map[Int /*#Repeatition*/ , Int /*TokenCount*/ ] = Map()
    for (t <- in.tokens)
      map = map.updated(t.profile_consumed_replicated, map.getOrElse(t.profile_consumed_replicated, 0) + 1)
    map
  }
}