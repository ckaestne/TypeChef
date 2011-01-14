package de.fosd.typechef.parser.test.parsers

import de.fosd.typechef.parser._

/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 30.12.10
 * Time: 09:23
 * To change this template use File | Settings | File Templates.
 */

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

    def expr: MultiParser[AST] = expr1 ~ opt(t("*") ~> expr) ^^! (Alt.join, {
        case ~(f, Some(e)) => Mul(f, e);
        case ~(f, None) => f
    })
    def expr1: MultiParser[AST] = expr2 ~ opt(t("+") ~> expr) ^^! (Alt.join, {
        case ~(f, Some(e)) => Plus(f, e);
        case ~(f, None) => f
    })
    def expr2: MultiParser[AST] = t("(") ~> expr <~ t(")") | (digit ! (Alt.join))


    def tr(l: List[Elem]): Input = new TokenReader[Elem, Any](l, 0, null, EofToken)
}