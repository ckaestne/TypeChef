/*
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 17.03.11
 * Time: 16:09
 */
package de.fosd.typechef.linux

import de.fosd.typechef.parser.c._
import java.io.File
;

object LinuxParser {


    def main(args: Array[String]): Unit = this.main(args, null)
    def main(args: Array[String], check: AST => Unit) = {

        val parserMain = new ParserMain(new CParser(LinuxFeatureModel.featureModelApprox))

        for (filename <- args) {
            println("**************************************************************************")
            println("** Processing file: " + filename)
            println("**************************************************************************")
            val parentPath = new File(filename).getParent()
            val ast = parserMain.parserMain(filename, parentPath, new CTypeContext())
            if (check != null && ast != null)
                check(ast)
            println("**************************************************************************")
            println("** End of processing for: " + filename)
            println("**************************************************************************")
        }
    }
}
