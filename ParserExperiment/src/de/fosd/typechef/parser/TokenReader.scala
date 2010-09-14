package de.fosd.typechef.parser
import scala.util.parsing.input._
import de.fosd.typechef.featureexpr.FeatureExpr

class TokenReader(val tokens: List[Token], offst: Int) extends Reader[Token] {

    override def offset: Int = offst

    override def first = if (tokens.isEmpty) EofToken else tokens.head

    override def rest: TokenReader = new TokenReader(tokens.tail, offst + 1)

    override def pos: Position = NoPosition

    /** true iff there are no more elements in this reader 
     */
    def atEnd: Boolean = tokens.size <= 1

    override def toString: String = "TokenReader(" + tokens + ")"

    override def hashCode = tokens.hashCode

    override def equals(that: Any) = that match {
        case other: TokenReader => this.tokens == other.tokens
        case _ => false
    }

    def skipHidden(context: FeatureExpr): TokenReader = {
        var result = this
        while (context.and(result.first.f).isDead)
            result = result.rest
        result
    }

}