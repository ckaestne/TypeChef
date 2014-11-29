package de.fosd.typechef.parser.test.parsers

import de.fosd.typechef.parser._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.conditional._

class MultiExpressionParser extends MultiFeatureParser {
    type Elem = MyToken
    type TypeContext = Any

    def parse(tokens: List[MyToken]): ParseResult[Conditional[AST]] = expr(new TokenReader[MyToken, TypeContext](tokens, 0, null, EofToken), FeatureExprFactory.True).expectOneResult

    def expr: MultiParser[Conditional[AST]] = {
        val r = term ~ opt((t("+") | t("-")) ~ expr) ^^! ({
            case ~(f, Some(~(op, e))) if (op.text == "+") => One(Plus(f, e))
            case ~(f, Some(~(op, e))) if (op.text == "-") => One(Minus(f, e))
            case ~(f, None) => f
            case _ => throw new Exception("unsupported match")
        })
        r.map(ConditionalLib.combine(_))
    }


    def term: MultiParser[Conditional[AST]] =
        (fact ~ ((t("*") ~! expr) ?) ^^! ({
            case ~(f, Some(~(m, e))) => One(Mul(f, e))
            case ~(f, None) => f
        })).map(ConditionalLib.combine(_))

    def fact: MultiParser[Conditional[AST]] =
        (digits ^^! ({
            t => Lit(t.text.toInt)
        })
            | ((lookahead(t("(")) ~! (t("(") ~ expr ~ t(")"))) ^^! {
            case _ ~ (b1 ~ e ~ b2) => e
        }).map(ConditionalLib.combine(_))
            | failc("digit or '(' expected"))


    def t(text: String) = token(text, (x => x.t == text))

    def digits = token("digit", ((x) => x.t == "1" | x.t == "2" | x.t == "3" | x.t == "4" | x.t == "5"))

}