package de.fosd.typechef.typesystem.linker

import de.fosd.typechef.conditional.{Opt, Conditional}
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{KParameter, CType, CTypeSystem}
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import de.fosd.typechef.error.Position

/**
 * first attempt to infer the interface of a C file for linker checks
 *
 * listens to events of the type system to determine defined and called functions
 */

//TODO structs need to become part of the interface, or we need to resolve all structs to anonymous structs
//TODO nested functions behave like static functions (do not lead to imports)

trait CInferInterface extends CTypeSystem with InterfaceWriter {


    //if not already type checked, to the check now
    def inferInterface(ast: TranslationUnit, fm: FeatureExpr = FeatureExprFactory.True): CInterface = {
        typecheckTranslationUnit(ast, fm)
        getInferredInterface(fm)
    }

    def getInferredInterface(fm: FeatureExpr = FeatureExprFactory.True, strictness: Strictness = LINK_STRICT) = {
        cleanImports()
        new CInterface(fm, featureNames, Set(), imports, exports).pack(strictness)
    }


    var exports = List[CSignature]()
    var staticFunctions = List[CSignature]()
    var imports = List[CSignature]()
    var featureNames = Set[String]()


    /**
     * remove imports that are covered by exports or static functions
     *
     * ignore CFlags in importers
     */
    private def cleanImports() {
        type T2 = (FeatureExpr, Seq[Position], Set[CFlag])
        var importMap = Map[(String, CType), T2]()

        //eliminate duplicates with a map
        for (imp <- imports) {
            val key = (imp.name, imp.ctype.toValueLinker) //toValueLinker needed to remove the distinction between Object or not
            val old = importMap.getOrElse[T2](key, (FeatureExprFactory.False, Seq(), Set()))
            importMap = importMap + (key ->(old._1 or imp.fexpr, old._2 ++ imp.pos, CFlagOps.mergeOnImports(old._3, imp.extraFlags)))
        }
        //eliminate imports that have corresponding exports
        for (exp <- (exports ++ staticFunctions)) {
            val key = (exp.name, exp.ctype.toValueLinker)
            if (importMap.contains(key)) {
                val (oldFexpr, oldPos, oldExtras) = importMap(key)
                val newFexpr = oldFexpr andNot exp.fexpr
                if (newFexpr.isSatisfiable())
                    importMap = importMap + (key ->(newFexpr, oldPos, oldExtras))
                else
                    importMap = importMap - key
            }
        }


        val r = for ((k, v) <- importMap.iterator)
        yield CSignature(k._1, k._2, v._1, v._2, v._3)
        imports = r.toList
    }
    def findAttributes(a: GnuAttributeSpecifier, ctx: FeatureExpr): Seq[(FeatureExpr, AtomicAttribute)] =
        (for (Opt(f, at) <- a.attributeList) yield findAttributes(at, ctx and f)).flatten
    def findAttributes(a: AttributeSequence, ctx: FeatureExpr): Seq[(FeatureExpr, AtomicAttribute)] =
        (for (Opt(f, at) <- a.attributes) yield findAttributes(at, ctx and f)).flatten
    def findAttributes(a: Attribute, ctx: FeatureExpr): Seq[(FeatureExpr, AtomicAttribute)] = a match {
        case a: AtomicAttribute => Seq((ctx, a))
        case CompoundAttribute(inner) => (for (Opt(f, at) <- inner) yield findAttributes(at, ctx and f)).flatten
    }


    /**
     * try to recognize __attribute__((weak)) attribute as a flag.
     *
     * the recognition is conservative and does ignore conditional attribute declarations
     * (so a conditionally weak method is always recognized as weak, which may prevent us
     * from detecting some problems, but which will not produce false positives)
     */
    def getExtraFlags(functionDef: FunctionDef, ctx: FeatureExpr): Set[CFlag] = {
        val flags = for (Opt(f, g@GnuAttributeSpecifier(_)) <- functionDef.specifiers;
                         (f, a) <- findAttributes(g, ctx)
        ) yield
            if (a.n == "weak" && f.isSatisfiable()) Some[CFlag](WeakExport) else None

        flags.filter(_.isDefined).map(_.get).toSet
    }

    /**
     * all nonstatic function definitions are considered as exports
     *
     * actually the behavior of "extern inline" is slightly complicated, see also
     * http://stackoverflow.com/questions/216510/extern-inline
     * "extern inline" means neither static nor exported
     */
    override protected def typedFunction(fun: FunctionDef, funType: Conditional[CType], featureExpr: FeatureExpr) {
        super.typedFunction(fun, funType, featureExpr)

        val staticSpec = getStaticCondition(fun.specifiers)
        val externSpec = getSpecifierCondition(fun.specifiers, ExternSpecifier())
        val inlineSpec = getSpecifierCondition(fun.specifiers, InlineSpecifier())


        //exportCondition and staticCondition are disjoint, but may not cover all cases (they are both false for "extern inline")
        val exportCondition = staticSpec.not andNot (externSpec and inlineSpec)
        val staticCondition = staticSpec andNot (externSpec and inlineSpec)

        funType.simplify(featureExpr).vmap(featureExpr, {
            (fexpr, ctype) =>
                if ((fexpr and exportCondition).isSatisfiable())
                    exports = CSignature(fun.getName, ctype, fexpr and exportCondition, Seq(fun.declarator.getId.getPositionFrom), getExtraFlags(fun, fexpr and exportCondition)) :: exports
                if ((fexpr and staticCondition).isSatisfiable())
                    staticFunctions = CSignature(fun.getName, ctype, fexpr and staticCondition, Seq(fun.declarator.getId.getPositionFrom), getExtraFlags(fun, fexpr and staticCondition)) :: staticFunctions
        })
    }

    /**
     * all function declarations without definitions are imports
     * if they are referenced at least once
     */
    override protected def typedExpr(expr: Expr, ctypes: Conditional[CType], featureExpr: FeatureExpr, env: Env) {
        super.typedExpr(expr, ctypes, featureExpr, env)
        expr match {
            case identifier: Id =>
                val deadCondition = env.isDeadCode
                for ((fexpr, ctype) <- ctypes.toList) {
                    val localFexpr = fexpr and featureExpr andNot deadCondition
                    if (ctype.isFunction && (localFexpr isSatisfiable))
                        isParameter(identifier.name, env).vmap(localFexpr,
                            (f, e) => if (!e)
                                imports = CSignature(identifier.name, ctype, f, Seq(identifier.getPositionFrom), Set()) :: imports
                        )
                }
            case _ =>
        }


    }

    /**
     * check that the id refers to a parameter instead of to a function or variable
     *
     * returns true if scope==0
     */
    private def isParameter(name: String, env: Env): Conditional[Boolean] =
        env.varEnv.lookupKind(name).map(_ == KParameter)


}

