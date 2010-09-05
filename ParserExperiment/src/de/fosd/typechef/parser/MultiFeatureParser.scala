package de.fosd.typechef.parser

import scala.util.parsing.input.Reader

trait MultiFeatureParser {
    type Input = Reader[Token]
    type FeatureSelection = Set[Int] //partial feature selection

    //parser 
    abstract class Parser[+T] extends ((Input, FeatureSelection) => ParseResult[T]) { p =>
        def ~[U](q: => Parser[U]): Parser[~[T, U]] = new Parser[T ~ U] {
            def apply(in: Input, feature: FeatureSelection): ParseResult[T ~ U] = {
                //sequence

                val firstResults = p(in, feature).get
                val combinedResults: List[List[FeatureParseResult[T ~ U]]] =
                    for (val firstResult <- firstResults)
                        yield firstResult match {
                        case Success(fs, x, in1) => {
                            val secondResults = q(in1, fs).get
                            val combinedResults2: List[FeatureParseResult[T ~ U]] =
                                for (val secondResult <- secondResults) yield {
                                    if (fs subsetOf secondResult.f)
                                        secondResult match {
                                            case Success(fs2, y, in2) =>
                                                Success(fs2, new ~(x, y), in2)
                                            case NoSuccess(fs2, msg, next) =>
                                                NoSuccess(fs, msg, next)

                                        }
                                    else
                                        NoSuccess(fs, "incompatible features " + fs + " and " + secondResult.f, in1)
                                }

                            combinedResults2
                        }
                        case NoSuccess(fs, msg, next) => List(NoSuccess(fs, msg, next))
                    }
                val r: List[FeatureParseResult[T ~ U]] = List.flatten(combinedResults)
                new ParseResult(r)
            }
        }
        def |[U >: T](q: => Parser[U]): Parser[U] = new Parser[U] {
            def apply(in: Input, feature: FeatureSelection): ParseResult[U] = {
                val firstResults = p(in, feature).get
                val combinedResults: List[List[FeatureParseResult[U]]] =
                    for (val firstResult <- firstResults)
                        yield firstResult match {
                        case suc@Success(_, _, _) => List(suc)
                        case NoSuccess(fs, _, _) => q(in, fs).get
                    }
                val r: List[FeatureParseResult[U]] = List.flatten(combinedResults)
                new ParseResult(r)
            }
        }
        def ^^[U](f: T => U): Parser[U] = map(f)
        def map[U](f: T => U): Parser[U] = new Parser[U] { 
        	def apply(in: Input, feature: FeatureSelection) = p(in, feature).map(f) 
        }
    }

    case class ParseResult[+T](results: List[FeatureParseResult[T]]) {
        def get = results
        def isError = results.exists(_ match { case NoSuccess(_, _, _) => true; case _ => false })
        def map[U](f: T => U): ParseResult[U] = ParseResult(results.map(_.map(f)))
        //        def ++(that: ParseResult[T]) = ParseResult(this.get ++ that.get)
    }
    sealed abstract class FeatureParseResult[+T](feature: FeatureSelection) {
        def f = feature
        def map[U](f: T => U): FeatureParseResult[U]
    }
    case class NoSuccess(feature: FeatureSelection, val msg: String, val next: Input) extends FeatureParseResult[Nothing](feature){
    	def map[U](f: Nothing => U) = this
    }
    case class Success[+T](feature: FeatureSelection, val result: T, val next: Input) extends FeatureParseResult[T](feature) {
    	def map[U](f: T => U): FeatureParseResult[U] = Success(feature, f(result), next)    	
    }
    case class ~[+a, +b](_1: a, _2: b) {
        override def toString = "(" + _1 + "~" + _2 + ")"
    }
}

class MyMultiFeatureParser extends MultiFeatureParser {

    def parse(tokens: List[Token]) = expr(new TokenReader(tokens, 0), Set(0))

    def expr: Parser[Any] =
        digits ~ (t("+") | t("-")) ~ digits

    def t(text: String) = new Parser[Token] {
        def apply(in: Input, feature: FeatureSelection): ParseResult[Token] = {
            //only attempt to parse if feature is supported
            val start = skipHidden(in, feature)
            if (isSupported(start.first, feature)) {
                if (start.first.t.eq(text))
                    ParseResult(List(Success(feature, start.first, start.rest)))
                else
                    ParseResult(List(NoSuccess(feature, "expected " + text, start)))
            } else
                splitParser(start, feature)
        }
        def splitParser(in: Input, features: FeatureSelection): ParseResult[Token] =
            ParseResult[Token](this(in, features + in.first.f).get ++ this(in, features + (-in.first.f)).get)
    }

    def digits = new Parser[Token] {
        def apply(in: Input, feature: FeatureSelection): ParseResult[Token] = {
            //only attempt to parse if feature is supported
            val start = skipHidden(in, feature)
            if (isSupported(start.first, feature)) {
                val x = start.first
                if (x.t == "1" | x.t == "2" | x.t == "3" | x.t == "4" | x.t == "5")
                    ParseResult(List(Success(feature, start.first, start.rest)))
                else
                    ParseResult(List(NoSuccess(feature, "expected digit", start)))
            } else
                splitParser(start, feature)
        }
        def splitParser(in: Input, features: FeatureSelection): ParseResult[Token] =
            ParseResult[Token](this(in, features + in.first.f).get ++ this(in, features + (-in.first.f)).get)
    }

    def isSupported(token: Token, feature: FeatureSelection) =
        (token.f == 0) || (feature contains token.f) || (feature contains -token.f)
    def skipHidden(in: Input, feature: FeatureSelection): Input = {
        var result = in
        while (result.first.f != 0 && (feature contains (-result.first.f)))
            result = result.rest
        result
    }
}