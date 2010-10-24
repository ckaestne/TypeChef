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
    var currentScope = 1
    var errorMessages: List[ErrorMsg] = List()

    val DEBUG_PRINT = false

    def checkAST(ast: AST) {
        ast.accept(new TSVisitor())
        if (DEBUG_PRINT) println(table)
        println("Type Errors: " + errorMessages.mkString("\n"))
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

    def checkFunctionCall(source: AST, name: String, feature: FeatureExpr) {
        val targets: List[Entry] = table.find(name)
        if (DEBUG_PRINT) print("function " + name + " found " + targets.size + " targets: ")
        if (!targets.isEmpty) {
            //condition: feature implies (target1 or target2 ...)
            val condition = feature.implies(targets.map(_.feature).foldLeft(FeatureExpr.base.not)(_.or(_)))
            if (condition.isTautology) {
                if (DEBUG_PRINT) println(" always reachable " + condition)
            } else {
                if (DEBUG_PRINT) println(" not always reachable " + feature + " => " + targets.map(_.feature).mkString(" || "))
                errorMessages = new ErrorMsg("declaration of function " + name + " not always reachable (" + targets.size + " potential targets): " + feature + " => " + targets.map(_.feature).mkString(" || "), source, targets) :: errorMessages
            }
        } else {
            if (DEBUG_PRINT) println("dead")
            errorMessages = new ErrorMsg("declaration of function " + name + " not found", source, List()) :: errorMessages
        }
    }

}
