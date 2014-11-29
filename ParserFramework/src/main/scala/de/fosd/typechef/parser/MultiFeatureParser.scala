package de.fosd.typechef.parser

import scala.math._
import annotation.tailrec
import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureModel, FeatureExpr}
import FeatureExprFactory.True
import de.fosd.typechef.error.WithPosition

/**
 * adopted parser combinator framework with support for multi-feature parsing
 *
 * @author kaestner
 */
abstract class MultiFeatureParser(val featureModel: FeatureModel = null, debugOutput: Boolean = false) {
    type Elem <: AbstractToken
    type TypeContext
    type Input = TokenReader[Elem, TypeContext]
    type ParserState = FeatureExpr

    val featureSolverCache = new FeatureSolverCache(featureModel)

    class SeqParser[T, U](thisParser: => MultiParser[T], thatParser: => MultiParser[U]) extends MultiParser[~[T, U]] {
        name = "~"

        def apply(in: Input, parserState: ParserState): MultiParseResult[~[T, U]] = {
            val firstResult = thisParser(in, parserState)
            firstResult.seq2(parserState, thatParser)
        }

        def a = thisParser

        def b = thatParser
    }

    class SeqCommitParser[T, U](thisParser: => MultiParser[T], thatParser: => MultiParser[U]) extends SeqParser[T, U](thisParser, thatParser) {
        name = "~!"

        override def apply(in: Input, parserState: ParserState): MultiParseResult[~[T, U]] =
            thisParser(in, parserState).seq2(parserState, (next: Input, fs: FeatureExpr) => thatParser(next, fs).commit)
    }

    class AltParser[T, U >: T](thisParser: => MultiParser[T], alternativeParser: => MultiParser[U]) extends MultiParser[U] {
        name = "|"

        def a = thisParser

        def b = alternativeParser

        def apply(in: Input, parserState: ParserState): MultiParseResult[U] = {
            thisParser(in, parserState).replaceAllFailure(parserState, (fs: FeatureExpr) => alternativeParser(in, fs))
        }
    }

    class MapParser[T, U](thisParser: => MultiParser[T], f: T => U) extends MultiParser[U] {
        name = "map"

        def a = thisParser

        def apply(in: Input, feature: FeatureExpr): MultiParseResult[U] =
            thisParser(in, feature).map(f)
    }

    class MapWithPositionParser[T, U](thisParser: => MultiParser[T], f: T => U) extends MultiParser[U] {
        name = "map"

        def a = thisParser

        def apply(in: Input, feature: FeatureExpr): MultiParseResult[U] = {
            val result = thisParser(in, feature).map(f)
            result.mapfr(FeatureExprFactory.True, (f, r) => r match {
                case Success(t, restIn) =>
                    if (t.isInstanceOf[WithPosition])
                        t.asInstanceOf[WithPosition].setPositionRange(in.skipHidden(f, featureSolverCache).pos, restIn.pos)
                    null
                case _ => null
            })
            result
        }
    }

    abstract class RepParser[T](thisParser: => MultiParser[T]) extends MultiParser[List[Opt[T]]] {
        def a = thisParser

        name = "*"
    }

    class JoinParser[T](thisParser: => MultiParser[T]) extends MultiParser[Conditional[T]] {
        name = "!"

        def a = thisParser

        def apply(in: Input, feature: FeatureExpr): MultiParseResult[Conditional[T]] =
            thisParser(in, feature).join(feature)
    }

    abstract class AtomicParser[T](val kind: String) extends MultiParser[T] {
        name = kind
    }

    abstract class OtherParser[T](thisParser: => MultiParser[T]) extends MultiParser[T] {
        def a = thisParser
    }

    class OptParser[+T](thisParser: => MultiParser[T]) extends MultiParser[Option[T]] {
        name = "opt"

        def a = thisParser

        def apply(in: Input, feature: FeatureExpr): MultiParseResult[Option[T]] =
            (thisParser ^^ (x => Some(x)) | success(None))(in, feature)
    }

    //parser
    abstract class MultiParser[+T] extends ((Input, ParserState) => MultiParseResult[T]) {
        thisParser =>
        protected var name: String = ""

        def named(n: String): this.type = {
            name = n;
            this
        }

        override def toString() = "Parser (" + name + ")"

        /**
         * sequencing is difficult when each element can have multiple results for different features
         * tries to join split parsers as early as possible
         */
        def ~[U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = new SeqParser(this, thatParser)

        /**
         * non-backtracking sequencing (replace failures by errors)
         */
        def ~![U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = new SeqCommitParser(this, thatParser)

        /** allows backtracking */
        def ~~[U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = new SeqParser(this, thatParser)


        /**
         * alternatives in the presence of multi-parsing
         * (no attempt to join yet)
         */
        def |[U >: T](alternativeParser: => MultiParser[U]): MultiParser[U] = new AltParser(this, alternativeParser)

        /**
         * ^^ as in the original combinator parser framework
         */
        def ^^[U](f: T => U): MultiParser[U] = mapWithPosition(f)

        def map[U](f: T => U): MultiParser[U] = new MapParser(this, f)

        def mapWithPosition[U](f: T => U): MultiParser[U] = new MapWithPositionParser(this, f)

        //replace on success
        def ^^^[U](repl: U): MultiParser[U] = mapWithPosition(x => repl)

        /**
         * map and join ASTs (when possible)
         */
        def ^^![U](f: T => U): MultiParser[Conditional[U]] =
            this.mapWithPosition(f).join

        //        def ^^!![U](f: Conditional[T] => Conditional[U]): MultiParser[Conditional[U]] =
        //            this.map(f).join.map(Conditional.combine(_))

        /**
         * join parse results when possible
         */
        def !(): MultiParser[Conditional[T]] = join.named("!")

        def join: MultiParser[Conditional[T]] = new JoinParser(this)

        def changeContext(contextModification: (T, FeatureExpr, TypeContext) => TypeContext): MultiParser[T] =
            new MultiParser[T] {
                def apply(in: Input, feature: FeatureExpr): MultiParseResult[T] =
                    thisParser(in, feature).changeContext(feature, contextModification)
            }.named("__context")

        /**
         * from original framework, sequence parsers, but drop first result
         */
        def ~>[U](thatParser: => MultiParser[U]): MultiParser[U] = {
            thisParser ~ thatParser ^^ {
                (x: T ~ U) => x match {
                    case ~(a, b) => b
                }
            }
        }.named("~>")

        /**
         * combines ~> and ~! (non backtracking and dropping first result)
         */
        def ~!>[U](thatParser: => MultiParser[U]): MultiParser[U] = this ~! thatParser ^^ {
            case a ~ b => b
        }

        def ~~>[U](thatParser: => MultiParser[U]): MultiParser[U] = this ~~ thatParser ^^ {
            case a ~ b => b
        }

        /**
         * from original framework, sequence parsers, but drop last result
         */
        def <~[U](thatParser: => MultiParser[U]): MultiParser[T] = {
            thisParser ~ thatParser ^^ {
                (x: T ~ U) => x match {
                    case ~(a, b) => a
                }
            }
        }.named("<~")

        def <~~[U](thatParser: => MultiParser[U]): MultiParser[T] = {
            thisParser ~~ thatParser ^^ {
                (x: T ~ U) => x match {
                    case ~(a, b) => a
                }
            }
        }.named("<~~")

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
    def opt[T](p: => MultiParser[T]) = new OptParser[T](p)


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

        private def anyUnsealed(parseResult: MultiParseResult[Sealable[T]]) =
            parseResult.exists(!_.isSealed)

        def apply(in: Input, parserState: ParserState): MultiParseResult[List[T]] = {
            val p_ = opt(p) ^^ {
                case Some(x) =>
                    unsealed(List(x))
                case None =>
                    seal(Nil)
            }
            var res: MultiParseResult[Sealable[T]] = p_(in, parserState)
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


    /** joins two optList with two special features:
      *
      * common elements at the end of the list (eq) are joined (they originated from replication in ~)
      *
      * if the first element of both lists is the same (==) they are joined as well. this originates
      * from parsing elements after optional elements twice (e.g., 2 in "1_A 2")
      *
      * this is a heuristic to reduce the size of the produced AST. it does not affect correctness
      *
      * feature may be used and is conjuncted to all elements on list inA and (inA.each.and(feature))
      * and negated to inB (inB.each.and(feature.note))
      *
      * Note the checks for satisfiability are necessary as long as we allow crosstree joins, because:
      * During a split we simply replicate all previous entries, which may include entires that
      * are actually not allowed in that branch. We do not filter right away, because it is probably
      * faster to join the entire list again, and there it does not matter that it was temporarily invalid.
      * However, wenn we perform crosstree joins, invalid entries suddenly can matter and should be filtered.
      * (This indicates potential inefficienties where we actually perform replication in the AST)
      *
      *
      * Not a problem!: check whether there can be problems due to comparing two AST nodes. if a list produces
      * several equal AST nodes, can joinLists swallow some of them?
      *
      * nonprivate only for test cases
      */
    protected def joinOptLists[T](al: List[Opt[T]], bl: List[Opt[T]], feature: FeatureExpr): List[Opt[T]] = {
        var a: List[Opt[T]] = al
        var b: List[Opt[T]] = bl
        var result: List[Opt[T]] = Nil

        while (!(a.isEmpty && b.isEmpty)) {
            if (a eq b) {
                return result.reverse ++ a
            }

            //check for identity first, because its faster
            if (!a.isEmpty && !b.isEmpty &&
                ((a.head.entry.asInstanceOf[AnyRef] eq b.head.entry.asInstanceOf[AnyRef]) || (a.head.entry == b.head.entry))) {

                //                assert((a.head.feature and feature).isSatisfiable(featureModel))
                //                assert((b.head.feature andNot feature).isSatisfiable(featureModel))

                val newCondition = (a.head.condition and feature) or (b.head.condition andNot feature)
                if (newCondition.isSatisfiable(featureModel))
                    result = Opt(newCondition, a.head.entry) :: result
                a = a.tail
                b = b.tail
            } else if (a.size > b.size) {
                //                assert((a.head.feature and feature).isSatisfiable(featureModel))
                if ((a.head.condition and feature).isSatisfiable(featureModel))
                    result = a.head.and(feature) :: result
                a = a.tail
            } else {
                //                assert((b.head.feature andNot feature).isSatisfiable(featureModel))
                if ((b.head.condition andNot feature).isSatisfiable(featureModel))
                    result = b.head.andNot(feature) :: result
                b = b.tail
            }

        }

        return result.reverse
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
    def repOpt[T](p: => MultiParser[T], productionName: String = ""): MultiParser[List[Opt[T]]] = new RepParser[T](p) {

        //sealable is only used to enforce correct propagation of token positions in joins (which might not be ensured with fails)
        private case class Sealable(isSealed: Boolean, resultList: List[Opt[T]])

        //join anything, data does not matter, only position in tokenstream
        private def join(ctx: FeatureExpr, res: MultiParseResult[Sealable]): MultiParseResult[Sealable] = {
            val joinedRes: MultiParseResult[Conditional[Sealable]] = res.join(ctx)

            joinedRes.map(_.flatten(flattenConditionalSealable))
        }

        private def flattenConditionalSealable(f: FeatureExpr, a: Sealable, b: Sealable): Sealable =
            Sealable(a.isSealed && b.isSealed, joinOptLists(a.resultList, b.resultList, f))


        private def anyUnsealed(parseResult: MultiParseResult[Sealable]) =
            parseResult.exists(!_.isSealed)

        def apply(in: Input, ctx: ParserState): MultiParseResult[List[Opt[T]]] = {
            //parse token
            // convert alternative results into optList
            //but keep next entries
            var res: MultiParseResult[Sealable] = Success(Sealable(false, List()), in)

            //while not all result failed
            while (anyUnsealed(res)) {
                //parse token (in shortest not-failed result)
                //convert to optList
                var nextTokenOffset: Int =
                    res.toList(ctx).foldLeft(Integer.MAX_VALUE)((_, _) match {
                        case (x, (f, Success(t, next))) => if (t.isSealed) x else min(x, next.offset)
                        case (x, _) => x
                    })

                val res0 = res;
                res = res.seqAllSuccessful(ctx,
                    (ctx, lastSuccess) =>
                    //only extend the firstmost unsealed result
                        if (lastSuccess.result.isSealed || lastSuccess.next.offset != nextTokenOffset)
                            lastSuccess
                        else {
                            //try performance heuristic A first
                            applyStrategyA(lastSuccess.nextInput, ctx) match {
                                case Some((result, next)) =>
                                    Success(Sealable(false, result :: lastSuccess.result.resultList), next)
                                case None =>
                                    //default case, use normal mechanism
                                    // extend unsealed lists with the next result (if there is no next result, seal the list)
                                    val newResult = lastSuccess.seq2(ctx, opt(p))
                                    newResult map {
                                        case Sealable(_, resultList) ~ Some(t) => {
                                            if (debugOutput && productionName == "externalDef")
                                                println("next externalDef @ " + lastSuccess.next.first.getPosition) //+"   "+t+"/"+f)
                                            Sealable(false, Opt(True, t) :: resultList) //opt true, because it will be qualified anywhen when joining
                                        }
                                        case Sealable(_, resultList) ~ None => Sealable(true, resultList)
                                    }
                            }
                        })
                //aggressive joins
                val res2 = join(ctx, res)

                //                if (!check(res2)){
                //                    println(res0)
                //                    res=join(ctx,res);
                //                    assert(false)
                //                }
                res = res2
                //                if (productionName == "externalDef")
                //                    println("after join\n" + debug_printResult(res, 0))
            }
            //return all sealed lists
            res.map(_.resultList.reverse)
        }

        private def check(r: MultiParseResult[Sealable]) = r match {
            case Success(seal, next) =>
                check2(seal.resultList)
            case _ => true
        }

        private def check2(l: List[Opt[T]]): Boolean = {
            val knownExternals = new java.util.IdentityHashMap[T, FeatureExpr]();

            for (Opt(f, ext) <- l) {

                if (!knownExternals.containsKey(ext)) {
                    knownExternals.put(ext, f);
                } else {
                    val priorFexpr = knownExternals.get(ext)

                    if (!(f mex priorFexpr).isTautology(featureModel))
                        return false
                    //assert(false,"!"+priorFexpr + " mex "+f+" in "+ext )

                    knownExternals.put(ext, f or priorFexpr)
                }
            }
            true
        }

        private def debug_printResult(res: MultiParseResult[Sealable], indent: Int): String =
            (" " * indent * 2) + "- " + (res match {
                case SplittedParseResult(f, a, b) =>
                    "V " + f + "\n" + debug_printResult(a, indent + 1) + debug_printResult(b, indent + 1)
                case Success(t, in) => "S " + t.isSealed + " " + in.pos + "\n"
                case NoSuccess(msg, in, _) => "F " + in.pos + " - " + msg + "\n"
            })

        /**
         * performance heuristic 1: parse the next statement with the annotation of the next token.
         * if it yields a unique result before the next token that would be parsed
         * with the alternative (split) parser, then this is the only result we need
         * to care about
         *
         * will work in the common case that the entire entry is annotated and
         * is not interleaved with other annotations
         *
         * XXX this probably conflicts with the greedy approach of skipping tokens already in next
         * therefore this strategy might apply in significantly less cases than it could
         */
        def applyStrategyA(in0: Input, ctx: ParserState): Option[(Opt[T], Input)] = {
            val firstFeature = in0.first.getFeature
            if (!featureSolverCache.implies(ctx, firstFeature) && !featureSolverCache.mutuallyExclusive(ctx, firstFeature)) {
                val parseResult = p(in0, ctx.and(firstFeature))
                parseResult match {
                    case Success(result, next) =>
                        if (next.offset <= in0.skipHidden(ctx.and(firstFeature.not), featureSolverCache).offst)
                            Some(Opt(ctx.and(firstFeature), result): Opt[T], next)
                        else
                            None
                    case _ => None
                }
            } else
                None
        }

    }.named("repOpt-" + productionName)


    /**
     * repeated parsing, at least once (result may not be the empty list)
     * (x)+
     */
    def rep1[T](p: => MultiParser[T]): MultiParser[List[Opt[T]]] =
        p ~ repOpt(p) ^^ {
            case x ~ list => Opt(FeatureExprFactory.True, x) :: list
        }

    /**
     * returns failure when list is empty
     */
    def nonEmpty[T](p: => MultiParser[List[T]]): MultiParser[List[T]] = new OtherParser[List[T]](p) {
        def apply(in: Input, feature: FeatureExpr): MultiParseResult[List[T]] = {
            p(in, feature).seqAllSuccessful(feature,
                (fs: FeatureExpr, x: Success[List[T]]) =>
                    if (x.result.isEmpty)
                        Failure("empty list", x.nextInput, List())
                    else
                        x)
        }
    }.named("nonEmpty")

    /**
     * returns failure when list is empty
     * considers all elements of an optList and returns failure for all conditions where the optList is empty
     *
     * filter can be used to ignore certain kinds of elements. only elements that pass the filter are counted.
     */
    def alwaysNonEmpty[T](p: => MultiParser[List[Opt[T]]], filterE: T => Boolean = (x: T) => true): MultiParser[List[Opt[T]]] = new OtherParser[List[Opt[T]]](p) {
        def apply(in: Input, feature: FeatureExpr): MultiParseResult[List[Opt[T]]] = {
            val t = p(in, feature)
            t.seqAllSuccessful(feature,
                (fs: FeatureExpr, x: Success[List[Opt[T]]]) => {
                    val nonEmptyCondition = x.result.filter(x => filterE(x.entry)).map(_.condition).foldLeft(FeatureExprFactory.False)(_ or _)
                    def error = Failure("empty list", x.nextInput, List())
                    if ((fs implies nonEmptyCondition).isTautology())
                        x
                    else if ((fs and nonEmptyCondition).isContradiction())
                        error
                    else
                        SplittedParseResult(nonEmptyCondition, x, error)
                })
        }
    }.named("nonEmpty")

    /**
     * repetitions 1..n with separator
     *
     * for the pattern
     * p ~ (separator ~ p)*
     */
    def rep1Sep[T, U](p: => MultiParser[T], separator: => MultiParser[U]): MultiParser[List[Opt[T]]] =
        p ~ repOpt(separator ~> p) ^^ {
            case r ~ l => Opt(FeatureExprFactory.True, r) :: l
        }

    /**
     * repetitions 0..n with separator
     *
     * for the pattern
     * [p ~ (separator ~ p)*]
     */
    def repSep[T, U](p: => MultiParser[T], separator: => MultiParser[U]): MultiParser[List[Opt[T]]] = {
        val r: MultiParser[List[Opt[T]]] = opt(rep1Sep(p, separator)) ^^ {
            case Some(l) => l;
            case None => List()
        }
        val jr: MultiParser[Conditional[List[Opt[T]]]] = r.join
        jr ^^ {
            _.flatten[List[Opt[T]]]((f, a, b) => joinOptLists(a, b, f))
        }
    }

    /** see repSepOptIntern, consumes tailing separator(!) **/
    def repSepOpt[T](p: => MultiParser[T], separator: => MultiParser[Elem], productionName: String = ""): MultiParser[List[Opt[T]]] =
        repSepOptIntern(true, p, separator, productionName) ^^ (_._1)

    /** see repSepOptIntern, consumes tailing separator(!) **/
    def rep1SepOpt[T](p: => MultiParser[T], separator: => MultiParser[Elem], productionName: String = ""): MultiParser[List[Opt[T]]] =
        repSepOptIntern(false, p, separator, productionName) ^^ (_._1)


    /**
     * this is a performance optimization of repSep that avoids the exponential complexity
     * of repSep in case of misaligned (undisciplined) annotations, which are quite common
     * in certain lists of some language
     *
     * this parser has a number of serious restrictions, check if they apply:
     * - may only use a single token as a separator
     * - separators are not part of the output of this parser
     * - and most importantly: consumes all tailing separators
     *
     * It returns a list of parsed elements and a feature expression that describes
     * in which variants a tailing separator was found and consumed. In case
     * the separator can match different kinds of tokens, it is not stored what
     * kind of tailing token was consumed under which condition. If this is a problem
     * do not use repSepOpt!
     *
     */
    def repSepOptIntern[T](firstOptional: Boolean, p: => MultiParser[T], separator: => MultiParser[Elem], productionName: String = "") = new MultiParser[(List[Opt[T]], FeatureExpr)] {
        thisParser =>

        //sealable is only used to enforce correct propagation of token positions in joins (which might not be ensured with fails)
        private case class Sealable(isSealed: Boolean, resultList: List[Opt[T]], freeSeparator: FeatureExpr)

        //join anything, data does not matter, only position in tokenstream
        private def join(ctx: FeatureExpr, res: MultiParseResult[Sealable]) =
            res.join(ctx).map(_.flatten(joinSealable))

        private def joinSealable(f: FeatureExpr, a: Sealable, b: Sealable) =
            Sealable(a.isSealed && b.isSealed, joinOptLists(a.resultList, b.resultList, f), (a.freeSeparator) and (b.freeSeparator))

        //        private def flattenConditionalSealable(r:Co)
        //XXX a.freesep OR a.freesep is incorrect. is AND sufficient?

        private def anyUnsealed(parseResult: MultiParseResult[Sealable]) =
            parseResult.exists(!_.isSealed)

        def apply(in: Input, ctx: ParserState): MultiParseResult[(List[Opt[T]], FeatureExpr)] = {
            var res: MultiParseResult[Sealable] = Success(Sealable(false, List(), ctx), in)

            //while not all result failed
            while (anyUnsealed(res)) {
                //parse token (in shortest not-failed result)
                //convert to optList
                val nextTokenOffset: Int =
                    res.toList(ctx).foldLeft(Integer.MAX_VALUE)((_, _) match {
                        case (x, (f, Success(t, next))) => if (t.isSealed) x else min(x, next.offset)
                        case (x, _) => x
                    })

                //try to parse separator first
                res = res.seqAllSuccessful(ctx,
                    (fs, x) =>
                    //only extend the firstmost unsealed result
                        if (x.result.isSealed || x.next.offset != nextTokenOffset)
                            x
                        else {
                            //no split parsing for separators, just look at the next token
                            val nextToken = x.next.skipHidden(fs, featureSolverCache)
                            //checking the implication is not necessary technically, but used
                            //for optimization to avoid growing formula when all tokens have the same condition
                            val parsingCtx =
                                if (featureSolverCache.implies(fs, nextToken.first.getFeature))
                                    fs
                                else fs and (nextToken.first.getFeature)
                            val nextSep = separator(nextToken, parsingCtx)
                            if (nextSep.isInstanceOf[Success[_]]) {
                                val freeSepBefore = x.result.freeSeparator
                                //consume token only if not two subsequent separators, otherwise seal list
                                if (featureSolverCache.mutuallyExclusive(freeSepBefore, parsingCtx))
                                    Success(Sealable(x.result.isSealed, x.result.resultList, freeSepBefore or parsingCtx), nextToken.rest)
                                else
                                    Success(Sealable(true, x.result.resultList, freeSepBefore), nextToken)
                            } else
                                x
                        }
                )

                res = res.seqAllSuccessful(ctx,
                    (fs, x) =>
                    //only extend the firstmost unsealed result
                        if (x.result.isSealed || x.next.offset != nextTokenOffset)
                            x
                        else {
                            //try performance heuristic A first
                            applyStrategyA(x.nextInput, fs) match {
                                case Some((result, next)) =>
                                    Success(Sealable(false, result :: x.result.resultList, x.result.freeSeparator andNot (result.condition)), next)
                                case None =>
                                    //default case, use normal mechanism
                                    // extend unsealed lists with the next result (if there is no next result, seal the list)
                                    x.seq(fs, opt(p)(x.next, fs)).mapfr(fs, (f, r) => r match {
                                        case Success(Sealable(_, resultList, openSep) ~ Some(t), in) => {
                                            //requires separator first
                                            // XXX (might be more efficient to check before actually parsing, but unclear whether this works for split parse results)
                                            if (featureSolverCache.implies(f, openSep))
                                                Success(Sealable(false, Opt(f, t) :: resultList, openSep andNot f), in)
                                            else
                                                Success(Sealable(true, resultList, openSep), x.nextInput)
                                        }
                                        case Success(Sealable(_, resultList, openSep) ~ None, in) => Success(Sealable(true, resultList, openSep), in)
                                        case e: NoSuccess => e
                                    })
                            }
                        })
                //aggressive joins
                res = join(ctx, res)
            }
            //return all sealed lists
            val result: MultiParseResult[(List[Opt[T]], FeatureExpr)] = res.map(sealable => (sealable.resultList.reverse, sealable.freeSeparator))
            if (!firstOptional)
                result.mapfr(ctx, (f, r) => r match {
                    //XXX ChK: the following is an incorrect approximation and the lower code should be used, but reduces performance significantly
                    case Success((l, f), next) if (l.isEmpty) => Failure("empty list (" + productionName + ")", next, List())
                    //		    case e@Success((l, f), next) => {
                    //                       val nonEmptyCondition = l.foldRight(FeatureExprFactory.False)(_.feature or _)
                    //                       lazy val failure = Failure("empty list (" + productionName + ")", next, List())
                    //                       if (nonEmptyCondition.isTautology(featureModel)) e
                    //                       else if (nonEmptyCondition.isContradiction(featureModel)) failure
                    //                       else SplittedParseResult(nonEmptyCondition, e, failure)
                    //                   }
                    case e => e
                })
            else
                result
        }

        /**
         * performance heuristic 1: parse the next statement with the annotation of the next token.
         * if it yields a unique result before the next token that would be parsed
         * with the alternative (split) parser, then this is the only result we need
         * to care about
         *
         * will work in the common case that the entire entry is annotated and
         * is not interleaved with other annotations
         *
         * XXX this probably conflicts with the greedy approach of skipping tokens already in next
         * therefore this strategy might apply in significantly less cases than it could
         */
        def applyStrategyA(in0: Input, ctx: ParserState): Option[(Opt[T], TokenReader[Elem, TypeContext])] = {
            val firstFeature = in0.first.getFeature
            if (!featureSolverCache.implies(ctx, firstFeature) && !featureSolverCache.mutuallyExclusive(ctx, firstFeature)) {
                val parseResult = p(in0, ctx.and(firstFeature))
                parseResult match {
                    case Success(result, next) =>
                        if (next.offset <= in0.skipHidden(ctx.and(firstFeature.not), featureSolverCache).offst)
                            Some(Opt(ctx.and(firstFeature), result): Opt[T], next)
                        else None
                    case _ => None
                }
            } else
                None
        }

        //        /**turns a parse result with a conditional tailing separators into two parse results, indicating whther
        //         * there is a trailing separator */
        //        def hasTrailingSeparator(result: MultiParseResult[(List[Opt[T]], FeatureExpr)]): MultiParseResult[(List[Opt[T]], Boolean)] = {
        //            result.mapfr(FeatureExprFactory.True,
        //                //(FeatureExpr, ParseResult[T]) => ParseResult[U]
        //                (f, result) => result match {
        //                    case Success((r, f), in) =>
        //                        if (f.isBase) Success((r, true), in)
        //                        else if (f.isDead) Success((r, false), in)
        //                        else SplittedParseResult(f, Success((r, true), in), Success((r, false), in))
        //                    case e: Failure => e
        //                }
        //            )
        //        }
        //
        //        def sep_~[U](thatParser: => MultiParser[Option[U]]): MultiParser[~[List[Opt[T]], Option[U]]] = new MultiParser[~[List[Opt[T]], Option[U]]] {
        //            override def apply(in: Input, parserState: ParserState): MultiParseResult[~[List[Opt[T]], Option[U]]] = {
        //                val firstResult = hasTrailingSeparator(thisParser(in, parserState))
        //                firstResult.seqAllSuccessful(parserState, (fs, x) => {
        //                    val secondResult = if (x.result._2) thatParser(x.next, fs)
        //                    else Success(None, x.nextInput)
        //                    x.seq(fs, secondResult).map({case a ~ b => new ~(a._1, b)})
        //                })
        //            }
        //        }

    }.named("repSepOpt-" + productionName)


    /**
     * replace optional list by (possibly empty) list
     */
    def optList[T](p: => MultiParser[List[T]]): MultiParser[List[T]] =
        opt(p) ^^ {
            case Some(l) => l;
            case None => List()
        }

    /**
     * represent optional element either by singleton list or by empty list
     */
    def opt2List[T](p: => MultiParser[T]): MultiParser[List[T]] =
        opt(p) ^^ {
            _.toList
        }

    /**
     * parses using p, but only returns either success or no-success and(!)
     * does not proceed the input stream (for successful entries)
     */
    def lookahead[T](p: => MultiParser[T]): MultiParser[Any] = new MultiParser[Any] {
        def apply(in: Input, parserState: ParserState): MultiParseResult[Any] = {
            p(in, parserState).seqAllSuccessful(parserState,
                (fs: FeatureExpr, x: Success[T]) => Success("lookahead", in))
        }
    }

    def fail[T](msg: String): MultiParser[T] =
        new MultiParser[T] {
            def apply(in: Input, fs: FeatureExpr) = Failure(msg, in, List())
        }

    def failc[T](msg: String): MultiParser[Conditional[T]] =
        new MultiParser[Conditional[T]] {
            def apply(in: Input, fs: FeatureExpr) = Failure(msg, in, List())
        }

    def success[T](v: T) =
        MultiParser {
            (in: Input, fs: FeatureExpr) => Success(v, in)
        }

    def MultiParser[T](f: (Input, FeatureExpr) => MultiParseResult[T]): MultiParser[T] =
        new MultiParser[T] {
            def apply(in: Input, fs: FeatureExpr) = f(in, fs)
        }

    def matchInput(p: (Elem, FeatureExpr, TypeContext) => Boolean, kind: String) = new AtomicParser[Elem](kind) {
        private def err(e: Option[Elem], ctx: TypeContext) = errorMsg(kind, e, ctx)

        //        @tailrec
        def apply(in: Input, context: FeatureExpr): MultiParseResult[Elem] = {
            val parseResult: MultiParseResult[(Input, Elem)] = next(in, context)
            parseResult.mapfr(context, {
                case (feature, Success(resultPair, inNext)) =>
                    if (p(resultPair._2, feature, in.context)) {
                        //consumed one token
                        resultPair._2.countSuccess(feature)
                        Success(resultPair._2, inNext)
                    }
                    else {
                        resultPair._2.countFailure
                        Failure(err(Some(resultPair._2), in.context), resultPair._1, List())
                    }
                case (_, f: NoSuccess) => f
            }).joinNoSuccess
        }
    }.named("matchInput " + kind)

    private val next: MultiParser[(Input, Elem)] = new MultiParser[(Input, Elem)] {

        var cache_in: Input = null
        var cache_ctx: FeatureExpr = null
        var cache_value: MultiParseResult[(Input, Elem)] = null

        def apply(in: Input, context: FeatureExpr): MultiParseResult[(Input, Elem)] = {
            if (!((in eq cache_in) && (context eq cache_ctx))) {
                cache_in = in
                cache_ctx = context
                cache_value = getNext(in, context)
            }
            cache_value
        }

        @tailrec
        def getNext(in: Input, context: FeatureExpr): MultiParseResult[(Input, Elem)] = {
            if (in.atEnd)
                Failure(errorMsg("EOF", None, in.context), in, List())
            else {
                val tokenPresenceCondition = in.first.getFeature
                if (featureSolverCache.implies(context, tokenPresenceCondition))
                //always parsed in this context (and greedily skip subsequent ignored tokens)
                    Success((in, in.first), in.rest.skipHidden(context, featureSolverCache))
                else if (featureSolverCache.mutuallyExclusive(context, tokenPresenceCondition))
                //never parsed in this context
                    getNext(in.rest.skipHidden(context, featureSolverCache), context)
                else {
                    //token sometimes parsed in this context -> split parser
                    in.first.countSplit
                    splitParser(in, context)
                }
            }
        }

        private def splitParser(in: Input, context: FeatureExpr): MultiParseResult[(Input, Elem)] = {
            val feature = in.first.getFeature
            val ctxAndFeat = context.and(feature)
            assert(featureSolverCache.implies(ctxAndFeat, feature))
            val r1 = getNext(in, ctxAndFeat)

            val ctxAndNotFeat = context.and(feature.not)
            assert(featureSolverCache.mutuallyExclusive(ctxAndNotFeat, feature))
            val r2 = getNext(in, ctxAndNotFeat)

            DebugSplitting("split at \"" + in.first.getText + "\" at " + in.first.getPosition + " from " + context + " with " + feature)
            SplittedParseResult(feature, r1, r2)
        }
    }.named("next")


    def token(kind: String, p: Elem => Boolean) = tokenWithContext(kind, (e, _, _) => p(e))

    def tokenWithContext(kind: String, p: (Elem, FeatureExpr, TypeContext) => Boolean) = matchInput(p, kind)

    private
    def errorMsg(kind: String, inEl: Option[Elem], ctx: TypeContext): String =
        (if (!inEl.isDefined) "reached EOF, " else "found \"" + inEl.get.getText + "\", ") + "but expected \"" + kind + "\""

    // +" -- "+ctx

    /**
     *
     * Note:
     * TypeContext is an object that can be passed during parsing
     * it is not the feature expression of the current parser/result (which is only encoded
     * as feature in SplitParseResult)
     *
     * It is currently used for C to pass an object that contains all defined Types
     */
    sealed abstract class MultiParseResult[+T] {
        def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T]) => MultiParseResult[U]): MultiParseResult[U]

        def seq2[U](context: FeatureExpr, p: (Input, FeatureExpr) => MultiParseResult[U]): MultiParseResult[~[T, U]]

        def replaceAllFailure[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U]): MultiParseResult[U]

        def map[U](f: T => U): MultiParseResult[U]

        def mapf[U](feature: FeatureExpr, f: (FeatureExpr, T) => U): MultiParseResult[U]

        def mapfr[U](feature: FeatureExpr, f: (FeatureExpr, ParseResult[T]) => MultiParseResult[U]): MultiParseResult[U]

        def mapr[U](f: ParseResult[T] => MultiParseResult[U]): MultiParseResult[U] = mapfr(FeatureExprFactory.True, (ctx, r) => f(r))

        /**
         * joins as far as possible. joins all successful ones but maintains partially successful results.
         * keeping partially unsucessful results is necessary to consider multiple branches for an alternative on ASTs
         **/
        def join(parserContext: FeatureExpr): MultiParseResult[Conditional[T]]

        def joinTree(parserContext: FeatureExpr): MultiParseResult[Conditional[T]]

        def joinCrosstree(parserContext: FeatureExpr): MultiParseResult[Conditional[T]]

        def expectOneResult: ParseResult[T] =
            this match {
                case s@Success(_, _) => s
                case s@NoSuccess(_, _, _) => s
                case SplittedParseResult(f, a, b) => Error("Unsuccessful join " + f + ": " + a + " / " + b, null, List())
            }

        def joinNoSuccess(): MultiParseResult[T]

        def allFailed: Boolean

        def exists(predicate: T => Boolean): Boolean

        /**
         * toList recursively flattens the tree structure and creates resulting feature expressions
         */
        def toList(baseFeatureExpr: FeatureExpr): List[(FeatureExpr, ParseResult[T])]

        def toErrorList: List[Error]

        def changeContext(ctx: FeatureExpr, contextModification: (T, FeatureExpr, TypeContext) => TypeContext): MultiParseResult[T]

        //replace all failures by errors (non-backtracking!)
        def commit: MultiParseResult[T]

        // removes branches from split parse results that are not reachable according to the feature model
        def prune(fm: FeatureModel): MultiParseResult[T] = prune(True, fm)
        private[MultiFeatureParser] def prune(ctx: FeatureExpr, fm: FeatureModel): MultiParseResult[T]
    }

    /**
     * split into two parse results (all calls are propagated to the individual results)
     * @author kaestner
     */
    case class SplittedParseResult[+T](feature: FeatureExpr, resultA: MultiParseResult[T], resultB: MultiParseResult[T]) extends MultiParseResult[T] {
        def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T]) => MultiParseResult[U]): MultiParseResult[U] = {
            SplittedParseResult(feature, resultA.seqAllSuccessful(context.and(feature), f), resultB.seqAllSuccessful(context.and(feature.not), f))
        }

        def seq2[U](context: FeatureExpr, thatParser: (Input, FeatureExpr) => MultiParseResult[U]): MultiParseResult[~[T, U]] =
            SplittedParseResult[~[T, U]](feature, resultA.seq2(context and feature, thatParser), resultB.seq2(context andNot feature, thatParser))

        def replaceAllFailure[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U]): MultiParseResult[U] = {
            SplittedParseResult(feature, resultA.replaceAllFailure(context.and(feature), f), resultB.replaceAllFailure(context.and(feature.not), f))
        }

        def map[U](f: T => U): MultiParseResult[U] =
            SplittedParseResult(feature, resultA.map(f), resultB.map(f))

        def mapf[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => U): MultiParseResult[U] =
            SplittedParseResult(feature, resultA.mapf(inFeature and feature, f), resultB.mapf(inFeature and (feature.not), f))

        def mapfr[U](inFeature: FeatureExpr, f: (FeatureExpr, ParseResult[T]) => MultiParseResult[U]): MultiParseResult[U] = {
            val newResultA = resultA.mapfr(inFeature and feature, f)
            val newResultB = resultB.mapfr(inFeature and (feature.not), f)
            if ((newResultA eq resultA) && (newResultB eq resultB))
                this.asInstanceOf[MultiParseResult[U]]
            else
                SplittedParseResult(feature, newResultA, newResultB)
        }

        def join(parserContext: FeatureExpr): MultiParseResult[Conditional[T]] = joinCrosstree(parserContext)

        /**
         * joinCrosstree is an aggressive join algorithm that joins any joinable elements in the tree
         *
         * the algorithm works at follows: it starts bottom up, so that at any point during joining
         * there are no join opportunaties left within only the left or right branch of a split. joining
         * is only performed between pairs of elements, one from the left branch and and one from the right branch
         *
         * for each element in the left branch, we search whether there is a joinable element in the right
         * branch. if there is, we create a new parent SplittedParseResult with the joined result, and subsequently
         * prune both original entries from the tree (each removing the direct parent SplittedParseResult node)
         */
        def joinCrosstree(parserContext: FeatureExpr): MultiParseResult[Conditional[T]] = {
            def performJoin(featureA: FeatureExpr, a: ParseResult[Conditional[T]], b: ParseResult[Conditional[T]]): Option[ParseResult[Conditional[T]]] = (a, b) match {
                case (sA@Success(rA: Conditional[_], inA), sB@Success(rB: Conditional[_], inB)) => {
                    if (isSamePosition(parserContext, inA, inB)) {
                        val r = createChoiceC(featureA, rA.asInstanceOf[Conditional[T]], rB.asInstanceOf[Conditional[T]])
                        val pos = firstOf(inA, inB)
                        Some(Success(r, pos))
                    } else
                        None
                }
                //                //both not sucessful
                //                case (nA@NoSuccess(mA, inA, iA), nB@NoSuccess(mB, inB, iB)) => {
                //                    Some(Failure("joined error", inA, List(nA, nB)))
                //                }
                //partially successful
                case (a, b) => None
            }



            val left = resultA.joinCrosstree(parserContext and feature)
            val right = resultB.joinCrosstree(parserContext and (feature.not))
            var result = SplittedParseResult(feature, left, right)

            //implementation note: features local from here. does not make a difference for local rewrites, but keeps rewritten formulas shorter
            val leftResults: List[(FeatureExpr, ParseResult[Conditional[T]])] = left.toList(feature)
            val rightResults: List[(FeatureExpr, ParseResult[Conditional[T]])] = right.toList(feature.not)

            //compare every entry from the left with every entry from the right to find join candidates
            var toPruneList: List[ParseResult[Conditional[T]]] = List()
            for (lr <- leftResults; rr <- rightResults) {
                val joined: Option[ParseResult[Conditional[T]]] = performJoin(lr._1, lr._2, rr._2)
                if (joined.isDefined) {
                    result = SplittedParseResult(lr._1 or (rr._1), joined.get, result)
                    toPruneList = lr._2 :: rr._2 :: toPruneList
                }
            }
            prune(result, toPruneList)
        }

        private def isSamePosition(parserContext: FeatureExpr, inA: TokenReader[Elem, TypeContext], inB: TokenReader[Elem, TypeContext]): Boolean = {
            if (inA.offset == inB.offset) return true;
            val nextA = inA.skipHidden(parserContext and feature, featureSolverCache)
            val nextB = inB.skipHidden(parserContext and (feature.not), featureSolverCache)
            return nextA.offset == nextB.offset
        }

        private def firstOf(inA: TokenReader[Elem, TypeContext], inB: TokenReader[Elem, TypeContext]) =
            (if (inA.offst < inB.offst) inB else inA).setContext(joinContext(inA.context, inB.context))

        //don't create a choice when both branches are the same
        private def createChoice[T](f: FeatureExpr, a: T, b: T): Conditional[T] = createChoiceC(f, One(a), One(b))

        private def createChoiceC[T](f: FeatureExpr, a: Conditional[T], b: Conditional[T]): Conditional[T] = if (a == b) a else Choice(f, a, b)

        //removes entries from the tree
        private def prune[U >: T](tree: MultiParseResult[Conditional[U]], pruneList: List[ParseResult[Conditional[U]]]): MultiParseResult[Conditional[U]] =
            if (pruneList.isEmpty) tree
            else tree match {
                case SplittedParseResult(f, a, b) => {
                    val pa = prune(a, pruneList)
                    val pb = prune(b, pruneList)
                    if (pruneList exists (_ eq pa)) pb
                    else if (pruneList exists (_ eq pb)) pa
                    else SplittedParseResult(f, pa, pb)
                }
                case p => p
            }

        /**
         * joinTree is a non-aggressive join algorithm that only joins siblings in the tree, but does
         * not perform cross-tree joins. the advantage is that the algorithm is simple and quick,
         * the disadvantage is that it misses opportunaties for joins
         */
        def joinTree(parserContext: FeatureExpr): MultiParseResult[Conditional[T]] = {
            //do not skip ahead, important for repOpt
            (resultA.joinTree(parserContext and feature), resultB.joinTree(parserContext and (feature.not))) match {
                //both successful
                case (sA@Success(rA, inA), sB@Success(rB, inB)) => {
                    if (isSamePosition(parserContext, inA, inB)) {
                        Success(createChoiceC(feature, rA, rB), firstOf(inA, inB))
                    } else
                        SplittedParseResult(feature, sA, sB)
                }

                /**
                 * foldtree (heuristic for earlier joins, when treelike joins not possible)
                 * used for input such as <_{A&B} <_{A&!B} >_{A} x_{!A}
                 * which creates a parse tree SPLIT(A&B, <>, SPLIT(A&!B, <>, x))
                 * of which the former two can be merged. (occurs in expanded Linux headers...)
                 */
                case (sA@Success(rA, inA), sB@SplittedParseResult(innerFeature, Success(rB, inB), otherParseResult@_)) => {
                    if (isSamePosition(parserContext, inA, inB)) {
                        DebugSplitting("joinT at \"" + inA.first.getText + "\" at " + inA.first.getPosition + " from " + feature)
                        SplittedParseResult(
                            parserContext and (feature or innerFeature),
                            Success(Choice(feature or innerFeature, rA, rB),
                                if (inA.offst < inB.offst) inB else inA),
                            otherParseResult)
                    } else
                        SplittedParseResult(feature, sA, sB)
                }
                //both not sucessful
                case (nA@NoSuccess(mA, inA, iA), nB@NoSuccess(mB, inB, iB)) => {
                    Failure("joined error", inA, List(nA, nB))
                }
                //partially successful
                case (a, b) => SplittedParseResult(feature, a, b)
            }
        }

        def joinNoSuccess(): MultiParseResult[T] =
            (resultA.joinNoSuccess, resultB.joinNoSuccess) match {
                case (f1@Failure(msg1, next1, inner1), f2@Failure(msg2, next2, inner2)) =>
                    Failure(msg1, next1, List(f1, f2) ++ inner1 ++ inner2)
                case (f1@Error(msg1, next1, inner1), f2@Error(msg2, next2, inner2)) =>
                    Error(msg1, next1, List(f1, f2) ++ inner1 ++ inner2)
                case (a, b) => SplittedParseResult(feature, a, b)
            }

        def allFailed = resultA.allFailed && resultB.allFailed

        def exists(p: T => Boolean) = resultA.exists(p) || resultB.exists(p)

        def toList(context: FeatureExpr) = resultA.toList(context and feature) ++ resultB.toList(context and (feature.not))

        def toErrorList = resultA.toErrorList ++ resultB.toErrorList

        def changeContext(ctx: FeatureExpr, contextModification: (T, FeatureExpr, TypeContext) => TypeContext) =
            SplittedParseResult(feature, resultA.changeContext(ctx and feature, contextModification), resultB.changeContext(ctx andNot feature, contextModification))

        def commit: MultiParseResult[T] =
            SplittedParseResult(feature, resultA.commit, resultB.commit)

        private[MultiFeatureParser] def prune(ctx: FeatureExpr, fm: FeatureModel): MultiParseResult[T] =
            if ((ctx and feature).isContradiction(fm))
                resultB.prune(ctx andNot feature, fm)
            else if ((ctx andNot feature).isContradiction(fm))
                resultA.prune(ctx and feature, fm)
            else
                SplittedParseResult(feature, resultA.prune(ctx and feature, fm), resultB.prune(ctx andNot feature, fm))
    }

    /**
     * stores a list of results of which individual entries can belong to a specific feature
     * @author kaestner
     *
     */
    //case class OptListParseResult[+T](entries:List[MultiParseResult[T,Token,TypeContext]]) extends MultiParseResult[T,Token,TypeContext]

    /**
     * contains the recognized parser result (including recognized alternatives?)
     * @author kaestner
     */
    sealed abstract class ParseResult[+T](nextInput: TokenReader[Elem, TypeContext]) extends MultiParseResult[T] {
        def map[U](f: T => U): ParseResult[U]

        def mapfr[U](feature: FeatureExpr, f: (FeatureExpr, ParseResult[T]) => MultiParseResult[U]): MultiParseResult[U] = f(feature, this)

        def next = nextInput

        def isSuccess: Boolean

        def join(parserContext: FeatureExpr): MultiParseResult[Conditional[T]] = this.map(One(_))

        def joinTree(parserContext: FeatureExpr): MultiParseResult[Conditional[T]] = join(parserContext)

        def joinCrosstree(parserContext: FeatureExpr): MultiParseResult[Conditional[T]] = join(parserContext)

        def joinNoSuccess() = this

        def toList(context: FeatureExpr) = List((context, this))

        private[MultiFeatureParser] def prune(ctx: FeatureExpr, fm: FeatureModel): MultiParseResult[T] = this
    }

    abstract class NoSuccess(val msg: String, val nextInput: TokenReader[Elem, TypeContext], val innerErrors: List[NoSuccess]) extends ParseResult[Nothing](nextInput) {
        def map[U](f: Nothing => U) = this

        def mapf[U](inFeature: FeatureExpr, f: (FeatureExpr, Nothing) => U): MultiParseResult[U] = this

        def isSuccess: Boolean = false

        def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[Nothing]) => MultiParseResult[U]): MultiParseResult[U] = this

        def seq2[U](context: FeatureExpr, p: (Input, FeatureExpr) => MultiParseResult[U]): MultiParseResult[Nothing] = this

        def allFailed = true

        def exists(predicate: Nothing => Boolean) = false

        def changeContext(ctx: FeatureExpr, contextModification: (Nothing, FeatureExpr, TypeContext) => TypeContext) = this
    }

    /** An extractor so NoSuccess(msg, next) can be used in matches.
      */
    object NoSuccess {
        def unapply(x: NoSuccess) = x match {
            case Failure(msg, next, inner) => Some(msg, next, inner)
            case Error(msg, next, inner) => Some(msg, next, inner)
            case _ => None
        }
    }

    /**
     * see original parser comb. framework. noncritical error, caught in alternatives
     */
    case class Failure(override val msg: String, override val nextInput: TokenReader[Elem, TypeContext], override val innerErrors: List[NoSuccess]) extends NoSuccess(msg, nextInput, innerErrors) {
        def replaceAllFailure[U >: Nothing](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U]) = f(context)

        def commit = Error(msg, nextInput, innerErrors)

        def toErrorList = List()
    }

    /**
     * see original parser comb. framework. non-backtracking error
     */
    case class Error(override val msg: String, override val nextInput: TokenReader[Elem, TypeContext], override val innerErrors: List[NoSuccess]) extends NoSuccess(msg, nextInput, innerErrors) {
        def replaceAllFailure[U >: Nothing](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U]) = this

        def commit = this

        def toErrorList = List(this)
    }

    case class Success[+T](val result: T, nextInput: TokenReader[Elem, TypeContext]) extends ParseResult[T](nextInput) {
        def map[U](f: T => U): ParseResult[U] = Success(f(result), next)

        def mapf[U](inFeature: FeatureExpr, f: (FeatureExpr, T) => U): MultiParseResult[U] = Success(f(inFeature, result), next)

        def isSuccess: Boolean = true

        def seqAllSuccessful[U](context: FeatureExpr, f: (FeatureExpr, Success[T]) => MultiParseResult[U]): MultiParseResult[U] = f(context, this)

        def seq2[U](context: FeatureExpr, p: (Input, FeatureExpr) => MultiParseResult[U]): MultiParseResult[~[T, U]] = {
            val pResult = p(nextInput, context)
            concat(pResult)
        }

        private def concat[U](pResult: MultiParseResult[U]): MultiParseResult[~[T, U]] = pResult match {
            case s: Success[_] => Success(new ~(result, s.result.asInstanceOf[U]), s.next)
            case n: NoSuccess => n
            case s: SplittedParseResult[_] => SplittedParseResult(s.feature, concat(s.resultA.asInstanceOf[MultiParseResult[U]]), concat(s.resultB.asInstanceOf[MultiParseResult[U]]))
        }

        def seq[U](context: FeatureExpr, thatResult: MultiParseResult[U]): MultiParseResult[~[T, U]] =
            thatResult.seqAllSuccessful[~[T, U]](context, (fs: FeatureExpr, x: Success[U]) => Success(new ~(result, x.result), x.next))

        def replaceAllFailure[U >: T](context: FeatureExpr, f: FeatureExpr => MultiParseResult[U]): MultiParseResult[U] = this

        def allFailed = false

        def exists(predicate: T => Boolean) = predicate(result)

        def toErrorList = List()

        def changeContext(ctx: FeatureExpr, contextModification: (T, FeatureExpr, TypeContext) => TypeContext): MultiParseResult[T] = Success(result, nextInput.setContext(contextModification(result, ctx, nextInput.context)))

        def commit: MultiParseResult[T] = this
    }


    /** <p>
      * A parser generator delimiting whole phrases (i.e. programs).
      * </p>
      * <p>
      * <code>phrase(p)</code> succeeds if <code>p</code> succeeds and
      * no input is left over after <code>p</code>.
      * </p>
      *
      * @param p the parser that must consume all input for the resulting parser
      *          to succeed.
      * @return a parser that has the same result as `p', but that only succeeds
      *         if <code>p</code> consumed all the input.
      */
    def phrase[T](p: MultiParser[T]) = new MultiParser[T] {

        def apply(in: Input, fs: FeatureExpr) = {
            val result = p(in, fs)

            result.mapfr(fs, (feature, result) =>
                result match {
                    case s@Success(out, in1) =>
                        if (in1.atEnd)
                            s
                        else
                            Failure("end of input expected", in1, List())
                    case x: NoSuccess =>
                        result
                }
            )
        }
    }

    /** overwrite for all reasonable contexts */
    def joinContext(a: TypeContext, b: TypeContext): TypeContext = a
}

case class ~[+a, +b](_1: a, _2: b) {
    override def toString = "(" + _1 + "~" + _2 + ")"
}
