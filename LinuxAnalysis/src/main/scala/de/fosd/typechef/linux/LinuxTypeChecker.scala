/*
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 17.03.11
 * Time: 16:13
 */
package de.fosd.typechef.linux

import java.io.File
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.TypeSystem

object LinuxTypeChecker {
    def main(args: Array[String]): Unit = {
        for (path <- args) {
            val folder = new File(path).getParent

            val ast = new ParserMain(new CParser).parserMain(path, folder)
            if (ast != null)
                new TypeSystem().checkAST(ast)
        }
    }
}
