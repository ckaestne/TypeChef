package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.{PrettyPrinter, AST}

object DotGraph extends IOUtilities {
  import java.io.File

  private def getTmpFileName() = File.createTempFile("/tmp", ".dot")
  def map2file(m: List[(AST, List[AST])], env: ASTEnv) = {
    var dotstring = ""
    val fname = getTmpFileName()
    dotstring += "digraph \"" + fname.getName + "\" {" + "\n"
    dotstring += "node [shape=record];\n"

    // iterate ast elements and its successors and add nodes in for each ast element
    for ((o, succs) <- m) {
      val op = esc(PrettyPrinter.print(o))
      dotstring += "\"" + System.identityHashCode(o) + "\" [label=\"{{" + op + "}|" /*+ esc(env.featureExpr(o).toString())*/ + "empty" + "}\"];\n"

      // iterate successors and add edges
      for (succ <- succs) dotstring += "\"" + System.identityHashCode(o) + "\" -> \"" + System.identityHashCode(succ) + "\"\n"
    }
    dotstring = dotstring + "}\n"
    writeToFile(fname.getAbsolutePath, dotstring)
    fname
  }

  private def esc(i: String) = {
    i.replace("\n", "\\l").replace("{", "\\{").replace("}", "\\}").replace("<", "\\<").replace(">", "\\>").replace("\"", "\\\"")
  }
}