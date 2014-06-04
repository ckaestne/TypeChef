package de.fosd.typechef.parser.test.parsers

import de.fosd.typechef.parser._
import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import scala.language.higherKinds

case class DigitList2(list: List[Opt[Conditional[AST]]]) extends AST

abstract class DigitList2Parser extends MultiFeatureParser {
    type Elem = MyToken
    type TypeContext = Any
    type OptResult[T]
    def myRepOpt[T](p: => MultiParser[T], productionName: String): MultiParser[List[OptResult[T]]]

    def parse(tokens: List[MyToken]): ParseResult[Conditional[AST]] = digits(new TokenReader[MyToken, TypeContext](tokens, 0, null, EofToken), FeatureExprFactory.True).join(FeatureExprFactory.True).expectOneResult

    def digitList: MultiParser[Conditional[AST]] =
        (t("(") ~! (digits ~ t(")"))) ^^! ({
            case b1 ~ (e ~ b2) => e
        })

    def digits: MultiParser[AST]

    def t(text: String) = token(text, (x => x.t == text))

    def digit: MultiParser[AST] =
        token("digit", ((x) => x.t == "1" | x.t == "2" | x.t == "3" | x.t == "4" | x.t == "5")) ~ repPlain(t("!")) ^^ {
            case t ~ _ => Lit(t.text.toInt)
        }

}
