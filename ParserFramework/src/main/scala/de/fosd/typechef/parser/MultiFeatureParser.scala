package de.fosd.typechef.parser
import de.fosd.typechef.featureexpr.FeatureExpr

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.math._

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
        def * = repOpt(this)

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

    /*   */
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
     */ /*
    def repOpt[T](p: => MultiParser[T], joinFunction: (FeatureExpr, T, T) => T, productionName: String): MultiParser[List[Opt[T]]] = new MultiParser[List[Opt[T]]] {
        def apply(in: Input, parserState: ParserState): MultiParseResult[List[Opt[T]], Elem, TypeContext] = {
            val elems = new ListBuffer[Opt[T]]

            class ListHandlingException(msg: String) extends Exception(msg)

            def findOpt(in0: Input, result: MultiParseResult[T, Elem, TypeContext]): (Opt[T], Input) = {
                val (feature, singleResult) = selectFirstMostResult(in0, parserState, result)
                assert(singleResult.isInstanceOf[Success[_, _, _]])
                (Opt(feature, singleResult.asInstanceOf[Success[T, Elem, TypeContext]].result), singleResult.next)
            }
            */
    /**
     * @param in0: token stream position before attempting to parse this sequence
     * 
     * returns a single parse result with the corresponding feature 
     */ /*
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

                */
    /**
     * main repopt loop
     */ /*
                def applyp(_in: Input): MultiParseResult[List[Opt[T]], Elem, TypeContext] = {
                    var in0 = _in;
                    while (true) {
                        var skip = false
                        */
    /**
     * strategy1: parse the next statement with the annotation of the next token.
     *  if it yields a unique result before the next token that would be parsed 
     *  with the alternative (split) parser, then this is the only result we need 
     *  to care about
     * 
     * will work in the common case that the entire entry is annotated and 
     *  is not interleaved with other annotations
     */ /*

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

                        */
    /** strategy 2: parse normally and merge alternative results */ /*
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
                */
    /** fallback (try to avoid for long lists!): 
     * normal repetition, where each is wrapped in an Opt(base,_) */ /*
                case e: ListHandlingException => {
                    e.printStackTrace
                    rep(p)(in, parserState).map(_.map(Opt(FeatureExpr.base, _)))
                }
            }
        }
    }.named("repOpt-" + productionName)*/

    /**
     * straightforward implementation but computationally expensive without tail-call optimization, 
     * therefore use iterative implementation repPlain instead
     * @param p
     * @return
     */
    def repRecursive[T](p: => MultiParser[T]): MultiParser[List[T]] =
        opt(p ~ repRecursive(p)) ^^ {
            case Some(~(x, list: List[_])) => List(x) ++ list
            case None => List()
        }

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
    def repPlain[T](p: => MultiParser[T]): MultiParser[List[T]] = new MultiParser[List[T]] {
        private case class Sealable[T](val isSealed: Boolean, val list: List[T])
        private def seal(list: List[T]) = Sealable(true, list)
        private def unsealed(list: List[T]) = Sealable(false, list)
        private def anyUnsealed(parseResult: MultiParseResult[Sealable[T], Elem, TypeContext]) =
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
                                    /* Appending to the tail would take linear time, and building a list that way
                                     * would take quadratic time; therefore, add x to the head and remember to reverse the list at the end. */
                                    unsealed(x +: slist.list)
                                case slist ~ None =>
                                    seal(slist.list)
                            }))
            }
            //return all sealed lists
            res.map(_.list.reverse)
        }
    }

    /**
     * second attempt to implement repOpt
     * 
     * it uses the following mechanism: it parses a single subexpression (p) at a time
     * if there are multiple results, it only resumes the result which has consumed fewest tokens 
     * so far. after each step it tries to join parser branches.
     * the intuition is that after splitting, we parse branches in a regular fashion to
     * increase the chances for joins.
     * 
     * drawback: currently the first expression after an optional expression is parsed twice:
     * 1_A, 2 3 will be parsed as [Opt(A, 1), Opt(!A, 2), Opt(A, 2), 3]
     * 
     * 
     * @param p
     * @return
     */
    def repOpt[T](p: => MultiParser[T], productionName: String = ""): MultiParser[List[Opt[T]]] = new MultiParser[List[Opt[T]]] {
        //sealable is only used to enforce correct propagation of token positions in joins (which might not be ensured with fails)
        private case class Sealable(val isSealed: Boolean, resultList: List[Opt[T]])
        //join anything, data does not matter, only position in tokenstream
        private def join(ctx: FeatureExpr, res: MultiParseResult[Sealable, Elem, TypeContext]) =
            res.join(ctx, (f, a: Sealable, b: Sealable) => Sealable(a.isSealed && b.isSealed, joinLists(a.resultList, b.resultList)))
        /** joins two optList with two special features:
         * 
         * common elements at the end of the list (eq) are joined (they originated from replication in ~)
         * 
         * if the first element of both lists is the same (==) they are joined as well. this originates 
         * from parsing elements after optional elements twice (e.g., 2 in "1_A 2")  
         * 
         * this is a heuristic to reduce the size of the produced AST. it does not affect correctness
         */
        private def joinLists(inA: List[Opt[T]], inB: List[Opt[T]]): List[Opt[T]] = {
            var a = inA; var b = inB
            var lastEntry: Opt[T] = null;
            if (!a.isEmpty && !b.isEmpty && a.head.entry == b.head.entry) { //== needed because the ASTs were constructed independently
                lastEntry = Opt(a.head.feature or b.head.feature, a.head.entry)
                a = a.tail; b = b.tail;
            }
            var ar = a.reverse
            var br = b.reverse
            var result: List[Opt[T]] = Nil
            while (!ar.isEmpty && !br.isEmpty && (ar.head.entry == /*eq*/ br.head.entry)) { //XXX should use eq instead of ==, because it really points to the same structure
                result = Opt(ar.head.feature or br.head.feature, ar.head.entry) :: result
                ar = ar.tail; br = br.tail
            }
            while (!ar.isEmpty) {
                result = ar.head :: result
                ar = ar.tail
            }
            while (!br.isEmpty) {
                result = br.head :: result
                br = br.tail
            }
            if (lastEntry != null)
                result = lastEntry :: result
            result
        }
        private def anyUnsealed(parseResult: MultiParseResult[Sealable, Elem, TypeContext]) =
            parseResult.exists(!_.isSealed)

        def apply(in: Input, ctx: ParserState): MultiParseResult[List[Opt[T]], Elem, TypeContext] = {
            //parse token
            // convert alternative results into optList
            //but keep next entries
            var res: MultiParseResult[Sealable, Elem, TypeContext] = opt(p)(in, ctx).mapf(ctx, (f, t) => {
                t match {
                    case Some(x) => Sealable(false, List(Opt(f, x)))
                    case None => Sealable(true, List())
                }
            })
            res = join(ctx, res)

            //while not all result failed
            while (anyUnsealed(res)) {
                //parse token (in shortest not-failed result)
                //convert to optList
                var nextTokenOffset: Int =
                    res.toList(ctx).foldLeft(Integer.MAX_VALUE)((_, _) match {
                        case (x, (f, Success(t, next))) => if (t.isSealed) x else min(x, next.offset)
                        case (x, _) => x
                    })

                res = res.seqAllSuccessful(ctx,
                    (fs, x) =>
                        //only extend the firstmost unsealed result
                        if (x.result.isSealed || x.next.offset != nextTokenOffset)
                            x
                        else {
                            //try performance heuristic A first
                            applyStrategyA(x.nextInput, fs) match {
                                case Some((result, next)) =>
                                    Success(Sealable(false, result :: x.result.resultList), next)
                                case None =>
                                    //default case, use normal mechanism 
                                    // extend unsealed lists with the next result (if there is no next result, seal the list)                        	
                                    x.seq(fs, opt(p)(x.next, fs)).mapf(fs, (f, t) => t match {
                                        case Sealable(_, resultList) ~ Some(t) => {
                                            if (productionName == "externalDef")
                                                println("next externalDef @ " + x.next.first.getPosition) //+"   "+t+"/"+f)
                                            Sealable(false, Opt(f, t) :: resultList)
                                        }
                                        case Sealable(_, resultList) ~ None => Sealable(true, resultList)
                                    })
                            }
                        })
                //aggressive joins
                res = join(ctx, res)
            }
            //return all sealed lists
            res.map(_.resultList.reverse)
        }

        /**
         * performance heuristic 1: parse the next statement with the annotation of the next token.
         *  if it yields a unique result before the next token that would be parsed 
         *  with the alternative (split) parser, then this is the only result we need 
         *  to care about
         * 
         * will work in the common case that the entire entry is annotated and 
         *  is not interleaved with other annotations
         *  
         *  XXX this probably conflicts with the greedy approach of skipping tokens already in next
         *  therefore this strategy might apply in significantly less cases than it could
         */
        def applyStrategyA(in0: Input, ctx: ParserState): Option[(Opt[T], TokenReader[Elem, TypeContext])] = {
            val firstFeature = in0.first.getFeature
            if (!FeatureSolverCache.implies(ctx, firstFeature) && !FeatureSolverCache.mutuallyExclusive(ctx, firstFeature)) {
                val parseResult = p(in0, ctx.and(firstFeature))
                parseResult match {
                    case Success(result, next) =>
                        if (next.offset <= in0.skipHidden(ctx.and(firstFeature.not)).offst)
                            Some(Opt(ctx.and(firstFeature), result): Opt[T], next)
                        else None
                    case _ => None
                }
            } else
                None
        }

    }.named("repOpt-" + productionName)

    //old signature
    def repOpt[T](p: => MultiParser[T], joinFunction: (FeatureExpr, T, T) => T, productionName: String): MultiParser[List[Opt[T]]] =
        repOpt(p, productionName)

    /**
     * repeated parsing, at least once (result may not be the empty list)
     * (x)+
     */
    def rep1[T](p: => MultiParser[T]): MultiParser[List[Opt[T]]] =
        p ~ repOpt(p) ^^ {
            case x ~ list => Opt(FeatureExpr.base, x) :: list
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
    def rep1Sep[T, U](p: => MultiParser[T], separator: => MultiParser[U]): MultiParser[List[Opt[T]]] =
        repOpt(p <~ separator) ~ p ^^ {
            /* PG: List.:+ takes linear time, but here it is ok because it is done just once,
             * at the end of a linear-time operation, so the complexity is not changed.
             * This is different from the cases I complained about, where it was used in
             * a loop to construct a list, and lead to a quadratic time complexity */  
            case l ~ r => l :+ Opt(FeatureExpr.base, r)
        }
    /**
     * repetitions 0..n with separator
     * 
     * for the pattern
     * [p ~ (separator ~ p)*]
     */
    def repSep[T, U](p: => MultiParser[T], separator: => MultiParser[U]): MultiParser[List[Opt[T]]] =
        opt(rep1Sep(p, separator)) ^^ { case Some(l) => l; case None => List() }

    /**
     * replace optional list by (possibly empty) list
     */
    def optList[T](p: => MultiParser[List[T]]): MultiParser[List[T]] =
        opt(p) ^^ { case Some(l) => l; case None => List() }

    /**
     * represent optional element either by singleton list or by empty list
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

    def matchInput(p: (Elem, TypeContext) => Boolean, kind: String) = new MultiParser[Elem] {
        private def err(e: Option[Elem]) = errorMsg(kind, e)
        @tailrec
        def apply(in: Input, context: FeatureExpr): MultiParseResult[Elem, Elem, TypeContext] = {
            if (in.atEnd)
                Failure(err(None), context, in, List())
            else {
                val tokenPresenceCondition = in.first.getFeature
                if (FeatureSolverCache.implies(context, tokenPresenceCondition))
                    //always parsed in this context (and greedily skip subsequent ignored tokens)
                    returnFirstToken(in, context)
                else if (FeatureSolverCache.mutuallyExclusive(context, tokenPresenceCondition))
                    //never parsed in this context
                    apply(in.rest.skipHidden(context), context)
                else {
                    //token sometimes parsed in this context -> split parser
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
                Success(in.first, in.rest.skipHidden(context))
            } else {
                in.first.countFailure
                Failure(err(Some(in.first)), context, in, List())
            }
        }

    }.named("matchInput")

    def token(kind: String, p: Elem => Boolean) = tokenWithContext(kind, (e, c) => p(e))
    def tokenWithContext(kind: String, p: (Elem, TypeContext) => Boolean) = matchInput(p, kind)
    private def errorMsg(kind: String, inEl: Option[Elem]) =
        (if (!inEl.isDefined) "reached EOF, " else "found \"" + inEl.get.getText + "\", ") + "but expected \"" + kind + "\""

}
case class ~[+a, +b](_1: a, _2: b) {
    override def toString = "(" + _1 + "~" + _2 + ")"
}
case class Opt[+T](val feature: FeatureExpr, val entry: T)
