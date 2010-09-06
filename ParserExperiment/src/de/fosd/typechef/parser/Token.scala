package de.fosd.typechef.parser

class Token (val text:String, val feature:Int) {
	def t() = text
	def f = feature
	
	override def toString = "\""+text+"\""+(if (f!=0) f else "")
}
object EofToken extends Token("EOF",0)