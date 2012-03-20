package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.conditional.ConditionalLib
import de.fosd.typechef.parser.c.{TranslationUnit, FunctionDef, AST}


class CAnalysisFrontend(tunit: AST, fm: FeatureModel = NoFeatureModel) extends CASTEnv with ConditionalControlFlow with Liveness with EnforceTreeHelper {

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

  def checkCfG(fileName: String) = {

    // family-based
    val new_ast = prepareAST[TranslationUnit](tunit.asInstanceOf[TranslationUnit])
    val env = createASTEnv(new_ast)
    val function_defs = filterASTElems[FunctionDef](new_ast)

    val tfams = System.currentTimeMillis()
    function_defs.map(intraCfGFunctionDef(_, env))
    val tfame = System.currentTimeMillis()

    val tfam = tfame - tfams
    println("family-based: " + tfam + "ms")

    // base variant
    val base_ast = prepareAST[TranslationUnit](
      ConditionalLib.deriveProductFromConfiguration[TranslationUnit](new_ast.asInstanceOf[TranslationUnit], new Configuration(FeatureExpr.base, fm)))
    val base_env = createASTEnv(base_ast)
    val base_function_defs = filterASTElems[FunctionDef](base_ast)

    val tbases = System.currentTimeMillis()
    base_function_defs.map(intraCfGFunctionDef(_, base_env))
    val tbasee = System.currentTimeMillis()

    val tbase = tbasee - tbases
    println("base variant: " + tbase + "ms")

//    // product-based function level
//    val tproductf: Long = 0
//    for (function <- function_defs) {
//      for (config <- ConfigurationCoverage.naiveCoverageAny(function, fm, env.asInstanceOf[ConfigurationCoverage.ASTEnv])) {
//        val functionproduct = prepareAST[FunctionDef](ConditionalLib.deriveProductFromConfiguration[FunctionDef](function, new Configuration(config, fm)))
//        val myenv = createASTEnv(functionproduct)
//        val ss = getAllSucc(functionproduct.stmt.innerStatements.head.entry, myenv).map(_._1).filterNot(_.isInstanceOf[FunctionDef])
//        for (s <- ss) {
//          if (! isPartOf(s, functionproduct)) {
//            println(isPartOf(s, function) + " is part of old function")
//          }
//        }
//      }
//
//    }

//    println("product-based: " + tproductf + "ms")
  }

  private def intraCfGFunctionDef(f: FunctionDef, env: ASTEnv) = {
    val myenv = createASTEnv(f)

    val ss = getAllSucc(f.stmt.innerStatements.head.entry, myenv).map(_._1).filterNot(_.isInstanceOf[FunctionDef])
    for (s <- ss) {
      in(s, myenv)
      out(s, myenv)
    }

    true
  }
}