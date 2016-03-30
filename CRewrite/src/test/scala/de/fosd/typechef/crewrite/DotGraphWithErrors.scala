package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.{AST, ASTEnv, PrettyPrinter}
import de.fosd.typechef.conditional.Opt
import java.io.Writer

import com.sun.javafx.image.PixelUtils

class DotGraphWithErrors(fwriter: Writer) extends DotGraph(fwriter) {

    private val errorNodeFontName = "Calibri"
    private val errorNodeFontColor = "black"
    private val errorNodeFillColor = "#CD5200"
    private val errorConnectionEdgeColor = "red"
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

    def writeFlows(flows: List[(AST, List[Opt[AST]])], errEdges: List[(AST, AST)] = List()) {
        var coveredEdges: List[(AST, AST)] = List()

        for ((o, edges) <- flows)
            for (Opt(f, e) <- edges) {
                if (! coveredEdges.exists{ x => x._1.equals(o) && x._2.equals(e)} ) {
                    coveredEdges ::= (o,e)
                    fwriter.write("\"" + System.identityHashCode(o) + "\" -> \"" + System.identityHashCode(e) + "\"")
                    fwriter.write("[")

                    // current connection is one of the erroneous connections
                    // apply specific formatting
                    if (errEdges.exists { s => s._1.eq(e) && s._2.eq(o) }) {
                        fwriter.write("label=\"" + f.toTextExpr + "\", ")
                        fwriter.write("color=\"" + errorConnectionEdgeColor + "\", ")
                        fwriter.write("style=\"" + errorConnectionEdgeThickness + "\"")
                    } else {
                        fwriter.write("label=\"" + f.toTextExpr + "\", ")
                        fwriter.write("color=\"" + normalConnectionEdgeColor + "\", ")
                        fwriter.write("style=\"" + normalConnectionEdgeThickness + "\"")
                    }
                    fwriter.write("];\n")
                }

            }
    }
}
