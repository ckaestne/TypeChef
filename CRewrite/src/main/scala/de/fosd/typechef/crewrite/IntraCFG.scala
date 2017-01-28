package de.fosd.typechef.crewrite

import de.fosd.typechef.conditional.{Choice, Conditional, One, Opt}
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}
import de.fosd.typechef.parser.c._

import org.kiama.attribution.Attribution._

import scala.annotation.tailrec

trait IntraCFG extends ASTNavigation with ConditionalNavigation {

    type CFGStmts = List[Opt[CFGStmt]]

    var source: AST = null

    def getSource = source

    lazy val succ: ASTEnv => AST => CFGStmts =
        paramAttr {
            env =>
                s =>
                    source = s
                    val c = env.featureExpr(s)
                    if (c.isSatisfiable())
                        succComp(env, List(), c)(s).filter( _.condition and c isSatisfiable())
                    else
                        List()
        }

    lazy val pred: ASTEnv => AST => CFGStmts =
        paramAttr {
            env =>
                s =>
                    source = s
                    val c = env.featureExpr(s)
                    if (c.isSatisfiable())
                        predComp(env, List(), c)(s).filter(_.condition and c isSatisfiable())
                    else
                        List()
        }

    private def stmtPred(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(s: Statement, f: Boolean): CFGStmts = {
        parentAST(s, env) match {
            case e: CompoundStatement =>
                val pn = prevASTElems(s, env)
                    .reverse.tail.reverse
                    .map(parentOpt(_, env)).asInstanceOf[List[Opt[Statement]]]
                val (b, r) = compStmtPred(env, res, ctx)(e, pn)

                if (f && !(b equivalentTo ctx) && !isComplete(ctx)(r))
                    predFollowing(env, r, ctx)(e)
                else
                    r
            case _ =>
                predFollowing(env, res, ctx)(s)
        }
    }

    private def stmtSucc(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(s: Statement, f: Boolean): CFGStmts =
        parentAST(s, env) match {
            case e: CompoundStatement =>
                val sn = nextASTElems(s, env)
                    .tail.map(parentOpt(_, env)).asInstanceOf[List[Opt[Statement]]]
                val r = compStmtSucc(env, res, ctx)(e, sn)

                if (f && !isComplete(ctx)(r))
                    succFollowing(env, r, ctx)(e)
                else
                    r
            case _ =>
                succFollowing(env, res, ctx)(s)
        }

    private def compStmtPred(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)
                            (e: CompoundStatement, l: List[Opt[Statement]]): (FeatureExpr, CFGStmts) = {
        var r = res
        var blck = FeatureExprFactory.False
        l.reverse.foreach {
            case Opt(_, o: ReturnStatement) if !source.isInstanceOf[FunctionDef] =>
                blck = blck or env.featureExpr(o)
            case Opt(_, o: GotoStatement) =>
                blck = blck or env.featureExpr(o)
            case Opt(_, o: BreakStatement) =>
                blck = blck or env.featureExpr(o)
            case y =>
                val c = env.featureExpr(y.entry)
                if (blck.not().isSatisfiable()
                    && !(blck equivalentTo c)
                    && (ctx and c isSatisfiable())
                    && !isComplete(ctx)(r)) {
                    r = basicPred(env, r, ctx and c)(y.entry)
                        // filter equal elements
                        .foldLeft(List(): CFGStmts) {
                        (ul, nee) =>
                            if (ul.exists {_.entry eq nee.entry})
                                ul
                            else
                                ul ++ List(nee)
                        }
                }
        }
        (blck, r)
    }

    private def compStmtSucc(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(e: CompoundStatement, l: List[Opt[Statement]]): CFGStmts = {
        var r = res

        l.foreach {
            y =>
                val c = env.featureExpr(y.entry)
                if ((ctx and c isSatisfiable())
                    && !isComplete(ctx)(r)) {
                    r = basicSucc(env, r, ctx and c)(y.entry)
                        // filter elements with an equivalent annotation
                        .foldLeft(List(): CFGStmts) {
                        (ul, nee) =>
                            if (ul.exists {_.condition equivalentTo nee.condition})
                                ul
                            else
                                ul ++ List(nee)
                        }
                    r
                }
        }
        r
    }

    private def condStmtPred(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(s: Conditional[Statement]): CFGStmts =
        s match {
            case Choice(_, thenBranch, elseBranch) =>
                condStmtPred(env, res, ctx)(thenBranch) ++ condStmtPred(env, res, ctx)(elseBranch)
            case One(c: CompoundStatement) =>
                basicPred(env, res, ctx)(c)
            case One(s: Statement) =>
                basicPred(env, res, ctx)(s)
        }

    private def condStmtSucc(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(s: Conditional[Statement]): CFGStmts =
        s match {
            case Choice(_, thenBranch, elseBranch) =>
                condStmtSucc(env, res, ctx)(thenBranch) ++ condStmtSucc(env, res, ctx)(elseBranch)
            case One(c: CompoundStatement) =>
                basicSucc(env, res, ctx)(c)
            case One(s: Statement) =>
                basicSucc(env, res, ctx)(s)
        }

    private def condExprPred(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(e: Conditional[Expr]): CFGStmts =
        e match {
            case One(value) => exprPred(env, res, ctx)(value)
            case Choice(_, thenBranch, elseBranch) =>
                condExprPred(env, res, ctx)(thenBranch) ++
                    condExprPred(env, res, ctx)(elseBranch)
        }

    private def condExprSucc(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(e: Conditional[Expr]): CFGStmts =
        e match {
            case One(value) => exprSucc(env, res, ctx)(value)
            case Choice(_, thenBranch, elseBranch) =>
                condExprSucc(env, res, ctx)(thenBranch) ++
                    condExprSucc(env, res, ctx)(elseBranch)
        }

    private def retuStmtSucc(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(a: AST): CFGStmts =
        findPriorASTElem[FunctionDef](a, env) match {
            case None =>
                res
            case Some(f) =>
                val c = getCFGStmtCtx(res, ctx, env.featureExpr(f))
                if (c.isSatisfiable())
                    res ++ List(Opt(c, f))
                else
                    res
        }

    private def switStmtPred(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(a: AST): CFGStmts =
        findPriorASTElem[SwitchStatement](a, env) match {
            case None =>
                res
            case Some(s@SwitchStatement(expr, _)) =>
                var r = res
                val c = env.featureExpr(a)
                r = exprPred(env, res, ctx and c)(expr)

                if (!isComplete(ctx)(r))
                    r = stmtPred(env, r, ctx and c)(s, f = true)
                r
        }

    private def exprPred(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(e: Expr): CFGStmts =
        e match {
            case CompoundStatementExpr(cs) =>
                basicPred(env, res, ctx)(cs)
            case y =>
                val c = env.featureExpr(y)
                if (c and ctx isSatisfiable()) {
                    val uc = getCFGStmtCtx(res, ctx, c)
                    if (uc isSatisfiable())
                        res ++ List(Opt(uc, y))
                    else
                        res
                } else
                    res
        }

    private[crewrite] def exprSucc(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(e: Expr): CFGStmts =
        e match {
            case CompoundStatementExpr(cs) =>
                compStmtSucc(env, res, ctx)(cs, cs.innerStatements)
            case y =>
                val c = env.featureExpr(y)
                if (ctx and c isSatisfiable()) {
                    val uc = getCFGStmtCtx(res, ctx, c)
                    if (uc.isSatisfiable())
                        res ++ List(Opt(uc, y))
                    else
                        res
                } else
                    res
        }

    private def basicPred(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(a: AST): CFGStmts =
        a match {
            case ForStatement(_, expr2, _, s) =>
                var r = res
                if (expr2.isDefined)
                    r ++= exprPred(env, res, ctx)(expr2.get)
                val brkstmts = filterBreakStatements(s, ctx, env)
                    .flatMap{
                        case Opt(m, n) => stmtPred(env, res, ctx and m)(n.asInstanceOf[Statement], f = true)
                    }
                r ++= brkstmts
                r
            case WhileStatement(expr, s) =>
                var r = res
                r ++= exprPred(env, res, ctx)(expr)
                val brkstmts = filterBreakStatements(s, ctx, env)
                    .flatMap{
                        case Opt(m, n) => stmtPred(env, res, ctx and m)(n.asInstanceOf[Statement], f = true)
                    }
                r ++= brkstmts
                r
            case DoStatement(expr, s) =>
                var r = res
                r ++= exprPred(env, res, ctx)(expr)
                val brkstmts = filterBreakStatements(s, ctx, env)
                    .flatMap{
                        case Opt(m, n) => stmtPred(env, res, ctx and m)(n.asInstanceOf[Statement], f = false)
                    }
                r ++= brkstmts
                r
            case _: BreakStatement =>
                res

            // conditional statements
            case IfStatement(condition, thenBranch, elifs, elseBranch) =>
                var r = res
                if (elseBranch.isDefined)
                    r ++= condStmtPred(env, res, ctx)(elseBranch.get)
                else
                    elifs.reverse.takeWhile(_ => !isComplete(ctx)(r)).foreach {
                        case Opt(_, ElifStatement(c, _)) =>
                            val re = condExprPred(env, res, ctx)(c)

                            re.filter {
                                n => getUnsatisfiedCtx(r).and(n.condition).isSatisfiable()
                            } foreach {
                                n => r ++= List(n)
                            }
                    }
                r ++= elifs.flatMap{
                    case Opt(m, ElifStatement(_, tb)) => condStmtPred(env, res, ctx and m)(tb)
                }
                if (!isComplete(ctx)(r))
                    r ++= condExprPred(env, res, ctx)(condition)
                r ++= condStmtPred(env, res, ctx)(thenBranch)
                r
            case e@ElifStatement(expr, thenBranch) =>
                val c = env.featureExpr(e)
                condExprPred(env, res, ctx and c)(expr) ++ condStmtPred(env, res, ctx and c)(thenBranch)
            case SwitchStatement(expr, s) =>
                var r = res
                val brkstmts = filterBreakStatements(s, ctx, env)
                    .flatMap{
                        case Opt(m, n) => stmtPred(env, res, ctx and m)(n.asInstanceOf[Statement], f = true)
                    }
                r ++= brkstmts
                val c = env.featureExpr(expr)
                val defstmts = filterDefaultStatements(s, c, env)
                if (!isComplete(c)(defstmts))
                    r ++= exprPred(env, res, ctx)(expr)
                val sstmts = condStmtPred(env, res, ctx)(s)
                    .flatMap {
                        case Opt(m, n: BreakStatement) => List()
                        case o => List(o)
                    }
                r ++= sstmts
                    r
            case CaseStatement(c) =>
                exprPred(env, res, ctx)(c)
            case e: DefaultStatement if e == source =>
                val r = stmtPred(env, res, ctx and env.featureExpr(e))(e, f = true)
                findPriorASTElem[SwitchStatement](e, env) match {
                    case None => r
                    case Some(SwitchStatement(expr, s)) =>
                        r ++ exprPred(env, res, ctx)(expr)
                }
            case ReturnStatement(expr) =>
                source match {
                    case _: FunctionDef if expr.isDefined => exprPred(env, res, ctx)(expr.get)
                    case _ => res
                }
            case _: GotoStatement =>
                res
            case LabelStatement(id, _) =>
                exprPred(env, res, ctx)(id)
            case e: CompoundStatement =>
                compStmtPred(env, res, ctx)(e, e.innerStatements)._2

            case e: CFGStmt =>
                val c = env.featureExpr(e)
                if (c and ctx isSatisfiable()) {
                    val cu = getCFGStmtCtx(res, ctx, c)
                    if (cu isSatisfiable())
                        res ++ List(Opt(cu, e))
                    else
                        res
                } else
                    res
        }

    private def basicSucc(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(a: AST): CFGStmts =
        a match {
            // loops
            case ForStatement(expr1, expr2, _, s) =>
                if (expr1.isDefined)
                    exprSucc(env, res, ctx)(expr1.get)
                else if (expr2.isDefined)
                    exprSucc(env, res, ctx)(expr2.get)
                else
                    condStmtSucc(env, res, ctx)(s)
            case WhileStatement(expr, _) =>
                exprSucc(env, res, ctx)(expr)
            case DoStatement(expr, s) =>
                var r = condStmtSucc(env, res, ctx)(s)
                if (!isComplete(ctx)(r))
                    r ++= exprPred(env, r, ctx)(expr)
                r

            // conditional statements
            case e: BreakStatement =>
                getBreakStmtContext(e, env) match {
                    case None =>
                        assert(assertion = false, "break statement should always occur within a for, do-while, while or switch statement")
                        res
                    case Some(s) =>
                        stmtSucc(env, res, ctx and env.featureExpr(e))(s, true)
                }
            case e: ContinueStatement if e == source =>
                val y = ctx and env.featureExpr(e)
                getContinueStmtContext(e, env) match {
                    case None =>
                        assert(assertion = false, "continue statement should always occur within a for, do-while, or while statement")
                        res
                    case Some(ForStatement(_, expr2, expr3, s)) =>
                        if (expr3.isDefined)
                            exprSucc(env, res, y)(expr3.get)
                        else if (expr2.isDefined)
                            exprSucc(env, res, y)(expr2.get)
                        else
                            condStmtSucc(env, res, y)(s)
                    case Some(WhileStatement(expr, _)) =>
                        exprSucc(env, res, y)(expr)
                    case Some(DoStatement(expr, _)) =>
                        exprSucc(env, res, y)(expr)
                    case _ => List()
                }
            case CaseStatement(c) =>
                exprSucc(env, res, ctx)(c)
            case e: DefaultStatement if e == source =>
                stmtSucc(env, res, ctx and env.featureExpr(e))(e, false)
            case IfStatement(condition, _, _, _) =>
                condExprSucc(env, res, ctx)(condition)
            case ElifStatement(condition, _) =>
                condExprSucc(env, res, ctx)(condition)
            case SwitchStatement(expr, _) =>
                exprSucc(env, res, ctx)(expr)
            case GotoStatement(target) =>
                exprSucc(env, res, ctx)(target)
            case LabelStatement(id, _) =>
                exprSucc(env, res, ctx)(id)

            case ReturnStatement(Some(expr)) =>
                exprSucc(env, res, ctx)(expr)
            case e@ReturnStatement(None) =>
                findPriorASTElem[FunctionDef](e, env) match {
                    case None => res
                    case Some(f) =>
                        val c = env.featureExpr(e)
                        if (c and ctx isSatisfiable()) {
                            val cu = getCFGStmtCtx(res, ctx, c)
                            if (cu isSatisfiable())
                                res ++ List(Opt(cu, f))
                            else
                                res
                        } else
                            res
                }
            case e: CompoundStatement =>
                compStmtSucc(env, res, ctx)(e, e.innerStatements)

            case e: CFGStmt =>
                val c = env.featureExpr(e)
                if (c and ctx isSatisfiable()) {
                    val cu = getCFGStmtCtx(res, ctx, c)
                    if (cu isSatisfiable())
                        res ++ List(Opt(cu, e))
                    else
                        res
                } else
                    res
        }

    private def predComp(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(a: AST): CFGStmts =
        a match {
            case FunctionDef(_, _, _, stmt) =>
                var r = res
                val retustmts = filterReturnStatements(stmt, ctx, env)
                    .flatMap{
                        case Opt(m, n@ReturnStatement(None)) =>
                            stmtPred(env, res, ctx and m)(n, f = false)
                        case Opt(m, ReturnStatement(Some(expr))) =>
                            exprPred(env, res, ctx and m)(expr)
                    }
                r ++= retustmts
                r ++= predComp(env, res, ctx)(stmt)
                r
            case e@CompoundStatement(innerStatements) =>
                val (b, r) = compStmtPred(env, res, ctx)(e, innerStatements)

                if (!(b equivalentTo ctx) && !isComplete(ctx)(r))
                    predFollowing(env, r, ctx)(e)
                else
                    r
            case e => predFollowing(env, res, ctx)(e)
        }

    @tailrec
    private def succComp(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(a: AST): CFGStmts =
        a match {
            case FunctionDef(_, _, _, stmt) => succComp(env, res, ctx)(stmt)
            case e@CompoundStatement(innerStatements) =>
                val r = compStmtSucc(env, res, ctx)(e, innerStatements)

                if (!isComplete(ctx)(r))
                    succFollowing(env, r, ctx)(e)
                else
                    r
            case e => succFollowing(env, res, ctx)(e)
        }

    // checks reference equality of se in a given structure
    private[crewrite] lazy val isPartOf: Product => Any => Boolean =
        paramAttr {
            se => {
                case e: Product if se.asInstanceOf[AnyRef].eq(e.asInstanceOf[AnyRef]) => true
                case l: List[_] => l.exists(isPartOf(se)(_))
                case e: Product => e.productIterator.toList.exists(isPartOf(se)(_))
                case _ => false
            }
        }

    private def getUnsatisfiedCtx(res: CFGStmts): FeatureExpr = {
        res.map(_.condition).fold(FeatureExprFactory.False)(_ or _).not()
    }

    protected def getCFGStmtCtx(res: CFGStmts, ctx: FeatureExpr, ectx: FeatureExpr): FeatureExpr = {
        getUnsatisfiedCtx(res) and ctx and ectx
    }

    private lazy val isComplete: FeatureExpr => CFGStmts => Boolean =
        paramAttr {
            ctx => {
                res =>
                    res.map(_.condition).fold(FeatureExprFactory.False)(_ or _) equivalentTo ctx
            }
        }

    @tailrec
    private def getContinueStmtContext(a: Product, env: ASTEnv): Option[Statement] = {
        parentAST(a, env) match {
            case t: ForStatement => Some(t)
            case t: WhileStatement => Some(t)
            case t: DoStatement => Some(t)
            case null => None
            case p: Product => getContinueStmtContext(p, env)
        }
    }

    @tailrec
    private def getBreakStmtContext(a: Product, env: ASTEnv): Option[Statement] = {
        parentAST(a, env) match {
            case t: ForStatement => Some(t)
            case t: WhileStatement => Some(t)
            case t: DoStatement => Some(t)
            case t: SwitchStatement => Some(t)
            case null => None
            case p: Product => getBreakStmtContext(p, env)
        }
    }

    @tailrec
    private def predFollowing(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(a: AST): CFGStmts =
        a match {
            // case or default statements belong only to switch statements
            case e: CaseStatement =>
                switStmtPred(env, res, ctx)(e) ++ stmtPred(env, res, ctx)(e, f = true)
            case e: DefaultStatement =>
                switStmtPred(env, res, ctx)(e)
            case se =>
                val sep = parentAST(se, env)
                sep match {
                    // loops
                    case e@ForStatement(Some(expr1), _, _, _) if isPartOf(se)(expr1) =>
                        stmtPred(env, res, ctx)(e, f = true)
                    case e@ForStatement(expr1, Some(expr2), expr3, s) if isPartOf(se)(expr2) =>
                        var r = res
                        if (expr3.isDefined)
                            r ++= exprPred(env, res, ctx)(expr3.get)
                        else {
                            r ++= condStmtPred(env, res, ctx)(s)

                            if (!isComplete(ctx)(r))
                                r ++= exprPred(env, r, ctx)(expr2)

                            r ++= filterContinueStatements(s, env.featureExpr(e), env)
                        }
                        if (expr1.isDefined)
                            r ++= exprPred(env, res, ctx)(expr1.get)
                        else
                            r ++= stmtPred(env, res, ctx)(e, f = true)
                        r
                    case e@ForStatement(_, Some(expr2), Some(expr3), s) if isPartOf(se)(expr3) =>
                        val rs = condStmtPred(env, res, ctx)(s)
                        val rf = filterContinueStatements(s, env.featureExpr(e), env)

                        if (!isComplete(ctx)(rs)) {
                            rs ++ exprPred(env, rs, ctx)(expr2) ++ rf
                        } else {
                            rs ++ rf
                        }
                    case e@ForStatement(expr1, expr2, expr3, s) if isPartOf(se)(s) =>
                        if (expr2.isDefined)
                            exprPred(env, res, ctx)(expr2.get)
                        else if (expr3.isDefined)
                            exprPred(env, res, ctx)(expr3.get)
                        else {
                            var r = res
                            if (expr1.isDefined)
                                r ++= exprPred(env, res, ctx)(expr1.get)
                            else
                                r ++= stmtPred(env, res, ctx)(e, f = true)

                            r ++= condStmtPred(env, res, ctx)(s)
                            r
                        }
                    case e@WhileStatement(expr, s) if isPartOf(se)(expr) =>
                        val re = stmtPred(env, res, ctx)(e, f = true)
                        var rs = condStmtPred(env, res, ctx)(s)

                        if (!isComplete(ctx)(rs))
                           rs ++= exprPred(env, rs, ctx)(expr)
                        re ++ rs
                    case WhileStatement(expr, _) =>
                        exprPred(env, res, ctx)(expr)
                    case e@DoStatement(expr, s) if isPartOf(se)(expr) =>
                        val r = condStmtPred(env, res, ctx)(s)
                        if (!isComplete(ctx)(r))
                            exprPred(env, r, ctx)(expr) ++ stmtPred(env, r, ctx)(e, f = true)
                        else
                            r
                    case e@DoStatement(expr, s) =>
                        exprPred(env, res, ctx)(expr) ++ stmtPred(env, res, ctx)(e, f = true)

                    // conditional statements
                    case e@IfStatement(condition, _, _, _) if isPartOf(se)(condition) =>
                        val r = stmtPred(env, res, ctx)(e, f = true)
                        if (!isComplete(ctx)(r))
                            predFollowing(env, r, ctx)(e)
                        else
                            r
                    case IfStatement(condition, thenBranch, _, _) if isPartOf(se)(thenBranch) =>
                        condExprPred(env, res, ctx)(condition)
                    case IfStatement(condition, _, elifs, elseBranch) if isPartOf(se)(elseBranch) =>
                        var r = res

                        elifs.reverse.takeWhile(_ => !isComplete(ctx)(r)).foreach {
                            case Opt(_, ElifStatement(c, _)) =>
                                val re = condExprPred(env, res, ctx)(c)

                                re.filter {
                                    n => getUnsatisfiedCtx(r).and(n.condition).isSatisfiable()
                                } foreach {
                                    n => r ++= List(n)
                                }
                        }

                        if (!isComplete(ctx)(r)) {
                            condExprPred(env, r, ctx)(condition)
                        } else
                            r
                    case e@ElifStatement(condition, _) if isPartOf(se)(condition) =>
                        var r = res
                        val elifs = prevASTElems(e, env).reverse.tail.asInstanceOf[List[ElifStatement]]

                        elifs.takeWhile(_ => !isComplete(ctx)(r)).foreach {
                            case ElifStatement(c, _) =>
                                val re = condExprPred(env, res, ctx)(c)

                                re.filter {
                                    n => getUnsatisfiedCtx(r).and(n.condition).isSatisfiable()
                                } foreach {
                                    n => r ++= List(n)
                                }
                        }

                        if (!isComplete(ctx)(r)) {
                            findPriorASTElem[IfStatement](e, env) match {
                                case None =>
                                case Some(IfStatement(icon, _, _, _)) =>
                                    r ++= condExprPred(env, r, ctx)(icon)
                            }
                            r
                        } else
                            r
                    case ElifStatement(condition, _) =>
                        condExprPred(env, res, ctx)(condition)
                    case SwitchStatement(expr, s) if isPartOf(se)(s) =>
                        exprPred(env, res, ctx)(expr)
                    case e: SwitchStatement =>
                        stmtPred(env, res, ctx)(e, f = true)
                    case e: CaseStatement =>
                        stmtPred(env, res, ctx)(e, f = true) ++ switStmtPred(env, res, ctx)(e)
                    case e: LabelStatement =>
                        val c = env.featureExpr(e)
                        findPriorASTElem[FunctionDef](e, env) match {
                            case None =>
                                res
                            case Some(f) =>
                                val gotostmts = filterAllASTElems[GotoStatement](f, c, env)
                                    .filter {
                                        case GotoStatement(Id(target)) if target != e.id.name =>
                                            false
                                        case _ =>
                                            true
                                    }
                                    .flatMap {
                                        case g: GotoStatement =>
                                            val c = env.featureExpr(g)
                                            exprPred(env, res, ctx and c)(g.target)
                                    }
                                res ++ gotostmts ++ stmtPred(env, res, ctx)(e, f = true)
                        }

                    case e: CompoundStatement =>
                        stmtPred(env, res, ctx)(se.asInstanceOf[Statement], f = true)
                    case e: CompoundStatementExpr =>
                        predFollowing(env, res, ctx)(e)

                    case e: FunctionDef =>
                        val c = env.featureExpr(e)
                        val m = getCFGStmtCtx(res, ctx, c)
                        var r = res
                        if (m.isSatisfiable())
                            r = res ++ List(Opt(m, e))
                        else
                            r = res
                        r

                    case e: Statement =>
                        stmtPred(env, res, ctx)(e, f = true)

                    case _ => res
                }
        }

    private def succFollowing(env: ASTEnv, res: CFGStmts, ctx: FeatureExpr)(a: AST): CFGStmts =
        a match {
            case se: ReturnStatement =>
                retuStmtSucc(env, res, ctx)(se)
            case se =>
                parentAST(se, env) match {
                    // loops
                    case ForStatement(Some(expr1), expr2, _, s) if isPartOf(se)(expr1) =>
                        if (expr2.isDefined)
                            exprSucc(env, res, ctx)(expr2.get)
                        else
                            condStmtSucc(env, res, ctx)(s)
                    case e@ForStatement(_, Some(expr2), expr3, s) if isPartOf(se)(expr2) =>
                        val rt = succFollowing(env, res, ctx)(e)
                        val rs = condStmtSucc(env, res, ctx)(s)
                        if (!isComplete(ctx)(rs)) {
                            val re =
                                if (expr3.isDefined)
                                    exprSucc(env, res, ctx)(expr3.get)
                                else
                                    exprSucc(env, res, ctx)(expr2)
                            rt ++ rs ++ re
                        } else rt ++ rs
                    case ForStatement(_, expr2, Some(expr3), s) if isPartOf(se)(expr3) =>
                        if (expr2.isDefined)
                            exprSucc(env, res, ctx)(expr2.get)
                        else
                            condStmtSucc(env, res, ctx)(s)
                    case ForStatement(_, expr2, expr3, s) if isPartOf(se)(s) =>
                        if (expr3.isDefined)
                            exprSucc(env, res, ctx)(expr3.get)
                        else if (expr2.isDefined)
                            exprSucc(env, res, ctx)(expr2.get)
                        else
                            condStmtSucc(env, res, ctx)(s)

                    case e@WhileStatement(expr, s) if isPartOf(se)(expr) =>
                        val rs = condStmtSucc(env, res, ctx)(s)
                        val rt = succFollowing(env, res, ctx)(e)
                        val re =
                            if (!isComplete(ctx)(rs))
                                exprSucc(env, res, ctx)(expr)
                            else
                                List()
                        rs ++ re ++ rt
                    case WhileStatement(expr, _) =>
                        exprSucc(env, res, ctx)(expr)

                    case e@DoStatement(expr, s) if isPartOf(se)(expr) =>
                        val rs = condStmtSucc(env, res, ctx)(s)
                        val rt = succFollowing(env, res, ctx)(e)
                        val re =
                            if (!isComplete(ctx)(rs))
                                exprSucc(env, res, ctx)(expr)
                            else
                                List()
                        rs ++ re ++ rt
                    case DoStatement(expr, s) =>
                        exprSucc(env, res, ctx)(expr)

                    // conditional statements
                    // condition of the if statement
                    case e@IfStatement(condition, thenBranch, elifs, elseBranch) if isPartOf(se)(condition) =>
                        var r = res

                        elifs.takeWhile(_ => !isComplete(ctx)(r)).foreach {
                            case Opt(_, ElifStatement(c, _)) =>
                                val re = condExprSucc(env, res, ctx)(c)

                                re.filter {
                                    n => getUnsatisfiedCtx(r).and(n.condition).isSatisfiable()
                                } foreach {
                                    n => r ++= List(n)
                                }
                        }

                        if (!isComplete(ctx)(r)) {
                            if (elseBranch.isDefined)
                                r ++= condStmtSucc(env, res, ctx)(elseBranch.get)
                            else
                                r = succFollowing(env, r, ctx)(e)
                        }
                        r ++= condStmtSucc(env, res, ctx)(thenBranch)
                        r
                    // part of then branch, elifs, or else branch
                    case e@IfStatement(_, _, _, _) =>
                        stmtSucc(env, res, ctx)(e, true)
                    // condition of the elif statement
                    case e@ElifStatement(condition, thenBranch) if isPartOf(se)(condition) =>
                        var r = res
                        val elifs = nextASTElems(e, env).tail.asInstanceOf[List[ElifStatement]]

                        elifs.takeWhile(_ => !isComplete(ctx)(r)).foreach {
                            case ElifStatement(c, _) =>
                                val re = condExprSucc(env, res, ctx)(c)

                                re.filter {
                                    n => getUnsatisfiedCtx(r).and(n.condition).isSatisfiable()
                                } foreach {
                                    n => r ++= List(n)
                                }
                        }

                        if (!isComplete(ctx)(r)) {
                            findPriorASTElem[IfStatement](e, env) match {
                                case None =>
                                case Some(i@IfStatement(_, _, _, None)) =>
                                    r = succFollowing(env, r, ctx)(i)
                                case Some(IfStatement(_, _, _, Some(elseBranch))) =>
                                    r = condStmtSucc(env, r, ctx)(elseBranch)
                            }
                        }
                        r ++ condStmtSucc(env, res, ctx)(thenBranch)
                    case e: ElifStatement =>
                        succFollowing(env, res, ctx)(e)

                    case e@GotoStatement(g) =>
                        findPriorASTElem[FunctionDef](e, env) match {
                            case None => res // should never happen, as parser ensures that goto statements belong to functions
                            case Some(f) =>
                                var lstmts = filterAllASTElems[LabelStatement](f, env.featureExpr(e), env)

                                g match {
                                    case Id(l) =>
                                        lstmts = lstmts.filter{ y => y.id.name == l }

                                        @tailrec
                                        def pairwise[T](l: List[T], r: List[(T,T)] = List()): List[(T,T)] = {
                                            l match {
                                                case Nil => r
                                                case y::ys => pairwise(ys, ys.map{ (y, _) })
                                            }

                                        }
                                        val pwcombs = pairwise(lstmts.map(env.featureExpr(_)))
                                        assert(assertion = pwcombs.forall{
                                            case (m, n) => !(m equivalentTo n)
                                        }, "found label statements with equivalent annotation!")
                                    case _ =>
                                }

                                if (lstmts.isEmpty)
                                    stmtSucc(env, res, ctx)(e, true)
                                else
                                    res ++ lstmts.flatMap { ls => basicSucc(env, res, ctx)(ls) }
                        }
                    case e@SwitchStatement(expr, s) if isPartOf(se)(expr) =>
                        var r = res
                        // switch code (in s) before the first case statement is dead code
                        // and can be used only to declare variables
                        // see section 6.8.2.4 in the c standard
                        val casstmts = filterCaseStatements(s, env.featureExpr(expr), env)
                            .flatMap{
                                case Opt(m, n) => basicSucc(env, res, ctx and m)(n.asInstanceOf[Statement])
                            }
                        r ++= casstmts
                        val defstmts = filterDefaultStatements(s, env.featureExpr(expr), env)

                        if (defstmts.isEmpty)
                            r ++= stmtSucc(env, res, ctx)(e, true)
                        else
                            r ++= defstmts
                        r

                    case e: CompoundStatement =>
                        val r = stmtSucc(env, res, ctx)(se.asInstanceOf[Statement], true)
                        if (!isComplete(ctx)(r))
                            succFollowing(env, r, ctx)(e)
                        else
                            r
                    case e: CompoundStatementExpr =>
                        succFollowing(env, res, ctx)(e)

                    case e: ReturnStatement =>
                        retuStmtSucc(env, res, ctx)(e)

                    case e: FunctionDef =>
                        val c = env.featureExpr(e)
                        val uc = getCFGStmtCtx(res, ctx, c)
                        var r = res
                        if (uc.isSatisfiable())
                            r = res ++ List(Opt(uc, e))
                        else
                            r = res
                        r

                    case e: Statement =>
                        stmtSucc(env, res, ctx)(e, true)

                    case _ => res
                }
        }


    private def filterCaseStatements(c: Conditional[Statement], ctx: FeatureExpr, env: ASTEnv): CFG = {
        def filterCaseStatementsHelper(a: Any): CFG = {
            a match {
                case _: SwitchStatement => List()
                case t: CaseStatement =>
                    val c = env.featureExpr(t)
                    if (c and ctx isSatisfiable())
                        List(Opt(c, t))
                    else
                        List()
                case l: List[_] => l.flatMap(filterCaseStatementsHelper)
                case x: Product => x.productIterator.toList.flatMap(filterCaseStatementsHelper)
                case _ => List()
            }
        }
        filterCaseStatementsHelper(c)
    }

    private def filterDefaultStatements(c: Conditional[Statement], ctx: FeatureExpr, env: ASTEnv): CFG = {
        def filterDefaultStatementsHelper(a: Any): CFG = {
            a match {
                case _: SwitchStatement => List()
                case t: DefaultStatement =>
                    val c = env.featureExpr(t)
                    if (!(c and ctx).isContradiction())
                        List(Opt(c, t))
                    else
                        List()
                case l: List[_] => l.flatMap(filterDefaultStatementsHelper)
                case x: Product => x.productIterator.toList.flatMap(filterDefaultStatementsHelper)
                case _ => List()
            }
        }
        filterDefaultStatementsHelper(c)
    }

    private def filterContinueStatements(c: Conditional[Statement], ctx: FeatureExpr, env: ASTEnv): CFG = {
        def filterContinueStatementsHelper(a: Any): CFG = {
            a match {
                case e: ContinueStatement =>
                    val c = env.featureExpr(e)
                    if (c and ctx isSatisfiable())
                        List(Opt(c, e))
                    else
                        List()
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

    private def filterBreakStatements(c: Conditional[Statement], ctx: FeatureExpr, env: ASTEnv): CFG = {
        def filterBreakStatementsHelper(a: Any): CFG = {
            a match {
                case e: BreakStatement =>
                    val c = env.featureExpr(e)
                    if (c and ctx isSatisfiable())
                        List(Opt(c, e))
                    else
                        List()
                case _: ForStatement => List()
                case _: WhileStatement => List()
                case _: DoStatement => List()
                case _: SwitchStatement => List()
                case l: List[_] => l.flatMap(filterBreakStatementsHelper)
                case p: Product => p.productIterator.toList.flatMap(filterBreakStatementsHelper)
                case _ => List()
            }
        }
        filterBreakStatementsHelper(c)
    }

    private def filterReturnStatements(c: CompoundStatement, ctx: FeatureExpr, env: ASTEnv): CFG = {
        def filterReturnStatementsHelper(a: Any): CFG = {
            a match {
                case e: ReturnStatement =>
                    val c = env.featureExpr(e)
                    if (c and ctx isSatisfiable())
                        List(Opt(c, e))
                    else
                        List()
                case _: NestedFunctionDef => List()
                case l: List[_] => l.flatMap(filterReturnStatementsHelper)
                case p: Product => p.productIterator.toList.flatMap(filterReturnStatementsHelper)
                case _ => List()
            }
        }
        filterReturnStatementsHelper(c)
    }

    // necessary hook for InterCFG implementation; provides means to resolve function calls for inter procedural control
    // flow computation
    private[crewrite] def findMethodCalls(t: AST, env: ASTEnv, res: CFG, ctx: FeatureExpr, _res: CFG): CFG = {
        _res
    }
}
