package de.fosd.typechef.parser
import scala.util.parsing.input._
import de.fosd.typechef.featureexpr.FeatureExpr

/**
 * reader of elements that each have a feature expression (that can be accessed with the getFeature function)
 * 
 * @author kaestner
 *
 */
class TokenReader[T<:AbstractToken,U](val tokens: List[T], val offst: Int, val context:U=null) extends Reader[T] {

    override def offset: Int = offst

    override def first = tokens.head

    override def rest: TokenReader[T,U] = new TokenReader(tokens.tail, offst + 1,context)

    override def pos: Position = NoPosition

    /** true iff there are no more elements in this reader 
     */
    def atEnd: Boolean = tokens.isEmpty

    override def toString: String = "TokenReader(" + tokens + ")"

    override def hashCode = tokens.hashCode

    override def equals(that: Any) = that match {
        case other: TokenReader[_,_] => this.tokens == other.tokens && this.context==other.context
        case _ => false
    }

    def skipHidden(context: FeatureExpr): TokenReader[T,U] = {
        var result = this
        while (!result.atEnd && FeatureSolverCache.mutuallyExclusive(context,result.first.getFeature))
            result = result.rest
        result
    }
    
    def setContext(newContext: U): TokenReader[T,U] = if (context==newContext) this else new TokenReader(tokens,offst,newContext) 
}