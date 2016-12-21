package de.fosd.typechef.parser.test.parsers

import de.fosd.typechef.parser._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.conditional._

case class DigitList(list: List[Lit]) extends AST

class DigitListParser extends MultiFeatureParser {
    type Elem = MyToken
    type TypeContext = Any

    def parse(tokens: List[MyToken]): ParseResult[Conditional[AST]] = digitList(new TokenReader[MyToken, TypeContext](tokens, 0, null, EofToken), FeatureExprFactory.True).expectOneResult

    def digitList: MultiParser[Conditional[AST]] =
        (t("(") ~ digits ~ t(")")) ^^ {
            case (~(~(b1, e), b2)) => e
        }

    def digits: MultiParser[Conditional[AST]] =
        (digit ~ opt(digits) ^^! {
            case ~(x, Some(One(DigitList(list: List[_])))) => One(DigitList(List(x) ++ list))
            case ~(x, Some(Choice(f, One(DigitList(listA: List[_])), One(DigitList(listB: List[_]))))) => Choice(f, One(DigitList(List(x) ++ listA)), One(DigitList(List(x) ++ listB)))
            case ~(x, None) => One(DigitList(List(x)))
            case _ => throw new RuntimeException()
        }).map(ConditionalLib.combine(_))

    def t(text: String) = token(text, (x => x.t == text))

    def digit: MultiParser[Lit] = token("digit", ((x) => x.t == "1" | x.t == "2" | x.t == "3" | x.t == "4" | x.t == "5")) ^^ {
        t => Lit(t.text.toInt)
    }

}
