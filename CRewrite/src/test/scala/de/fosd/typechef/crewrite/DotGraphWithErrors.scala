package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.{ASTEnv, PrettyPrinter, AST}
import de.fosd.typechef.conditional.Opt
import java.io.Writer

class DotGraphWithErrors(fwriter: Writer) extends DotGraph(fwriter) {

    private val errorNodeFontName = "Calibri"
    private val errorNodeFontColor = "black"
    private val errorNodeFillColor = "#CD5200"
    private val errorConnectionEdgeColor = "red"
    private val errorConnectionEdgeThickness = "setlinewidth(4)"

    def writeMethodGraphWithErrors(m: List[(AST, List[Opt[AST]])], env: ASTEnv, errorNodes: List[AST] = List(), errorConnections: List[(AST, AST)] = List()) {
        // iterate ast elements and its successors and add nodes in for each ast element
        for ((o, csuccs) <- m) {
            val op = esc(PrettyPrinter.print(o))
            fwriter.write("\"" + System.identityHashCode(o) + "\"")
            fwriter.write("[")
            fwriter.write("label=\"{{" + op + "}|" + esc(env.featureExpr(o).toString()) + "}\", ")

            // current node is one of the error nodes
            // apply specific formatting
            if (errorNodes.filter(_.eq(o)).size > 0) {
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

            // iterate successors and add edges
            for (Opt(f, succ) <- csuccs) {
                fwriter.write("\"" + System.identityHashCode(o) + "\" -> \"" + System.identityHashCode(succ) + "\"")
                fwriter.write("[")

                // current connection is one of the erroneous connections
                // apply specific formatting
                if (errorConnections.filter({s => s._1.eq(succ) && s._2.eq(o)}).size > 0) {
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
