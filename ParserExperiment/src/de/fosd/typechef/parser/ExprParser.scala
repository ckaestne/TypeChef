package de.fosd.typechef.parser
import scala.util.parsing.combinator._

class ExprParser extends Parsers {
    type Elem = Token

    def parse(tokens: List[Token]) = expr(0)(new TokenReader(tokens, 0))

    def expr(feature: Int): Parser[AST] =
        term(feature) ~ opt(t("+", feature) ~> expr(feature)) ^^ { case a ~ Some(b) => Plus(a, b); case a ~ None => a }
    def term(feature: Int): Parser[AST] =
        factor(feature) ~ opt(t("*", feature) ~> expr(feature)) ^^ { case a ~ Some(b) => Mul(a, b); case a ~ None => a }
    def factor(feature: Int): Parser[AST] =
        digits(feature) ^^ { case a => Lit(a.t.toInt) } | t("(", feature) ~> expr(feature) <~ t(")", feature)

    def t(text: String, feature: Int) = new Parser[Token] {
        def apply(in: Input): ParseResult[Token] = {
            if (List(0,feature) contains in.first.f)
                elem(text, (x: Token) => x.t.eq(text))(in)
            else
                Error("unsupported feature code", in)
        }

    }
    def digits(feature: Int) = elem("digits", (x: Token) => x.t == "1" | x.t == "2" | x.t == "3" | x.t == "4" | x.t == "5")

}

//	def ifexpr: Parser[AST] = t("IFDEF")~>expr~t("ELSE")~expr<~t("ENDIF") ^^ {case a~"ELSE"~b => IF(a,b) } | factor
