package de.fosd.typechef.parser.test.parsers

import de.fosd.typechef.parser._
import de.fosd.typechef.conditional._
import scala.language.higherKinds


class CharDigitParser extends MultiFeatureParser {
    type Elem = MyToken
    type TypeContext = Any
    type OptResult[T]

    def symb = digit | char
    def twosymb = symb ~ symb
    def ab = (symb ~ char) | (symb ~ digit)
    def parenDigit = t("(") ~ digit ~ t(")")
    def parenAb = t("(") ~ ab ~ t(")")

    def digits: MultiParser[List[Opt[AST]]] = repOpt(digit)

    def t(text: String) = token(text, (x => x.t == text))

    def comma = t(",")

    def digit: MultiParser[Lit] =
        token("digit", ((x) => x.t == "0" | x.t == "1" | x.t == "2" | x.t == "3" | x.t == "4" | x.t == "5" | x.t == "6" | x.t == "7" | x.t == "8" | x.t == "9")) ^^ {
            (x: Elem) => Lit(x.text.toInt)
        }
    def number: MultiParser[Lit] =
        token("number", ((s) =>
            try {
                s.text.toInt
                true
            } catch {
                case _: java.lang.NumberFormatException => false
            })) ^^ {
            (x: Elem) => Lit(x.text.toInt)
        }
    def char: MultiParser[AST] =
        token("char", ((x) => x.t == "a" | x.t == "b" | x.t == "c" | x.t == "d" | x.t == "e")) ^^ {
            (x: Elem) => Char(x.text)
        }

    def expr: MultiParser[Conditional[AST]] = (expr1 ~ opt(t("*") ~> expr) ^^! ({
        case ~(f, Some(e)) => One(Mul(f, e))
        case ~(f, None) => f
    })).map(ConditionalLib.combine(_))

    def expr1: MultiParser[Conditional[AST]] = (expr2 ~ opt(t("+") ~> expr) ^^! ({
        case ~(f, Some(e)) => One(Plus(f, e))
        case ~(f, None) => f
    })).map(ConditionalLib.combine(_))

    def expr2: MultiParser[Conditional[AST]] = t("(") ~> expr <~ t(")") | (digit.join)


    def tr(l: List[Elem]): Input = new TokenReader[Elem, Any](l, 0, null, EofToken)
}