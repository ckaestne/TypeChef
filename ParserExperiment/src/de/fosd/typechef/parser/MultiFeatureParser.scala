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
    type ParserState = Context

    object ASTParser {
        def joinASTs[T <: AST](parser: MultiParser[T]): ASTParser = new ASTParser {
            def apply(in: Input, feature: FeatureSelection): ParseResult[T] = {
                joinAlternativesAST(parser(in, feature))
            }
        }

        //checks whether all alternatives consumed the same amount of tokens (precondition to merging)
        def joinAlternativesAST(inResults: Map[FeatureSelection, ParseResult[AST]]): ParseResult[AST] = {
            assert(inResults.size >= 1)
            val commonInput = inResults.head._2.next
            assert(!inResults.values.forall(_.isSuccess) || inResults.values.forall(_.next == commonInput))

            var result: Map[FeatureSelection, ParseResult[AST]] = inResults
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
            assert(result.size == 1)
            result.values.first
        }

        def joinResults(feature: Int, firstResult: ParseResult[AST], secondResult: ParseResult[AST]): ParseResult[AST] =
            (firstResult, secondResult) match {
                case (Success(r1, in), Success(r2, _)) => Success(Alt(feature, r1, r2), in)
                case _ => {
                    System.err.println("not merging " + firstResult + " and " + secondResult)
                    firstResult
                    //super.joinResults(feature, firstResult, secondResult)
                }
            }

    }
    abstract class ASTParser extends ((Input, ParserState) => ParseResult[AST]) { thisParser =>
        type T = AST

        def ~[U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = toMultiParser ~ thatParser

        def |(alternativeParser: => ASTParser): ASTParser = ASTParser.joinASTs(thisParser.toMultiParser | alternativeParser.toMultiParser)

        def toMultiParser: MultiParser[T] = new MultiParser[T] {
            def apply(in: Input, parserState: ParserState): Map[Context, ParseResult[T]] = Map(parserState -> thisParser(in, parserState))
        }
    }

    //parser 
    abstract class MultiParser[T] extends ((Input, ParserState) => Map[Context, ParseResult[T]]) { thisParser =>

        def ~~(thatParser: => ASTParser): MultiParser[~[T, AST]] = this ~ thatParser.toMultiParser

        /**
         * sequencing is difficult when each element can have multiple results for different features
         * tries to join split parsers as early as possible
         */
        def ~[U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = new MultiParser[~[T, U]] {
            def apply(in: Input, parserState: ParserState): Map[Context, ParseResult[~[T, U]]] = {
                //sequence
                var combinedResults: Map[FeatureSelection, ParseResult[T ~ U]] = Map()
                for ((fs, firstResult) <- thisParser(in, parserState))
                    firstResult match {
                        case Success(x, in1) => {
                            for ((fs2, secondResult) <- thatParser(in1, fs)) {
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
                combinedResults
            }
        }

        /**
         * alternatives in the presence of multi-parsing
         * (no attempt to join yet)
         */
        def |[U >: T](alternativeParser: => MultiParser[U]): MultiParser[U] = new MultiParser[U] {
            def apply(in: Input, feature: FeatureSelection): Map[Context, ParseResult[U]] = {
                var combinedResults: Map[Context, ParseResult[U]] = Map()
                for ((fs, firstResult) <- thisParser(in, feature))
                    firstResult match {
                        case suc@Success(_, _) => combinedResults = combinedResults + (fs -> suc)
                        case NoSuccess(_, _) => combinedResults = combinedResults ++ alternativeParser(in, fs)
                    }
                combinedResults
            }
        }
        /**
         * ^^ as in the original combinator parser framework
         */
        def ^^[U](f: T => U): MultiParser[U] = map(f)
        def map[U](f: T => U): MultiParser[U] = new MultiParser[U] {
            def apply(in: Input, feature: FeatureSelection): Map[Context, ParseResult[U]] =
                thisParser(in, feature).map(((e) => (e._1 -> e._2.map(f))))
        }

        /**
         * create AST (joins when necessary)
         */
        def ^^!(f: T => AST): ASTParser = ASTParser.joinASTs(map(f))

    }
    //    abstract class ASTParser extends Parser[AST]

    /**
     * opt (and helper functions) as in the original combinator parser framework
     */
    def opt[T](p: => MultiParser[T]): MultiParser[Option[T]] =
        p ^^ (x => Some(x)) | success(None)
    def success[T](v: T) =
        MultiParser { (in: Input, fs: FeatureSelection) => Map(fs -> Success(v, in)) }
    def MultiParser[T](f: (Input, FeatureSelection) => Map[Context, ParseResult[T]]): MultiParser[T] =
        new MultiParser[T] { def apply(in: Input, fs: FeatureSelection) = f(in, fs) }

    case class ~[+a, +b](_1: a, _2: b) {
        override def toString = "(" + _1 + "~" + _2 + ")"
    }

    def matchInput(p: Token => Boolean, err: Token => String) = new MultiParser[Token] {
        def apply(in: Input, feature: FeatureSelection): Map[Context, ParseResult[Token]] = {
            //only attempt to parse if feature is supported
            val start = skipHidden(in, feature)
            if (isSupported(start.first, feature)) {
                if (p(start.first))
                    Map(feature -> Success(start.first, skipHidden(start.rest, feature)))
                else
                    Map(feature -> NoSuccess(err(start.first), start))
            } else
                splitParser(start, feature)
        }
        def splitParser(in: Input, features: FeatureSelection): Map[Context, ParseResult[Token]] = {
            val (contextA, contextB) = features.split(in.first.f)
            this(in, contextA) ++ this(in, contextB)
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

    def token(kind: String, p: Token => Boolean) = matchInput(p, inEl => kind + " expected")
    def textToken(kind: String) = token(kind, (_.text == kind))

}

