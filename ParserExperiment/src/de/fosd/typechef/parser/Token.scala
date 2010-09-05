package de.fosd.typechef.parser

class Token (text:String, feature:Int) {
	def t() = text
	def f = feature
	
	override def toString = "\""+text+"\""+(if (f!=0) f else "")
}
object EofToken extends Token("EOF",0)