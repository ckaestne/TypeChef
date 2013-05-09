package de.fosd.typechef.typesystem

import java.io.File
import de.fosd.typechef.parser.c._
import java.util.Collections

object Main {
  def main(args: Array[String]): Unit = {
    val createInterface = args.size > 0 && args(0) == "-interface"
    for (path <- args.take(if (createInterface) 1 else 0)) {
      val folder = new File(path).getParent

      val ast = new ParserMain(new CParser).parserMain(path, Collections.singletonList(folder))
      if (ast != null && ast.isInstanceOf[TranslationUnit]) {
        val ts = new CTypeSystemFrontend(ast.asInstanceOf[TranslationUnit])
        ts.checkAST
        if (createInterface) {
          val interface = ts.getInferredInterface()
          ts.writeInterface(interface, new File(path + ".interface"))
          ts.debugInterface(interface, new File(path + ".dbginterface"))
        }
      }
    }
  }
}
