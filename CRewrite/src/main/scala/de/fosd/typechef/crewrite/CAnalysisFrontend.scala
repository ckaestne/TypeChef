package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser.c.{TranslationUnit, FunctionDef, AST}
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.conditional.{Opt, Choice, ConditionalLib}


class CAnalysisFrontend(tunit: AST, fm: FeatureModel = NoFeatureModel) extends CASTEnv with ConditionalControlFlow with IOUtilities with Liveness with EnforceTreeHelper {
  
  // derive a specific product from a given configuration
  def deriveProductFromConfiguration[T <: Product](a: T, c: Configuration): T = {
    val pconfig = everywherebu(rule {
      case Choice(f, x, y) => if (c.config implies f isTautology()) x else y
      case l: List[Opt[_]] => {
        var res: List[Opt[_]] = List()
        // use l.reverse here to omit later reverse on res or use += or ++= in the thenBranch
        for (o <- l.reverse)
          if (o.feature == FeatureExpr.base)
            res ::= o
          else if (c.config implies o.feature isTautology()) {
            res ::= o.copy(feature = FeatureExpr.base)
          }
        res
      }
      case x => x
    })

    pconfig(a).get.asInstanceOf[T]
  }

  class CCFGError(msg: String, s: AST, sfexp: FeatureExpr, t: AST, tfexp: FeatureExpr) {
    override def toString =
      "[" + sfexp + "]" + s.getClass() + "(" + s.getPositionFrom + "--" + s.getPositionTo + ")" + // print source
        "--> " +
        "[" + tfexp + "]" + t.getClass() + "(" + t.getPositionFrom + "--" + t.getPositionTo + ")" + // print target
        "\n" + msg + "\n\n\n"
  }

  // given an ast element x and its successors lx: x should be in pred(lx)
  private def compareSuccWithPred(lsuccs: List[(AST, List[AST])], lpreds: List[(AST, List[AST])], env: ASTEnv): Boolean = {
    // check that number of nodes match
    if (lsuccs.size != lpreds.size) {
      println("number of nodes in ccfg does not match")
      return false
    }

    // check that number of edges match
    var res = true
    var succ_edges: List[(AST, AST)] = List()
    for ((ast_elem, succs) <- lsuccs) {
      for (succ <- succs) {
        succ_edges = (ast_elem, succ) :: succ_edges
      }
    }

    var pred_edges: List[(AST, AST)] = List()
    for ((ast_elem, preds) <- lpreds) {
      for (pred <- preds) {
        pred_edges = (ast_elem, pred) :: pred_edges
      }
    }

    // check succ/pred connection and print out missing connections
    // given two ast elems:
    //   a
    //   b
    // we check (a1, b1) successor
    // against  (b2, a2) predecessor
    for ((a1, b1) <- succ_edges) {
      var isin = false
      for ((b2, a2) <- pred_edges) {
        if (a1.eq(a2) && b1.eq(b2))
          isin = true
      }
      if (!isin) {
        errors = new CCFGError("is missing in preds", b1, env.featureExpr(b1), a1, env.featureExpr(a1)) :: errors
        res = false
      }
    }

    // check pred/succ connection and print out missing connections
    // given two ast elems:
    //  a
    //  b
    // we check (b1, a1) predecessor
    // against  (a2, b2) successor
    for ((b1, a1) <- pred_edges) {
      var isin = false
      for ((a2, b2) <- succ_edges) {
        if (a1.eq(a2) && b1.eq(b2))
          isin = true
      }
      if (!isin) {
        errors = new CCFGError("is missing in succs", a1, env.featureExpr(a1), b1, env.featureExpr(b1)) :: errors
        res = false
      }
    }

    res
  }

  var errors = List[CCFGError]()
  val liveness = "liveness.csv"

  def checkCfG(fileName: String) = {

    // file-output
    appendToFile(liveness, "filename;family-based;full-coverage;full-coverage-configs")

    // family-based
    println("checking family-based")
    val family_ast = prepareAST[TranslationUnit](tunit.asInstanceOf[TranslationUnit])
    val family_env = createASTEnv(family_ast)
    val family_function_defs = filterASTElems[FunctionDef](family_ast)

    val tfams = System.currentTimeMillis()
    family_function_defs.map(intraCfGFunctionDef(_, family_env))
    val tfame = System.currentTimeMillis()

    val tfam = tfame - tfams

    // base variant
    println("checking base variant")
    val base_ast = prepareAST[TranslationUnit](
      deriveProductFromConfiguration[TranslationUnit](family_ast.asInstanceOf[TranslationUnit], new Configuration(FeatureExpr.base, fm)))
    val base_env = createASTEnv(base_ast)
    val base_function_defs = filterASTElems[FunctionDef](base_ast)

    val tbases = System.currentTimeMillis()
    base_function_defs.map(intraCfGFunctionDef(_, base_env))
    val tbasee = System.currentTimeMillis()

    val tbase = tbasee - tbases

    // full coverage
    println("checking full coverage")
    val configs = ConfigurationCoverage.naiveCoverageAny(family_ast, fm, family_env.asInstanceOf[ConfigurationCoverage.ASTEnv])
    var current_config = 1
    var tfullcoverage: Long = 0

    for (config <- configs) {
      println("checking configuration " + current_config + " of " + configs.size)
      current_config += 1
      val product_ast = prepareAST[TranslationUnit](deriveProductFromConfiguration[TranslationUnit](family_ast, new Configuration(config, fm)))
      val product_env = createASTEnv(product_ast)
      val product_function_defs = filterASTElems[FunctionDef](product_ast)

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
    val myenv = createASTEnv(f)

    val ss = if (f.stmt.innerStatements.isEmpty) List() else getAllSucc(f.stmt.innerStatements.head.entry, myenv).map(_._1).filterNot(_.isInstanceOf[FunctionDef])
    for (s <- ss.reverse) {
      in(s, myenv)
      out(s, myenv)
    }

    true
  }
}
