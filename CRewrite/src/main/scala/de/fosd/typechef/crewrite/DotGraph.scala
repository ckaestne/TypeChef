package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.{PrettyPrinter, AST}

object DotGraph extends IOUtilities {
  import java.io.File

  private val normalNodeFontName = "Calibri"
  private val normalNodeFontColor = "black"
  private val normalNodeFillColor = "white"

  private val errorNodeFontName = "Calibri"
  private val errorNodeFontColor = "black"
  private val errorNodeFillColor = "#CD5200"

  private val normalConnectionEdgeColor = "black"
  // https://mailman.research.att.com/pipermail/graphviz-interest/2001q2/000042.html
  private val normalConnectionEdgeThickness = "setlinewidth(1)"

  private val errorConnectionEdgeColor = "red"
  private val errorConnectionEdgeThickness = "setlinewidth(4)"

  private def getTmpFileName = File.createTempFile("/tmp", ".dot")

  def map2file(m: List[(AST, List[AST])], env: ASTEnv, errorNodes: List[AST] = List(), errorConnections: List[(AST, AST)] = List()) = {
    var dotstring = ""
    val fname = getTmpFileName

    //
    dotstring += "digraph \"" + fname.getName + "\" {" + "\n"
    dotstring += "node [shape=record];\n"

    // iterate ast elements and its successors and add nodes in for each ast element
    for ((o, succs) <- m) {
      val op = esc(PrettyPrinter.print(o))
      dotstring += "\"" + System.identityHashCode(o) + "\""
      dotstring += "["
      dotstring += "label=\"{{" + op + "}|" + esc(env.featureExpr(o).toString()) + "}\", "

      // current node is one of the error nodes
      // apply specific formatting
      if (errorNodes.filter(_.eq(o)).size > 0) {
        dotstring += "color=\"" + errorNodeFontColor + "\", "
        dotstring += "fontname=\"" + errorNodeFontName + "\", "
        dotstring += "style=\"filled\"" + ", "
        dotstring += "fillcolor=\"" + errorNodeFillColor + "\""
      } else {
        dotstring += "color=\"" + normalNodeFontColor + "\", "
        dotstring += "fontname=\"" + normalNodeFontName + "\", "
        dotstring += "style=\"filled\"" + ", "
        dotstring += "fillcolor=\"" + normalNodeFillColor + "\""
      }

      dotstring += "];\n"

      // iterate successors and add edges
      for (succ <- succs) {

        dotstring += "\"" + System.identityHashCode(o) + "\" -> \"" + System.identityHashCode(succ) + "\""
        dotstring += "["

        // current connection is one of the erroneous connections
        // apply specific formatting
        if (errorConnections.filter({s => s._1.eq(succ) && s._2.eq(o)}).size > 0) {
          dotstring += "color=\"" + errorConnectionEdgeColor + "\", "
          dotstring += "style=\"" + errorConnectionEdgeThickness + "\""
        } else {
          dotstring += "color=\"" + normalConnectionEdgeColor + "\", "
          dotstring += "style=\"" + normalConnectionEdgeThickness + "\""
        }
        dotstring += "];\n"
      }
    }


    dotstring = dotstring + "}\n"
    writeToFile(fname.getAbsolutePath, dotstring)
    fname
  }

  private def esc(i: String) = {
    i.replace("\n", "\\l").
      replace("{", "\\{").
      replace("}", "\\}").
      replace("<", "\\<").
      replace(">", "\\>").
      replace("\"", "\\\"").
      replace("|", "\\|")
  }
}