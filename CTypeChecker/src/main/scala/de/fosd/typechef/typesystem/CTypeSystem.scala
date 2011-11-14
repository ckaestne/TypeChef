package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.conditional._
import de.fosd.typechef.parser.Position

/**
 * checks an AST (from CParser) for type errors (especially dangling references)
 *
 * performs type checking in a single tree-walk, uses lookup functions from various traits
 *
 * @author kaestner
 *
 */

trait CTypeSystem extends CTypes with CEnv with CDeclTyping with CTypeEnv with CExprTyping {

    def typecheckTranslationUnit(tunit: TranslationUnit): Unit = {
        val env = new Env(null, null, new StructEnv(), Map(), None)
        val finalEnv = checkTranslationUnit(tunit, FeatureExpr.base, env)
        //        finalEnv.errorList
    }


    private def checkTranslationUnit(tunit: TranslationUnit, featureExpr: FeatureExpr, initialEnv: Env): Env = {
        var env = initialEnv
        for (Opt(f, e) <- tunit.defs) {
            env = checkExternalDef(e, featureExpr and f, env)
        }
        env
    }

    private def checkExternalDef(externalDef: ExternalDef, featureExpr: FeatureExpr, env: Env): Env = {
        debugCheckExternal(externalDef)
        externalDef match {
            case _: EmptyExternalDef => env
            case _: Pragma => env //ignore
            case _: AsmExpr => env //ignore
            case e: TypelessDeclaration => assert(false, "will not occur " + e); env //ignore
            case d: Declaration =>
                addDeclarationToEnvironment(d, featureExpr, env)
            case FunctionDef(specifiers, declarator, oldStyleParameters, stmt) =>
                checkFunction(specifiers, declarator, oldStyleParameters, stmt, featureExpr, env)
        }
    }


    private def checkFunction(specifiers: List[Opt[Specifier]], declarator: Declarator, oldStyleParameters: List[Opt[OldParameterDeclaration]], stmt: CompoundStatement, featureExpr: FeatureExpr, env: Env): Env = {
        //TODO check function redefinitions
        val funType = getFunctionType(specifiers, declarator, oldStyleParameters, featureExpr, env)
        funType.map(t => assert(t.isFunction))
        val expectedReturnType = funType.map(t => t.asInstanceOf[CFunction].ret).simplify(featureExpr)

        //check body (add parameters to environment)
        val innerEnv = env.addVars(parameterTypes(declarator, featureExpr, env))
        getStmtType(stmt, featureExpr, innerEnv) //ignore changed environment, to enforce scoping!

        //check actual return type against declared return type
        //TODO check that something was returned at all
        //add type to environment for remaining code
        val newEnv = env.addVar(declarator.getName, featureExpr, funType)

        newEnv
    }

    private def addDeclarationToEnvironment(d: Declaration, featureExpr: FeatureExpr, oldEnv: Env): Env = {
        var env = oldEnv
        //add declared variables to variable typing environment
        env = env.addVars(getDeclaredVariables(d, featureExpr, env))
        //declared struct?
        env = env.updateStructEnv(addStructDeclarationToEnv(d, featureExpr, env))
        //declared enums?
        env = env.updateEnumEnv(addEnumDeclarationToEnv(d, featureExpr, env.enumEnv))
        //declared typedefs?
        env = env.addTypedefs(recognizeTypedefs(d, featureExpr, env))
        env
    }


    /**
     * returns a type and a changed environment for subsequent statements
     *
     * most statements do not have types; type information extracted from sparse (evaluate.c)
     */
    def getStmtType(stmt: Statement, featureExpr: FeatureExpr, env: Env): (Conditional[CType], Env) = {
        def checkStmtF(stmt: Statement, newFeatureExpr: FeatureExpr) = getStmtType(stmt, newFeatureExpr, env)
        def checkStmt(stmt: Statement) = checkStmtF(stmt, featureExpr)
        def checkCStmtF(stmt: Conditional[Statement], newFeatureExpr: FeatureExpr) = stmt.mapf(newFeatureExpr, {(f, t) => checkStmtF(t, f)})
        def checkCStmt(stmt: Conditional[Statement]) = checkCStmtF(stmt, featureExpr)
        def checkOCStmt(stmt: Option[Conditional[Statement]]) = stmt map checkCStmt

        def expectScalar(expr: Expr, ctx: FeatureExpr = featureExpr) = checkExprX(expr, isScalar, {c => "expected scalar, found " + c}, ctx)
        def expectIntegral(expr: Expr, ctx: FeatureExpr = featureExpr) = checkExprX(expr, isIntegral, {c => "expected int, found " + c}, ctx)
        //        def checkFunctionCall(call: PostfixExpr) = checkExpr(call, !_.isUnknown, {ct => "cannot resolve function call, found " + ct})
        //        def checkIdentifier(id: Id) = checkExpr(id, !_.isUnknown, {ct => "identifier " + id.name + " unknown: " + ct})
        def checkExpr(expr: Expr) = checkExprF(expr, featureExpr)
        def checkExprF(expr: Expr, ctx: FeatureExpr) = checkExprX(expr, !_.isUnknown, {ct => "cannot resolve expression, found " + ct}, ctx)
        def checkExprX(expr: Expr, check: CType => Boolean, errorMsg: CType => String, featureExpr: FeatureExpr) =
            performExprCheck(expr, check, errorMsg, featureExpr, env)
        def nop = (One(CUnknown("no type for " + stmt)), env)

        stmt match {
            case CompoundStatement(innerStmts) =>
                //get a type of every inner feature, propagate environments between siblings, collect OptList of types (with one type for every statement, under the same conditions)
                var innerEnv = env
                val typeOptList: List[Opt[Conditional[CType]]] =
                    for (Opt(stmtFeature, innerStmt) <- innerStmts) yield {
                        val (stmtType, newEnv) = getStmtType(innerStmt, featureExpr and stmtFeature, innerEnv)
                        innerEnv = newEnv
                        Opt(stmtFeature, stmtType)
                    }

                //return last type
                val lastType: Conditional[Option[Conditional[CType]]] = ConditionalLib.lastEntry(typeOptList)
                val t: Conditional[CType] = lastType.mapr({
                    case None => One(CVoid())
                    case Some(ctype) => ctype
                }) simplify;

                //return original environment, definitions don't leave this scope
                (t, env)


            case ExprStatement(expr) =>
                //expressions do not change the environment
                (checkExpr(expr), env)

            case DeclarationStatement(d) =>
                (One(CVoid()), addDeclarationToEnvironment(d, featureExpr, env))

            case NestedFunctionDef(_, spec, decl, oldSP, stmt) =>
                (One(CVoid()), checkFunction(spec, decl, oldSP, stmt, featureExpr, env))

            case WhileStatement(expr, stmt) => expectScalar(expr); checkCStmt(stmt); nop //spec
            case DoStatement(expr, smt) => expectScalar(expr); checkStmt(stmt); nop //spec
            case ForStatement(expr1, expr2, expr3, stmt) =>
                if (expr1.isDefined) checkExpr(expr1.get)
                if (expr2.isDefined) expectScalar(expr2.get) //spec
                if (expr3.isDefined) checkExpr(expr3.get)
                checkCStmt(stmt)
                nop
            //case GotoStatement(expr) => checkExpr(expr) TODO check goto against labels
            case r@ReturnStatement(mexpr) =>
                assert(env.expectedReturnType.isDefined)
                val expectedReturnType = env.expectedReturnType.get
                mexpr match {

                    case None =>
                        if (expectedReturnType map (_ == CVoid()) exists (!_))
                            issueError(featureExpr, "no return expression, expected type " + expectedReturnType, r)
                    case Some(expr) =>
                        checkExprX(expr, et => expectedReturnType map {rt => coerce(rt, et)} forall (x => x), {c => "incorrect return type, expected " + expectedReturnType + ", found " + c}, featureExpr)
                }
                nop

            case CaseStatement(expr, stmt) => checkExpr(expr); checkOCStmt(stmt); nop
            case IfStatement(expr, tstmt, elifstmts, estmt) =>
                expectScalar(expr) //spec
                checkCStmt(tstmt)
                for (Opt(elifFeature, ElifStatement(elifExpr, elifStmt)) <- elifstmts) {
                    expectScalar(elifExpr, featureExpr and elifFeature)
                    checkCStmtF(elifStmt, featureExpr and elifFeature)
                }
                checkOCStmt(estmt)
                nop

            case SwitchStatement(expr, stmt) => expectIntegral(expr); checkCStmt(stmt); nop //spec
            case DefaultStatement(stmt) => checkOCStmt(stmt); nop

            case EmptyStatement() => nop
            case ContinueStatement() => nop
            case BreakStatement() => nop

            case GotoStatement(_) => nop //TODO check goto against labels
            case LabelStatement(_, _) => nop
            case LocalLabelDeclaration(ids) => nop
        }
    }


    def debugCheckExternal(externalDef: ExternalDef)
    def issueError(condition: FeatureExpr, msg: String, where: AST, whereElse: AST = null)
    def issueTypeError(condition: FeatureExpr, msg: String, where: AST, ctype: CType)

    //
    //
    //    def performCheck(node: Attributable): Unit = node match {
    //        case fun: FunctionDef =>
    //            val priorDefs = fun -> priorDefinitions
    //            for (priorFun <- priorDefs)
    //                if (!mex(fun -> featureExpr, priorFun -> featureExpr))
    //                    issueError(fun -> featureExpr, "function redefinition of " + fun.getName + " in context " + (fun -> featureExpr) + "; prior definition in context " + (priorFun -> featureExpr), fun, priorFun)
    //        //case GotoStatement(expr) => checkExpr(expr)
    //    }
    //
    //


    private def performExprCheck(expr: Expr, check: CType => Boolean, errorMsg: CType => String, context: FeatureExpr, env: Env): Conditional[CType] =
        if (context.isSatisfiable()) {
            val ct = getExprType(expr, context, env).simplify(context)
            ct.mapf(context, {
                (f, c) => if (!check(c)) issueTypeError(f, errorMsg(c), expr, c)
            })
            ct
        } else One(CUnknown("unsatisfiable condition for expression"))


    //
    //    //
    //    //    def checkFunctionCallTargets(source: AST, name: String, callerFeature: FeatureExpr, targets: List[Entry]) = {
    //    //        if (!targets.isEmpty) {
    //    //            //condition: feature implies (target1 or target2 ...)
    //    //            functionCallChecks += 1
    //    //            val condition = callerFeature.implies(targets.map(_.feature).foldLeft(FeatureExpr.base.not)(_.or(_)))
    //    //            if (condition.isTautology(null) || condition.isTautology(featureModel)) {
    //    //                dbgPrintln(" always reachable " + condition)
    //    //                None
    //    //            } else {
    //    //                dbgPrintln(" not always reachable " + callerFeature + " => " + targets.map(_.feature).mkString(" || "))
    //    //                Some(functionCallErrorMessages.get(name) match {
    //    //                    case None => ErrorMsgs(name, List((callerFeature, source)), targets)
    //    //                    case Some(err: ErrorMsgs) => err.withNewCaller(source, callerFeature)
    //    //                })
    //    //            }
    //    //        } else {
    //    //            dbgPrintln("dead")
    //    //            Some(ErrorMsgs.errNoDecl(name, source, callerFeature))
    //    //        }
    //    //    }
    //
    //    //
    //    //
    //    //    def checkFunctionRedefinition(env: LookupTable) {
    //    //        val definitions = env.byNames
    //    //        for ((name, defs) <- definitions) {
    //    //            if (defs.size > 1) {
    //    //                var fexpr = defs.head.feature
    //    //                for (adef <- defs.tail) {
    //    //                    if (!(adef.feature mex fexpr).isTautology(featureModel)) {
    //    //                        dbgPrintln("function " + name + " redefined with feature " + adef.feature + "; previous: " + fexpr)
    //    //                        functionRedefinitionErrorMessages = RedefErrorMsg(name, adef, fexpr) :: functionRedefinitionErrorMessages
    //    //                    }
    //    //                    fexpr = fexpr or adef.feature
    //    //                }
    //    //            }
    //    //        }
    //    //    }
    //    //
    //    //    val checkFunctionCalls: Attributable ==> Unit = attr {
    //    //        case obj => {
    //    //            // Process the errors of the children of t
    //    //            for (child <- obj.children)
    //    //                checkFunctionCalls(child)
    //    //            obj match {
    //    //            //function call (XXX: PG: not-so-good detection, but will work for typical code).
    //    //                case e@PostfixExpr(Id(name), FunctionCall(_)) => {
    //    //                    //Omit feat2, for typical code a function call is always a function call, even if the parameter list is conditional.
    //    //                    checkFunctionCall(e -> env, e, name, e -> presenceCondition)
    //    //                }
    //    //                case _ =>
    //    //            }
    //    //        }
    //    //    }
    //    //
    //    //
    //    //    def checkFunctionCall(table: LookupTable, source: AST, name: String, callerFeature: FeatureExpr) {
    //    //        val targets: List[Entry] = table.find(name)
    //    //        dbgPrint("function " + name + " found " + targets.size + " targets: ")
    //    //        checkFunctionCallTargets(source, name, callerFeature, targets) match {
    //    //            case Some(newEntry) =>
    //    //                functionCallErrorMessages = functionCallErrorMessages.updated(name, newEntry)
    //    //            case _ => ()
    //    //        }
    //    //    }

}


// trait, mostly for test cases
trait NoErrorReporting {
    def debugCheckExternal(externalDef: ExternalDef) {}
    def issueError(condition: FeatureExpr, msg: String, where: AST, whereElse: AST = null) {}
    def issueTypeError(condition: FeatureExpr, msg: String, where: AST, ctype: CType) {}
}


class CTypeSystemFrontend(iast: TranslationUnit, featureModel: FeatureModel = null) extends CTypeSystem {


    abstract class ErrorMsg(condition: FeatureExpr, msg: String, location: (Position, Position)) {
        override def toString =
            "[" + condition + "] " + location._1 + "--" + location._2 + "\n\t" + msg
    }

    class SimpleError(condition: FeatureExpr, msg: String, where: AST) extends ErrorMsg(condition, msg, where.rangeClean)
    class TypeError(condition: FeatureExpr, msg: String, where: AST, ctype: CType) extends ErrorMsg(condition, msg, where.rangeClean)

    def prettyPrintType(ctype: Conditional[CType]): String =
        Conditional.toOptList(ctype).map(o => o.feature.toString + ": \t" + o.entry).mkString("\n")

    private def indentAllLines(s: String): String =
        s.lines.map("\t\t" + _).foldLeft("")(_ + "\n" + _)

    var errors: List[ErrorMsg] = List()


    val DEBUG_PRINT = false

    def dbgPrint(o: Any) = if (DEBUG_PRINT) print(o)

    def dbgPrintln(o: Any) = if (DEBUG_PRINT) println(o)

    val verbose = true


    var externalDefCounter: Int = 0
    def debugCheckExternal(externalDef: ExternalDef) = {
        externalDefCounter = externalDefCounter + 1
        if (verbose)
            println("check " + externalDefCounter + "/" + iast.defs.size + ". line " + externalDef.getPositionFrom.getLine + ". err " + errors.size)
    }
    def issueError(condition: FeatureExpr, msg: String, where: AST, whereElse: AST = null) =
        errors = new SimpleError(condition, msg, where) :: errors
    def issueTypeError(condition: FeatureExpr, msg: String, where: AST, ctype: CType) =
        errors = new TypeError(condition, msg, where, ctype) :: errors


    def checkAST: Boolean = {

        typecheckTranslationUnit(iast)


        if (errors.isEmpty)
            println("No type errors found.")
        else {
            println("Found " + errors.size + " type errors: ");
            for (e <- errors.reverse)
                println("  - " + e)
        }
        println("\n")
        return errors.isEmpty
    }
}

