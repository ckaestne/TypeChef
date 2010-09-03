package de.fosd.typechef.parser

class Token (text:String, feature:Int) {
	def t() = text
}
object EofToken extends Token("EOF",0)