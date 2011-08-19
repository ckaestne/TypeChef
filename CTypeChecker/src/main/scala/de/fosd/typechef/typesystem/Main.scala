package de.fosd.typechef.typesystem

import java.io.File
import de.fosd.typechef.parser.c._
import linker.CInferInterface

object Main {
    def main(args: Array[String]): Unit = {
        val createInterface = args.size > 0 && args(0) == "-interface"
        for (path <- args.take(if (createInterface) 1 else 0)) {
            val folder = new File(path).getParent

            val ast = new ParserMain(new CParser).parserMain(path, folder)
            if (ast != null && ast.isInstanceOf[TranslationUnit]) {
                new CTypeSystem().checkAST(ast.asInstanceOf[TranslationUnit])
                if (createInterface) {
                    val i = new CInferInterface {}
                    val interface = i.inferInterface(ast.asInstanceOf[TranslationUnit])
                    i.writeInterface(interface, new File(path + ".interface"))
                    i.debugInterface(interface, new File(path + ".dbginterface"))
                }
            }
        }
    }
}
