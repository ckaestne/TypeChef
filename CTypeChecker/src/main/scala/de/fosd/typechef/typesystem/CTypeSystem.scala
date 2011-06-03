//package de.fosd.typechef.typesystem
//
//import de.fosd.typechef.parser.c._
//import de.fosd.typechef.featureexpr._
//import org.kiama.attribution.DynamicAttribution._
//import org.kiama._
//import attribution.Attributable
//
///**
// * checks an AST (from CParser) for type errors (especially dangling references)
// *
// * performs type checking in a single tree-walk
// *
// * @author kaestner
// *
// */
//class CTypeSystem(featureModel: FeatureModel = null) extends CTypeAnalysis {
//    var functionCallChecks = 0
//
//    /*
//     * This dictionary groups error messages by function, consolidating duplicate warnings together.
//     */
//    var functionCallErrorMessages: Map[String, ErrorMsgs] = Map()
//    var functionRedefinitionErrorMessages: List[RedefErrorMsg] = List()
//
//    val DEBUG_PRINT = false
//    def dbgPrint(o: Any) = if (DEBUG_PRINT) print(o)
//    def dbgPrintln(o: Any) = if (DEBUG_PRINT) println(o)
//
//
//    def checkAST(ast: TranslationUnit): Boolean = {
//
//        ast -> checkFunctionCalls
//        if (!ast.defs.isEmpty) {
//            val lastEnv = ast.defs.last.entry -> env
//            checkFunctionRedefinition(lastEnv)
//        }
//
//        if (functionCallErrorMessages.values.isEmpty && functionRedefinitionErrorMessages.isEmpty)
//            println("No type errors found.")
//        else {
//            println("Type Errors: ");
//            println(functionCallErrorMessages.values.mkString("\n"))
//            println(functionRedefinitionErrorMessages.mkString("\n"))
//        }
//        println("(performed " + functionCallChecks + " checks regarding function calls)");
//
//        return (functionCallErrorMessages.values.isEmpty && functionRedefinitionErrorMessages.isEmpty)
//    }
//
//    //    class TSVisitor extends ASTVisitor {
//    //        var in: List[Int] = List()
//    //        private def isStatementLevel = in.contains(2)
//    //        override def visit(ast: AST, feature: FeatureExpr) {
//    //            in = (ast match {
//    //                case s: Statement => 2
//    //                case _ => 1
//    //            }) :: in
//    //            //for (a<-in)
//    //            //	print("  ")
//    //            //println(ast.getClass.getName)
//    //
//    //            ast match {
//    //            /**** declarations ****/
//    //            //function definition
//    //                case FunctionDef(specifiers, DeclaratorId(pointers, Id(name), extensions), params, stmt) => addToLookupTableAndCheckForDuplicates(new LFunctionDef(name, "", currentScope, feature))
//    //                //function declaration and other declarations
//    //                case ADeclaration(specifiers, initDecls) if (!isStatementLevel) =>
//    //                    for (initDecl <- initDecls.toList.flatten)
//    //                        initDecl.entry match {
//    //                            case InitDeclaratorI(DeclaratorId(_, Id(name), _), _, _) => table = table add (new LDeclaration(name, "", currentScope, feature))
//    //                            case InitDeclaratorE(DeclaratorId(_, Id(name), _), _, _) => table = table add (new LDeclaration(name, "", currentScope, feature))
//    //                            case _ =>
//    //                        }
//    //
//    //                /**** references ****/
//    //                //function call (XXX: PG: not-so-good detection, but will work for typical code).
//    //                case PostfixExpr(Id(name), Opt(feat2, FunctionCall(_)) :: _) => checkFunctionCall(ast, name, feature /* and feat2 */)
//    //                //Omit feat2, for typical code a function call is always a function call, even if the parameter list is conditional.
//    //                case _ =>
//    //            }
//    //        }
//    //        override def postVisit(ast: AST, feature: FeatureExpr) {
//    //            in = in.tail
//    //        }
//    //
//    //    }
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
//    //    def addToLookupTableAndCheckForDuplicates(entry: LFunctionDef) = {
//    //        val existingEntries = table.find(entry.name).filter(_.isInstanceOf[LFunctionDef])
//    //        for (otherEntry <- existingEntries) {
//    //            if (!(otherEntry.feature and entry.feature).isContradiction(featureModel)) {
//    //                dbgPrintln("function " + entry.name + " redefined with feature " + entry.feature + "; previous: " + otherEntry)
//    //                functionRedefinitionErrorMessages = RedefErrorMsg(entry.name, entry, otherEntry) :: functionRedefinitionErrorMessages
//    //            }
//    //        }
//    //
//    //        table = table.add(entry)
//    //    }
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
//
//}