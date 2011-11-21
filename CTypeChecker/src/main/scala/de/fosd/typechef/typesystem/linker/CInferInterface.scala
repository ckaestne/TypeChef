package de.fosd.typechef.typesystem.linker

import de.fosd.typechef.conditional.{Opt, Conditional}
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.typesystem.{CType, CTypeSystem}

/**
 * first attempt to infer the interface of a C file for linker checks
 *
 * listens to events of the type system to determine defined and called functions
 */

//TODO structs need to become part of the interface, or we need to resolve all structs to anonymous structs

trait CInferInterface extends CTypeSystem with InterfaceWriter {


    //if not already type checked, to the check now
    def inferInterface(ast: TranslationUnit, fm: FeatureExpr = FeatureExpr.base): CInterface = {
        typecheckTranslationUnit(ast, fm)
        getInferredInterface(fm)
    }

    def getInferredInterface(fm: FeatureExpr = FeatureExpr.base) = new CInterface(fm, imports, exports).pack


    var exports = List[CSignature]()
    var imports = List[CSignature]()

    /**
     * all function definitions are considered as exports
     */
    override def typedFunction(fun: FunctionDef, funType: Conditional[CType], featureExpr: FeatureExpr) {
        super.typedFunction(fun, funType, featureExpr)

        val staticCondition = FeatureExpr.base andNot getStaticCondition(fun.specifiers)

        funType.simplify(featureExpr and staticCondition).mapf(featureExpr and staticCondition, {
            (fexpr, ctype) =>
                if (fexpr.isSatisfiable())
                    exports = CSignature(fun.getName, ctype, fexpr, Seq(fun.getPositionFrom)) :: exports
        })
    }

    private def getStaticCondition(specifiers: List[Opt[Specifier]]): FeatureExpr =
        specifiers.filter(_.entry == StaticSpecifier()).foldLeft(FeatureExpr.dead)((f, o) => f or o.feature)


    /**
     * all function declarations without definitions are imports
     * if they are referenced at least once
     */
    override def typedExpr(expr: Expr, ctypes: Conditional[CType], featureExpr: FeatureExpr) {
        expr match {
            case identifier: Id =>
                for ((fexpr, ctype) <- ctypes.toList)
                    if (ctype.isFunction && (fexpr and (featureExpr) isSatisfiable))
                        imports = CSignature(identifier.name, ctype, fexpr and featureExpr, Seq(identifier.getPositionFrom)) :: imports
            case _ =>
        }


    }


}

