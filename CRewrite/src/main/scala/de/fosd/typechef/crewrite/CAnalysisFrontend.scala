package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.conditional.{Opt, Choice}
import de.fosd.typechef.parser.c.{PrettyPrinter, TranslationUnit, FunctionDef, AST}

import sat.DefinedMacro
import de.fosd.typechef.conditional._
import de.fosd.typechef.parser.WithPosition
import de.fosd.typechef.parser.c._

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
      case Choice(f, x, y) => if (c.config implies (if (env.containsASTElem(x)) env.featureExpr(x) else FeatureExprFactory.True) isTautology()) x else y
      case l: List[Opt[_]] => {
        var res: List[Opt[_]] = List()
        // use l.reverse here to omit later reverse on res or use += or ++= in the thenBranch
        for (o <- l.reverse)
          if (o.feature == FeatureExprFactory.True)
            res ::= o
          else if (c.config implies (if (env.containsASTElem(o.entry)) env.featureExpr(o.entry) else FeatureExprFactory.True) isTautology()) {
            res ::= o.copy(feature = FeatureExprFactory.True)
          }
        res
      }
      case x => x
    })

    val x = pconfig(a).get.asInstanceOf[T]
    //appendToFile("output.c", PrettyPrinter.print(x.asInstanceOf[AST]))
    x
  }



  class CCFGError(msg: String, s: AST, sfexp: FeatureExpr, t: AST, tfexp: FeatureExpr) {
    override def toString =
      "[" + sfexp + "]" + s.getClass() + "(" + s.getPositionFrom + "--" + s.getPositionTo + ")" + // print source
        "--> " +
        "[" + tfexp + "]" + t.getClass() + "(" + t.getPositionFrom + "--" + t.getPositionTo + ")" + // print target
        "\n" + msg + "\n\n\n"
  }

  var errors = List[CCFGError]()
  val liveness = "liveness.csv"

  def checkCfG(fileName: String) = {

    // file-output
    appendToFile(liveness, "filename;family-based;full-coverage;full-coverage-configs")

    // family-based
    println("checking family-based")
    val family_ast = prepareAST[TranslationUnit](tunit.asInstanceOf[TranslationUnit])
    val family_env = CASTEnv.createASTEnv(family_ast)
    val family_function_defs = filterASTElems[FunctionDef](family_ast)

    val tfams = System.currentTimeMillis()
    family_function_defs.map(intraCfGFunctionDef(_, family_env))
    val tfame = System.currentTimeMillis()

    val tfam = tfame - tfams

    // base variant
    println("checking base variant")
    val base_ast = deriveProductFromConfiguration[TranslationUnit](family_ast.asInstanceOf[TranslationUnit], new Configuration(FeatureExprFactory.True, fm), family_env)
    val base_env = CASTEnv.createASTEnv(base_ast)
    val base_function_defs = filterASTElems[FunctionDef](base_ast)

    val tbases = System.currentTimeMillis()
    base_function_defs.map(intraCfGFunctionDef(_, base_env))
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
      val product_ast = deriveProductFromConfiguration[TranslationUnit](family_ast, new Configuration(config, fm), family_env)
      val product_env = CASTEnv.createASTEnv(product_ast)
      val product_function_defs = filterASTElems[FunctionDef](product_ast)
      appendToFile("test.c", PrettyPrinter.print(product_ast))

      val tfullcoverages = System.currentTimeMillis()
      product_function_defs.map(intraCfGFunctionDef(_, product_env))
      val tfullcoveragee = System.currentTimeMillis()

      tfullcoverage += (tfullcoveragee - tfullcoverages)
    }

    println("family-based: " + tfam + "ms")
    println("base variant: " + tbase + "ms")
    println("full coverage: " + tfullcoverage + "ms")

    appendToFile(liveness, fileName + ";" + tfam + ";" + tbase + ";" + tfullcoverage + ";" + configs.size + "\n")
  }

  private def intraCfGFunctionDef(f: FunctionDef, env: ASTEnv) = {
    val myenv = CASTEnv.createASTEnv(f)

    val ss = if (f.stmt.innerStatements.isEmpty) List() else getAllSucc(f.stmt.innerStatements.head.entry, myenv).map(_._1).filterNot(_.isInstanceOf[FunctionDef])
    for (s <- ss.reverse) {
      in(s, myenv)
      out(s, myenv)
    }

    true
  }
}
