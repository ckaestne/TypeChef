package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.conditional.{Opt, Choice}
import de.fosd.typechef.parser.c.{PrettyPrinter, TranslationUnit, FunctionDef, AST}

class CAnalysisFrontend(tunit: AST, fm: FeatureModel = FeatureExprFactory.default.featureModelFactory.empty) extends ConditionalNavigation with ConditionalControlFlow with IOUtilities with Liveness with EnforceTreeHelper {

  // derive a specific product from a given configuration
  def deriveProductFromConfiguration[T <: Product](a: T, c: Configuration, env: ASTEnv): T = {
    // manytd is crucial here; consider the following example
    // Product1( c1, c2, c3, c4, c5)
    // all changes the elements top down, so the parent is changed before the children and this
    // way the lookup env.featureExpr(x) will not fail. Using topdown or everywherebu changes the children and so also the
    // parent before the parent is processed so we get a NullPointerExceptions calling env.featureExpr(x). Reason is
    // changed children lead to changed parent and a new hashcode so a call to env fails.
    val pconfig = manytd(rule {
      case Choice(feature, thenBranch, elseBranch) => if (c.config implies (if (env.containsASTElem(thenBranch)) env.featureExpr(thenBranch) else FeatureExprFactory.True) isTautology()) thenBranch else elseBranch
      case l: List[Opt[_]] => {
        var res: List[Opt[_]] = List()
        // use l.reverse here to omit later reverse on res or use += or ++= in the thenBranch
        for (o <- l.reverse)
          if (o.feature == FeatureExprFactory.True)
            res ::= o
          else if (c.config implies (if (env.containsASTElem(o)) env.featureExpr(o) else FeatureExprFactory.True) isTautology()) {
            res ::= o.copy(feature = FeatureExprFactory.True)
          }
        res
      }
      // we need ast here because otherwise we have old and new elements in the resulting product
      // and this might pollute our caches later
      case a: AST => a.clone()
    })

    val x = pconfig(a).get.asInstanceOf[T]
    appendToFile("output.c", PrettyPrinter.print(x.asInstanceOf[AST]))
    assert(isVariable(x) == false, "product still contains variability")
    x
  }

  val liveness = "liveness.csv"

  def checkCfG(fileName: String) {

    // file-output
    appendToFile(liveness, "filename;family-based;full-coverage;full-coverage-configs")

    // family-based
    println("checking family-based")
    val family_ast = prepareAST[TranslationUnit](tunit.asInstanceOf[TranslationUnit])
    val family_env = CASTEnv.createASTEnv(family_ast)
    val family_function_defs = filterAllASTElems[FunctionDef](family_ast)

    val tfams = System.currentTimeMillis()
    family_function_defs.map(intraCfGFunctionDef)
    val tfame = System.currentTimeMillis()

    val tfam = tfame - tfams

    // base variant
    println("checking base variant")
    val base_ast = deriveProductFromConfiguration[TranslationUnit](family_ast.asInstanceOf[TranslationUnit],
      new Configuration(ConfigurationCoverage.completeConfiguration(FeatureExprFactory.True, List(), fm), fm), family_env)
    val base_function_defs = filterAllASTElems[FunctionDef](base_ast)

    val tbases = System.currentTimeMillis()
    base_function_defs.map(intraCfGFunctionDef)
    val tbasee = System.currentTimeMillis()

    val tbase = tbasee - tbases

    // full coverage
    println("checking full coverage")
    val configs = ConfigurationCoverage.naiveCoverageAny(family_ast, fm, family_env)
    var current_config = 1
    var tfullcoverage: Long = 0

    for (config <- configs) {
      println("checking configuration " + current_config + " of " + configs.size)
      current_config += 1
      val product_ast = deriveProductFromConfiguration[TranslationUnit](family_ast,
        new Configuration(ConfigurationCoverage.completeConfiguration(config, List(), fm), fm), family_env)
      val product_function_defs = filterAllASTElems[FunctionDef](product_ast)
      appendToFile("test.c", PrettyPrinter.print(product_ast))

      val tfullcoverages = System.currentTimeMillis()
      product_function_defs.map(intraCfGFunctionDef)
      val tfullcoveragee = System.currentTimeMillis()

      tfullcoverage += (tfullcoveragee - tfullcoverages)
    }

    println("family-based: " + tfam + "ms")
    println("base variant: " + tbase + "ms")
    println("full coverage: " + tfullcoverage + "ms")

    println("\n\n\n")


    appendToFile(liveness, fileName + ";" + tfam + ";" + tbase + ";" + tfullcoverage + ";" + configs.size + "\n")
  }

  private def intraCfGFunctionDef(f: FunctionDef) = {
    val fenv = CASTEnv.createASTEnv(f)
    val s = getAllSucc(f, fenv)
    val p = getAllPred(f, fenv)

    val errors = compareSuccWithPred(s, p, fenv)
    CCFGErrorOutput.printCCFGErrors(s, p, errors, fenv)

    errors.size > 0
  }
}
