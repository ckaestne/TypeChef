package de.fosd.typechef.typesystem.linker

import de.fosd.typechef.conditional.{Opt, Conditional}
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.typesystem.{CType, CTypeSystem}
import de.fosd.typechef.parser.Position

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

    def getInferredInterface(fm: FeatureExpr = FeatureExpr.base) = {
        cleanImports()
        new CInterface(fm, imports, exports).pack
    }


    var exports = List[CSignature]()
    var staticFunctions = List[CSignature]()
    var imports = List[CSignature]()


    /**
     * remove imports that are covered by exports or static functions
     */
    private def cleanImports() {
        var importMap = Map[(String, CType), (FeatureExpr, Seq[Position])]()

        //eliminate duplicates with a map
        for (imp <- imports) {
            val key = (imp.name, imp.ctype)
            val old = importMap.getOrElse(key, (FeatureExpr.dead, Seq()))
            importMap = importMap + (key -> (old._1 or imp.fexpr, old._2 ++ imp.pos))
        }
        //eliminate imports that have corresponding exports
        for (exp <- (exports ++ staticFunctions)) {
            val key = (exp.name, exp.ctype)
            if (importMap.contains(key)) {
                val (oldFexpr, oldPos) = importMap(key)
                val newFexpr = oldFexpr andNot exp.fexpr
                if (newFexpr.isSatisfiable())
                    importMap = importMap + (key -> (newFexpr, oldPos))
                else
                    importMap = importMap - key
            }
        }


        val r = for ((k, v) <- importMap.iterator)
        yield CSignature(k._1, k._2, v._1, v._2)
        imports = r.toList
    }

    /**
     * all function definitions are considered as exports
     */
    override def typedFunction(fun: FunctionDef, funType: Conditional[CType], featureExpr: FeatureExpr) {
        super.typedFunction(fun, funType, featureExpr)

        val staticCondition = FeatureExpr.base andNot getStaticCondition(fun.specifiers)

        funType.simplify(featureExpr).mapf(featureExpr, {
            (fexpr, ctype) =>
                if ((fexpr and staticCondition).isSatisfiable())
                    exports = CSignature(fun.getName, ctype, fexpr and staticCondition, Seq(fun.getPositionFrom)) :: exports
                if ((fexpr andNot staticCondition).isSatisfiable())
                    staticFunctions = CSignature(fun.getName, ctype, fexpr andNot staticCondition, Seq(fun.getPositionFrom)) :: staticFunctions
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

