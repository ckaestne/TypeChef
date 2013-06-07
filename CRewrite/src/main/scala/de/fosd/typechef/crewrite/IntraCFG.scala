package de.fosd.typechef.crewrite


import de.fosd.typechef.conditional._
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.{FeatureModel, FeatureExprFactory, FeatureExpr}

// implements intraprocedural conditional control flow (cfg) on top of
// the typechef infrastructure
// at first sight the implementation of succ with a lot of private
// function seems overly complicated; however the structure allows
// also to implement pred
// consider the following points:

// the function definition serves as the entry and exit node of the
// cfg, because we do not have special ast nodes for that, or we
// store everything in a cfg itself with // special nodes for entry
// and exit such as
// [1] http://soot.googlecode.com/svn/DUA_Forensis/src/dua/method/CFG.java

// normally pred succ are the same except for the following cases:
// 1. code in switch body that does not belong to a case block, but that has a label and
//    can be reached otherwise, e.g., switch (x) { l1: <code> case 0: .... }
// 2. infinite for loops without break or return statements in them, e.g.,
//    for (;;) { <code without break or return> }
//    this way we do not have any handle to jump into the for block from
//    its successors; we work around this issue by introducing a break statement
//    that always evaluates to true; for (;;) => for (;1;)

// for more information:
// iso/iec 9899 standard; committee draft
// [2] http://www.open-std.org/jtc1/sc22/wg14/www/docs/n1124.pdf

// TODO handling empty { } e.g., void foo() { { } }
// TODO support for (expr) ? (expr) : (expr);
// TODO analysis static gotos should have a label (if more labels must be unique according to feature expresssions)
// TODO analysis dynamic gotos should have a label
// TODO analysis: The expression of each case label shall be an integer constant expression and no two of
//                the case constant expressions in the same switch statement shall have the same value
//                after conversion.
// TODO analysis: There may be at most one default label in a switch statement.
// TODO analysis: we can continue this list only by looking at constrains in [2]


trait IntraCFG extends ASTNavigation with ConditionalNavigation {

    private implicit def optList2ASTList(l: List[Opt[AST]]) = l.map(_.entry)

    private implicit def opt2AST(s: Opt[AST]) = s.entry

    private implicit def condition2AST(c: Conditional[AST]) = childAST(c)

    class CFGCache {
        private val cache = new java.util.IdentityHashMap[Product, List[Opt[AST]]]()

        def update(k: Product, v: List[Opt[AST]]) {
            cache.put(k, v)
        }

        def lookup(k: Product): Option[List[Opt[AST]]] = {
            val v = cache.get(k)
            if (v != null) Some(v)
            else None
        }
    }

    private val predCCFGCache = new CFGCache()
    private val succCCFGCache = new CFGCache()

    // result type of pred/succ determination
    // List[(computed annotation, given annotation, ast node)]
    type CFGRes = List[(FeatureExpr, FeatureExpr, AST)]
    type CFG = List[Opt[AST]]

    // during traversal of AST elements, we sometimes dig into elements, and don't want to get out again
    // we use the barrier list to add elements we do not want to get out again;
    // in case we determine where we come from.
    var barrier: List[AST] = List()

    // determines predecessor of a given element
    // results are cached for secondary evaluation
    def pred(source: Product, fm: FeatureModel, env: ASTEnv): CFG = {
        predCCFGCache.lookup(source) match {
            case Some(v) => v
            case None => {
                var oldres: CFGRes = List()
                val ctx = env.featureExpr(source)

                if (ctx isContradiction (fm)) return List()

                var newres: CFGRes = predHelper(source, ctx, oldres, fm, env)
                var changed = true

                while (changed) {
                    changed = false
                    oldres = newres
                    newres = List()

                    for (oldelem <- oldres) {
                        var add2newres: CFGRes = List()
                        oldelem._3 match {

                            case _: ReturnStatement if (!source.isInstanceOf[FunctionDef]) => add2newres = List()
                            case ReturnStatement(Some(CompoundStatementExpr(_))) => add2newres = List()

                            // a break statement shall appear only in or as a switch body or loop body
                            // a break statement terminates execution of the smallest enclosing switch or
                            // iteration statement (see standard [2])
                            // so as soon as we hit a break statement and the break statement belongs to the same loop as we do
                            // the break statement is not a valid predecessor
                            case b: BreakStatement => {
                                val b2b = findPriorASTElem2BreakStatement(b, env)

                                assert(b2b.isDefined, "missing loop to break statement!")
                                if (isPartOf(source, b2b.get)) add2newres = List()
                                else add2newres = List((env.featureExpr(b), env.featureExpr(b), b))
                            }
                            // a continue statement shall appear only in a loop body
                            // a continue statement causes a jump to the loop-continuation portion
                            // of the smallest enclosing iteration statement
                            case c: ContinueStatement => {
                                val a2c = findPriorASTElem2ContinueStatement(source, env)
                                val b2c = findPriorASTElem2ContinueStatement(c, env)

                                if (a2c.isDefined && b2c.isDefined && a2c.get.eq(b2c.get)) {
                                    a2c.get match {
                                        case WhileStatement(expr, _) if (isPartOf(source, expr)) => add2newres = List((env.featureExpr(c), env.featureExpr(c), c))
                                        case DoStatement(expr, _) if (isPartOf(source, expr)) => add2newres = List((env.featureExpr(c), env.featureExpr(c), c))
                                        case ForStatement(_, Some(expr2), None, _) if (isPartOf(source, expr2)) => add2newres = List((env.featureExpr(c), env.featureExpr(c), c))
                                        case ForStatement(_, _, Some(expr3), _) if (isPartOf(source, expr3)) => add2newres = List((env.featureExpr(c), env.featureExpr(c), c))
                                        case _ => add2newres = List()
                                    }
                                } else add2newres = List()
                            }

                            // goto statements
                            // in general only label statements can be the source of goto statements
                            // and only the ones that have the same name
                            case s@GotoStatement(Id(name)) => {
                                if (source.isInstanceOf[LabelStatement]) {
                                    val lname = source.asInstanceOf[LabelStatement].id.name
                                    if (name == lname) add2newres = List((env.featureExpr(s), env.featureExpr(s), s))
                                }
                            }

                            case _ => add2newres = List(oldelem)
                        }

                        // add only elements that are not in newres so far
                        for (addnew <- add2newres)
                            if (!newres.exists(_._3.eq(addnew._3))) newres ::= addnew
                    }
                }

                val res = newres.map(x => Opt(x._1, x._3))
                predCCFGCache.update(source, res)
                res
            }
        }
    }

    // determine context of new element based on the current result
    // the context is the not of all elements (or-d) together combined with the context of the source elemente
    // (ctx) and the context of the current element (curctx)
    private[crewrite] def getNewResCtx(curres: CFGRes, ctx: FeatureExpr, curctx: FeatureExpr) = {
        curres.map(_._1).fold(FeatureExprFactory.False)(_ or _).not() and ctx and curctx
    }

    // checks reference equality of e in a given struture t (either product or list)
    private def isPartOf(subterm: Product, term: Any): Boolean = {
        term match {
            case _: Product if (subterm.asInstanceOf[AnyRef].eq(term.asInstanceOf[AnyRef])) => true
            case l: List[_] => l.map(isPartOf(subterm, _)).exists(_ == true)
            case p: Product => p.productIterator.toList.map(isPartOf(subterm, _)).exists(_ == true)
            case _ => false
        }
    }

    def predHelper(source: Product, ctx: FeatureExpr, oldres: CFGRes, fm: FeatureModel, env: ASTEnv): CFGRes = {

        def findPriorJumpStatements(elem: AST): List[AST] = {
            elem match {
                case _: SwitchStatement => List()
                case e: AST => prevASTElems(e, env).filter({
                    case _: CaseStatement => true
                    case _: DefaultStatement => true
                    case _ => false
                }) ++ findPriorJumpStatements(parentAST(e, env))
                case _ => List()
            }
        }

        // helper method to handle a switch, source is a case or a default statement
        def handleSwitch(t: AST) = {
            val prior_switch = findPriorASTElem[SwitchStatement](t, env)
            assert(prior_switch.isDefined, "default or case statements should always occur withing a switch definition")
            prior_switch.get match {
                case SwitchStatement(expr, _) => {
                    val r1 = getExprPred(expr, ctx, oldres, fm, env)

                    // do not determine the pred of t in case no case statement precedes t
                    // switch (e) {
                    //   int a;
                    //   case 1;
                    val r2 = {
                        // we count given case t itself also
                        val prevcases = findPriorJumpStatements(t).filterNot(x => x.eq(t))
                        if (prevcases.size > 0) getStmtPred(t, ctx, oldres, fm, env)
                        else List()
                    }
                    r1 ++ r2
                }
            }
        }

        source match {
            case t: CaseStatement => handleSwitch(t)
            case t: DefaultStatement => handleSwitch(t)

            // pred elements of an if statement are
            // all bodys of existing elifs
            // body of existing else
            // body of existing then
            // if no else exists then go for conditions of elifs and condition itself
            case t@IfStatement(condition, thenBranch, elifs, elseBranch) => {
                var res: CFGRes = List()
                val elifsrc = elifs.reverse.map(childAST)

                if (!elifs.isEmpty) {
                    if (elseBranch.isEmpty) {
                        for (elif <- elifsrc) {

                            if (predComplete(ctx, res, fm)) {}
                            else {
                                elif match {
                                    case ElifStatement(elif_condition, _) => {
                                        val newres = getCondExprPred(elif_condition, ctx, oldres, fm, env)

                                        for (n <- newres) {
                                            if (res.map(_._2).fold(FeatureExprFactory.False)(_ or _).not() and n._2 isContradiction (fm)) {}
                                            else res ++= List(n)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    for (elif <- elifsrc) {
                        val eliffexp = env.featureExpr(elif)
                        if (!(eliffexp and ctx isContradiction (fm))) {
                            elif match {
                                case ElifStatement(_, elif_thenBranch) => res ++= getCondStmtPred(elif_thenBranch, ctx, oldres, fm, env)
                                case _ => assert(assertion = false, message = "expected elif statement")
                            }
                        }
                    }
                }

                if (elseBranch.isDefined) res ++= getCondStmtPred(elseBranch.get, ctx, oldres, fm, env)
                else if (!predComplete(ctx, res, fm)) {
                    val newres = getCondExprPred(condition, ctx, oldres, fm, env)

                    for (n <- newres) {
                        if (res.map(_._2).fold(FeatureExprFactory.False)(_ or _).not() and n._2 isContradiction (fm)) {}
                        else res ++= List(n)
                    }
                }

                res ++= getCondStmtPred(thenBranch, ctx, oldres, fm, env)

                res
            }

            // all break statements are possible predecessors
            // furthermore if there is no default, we might just have fallen through all case statements (i.e. no break
            // statements available)
            case t@SwitchStatement(expr, s) => {
                val lbreaks = filterBreakStatements(s, ctx, fm, env)
                val ldefaults = filterDefaultStatements(s, ctx, fm, env)

                var res = oldres ++ lbreaks

                if (ldefaults.isEmpty) {
                    res ++= getExprPred(expr, ctx, oldres, fm, env)
                    res ++= getCondStmtPred(s, ctx, oldres, fm, env)
                } else {
                    res ++= getCondStmtPred(s, ctx, oldres, fm, env)
                }
                res
            }

            case t@WhileStatement(expr, s) => getExprPred(expr, ctx, oldres, fm, env) ++ filterBreakStatements(s, ctx, fm, env)
            case t@DoStatement(expr, s) => getExprPred(expr, ctx, oldres, fm, env) ++ filterBreakStatements(s, ctx, fm, env)
            case t@ForStatement(_, Some(expr2), _, s) => getExprPred(expr2, ctx, oldres, fm, env) ++
                    filterBreakStatements(s, ctx, fm, env)
            case t@ForStatement(_, _, _, s) => oldres ++ filterBreakStatements(s, ctx, fm, env)

            case c@CompoundStatement(innerStatements) => {
                if (!parentAST(c, env).isInstanceOf[FunctionDef]) barrier ::= c
                val res = getCompoundPred(innerStatements.reverse.map(_.entry), c, ctx, oldres, fm, env)
                barrier = barrier.filterNot(x => x.eq(c))
                res
            }

            case t@LabelStatement(Id(n), _) => {
                findPriorASTElem[FunctionDef](t, env) match {
                    case None => assert(assertion = false, message = "label statements should always occur within a function definition"); List()
                    case Some(f) => {
                        val l_gotos = filterASTElems[GotoStatement](f, env.featureExpr(t), env)
                        // filter gotostatements with the same id as the labelstatement
                        // and all gotostatements with dynamic target
                        val l_gotos_filtered = l_gotos.filter({
                            case GotoStatement(Id(name)) => if (n == name) true else false
                            case _ => true
                        })
                        val l_preds = getStmtPred(t, ctx, oldres, fm, env)
                        l_gotos_filtered.map(x => (env.featureExpr(x), env.featureExpr(x), x)) ++ l_preds
                    }
                }
            }

            case o: Opt[_] => predHelper(childAST(o), ctx, oldres, fm, env)
            case c: Conditional[_] => predHelper(childAST(c), ctx, oldres, fm, env)

            case f@FunctionDef(_, _, _, CompoundStatement(List())) => {
                val newresctx = getNewResCtx(oldres, ctx, env.featureExpr(f))
                if (newresctx isContradiction (fm)) oldres
                else (newresctx, env.featureExpr(f), f) :: oldres
            }
            case f@FunctionDef(_, _, _, stmt) => predHelper(childAST(stmt), ctx, oldres, fm, env) ++
                    filterReturnStatements(stmt, ctx, oldres, fm, env)

            case s: Statement => getStmtPred(s, ctx, oldres, fm, env)
            case _ => followPred(source, ctx, oldres, fm, env)
        }
    }

    def succ(source: AST, fm: FeatureModel, env: ASTEnv): CFG = {
        succCCFGCache.lookup(source) match {
            case Some(v) => v
            case None => {
                var newres: CFGRes = List()
                val ctx = env.featureExpr(source)

                if (ctx isContradiction (fm)) return List()

                newres = succHelper(source, ctx, newres, fm, env)

                val res = newres.map(x => Opt(x._1, x._3))
                succCCFGCache.update(source, res)
                res
            }
        }
    }

    // checks whether a given AST element is a succ instruction or not
    private def isCFGInstructionSucc(elem: AST): Boolean = {
        elem match {
            case _: ForStatement => false
            case _: WhileStatement => false
            case _: DoStatement => false
            case _: CompoundStatement => false
            case _: CompoundStatementExpr => false
            case _: IfStatement => false
            case _: ElifStatement => false
            case _: SwitchStatement => false
            case ReturnStatement(Some(CompoundStatementExpr(_))) => false
            case _ => true
        }
    }

    // checks whether a given AST element is a succ instruction or not
    private def isCFGInstructionPred(elem: AST): Boolean = {
        elem match {
            case _: ForStatement => false
            case _: WhileStatement => false
            case _: DoStatement => false
            case _: CompoundStatement => false
            case _: CompoundStatementExpr => false
            case _: IfStatement => false
            case _: ElifStatement => false
            case _: SwitchStatement => false
            case _ => true
        }
    }

    // the predecessor determination is complete, if
    // all result elements combined with or are equivalent to element x with ctx: equivalence ensures that result elements
    // would have been reached before x
    private def predComplete(ctx: FeatureExpr, curres: CFGRes, fm: FeatureModel): Boolean = {
        val curresfexp = curres.map(_._2)
        curresfexp.exists(x => x.not() and ctx isContradiction (fm))
    }

    private def predCompleteBlock(ctx: FeatureExpr, curres: CFGRes): Boolean = {
        val curresctx = curres.map(_._1).fold(FeatureExprFactory.False)(_ or _)
        curresctx equivalentTo ctx
    }

    // checks whether the current result list is complete
    private def succComplete(ctx: FeatureExpr, curres: CFGRes): Boolean = {
        val curresctx = curres.map(_._1).fold(FeatureExprFactory.False)(_ or _)
        ctx equivalentTo curresctx
    }

    private def succHelper(source: AST, ctx: FeatureExpr, oldres: CFGRes, fm: FeatureModel, env: ASTEnv): CFGRes = {
        source match {
            // ENTRY element
            case f@FunctionDef(_, _, _, CompoundStatement(List())) => List((env.featureExpr(f), env.featureExpr(f), f))
            case f@FunctionDef(_, _, _, stmt) => oldres ++ succHelper(stmt, ctx, oldres, fm, env)

            // EXIT element
            case t@ReturnStatement(Some(c: CompoundStatementExpr)) => getExprSucc(c, ctx, oldres, fm, env)
            case t@ReturnStatement(retExpr) => {
                findPriorASTElem[FunctionDef](t, env) match {
                    case None => assert(assertion = false, message = "return statement should always occur within a function statement"); List()
                    case Some(f) => {
                        val newresctx = getNewResCtx(oldres, ctx, env.featureExpr(f))
                        val res = if (newresctx isContradiction (fm)) oldres
                        else (newresctx, env.featureExpr(f), f) :: oldres

                        if (retExpr.isDefined) findMethodCalls(retExpr.get, env, oldres, ctx, res)
                        else res
                    }
                }
            }

            case c@CompoundStatement(l) => getCompoundSucc(l.map(_.entry), c, ctx, oldres, fm, env)

            // loop statements
            case ForStatement(None, Some(expr2), None, One(EmptyStatement())) => getExprSucc(expr2, ctx, oldres, fm, env)
            case ForStatement(None, Some(expr2), None, One(CompoundStatement(List()))) => getExprSucc(expr2, ctx, oldres, fm, env)
            case ForStatement(expr1, expr2, expr3, s) => {
                if (expr1.isDefined) getExprSucc(expr1.get, ctx, oldres, fm, env)
                else if (expr2.isDefined) getExprSucc(expr2.get, ctx, oldres, fm, env)
                else getCondStmtSucc(s, ctx, oldres, fm, env)
            }
            case WhileStatement(expr, One(EmptyStatement())) => getExprSucc(expr, ctx, oldres, fm, env)
            case WhileStatement(expr, One(CompoundStatement(List()))) => getExprSucc(expr, ctx, oldres, fm, env)
            case WhileStatement(expr, _) => getExprSucc(expr, ctx, oldres, fm, env)
            case DoStatement(expr, One(CompoundStatement(List()))) => getExprSucc(expr, ctx, oldres, fm, env)
            case DoStatement(expr, s) => {
                val rs = getCondStmtSucc(s, ctx, oldres, fm, env)
                val re = if (!succComplete(ctx, rs)) getExprSucc(expr, ctx, rs, fm, env)
                else List()
                rs ++ re
            }

            // conditional statements
            case t@IfStatement(condition, _, _, _) => getCondExprSucc(condition, ctx, oldres, fm, env)
            case t@ElifStatement(condition, _) => getCondExprSucc(condition, ctx, oldres, fm, env)
            case SwitchStatement(expr, _) => getExprSucc(expr, ctx, oldres, fm, env)

            case t@BreakStatement() => {
                val e2b = findPriorASTElem2BreakStatement(t, env)
                assert(e2b.isDefined, "break statement should always occur within a for, do-while, while, or switch statement")
                getStmtSucc(e2b.get, ctx, oldres, fm, env)
            }
            case t@ContinueStatement() => {
                val e2c = findPriorASTElem2ContinueStatement(t, env)
                assert(e2c.isDefined, "continue statement should always occur within a for, do-while, or while statement")
                e2c.get match {
                    case t@ForStatement(_, expr2, expr3, s) => {
                        if (expr3.isDefined) getExprSucc(expr3.get, ctx, oldres, fm, env)
                        else if (expr2.isDefined) getExprSucc(expr2.get, ctx, oldres, fm, env)
                        else getCondStmtSucc(s, ctx, oldres, fm, env)
                    }
                    case WhileStatement(expr, _) => getExprSucc(expr, ctx, oldres, fm, env)
                    case DoStatement(expr, _) => getExprSucc(expr, ctx, oldres, fm, env)
                    case _ => List()
                }
            }
            case t@GotoStatement(Id(l)) => {
                findPriorASTElem[FunctionDef](t, env) match {
                    case None => assert(assertion = false, message = "goto statement should always occur within a function definition"); oldres
                    case Some(f) => {
                        val l_list = filterAllASTElems[LabelStatement](f, env.featureExpr(t), env).filter(_.id.name == l)
                        if (l_list.isEmpty) getStmtSucc(t, ctx, oldres, fm, env)
                        else oldres ++ l_list.map(x => (env.featureExpr(x), env.featureExpr(x), x))
                    }
                }
            }
            // in case we have an indirect goto dispatch all goto statements
            // within the function (this is our invariant) are possible targets of this goto
            // so fetch the function statement and filter for all label statements
            case t@GotoStatement(PointerDerefExpr(_)) => {
                findPriorASTElem[FunctionDef](t, env) match {
                    case None => assert(assertion = false, message = "goto statement should always occur within a function definition"); oldres
                    case Some(f) => {
                        val l_list = filterAllASTElems[LabelStatement](f, env.featureExpr(t))
                        if (l_list.isEmpty) getStmtSucc(t, ctx, oldres, fm, env)
                        else oldres ++ l_list.map(x => (env.featureExpr(x), env.featureExpr(x), x))
                    }
                }
            }

            case t: DefaultStatement => getStmtSucc(t, ctx, oldres, fm, env)

            case t: Statement => {
                val condexprs = filterAllASTElems[ConditionalExpr](t)
                if (condexprs.size > 0) {
                    val fexpcondexprs = env.featureExpr(condexprs.head.condition)
                    val newresctx = getNewResCtx(oldres, ctx, fexpcondexprs)
                    if (newresctx isContradiction (fm)) List()
                    else List((newresctx, fexpcondexprs, condexprs.head.condition))
                }
                else getStmtSucc(t, ctx, oldres, fm, env)
            }
            case t => followSucc(t, ctx, oldres, fm, env)
        }
    }

    private def getCondStmtSucc(c: Conditional[Statement], ctx: FeatureExpr, oldres: CFGRes, fm: FeatureModel, env: ASTEnv): CFGRes = {
        c match {
            case Choice(_, thenBranch, elseBranch) =>
                getCondStmtSucc(thenBranch, ctx, oldres, fm, env) ++ getCondStmtSucc(elseBranch, ctx, oldres, fm, env)
            case One(c@CompoundStatement(l)) => {
                barrier ::= c
                val res = getCompoundSucc(l.map(_.entry), c, ctx, oldres, fm, env)
                barrier = barrier.filterNot(x => x.eq(c))
                res
            }
            case One(s: Statement) => {
                barrier ::= s
                val res = getCompoundSucc(List(s), s, ctx, oldres, fm, env)
                barrier = barrier.filterNot(x => x.eq(s))
                res
            }
        }
    }

    private def getCondStmtPred(cond: Conditional[_], ctx: FeatureExpr, oldres: CFGRes, fm: FeatureModel, env: ASTEnv): CFGRes = {
        cond match {
            case Choice(_, thenBranch, elseBranch) =>
                getCondStmtPred(thenBranch, ctx, oldres, fm, env) ++ getCondStmtPred(elseBranch, ctx, oldres, fm, env)
            case One(c@CompoundStatement(l)) => {
                barrier ::= c
                val res = getCompoundPred(l.reverse.map(_.entry), c, ctx, oldres, fm, env)
                barrier = barrier.filterNot(x => x.eq(c))
                res
            }
            case One(s: Statement) => {
                barrier ::= s
                val res = getCompoundPred(List(s), s, ctx, oldres, fm, env)
                barrier = barrier.filterNot(x => x.eq(s))
                res
            }
        }
    }

    // necessary hook for InterCFG implementation; provides means to resolve function calls for inter procedural control
    // flow computation
    private[crewrite] def findMethodCalls(t: AST, env: ASTEnv, oldres: CFGRes, ctx: FeatureExpr, _res: CFGRes): CFGRes = {
        _res
    }

    private[crewrite] def getExprSucc(exp: Expr, ctx: FeatureExpr, oldres: CFGRes, fm: FeatureModel, env: ASTEnv): CFGRes = {
        exp match {
            case c@CompoundStatementExpr(CompoundStatement(innerStatements)) => {
                if (barrierExists(c)) {
                    getCompoundSucc(innerStatements.map(_.entry), c, ctx, oldres, fm, env)
                } else {
                    barrier ::= c
                    val res = getCompoundSucc(innerStatements.map(_.entry), c, ctx, oldres, fm, env)
                    barrier = barrier.filterNot(e => e.eq(c))
                    res
                }
            }
            case _ => {
                // check if exp is part of an ConditionalExpr

                val fexpexp = env.featureExpr(exp)
                if (!(ctx and fexpexp isContradiction (fm))) oldres ++ {
                    val newresctx = getNewResCtx(oldres, ctx, fexpexp)
                    if (newresctx isContradiction (fm)) List()
                    else List((newresctx, fexpexp, exp))
                }
                else oldres
            }
        }
    }

    private def getCondExprSucc(cexp: Conditional[Expr], ctx: FeatureExpr, oldres: CFGRes, fm: FeatureModel, env: ASTEnv): CFGRes = {
        cexp match {
            case One(value) => getExprSucc(value, ctx, oldres, fm, env)
            case Choice(_, thenBranch, elseBranch) =>
                getCondExprSucc(thenBranch, ctx, oldres, fm, env) ++
                        getCondExprSucc(elseBranch, ctx, oldres, fm, env)
        }
    }

    private def getExprPred(exp: Expr, ctx: FeatureExpr, oldres: CFGRes, fm: FeatureModel, env: ASTEnv): CFGRes = {
        exp match {
            case c@CompoundStatementExpr(CompoundStatement(innerStatements)) => {
                if (barrierExists(c)) {
                    getCompoundPred(innerStatements.reverse.map(_.entry), c, ctx, oldres, fm, env)
                } else {
                    barrier ::= c
                    val res = getCompoundPred(innerStatements.reverse.map(_.entry), c, ctx, oldres, fm, env)
                    barrier = barrier.filterNot(e => e.eq(c))
                    res
                }
            }
            case _ => {
                val fexpexp = env.featureExpr(exp)
                if (!(fexpexp and ctx isContradiction (fm))) {
                    if (oldres.map(_._1).exists(x => x equivalentTo fexpexp)) oldres
                    else oldres ++ {
                        val newresctx = getNewResCtx(oldres, ctx, fexpexp)
                        if (newresctx isContradiction (fm)) List()
                        else List((newresctx, fexpexp, exp))
                    }
                }
                else oldres
            }
        }
    }

    private def getCondExprPred(cexp: Conditional[Expr], ctx: FeatureExpr, oldres: CFGRes, fm: FeatureModel, env: ASTEnv): CFGRes = {
        cexp match {
            case One(value) => getExprPred(value, ctx, oldres, fm, env)
            case Choice(_, thenBranch, elseBranch) =>
                getCondExprPred(thenBranch, ctx, oldres, fm, env) ++
                        getCondExprPred(elseBranch, ctx, oldres, fm, env)
        }
    }

    private def getReturnStatementSucc(t: AST, ctx: FeatureExpr, oldres: CFGRes, fm: FeatureModel, env: ASTEnv): CFGRes = {
        findPriorASTElem[FunctionDef](t, env) match {
            case None => assert(assertion = false, message = "return statement should always occur within a function statement"); List()
            case Some(f) => oldres ++ {
                val newresctx = getNewResCtx(oldres, ctx, env.featureExpr(f))
                if (newresctx isContradiction (fm)) List()
                else List((newresctx, env.featureExpr(f), f))
            }
        }
    }

    private def barrierExists(elem: Product): Boolean =
        (elem.isInstanceOf[AST]) && (barrier.exists(x => x.eq(elem.asInstanceOf[AST])))

    // handling of successor determination of nested structures, such as for, while, ... and next element in a list
    // of statements
    private def followSucc(nested_ast_elem: Product, ctx: FeatureExpr, oldres: CFGRes, fm: FeatureModel, env: ASTEnv): CFGRes = {

        if (barrierExists(nested_ast_elem)) return oldres

        nested_ast_elem match {
            case t: ReturnStatement => getReturnStatementSucc(t, ctx, oldres, fm, env)
            case _ => {
                val surrounding_parent = parentAST(nested_ast_elem, env)
                surrounding_parent match {
                    // loops
                    case t@ForStatement(Some(expr1), expr2, _, s) if (isPartOf(nested_ast_elem, expr1)) =>
                        if (expr2.isDefined) getExprSucc(expr2.get, ctx, oldres, fm, env)
                        else getCondStmtSucc(s, ctx, oldres, fm, env)
                    case t@ForStatement(_, Some(expr2), expr3, s) if (isPartOf(nested_ast_elem, expr2)) => {
                        val rt = getStmtSucc(t, ctx, oldres, fm, env)
                        val rs = getCondStmtSucc(s, ctx, oldres, fm, env)

                        if (!succComplete(ctx, rs)) {
                            val re = if (expr3.isDefined) getExprSucc(expr3.get, ctx, oldres, fm, env)
                            else getExprSucc(expr2, ctx, oldres, fm, env)
                            rt ++ rs ++ re
                        } else {
                            rt ++ rs
                        }
                    }
                    case t@ForStatement(_, expr2, Some(expr3), s) if (isPartOf(nested_ast_elem, expr3)) =>
                        if (expr2.isDefined) getExprSucc(expr2.get, ctx, oldres, fm, env)
                        else getCondStmtSucc(s, ctx, oldres, fm, env)
                    case t@ForStatement(_, expr2, expr3, s) if (isPartOf(nested_ast_elem, s)) => {
                        if (expr3.isDefined) getExprSucc(expr3.get, ctx, oldres, fm, env)
                        else if (expr2.isDefined) getExprSucc(expr2.get, ctx, oldres, fm, env)
                        else getCondStmtSucc(s, ctx, oldres, fm, env)
                    }
                    case t@WhileStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) => {
                        val rt = getStmtSucc(t, ctx, oldres, fm, env)
                        val rs = getCondStmtSucc(s, ctx, oldres, fm, env)
                        val re = if (!succComplete(ctx, rs)) getExprSucc(expr, ctx, rs, fm, env)
                        else List()
                        rs ++ re ++ rt
                    }
                    case WhileStatement(expr, _) => getExprSucc(expr, ctx, oldres, fm, env)
                    case t@DoStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) => {
                        val rs = getCondStmtSucc(s, ctx, oldres, fm, env)
                        val rt = getStmtSucc(t, ctx, oldres, fm, env)
                        val re = if (!succComplete(ctx, rs)) getExprSucc(expr, ctx, rs, fm, env)
                        else List()
                        rs ++ re ++ rt
                    }

                    case DoStatement(expr, s) => getExprSucc(expr, ctx, oldres, fm, env)

                    // conditional statements
                    // we are in the condition of the if statement
                    case t@IfStatement(condition, thenBranch, elifs, elseBranch) if (isPartOf(nested_ast_elem, condition)) => {
                        var res = oldres

                        if (!elifs.isEmpty) {
                            for (e <- elifs.map(childAST)) {
                                if (succComplete(ctx, res)) {}
                                else {
                                    e match {
                                        case ce@ElifStatement(elif_condition, _) => {
                                            val newres = getCondExprSucc(elif_condition, ctx, oldres, fm, env)

                                            for (n <- newres) {
                                                if (res.map(_._2).fold(FeatureExprFactory.False)(_ or _).not() and n._2 isContradiction (fm)) {}
                                                else res ++= List(n)
                                            }
                                        }
                                        case _ => assert(assertion = true, message = "expected elif statement")
                                    }
                                }
                            }
                        }

                        if (!succComplete(ctx, res)) {
                            if (elseBranch.isDefined) res ++= getCondStmtSucc(elseBranch.get, ctx, oldres, fm, env)
                            else res ++= getStmtSucc(t, ctx, res, fm, env)
                        }
                        res ++= getCondStmtSucc(thenBranch, ctx, oldres, fm, env)
                        res
                    }

                    // either go to next ElifStatement, ElseBranch, or next statement of the surrounding IfStatement
                    case t@ElifStatement(condition, thenBranch) if (isPartOf(nested_ast_elem, condition)) => {
                        var res = oldres
                        val snexts = nextASTElems(t, env).tail

                        for (e <- snexts) {
                            if (succComplete(ctx, res)) {}
                            else {
                                e match {
                                    case ce@ElifStatement(elif_condition, _) => {
                                        val newres = getCondExprSucc(elif_condition, ctx, oldres, fm, env)

                                        for (n <- newres) {
                                            if (res.map(_._2).fold(FeatureExprFactory.False)(_ or _).not() and n._2 isContradiction (fm)) {}
                                            else res ++= List(n)
                                        }
                                    }
                                    case _ => assert(assertion = true, message = "expected elif statement")
                                }
                            }
                        }

                        if (!succComplete(ctx, res)) {
                            parentAST(t, env) match {
                                case tp@IfStatement(_, _, _, None) => res ++= getStmtSucc(tp, ctx, res, fm, env)
                                case IfStatement(_, _, _, Some(elseBranch)) => res ++= getCondStmtSucc(elseBranch, ctx, res, fm, env)
                            }
                        }

                        res ++ getCondStmtSucc(thenBranch, ctx, oldres, fm, env)
                    }
                    case t: ElifStatement => followSucc(t, ctx, oldres, fm, env)

                    // the switch statement behaves like a dynamic goto statement;
                    // based on the expression we jump to one of the case statements or default statements
                    // after the jump the case/default statements do not matter anymore
                    // when hitting a break statement, we jump to the end of the switch
                    case t@SwitchStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) => {
                        var res: CFGRes = oldres
                        if (isPartOf(nested_ast_elem, expr)) {
                            res ++= filterCaseStatements(s, env.featureExpr(t), fm, env)
                            val dcase = filterDefaultStatements(s, env.featureExpr(t), fm, env)

                            if (dcase.isEmpty) res ++= getStmtSucc(t, ctx, oldres, fm, env)
                            else res ++= dcase
                        }
                        res
                    }

                    case t@ConditionalExpr(condition, thenExpr, elseExpr) => {
                        // condition
                        if (isPartOf(nested_ast_elem, condition)) {
                            (if (thenExpr.isDefined) getExprSucc(thenExpr.get, ctx, oldres, fm, env) else List()) ++
                                    getExprSucc(elseExpr, ctx, oldres, fm, env)
                        } else {
                            followSucc(t, ctx, oldres, fm, env)
                        }
                    }

                    case t: Expr => followSucc(t, ctx, oldres, fm, env)
                    case t: ReturnStatement => getReturnStatementSucc(t, ctx, oldres, fm, env)
                    case t: Statement => {
                        var res = getStmtSucc(t, ctx, oldres, fm, env)
                        res = findMethodCalls(t, env, oldres, ctx, res)
                        res
                    }

                    //ChK: deactivate conditional expressions for now, since they do not contain variability and are
                    //not fully implemented anyway
                    //          case t@ConditionalExpr(condition, thenExpr, elseExpr) => {
                    //            // condition
                    //            if (isPartOf(nested_ast_elem, condition)) {
                    //              (if (thenExpr.isDefined) getExprSucc(thenExpr.get, ctx, oldres, fm, env) else List()) ++
                    //              getExprSucc(elseExpr, ctx, oldres, fm, env)
                    //            } else {
                    //              followSucc(t, ctx, oldres, fm, env)
                    //            }
                    //          }

                    case t: FunctionDef => oldres ++ List((env.featureExpr(t), env.featureExpr(t), t))
                    case _ => List()
                }
            }
        }
    }

    // method to catch surrounding ast element, which precedes the given nested_ast_element
    private def followPred(nested_ast_elem: Product, ctx: FeatureExpr, oldres: CFGRes, fm: FeatureModel, env: ASTEnv): CFGRes = {

        def handleSwitch(t: AST): CFGRes = {
            val prior_switch = findPriorASTElem[SwitchStatement](t, env)
            assert(prior_switch.isDefined, "default statement without surrounding switch")
            prior_switch.get match {
                case SwitchStatement(expr, _) => {
                    val lconds = getExprPred(expr, ctx, oldres, fm, env)
                    if (env.previous(t) != null) lconds ++ getStmtPred(t, ctx, oldres, fm, env)
                    else {
                        val tparent = parentAST(t, env)
                        if (tparent.isInstanceOf[CaseStatement]) {
                            val newresctx = getNewResCtx(oldres, ctx, env.featureExpr(tparent))
                            if (newresctx isContradiction (fm)) lconds
                            else (newresctx, env.featureExpr(tparent), tparent) :: lconds
                        }
                        else lconds ++ getStmtPred(tparent, ctx, oldres, fm, env)
                    }
                }
            }
        }

        if (barrierExists(nested_ast_elem)) {
            parentAST(nested_ast_elem, env) match {
                case _: DoStatement =>
                case _ => return oldres
            }
        }

        nested_ast_elem match {

            // case or default statements belong only to switch statements
            case t: CaseStatement => handleSwitch(t)
            case t: DefaultStatement => handleSwitch(t)

            case _ => {
                val surrounding_parent = parentAST(nested_ast_elem, env)
                surrounding_parent match {

                    // loop statements

                    // for statements consists of of (init, break, inc, s)
                    // we are in one of these elements
                    // init
                    case t@ForStatement(Some(expr1), _, _, _) if (isPartOf(nested_ast_elem, expr1)) =>
                        getStmtPred(t, ctx, oldres, fm, env)
                    // inc
                    case t@ForStatement(_, Some(expr2), Some(expr3), s) if (isPartOf(nested_ast_elem, expr3)) => {
                        val rs = getCondStmtPred(s, ctx, oldres, fm, env)
                        val rf = filterContinueStatements(s, env.featureExpr(t), fm, env)

                        if (!predCompleteBlock(ctx, rs)) {
                            rs ++ getExprPred(expr2, ctx, rs, fm, env) ++ rf
                        } else {
                            rs ++ rf
                        }
                    }
                    // break
                    case t@ForStatement(None, Some(expr2), None, One(CompoundStatement(List()))) => {
                        val newresctx = getNewResCtx(oldres, ctx, env.featureExpr(expr2))
                        if (newresctx isContradiction (fm)) getStmtPred(t, ctx, oldres, fm, env)
                        else (newresctx, env.featureExpr(expr2), expr2) :: getStmtPred(t, ctx, oldres, fm, env)
                    }
                    case t@ForStatement(expr1, Some(expr2), expr3, s) if (isPartOf(nested_ast_elem, expr2)) => {
                        var res = oldres
                        if (expr1.isDefined) res ++= getExprPred(expr1.get, ctx, oldres, fm, env)
                        else res ++= getStmtPred(t, ctx, oldres, fm, env)
                        if (expr3.isDefined) res ++= getExprPred(expr3.get, ctx, oldres, fm, env)
                        else {
                            res ++= getCondStmtPred(s, ctx, oldres, fm, env)
                            res ++= filterContinueStatements(s, env.featureExpr(t), fm, env)
                        }
                        res
                    }
                    // s
                    case t@ForStatement(expr1, expr2, expr3, s) if (isPartOf(nested_ast_elem, s)) =>
                        if (expr2.isDefined) getExprPred(expr2.get, ctx, oldres, fm, env)
                        else if (expr3.isDefined) getExprPred(expr3.get, ctx, oldres, fm, env)
                        else {
                            var res = oldres
                            if (expr1.isDefined) res = res ++ getExprPred(expr1.get, ctx, oldres, fm, env)
                            else res = getStmtPred(t, ctx, oldres, fm, env) ++ res
                            res = res ++ getCondStmtPred(s, ctx, oldres, fm, env)
                            res
                        }

                    // while statement consists of (expr, s)
                    // special case; we handle empty compound statements here directly because otherwise we do not terminate
                    case t@WhileStatement(expr, One(CompoundStatement(List()))) if (isPartOf(nested_ast_elem, expr)) => {
                        val newresctx = getNewResCtx(oldres, ctx, env.featureExpr(expr))
                        if (newresctx isContradiction (fm)) getStmtPred(t, ctx, oldres, fm, env)
                        else (newresctx, env.featureExpr(expr), expr) :: getStmtPred(t, ctx, oldres, fm, env)
                    }
                    case t@WhileStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) =>
                        (getStmtPred(t, ctx, oldres, fm, env) ++ getCondStmtPred(s, ctx, oldres, fm, env) ++
                                filterContinueStatements(s, env.featureExpr(t), fm, env))
                    case t@WhileStatement(expr, _) => {
                        if (nested_ast_elem.isInstanceOf[AST] && nested_ast_elem.asInstanceOf[AST].eq(expr)) getStmtPred(t, ctx, oldres, fm, env)
                        else getExprPred(expr, ctx, oldres, fm, env)
                    }

                    // do statement consists of (expr, s)
                    // special case: we handle empty compound statements here directly because otherwise we do not terminate
                    case t@DoStatement(expr, One(CompoundStatement(List()))) if (isPartOf(nested_ast_elem, expr)) => {
                        val newresctx = getNewResCtx(oldres, ctx, env.featureExpr(expr))
                        if (newresctx isContradiction (fm)) getStmtPred(t, ctx, oldres, fm, env)
                        else (newresctx, env.featureExpr(expr), expr) :: getStmtPred(t, ctx, oldres, fm, env)
                    }
                    case t@DoStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) =>
                        getCondStmtPred(s, ctx, oldres, fm, env) ++ filterContinueStatements(s, env.featureExpr(t), fm, env)
                    case t@DoStatement(expr, s) => {
                        if (isPartOf(nested_ast_elem, expr)) getCondStmtPred(s, ctx, oldres, fm, env)
                        else getExprPred(expr, ctx, oldres, fm, env) ++ getStmtPred(t, ctx, oldres, fm, env)
                    }

                    // conditional statements
                    // if statement: control flow comes either out of:
                    // elseBranch: elifs + condition is the result
                    // elifs: rest of elifs + condition
                    // thenBranch: condition
                    case t@IfStatement(condition, thenBranch, elifs, elseBranch) => {
                        if (isPartOf(nested_ast_elem, condition)) getStmtPred(t, ctx, oldres, fm, env)
                        else if (isPartOf(nested_ast_elem, thenBranch)) getCondExprPred(condition, ctx, oldres, fm, env)
                        else if (isPartOf(nested_ast_elem, elseBranch)) {
                            var res = oldres

                            for (e <- elifs.reverse.map(childAST)) {
                                if (predComplete(ctx, res, fm)) {}
                                else {
                                    e match {
                                        case ElifStatement(elif_condition, _) => {
                                            val newres = getCondExprPred(elif_condition, ctx, oldres, fm, env)

                                            for (n <- newres) {
                                                if (res.map(_._2).fold(FeatureExprFactory.False)(_ or _).not() and n._2 isContradiction (fm)) {}
                                                else res ++= List(n)
                                            }
                                        }
                                    }
                                }
                            }

                            if (!predComplete(ctx, res, fm)) {
                                val newres = getCondExprPred(condition, ctx, oldres, fm, env)

                                for (n <- newres) {
                                    if (res.map(_._2).fold(FeatureExprFactory.False)(_ or _).not() and n._2 isContradiction (fm)) {}
                                    else res ++= List(n)
                                }
                            }
                            res
                        } else {
                            getStmtPred(nested_ast_elem.asInstanceOf[AST], ctx, oldres, fm, env)
                        }
                    }

                    // pred of thenBranch is the condition itself
                    // and if we are in condition, we strike for a previous elifstatement or the if
                    case t@ElifStatement(condition, thenBranch) => {
                        if (isPartOf(nested_ast_elem, condition)) {
                            var res = oldres
                            val elifs = prevASTElems(t, env).reverse.tail

                            for (e <- elifs) {
                                if (predCompleteBlock(ctx, res)) {}
                                else {
                                    e match {
                                        case ce@ElifStatement(elif_condition, _) => {
                                            val newres = getCondExprPred(elif_condition, ctx, oldres, fm, env)

                                            for (n <- newres) {
                                                if (res.map(_._2).fold(FeatureExprFactory.False)(_ or _).not() and n._2 isContradiction (fm)) {}
                                                else res ++= List(n)
                                            }
                                        }
                                    }
                                }
                            }

                            if (!predCompleteBlock(ctx, res)) {
                                parentAST(t, env) match {
                                    case tp@IfStatement(if_condition, _, _, _) => res ++= getCondExprPred(if_condition, ctx, oldres, fm, env)
                                }
                            }
                            res
                        }
                        else getCondExprPred(condition, ctx, oldres, fm, env)
                    }

                    case t@SwitchStatement(expr, s) => {
                        if (isPartOf(nested_ast_elem, s)) getExprPred(expr, ctx, oldres, fm, env)
                        else getStmtPred(t, ctx, oldres, fm, env)
                    }

                    case t: CaseStatement => oldres ++ {
                        val newresctx = getNewResCtx(oldres, ctx, env.featureExpr(t))
                        if (newresctx isContradiction (fm)) List()
                        else List((newresctx, env.featureExpr(t), t))
                    }

                    // pred of default is either the expression of the switch, which is
                    // returned by handleSwitch, or a previous statement (e.g.,
                    // switch (exp) {
                    // ...
                    // label1:
                    // default: ...)
                    // as part of a fall through (sequence of statements without a break and that we catch
                    // with getStmtPred
                    case t: DefaultStatement => handleSwitch(t) ++ getStmtPred(t, ctx, oldres, fm, env)

                    case t: CompoundStatementExpr => followPred(t, ctx, oldres, fm, env)

                    case t: Statement => getStmtPred(t, ctx, oldres, fm, env)
                    case t: FunctionDef => {
                        val ffexp = env.featureExpr(t)
                        if (predComplete(ctx, oldres, fm)) oldres
                        else oldres ++ {
                            val newresctx = getNewResCtx(oldres, ctx, ffexp)
                            if (newresctx isContradiction (fm)) List()
                            else List((newresctx, ffexp, t))
                        }
                    }
                    case _ => oldres
                }
            }
        }
    }

    // method to find a prior loop statement that belongs to a given break statement
    private def findPriorASTElem2BreakStatement(a: Product, env: ASTEnv): Option[AST] = {
        val aparent = env.parent(a)
        aparent match {
            case t: ForStatement => Some(t)
            case t: WhileStatement => Some(t)
            case t: DoStatement => Some(t)
            case t: SwitchStatement => Some(t)
            case null => None
            case p: Product => findPriorASTElem2BreakStatement(p, env)
        }
    }

    // method to find prior element to a continue statement
    private def findPriorASTElem2ContinueStatement(a: Product, env: ASTEnv): Option[AST] = {
        val aparent = env.parent(a)
        aparent match {
            case t: ForStatement => Some(t)
            case t: WhileStatement => Some(t)
            case t: DoStatement => Some(t)
            case null => None
            case p: Product => findPriorASTElem2ContinueStatement(p, env)
        }
    }

    private def getStmtPred(s: AST, ctx: FeatureExpr, oldres: CFGRes, fm: FeatureModel, env: ASTEnv): CFGRes = {
        val sprevs = prevASTElems(s, env).reverse.tail
        getCompoundPred(sprevs, s, ctx, oldres, fm, env)
    }

    private def getStmtSucc(s: AST, ctx: FeatureExpr, oldres: CFGRes, fm: FeatureModel, env: ASTEnv): CFGRes = {
        val snexts = nextASTElems(s, env).tail
        getCompoundSucc(snexts, s, ctx, oldres, fm, env)
    }


    // this method filters BreakStatements
    // a break belongs to next outer loop (for, while, do-while)
    // or a switch statement (see [2])
    // use this method with the loop or switch body!
    // so we recursively go over the structure of the ast elems
    // in case we find a break, we add it to the result list
    // in case we hit another loop or switch we return the empty list
    private def filterBreakStatements(c: Conditional[Statement], ctx: FeatureExpr, fm: FeatureModel, env: ASTEnv): CFGRes = {
        def filterBreakStatementsHelper(a: Any): CFGRes = {
            a match {
                case t: BreakStatement => {
                    val tfexp = env.featureExpr(t)
                    if (!(tfexp and ctx isContradiction (fm))) List((tfexp, tfexp, t)) else List()
                }
                case _: SwitchStatement => List()
                case _: ForStatement => List()
                case _: WhileStatement => List()
                case _: DoStatement => List()
                case l: List[_] => l.flatMap(filterBreakStatementsHelper)
                case x: Product => x.productIterator.toList.flatMap(filterBreakStatementsHelper)
                case _ => List()
            }
        }
        filterBreakStatementsHelper(c)
    }

    // this method filters ContinueStatements
    // according to [2]: A continue statement shall appear only in or as a
    // loop body
    // use this method only with the loop body!
    private def filterContinueStatements(c: Conditional[Statement], ctx: FeatureExpr, fm: FeatureModel, env: ASTEnv): CFGRes = {
        def filterContinueStatementsHelper(a: Any): CFGRes = {
            a match {
                case t: ContinueStatement => {
                    val tfexp = env.featureExpr(t)
                    if (!(tfexp and ctx isContradiction (fm))) List((tfexp, tfexp, t)) else List()
                }
                case _: ForStatement => List()
                case _: WhileStatement => List()
                case _: DoStatement => List()
                case l: List[_] => l.flatMap(filterContinueStatementsHelper)
                case x: Product => x.productIterator.toList.flatMap(filterContinueStatementsHelper)
                case _ => List()
            }
        }
        filterContinueStatementsHelper(c)
    }

    // this method filters all CaseStatements
    private def filterCaseStatements(c: Conditional[Statement], ctx: FeatureExpr, fm: FeatureModel, env: ASTEnv): CFGRes = {
        def filterCaseStatementsHelper(a: Any): CFGRes = {
            a match {
                case t@CaseStatement(_) => {
                    val tfexp = env.featureExpr(t)
                    if (!(tfexp and ctx isContradiction (fm))) List((tfexp, tfexp, t)) else List()
                }
                case _: SwitchStatement => List()
                case l: List[_] => l.flatMap(filterCaseStatementsHelper)
                case x: Product => x.productIterator.toList.flatMap(filterCaseStatementsHelper)
                case _ => List()
            }
        }
        filterCaseStatementsHelper(c)
    }

    // this method filters all ReturnStatements
    private def filterReturnStatements(c: CompoundStatement, ctx: FeatureExpr, oldres: CFGRes, fm: FeatureModel, env: ASTEnv): CFGRes = {
        def filterReturnStatementsHelper(a: Any): CFGRes = {
            a match {
                case t@ReturnStatement(Some(c: CompoundStatementExpr)) => getExprPred(c, ctx, oldres, fm, env)
                case t@ReturnStatement(_) => {
                    val tfexp = env.featureExpr(t)
                    if (!(tfexp and ctx isContradiction (fm))) List((tfexp, tfexp, t)) else List()
                }
                case _: NestedFunctionDef => List()
                case l: List[_] => l.flatMap(filterReturnStatementsHelper)
                case x: Product => x.productIterator.toList.flatMap(filterReturnStatementsHelper)
                case _ => List()
            }
        }
        filterReturnStatementsHelper(c)
    }

    // although the standard says that a case statement only has one default statement
    // we may have differently annotated default statements
    private def filterDefaultStatements(c: Conditional[Statement], ctx: FeatureExpr, fm: FeatureModel, env: ASTEnv): CFGRes = {
        def filterDefaultStatementsHelper(a: Any): CFGRes = {
            a match {
                case _: SwitchStatement => List()
                case t: DefaultStatement => {
                    val tfexp = env.featureExpr(t)
                    if (!(tfexp and ctx isContradiction (fm))) List((tfexp, tfexp, t)) else List()
                }
                case l: List[_] => l.flatMap(filterDefaultStatementsHelper)
                case x: Product => x.productIterator.toList.flatMap(filterDefaultStatementsHelper)
                case _ => List()
            }
        }
        filterDefaultStatementsHelper(c)
    }

    // given a list of AST elements, determine successor AST elements based on feature expressions
    private def getCompoundSucc(l: List[AST], parent: AST, ctx: FeatureExpr, oldres: CFGRes, fm: FeatureModel, env: ASTEnv): CFGRes = {
        if (l.isEmpty) {
            if (succComplete(ctx, oldres)) oldres
            else followSucc(parent, ctx, oldres, fm, env)
        } else {
            var curres = oldres
            l.map({
                x => {
                    val ctxx = env.featureExpr(x)
                    val newres = if (isCFGInstructionSucc(x)) {
                        List((getNewResCtx(curres, ctx, ctxx), ctxx, x))
                    } else {
                        if (barrierExists(x)) {
                            succHelper(x, ctx, curres, fm, env)
                        } else {
                            barrier ::= x
                            val res = succHelper(x, ctx, curres, fm, env)
                            barrier = barrier.filterNot(e => e.eq(x))
                            res
                        }
                    }

                    if (newres.isEmpty) {}
                    else if (ctxx and ctx isContradiction (fm)) {}
                    else if (newres.map(_._2).forall(z => curres.map(_._2).exists(y => z equivalentTo y))) {}
                    else if (newres.map(_._2).forall(x => curres.map(_._2).fold(FeatureExprFactory.False)(_ or _) equivalentTo x)) {}
                    else {

                        for (n <- newres) {
                            if (n._1 isContradiction (fm)) {}
                            else if (curres.map(_._2).fold(FeatureExprFactory.False)(_ or _).not() and n._2 isContradiction (fm)) {}
                            else if (curres.map(_._2).exists(x => (x and ctx) equivalentTo (n._2 and ctx))) {}
                            else curres ++= List(n)
                        }

                        if (succComplete(ctx, curres)) return curres
                    }
                }
            })

            followSucc(parent, ctx, curres, fm, env)
        }
    }

    // determine pred elements from a list of variable elements; originally type is List[Opt[AST]]
    // we check the input list and determine, whether we hit all variants; if not go one level up (followPred) and
    // continue the search with the already determine result list curres.
    private def getCompoundPred(l: List[AST], parent: AST, ctx: FeatureExpr, oldres: CFGRes, fm: FeatureModel, env: ASTEnv): CFGRes = {
        if (l.isEmpty) {
            if (predComplete(ctx, oldres, fm)) oldres
            else followPred(parent, ctx, oldres, fm, env)
        } else {
            var curres = oldres
            l.map({
                x => {
                    val ctxx = env.featureExpr(x)

                    if (ctxx and ctx isContradiction (fm)) {}
                    else if (curres.map(_._2).exists(z => z equivalentTo ctxx)) {}
                    else if (ctxx implies curres.map(_._2).fold(FeatureExprFactory.False)(_ or _) isTautology (fm)) {}
                    else {
                        if (isCFGInstructionPred(x)) curres ++= {
                            if (curres.map(_._2).exists(z => (ctx and ctxx) equivalentTo (ctxx and z))) List()
                            else {
                                val newresctx = getNewResCtx(curres, ctx, ctxx)
                                if (newresctx isContradiction (fm)) List()
                                else List((newresctx, ctxx, x))
                            }
                        }
                        else curres = predHelper(x, ctx, curres, fm, env)

                        if (predComplete(ctx, curres, fm) || predCompleteBlock(ctx, curres)) return curres
                    }
                }
            })
            followPred(parent, ctx, curres, fm, env)
        }
    }
}

trait NoFunctionLookup {
    def lookupFunctionDef(name: String): Conditional[Option[ExternalDef]] = One(None)
}
