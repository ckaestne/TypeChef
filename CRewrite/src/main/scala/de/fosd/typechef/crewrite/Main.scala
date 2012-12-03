package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import java.io.File

object Main {
  def main(args: Array[String]): Unit = {
    for (path <- args) {
      val folder = new File(path).getParent

      val ast = new ParserMain(new CParser).parserMain(path, folder)

      if (ast != null && ast.isInstanceOf[TranslationUnit]) {
        val ca = new CAnalysisFrontend(ast.asInstanceOf[TranslationUnit])
        ca.checkCfG()
      }
    }
  }
}