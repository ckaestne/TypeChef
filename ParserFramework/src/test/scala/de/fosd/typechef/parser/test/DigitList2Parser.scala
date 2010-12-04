package de.fosd.typechef.parser.test
import de.fosd.typechef.parser._
import scala.util.parsing.input.Reader
import de.fosd.typechef.featureexpr.FeatureExpr

case class DigitList2(list: List[Opt[AST]]) extends AST

abstract class DigitList2Parser extends MultiFeatureParser {
    type Elem = MyToken
    type TypeContext = Any
    type OptResult[T]// = Opt[T]
    //var myRepOpt: [T] (=> MultiParser[T], (FeatureExpr, T, T) => T, String) => MultiParser[List[Opt[T]]] = repOpt
    def myRepOpt[T](p: => MultiParser[T], joinFunction: (FeatureExpr, T, T) => T, productionName: String): MultiParser[List[OptResult[T]]]
    //= repOpt(p, joinFunction, productionName)
    /*var myRepOpt = repOpt _
    //def setRepOpt(myRepOpt: [T] (=> MultiParser[T], (FeatureExpr, T, T) => T, String) => MultiParser[List[Opt[T]]]) {
    def setRepOpt(myRepOpt: (=> MultiParser[_ >: Nothing], (FeatureExpr, Nothing, Nothing) => Nothing, String) 
         => DigitList2Parser.this.MultiParser[List[de.fosd.typechef.parser.Opt[Nothing]]]) {
        this.myRepOpt = myRepOpt
    }*/

    def parse(tokens: List[MyToken]): ParseResult[AST, MyToken, TypeContext] = digits(new TokenReader[MyToken, TypeContext](tokens, 0, null,EofToken), FeatureExpr.base).forceJoin[AST](FeatureExpr.base,Alt.join)

    def digitList: MultiParser[AST] =
        (t("(") ~! (digits ~ t(")"))) ^^! (Alt.join, { case b1 ~(e ~ b2) => e })

    def digits: MultiParser[AST]

    def t(text: String) = token(text, (x => x.t == text))

    def digit: MultiParser[AST] =
        token("digit", ((x) => x.t == "1" | x.t == "2" | x.t == "3" | x.t == "4" | x.t == "5")) ~ repPlain(t("!")) ^^
            { case t ~ _ => Lit(t.text.toInt) }

}
