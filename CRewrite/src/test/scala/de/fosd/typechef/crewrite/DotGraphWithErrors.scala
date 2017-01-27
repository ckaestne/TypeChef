package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.{AST, ASTEnv, PrettyPrinter}
import de.fosd.typechef.conditional.Opt
import java.io.Writer
import scala.collection.mutable.{HashMap,HashSet}

class DotGraphWithErrors(fwriter: Writer) extends DotGraph(fwriter) {

    private val errorNodeFontName = "Calibri"
    private val errorNodeFontColor = "black"
    private val errorNodeFillColor = "#CD5200"
    private val errorConnectionPredEdgeColor = "purple"
    private val errorConnectionSuccEdgeColor = "sienna"
    private val errorConnectionEdgeThickness = "setlinewidth(4)"

    def writeNodes(nodes: List[AST], env: ASTEnv, errNodes: List[AST] = List()) {
        for (n <- nodes) {
            val op = esc(PrettyPrinter.print(n))
            fwriter.write("\"" + System.identityHashCode(n) + "\"")
            fwriter.write("[")
            fwriter.write("label=\"{{" + op + "}|" + esc(env.featureExpr(n).toString()) + "}\", ")

            // current node is one of the error nodes
            // apply specific formatting
            if (errNodes.exists(_.eq(n))) {
                fwriter.write("color=\"" + errorNodeFontColor + "\", ")
                fwriter.write("fontname=\"" + errorNodeFontName + "\", ")
                fwriter.write("style=\"filled\"" + ", ")
                fwriter.write("fillcolor=\"" + errorNodeFillColor + "\"")
            } else {
                fwriter.write("color=\"" + normalNodeFontColor + "\", ")
                fwriter.write("fontname=\"" + normalNodeFontName + "\", ")
                fwriter.write("style=\"filled\"" + ", ")
                fwriter.write("fillcolor=\"" + normalNodeFillColor + "\"")
            }
            fwriter.write("];\n")
        }
    }

    def writeFlows(flows: List[(AST, List[Opt[AST]])], errEdges: HashMap[(AST, AST), Boolean] = HashMap()) {
        var coveredEdges: HashSet[(AST, AST)] = HashSet()

        for ((o, edges) <- flows)
            for (Opt(f, e) <- edges) {
                if (! coveredEdges.contains((o,e))) {
                    coveredEdges.+=((o,e))
                    fwriter.write("\"" + System.identityHashCode(o) + "\" -> \"" + System.identityHashCode(e) + "\"")
                    fwriter.write("[")
                    fwriter.write("label=\"" + f.toTextExpr + "\", ")

                    // current connection is one of the erroneous connections
                    // apply specific formatting
                    errEdges.get((e,o)) match {
                        case None =>
                            fwriter.write("color=\"" + normalConnectionEdgeColor + "\", ")
                            fwriter.write("style=\"" + normalConnectionEdgeThickness + "\"")
                        case Some(d) =>
                            fwriter.write("color=\"" + (
                                if (d) errorConnectionPredEdgeColor
                                else errorConnectionSuccEdgeColor) + "\", ")
                            fwriter.write("style=\"" + errorConnectionEdgeThickness + "\"")
                    }
                    fwriter.write("];\n")
                }

            }
    }
}
