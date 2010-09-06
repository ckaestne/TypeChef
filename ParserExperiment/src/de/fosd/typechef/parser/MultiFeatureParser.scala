package de.fosd.typechef.parser

import scala.util.parsing.input.Reader

/**
 * adopted parser combinator framework with support for multi-feature parsing
 * 
 * @author kaestner
 */
trait MultiFeatureParser {
    type Input = Reader[Token]
    type FeatureSelection = Context

    //parser 
    abstract class Parser[+T] extends ((Input, FeatureSelection) => ParseResult[T]) { p =>

        /**
         * sequencing is difficult when each element can have multiple results for different features
         * tries to join split parsers as early as possible
         */
        def ~[U](q: => Parser[U]): Parser[~[T, U]] = new Parser[T ~ U] {
            def apply(in: Input, feature: FeatureSelection): ParseResult[T ~ U] = {
                //sequence
                var combinedResults: Map[FeatureSelection, FeatureParseResult[T ~ U]] = Map()
                val firstResults = /*joinAlternatives*/ (p(in, feature).get)
                for ((fs, firstResult) <- firstResults)
                    firstResult match {
                        case Success(x, in1) => {
                            val secondResults = /*joinAlternatives*/ (q(in1, fs).get)
                            for ((fs2, secondResult) <- secondResults) {
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

        /**
         * alternatives in the presence of multi-parsing
         * (no attempt to join yet)
         */
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
        /**
         * ^^ as in the original combinator parser framework
         */
        def ^^[U](f: T => U): Parser[U] = map(f)
        def map[U](f: T => U): Parser[U] = new Parser[U] {
            def apply(in: Input, feature: FeatureSelection) = p(in, feature).map(f)
        }
        def splitParser(in: Input, features: FeatureSelection): ParseResult[T] = {
            val (contextA, contextB) = features.split(in.first.f)
            ParseResult[T](p(in, contextA).get ++ p(in, contextB).get)
        }
    }

    /**
     * opt (and helper functions) as in the original combinator parser framework
     */
    def opt[T](p: => Parser[T]): Parser[Option[T]] =
        p ^^ (x => Some(x)) | success(None)
    def success[T](v: T) =
        Parser { (in: Input, fs: FeatureSelection) => ParseResult(Map(fs -> Success(v, in))) }
    def Parser[T](f: (Input, FeatureSelection) => ParseResult[T]): Parser[T] =
        new Parser[T] { def apply(in: Input, fs: FeatureSelection) = f(in, fs) }

    //checks whether all alternatives consumed the same amount of tokens (precondition to merging)
    def joinAlternativesAST(results: Map[FeatureSelection, FeatureParseResult[AST]]): Map[FeatureSelection, FeatureParseResult[AST]] = {
        val commonInput = results.head._2.next
        if (results.size <= 1 || !results.values.forall(_.next == commonInput))
            results
        else
            joinCompatibleAlternatives(results)
    }

    //join alternatives (already checked that parsing rest is compatible)
    def joinCompatibleAlternatives(inResults: Map[FeatureSelection, FeatureParseResult[AST]]): Map[FeatureSelection, FeatureParseResult[AST]] = {
        var result: Map[FeatureSelection, FeatureParseResult[AST]] = inResults
        var changed = true;
        while (changed) {
            changed = false
            var input = result
            result = Map()
            for (val featureContext: FeatureSelection <- input.keys) {
                if (featureContext.feature > 0 && input.contains(featureContext.complement)) {
                	if (input(featureContext) == input(featureContext.complement))
                		result = result + (featureContext.parent -> input(featureContext))
                	else
                		result = result + (featureContext.parent -> joinResults(featureContext.feature, input(featureContext), input(featureContext.complement)))
                    changed = true
                } else if (featureContext.feature >= 0 || !input.contains(featureContext.complement))
                    result = result + (featureContext -> input(featureContext))
            }

        }
        result
    }
    //    def joinResults[T](feature: Int, firstResult: T, secondResult: T): T = {
    //        firstResult
    //    }
    def joinResults(feature: Int, firstResult: FeatureParseResult[AST], secondResult: FeatureParseResult[AST]): FeatureParseResult[AST]

    case class ~[+a, +b](_1: a, _2: b) {
        override def toString = "(" + _1 + "~" + _2 + ")"
    }

  
}

class MyMultiFeatureParser extends MultiFeatureParser {

    override def joinResults(feature: Int, firstResult: FeatureParseResult[AST], secondResult: FeatureParseResult[AST]): FeatureParseResult[AST] =
        (firstResult, secondResult) match {
            case (Success(r1, in), Success(r2, _)) => Success(Alt(feature, r1, r2), in)
            case _ => {
                System.err.println("not merging " + firstResult + " and " + secondResult)
                firstResult
                //super.joinResults(feature, firstResult, secondResult)
            }
        }

    def parse(tokens: List[Token]): ParseResult[AST] = expr(new TokenReader(tokens, 0), Context.base)

    def expr: Parser[AST] = joinASTs(
        term ~ opt((t("+") | t("-")) ~ expr) ^^ {
            case ~(f, Some(~(op, e))) if (op.text == "+") => Plus(f, e)
            case ~(f, Some(~(op, e))) if (op.text == "-") => Minus(f, e)
            case ~(f, None) => f
        }
        )
    def term: Parser[AST] = joinASTs(
        fact ~ opt(t("*") ~ expr) ^^ { case ~(f, Some(~(m, e))) => Mul(f, e); case ~(f, None) => f }
        )
    def fact: Parser[AST] = joinASTs(
        digits ^^ { t => Lit(t.text.toInt) } | (t("(") ~ expr ~ t(")")) ^^ { case (~(~(b1, e), b2)) => e }
        )
    def joinASTs(parser: Parser[AST]): Parser[AST] = new Parser[AST] {
        def apply(in: Input, feature: FeatureSelection): ParseResult[AST] = {
            val result = parser(in, feature)
            ParseResult(joinAlternativesAST(result.get))
        }
    }

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
