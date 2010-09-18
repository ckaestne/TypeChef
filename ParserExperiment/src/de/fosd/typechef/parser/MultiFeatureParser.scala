package de.fosd.typechef.parser

import scala.annotation.tailrec

import scala.util.parsing.input.Reader
import scala.collection.mutable.ListBuffer
import de.fosd.typechef.featureexpr.FeatureExpr

/**
 * adopted parser combinator framework with support for multi-feature parsing
 * 
 * @author kaestner
 */
class MultiFeatureParser {
    type Elem <: AbstractToken
    type Context
    type Input = TokenReader[Elem, Context]
    type ParserState = FeatureExpr

    //parser  
    abstract class MultiParser[+T] extends ((Input, ParserState) => MultiParseResult[T, Elem, Context]) { thisParser =>
        private var name: String = ""
        def named(n: String): this.type = { name = n; this }
        override def toString() = "Parser (" + name + ")"

        /**
         * sequencing is difficult when each element can have multiple results for different features
         * tries to join split parsers as early as possible
         */
        def ~[U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = new MultiParser[~[T, U]] {
            def apply(in: Input, parserState: ParserState): MultiParseResult[~[T, U], Elem, Context] =
                thisParser(in, parserState).seqAllSuccessful(parserState, (fs: FeatureExpr, x: Success[T, Elem, Context]) => x.seq(fs, thatParser(x.next, fs)))
        }.named("~")

        /**
         * non-backtracking sequencing (replace failures by errors)
         */
        def ~![U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = new MultiParser[~[T, U]] {
            def apply(in: Input, parserState: ParserState): MultiParseResult[~[T, U], Elem, Context] =
                thisParser(in, parserState).seqAllSuccessful(parserState, (fs: FeatureExpr, x: Success[T, Elem, Context]) => x.seq(fs, thatParser(x.next, fs)).commit)
        }.named("~!")

        /**
         * alternatives in the presence of multi-parsing
         * (no attempt to join yet)
         */
        def |[U >: T](alternativeParser: => MultiParser[U]): MultiParser[U] = new MultiParser[U] {
            def apply(in: Input, parserState: ParserState): MultiParseResult[U, Elem, Context] = {
                thisParser(in, parserState).replaceAllFailure(parserState, (fs: FeatureExpr) => alternativeParser(in, fs))
            }
        }.named("|")

        /**
         * ^^ as in the original combinator parser framework
         */
        def ^^[U](f: T => U): MultiParser[U] = map(f).named("^^")
        def map[U](f: T => U): MultiParser[U] = new MultiParser[U] {
            def apply(in: Input, feature: FeatureExpr): MultiParseResult[U, Elem, Context] =
                thisParser(in, feature).map(f)
        }

        /**
         * map and join ASTs (when possible)
         */
        def ^^![U](joinFunction: (FeatureExpr, U, U) => U, f: T => U): MultiParser[U] =
            new MultiParser[U] {
                def apply(in: Input, feature: FeatureExpr): MultiParseResult[U, Elem, Context] = {
                    thisParser.map(f)(in, feature).join[U](joinFunction)
                }
            }.named("^^!")

        def changeContext(contextModification: (T, Context) => Context): MultiParser[T] =
            new MultiParser[T] {
                def apply(in: Input, feature: FeatureExpr): MultiParseResult[T, Elem, Context] =
                    thisParser(in, feature).changeContext(contextModification)
            }.named("__context")

        /**
         * from original framework, sequence parsers, but drop first result
         */
        def ~>[U](thatParser: => MultiParser[U]): MultiParser[U] = { thisParser ~ thatParser ^^ { (x: T ~ U) => x match { case ~(a, b) => b } } }.named("~>")

        /**
         * from original framework, sequence parsers, but drop last result
         */
        def <~[U](thatParser: => MultiParser[U]): MultiParser[T] = { thisParser ~ thatParser ^^ { (x: T ~ U) => x match { case ~(a, b) => a } } }.named("<~")
        /** Returns a parser that repeatedly parses what this parser parses
         *
         * @return rep(this) 
         */
        def * = rep(this)

        /** Returns a parser that repeatedly parses what this parser parses, interleaved with the `sep' parser.
         * The `sep' parser specifies how the results parsed by this parser should be combined.
         *
         * @return chainl1(this, sep) 
         */
        def *[U >: T](sep: => MultiParser[(U, U) => U]) = repSep(this, sep)

        // TODO: improve precedence? a ~ b*(",") = a ~ (b*(","))  should be true 

        /** Returns a parser that repeatedly (at least once) parses what this parser parses.
         *
         * @return rep1(this) 
         */
        def + = rep1(this)

        /** Returns a parser that optionally parses what this parser parses.
         *
         * @return opt(this) 
         */
        def ? = opt(this)

    }

    /**
     * opt (and helper functions) as in the original combinator parser framework
     * (x)?
     */
    def opt[T](p: => MultiParser[T]): MultiParser[Option[T]] =
        p ^^ (x => Some(x)) | success(None)

    /** 
     * repeated application (0..n times), or (x)*
     *
     * this deserves special attention, because standard expansion would result in constructs as follow
     * a,b_1,c => Alt(1,List(a,b,c),List(a,c))
     * However, we do not want to replicate the entire list in some cases (especially for high-level AST constructs
     * such as functions and statements). Instead we want a single list with optional entries
     * a,b_1,c => List(a,Opt(1,b),c)
     *  
     */
    def repOpt[T](p: => MultiParser[T], joinFunction: (FeatureExpr, T, T) => T): MultiParser[List[Opt[T]]] = new MultiParser[List[Opt[T]]] {
        def apply(in: Input, parserState: ParserState): MultiParseResult[List[Opt[T]], Elem, Context] = {
            val elems = new ListBuffer[Opt[T]]

            class ListHandlingException(msg: String) extends Exception(msg)
            def findOpt(in0: Input, result: MultiParseResult[T, Elem, Context]): (Opt[T], Input) = {
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

            def continue(in: Input): MultiParseResult[List[Opt[T]], Elem, Context] = {
                val p0 = p // avoid repeatedly re-evaluating by-name parser
                @tailrec
                def applyp(in0: Input): MultiParseResult[List[Opt[T]], Elem, Context] = {
                    val parseResult = p0(in0, parserState).join(joinFunction)
                    //if there are errors (not failures) abort
                    val errors=parseResult.toErrorList
                    if (!errors.isEmpty)
                    	if (errors.size==1)
                    		errors.iterator.next
                    	else
                    		Error("error in loop (see inner errors)",parserState,in0,errors)
                    //if all failed, return results so far
                    else if (parseResult.allFailed)
                        Success(elems.toList, in0)
                    //when there are multiple results, create Opt-entry for shortest one(s), if there is no overlapping
                    else { //continue parsing
                        val (e: Opt[_], rest: Input) = findOpt(in0, parseResult)
                        elems += e
                        applyp(rest)
                    }
                }

                applyp(in)
            }

            try {
                continue(in)
            } catch {
                //fallback: normal repetition, where each is wrapped in an Opt(base,_)
                case e: ListHandlingException => e.printStackTrace; rep[T](p)(in, parserState).map(_.map(Opt(FeatureExpr.base, _)))
            }
        }
    }

    /**
     * normal repetition, 0..n times (x)*
     * @param p
     * @return
     */
    def rep[T](p: => MultiParser[T]): MultiParser[List[T]] =
        opt(p ~ rep(p)) ^^ {
            case Some(~(x, list: List[_])) => List(x) ++ list
            case None => List()
        }
    /**
     * repeated parsing, at least once (result may not be the empty list)
     * (x)+
     */
    def rep1[T](p: => MultiParser[T]): MultiParser[List[T]] =
        p ~ opt(rep1(p)) ^^ {
            case ~(x, Some(list: List[_])) => List(x) ++ list
            case ~(x, None) => List(x)
        }

    /**
     * repetitions 1..n with separator
     * 
     * for the pattern
     * p ~ (separator ~ p)*
     */
    def rep1Sep[T, U](p: => MultiParser[T], separator: => MultiParser[U]): MultiParser[List[T]] =
        p ~ rep(separator ~> p) ^^ { case r ~ l => List(r) ++ l }
    /**
     * repetitions 0..n with separator
     * 
     * for the pattern
     * [p ~ (separator ~ p)*]
     */
    def repSep[T, U](p: => MultiParser[T], separator: => MultiParser[U]): MultiParser[List[T]] =
        opt(rep1Sep(p, separator)) ^^ { case Some(l) => l; case None => List() }

    /**
     * replace optional list by (possibly empty) list
     */
    def optList[T](p: => MultiParser[List[T]]): MultiParser[List[T]] =
        opt(p) ^^ { case Some(l) => l; case None => List() }

    /**
     * represent optional element either bei singleton list or by empty list
     */
    def opt2List[T](p: => MultiParser[T]): MultiParser[List[T]] =
        opt(p) ^^ { _.toList }

    /**
     * parses using p, but only returns either success or no-success and(!) 
     * does not proceed the input stream (for successful entries)
     */
    def lookahead[T](p: => MultiParser[T]): MultiParser[Any] = new MultiParser[Any] {
        def apply(in: Input, parserState: ParserState): MultiParseResult[Any, Elem, Context] = {
            p(in, parserState).seqAllSuccessful(parserState,
                (fs: FeatureExpr, x: Success[T, Elem, Context]) => Success("lookahead", in))
        }
    }

    def fail[T](msg: String): MultiParser[T] =
        new MultiParser[T] { def apply(in: Input, fs: FeatureExpr) = Failure(msg, fs, in, List()) }

    def success[T](v: T) =
        MultiParser { (in: Input, fs: FeatureExpr) => Success(v, in) }
    def MultiParser[T](f: (Input, FeatureExpr) => MultiParseResult[T, Elem, Context]): MultiParser[T] =
        new MultiParser[T] { def apply(in: Input, fs: FeatureExpr) = f(in, fs) }

    def matchInput(p: (Elem, Context) => Boolean, err: Option[Elem] => String) = new MultiParser[Elem] {
        def apply(in: Input, context: FeatureExpr): MultiParseResult[Elem, Elem, Context] = {
            //only attempt to parse if feature is supported
            val start = in.skipHidden(context)

            if (start.atEnd)
                Failure(err(None), context, start, List())
            else {
                //should not find an unreachable token, it would have been skipped
                assert(!context.implies(start.first.getFeature).isDead)

                if (context.implies(start.first.getFeature).isBase) {
                    //token always parsed in this context
                    if (p(start.first, start.context))
                        Success(start.first, start.rest) //.skipHidden(context))//TODO rather when joining?
                    else
                        Failure(err(Some(start.first)), context, start, List())
                } else
                    //token sometimes parsed in this context -> plit parser
                    splitParser(start, context)
            }
        }
        def splitParser(in: Input, context: FeatureExpr): MultiParseResult[Elem, Elem, Context] = {
            val feature = in.first.getFeature
            SplittedParseResult(in.first.getFeature, this(in, context.and(feature)), this(in, context.and(feature.not)))
        }
    }.named("matchInput")

    def isSupported(token: Elem, context: FeatureExpr) =
        context.implies(token.getFeature).isBase

    def token(kind: String, p: Elem => Boolean) = tokenWithContext(kind, (e, c) => p(e))
    def tokenWithContext(kind: String, p: (Elem, Context) => Boolean) = matchInput(p, errorMsg(kind,_))
    private def errorMsg(kind:String, inEl:Option[Elem]) =
    	(if (!inEl.isDefined) "reached EOF, " else "found \""+inEl.get.getText+"\", ")+ "\"" + kind + "\" expected"
}
case class ~[+a, +b](_1: a, _2: b) {
    override def toString = "(" + _1 + "~" + _2 + ")"
}
case class Opt[T](val feature: FeatureExpr, val entry: T)
