package de.fosd.typechef.parser

sealed abstract class AST (feature:Int)
case class Plus(left:AST, right:AST, feature:Int=0) extends AST(feature)
case class Mul(left:AST, right:AST,feature:Int=0) extends AST(feature)
case class Lit(value:Int,feature:Int=0) extends AST(feature)
case class IF(thenBranch:AST, elseBranch:AST,feature:Int=0) extends AST(feature)