package de.fosd.typechef.parser

class Token (text:String, feature:Int) {
	def t() = text
	def f = feature
	
	override def toString = "\""+text+"\""
}
object EofToken extends Token("EOF",0)