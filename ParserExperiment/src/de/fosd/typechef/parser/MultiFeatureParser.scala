package de.fosd.typechef.parser
import scala.annotation.tailrec

import scala.util.parsing.input.Reader
import scala.collection.mutable.ListBuffer
import de.fosd.typechef.featureexpr.FeatureExpr


abstract class AST

/**
 * adopted parser combinator framework with support for multi-feature parsing
 * 
 * @author kaestner
 */
trait MultiFeatureParser {
    type Input = Reader[Token]
    type ParserState = FeatureExpr

    object ASTParser {
        def joinASTs[T <: AST](parser: MultiParser[T]): ASTParser = new ASTParser {
            def apply(in: Input, feature: FeatureExpr): ParseResult[T] = {
                parser(in, feature).join[AST]((f, x, y) => if (x == y) x else Alt(f, x, y))
            }
        }
    }
    abstract class ASTParser extends ((Input, ParserState) => ParseResult[AST]) { thisParser =>
        type T = AST

        def ~[U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = toMultiParser ~ thatParser

        def |(alternativeParser: => ASTParser): ASTParser = ASTParser.joinASTs(thisParser.toMultiParser | alternativeParser.toMultiParser)

        def toMultiParser: MultiParser[T] = new MultiParser[T] {
            def apply(in: Input, parserState: ParserState): MultiParseResult[T] = thisParser(in, parserState)
        }
    }

    //parser 
    abstract class MultiParser[T] extends ((Input, ParserState) => MultiParseResult[T]) { thisParser =>

        def ~~(thatParser: => ASTParser): MultiParser[~[T, AST]] = this ~ thatParser.toMultiParser

        /**
         * sequencing is difficult when each element can have multiple results for different features
         * tries to join split parsers as early as possible
         */
        def ~[U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = new MultiParser[~[T, U]] {
            def apply(in: Input, parserState: ParserState): MultiParseResult[~[T, U]] =
                thisParser(in, parserState).seqAllSuccessful(parserState, (fs: FeatureExpr, x: Success[T]) => x.seq(fs, thatParser(x.next, fs)))
        }

        /**
         * alternatives in the presence of multi-parsing
         * (no attempt to join yet)
         */
        def |[U >: T](alternativeParser: => MultiParser[U]): MultiParser[U] = new MultiParser[U] {
            def apply(in: Input, parserState: ParserState): MultiParseResult[U] = {
                thisParser(in, parserState).replaceAllUnsuccessful(parserState, (fs: FeatureExpr) => alternativeParser(in, fs))
            }
        }
        /**
         * ^^ as in the original combinator parser framework
         */
        def ^^[U](f: T => U): MultiParser[U] = map(f)
        def map[U](f: T => U): MultiParser[U] = new MultiParser[U] {
            def apply(in: Input, feature: FeatureExpr): MultiParseResult[U] =
                thisParser(in, feature).map(f)
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
//    def opt[T](p: => ASTParser): MultiParser[Option[T]] =
//        p ^^ (x => Some(x)) | success(None)

//    /** as in the original combinator parser framework
//     */
//    def rep[T](p: => MultiParser[T]): MultiParser[List[T]] = rep1(p) | success(List())
//
//    //    /** as in the original combinator parser framework
//    //     */
//    //    def repsep[T](p: => MultiParser[T], q: => MultiParser[Any]): MultiParser[List[T]] =
//    //        rep1sep(p, q) | success(List())
//
//    /** as in the original combinator parser framework
//     */
//    def rep1[T](p: => MultiParser[T]): MultiParser[List[T]] = rep1(p, p)
//
//    /** 
//     * parsing list entries:
//     *
//     * parse an entry. if multiple results are returned, it is sufficient when one succeeds.
//     * 
//     * if multiple succeed, but do not finish at the same position, use the shortest result 
//     * (with the largest rest) and restart parsing 
//     */
//    def rep1[T](first: => MultiParser[T], p: => MultiParser[T]): MultiParser[List[T]] = new MultiParser[List[T]] {
//        def apply(in: Input, parserState: ParserState): MultiParseResult[T] = {
//            val elems = new ListBuffer[MultiParseResult[T]]
//
//            def continue(in: Input): OptListParseResult[T] = {
//                val p0 = p // avoid repeatedly re-evaluating by-name parser
//                @tailrec
//                def applyp(in0: Input): OptListParseResult[T] = p0(in0, parserState) match {
//                    case Success(x, rest) => elems += x; applyp(rest)
//                    case _ => new OptListParseResult(elems.toList, in0)
//                }
//
//                applyp(in)
//            }
//
//            val parseResult = first(in, parserState)
//            if (parseResult.allFailed)
//                parseResult
//            else
//                {
//                    elems += parseResult;
//                    new OptListParseResult(elems.toList)
//                }
//
//            
//            
//            //parseFirst
//            //if all failed, return error
//            //if some failed, return error
//            
//                 
//        }
//    }

    def success[T](v: T) =
        MultiParser { (in: Input, fs: FeatureExpr) => Success(v, in) }
    def MultiParser[T](f: (Input, FeatureExpr) => MultiParseResult[T]): MultiParser[T] =
        new MultiParser[T] { def apply(in: Input, fs: FeatureExpr) = f(in, fs) }

    def matchInput(p: Token => Boolean, err: Token => String) = new MultiParser[Token] {
        def apply(in: Input, context: FeatureExpr): MultiParseResult[Token] = {
            //only attempt to parse if feature is supported
            val start = skipHidden(in, context)

            //should not find an unreachable token, it would have been skipped
            assert(!context.implies(start.first.f).isDead)

            if (context.implies(start.first.f).isBase) {
                //token always parsed in this context
                if (p(start.first))
                    Success(start.first, skipHidden(start.rest, context))
                else
                    NoSuccess(err(start.first), start)
            } else
                //token sometimes parsed in this context -> plit parser
                splitParser(start, context)
        }
        def splitParser(in: Input, context: FeatureExpr): MultiParseResult[Token] = {
            val feature = in.first.f
            SplittedParseResult(in.first.f, this(in, context.and(feature)), this(in, context.and(feature.not)))
        }
    }

    def isSupported(token: Token, context: FeatureExpr) =
        context.implies(token.f).isBase
    def skipHidden(in: Input, context: FeatureExpr): Input = {
        var result = in
        while (context.and(result.first.f).isDead)
            result = result.rest
        result
    }

    def token(kind: String, p: Token => Boolean) = matchInput(p, inEl => kind + " expected")
    def textToken(kind: String) = token(kind, (_.text == kind))

}
case class ~[+a, +b](_1: a, _2: b) {
    override def toString = "(" + _1 + "~" + _2 + ")"
}
