package de.fosd.typechef.crefactor.BusyBoxEvaluation

import org.junit.Test
import java.io.File
import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.featureexpr.FeatureExpr

class RenameEvaluation extends BusyBoxEvaluation {
    @Test
    def evaluate() {
        println(completeBusyBoxPath)
        //analyseDir(new File(absoluteBusyBoxPath))
    }
    def performRefactor(fileToRefactor: File): Boolean = {
        val parsed = parse(fileToRefactor)
        val ast = parsed._1
        val fm = parsed._2

        false
    }

    def applyRefactor(ast: AST): (AST, List[FeatureExpr]) = {
        (null, null)
    }
}
