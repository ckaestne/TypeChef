package de.fosd.typechef.parser

sealed abstract class AST
case class Plus(left:AST, right:AST) extends AST
case class Mul(left:AST, right:AST) extends AST
case class Lit(value:Int) extends AST
case class IF(thenBranch:AST, elseBranch:AST) extends AST