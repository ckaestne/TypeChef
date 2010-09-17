package de.fosd.typechef.parser.test
import de.fosd.typechef.parser._
import scala.util.parsing.input.Reader
import de.fosd.typechef.featureexpr.FeatureExpr

class MultiExpressionParser extends MultiFeatureParser {
    type Elem = MyToken
    	type Context = Any

    def parse(tokens: List[MyToken]): ParseResult[AST,MyToken,Context] = expr(new TokenReader[MyToken,Context](tokens, 0,null), FeatureExpr.base).forceJoin(Alt.join)

    def expr: MultiParser[AST] =
        term ~ opt((t("+") | t("-")) ~ expr) ^^!(Alt.join, {
            case ~(f, Some(~(op, e))) if (op.text == "+") => Plus(f, e)
            case ~(f, Some(~(op, e))) if (op.text == "-") => Minus(f, e)
            case ~(f, None) => f
        })

    def term: MultiParser[AST] =
        fact ~ opt(t("*") ~ expr) ^^!(Alt.join, {
            case ~(f, Some(~(m, e))) => Mul(f, e);
            case ~(f, None) => f
        })

    def fact: MultiParser[AST] =
        digits ^^!(Alt.join, { t => Lit(t.text.toInt) }) | (t("(") ~ expr ~ t(")")) ^^!(Alt.join, { case (~(~(b1, e), b2)) => e })

    def t(text: String) = token(text,(x=>x.t==text))

    def digits = token("digit", ((x) => x.t == "1" | x.t == "2" | x.t == "3" | x.t == "4" | x.t == "5"))

}
