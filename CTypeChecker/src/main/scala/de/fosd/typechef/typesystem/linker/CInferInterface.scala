package de.fosd.typechef.typesystem.linker

import de.fosd.typechef.conditional.{Opt, Conditional}
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{KParameter, CType, CTypeSystem}
import de.fosd.typechef.parser.Position
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}

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

  def getInferredInterface(fm: FeatureExpr = FeatureExprFactory.True) = {
    cleanImports()
    new CInterface(fm, featureNames, Set(), imports, exports).pack
  }


  var exports = List[CSignature]()
  var staticFunctions = List[CSignature]()
  var imports = List[CSignature]()
  var featureNames = Set[String]()


  /**
   * remove imports that are covered by exports or static functions
   */
  private def cleanImports() {
    var importMap = Map[(String, CType), (FeatureExpr, Seq[Position])]()

    //eliminate duplicates with a map
    for (imp <- imports) {
      val key = (imp.name, imp.ctype)
      val old = importMap.getOrElse(key, (FeatureExprFactory.False, Seq()))
      importMap = importMap + (key ->(old._1 or imp.fexpr, old._2 ++ imp.pos))
    }
    //eliminate imports that have corresponding exports
    for (exp <- (exports ++ staticFunctions)) {
      val key = (exp.name, exp.ctype)
      if (importMap.contains(key)) {
        val (oldFexpr, oldPos) = importMap(key)
        val newFexpr = oldFexpr andNot exp.fexpr
        if (newFexpr.isSatisfiable())
          importMap = importMap + (key ->(newFexpr, oldPos))
        else
          importMap = importMap - key
      }
    }


    val r = for ((k, v) <- importMap.iterator)
    yield CSignature(k._1, k._2, v._1, v._2)
    imports = r.toList
  }

  /**
   * all nonstatic function definitions are considered as exports
   *
   * actually the behavior of "extern inline" is slightly complicated, see also
   * http://stackoverflow.com/questions/216510/extern-inline
   * "extern inline" means neither static nor exported
   */
  override def typedFunction(fun: FunctionDef, funType: Conditional[CType], featureExpr: FeatureExpr) {
    super.typedFunction(fun, funType, featureExpr)

    val staticSpec = getStaticCondition(fun.specifiers)
    val externSpec = getSpecifierCondition(fun.specifiers, ExternSpecifier())
    val inlineSpec = getSpecifierCondition(fun.specifiers, InlineSpecifier())


    //exportCondition and staticCondition are disjoint, but may not cover all cases (they are both false for "extern inline")
    val exportCondition = staticSpec.not andNot (externSpec and inlineSpec)
    val staticCondition = staticSpec andNot (externSpec and inlineSpec)

    funType.simplify(featureExpr).mapf(featureExpr, {
      (fexpr, ctype) =>
        if ((fexpr and exportCondition).isSatisfiable())
          exports = CSignature(fun.getName, ctype, fexpr and exportCondition, Seq(fun.getPositionFrom)) :: exports
        if ((fexpr and staticCondition).isSatisfiable())
          staticFunctions = CSignature(fun.getName, ctype, fexpr and staticCondition, Seq(fun.getPositionFrom)) :: staticFunctions
    })
  }

  private def getStaticCondition(specifiers: List[Opt[Specifier]]): FeatureExpr = getSpecifierCondition(specifiers, StaticSpecifier())

  private def getExternCondition(specifiers: List[Opt[Specifier]]): FeatureExpr = getSpecifierCondition(specifiers, ExternSpecifier())

  private def getSpecifierCondition(specifiers: List[Opt[Specifier]], specifier: Specifier): FeatureExpr =
    specifiers.filter(_.entry == specifier).foldLeft(FeatureExprFactory.False)((f, o) => f or o.feature)


  /**
   * all function declarations without definitions are imports
   * if they are referenced at least once
   */
  override def typedExpr(expr: Expr, ctypes: Conditional[CType], featureExpr: FeatureExpr, env: Env) {
    expr match {
      case identifier: Id =>
        val deadCondition = env.isDeadCode
        for ((fexpr, ctype) <- ctypes.toList) {
          val localFexpr = fexpr and featureExpr andNot deadCondition
          if (ctype.isFunction && (localFexpr isSatisfiable))
            isParameter(identifier.name, env).mapf(localFexpr,
              (f, e) => if (!e)
                imports = CSignature(identifier.name, ctype, f, Seq(identifier.getPositionFrom)) :: imports
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

