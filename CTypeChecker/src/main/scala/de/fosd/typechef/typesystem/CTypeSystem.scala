package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr._
import org.kiama.attribution.Attribution._
import org.kiama._
import attribution.Attributable
import de.fosd.typechef.conditional._
import de.fosd.typechef.parser.{WithPosition, Position, NoPosition}

/**
 * checks an AST (from CParser) for type errors (especially dangling references)
 *
 * performs type checking in a single tree-walk, uses lookup functions from various traits
 *
 * @author kaestner
 *
 */
class CTypeSystem(featureModel: FeatureModel = null) extends CTypeAnalysis with FeatureExprLookup with EnforceTreeHelper {

    //    var functionCallChecks = 0

    /*
    * This dictionary groups error messages by function, consolidating duplicate warnings together.
    */
    //    var functionCallErrorMessages: Map[String, ErrorMsgs] = Map()
    //    var functionRedefinitionErrorMessages: List[RedefErrorMsg] = List()

    val startPosition: Attributable ==> Position = {
        case a: Attributable => (a -> positionRange)._1
    }
    val positionRange: Attributable ==> (Position, Position) = attr {
        case a: WithPosition with Attributable =>
            if (a.hasPosition)
                a.range.get
            else
            if (a.parent == null) (NoPosition, NoPosition) else a.parent -> positionRange
        case a => if (a.parent == null) (NoPosition, NoPosition) else a.parent -> positionRange
    }

    abstract class ErrorMsg(condition: FeatureExpr, msg: String, location: (Position, Position)) {
        override def toString =
            "[" + condition + "] " + location._1 + "--" + location._2 + "\n\t" + msg
    }

    class SimpleError(condition: FeatureExpr, msg: String, where: AST) extends ErrorMsg(condition, msg, where -> positionRange)
    class TypeError(condition: FeatureExpr, msg: String, where: AST, ctype: CType) extends ErrorMsg(condition, msg, where -> positionRange)

    def prettyPrintType(ctype: Conditional[CType]): String =
        Conditional.toOptList(ctype).map(o => o.feature.toString + ": \t" + o.entry).mkString("\n")

    private def indentAllLines(s: String): String =
        s.lines.map("\t\t" + _).foldLeft("")(_ + "\n" + _)

    var errors: List[ErrorMsg] = List()


    val DEBUG_PRINT = false

    def dbgPrint(o: Any) = if (DEBUG_PRINT) print(o)

    def dbgPrintln(o: Any) = if (DEBUG_PRINT) println(o)

    private val checkNode: Attributable ==> Unit = attr {
        case obj => {
            // Process the errors of the children of t
            for (child <- obj.children)
                child -> checkNode
            checkTree(obj)
            checkAssumptions(obj)
            performCheck(obj)
        }
    }


    def checkAST(iast: TranslationUnit): Boolean = {
        val ast = prepareAST(iast)

        val verbose = true
        var i = 0
        for (Opt(_, d) <- ast.defs) {
            //            if (i<2000)
            checkNode(d)
            if (verbose) {
                i = i + 1
                println("check " + i + "/" + ast.defs.size + ". line " + (d -> startPosition).getLine + ". err " + errors.size)
            }
        }

        if (errors.isEmpty)
            println("No type errors found.")
        else {
            println("Found " + errors.size + " type errors: ");
            for (e <- errors.reverse)
                println("  - " + e)
        }
        //        println("(performed " + functionCallChecks + " checks regarding function calls)");
        println("\n")
        return errors.isEmpty
    }


    def performCheck(node: Attributable): Unit = node match {
        case fun: FunctionDef => //check function redefinitions
            val priorDefs = fun -> priorDefinitions
            for (priorFun <- priorDefs)
                if (!mex(fun -> featureExpr, priorFun -> featureExpr))
                    issueError(fun -> featureExpr, "function redefinition of " + fun.getName + " in context " + (fun -> featureExpr) + "; prior definition in context " + (priorFun -> featureExpr), fun, priorFun)

        case expr@PostfixExpr(_, FunctionCall(_)) => // check function calls in PostfixExpressions
            checkFunctionCall(expr)
        //        case id: Id =>//not a good idea. not all identifier are expressions?
        //            checkIdentifier(id)

        case ExprStatement(expr) => checkExpr(expr)
        case WhileStatement(expr, _) => expectScalar(expr) //spec
        case DoStatement(expr, _) => expectScalar(expr) //spec
        case ForStatement(expr1, expr2, expr3, _) =>
            if (expr1.isDefined) checkExpr(expr1.get)
            if (expr2.isDefined) expectScalar(expr2.get) //spec
            if (expr3.isDefined) checkExpr(expr3.get)
        //case GotoStatement(expr) => checkExpr(expr) TODO check goto against labels
        case r@ReturnStatement(expr) =>
            val funTypes: Conditional[CType] = r -> surroundingFunType
            funTypes.simplify(r -> featureExpr).mapf(r -> featureExpr,
                (fFeature, fType) => fType match {
                    case CFunction(_, returnType) =>
                        if (returnType == CVoid()) {
                            if (expr.isDefined) issueError(fFeature, "return statement with expression despite void return type", r)
                        } else {
                            if (!expr.isDefined) issueError(fFeature, "no return expression, expected type " + returnType, r)
                            else {
                                checkExpr(
                                fFeature and (expr.get -> featureExpr),
                                expr.get, {r => coerce(returnType, r)}, {c => "incorrect return type, expected " + returnType + ", found " + c})
                            }
                        }
                    case e =>
                        issueError(fFeature, "return statement outside a function definition " + funType, r) //should not occur
                })

        case CaseStatement(expr, _) => checkExpr(expr)
        case IfStatement(expr, _, _, _) => expectScalar(expr) //spec
        case ElifStatement(expr, _) => expectScalar(expr) //spec
        case SwitchStatement(expr, _) => expectIntegral(expr) //spec

        case _ =>
    }


    private def expectScalar(expr: Expr) {
        checkExpr(expr, isScalar, {c => "expected scalar, found " + c})
    }
    private def expectIntegral(expr: Expr) {
        checkExpr(expr, isIntegral, {c => "expected int, found " + c})
    }
    private def checkFunctionCall(call: PostfixExpr) {
        checkExpr(call, !_.isUnknown, {ct => "cannot resolve function call, found " + ct})
    }
    private def checkIdentifier(id: Id) {
        checkExpr(id, !_.isUnknown, {ct => "identifier " + id.name + " unknown: " + ct})
    }

    private def checkExpr(expr: Expr): Unit =
        checkExpr(expr, !_.isUnknown, {ct => "cannot resolve expression, found " + ct})

    private def checkExpr(expr: Expr, check: CType => Boolean, errorMsg: CType => String): Unit =
        checkExpr(expr -> featureExpr, expr, check, errorMsg)

    private def checkExpr(context: FeatureExpr, expr: Expr, check: CType => Boolean, errorMsg: CType => String): Unit =
        if (context.isSatisfiable()) {
            val ct = ctype(expr).simplify(context)
            ct.mapf(context, {
                (f, c) => if (!check(c)) issueTypeError(f, errorMsg(c), expr, c)
            })
        }

    /**
     * enforce certain assumptions about the layout of the AST
     *
     * these can later be relaxed or automatically ensured by tree transformations
     * before type checking
     */
    def checkAssumptions(node: Attributable): Unit = node match {
        //        case x: X => assert(false, "X not supported, yet")
        case _ =>
    }

    /**
     * TODO additional assumptions:
     * * typedef specifier applies to the whole declaration
     *
     */
    private def checkTree(node: Attributable) {
        for (c <- node.children) assert(c.parent == node, "Child " + c + " points to different parent:\n  " + c.parent + "\nshould be\n  " + node)

    }

    private def assertNoVariability[T](l: List[Opt[T]]) {
        def noVariability(o: Opt[T]) =
            (o.feature == FeatureExpr.base) ||
                    (o -> featureExpr implies (o.feature)).isTautology
        assert(l.forall(noVariability), "found unexpected variability in " + l)
    }

    private def mex(a: FeatureExpr, b: FeatureExpr): Boolean = (a mex b).isTautology(featureModel)

    private def issueError(condition: FeatureExpr, msg: String, where: AST, whereElse: AST = null) {
        errors = new SimpleError(condition, msg, where) :: errors
    }
    //    private def issueError(msg: String, where: AST, whereElse: AST = null) {
    //        errors = new SimpleError(FeatureExpr.base, msg, where) :: errors
    //    }

    private def issueTypeError(condition: FeatureExpr, msg: String, where: AST, ctype: CType) {
        errors = new TypeError(condition, msg, where, ctype) :: errors
    }

    //
    //    def checkFunctionCallTargets(source: AST, name: String, callerFeature: FeatureExpr, targets: List[Entry]) = {
    //        if (!targets.isEmpty) {
    //            //condition: feature implies (target1 or target2 ...)
    //            functionCallChecks += 1
    //            val condition = callerFeature.implies(targets.map(_.feature).foldLeft(FeatureExpr.base.not)(_.or(_)))
    //            if (condition.isTautology(null) || condition.isTautology(featureModel)) {
    //                dbgPrintln(" always reachable " + condition)
    //                None
    //            } else {
    //                dbgPrintln(" not always reachable " + callerFeature + " => " + targets.map(_.feature).mkString(" || "))
    //                Some(functionCallErrorMessages.get(name) match {
    //                    case None => ErrorMsgs(name, List((callerFeature, source)), targets)
    //                    case Some(err: ErrorMsgs) => err.withNewCaller(source, callerFeature)
    //                })
    //            }
    //        } else {
    //            dbgPrintln("dead")
    //            Some(ErrorMsgs.errNoDecl(name, source, callerFeature))
    //        }
    //    }

    //
    //
    //    def checkFunctionRedefinition(env: LookupTable) {
    //        val definitions = env.byNames
    //        for ((name, defs) <- definitions) {
    //            if (defs.size > 1) {
    //                var fexpr = defs.head.feature
    //                for (adef <- defs.tail) {
    //                    if (!(adef.feature mex fexpr).isTautology(featureModel)) {
    //                        dbgPrintln("function " + name + " redefined with feature " + adef.feature + "; previous: " + fexpr)
    //                        functionRedefinitionErrorMessages = RedefErrorMsg(name, adef, fexpr) :: functionRedefinitionErrorMessages
    //                    }
    //                    fexpr = fexpr or adef.feature
    //                }
    //            }
    //        }
    //    }
    //
    //    val checkFunctionCalls: Attributable ==> Unit = attr {
    //        case obj => {
    //            // Process the errors of the children of t
    //            for (child <- obj.children)
    //                checkFunctionCalls(child)
    //            obj match {
    //            //function call (XXX: PG: not-so-good detection, but will work for typical code).
    //                case e@PostfixExpr(Id(name), FunctionCall(_)) => {
    //                    //Omit feat2, for typical code a function call is always a function call, even if the parameter list is conditional.
    //                    checkFunctionCall(e -> env, e, name, e -> presenceCondition)
    //                }
    //                case _ =>
    //            }
    //        }
    //    }
    //
    //
    //    def checkFunctionCall(table: LookupTable, source: AST, name: String, callerFeature: FeatureExpr) {
    //        val targets: List[Entry] = table.find(name)
    //        dbgPrint("function " + name + " found " + targets.size + " targets: ")
    //        checkFunctionCallTargets(source, name, callerFeature, targets) match {
    //            case Some(newEntry) =>
    //                functionCallErrorMessages = functionCallErrorMessages.updated(name, newEntry)
    //            case _ => ()
    //        }
    //    }

}

