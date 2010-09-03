package de.fosd.typechef.parser
import scala.util.parsing.combinator._

class ExprParser  extends Parsers {
	type Elem = Token
	
	def parse(tokens:List[Token]) = expr(new TokenReader(tokens,0))
	
	def expr: Parser[AST] = term~opt(t("+")~>expr) ^^ {case a~Some(b) => Plus(a,b); case a~None => a } 
	def term: Parser[AST] = factor~opt(t("*")~>expr) ^^ {case a~Some(b) => Mul(a,b); case a~None => a }
//	def ifexpr: Parser[AST] = t("IFDEF")~>expr~t("ELSE")~expr<~t("ENDIF") ^^ {case a~"ELSE"~b => IF(a,b) } | factor
	def factor: Parser[AST] = digits ^^ {case a=>Lit(a.t.toInt)} | t("(")~>expr<~t(")")

	def t(text:String) = new Parser[AST] {
		def apply(in: Input): ParseResult[AST] = {
			Error("end",in)
		}
			
	}
	//elem(text,(x:Token) => x.t.eq(text))
	def digits() = elem("digits",(x:Token) => x.t=="1"|x.t=="2"|x.t=="3"|x.t=="4"|x.t=="5")
	
	
}

