package de.fosd.typechef.parser
import scala.util.parsing.input.Reader
import de.fosd.typechef.featureexpr.FeatureExpr

case class DigitList(list: List[Lit]) extends AST

class DigitListParser extends MultiFeatureParser {
    type Elem = MyToken

    def parse(tokens: List[MyToken]): ParseResult[AST, MyToken] = digitList(new TokenReader[MyToken](tokens, 0), FeatureExpr.base).forceJoin[AST](Alt.join)

    def digitList: MultiParser[AST] =
        (t("(") ~ digits ~ t(")")) ^^!(Alt.join, { case (~(~(b1, e), b2)) => e })

    def digits: MultiParser[AST] =
        digit ~ opt(digits) ^^!(Alt.join, {
            case ~(x, Some(DigitList(list: List[Lit]))) => DigitList(List(x) ++ list)
            case ~(x, Some(Alt(f, DigitList(listA: List[Lit]), DigitList(listB: List[Lit])))) => Alt(f, DigitList(List(x) ++ listA), DigitList(List(x) ++ listB))
            case ~(x, None) => DigitList(List(x))
        })

    def t(text: String) = token(text, (x => x.t == text))

    def digit: MultiParser[Lit] = token("digit", ((x) => x.t == "1" | x.t == "2" | x.t == "3" | x.t == "4" | x.t == "5")) ^^ { t => Lit(t.text.toInt) }

}