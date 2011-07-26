package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c._
import org.kiama.attribution.Attribution._
import org.kiama._

trait CFunctionRedefinition extends ASTNavigation with CTypeEnv {

    /**
     * finds prior definitions from which we can derive
     * feature expression and type
     */
    val priorDefinitions: FunctionDef ==> Seq[FunctionDef] = attr {
        case fun => outerPriorDefinitions(fun.getName, fun)
    }


    private val findPriorDefinitions: String => AST ==> Seq[FunctionDef] = paramAttr {
        funName => {
            case fun: FunctionDef if (fun.getName == funName) => outerPriorDefinitions(funName, fun) :+ fun
            case e => outerPriorDefinitions(funName, e)
        }
    }
    private def outerPriorDefinitions(funName: String, e: AST): Seq[FunctionDef] =
        outer[Seq[FunctionDef]](findPriorDefinitions(funName), () => Seq[FunctionDef](), e)


}