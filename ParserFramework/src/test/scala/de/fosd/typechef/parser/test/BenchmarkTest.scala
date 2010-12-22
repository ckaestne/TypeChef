package de.fosd.typechef.parser.test

import junit.framework.TestCase
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 22.12.10
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */

import de.fosd.typechef.parser._
import scala.util.parsing.input.Reader
import de.fosd.typechef.featureexpr.FeatureExpr

class BenchmarkTest extends TestCase {


    abstract class MyParser extends MultiFeatureParser {
        type Elem = MyToken
        type TypeContext = Any
        type OptResult[T]

        def symb = digit | char
        def twosymb= symb~symb

//        def digitList: MultiParser[AST] =
//            (t("(") ~! (digits ~ t(")"))) ^^! (Alt.join, {
//                case b1 ~ (e ~ b2) => e
//            })

        def digits: MultiParser[List[Opt[AST]]]      = repOpt(digit)

        def t(text: String) = token(text, (x => x.t == text))

        def digit: MultiParser[AST] =
            token("digit", ((x) => x.t == "1" | x.t == "2" | x.t == "3" | x.t == "4" | x.t == "5"))  ^^ {
                (x:Elem)=>Lit(x.t.toInt)
            }
        def char: MultiParser[AST] =
            token("digit", ((x) => x.t == "a" | x.t == "b" | x.t == "c" | x.t == "d" | x.t == "e"))  ^^ {
                (x:Elem)=>Lit(x.t.toChar)
            }


        def tr(l:List[Elem])= new TokenReader[Elem, Any](tokens, 0, null, EofToken)
    }


    @Test
    def testTokenCounter() {
           println(new MyParser().twosymb(tr(List(d1,d2)),FeatureExpr.base))
    }
}