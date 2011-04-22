package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.Opt
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr._
import FeatureExpr.base
import org.kiama.attribution.DynamicAttribution._
import org.kiama._
import attribution.Attributable
import org.kiama.rewriting.Rewriter._
/**
 * checks an AST (from CParser) for type errors (especially dangling references)
 *
 * performs type checking in a single tree-walk
 *
 * @author kaestner
 *
 */
class TypeSystem(featureModel: FeatureModel = null) {
    var functionCallChecks = 0

    /*
     * This dictionary groups error messages by function, consolidating duplicate warnings together.
     */
    var functionCallErrorMessages: Map[String, ErrorMsgs] = Map()
    var functionRedefinitionErrorMessages: List[RedefErrorMsg] = List()

    val DEBUG_PRINT = false
    def dbgPrint(o: Any) = if (DEBUG_PRINT) print(o)
    def dbgPrintln(o: Any) = if (DEBUG_PRINT) println(o)


    def checkAST(ast: AST):Boolean = {

        topdown(checkFunctionCalls)(ast)

        if (functionCallErrorMessages.values.isEmpty && functionRedefinitionErrorMessages.isEmpty)
            println("No type errors found.")
        else {
            println("Type Errors: ");
            println(functionCallErrorMessages.values.mkString("\n"))
            println(functionRedefinitionErrorMessages.mkString("\n"))
        }
        println("(performed " + functionCallChecks + " checks regarding function calls)");

        return (functionCallErrorMessages.values.isEmpty && functionRedefinitionErrorMessages.isEmpty)
    }

    //    class TSVisitor extends ASTVisitor {
    //        var in: List[Int] = List()
    //        private def isStatementLevel = in.contains(2)
    //        override def visit(ast: AST, feature: FeatureExpr) {
    //            in = (ast match {
    //                case s: Statement => 2
    //                case _ => 1
    //            }) :: in
    //            //for (a<-in)
    //            //	print("  ")
    //            //println(ast.getClass.getName)
    //
    //            ast match {
    //            /**** declarations ****/
    //            //function definition
    //                case FunctionDef(specifiers, DeclaratorId(pointers, Id(name), extensions), params, stmt) => addToLookupTableAndCheckForDuplicates(new LFunctionDef(name, "", currentScope, feature))
    //                //function declaration and other declarations
    //                case ADeclaration(specifiers, initDecls) if (!isStatementLevel) =>
    //                    for (initDecl <- initDecls.toList.flatten)
    //                        initDecl.entry match {
    //                            case InitDeclaratorI(DeclaratorId(_, Id(name), _), _, _) => table = table add (new LDeclaration(name, "", currentScope, feature))
    //                            case InitDeclaratorE(DeclaratorId(_, Id(name), _), _, _) => table = table add (new LDeclaration(name, "", currentScope, feature))
    //                            case _ =>
    //                        }
    //
    //                /**** references ****/
    //                //function call (XXX: PG: not-so-good detection, but will work for typical code).
    //                case PostfixExpr(Id(name), Opt(feat2, FunctionCall(_)) :: _) => checkFunctionCall(ast, name, feature /* and feat2 */)
    //                //Omit feat2, for typical code a function call is always a function call, even if the parameter list is conditional.
    //                case _ =>
    //            }
    //        }
    //        override def postVisit(ast: AST, feature: FeatureExpr) {
    //            in = in.tail
    //        }
    //
    //    }

    def checkFunctionCallTargets(source: AST, name: String, callerFeature: FeatureExpr, targets: List[Entry]) = {
        if (!targets.isEmpty) {
            //condition: feature implies (target1 or target2 ...)
            functionCallChecks += 1
            val condition = callerFeature.implies(targets.map(_.feature).foldLeft(FeatureExpr.base.not)(_.or(_)))
            if (condition.isTautology(null) || condition.isTautology(featureModel)) {
                dbgPrintln(" always reachable " + condition)
                None
            } else {
                dbgPrintln(" not always reachable " + callerFeature + " => " + targets.map(_.feature).mkString(" || "))
                Some(functionCallErrorMessages.get(name) match {
                    case None => ErrorMsgs(name, List((callerFeature, source)), targets)
                    case Some(err: ErrorMsgs) => err.withNewCaller(source, callerFeature)
                })
            }
        } else {
            dbgPrintln("dead")
            Some(ErrorMsgs.errNoDecl(name, source, callerFeature))
        }
    }


    //    def addToLookupTableAndCheckForDuplicates(entry: LFunctionDef) = {
    //        val existingEntries = table.find(entry.name).filter(_.isInstanceOf[LFunctionDef])
    //        for (otherEntry <- existingEntries) {
    //            if (!(otherEntry.feature and entry.feature).isContradiction(featureModel)) {
    //                dbgPrintln("function " + entry.name + " redefined with feature " + entry.feature + "; previous: " + otherEntry)
    //                functionRedefinitionErrorMessages = RedefErrorMsg(entry.name, entry, otherEntry) :: functionRedefinitionErrorMessages
    //            }
    //        }
    //
    //        table = table.add(entry)
    //    }

    val gccBuiltins = List(
        "constant_p",
        "expect",
        "memcpy",
        "memset",
        "return_address", "va_start", "va_end")

    def initTable() = {
        var table = new LookupTable()
        for (name <- gccBuiltins) {
            table = table.add(new LFunctionDef("__builtin_" + name, "", 0, FeatureExpr.base))
        }
        table
    }

    val isStatementLevel: Attributable ==> Boolean = attr {
        case _: Statement => true
        case e => if (e.parent == null) false else (e.parent -> isStatementLevel)
    }

    val env: Attributable ==> LookupTable = attr {
        case e@FunctionDef(specifiers, DeclaratorId(pointers, Id(name), extensions), params, stmt) =>
            e.parent -> env add (new LFunctionDef(name, "", 1, e -> presenceCondition))
        //            addToLookupTableAndCheckForDuplicates(new LFunctionDef(name, "", currentScope, feature))
        //function declaration and other declarations
        case e@ADeclaration(specifiers, initDecls) if (!(e -> isStatementLevel)) => {
            var table = e.parent -> env
            for (initDecl <- initDecls.toList.flatten)
                initDecl.entry match {
                    case InitDeclaratorI(DeclaratorId(_, Id(name), _), _, _) => table = table add (new LDeclaration(name, "", 1, e -> presenceCondition))
                    case InitDeclaratorE(DeclaratorId(_, Id(name), _), _, _) => table = table add (new LDeclaration(name, "", 1, e -> presenceCondition))
                    case _ =>
                }
            table
        }
        case _: TranslationUnit => initTable
        case e =>
            if (e.prev[AST] != null) e.prev[AST] -> env
            else
            if (e.parent != null) e.parent -> env
            else
                initTable//should not occur when checking entire TranslationUnits
}

private def ppc (e: Attributable): FeatureExpr = if (e.parent == null) base else e.parent -> presenceCondition

val presenceCondition: Attributable ==> FeatureExpr = childAttr {
case e => {
case c: Choice[_] if (e == c.thenBranch) => ppc (e) and c.feature
case c: Choice[_] if (e == c.elseBranch) => ppc (e) andNot c.feature
case o: Opt[_] => ppc (e) and o.feature
case e => ppc (e)
}
}

val checkFunctionCalls = query {
//function call (XXX: PG: not-so-good detection, but will work for typical code).
case e@PostfixExpr (Id (name), Opt (feat2, FunctionCall (_) ) :: _) => {
//Omit feat2, for typical code a function call is always a function call, even if the parameter list is conditional.
checkFunctionCall (e -> env, e, name, e -> presenceCondition /* and feat2 */ )
}
case _ =>
}


def checkFunctionCall (table: LookupTable, source: AST, name: String, callerFeature: FeatureExpr) {
val targets: List[Entry] = table.find (name)
dbgPrint ("function " + name + " found " + targets.size + " targets: ")
checkFunctionCallTargets (source, name, callerFeature, targets) match {
case Some (newEntry) =>
functionCallErrorMessages = functionCallErrorMessages.updated (name, newEntry)
case _ => ()
}
}

}