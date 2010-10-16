package de.fosd.typechef.parser.test
import de.fosd.typechef.parser._
import scala.util.parsing.input.Reader
import de.fosd.typechef.featureexpr.FeatureExpr

case class DigitList2(list: List[Opt[AST]]) extends AST

class DigitList2Parser extends MultiFeatureParser {
    type Elem = MyToken
    type Context = Any

    def parse(tokens: List[MyToken]): ParseResult[AST, MyToken, Context] = digits(new TokenReader[MyToken, Context](tokens, 0, null,EofToken), FeatureExpr.base).forceJoin[AST](Alt.join)

    def digitList: MultiParser[AST] =
        (t("(") ~! (digits ~ t(")"))) ^^! (Alt.join, { case b1 ~(e ~ b2) => e })

    def digits: MultiParser[AST] =
        repOpt(digitList | digit, Alt.join) ^^! (Alt.join, { //List(Opt(AST)) -> DigitList[List[Opt[Lit]]
            DigitList2(_)
        })

    def t(text: String) = token(text, (x => x.t == text))

    def digit: MultiParser[AST] =
        token("digit", ((x) => x.t == "1" | x.t == "2" | x.t == "3" | x.t == "4" | x.t == "5")) ~ rep(t("!")) ^^
            { case t ~ _ => Lit(t.text.toInt) }

}
