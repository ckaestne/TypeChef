//package de.fosd.typechef.crewrite
//
//import de.fosd.typechef.conditional.{Choice, Conditional, One, Opt}
//import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}
//import de.fosd.typechef.parser.c._
//
//import org.kiama.attribution.Attribution._
//
//import scala.annotation.tailrec
//
//trait CCFG extends ASTNavigation with ConditionalNavigation {
//
//    type CFGStmts = List[Opt[CFGStmt]]
//
//    private lazy val stmtPred: ((ASTEnv, CFGStmts, FeatureExpr)) => Statement => CFGStmts = {
//        paramAttr {
//            case x@(env, res, ctx) =>
//                s =>
//                    val pn = prevASTElems(s, env)
//                        .reverse.tail.reverse
//                        .map(parentOpt(_, env)).asInstanceOf[List[Opt[Statement]]]
//                    val c = parentAST(s, env).asInstanceOf[CompoundStatement]
//                    val r = compStmtPred(x)(c, pn)
//
//                    if (!isComplete(ctx)(r))
//                        predFollowing(env, r, ctx)(c)
//                    else
//                        r
//            }
//        }
//
//    private lazy val stmtSucc: ((ASTEnv, CFGStmts, FeatureExpr)) => Statement => CFGStmts =
//        paramAttr {
//            case x@(env, res, ctx) =>
//                s =>
//                    val sn = nextASTElems(s, env).tail.map(parentOpt(_, env)).asInstanceOf[List[Opt[Statement]]]
//                    val c = parentAST(s, env).asInstanceOf[CompoundStatement]
//                    val r = compStmtSucc(x)(c, sn)
//
//                    if (!isComplete(ctx)(r))
//                        succFollowing(env, r, ctx)(c)
//                    else
//                        r
//        }
//
//    private lazy val compStmtPred: ((ASTEnv, CFGStmts, FeatureExpr)) => ((CompoundStatement, List[Opt[Statement]])) => CFGStmts =
//        paramAttr {
//            case x@(env, res, ctx) => {
//                case (e, l) =>
//                    var r = res
//
//                    l.reverse.foreach {
//                        y =>
//                            val c = env.featureExpr(y.entry)
//                            if ((ctx and c isSatisfiable())
//                                && !isComplete(ctx)(r)) {
//                                r = basicPred(env, r, ctx and c)(y.entry)
//                                // filter elements with an equivalent annotation
//                                .foldLeft(List(): CFGStmts){
//                                    (ul, nee) =>
//                                        if (ul.exists{ _.condition equivalentTo nee.condition })
//                                            ul
//                                        else
//                                            ul ++ List(nee)
//                                }
//                                // filter unsatisfiable control-flow paths,
//                                // i.e., feature expression is contradiction
//                                .filter{ _.condition and ctx isSatisfiable() }
//                                r
//                            }
//                    }
//                    r
//            }
//        }
//
//    private lazy val compStmtSucc: ((ASTEnv, CFGStmts, FeatureExpr)) => ((CompoundStatement, List[Opt[Statement]])) => CFGStmts =
//        paramAttr {
//            case x@(env, res, ctx) => {
//                case (e, l) =>
//                    var r = res
//
//                    l.foreach {
//                        y =>
//                            val c = env.featureExpr(y.entry)
//                            if ((ctx and c isSatisfiable())
//                                && !isComplete(ctx)(r)) {
//                                r = basicSucc(env, r, ctx and c)(y.entry)
//                                // filter elements with an equivalent annotation
//                                .foldLeft(List(): CFGStmts){
//                                    (ul, nee) =>
//                                        if (ul.exists{ _.condition equivalentTo nee.condition })
//                                            ul
//                                        else
//                                            ul ++ List(nee)
//                                }
//                                // filter unsatisfiable control-flow paths,
//                                // i.e., feature expression is contradiction
//                                .filter{ _.condition and ctx isSatisfiable() }
//                                r
//                            }
//                    }
//                    r
//            }
//        }
//
//    private lazy val condStmtPred: ((ASTEnv, CFGStmts, FeatureExpr)) => Conditional[Statement] => CFGStmts =
//        paramAttr {
//            case x@(env, res, ctx) => {
//                case Choice(_, thenBranch, elseBranch) =>
//                    condStmtPred(x)(thenBranch) ++ condStmtPred(x)(elseBranch)
//                case One(c: CompoundStatement) =>
//                    compStmtPred(x)(c, c.innerStatements)
//                case One(s: Statement) =>
//                    basicPred(x)(s)
//            }
//        }
//
//    private lazy val condStmtSucc: ((ASTEnv, CFGStmts, FeatureExpr)) => Conditional[Statement] => CFGStmts =
//        paramAttr {
//            case x@(env, res, ctx) => {
//                case Choice(_, thenBranch, elseBranch) =>
//                    condStmtSucc(x)(thenBranch) ++ condStmtSucc(x)(elseBranch)
//                case One(c: CompoundStatement) =>
//                    compStmtSucc(x)(c, c.innerStatements)
//                case One(s: Statement) =>
//                    basicSucc(x)(s)
//            }
//        }
//
//    private lazy val condExprPred: ((ASTEnv, CFGStmts, FeatureExpr)) => Conditional[Expr] => CFGStmts =
//        paramAttr {
//            case x@(env, res, ctx) => {
//                case One(value) => exprPred(x)(value)
//                case Choice(_, thenBranch, elseBranch) =>
//                    condExprPred(x)(thenBranch) ++
//                        condExprPred(x)(elseBranch)
//            }
//        }
//
//    private lazy val condExprSucc: ((ASTEnv, CFGStmts, FeatureExpr)) => Conditional[Expr] => CFGStmts =
//        paramAttr {
//            case x@(env, res, ctx) => {
//                case One(value) => exprSucc(x)(value)
//                case Choice(_, thenBranch, elseBranch) =>
//                    condExprSucc(x)(thenBranch) ++
//                        condExprSucc(x)(elseBranch)
//            }
//        }
//
//    private lazy val retuStmtSucc: ((ASTEnv, CFGStmts, FeatureExpr)) => AST => CFGStmts =
//        paramAttr {
//            case (env, res, ctx) =>
//                r =>
//                    findPriorASTElem[FunctionDef](r, env) match {
//                        case None =>
//                            res
//                        case Some(f) =>
//                            val c = getCFGStmtCtx(res, ctx, env.featureExpr(f))
//                            if (c.isSatisfiable())
//                                res ++ List(Opt(c, f))
//                            else
//                                res
//                    }
//        }
//
//    private lazy val switStmtPred: ((ASTEnv, CFGStmts, FeatureExpr)) => AST => CFGStmts =
//        paramAttr {
//            case x@(env, res, ctx) =>
//                e =>
//                    findPriorASTElem[SwitchStatement](e, env) match {
//                        case None =>
//                            res
//                        case Some(s@SwitchStatement(expr, _)) =>
//                            val c = env.featureExpr(e)
//                            exprPred(env, res, ctx and c)(expr) ++ stmtPred(env, res, ctx and c)(s)
//                    }
//        }
//
//    private lazy val exprPred: ((ASTEnv, CFGStmts, FeatureExpr)) => Expr => CFGStmts =
//        paramAttr {
//            case x@(env, res, ctx) => {
//                case CompoundStatementExpr(compoundStatement) =>
//                    compStmtPred(x)(compoundStatement, compoundStatement.innerStatements)
//                case e =>
//                    val c = env.featureExpr(e)
//                    if (c and ctx isSatisfiable()) {
//                        val uc = getCFGStmtCtx(res, ctx, c)
//                        if (uc isSatisfiable())
//                            res ++ List(Opt(uc, e))
//                        else
//                            res
//                    } else
//                        res
//            }
//        }
//
//    private lazy val exprSucc: ((ASTEnv, CFGStmts, FeatureExpr)) => Expr => CFGStmts =
//        paramAttr {
//            case x@(env, res, ctx) => {
//                case CompoundStatementExpr(compoundStatement) =>
//                    compStmtSucc(x)(compoundStatement, compoundStatement.innerStatements)
//                case e =>
//                    val c = env.featureExpr(e)
//                    if (ctx and c isSatisfiable()) {
//                        val uc = getCFGStmtCtx(res, ctx, c)
//                        if (uc.isSatisfiable())
//                            res ++ List(Opt(uc, e))
//                        else
//                            res
//                    } else
//                        res
//                }
//        }
//
//    lazy val succ: ASTEnv => AST => CFGStmts =
//        paramAttr {
//            env =>
//                s =>
//                    val c = env.featureExpr(s)
//                    if (c.isSatisfiable())
//                        succComp(env, List(), c)(s)
//                    else
//                        List()
//        }
//
//    lazy val pred: ASTEnv => AST => CFGStmts =
//        paramAttr {
//            env =>
//                s =>
//                    val c = env.featureExpr(s)
//                    if (c.isSatisfiable())
//                        predComp(env, List(), c)(s)
//                    else
//                        List()
//        }
//
//    private lazy val basicPred: ((ASTEnv, CFGStmts, FeatureExpr)) => AST => CFGStmts =
//        paramAttr {
//            case x@(env, res, ctx) => {
//                // loops
//                case ForStatement(_, expr2, _, s) =>
//                    var r = res
//                    r ++= condStmtPred(x)(s)
//                    if (expr2.isDefined)
//                        r ++= exprPred(x)(expr2.get)
//                    val brkstmts = filterBreakStatements(s, ctx, env)
//                        .flatMap{ case Opt(c, b) => stmtPred(env, res, c and ctx)(b.asInstanceOf[Statement]) }
//                    r ++= brkstmts
//                    r
//                case WhileStatement(expr, s) =>
//                    var r = res
//                    r ++= exprPred(x)(expr)
//                    val brkstmts = filterBreakStatements(s, ctx, env)
//                        .flatMap{ case Opt(c, b) => stmtPred(env, res, c and ctx)(b.asInstanceOf[Statement]) }
//                    r ++= brkstmts
//                    r
//                case DoStatement(expr, s) =>
//                    var r = res
//                    r ++= exprPred(x)(expr)
//                    val brkstmts = filterBreakStatements(s, ctx, env)
//                        .flatMap{ case Opt(c, b) => stmtPred(env, res, c and ctx)(b.asInstanceOf[Statement]) }
//                    r ++= brkstmts
//                    r
//
//                // conditional statements
//                case IfStatement(condition, thenBranch, elifs, elseBranch) =>
//                    var r = res
//                    if (elseBranch.isDefined)
//                        r ++= condStmtPred(x)(elseBranch.get)
//                    else
//                        r ++= condExprPred(x)(condition)
//                    r ++= elifs.flatMap{ case Opt(c, o) => basicPred(env, res, ctx and c)(o) }
//                    r ++= condStmtPred(x)(thenBranch)
//                    r
//                case ElifStatement(expr, thenBranch) =>
//                    condExprPred(x)(expr) ++ condStmtPred(x)(thenBranch)
//                case SwitchStatement(expr, s) =>
//                    var r = res
//                    val brkstmts = filterBreakStatements(s, ctx, env)
//                        .flatMap{ case Opt(c, b) => stmtPred(env, res, c and ctx)(b.asInstanceOf[Statement]) }
//                    r ++= brkstmts
//                    val c = env.featureExpr(expr)
//                    val defstmts = filterDefaultStatements(s, c, env)
//                    if (!isComplete(c)(defstmts))
//                        r ++= exprPred(env, r, ctx)(expr)
//                    r ++= condStmtPred(x)(s)
//                    r
//                case e: CaseStatement =>
//                    stmtPred(env, res, ctx and env.featureExpr(e))(e)
//                case e: DefaultStatement =>
//                    stmtPred(env, res, ctx and env.featureExpr(e))(e)
//
//                case e: CFGStmt =>
//                    val c = env.featureExpr(e)
//                    if (c and ctx isSatisfiable()) {
//                        val cu = getCFGStmtCtx(res, ctx, c)
//                        if (cu isSatisfiable())
//                            res ++ List(Opt(cu, e))
//                        else
//                            res
//                    } else
//                        res
//            }
//        }
//
//    private lazy val basicSucc: ((ASTEnv, CFGStmts, FeatureExpr)) => AST => CFGStmts =
//        paramAttr {
//            case x@(env, res, ctx) => {
//                // loops
//                case ForStatement(expr1, expr2, _, s) =>
//                    if (expr1.isDefined)
//                        exprSucc(x)(expr1.get)
//                    else if (expr2.isDefined)
//                        exprSucc(x)(expr2.get)
//                    else
//                        condStmtSucc(x)(s)
//                case WhileStatement(expr, _) =>
//                    exprSucc(x)(expr)
//                case DoStatement(_, s) =>
//                    condStmtSucc(x)(s)
//
//                case e: BreakStatement =>
//                    getBreakStmtContext(e, env) match {
//                        case None =>
//                            assert(assertion = false, "break statement should always occur within a for, do-while, while or switch statement")
//                            res
//                        case Some(s) =>
//                            stmtSucc(env, res, ctx and env.featureExpr(e))(s)
//                    }
//                case e: ContinueStatement =>
//                    lazy val y = (env, res, ctx and env.featureExpr(e))
//                    getContinueStmtContext(e, env) match {
//                        case None =>
//                            assert(assertion = false, "continue statement should always occur within a for, do-while, or while statement")
//                            res
//                        case Some(ForStatement(_, expr2, expr3, s)) =>
//                            if (expr3.isDefined)
//                                exprSucc(y)(expr3.get)
//                            else if (expr2.isDefined)
//                                exprSucc(y)(expr2.get)
//                            else
//                                condStmtSucc(y)(s)
//                        case Some(WhileStatement(expr, _)) =>
//                            exprSucc(y)(expr)
//                        case Some(DoStatement(expr, _)) =>
//                            exprSucc(y)(expr)
//                        case _ => List()
//                    }
//                case e: CaseStatement =>
//                    stmtSucc(env, res, ctx and env.featureExpr(e))(e)
//                case e: DefaultStatement =>
//                    stmtSucc(env, res, ctx and env.featureExpr(e))(e)
//
//                // conditional statements
//                case IfStatement(condition, _, _, _) =>
//                    condExprSucc(x)(condition)
//                case ElifStatement(condition, _) =>
//                    condExprSucc(x)(condition)
//                case SwitchStatement(expr, _) =>
//                    exprSucc(x)(expr)
//                case GotoStatement(target) =>
//                    exprSucc(x)(target)
//
//                case ReturnStatement(Some(expr)) =>
//                    exprSucc(x)(expr)
//
//                case e: CFGStmt =>
//                    val c = env.featureExpr(e)
//                    if (c and ctx isSatisfiable()) {
//                        val cu = getCFGStmtCtx(res, ctx, c)
//                        if (cu isSatisfiable())
//                            res ++ List(Opt(cu, e))
//                        else
//                            res
//                    } else
//                        res
//            }
//        }
//
//    private lazy val predComp: ((ASTEnv, CFGStmts, FeatureExpr)) => AST => CFGStmts =
//        paramAttr {
//            case x@(env, res, ctx) => {
//                case FunctionDef(_, _, _, stmt) =>
//                    var r = res
//                    r ++= predComp(x)(stmt)
//                    val retustmts = filterReturnStatements(stmt, ctx, env)
//                        .flatMap{ case Opt(c, a) => basicPred(env, res, ctx and c)(a) }
//                    r ++= retustmts
//                    r
//                case e@CompoundStatement(innerStatements) =>
//                    var r = res
//                    innerStatements.takeWhile {
//                        case _ =>
//                            !isComplete(ctx)(r)
//                    }.foreach {
//                        case Opt(_, a) =>
//                            r = basicPred(env, r, ctx)(a)
//                    }
//
//                    if (!isComplete(ctx)(r))
//                        predFollowing(env, r, ctx)(e)
//                    else
//                        r
//                case e => predFollowing(x)(e)
//            }
//        }
//
//    private lazy val succComp: ((ASTEnv, CFGStmts, FeatureExpr)) => AST => CFGStmts =
//        paramAttr {
//            case x@(env, res, ctx) => {
//                case FunctionDef(_, _, _, stmt) => succComp(x)(stmt)
//                case e@CompoundStatement(innerStatements) =>
//                    var r = res
//                    innerStatements.takeWhile {
//                        case _ =>
//                            !isComplete(ctx)(r)
//                    }.foreach {
//                        case Opt(_, a) =>
//                            r = basicSucc(env, r, ctx)(a)
//                    }
//
//                    if (!isComplete(ctx)(r))
//                        succFollowing(env, r, ctx)(e)
//                    else
//                        r
//                case e => succFollowing(x)(e)
//            }
//        }
//
//    // checks reference equality of e in a given structure
//    private lazy val isPartOf: Product => Any => Boolean =
//        paramAttr {
//            se => {
//                case e: Product if se.asInstanceOf[AnyRef].eq(e.asInstanceOf[AnyRef]) => true
//                case l: List[_] => l.exists(isPartOf(se)(_))
//                case e: Product => e.productIterator.toList.exists(isPartOf(se)(_))
//                case _ => false
//            }
//        }
//
//    private def getUnsatisfiedCtx(res: CFGStmts): FeatureExpr = {
//        res.map(_.condition).fold(FeatureExprFactory.False)(_ or _).not()
//    }
//
//    private def getCFGStmtCtx(res: CFGStmts, ctx: FeatureExpr, ectx: FeatureExpr): FeatureExpr = {
//        getUnsatisfiedCtx(res) and ctx and ectx
//    }
//
//    private lazy val isComplete: FeatureExpr => CFGStmts => Boolean =
//        paramAttr {
//            ctx => {
//                res =>
//                    res.map(_.condition).fold(FeatureExprFactory.False)(_ or _) equivalentTo ctx
//            }
//        }
//
//    @tailrec
//    private def getContinueStmtContext(a: Product, env: ASTEnv): Option[Statement] = {
//        parentAST(a, env) match {
//            case t: ForStatement => Some(t)
//            case t: WhileStatement => Some(t)
//            case t: DoStatement => Some(t)
//            case null => None
//            case p: Product => getContinueStmtContext(p, env)
//        }
//    }
//
//    @tailrec
//    private def getBreakStmtContext(a: Product, env: ASTEnv): Option[Statement] = {
//        parentAST(a, env) match {
//            case t: ForStatement => Some(t)
//            case t: WhileStatement => Some(t)
//            case t: DoStatement => Some(t)
//            case t: SwitchStatement => Some(t)
//            case null => None
//            case p: Product => getBreakStmtContext(p, env)
//        }
//    }
//
//    private lazy val predFollowing: ((ASTEnv, CFGStmts, FeatureExpr)) => AST => CFGStmts =
//        paramAttr {
//            case x@(env, res, ctx) => {
//                // case or default statements belong only to switch statements
//                case e: CaseStatement =>
//                    switStmtPred(x)(e)
//                case e: DefaultStatement =>
//                    switStmtPred(x)(e)
//                case se =>
//                    parentAST(se, env) match {
//                        // loops
//                        case e@ForStatement(Some(expr1), _, _, _) if isPartOf(se)(expr1) =>
//                            stmtPred(x)(e)
//                        case e@ForStatement(_, Some(expr2), Some(expr3), s) if isPartOf(se)(expr3) =>
//                            val rs = condStmtPred(x)(s)
//                            val rf = filterContinueStatements(s, env.featureExpr(e), env)
//
//                            if (!isComplete(ctx)(rs)) {
//                                rs ++ exprPred(env, rs, ctx)(expr2) ++ rf
//                            } else {
//                                rs ++ rf
//                            }
//                        case e@ForStatement(None, Some(expr2), None, One(CompoundStatement(List()))) =>
//                            val c = getCFGStmtCtx(res, ctx, env.featureExpr(expr2))
//                            if (c.isContradiction())
//                                stmtPred(x)(e)
//                            else
//                                exprPred(x)(expr2) ++ stmtPred(x)(e)
//                        case e@ForStatement(expr1, Some(expr2), expr3, s) if isPartOf(se)(expr2) =>
//                            var r = res
//                            if (expr1.isDefined)
//                                r ++= exprPred(x)(expr1.get)
//                            else
//                                r ++= stmtPred(x)(e)
//                            if (expr3.isDefined)
//                                r ++= exprPred(x)(expr3.get)
//                            else {
//                                r ++= condStmtPred(x)(s)
//                                r ++= filterContinueStatements(s, env.featureExpr(e), env)
//                            }
//                            res
//                        case e@ForStatement(expr1, expr2, expr3, s) if isPartOf(se)(s) =>
//                            if (expr2.isDefined)
//                                exprPred(x)(expr2.get)
//                            else if (expr3.isDefined)
//                                exprPred(x)(expr3.get)
//                            else {
//                                var r = res
//                                if (expr1.isDefined)
//                                    r ++= exprPred(x)(expr1.get)
//                                else
//                                    r ++= stmtPred(x)(e)
//
//                                r ++= condStmtPred(x)(s)
//                                r
//                            }
//                        case e@WhileStatement(expr, s) if isPartOf(se)(expr) =>
//                            stmtPred(x)(e) ++ condStmtPred(x)(s)
//                        case WhileStatement(expr, _) =>
//                            exprPred(x)(expr)
//                        case DoStatement(expr, s) if isPartOf(se)(expr) =>
//                            condStmtPred(x)(s)
//                        case e@DoStatement(expr, s) =>
//                            exprPred(x)(expr) ++ stmtPred(x)(e)
//
//                        // conditional statements
//                        case e@IfStatement(condition, _, _, _) if isPartOf(se)(condition) =>
//                            stmtPred(x)(e)
//                        case IfStatement(condition, thenBranch, _, _) if isPartOf(se)(thenBranch) =>
//                            condExprPred(x)(condition)
//                        case IfStatement(condition, _, elifs, elseBranch) if isPartOf(se)(elseBranch) =>
//                            var r = res
//
//                            elifs.reverse.takeWhile(_ => !isComplete(ctx)(r)).foreach {
//                                case Opt(_, ElifStatement(c, _)) =>
//                                    val re = condExprSucc(x)(c)
//
//                                    re.filter {
//                                        n => getUnsatisfiedCtx(r).and(n.condition).isSatisfiable()
//                                    } foreach {
//                                        n => r ++= List(n)
//                                    }
//                            }
//
//                            if (!isComplete(ctx)(r)) {
//                                condExprPred(env, r, ctx)(condition)
//                            } else
//                                r
//                        case e@ElifStatement(condition, _) if isPartOf(se)(condition) =>
//                            var r = res
//                            val elifs = prevASTElems(e, env).tail.asInstanceOf[List[Opt[ElifStatement]]]
//
//                            elifs.takeWhile(_ => !isComplete(ctx)(r)).foreach {
//                                case Opt(_, ElifStatement(c, _)) =>
//                                    val re = condExprSucc(x)(c)
//
//                                    re.filter {
//                                        n => getUnsatisfiedCtx(r).and(n.condition).isSatisfiable()
//                                    } foreach {
//                                        n => r ++= List(n)
//                                    }
//                            }
//
//                            if (!isComplete(ctx)(r)) {
//                                findPriorASTElem[IfStatement](e, env) match {
//                                    case None =>
//                                    case Some(IfStatement(icon, _, _, _)) =>
//                                        r ++= condExprPred(env, r, ctx)(icon)
//                                }
//                                r
//                            } else
//                                r
//                        case SwitchStatement(expr, s) if isPartOf(se)(s) =>
//                            exprPred(x)(expr)
//                        case e: SwitchStatement =>
//                            stmtPred(x)(e)
//                        case e: CaseStatement =>
//                            stmtPred(x)(e) ++ switStmtPred(x)(e)
//
//                        case e: CompoundStatement =>
//                            val r = stmtPred(x)(se.asInstanceOf[Statement])
//                            if (!isComplete(ctx)(r))
//                                predFollowing(env, r, ctx)(e)
//                            else
//                                r
//
//                        case e: FunctionDef =>
//                            val c = env.featureExpr(e)
//                            val uc = getCFGStmtCtx(res, ctx, c)
//                            var r = res
//                            if (uc.isSatisfiable())
//                                r = res ++ List(Opt(uc, e))
//                            else
//                                r = res
//                            r
//
//                        // pred of default is either the expression of the switch, which is
//                        // returned by handleSwitch, or a previous statement (e.g.,
//                        // switch (exp) {
//                        // ...
//                        // label1:
//                        // default: ...)
//                        // as part of a fall through (sequence of statements without a break and that we catch
//                        // with getStmtPred
//                        //                            case t: DefaultStatement => handleSwitch(t) ++ getStmtPred(t, ctx, oldres, env)
//                        //
//                        //                            case t: CompoundStatementExpr => followPred(t, ctx, oldres, env)
//                        //
//                        //                            case t: Statement => getStmtPred(t, ctx, oldres, env)
//                        //                            case t: FunctionDef =>
//                        //                                val ffexp = env.featureExpr(t)
//                        //                                if (predComplete(ctx, oldres)) oldres
//                        //                                else oldres ++ {
//                        //                                    val newresctx = getNewResCtx(oldres, ctx, ffexp)
//                        //                                    if (newresctx.isContradiction()) List()
//                        //                                    else List((newresctx, ffexp, t))
//                        //                                }
//                        case _ => res
//                    }
//            }
//        }
//
//    private lazy val succFollowing: ((ASTEnv, CFGStmts, FeatureExpr)) => AST => CFGStmts =
//        paramAttr {
//            case x@(env, res, ctx) => {
//                case se: ReturnStatement =>
//                    retuStmtSucc(x)(se)
//                case se =>
//                    parentAST(se, env) match {
//                        // loops
//                        case ForStatement(Some(expr1), expr2, _, s) if isPartOf(se)(expr1) =>
//                            if (expr2.isDefined)
//                                exprSucc(x)(expr2.get)
//                            else
//                                condStmtSucc(x)(s)
//                        case e@ForStatement(_, Some(expr2), expr3, s) if isPartOf(se)(expr2) =>
//                            val rt = succFollowing(x)(e)
//                            val rs = condStmtSucc(x)(s)
//                            if (!isComplete(ctx)(rs)) {
//                                val re =
//                                    if (expr3.isDefined)
//                                        exprSucc(x)(expr3.get)
//                                    else
//                                        exprSucc(x)(expr2)
//                                rt ++ rs ++ re
//                            } else rt ++ rs
//                        case ForStatement(_, expr2, Some(expr3), s) if isPartOf(se)(expr3) =>
//                            if (expr2.isDefined)
//                                exprSucc(x)(expr2.get)
//                            else
//                                condStmtSucc(x)(s)
//                        case ForStatement(_, expr2, expr3, s) if isPartOf(se)(s) =>
//                            if (expr3.isDefined)
//                                exprSucc(x)(expr3.get)
//                            else if (expr2.isDefined)
//                                exprSucc(x)(expr2.get)
//                            else
//                                condStmtSucc(x)(s)
//
//                        case e@WhileStatement(expr, s) if isPartOf(se)(expr) =>
//                            val rs = condStmtSucc(x)(s)
//                            val rt = succFollowing(x)(e)
//                            val re =
//                                if (!isComplete(ctx)(rs))
//                                    exprSucc(x)(expr)
//                                else
//                                    List()
//                            rs ++ re ++ rt
//                        case WhileStatement(expr, _) =>
//                            exprSucc(x)(expr)
//
//                        case e@DoStatement(expr, s) if isPartOf(se)(expr) =>
//                            val rs = condStmtSucc(x)(s)
//                            val rt = succFollowing(x)(e)
//                            val re =
//                                if (!isComplete(ctx)(rs))
//                                    exprSucc(x)(expr)
//                                else
//                                    List()
//                            rs ++ re ++ rt
//                        case DoStatement(expr, s) =>
//                            exprSucc(x)(expr)
//
//                        // conditional statements
//                        // condition of the if statement
//                        case e@IfStatement(condition, thenBranch, elifs, elseBranch) if isPartOf(se)(condition) =>
//                            var r = res
//
//                            elifs.takeWhile(_ => !isComplete(ctx)(r)).foreach {
//                                case Opt(_, ElifStatement(c, _)) =>
//                                    val re = condExprSucc(x)(c)
//
//                                    re.filter {
//                                        n => getUnsatisfiedCtx(r).and(n.condition).isSatisfiable()
//                                    } foreach {
//                                        n => r ++= List(n)
//                                    }
//                            }
//
//                            if (!isComplete(ctx)(r)) {
//                                if (elseBranch.isDefined)
//                                    r ++= condStmtSucc(x)(elseBranch.get)
//                                else
//                                    r = succFollowing(env, r, ctx)(e)
//                            }
//                            r ++ condStmtSucc(x)(thenBranch)
//                        // part of then branch, elifs, or else branch
//                        case e@IfStatement(_, _, _, _) =>
//                            stmtSucc(x)(e)
//                        // condition of the elif statement
//                        case e@ElifStatement(condition, thenBranch) if isPartOf(se)(condition) =>
//                            var r = res
//                            val elifs = nextASTElems(e, env).tail.asInstanceOf[List[Opt[ElifStatement]]]
//
//                            elifs.takeWhile(_ => !isComplete(ctx)(r)).foreach {
//                                case Opt(_, ElifStatement(c, _)) =>
//                                    val re = condExprSucc(x)(c)
//
//                                    re.filter {
//                                        n => getUnsatisfiedCtx(r).and(n.condition).isSatisfiable()
//                                    } foreach {
//                                        n => r ++= List(n)
//                                    }
//                            }
//
//                            if (!isComplete(ctx)(r)) {
//                                findPriorASTElem[IfStatement](e, env) match {
//                                    case None =>
//                                    case Some(i@IfStatement(_, _, _, None)) =>
//                                        r = succFollowing(env, r, ctx)(i)
//                                    case Some(IfStatement(_, _, _, Some(elseBranch))) =>
//                                        r = condStmtSucc(env, r, ctx)(elseBranch)
//                                }
//                            }
//                            r ++ condStmtSucc(x)(thenBranch)
//                        case e: ElifStatement =>
//                            succFollowing(x)(e)
//
//                        case e@GotoStatement(g) =>
//                            findPriorASTElem[FunctionDef](e, env) match {
//                                case None => res // should never happen, as parser ensures that goto statements belong to functions
//                                case Some(f) =>
//                                    var lstmts = filterAllASTElems[LabelStatement](f, env.featureExpr(e), env)
//
//                                    g match {
//                                        case Id(l) =>
//                                            lstmts = lstmts.filter{ y => y.id.name == l }
//
//                                            @tailrec
//                                            def pairwise[T](l: List[T], r: List[(T,T)] = List()): List[(T,T)] = {
//                                                l match {
//                                                    case Nil => r
//                                                    case y::ys => pairwise(ys, ys.map{ (y, _) })
//                                                }
//
//                                            }
//                                            val pwcombs = pairwise(lstmts.map(env.featureExpr(_)))
//                                            assert(assertion = pwcombs.forall{ case (a, b) => !(a equivalentTo b) }, "found label statements with equivalent annotation!")
//                                        case _ =>
//                                    }
//
//                                    if (lstmts.isEmpty)
//                                        stmtSucc(x)(e)
//                                    else
//                                        res ++ lstmts.map { ls => Opt(env.featureExpr(ls), ls) }
//                            }
//                        case e@SwitchStatement(expr, s) if isPartOf(se)(expr) =>
//                            var r = res
//                            // switch code (in s) before the first case statement is dead code
//                            // and can be used only to declare variables
//                            // see section 6.8.2.4 in the c standard
//                            val casstmts = filterCaseStatements(s, env.featureExpr(expr), env)
//                                .flatMap{ case Opt(c, a) => stmtSucc(env, res, ctx and c)(a.asInstanceOf[Statement]) }
//                            r ++= casstmts
//                            val defstmts = filterDefaultStatements(s, env.featureExpr(expr), env)
//                                .flatMap{ case Opt(c, a) => stmtSucc(env, res, ctx and c)(a.asInstanceOf[Statement]) }
//
//                            if (defstmts.isEmpty)
//                                r ++= stmtSucc(x)(e)
//                            else
//                                r ++= defstmts
//                            r
//
//                        case e: CompoundStatement =>
//                            val r = stmtSucc(x)(se.asInstanceOf[Statement])
//                            if (!isComplete(ctx)(r))
//                                succFollowing(env, r, ctx)(e)
//                            else
//                                r
//
//                        case e: ReturnStatement =>
//                            retuStmtSucc(x)(e)
//
//                        case e: FunctionDef =>
//                            val c = env.featureExpr(e)
//                            val uc = getCFGStmtCtx(res, ctx, c)
//                            var r = res
//                            if (uc.isSatisfiable())
//                                r = res ++ List(Opt(uc, e))
//                            else
//                                r = res
//                            r
//
//                        case e: Statement =>
//                            stmtSucc(x)(e)
//
//                        case _ => res
//                    }
//            }
//        }
//
//    private def filterCaseStatements(c: Conditional[Statement], ctx: FeatureExpr, env: ASTEnv): CFG = {
//        def filterCaseStatementsHelper(a: Any): CFG = {
//            a match {
//                case _: SwitchStatement => List()
//                case t: CaseStatement =>
//                    val c = env.featureExpr(t)
//                    if (c and ctx isSatisfiable())
//                        List(Opt(c, t))
//                    else
//                        List()
//                case l: List[_] => l.flatMap(filterCaseStatementsHelper)
//                case x: Product => x.productIterator.toList.flatMap(filterCaseStatementsHelper)
//                case _ => List()
//            }
//        }
//        filterCaseStatementsHelper(c)
//    }
//
//    private def filterDefaultStatements(c: Conditional[Statement], ctx: FeatureExpr, env: ASTEnv): CFG = {
//        def filterDefaultStatementsHelper(a: Any): CFG = {
//            a match {
//                case _: SwitchStatement => List()
//                case t: DefaultStatement =>
//                    val c = env.featureExpr(t)
//                    if (!(c and ctx).isContradiction())
//                        List(Opt(c, t))
//                    else
//                        List()
//                case l: List[_] => l.flatMap(filterDefaultStatementsHelper)
//                case x: Product => x.productIterator.toList.flatMap(filterDefaultStatementsHelper)
//                case _ => List()
//            }
//        }
//        filterDefaultStatementsHelper(c)
//    }
//
//    private def filterContinueStatements(c: Conditional[Statement], ctx: FeatureExpr, env: ASTEnv): CFG = {
//        def filterContinueStatementsHelper(a: Any): CFG = {
//            a match {
//                case e: ContinueStatement =>
//                    val c = env.featureExpr(e)
//                    if (c and ctx isSatisfiable())
//                        List(Opt(c, e))
//                    else
//                        List()
//                case _: ForStatement => List()
//                case _: WhileStatement => List()
//                case _: DoStatement => List()
//                case l: List[_] => l.flatMap(filterContinueStatementsHelper)
//                case x: Product => x.productIterator.toList.flatMap(filterContinueStatementsHelper)
//                case _ => List()
//            }
//        }
//        filterContinueStatementsHelper(c)
//    }
//
//    private def filterBreakStatements(c: Conditional[Statement], ctx: FeatureExpr, env: ASTEnv): CFG = {
//        def filterBreakStatementsHelper(a: Any): CFG = {
//            a match {
//                case e: BreakStatement =>
//                    val c = env.featureExpr(e)
//                    if (c and ctx isSatisfiable())
//                        List(Opt(c, e))
//                    else
//                        List()
//                case _: ForStatement => List()
//                case _: WhileStatement => List()
//                case _: DoStatement => List()
//                case _: SwitchStatement => List()
//                case l: List[_] => l.flatMap(filterBreakStatementsHelper)
//                case p: Product => p.productIterator.toList.flatMap(filterBreakStatementsHelper)
//                case _ => List()
//            }
//        }
//        filterBreakStatementsHelper(c)
//    }
//
//    private def filterReturnStatements(c: CompoundStatement, ctx: FeatureExpr, env: ASTEnv): CFG = {
//        def filterReturnStatementsHelper(a: Any): CFG = {
//            a match {
//                case e: ReturnStatement =>
//                    val c = env.featureExpr(e)
//                    if (c and ctx isSatisfiable())
//                        List(Opt(c, e))
//                    else
//                        List()
//                case _: NestedFunctionDef => List()
//                case l: List[_] => l.flatMap(filterReturnStatementsHelper)
//                case p: Product => p.productIterator.toList.flatMap(filterReturnStatementsHelper)
//                case _ => List()
//            }
//        }
//        filterReturnStatementsHelper(c)
//    }
//}
