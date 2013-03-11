package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional.{Opt, ConditionalLib, Conditional}
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.c.FunctionDef
import java.io.{Writer, FileWriter}
import java.util

class DotGraph2(fwriter: Writer) extends IOUtilities {

    import java.io.File

    val printedNodes = new util.IdentityHashMap[AST, Object]()
    val FOUND = new Object()

    private val normalNodeFontName = "Calibri"
    private val normalNodeFontColor = "black"
    private val normalNodeFillColor = "white"

    private val externalDefNodeFontColor = "blue"


    private val errorNodeFontName = "Calibri"
    private val errorNodeFontColor = "black"
    private val errorNodeFillColor = "#CD5200"

    private val normalConnectionEdgeColor = "black"
    // https://mailman.research.att.com/pipermail/graphviz-interest/2001q2/000042.html
    private val normalConnectionEdgeThickness = "setlinewidth(1)"

    private val errorConnectionEdgeColor = "red"
    private val errorConnectionEdgeThickness = "setlinewidth(4)"

    private def getTmpFileName: File = File.createTempFile("/tmp", ".dot")

    private def asText(o: AST): String = o match {
        case FunctionDef(_, decl, _, _) => "Function " + decl.getName
        case s: Statement => "Stmt " + s.getPositionFrom.getLine + ": " + PrettyPrinter.print(s).take(20)
        case e: Expr => "Expr " + e.getPositionFrom.getLine + ": " + PrettyPrinter.print(e).take(20)
        case o => esc(PrettyPrinter.print(o)).take(20)
    }

    private def lookupFExpr(e: AST, env: ASTEnv, externalDefFExprs: Map[ExternalDef, FeatureExpr]): String = e match {
        case o if env.isKnown(o) => env.featureExpr(o).toString()
        case e: ExternalDef => externalDefFExprs.get(e).map(_.toString).getOrElse("?")
        case _ => "?"
    }

    def writeMethodGraph(m: List[(AST, List[Opt[AST]])], env: ASTEnv, externalDefFExprs: Map[ExternalDef, FeatureExpr]) {
        // iterate ast elements and its successors and add nodes in for each ast element
        for ((o, csuccs) <- m) {
            writeNode(o, env, externalDefFExprs)

            // iterate successors and add edges
            for (Opt(f, succ) <- csuccs) {
                fwriter.write("\"" + System.identityHashCode(o) + "\" -> \"" + System.identityHashCode(succ) + "\"")
                fwriter.write("[")

                fwriter.write("label=\"" + f.toTextExpr + "\", ")
                fwriter.write("color=\"" + normalConnectionEdgeColor + "\", ")
                fwriter.write("style=\"" + normalConnectionEdgeThickness + "\"")
                fwriter.write("];\n")
            }
        }
    }

    def writeNode(o: AST, env: ASTEnv, externalDefFExprs: Map[ExternalDef, FeatureExpr]) {
        if (!printedNodes.containsKey(o)) {
            printedNodes.put(o, FOUND)
            val op = esc(asText(o))
            fwriter.write("\"" + System.identityHashCode(o) + "\"")
            fwriter.write("[")
            fwriter.write("label=\"{{" + op + "}|" + esc(lookupFExpr(o, env, externalDefFExprs)) + "}\", ")

            fwriter.write("color=\"" + (if (o.isInstanceOf[ExternalDef]) externalDefNodeFontColor else normalNodeFontColor) + "\", ")
            fwriter.write("fontname=\"" + normalNodeFontName + "\", ")
            fwriter.write("style=\"filled\"" + ", ")
            fwriter.write("fillcolor=\"" + normalNodeFillColor + "\"")

            fwriter.write("];\n")
        }
    }
    def writeFooter() {
        fwriter.write("}\n")
    }
    def writeHeader(title: String) {
        fwriter.write("digraph \"" + title + "\" {" + "\n")
        fwriter.write("node [shape=record];\n")
    }
    private def esc(i: String) = {
        i.replace("\n", "\\l").
            replace("{", "\\{").
            replace("}", "\\}").
            replace("<", "\\<").
            replace(">", "\\>").
            replace("\"", "\\\"").
            replace("|", "\\|").
            replace(" ", "\\ ").
            replace("\\\"", "\\\\\"").
            replace("\\\\\"", "\\\\\\\"").
            replace("\\\\\\\\\"", "\\\\\\\"")
    }

    def close() {
        fwriter.close()
    }
}