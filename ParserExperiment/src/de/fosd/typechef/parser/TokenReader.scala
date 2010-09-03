package de.fosd.typechef.parser
import scala.util.parsing.input._

class TokenReader (tokens:List[Token], offst:Int) extends Reader[Token] {

  override def offset: Int = offst

  override def first = if (tokens.isEmpty) EofToken else tokens.head

  override def rest: Reader[Token] = new TokenReader(tokens.tail,offst+1)
  
  override def pos: Position = NoPosition

  /** true iff there are no more elements in this reader 
   */
  def atEnd: Boolean = tokens.size<=1
	
	
}