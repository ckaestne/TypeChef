package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.{FunctionDef, AST}
import de.fosd.typechef.featureexpr._


class CAnalysisFrontend(tunit: AST, featureModel: FeatureModel = NoFeatureModel) extends CASTEnv with ConditionalControlFlow with EnforceTreeHelper {

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

  def checkCfG() = {
    val new_ast = prepareAST(tunit)
    val env = createASTEnv(new_ast)
    val function_defs = filterASTElems[FunctionDef](new_ast)
    var productnum = 0
    var stproducts = new SatSolverProducts(featureModel).getAllProducts

    while (stproducts.isEmpty.unary_!) {
      productnum += 1
      stproducts = stproducts.tail
      if (productnum % 1000 == 0) println("num product: " + productnum)
    }

    function_defs.map(intraCfGFunctionDef(_, env)).forall(_.==(true))

    if (errors.size > 0) {
      println(errors.foldLeft("")(_ + _))
    }
  }

  private def intraCfGFunctionDef(f: FunctionDef, env: ASTEnv) = {
    val s = getAllSucc(f, env)
    true
  }
}