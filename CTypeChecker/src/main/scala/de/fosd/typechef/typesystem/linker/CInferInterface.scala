package de.fosd.typechef.typesystem.linker

import de.fosd.typechef.typesystem.CTypeAnalysis
import de.fosd.typechef.parser.Position
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.conditional.{Opt, TConditional}
import de.fosd.typechef.parser.c._

/**
 * first attempt to infer the interface of a C file for linker checks
 */

trait CInferInterface extends CTypeAnalysis {

    def inferInterface(ast: TranslationUnit): CInterface =
        new CInterface(getImports(ast), getExports(ast))


    /**
     * all function definitions are considered as exports
     */
    def getExports(ast: TranslationUnit): Seq[CSignature] = {

        var exports = List[CSignature]()

        for (Opt(_, extDecl) <- ast.defs)
            extDecl match {
                case fun: FunctionDef =>
                    for (Opt(fexpr, ctype) <- TConditional.toOptList(fun -> funType).map(_.and(fun -> featureExpr)))
                        if (fexpr.isSatisfiable())
                            exports = CSignature(fun.getName, ctype, fexpr, fun.getPositionFrom) :: exports
                case _ =>
            }

        exports
    }


    /**
     * all function declarations without definitions are imports
     * if they are referenced at least once
     */
    def getImports(ast: TranslationUnit): Seq[CSignature] = {
        var declarations = List[CSignature]()

        def findImports(ast: AST): Boolean = ast match {
            case identifier: Id =>
                if (!(identifier -> inDeclaratorOrSpecifier)) {
                    val ctypes = identifier -> exprType
                    for ((fexpr, ctype) <- ctypes.toList)
                        if (ctype.isFunction && (fexpr and (identifier -> featureExpr) isSatisfiable))
                            declarations = CSignature(identifier.name, ctype, fexpr and (identifier -> featureExpr), identifier.getPositionFrom) :: declarations
                }
                true
            case _ => true
        }

        visitAST(ast, findImports)

        //        for (Opt(_, extDecl) <- ast.defs)
        //            extDecl match {
        //                case decl:Declaration =>
        //                    val typeInformation=declType(decl)
        //                    for ((name, fexpr, ctypes) <- typeInformation)
        //                        for (Opt(newfexpr, ctype) <- TConditional.toOptList(ctypes).map(_.and(fexpr)))
        //                            if (ctype.isFunction && newfexpr.isSatisfiable())
        //                                declarations=CSignature(name, ctype, fexpr, decl.getPositionFrom)::declarations
        //                case _=>
        //            }

        declarations
    }


    case class CInterface(imports: Seq[CSignature], exports: Seq[CSignature]) {
        override def toString =
            "imports\n" + imports.map("\t" + _.toString).mkString("\n") +
                    "\nexports\n" + exports.map("\t" + _.toString).mkString("\n") + "\n"
    }
    case class CSignature(name: String, ctype: CType, fexpr: FeatureExpr, pos: Position) {
        override def toString =
            name + ": " + ctype + "\t\tif " + fexpr + "\t\tat " + pos
    }
}

