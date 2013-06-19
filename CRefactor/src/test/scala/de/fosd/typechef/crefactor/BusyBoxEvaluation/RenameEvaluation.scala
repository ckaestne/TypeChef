package de.fosd.typechef.crefactor.BusyBoxEvaluation

import org.junit.Test
import java.io.File
import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.featureexpr.FeatureExpr

class RenameEvaluation extends BusyBoxEvaluation {
    @Test
    def evaluate() {
        println(completeBusyBoxPath)




        val testData = getClass.getResource("/BusyBoxAllFeatures.config")
        val fm = getClass.getResource("/busybox_Configs/")
        val testData2 = getClass.getResource("/busybox_Configs/")
        val file = new File(testData.getFile)
        println(allFeatures)
        println(allFeatures.size)
        println(getBusyBoxFiles)

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
