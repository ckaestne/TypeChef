package de.fosd.typechef.parser

sealed abstract class AST {
	def alt(feature:Int, that:AST):Alt = Alt(feature,this,that) 
}
case class Plus(left: AST, right: AST) extends AST
case class Minus(left: AST, right: AST) extends AST
case class Mul(left: AST, right: AST) extends AST
case class Lit(value: Int) extends AST
case class Alt(feature: Int, thenBranch: AST, elseBranch: AST) extends AST