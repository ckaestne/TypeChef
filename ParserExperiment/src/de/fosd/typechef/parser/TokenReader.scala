package de.fosd.typechef.parser
import scala.util.parsing.input._
import de.fosd.typechef.featureexpr.FeatureExpr

/**
 * reader of elements that each have a feature expression (that can be accessed with the getFeature function)
 * 
 * @author kaestner
 *
 */
class TokenReader[T<:AbstractToken](val tokens: List[T], val offst: Int) extends Reader[T] {

    override def offset: Int = offst

    override def first = tokens.head

    override def rest: TokenReader[T] = new TokenReader(tokens.tail, offst + 1)

    override def pos: Position = NoPosition

    /** true iff there are no more elements in this reader 
     */
    def atEnd: Boolean = tokens.isEmpty

    override def toString: String = "TokenReader(" + tokens + ")"

    override def hashCode = tokens.hashCode

    override def equals(that: Any) = that match {
        case other: TokenReader[T] => this.tokens == other.tokens
        case _ => false
    }

    def skipHidden(context: FeatureExpr): TokenReader[T] = {
        var result = this
        while (!result.atEnd && context.and(result.first.getFeature).isDead)
            result = result.rest
        result
    }

}