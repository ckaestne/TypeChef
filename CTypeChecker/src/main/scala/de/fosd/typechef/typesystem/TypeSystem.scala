package de.fosd.typechef.typesystem
import de.fosd.typechef.parser.c.ASTVisitor

import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr._

/**
 * checks an AST (from CParser) for type errors (especially dangling references)
 * 
 * performs type checking in a single tree-walk
 * 
 * @author kaestner
 *
 */
class TypeSystem {
    var table = new LookupTable()

    val globalScope = 1
    var currentScope = 1

    /*
     * This dictionary groups error messages by function, consolidating duplicate warnings together.
     */
    var errorMessages: Map[String, ErrorMsgs] = Map() 

    val DEBUG_PRINT = false
    def dbgPrint(o: Any) = if (DEBUG_PRINT) print(o)
    def dbgPrintln(o: Any) = if (DEBUG_PRINT) println(o)

    val gccBuiltins = List(
      "constant_p",
      "expect",
      "memcpy",
      "memset",
      "return_address")

    def declareBuiltins() {
        for (name <- gccBuiltins) {
            table = table.add(new LFunctionDef("__builtin_" + name, "", globalScope, FeatureExpr.base))
        }
    }

    def checkAST(ast: AST) {
        declareBuiltins()
        ast.accept(new TSVisitor())
        dbgPrintln(table)
        println("Type Errors: " + errorMessages.values.mkString("\n"))
    }

    class TSVisitor extends ASTVisitor {
        var in: List[Int] = List()
        override def visit(ast: AST, feature: FeatureExpr) {
            in = 1 :: in
            //for (a<-in)
            //	print("  ")
            //println(ast.getClass.getName)

            ast match {
                /**** declarations ****/
                //function definition
                case FunctionDef(specifiers, DeclaratorId(pointers, Id(name), extensions), params, stmt) => table = table.add(new LFunctionDef(name, "", currentScope, feature))
                //function declaration and other declarations
                case ADeclaration(specifiers, decls) =>
                    for (decl <- decls.toList.flatten)
                        decl match {
                            case InitDeclaratorI(DeclaratorId(_, Id(name), _), _, _) => table = table.add(new LDeclaration(name, "", currentScope, feature))
                            case InitDeclaratorE(DeclaratorId(_, Id(name), _), _, _) => table = table.add(new LDeclaration(name, "", currentScope, feature))
                            case _ =>
                        }

                /**** references ****/
                //function call
                case PostfixExpr(Id(name), List(FunctionCall(parameters))) => checkFunctionCall(ast, name, feature)
                case _ =>
            }
        }
        override def postVisit(ast: AST, feature: FeatureExpr) {
            in = in.tail
        }

    }

    def checkFunctionCallTargets(source: AST, name: String, callerFeature: FeatureExpr, targets: List[Entry]) = {
        if (!targets.isEmpty) {
            //condition: feature implies (target1 or target2 ...)
            val condition = callerFeature.implies(targets.map(_.feature).foldLeft(FeatureExpr.base.not)(_.or(_)))
            if (condition.isTautology()) {
                dbgPrintln(" always reachable " + condition)
                None
            } else {
                dbgPrintln(" not always reachable " + callerFeature + " => " + targets.map(_.feature).mkString(" || "))
                Some(errorMessages.get(name) match {
                        case None => ErrorMsgs(name, List((callerFeature, source)), targets)
                        case Some(err : ErrorMsgs) => err.withNewCaller(source, callerFeature)  
                })
            }
        } else {
            dbgPrintln("dead")
            Some(ErrorMsgs.errNoDecl(name, source, callerFeature))
        }
    }

    def checkFunctionCall(source: AST, name: String, callerFeature: FeatureExpr) {
        val targets: List[Entry] = table.find(name)
        dbgPrint("function " + name + " found " + targets.size + " targets: ")
        checkFunctionCallTargets(source, name, callerFeature, targets) match {
                case Some(newEntry) =>
                        errorMessages = errorMessages.updated(name, newEntry)
                case _ => ()
        }
    }
}
