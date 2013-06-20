package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.{CASTEnv, ASTEnv, FunctionDef, AST}
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureModel}

object CheckCFG extends IntraCFG with CFGHelper {

    def checkCfG(tunit: AST, fm: FeatureModel = FeatureExprFactory.default.featureModelFactory.empty) {
        val fdefs = filterAllASTElems[FunctionDef](tunit)
        fdefs.map(intraCfGFunctionDef(_, fm))
    }

    private def intraCfGFunctionDef(f: FunctionDef, fm: FeatureModel) = {
        val env = CASTEnv.createASTEnv(f)
        val s = getAllSucc(f, fm, env)
        val p = getAllPred(f, fm, env)

        val errors = compareSuccWithPred(s, p, env)
        CFGErrorOutput.printCFGErrors(s, p, errors, env)

        errors.size > 0
    }

    // given an ast element x and its successors lx: x should be in pred(lx)
    def compareSuccWithPred(lsuccs: List[(AST, CFG)], lpreds: List[(AST, CFG)], env: ASTEnv): List[CFGError] = {
        var errors: List[CFGError] = List()

        // check that number of nodes match
        val lsuccsast = lsuccs.map(_._1)
        val lpredsast = lpreds.map(_._1)
        var sdiff = List[AST]()
        var pdiff = List[AST]()

        for (s <- lsuccsast)
            if (lpredsast.filter(x => s.eq(x)).isEmpty)
                sdiff ::= s

        for (p <- lpredsast)
            if (lsuccsast.filter(x => p.eq(x)).isEmpty)
                pdiff ::= p

        for (sdelem <- sdiff)
            errors = new CFGErrorMis("is not present in preds!", sdelem, env.featureExpr(sdelem)) :: errors

        for (pdelem <- pdiff)
            errors = new CFGErrorMis("is not present in succs!", pdelem, env.featureExpr(pdelem)) :: errors

        // check that number of edges match
        var succ_edges: List[(AST, AST)] = List()
        for ((ast_elem, csuccs) <- lsuccs) {
            for (succ <- csuccs.map(_.entry))
                succ_edges = (ast_elem, succ) :: succ_edges
        }

        var pred_edges: List[(AST, AST)] = List()
        for ((ast_elem, cpreds) <- lpreds) {
            for (pred <- cpreds.map(_.entry))
                pred_edges = (ast_elem, pred) :: pred_edges
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
                errors = new CFGErrorDir("is missing in preds", b1, env.featureExpr(b1), a1, env.featureExpr(a1)) :: errors
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
                errors = new CFGErrorDir("is missing in succs", a1, env.featureExpr(a1), b1, env.featureExpr(b1)) :: errors
            }
        }

        errors
    }
}
