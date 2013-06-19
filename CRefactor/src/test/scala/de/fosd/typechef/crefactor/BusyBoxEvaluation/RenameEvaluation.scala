package de.fosd.typechef.crefactor.BusyBoxEvaluation

import org.junit.Test
import java.io.File
import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.featureexpr.FeatureExpr

class RenameEvaluation extends BusyBoxEvaluation {
    @Test
    def evaluate() {
        val files = getBusyBoxFiles
        files.forall(file => performRefactor(new File(busyBoxPath + file)))
    }
    def performRefactor(fileToRefactor: File): Boolean = {
        val parsed = parse(fileToRefactor)
        val ast = parsed._1
        val fm = parsed._2
        println(ast)
        false
    }

    def applyRefactor(ast: AST): (AST, List[FeatureExpr]) = {
        (null, null)
    }
}
