package de.fosd.typechef.typesystem

import java.io.File
import de.fosd.typechef.parser.c._

object Main {
    def main(args: Array[String]): Unit = {
        for (path <- args) {
            val folder = new File(path).getParent
 
            val ast = ParserMain.parserMain(path, folder)
            new TypeSystem().checkAST(ast)
        }
    }
}
