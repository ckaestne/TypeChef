package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.c.FunctionDef
import java.io.Writer
import java.util

trait CFGWriter {

    def writeNode(node: AST, fexpr: FeatureExpr, containerName: String)

    def writeEdge(source: AST, target: AST, fexpr: FeatureExpr)

    def writeFooter()

    def writeHeader(filename: String)

    def close()

    protected val printedNodes = new util.IdentityHashMap[AST, Object]()
    protected val FOUND = new Object()



    protected def writeNodeOnce(o: AST, fexpr: () => FeatureExpr, containerName: String) {
        if (!printedNodes.containsKey(o)) {
            printedNodes.put(o, FOUND)
            writeNode(o, fexpr(), containerName)
        }
    }

    def writeMethodGraph(m: List[(AST, List[Opt[AST]])], lookupFExpr: AST => FeatureExpr, containerName: String) {
        // iterate ast elements and its successors and add nodes in for each ast element
        for ((o, csuccs) <- m) {
            writeNodeOnce(o, ()=>lookupFExpr(o), containerName)

            // iterate successors and add edges
            for (Opt(f, succ) <- csuccs) {
                writeNodeOnce(succ, ()=>lookupFExpr(succ), containerName)

                writeEdge(o, succ, f)
            }
        }
    }

}

class DotGraph(fwriter: Writer) extends IOUtilities with CFGWriter {

    protected val normalNodeFontName = "Calibri"
    protected val normalNodeFontColor = "black"
    protected val normalNodeFillColor = "white"

    private val externalDefNodeFontColor = "blue"

    private val featureNodeFillColor = "#CD5200"

    protected val normalConnectionEdgeColor = "black"
    // https://mailman.research.att.com/pipermail/graphviz-interest/2001q2/000042.html
    protected val normalConnectionEdgeThickness = "setlinewidth(1)"

    private val featureConnectionEdgeColor = "red"

    private def asText(o: AST): String = o match {
        case FunctionDef(_, decl, _, _) => "Function " + o.getPositionFrom.getLine + ": " + decl.getName
        case s: Statement => "Stmt " + s.getPositionFrom.getLine + ": " + PrettyPrinter.print(s).take(20)
        case e: Expr => "Expr " + e.getPositionFrom.getLine + ": " + PrettyPrinter.print(e).take(20)
        case Declaration(_, initDecl) => "Decl " + o.getPositionFrom.getLine + ": " + initDecl.map(_.entry.getName).mkString(", ")
        case x => esc(PrettyPrinter.print(x)).take(20)
    }


    def writeEdge(source: AST, target: AST, fexpr: FeatureExpr) {
        fwriter.write("\"" + System.identityHashCode(source) + "\" -> \"" + System.identityHashCode(target) + "\"")
        fwriter.write("[")

        fwriter.write("label=\"" + fexpr.toTextExpr + "\", ")
        fwriter.write("color=\"" + (if (fexpr.isTautology()) normalConnectionEdgeColor else featureConnectionEdgeColor) + "\", ")
        fwriter.write("style=\"" + normalConnectionEdgeThickness + "\"")
        fwriter.write("];\n")
    }

    def writeNode(o: AST, fexpr: FeatureExpr, containerName: String) {
        val op = esc(asText(o))
        fwriter.write("\"" + System.identityHashCode(o) + "\"")
        fwriter.write("[")
        fwriter.write("label=\"{{" + op + "}|" + esc(fexpr.toString) + "}\", ")

        fwriter.write("color=\"" + (if (o.isInstanceOf[ExternalDef]) externalDefNodeFontColor else normalNodeFontColor) + "\", ")
        fwriter.write("fontname=\"" + normalNodeFontName + "\", ")
        fwriter.write("style=\"filled\"" + ", ")
        fwriter.write("fillcolor=\"" + (if (fexpr.isTautology()) normalNodeFillColor else featureNodeFillColor) + "\"")

        fwriter.write("];\n")
    }

    def writeFooter() {
        fwriter.write("}\n")
    }

    def writeHeader(title: String) {
        fwriter.write("digraph \"" + title + "\" {" + "\n")
        fwriter.write("node [shape=record];\n")
    }

    protected def esc(i: String) = {
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


class CFGCSVWriter(fwriter: Writer) extends IOUtilities with CFGWriter {

    /**
     * output format in CSV
     *
     * we distinguish nodes and edges, nodes start with "N" edges with "E"
     *
     * nodes have the following format:
     *
     * N;id;kind;line;name[::container];featureexpr;container
     *
     * * id is an identifier that only has a meaning within a file and that is not stable over multiple runs
     *
     * * kind is one of "function|function-inline|function-static|declaration|statement|expression|unknown"
     *   functions are distinguished into functions with an inline or a static modifier (inline takes precedence)
     *
     * * line refers to the starting position in the .pi file
     *
     * * name is either the name of a function or some debug information together with the name of the containing function.
     *   For functions and declarations, the name is used as a reference and can be used to match nodes across files.
     *   For expressions and statements, the name is used for debugging only and returns the first characters of the statement.
     *   In this case, the name is however followed by :: and the function name that can be used to extract hierarchy information.
     *   Note that function names should be unique in the entire system for each configuration (that is, there may be multiple
     *   functions with the same name but mutually exclusive feature expressions)
     *
     * * featureexpr describes the condition when the node is included
     *
     *
     *
     * edges do not have a line and title:
     *
     * E;sourceid;targetid;featureexpr
     *
     * they connect nodes within a file
     * ids refer to node ids within the file
     * nodeids are always declared before edges connecting them
     *
     * edges between files are not described in the output, but must be computed separately with an external linker
     * that matches nodes based on function/declaration names
     */

    private def asText(o: AST, containerName: String): String = o match {
        case FunctionDef(specs, decl, _, _) =>
            //functions are tagged as inline or static if that modifier occurs at all. not handling conditional
            //modifiers correctly yet
            (if (specs.map(_.entry).contains(InlineSpecifier())) "function-inline;"
            else if (specs.map(_.entry).contains(StaticSpecifier())) "function-static;"
            else "function;") +
                o.getPositionFrom.getLine + ";" + decl.getName
        case s: Statement => "statement;" + s.getPositionFrom.getLine + ";" + esc(PrettyPrinter.print(s).take(20))+"::"+containerName
        case e: Expr => "expression;" + e.getPositionFrom.getLine + ";" + esc(PrettyPrinter.print(e).take(20))+"::"+containerName
        case Declaration(_, initDecl) => "declaration;" + o.getPositionFrom.getLine + ";" + initDecl.map(_.entry.getName).mkString(",")
        case x => "unknown;" + x.getPositionFrom.getLine + ";" + esc(PrettyPrinter.print(x).take(20))+"::"+containerName
    }




    def writeEdge(source: AST, target: AST, fexpr: FeatureExpr) {
        fwriter.write("E;" + System.identityHashCode(source) + ";" + System.identityHashCode(target) + ";" + fexpr.toTextExpr + "\n")
    }

    def writeNode(o: AST, fexpr: FeatureExpr, containerName: String) {
        fwriter.write("N;" + System.identityHashCode(o) + ";" + asText(o, containerName) + ";" + fexpr.toTextExpr + "\n")
    }

    def writeFooter() {
    }

    def writeHeader(title: String) {
    }

    private def esc(i: String) = {
        i.replace(";", "").
            replace("\n", " ")
    }

    def close() {
        fwriter.close()
    }
}


class ComposedWriter(writers: List[CFGWriter]) extends CFGWriter {
    def writeNode(node: AST, fexpr: FeatureExpr, containerName: String) {
        writers.map(_.writeNode(node, fexpr, containerName))
    }

    def writeEdge(source: AST, target: AST, fexpr: FeatureExpr) {
        writers.map(_.writeEdge(source, target, fexpr))
    }

    def writeFooter() {
        writers.map(_.writeFooter())
    }

    def writeHeader(filename: String) {
        writers.map(_.writeHeader(filename))
    }

    def close() {
        writers.map(_.close())
    }
}