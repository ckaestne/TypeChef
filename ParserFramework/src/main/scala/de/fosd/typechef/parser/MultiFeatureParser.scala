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
    type TypeContext
    type Input = TokenReader[Elem, TypeContext]
    type ParserState = FeatureExpr

    //parser  
    abstract class MultiParser[+T] extends ((Input, ParserState) => MultiParseResult[T, Elem, TypeContext]) { thisParser =>
        private var name: String = ""
        def named(n: String): this.type = { name = n; this }
        override def toString() = "Parser (" + name + ")"

        /**
         * sequencing is difficult when each element can have multiple results for different features
         * tries to join split parsers as early as possible
         */
        def ~[U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = new MultiParser[~[T, U]] {
            def apply(in: Input, parserState: ParserState): MultiParseResult[~[T, U], Elem, TypeContext] =
                thisParser(in, parserState).seqAllSuccessful(parserState, (fs: FeatureExpr, x: Success[T, Elem, TypeContext]) => x.seq(fs, thatParser(x.next, fs)))
        }.named("~")

        /**
         * non-backtracking sequencing (replace failures by errors)
         */
        def ~![U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = new MultiParser[~[T, U]] {
            def apply(in: Input, parserState: ParserState): MultiParseResult[~[T, U], Elem, TypeContext] =
                thisParser(in, parserState).seqAllSuccessful(parserState, (fs: FeatureExpr, x: Success[T, Elem, TypeContext]) => x.seq(fs, thatParser(x.next, fs)).commit)
        }.named("~!")

        /**
         * alternatives in the presence of multi-parsing
         * (no attempt to join yet)
         */
        def |[U >: T](alternativeParser: => MultiParser[U]): MultiParser[U] = new MultiParser[U] {
            def apply(in: Input, parserState: ParserState): MultiParseResult[U, Elem, TypeContext] = {
                thisParser(in, parserState).replaceAllFailure(parserState, (fs: FeatureExpr) => alternativeParser(in, fs))
            }
        }.named("|")

        /**
         * ^^ as in the original combinator parser framework
         */
        def ^^[U](f: T => U): MultiParser[U] = map(f).named("^^")
        def map[U](f: T => U): MultiParser[U] = new MultiParser[U] {
            def apply(in: Input, feature: FeatureExpr): MultiParseResult[U, Elem, TypeContext] =
                thisParser(in, feature).map(f)
        }

        /**
         * map and join ASTs (when possible)
         */
        def ^^![U](joinFunction: (FeatureExpr, U, U) => U, f: T => U): MultiParser[U] =
            this.map(f).join(joinFunction)

        /**
         * join parse results when possible
         */
        def ![U >: T](joinFunction: (FeatureExpr, U, U) => U): MultiParser[U] = join(joinFunction).named("!")
        def join[U >: T](joinFunction: (FeatureExpr, U, U) => U): MultiParser[U] =
            new MultiParser[U] {
                def apply(in: Input, feature: FeatureExpr): MultiParseResult[U, Elem, TypeContext] = {
                    thisParser(in, feature).join[U](feature, joinFunction)
                }
            }

        def changeContext(contextModification: (T, TypeContext) => TypeContext): MultiParser[T] =
            new MultiParser[T] {
                def apply(in: Input, feature: FeatureExpr): MultiParseResult[T, Elem, TypeContext] =
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
     * Note: it is not allowed to sequence two repOpt calls (such as repOpt(a)~repOpt(b)), because it would not be
     * able to parse correctly interleaved entries of both lists. In this case use rep instead for the first sequence. XXX 
     *
     *  @param productionName provides a readable name for debugging purposes
     */
    def repOpt[T](p: => MultiParser[T], joinFunction: (FeatureExpr, T, T) => T, productionName: String): MultiParser[List[Opt[T]]] = new MultiParser[List[Opt[T]]] {
        def apply(in: Input, parserState: ParserState): MultiParseResult[List[Opt[T]], Elem, TypeContext] = {
            val elems = new ListBuffer[Opt[T]]

            class ListHandlingException(msg: String) extends Exception(msg)

            def findOpt(in0: Input, result: MultiParseResult[T, Elem, TypeContext]): (Opt[T], Input) = {
                val (feature, singleResult) = selectFirstMostResult(in0, parserState, result)
                assert(singleResult.isInstanceOf[Success[_, _, _]])
                (Opt(feature, singleResult.asInstanceOf[Success[T, Elem, TypeContext]].result), singleResult.next)
            }
            /**
             * @param in0: token stream position before attempting to parse this sequence
             * 
             * returns a single parse result with the corresponding feature 
             */
            def selectFirstMostResult(in0: Input, context: FeatureExpr, result: MultiParseResult[T, Elem, TypeContext]): (FeatureExpr, ParseResult[T, Elem, TypeContext]) =
                result match {
                    case SplittedParseResult(f, a, b) => {
                        //recursive call first (resolve all inner splits)
                        val (featureA, resultA) = selectFirstMostResult(in0, context and f, a)
                        val (featureB, resultB) = selectFirstMostResult(in0, context and (f.not), b)

                        (resultA, resultB) match {
                            case (s@Success(eA, inA), _) =>
                                if (inA.offst <= in0.skipHidden(featureB).offst) {
                                    DebugSplitting("joinl at \"" + inA.first.getText + "\" at " + inA.first.getPosition + " from " + f)
                                    (featureA, s)
                                } else throw new ListHandlingException("interleaved features in list currently not supported at " + inA.pos + ", using fallback strategy")
                            case (_, s@Success(eB, inB)) =>
                                if (inB.offst <= in0.skipHidden(featureA).offst) {
                                    DebugSplitting("joinr at \"" + inB.first.getText + "\" at " + inB.first.getPosition + " from " + f)
                                    (featureB, s)
                                } else throw new ListHandlingException("interleaved features in list currently not supported at " + inB + ", using fallback strategy")
                            case _ => throw new ListHandlingException("... should not occur ...")
                        }
                    }
                    case s@Success(_, _) => (context, s)
                    case s@NoSuccess(_, _, _, _) => (context, s)
                }

            def continue(in: Input): MultiParseResult[List[Opt[T]], Elem, TypeContext] = {
                val p0 = p // avoid repeatedly re-evaluating by-name parser

                /**
                 * main repopt loop
                 */
                def applyp(_in: Input): MultiParseResult[List[Opt[T]], Elem, TypeContext] = {
                    var in0 = _in;
                    while (true) {
                        var skip = false
                        /**
                         * strategy1: parse the next statement with the annotation of the next token.
                         *  if it yields a unique result before the next token that would be parsed 
                         *  with the alternative (split) parser, then this is the only result we need 
                         *  to care about
                         * 
                         * will work in the common case that the entire entry is annotated and 
                         *  is not interleaved with other annotations
                         */

                        if (productionName == "externalDef")
                            println("next externalDef @ " + in0.first.getPosition)

                        val firstFeature = in0.first.getFeature
                        if (!FeatureSolverCache.implies(parserState, firstFeature) && !FeatureSolverCache.mutuallyExclusive(parserState, firstFeature)) {
                            val parseResult = (p0.join(joinFunction))(in0, parserState.and(firstFeature))
                            parseResult match {
                                case Success(result, next) =>
                                    if (next.offset <= in0.skipHidden(parserState.and(firstFeature.not)).offst) {
                                        elems += Opt(parserState.and(firstFeature), result)
                                        //                                        println(productionName + " " + next.first.getPosition)
                                        in0 = next
                                        skip = true;
                                    }
                                case e@Error(_, _, next, _) =>
                                    if (next.offset <= in0.skipHidden(parserState.and(firstFeature.not)).offst)
                                        return e
                                case Failure(_, _, next, _) =>
                                    if (next.offset <= in0.skipHidden(parserState.and(firstFeature.not)).offst)
                                        return Success(elems.toList, in0)
                                case _ =>
                            }
                        }

                        /** strategy 2: parse normally and merge alternative results */
                        if (!skip) {
                            val parseResult = (p0.!(joinFunction))(in0, parserState)
                            //if there are errors (not failures) abort
                            val errors = parseResult.toErrorList
                            if (!errors.isEmpty)
                                if (errors.size == 1)
                                    return errors.iterator.next
                                else
                                    return Error("error in loop (see inner errors)", parserState, in0, errors)
                            //if all failed, return results so far
                            else if (parseResult.allFailed) {
                                DebugSplitting("abort at \"" + in0.first.getText + "\" at " + in0.first.getPosition)
                                return Success(elems.toList, in0)
                            } else {
                                //when there are multiple results, create Opt-entry for shortest one(s), if there is no overlapping
                                val (e, rest) = findOpt(in0, parseResult)
                                //                                if (productionName=="externalDef")
                                //println(productionName + " " + rest.first.getPosition)
                                elems += e
                                //continue parsing
                                in0 = rest
                            }
                        }
                    }
                    //never happens:
                    throw new Exception("never happens")
                }

                applyp(in)
            }

            try {
                continue(in)
            } catch {
                /** fallback (try to avoid for long lists!): 
                 * normal repetition, where each is wrapped in an Opt(base,_) */
                case e: ListHandlingException => {
                    e.printStackTrace
                    rep(p)(in, parserState).map(_.map(Opt(FeatureExpr.base, _)))
                }
            }
        }
    }.named("repOpt-" + productionName)

    /*def rep[T](p: => MultiParser[T]): MultiParser[List[T]] =
        opt(p ~ rep(p)) ^^ {
            case Some(~(x, list: List[_])) => List(x) ++ list
            case None => List()
        }*/

    /*
        rep(p) = opt(p ~ rep(p)) ^^ {
            case Some(~(x, list: List[_])) => List(x) ++ list
            case None => List()
        } ==
        fix $ \rep -> ((p ~ rep(p)) ^^ (x => Some(x)) | success(None)) ^^ {
            case Some(~(x, list: List[_])) => List(x) ++ list
            case None => List()
        } ==
        fix $ \rep -> ((p ~ rep(p)) ^^ (x => Some(x)) | success(None)) ^^ {
            case Some(~(x, list: List[_])) => List(x) ++ list
            case None => List()
        } ==
        fix $ \rep -> (new MultiParser[U] {
                def mainParser = (new MultiParser[~[T, U]] {
                        def apply(in: Input, parserState: ParserState): MultiParseResult[~[T, U], Elem, TypeContext] =
                                p(in, parserState).seqAllSuccessful(parserState, (fs: FeatureExpr, x: Success[T, Elem, TypeContext]) => x.seq(fs, rep(p)(x.next, fs)))
                }) ^^ (x => Some(x))
                def apply(in: Input, parserState: ParserState): MultiParseResult[U, Elem, TypeContext] = {
                        mainParser(in, parserState).replaceAllFailure(parserState, (fs: FeatureExpr) => success(None)(in, fs))
                }
        })
        Alternatively:
        opt(p) ~ opt(p)*
     */

    /**
     * normal repetition, 0..n times (x)*
     * 
     * may return alternative lists. a list is sealed if parser p cannot
     * parse an additional entry. parsing continues on unsealed lists
     * until all lists are sealed 
     * 
     * @param p
     * @return
     */
    def rep[T](p: => MultiParser[T]): MultiParser[List[T]] = new MultiParser[List[T]] {

        private case class Sealable[T](val isSealed: Boolean, val list: List[T])
        def seal(list: List[T]) = Sealable(true, list)
        def unsealed(list: List[T]) = Sealable(false, list)
        def anyUnsealed(parseResult: MultiParseResult[Sealable[T], Elem, TypeContext]) =
            parseResult.exists(!_.isSealed)

        def apply(in: Input, parserState: ParserState): MultiParseResult[List[T], Elem, TypeContext] = {
            val p_ = opt(p) ^^ {
                case Some(x) =>
                    unsealed(List(x))
                case None =>
                    seal(Nil)
            }
            var res: MultiParseResult[Sealable[T], Elem, TypeContext] = p_(in, parserState)
            while (anyUnsealed(res)) {
                res = res.seqAllSuccessful(parserState,
                    (fs, x) =>
                        if (x.result.isSealed)
                            x // do not do anything on sealed lists
                        else
                            // extend unsealed lists with the next result (if there is no next result, seal the list)                        	
                            x.seq(fs, opt(p)(x.next, fs)).map({
                                case slist ~ Some(x) =>
                                    unsealed(slist.list :+ x)
                                case slist ~ None =>
                                    seal(slist.list)
                            }))
            }
            //return all sealed lists
            res.map(_.list)
        }
    }

    /**
     * straightforward implementation but computationally expensive in a stack-based language, 
     * therefore use iterative implementation rep instead
     * @param p
     * @return
     */
    def repRecursive[T](p: => MultiParser[T]): MultiParser[List[T]] =
        opt(p ~ rep(p)) ^^ {
            case Some(~(x, list: List[_])) => List(x) ++ list
            case None => List()
        }

    /*
    def ~[U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = new MultiParser[~[T, U]] {
            def apply(in: Input, parserState: ParserState): MultiParseResult[~[T, U], Elem, TypeContext] =
                    thisParser(in, parserState).seqAllSuccessful(parserState, (fs: FeatureExpr, x: Success[T, Elem, TypeContext]) => x.seq(fs, thatParser(x.next, fs)))
    }.named("~")


    def opt[T](p: => MultiParser[T]): MultiParser[Option[T]] =
        p ^^ (x => Some(x)) | success(None)
    def |[U >: T](alternativeParser: => MultiParser[U]): MultiParser[U] = new MultiParser[U] {
            def apply(in: Input, parserState: ParserState): MultiParseResult[U, Elem, TypeContext] = {
                            thisParser(in, parserState).replaceAllFailure(parserState, (fs: FeatureExpr) => alternativeParser(in, fs))
            }
    }.named("|")*/

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
     * returns failure when list is empty
     */
    def nonEmpty[T](p: => MultiParser[List[T]]): MultiParser[List[T]] = new MultiParser[List[T]] {
        def apply(in: Input, feature: FeatureExpr): MultiParseResult[List[T], Elem, TypeContext] = {
            p(in, feature).seqAllSuccessful(feature,
                (fs: FeatureExpr, x: Success[List[T], Elem, TypeContext]) =>
                    if (x.result.isEmpty)
                        Failure("empty list", fs, x.nextInput, List())
                    else
                        x)
        }
    }.named("nonEmpty")

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
        def apply(in: Input, parserState: ParserState): MultiParseResult[Any, Elem, TypeContext] = {
            p(in, parserState).seqAllSuccessful(parserState,
                (fs: FeatureExpr, x: Success[T, Elem, TypeContext]) => Success("lookahead", in))
        }
    }

    def fail[T](msg: String): MultiParser[T] =
        new MultiParser[T] { def apply(in: Input, fs: FeatureExpr) = Failure(msg, fs, in, List()) }

    def success[T](v: T) =
        MultiParser { (in: Input, fs: FeatureExpr) => Success(v, in) }
    def MultiParser[T](f: (Input, FeatureExpr) => MultiParseResult[T, Elem, TypeContext]): MultiParser[T] =
        new MultiParser[T] { def apply(in: Input, fs: FeatureExpr) = f(in, fs) }

    def matchInput(p: (Elem, TypeContext) => Boolean, err: Option[Elem] => String) = new MultiParser[Elem] {
        @tailrec
        def apply(in: Input, context: FeatureExpr): MultiParseResult[Elem, Elem, TypeContext] = {
            if (in.atEnd)
                Failure(err(None), context, in, List())
            else {
                val tokenPresenceCondition = in.first.getFeature
                if (FeatureSolverCache.implies(context, tokenPresenceCondition))
                    //always parsed in this context
                    returnFirstToken(in, context)
                else if (FeatureSolverCache.mutuallyExclusive(context, tokenPresenceCondition))
                    //never parsed in this context
                    apply(in.rest.skipHidden(context), context)
                else {
                    //token sometimes parsed in this context -> plit parser
                    in.first.countSplit
                    splitParser(in, context)
                }
            }
        }
        private def splitParser(in: Input, context: FeatureExpr): MultiParseResult[Elem, Elem, TypeContext] = {
            val feature = in.first.getFeature
            val ctxAndFeat = context.and(feature)
            assert(FeatureSolverCache.implies(ctxAndFeat, feature))
            val r1 = apply(in, ctxAndFeat)

            val ctxAndNotFeat = context.and(feature.not)
            assert(FeatureSolverCache.mutuallyExclusive(ctxAndNotFeat, feature))
            val r2 = apply(in, ctxAndNotFeat)

            (r1, r2) match {
                case (f1@Failure(msg1, context1, next1, inner1), f2@Failure(msg2, context2, next2, inner2)) =>
                    Failure(msg1, context1, next1, List(f1, f2) ++ inner1 ++ inner2)
                case (f1@Error(msg1, context1, next1, inner1), f2@Error(msg2, context2, next2, inner2)) =>
                    Error(msg1, context1, next1, List(f1, f2) ++ inner1 ++ inner2)
                case (a, b) => {
                    DebugSplitting("split at \"" + in.first.getText + "\" at " + in.first.getPosition + " from " + context + " with " + feature)
                    SplittedParseResult(feature, r1, r2)
                }
            }
        }
        private def returnFirstToken(in: Input, context: FeatureExpr) = {
            if (p(in.first, in.context)) {
                in.first.countSuccess
                Success(in.first, in.rest)
            } else {
                in.first.countFailure
                Failure(err(Some(in.first)), context, in, List())
            }
        }

    }.named("matchInput")

    def token(kind: String, p: Elem => Boolean) = tokenWithContext(kind, (e, c) => p(e))
    def tokenWithContext(kind: String, p: (Elem, TypeContext) => Boolean) = matchInput(p, errorMsg(kind, _))
    private def errorMsg(kind: String, inEl: Option[Elem]) =
        (if (!inEl.isDefined) "reached EOF, " else "found \"" + inEl.get.getText + "\", ") + "but expected \"" + kind + "\""

}
case class ~[+a, +b](_1: a, _2: b) {
    override def toString = "(" + _1 + "~" + _2 + ")"
}
case class Opt[+T](val feature: FeatureExpr, val entry: T)
