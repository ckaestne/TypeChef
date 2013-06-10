package de.fosd.typechef.parser

import scala.math.min
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import de.fosd.typechef.error.Position

/**
 * reader of elements that each have a feature expression (that can be accessed with the getFeature function)
 *
 * @author kaestner
 *
 */
class TokenReader[+T <: AbstractToken, U](val tokens: List[T], val offst: Int, val context: U = null, eofToken: T) {

    def offset: Int = offst

    def first: T = if (!tokens.isEmpty) tokens.head else eofToken

    def rest: TokenReader[T, U] = new TokenReader(tokens.tail, offst + 1, context, eofToken)

    /** position is for user output only. do not rely on this value.
      * use offset for comparing position in tokenstream
      */
    def pos: Position = first.getPosition

    /** true iff there are no more elements in this reader
      */
    def atEnd: Boolean = tokens.isEmpty

    override def toString: String = {
        val out = new StringBuilder
        out ++= "TokenReader(" + pos.getLine + ","
        var currFeat: FeatureExpr = FeatureExprFactory.True

        for (tok <- tokens.slice(0, min(tokens.size, 50))) {
            var newFeat: FeatureExpr = tok.getFeature
            if (newFeat != currFeat) {
                out ++= "[ PC -> "
                out ++= newFeat.toString
                out ++= "] "
                currFeat = newFeat
            }
            out ++= tok.getText
            out ++= " "
        }
        out ++= ", ...)"
        out.toString
    }

    override def hashCode = tokens.hashCode

    override def equals(that: Any) = that match {
        case other: TokenReader[_, _] => (this.tokens eq other.tokens) && this.context == other.context
        case _ => false
    }

    def skipHidden(context: FeatureExpr, featureSolverCache: FeatureSolverCache): TokenReader[T, U] = {
        var result = this
        while (!result.atEnd && featureSolverCache.mutuallyExclusive(context, result.first.getFeature))
            result = result.rest
        result
    }

    def setContext(newContext: U): TokenReader[T, U] = if (context == newContext) this else new TokenReader(tokens, offst, newContext, eofToken)
}
