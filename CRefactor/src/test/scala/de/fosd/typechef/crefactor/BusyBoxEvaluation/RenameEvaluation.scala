package de.fosd.typechef.crefactor.BusyBoxEvaluation

import org.junit.Test
import java.io.File
import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.crefactor.util.TimeMeasurement
import de.fosd.typechef.crefactor.Morpheus

class RenameEvaluation extends BusyBoxEvaluation {
    @Test
    def evaluate() {
        val files = getBusyBoxFiles
        val refactor = files.map(file => {
            var stats = List[Any]()
            val parseTypeCheckMs = new TimeMeasurement
            val parsed = parse(new File(busyBoxPath + file))
            val ast = parsed._1
            val fm = parsed._2
            val parseTypeCheckTime = parseTypeCheckMs.getTime
            stats ::= parseTypeCheckTime

            val morpheus = new Morpheus(ast, fm)
            true
        })
        logger.info("Refactor succ: " + refactor.contains(false))

    }
    def performRefactor(fileToRefactor: File): Boolean = {
        val parsed = parse(fileToRefactor)

        println("finished " + fileToRefactor.getName)
        false
    }

    def applyRefactor(ast: AST): (AST, List[FeatureExpr]) = {
        (null, null)
    }
}
