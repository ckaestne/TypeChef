package de.fosd.typechef.parser
import scala.util.parsing.input.Reader

class MultiExpressionParser extends MultiFeatureParser {

    def parse(tokens: List[Token]): ParseResult[AST] = expr(new TokenReader(tokens, 0), Context.base)

//    def exprList: ASTParser =
//    	t("[")~expr~rep(t(",")~expr)~t("]") ^^! {
//    		case ~(~(~(b1,e),List(l)),b2) => e
//    	}
    
    
    def expr: ASTParser =
        term ~ opt((t("+") | t("-")) ~~ expr) ^^! {
            case ~(f, Some(~(op, e))) if (op.text == "+") => Plus(f, e)
            case ~(f, Some(~(op, e))) if (op.text == "-") => Minus(f, e)
            case ~(f, None) => f
        }

    def term: ASTParser =
        fact ~ opt(t("*") ~~ expr) ^^! { case ~(f, Some(~(m, e))) => Mul(f, e); case ~(f, None) => f }

    def fact: ASTParser =
        digits ^^! { t => Lit(t.text.toInt) } | (t("(") ~~ expr ~ t(")")) ^^! { case (~(~(b1, e), b2)) => e }

    def t(text: String) = textToken(text)

    def digits = token("digit", ((x) => x.t == "1" | x.t == "2" | x.t == "3" | x.t == "4" | x.t == "5"))

}