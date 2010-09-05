package de.fosd.typechef.parser

import scala.util.parsing.input.Reader

trait MultiFeatureParser {
    type Input = Reader[Token]
    type FeatureSelection = Context

    //parser 
    abstract class Parser[+T] extends ((Input, FeatureSelection) => ParseResult[T]) { p =>
        def ~[U](q: => Parser[U]): Parser[~[T, U]] = new Parser[T ~ U] {
            def apply(in: Input, feature: FeatureSelection): ParseResult[T ~ U] = {
                //sequence
                var combinedResults: Map[FeatureSelection, FeatureParseResult[T ~ U]] = Map()
                for ((fs, firstResult) <- joinAlternatives(p(in, feature).get))
                    firstResult match {
                        case Success(x, in1) => {
                            for ((fs2, secondResult) <- joinAlternatives(q(in1, fs).get)) {
                                if (fs subsetOf fs2)
                                    secondResult match {
                                        case Success(y, in2) =>
                                            combinedResults = combinedResults + (fs2 -> Success(new ~(x, y), in2))
                                        case NoSuccess(msg, next) =>
                                            combinedResults = combinedResults + (fs -> NoSuccess(msg, next))

                                    }
                                else
                                    combinedResults = combinedResults + (fs -> NoSuccess("incompatible features " + fs + " and " + fs2, in1))
                            }
                        }
                        case NoSuccess(msg, next) => combinedResults = combinedResults + (fs -> NoSuccess(msg, next))
                    }
                new ParseResult(combinedResults)
            }
        }
        def |[U >: T](q: => Parser[U]): Parser[U] = new Parser[U] {
            def apply(in: Input, feature: FeatureSelection): ParseResult[U] = {
                val firstResults = p(in, feature).get
                var combinedResults: Map[FeatureSelection, FeatureParseResult[U]] = Map()
                for ((fs, firstResult) <- firstResults)
                    firstResult match {
                        case suc@Success(_, _) => combinedResults = combinedResults + (fs -> suc)
                        case NoSuccess(_, _) => combinedResults = combinedResults ++ q(in, fs).get
                    }
                new ParseResult(combinedResults)
            }
        }
        def ^^[U](f: T => U): Parser[U] = map(f)
        def map[U](f: T => U): Parser[U] = new Parser[U] {
            def apply(in: Input, feature: FeatureSelection) = p(in, feature).map(f)
        }
        def splitParser(in: Input, features: FeatureSelection): ParseResult[T] = {
            val (contextA, contextB) = features.split(in.first.f)
            ParseResult[T](p(in, contextA).get ++ p(in, contextB).get)
        }
    }

    def opt[T](p: => Parser[T]): Parser[Option[T]] =
        p ^^ (x => Some(x)) | success(None)

    def success[T](v: T) =
        Parser { (in: Input, fs: FeatureSelection) => ParseResult(Map(fs -> Success(v, in))) }

    def Parser[T](f: (Input, FeatureSelection) => ParseResult[T]): Parser[T] =
        new Parser[T] { def apply(in: Input, fs: FeatureSelection) = f(in, fs) }

    case class ParseResult[+T](results: Map[FeatureSelection, FeatureParseResult[T]]) {
        def get =
            results

        def isError =
            results.values.exists(_ match { case NoSuccess(_, _) => true; case _ => false })

        def map[U](f: T => U): ParseResult[U] =
            ParseResult(results.map((e) => (e._1 -> e._2.map(f))))
    }
    sealed abstract class FeatureParseResult[+T](nextInput: Input) {
        def map[U](f: T => U): FeatureParseResult[U]
        def next = nextInput
        def join(feature: Int, that: FeatureParseResult[T]): FeatureParseResult[IF[Int, T, T]]
    }
    case class NoSuccess(msg: String, nextInput: Input) extends FeatureParseResult[Nothing](nextInput) {
        def map[U](f: Nothing => U) = this
        def join(feature: Int, that: FeatureParseResult[Nothing]) = this
    }
    case class Success[+T](result: T, nextInput: Input) extends FeatureParseResult[T](nextInput) {
        def map[U](f: T => U): FeatureParseResult[U] = Success(f(result), next)
        def join(feature: Int, that: FeatureParseResult[T]) = that match {
            case Success(thatResult, thatNext) => Success(IF(feature, this.result, thatResult), nextInput)
            case ns@NoSuccess(_, _) => ns
        }

    }
    //checks whether all alternatives consumed the same amount of tokens (precondition to merging)
    def joinAlternatives[T](results: Map[FeatureSelection, FeatureParseResult[T]]): Map[FeatureSelection, FeatureParseResult[T]] = {
        val commonInput = results.head._2.next
        if (results.size <= 1 || !results.values.forall(_.next == commonInput))
            results
        else
            joinCompatibleAlternatives(results)
    }

    //join alternatives (already checked that parsing rest is compatible)
    def joinCompatibleAlternatives[T](inResults: Map[FeatureSelection, FeatureParseResult[T]]): Map[FeatureSelection, FeatureParseResult[T]] = {
        var result: Map[FeatureSelection, FeatureParseResult[T]] = inResults
        var changed = true;
        while (changed) {
            changed = false
            var input = result
            result = Map()
            for (val featureContext: FeatureSelection <- input.keys) {
                if (featureContext.feature > 0 && input.contains(featureContext.complement)) {
                    result = result + (featureContext.parent -> joinResults(featureContext.feature,input(featureContext), input(featureContext.complement)))
                    changed = true
                } else
                    result = result + (featureContext -> input(featureContext))
            }

        }
        result
    }
    def joinResults[T](feature:Int,firstResult:T,secondResult:T):T

    case class ~[+a, +b](_1: a, _2: b) {
        override def toString = "(" + _1 + "~" + _2 + ")"
    }
    class Result
    case class IF[Int, +a, +b](f: Int, _1: a, _2: b) extends Result {
        override def toString = "IF(" + f + "," + _1 + "," + _2 + ")"
    }
}

class MyMultiFeatureParser extends MultiFeatureParser {

    def parse(tokens: List[Token]) = expr(new TokenReader(tokens, 0), Context.base)

    def expr: Parser[Any] =
        term ~ opt((t("+") | t("-")) ~ expr)
    def term: Parser[Any] =
        fact ~ opt(t("*") ~ expr)
    def fact: Parser[Any] =
        digits | (t("(") ~ expr ~ t(")"))

    def t(text: String) = new Parser[Token] {
        def apply(in: Input, feature: FeatureSelection): ParseResult[Token] = {
            //only attempt to parse if feature is supported
            val start = skipHidden(in, feature)
            if (isSupported(start.first, feature)) {
                if (start.first.t.eq(text))
                    ParseResult(Map(feature -> Success(start.first, skipHidden(start.rest, feature))))
                else
                    ParseResult(Map(feature -> NoSuccess("expected " + text, start)))
            } else
                splitParser(start, feature)
        }
    }

    def digits = new Parser[Token] {
        def apply(in: Input, feature: FeatureSelection): ParseResult[Token] = {
            //only attempt to parse if feature is supported
            val start = skipHidden(in, feature)
            if (isSupported(start.first, feature)) {
                val x = start.first
                if (x.t == "1" | x.t == "2" | x.t == "3" | x.t == "4" | x.t == "5")
                    ParseResult(Map(feature -> Success(start.first, skipHidden(start.rest, feature))))
                else
                    ParseResult(Map(feature -> NoSuccess("expected digit", start)))
            } else
                splitParser(start, feature)
        }
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
