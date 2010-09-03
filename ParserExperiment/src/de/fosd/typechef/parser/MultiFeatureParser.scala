package de.fosd.typechef.parser

import scala.util.parsing.input.Reader

trait MultiFeatureParser {
    type Input = Reader[Token]
    type FeatureSelection = Set[Int] //partial feature selection

    //parser 
    abstract class Parser[+T] extends (Input => ParseResult[T]) { p =>
        def ~[U](q: => Parser[U]): Parser[~[T, U]] = new Parser[T ~ U] {
            def apply(in: Input): ParseResult[T ~ U] = p(in) match {
                case Success(List((fs, x, in1))) =>
                    q(in1) match {
                        case Success(List((fs2, y, in2))) => Success(List((fs2, new ~(x, y), in2)))
                        case Success(e) => NoSuccess("multiple results not yet supported", null)
                        case NoSuccess(msg, next) => NoSuccess(msg, next)
                    }
                case Success(e) => NoSuccess("multiple results not yet supported", null)
                case NoSuccess(msg, next) => NoSuccess(msg, next)
            }
        }
    }
    sealed abstract class ParseResult[+T];
    case class NoSuccess(val msg: String, val next: Input) extends ParseResult[Nothing]
    //multiple results possible for different feature configurations
    case class Success[+T](results: List[(FeatureSelection, T, Input)]) extends ParseResult[T]
    case class ~[+a, +b](_1: a, _2: b) {
        override def toString = "(" + _1 + "~" + _2 + ")"
    }
}

class MyMultiFeatureParser extends MultiFeatureParser {

    def parse(tokens: List[Token]) = expr(Set(0))(new TokenReader(tokens, 0))

    def expr(feature: FeatureSelection): Parser[Any] =
        digits(feature) ~ t("+", feature) ~ digits(feature)

    def t(text: String, feature: FeatureSelection) = new Parser[Token] {
        def apply(in: Input): ParseResult[Token] = {
            if (in.first.t.eq(text))
                Success(List((feature, in.first, in.rest)))
            else
                NoSuccess("expected " + text, in);
        }
    }

    def digits(feature: FeatureSelection) = new Parser[Token] {
        def apply(in: Input): ParseResult[Token] = {
            val x = in.first
            if (x.t == "1" | x.t == "2" | x.t == "3" | x.t == "4" | x.t == "5")
                Success(List((feature, in.first, in.rest)))
            else
                NoSuccess("expected digit", in);
        }
    }

}