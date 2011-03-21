/*
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 17.03.11
 * Time: 16:09
 */
package de.fosd.typechef.linux

import de.fosd.typechef.parser.c._
import java.io.{FileReader, File}
import de.fosd.typechef.featureexpr.{FeatureExprParser, FeatureExpr}
;

object LinuxParser {


    def main(args: Array[String]): Unit = this.main(args, null)
    def main(args: Array[String], check: AST => Unit) = {


        for (filename <- args) {
            println("**************************************************************************")
            println("** Processing file: " + filename)
            println("**************************************************************************")
            //load feature model in .fm file if available
            val featureModelFile = new File(filename + ".fm")
            val featureExpr = if (featureModelFile.exists) loadFeatureModel(featureModelFile) else FeatureExpr.base

            //create parser and start parsing
            val parserMain = new ParserMain(new CParser(LinuxFeatureModel.featureModelApprox.and(featureExpr)))

            val parentPath = new File(filename).getParent()
            val ast = parserMain.parserMain(filename, parentPath)
            if (check != null && ast != null)
                check(ast)
            println("**************************************************************************")
            println("** End of processing for: " + filename)
            println("**************************************************************************")
        }
    }

    private def loadFeatureModel(filename: File): FeatureExpr =
        new FeatureExprParser().parse(new FileReader(filename))
}
