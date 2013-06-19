package de.fosd.typechef.crefactor.BusyBoxEvaluation

import org.junit.Test
import java.io.File
import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.featureexpr.FeatureExpr

class RenameEvaluation extends BusyBoxEvaluation {
    @Test
    def evaluate() {
        val files = getBusyBoxFiles
        val refactor = files.map(file => performRefactor(new File(busyBoxPath + file)))
        logger.info("Refactor succ: " + refactor.contains(false))
    }
    def performRefactor(fileToRefactor: File): Boolean = {
        val parsed = parse(fileToRefactor)
        val ast = parsed._1
        val fm = parsed._2
        println("finished " + fileToRefactor.getName)
        false
    }

    def applyRefactor(ast: AST): (AST, List[FeatureExpr]) = {
        (null, null)
    }
}
