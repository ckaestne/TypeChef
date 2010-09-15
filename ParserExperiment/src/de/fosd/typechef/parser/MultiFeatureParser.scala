package de.fosd.typechef.parser
import scala.annotation.tailrec

import scala.util.parsing.input.Reader
import scala.collection.mutable.ListBuffer
import de.fosd.typechef.featureexpr.FeatureExpr

abstract class AST
case class Alt(feature: FeatureExpr, thenBranch: AST, elseBranch: AST) extends Expr
case class OptAST(feature: FeatureExpr, optBranch: AST) extends Expr
object Alt {
    def join = (f: FeatureExpr, x: AST, y: AST) => if (x == y) x else Alt(f, x, y)
}

/**
 * adopted parser combinator framework with support for multi-feature parsing
 * 
 * @author kaestner
 */
trait MultiFeatureParser {
    type Elem <: AbstractToken
    type Input = TokenReader[Elem]
    type ParserState = FeatureExpr

    //parser  
    abstract class MultiParser[T] extends ((Input, ParserState) => MultiParseResult[T,Elem]) { thisParser =>
        private var name: String = ""
        def named(n: String): this.type = { name = n; this }
        override def toString() = "Parser (" + name + ")"

        /**
         * sequencing is difficult when each element can have multiple results for different features
         * tries to join split parsers as early as possible
         */
        def ~[U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = new MultiParser[~[T, U]] {
            def apply(in: Input, parserState: ParserState): MultiParseResult[~[T, U],Elem] =
                thisParser(in, parserState).seqAllSuccessful(parserState, (fs: FeatureExpr, x: Success[T,Elem]) => x.seq(fs, thatParser(x.next, fs)))
        }.named("~")

        /**
         * alternatives in the presence of multi-parsing
         * (no attempt to join yet)
         */
        def |[U >: T](alternativeParser: => MultiParser[U]): MultiParser[U] = new MultiParser[U] {
            def apply(in: Input, parserState: ParserState): MultiParseResult[U,Elem] = {
                thisParser(in, parserState).replaceAllUnsuccessful(parserState, (fs: FeatureExpr) => alternativeParser(in, fs))
            }
        }.named("|")
        /**
         * ^^ as in the original combinator parser framework
         */
        def ^^[U](f: T => U): MultiParser[U] = map(f).named("^^")
        def map[U](f: T => U): MultiParser[U] = new MultiParser[U] {
            def apply(in: Input, feature: FeatureExpr): MultiParseResult[U,Elem] =
                thisParser(in, feature).map(f)
        }

        /**
         * map and join ASTs (when possible)
         */
        def ^^!(f: T => AST): MultiParser[AST] =
            new MultiParser[AST] {
                def apply(in: Input, feature: FeatureExpr): MultiParseResult[AST,Elem] = {
                    thisParser.map(f)(in, feature).join[AST](Alt.join)
                }
            }.named("^^!")
    }

    /**
     * opt (and helper functions) as in the original combinator parser framework
     */
    def opt[T](p: => MultiParser[T]): MultiParser[Option[T]] =
        p ^^ (x => Some(x)) | success(None)

    /** 
     * repeated application (0..n times)
     *
     * this deserves special attention, because standard expansion would result in constructs as follow
     * a,b_1,c => Alt(1,List(a,b,c),List(a,c))
     * However, we do not want to replicate the entire list in some cases (especially for high-level AST constructs
     * such as functions and statements). Instead we want a single list with optional entries
     * a,b_1,c => List(a,Opt(1,b),c)
     *  
     */
    def rep(p: => MultiParser[AST]): MultiParser[List[Opt[AST]]] = new MultiParser[List[Opt[AST]]] {
        def apply(in: Input, parserState: ParserState): MultiParseResult[List[Opt[AST]],Elem] = {
            val elems = new ListBuffer[Opt[AST]]

            class ListHandlingException(msg: String) extends Exception(msg)
            def findOpt(in0: Input, result: MultiParseResult[AST,Elem]): (Opt[AST], Input) = {
                result match {
                    case Success(e, in) => (Opt(parserState, e), in)
                    case SplittedParseResult(f, Success(e, in), NoSuccess(_, _, _, _)) => (Opt(f, e), in)
                    //first element must finish before second element starts
                    case SplittedParseResult(f, Success(e, in), _) =>
                        if (in.offst <= in0.skipHidden(parserState.and(f.not)).offst)
                            (Opt(f, e), in)
                        else throw new ListHandlingException("interleaved features in list currently not supported, TODO")
                    //others currently not supported
                    case _ => throw new ListHandlingException("deeper nesting currently not supported, TODO")
                }
            }

            def continue(in: Input): MultiParseResult[List[Opt[AST]],Elem] = {
                val p0 = p // avoid repeatedly re-evaluating by-name parser
                @tailrec
                def applyp(in0: Input): MultiParseResult[List[Opt[AST]],Elem] = {
                    val parseResult = p0(in0, parserState).join(Alt.join)
                    //if all failed, return error
                    if (parseResult.allFailed)
                        Success(elems.toList, in0)
                    //when there are multiple results, create Opt-entry for shortest one(s), if there is no overlapping
                    else { //continue parsing
                        val (e: Opt[AST], rest: Input) = findOpt(in0, parseResult)
                        elems += e
                        applyp(rest)
                    }
                }

                applyp(in)
            }

            try {
                continue(in)
            } catch {
                case e: ListHandlingException => e.printStackTrace; rep2(p)(in, parserState)
            }
        }
    }

    /**
     * fallback repetition without considering optional elements. all Opt entries have status parserstate
     * @param p
     * @return
     */
    def rep2(p: => MultiParser[AST]): MultiParser[List[Opt[AST]]] =
        opt(p ~ rep2(p)) ^^ {
            case Some(~(x, list: List[Opt[AST]])) => List(Opt(FeatureExpr.base, x)) ++ list
            case None => List()
        }

    def success[T](v: T) =
        MultiParser { (in: Input, fs: FeatureExpr) => Success(v, in) }
    def MultiParser[T](f: (Input, FeatureExpr) => MultiParseResult[T,Elem]): MultiParser[T] =
        new MultiParser[T] { def apply(in: Input, fs: FeatureExpr) = f(in, fs) }

    def matchInput(p: Elem => Boolean, err: Elem => String) = new MultiParser[Elem] {
        def apply(in: Input, context: FeatureExpr): MultiParseResult[Elem,Elem] = {
            //only attempt to parse if feature is supported
            val start = in.skipHidden(context)

            //should not find an unreachable token, it would have been skipped
            assert(!context.implies(start.first.getFeature).isDead)

            if (context.implies(start.first.getFeature).isBase) {
                //token always parsed in this context
                if (p(start.first))
                    Success(start.first, start.rest) //.skipHidden(context))//TODO rather when joining?
                else
                    NoSuccess(err(start.first), context, start, List())
            } else
                //token sometimes parsed in this context -> plit parser
                splitParser(start, context)
        }
        def splitParser(in: Input, context: FeatureExpr): MultiParseResult[Elem,Elem] = {
            val feature = in.first.getFeature
            SplittedParseResult(in.first.getFeature, this(in, context.and(feature)), this(in, context.and(feature.not)))
        }
    }.named("matchInput")

    def isSupported(token: Elem, context: FeatureExpr) =
        context.implies(token.getFeature).isBase

    def token(kind: String, p: Elem => Boolean) = matchInput(p, inEl => "\"" + kind + "\" expected")

}
case class ~[+a, +b](_1: a, _2: b) {
    override def toString = "(" + _1 + "~" + _2 + ")"
}
case class Opt[T](val feature: FeatureExpr, val entry: T)
