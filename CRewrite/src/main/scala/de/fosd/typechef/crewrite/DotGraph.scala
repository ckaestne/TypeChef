package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.c.FunctionDef
import java.io.Writer
import java.util

trait CFGWriter {

  def writeNode(node: AST, env: ASTEnv, externalDefFExprs: Map[ExternalDef, FeatureExpr])
  def writeEdge(source: AST, target: AST, fexpr: FeatureExpr)
  def writeFooter()
  def writeHeader(filename: String)
  def close()

  protected val printedNodes = new util.IdentityHashMap[AST, Object]()
  protected val FOUND = new Object()
  protected def writeNodeOnce(o: AST, env: ASTEnv, map: Map[ExternalDef, FeatureExpr]) {
    if (!printedNodes.containsKey(o)) {
      printedNodes.put(o, FOUND)
      writeNode(o, env, map)
    }
  }

  def writeMethodGraph(m: List[(AST, List[Opt[AST]])], env: ASTEnv, externalDefFExprs: Map[ExternalDef, FeatureExpr]) {
    // iterate ast elements and its successors and add nodes in for each ast element
    for ((o, csuccs) <- m) {
      writeNodeOnce(o, env, externalDefFExprs)

      // iterate successors and add edges
      for (Opt(f, succ) <- csuccs) {
        writeNodeOnce(succ, env, externalDefFExprs)

        writeEdge(o, succ, f)
      }
    }
  }

}

class DotGraph(fwriter: Writer) extends IOUtilities with CFGWriter {

  private val normalNodeFontName = "Calibri"
  private val normalNodeFontColor = "black"
  private val normalNodeFillColor = "white"

  private val externalDefNodeFontColor = "blue"

  private val featureNodeFillColor = "#CD5200"

  private val normalConnectionEdgeColor = "black"
  // https://mailman.research.att.com/pipermail/graphviz-interest/2001q2/000042.html
  private val normalConnectionEdgeThickness = "setlinewidth(1)"

  private val featureConnectionEdgeColor = "red"

  private val errorNodeFontName = "Calibri"
  private val errorNodeFontColor = "black"
  private val errorNodeFillColor = "#CD5200"
  private val errorConnectionEdgeColor = "red"
  private val errorConnectionEdgeThickness = "setlinewidth(4)"

  private def asText(o: AST): String = o match {
    case FunctionDef(_, decl, _, _) => "Function " + o.getPositionFrom.getLine + ": " + decl.getName
    case s: Statement => "Stmt " + s.getPositionFrom.getLine + ": " + PrettyPrinter.print(s).take(20)
    case e: Expr => "Expr " + e.getPositionFrom.getLine + ": " + PrettyPrinter.print(e).take(20)
    case Declaration(_, initDecl) => "Decl " + o.getPositionFrom.getLine + ": " + initDecl.map(_.entry.getName).mkString(", ")
    case x => esc(PrettyPrinter.print(x)).take(20)
  }

  private def lookupFExpr(e: AST, env: ASTEnv, externalDefFExprs: Map[ExternalDef, FeatureExpr]): FeatureExpr = e match {
    case o if env.isKnown(o) => env.featureExpr(o)
    case e: ExternalDef => externalDefFExprs.get(e).getOrElse(FeatureExprFactory.True)
    case _ => FeatureExprFactory.True
  }

  def writeEdge(source: AST, target: AST, fexpr: FeatureExpr) {
    fwriter.write("\"" + System.identityHashCode(source) + "\" -> \"" + System.identityHashCode(target) + "\"")
    fwriter.write("[")

    fwriter.write("label=\"" + fexpr.toTextExpr + "\", ")
    fwriter.write("color=\"" + (if (fexpr.isTautology()) normalConnectionEdgeColor else featureConnectionEdgeColor) + "\", ")
    fwriter.write("style=\"" + normalConnectionEdgeThickness + "\"")
    fwriter.write("];\n")
  }

  def writeNode(o: AST, env: ASTEnv, externalDefFExprs: Map[ExternalDef, FeatureExpr]) {
    val op = esc(asText(o))
    val fexpr = lookupFExpr(o, env, externalDefFExprs)
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


class CFGCSVWriter(fwriter: Writer) extends IOUtilities with CFGWriter {

  /**
   * output format in CSV
   *
   * we distinguish nodes and edges, nodes start with "N" edges with "E"
   *
   * nodes have the following format:
   *
   * N;id;kind;line;name;featureexpr
   *
   * id is an identifier that only has a meaning within a file and that is not stable over multiple runs
   * kind is one of "function|declaration|statement|expression|unknown"
   * line refers to the starting position in the .pi file
   * name is used as a reference for functions and declarations and can be used to match nodes across files
   * name in expressions and statements is used for debugging only and returns the first characters of the statement
   * function names should be unique in the entire system for each configuration (that is, there may be multiple
   * functions with the same name but mutually exclusive feature expressions)
   * featureexpr describes the condition when the node is included
   *
   * edges do not have a line and title:
   *
   * E;sourceid;targetid;featureexpr
   *
   * they connect nodes within a file
   * ids refer to node ids within the file
   * nodeids are always declared before edges connecting them
   *
   * edges between files need to be computed separately based on declaration and function names
   */

  private def asText(o: AST): String = o match {
    case FunctionDef(_, decl, _, _) => "function;" + o.getPositionFrom.getLine + ";" + decl.getName
    case s: Statement => "statement;" + s.getPositionFrom.getLine + ";" + esc(PrettyPrinter.print(s).take(20))
    case e: Expr => "expression;" + e.getPositionFrom.getLine + ";" + esc(PrettyPrinter.print(e).take(20))
    case Declaration(_, initDecl) => "declaration;" + o.getPositionFrom.getLine + ";" + initDecl.map(_.entry.getName).mkString(",")
    case x => "unknown;" + x.getPositionFrom.getLine + ";" + esc(PrettyPrinter.print(x).take(20))
  }

  private def lookupFExpr(e: AST, env: ASTEnv, externalDefFExprs: Map[ExternalDef, FeatureExpr]): FeatureExpr = e match {
    case o if env.isKnown(o) => env.featureExpr(o)
    case e: ExternalDef => externalDefFExprs.get(e).getOrElse(FeatureExprFactory.True)
    case _ => FeatureExprFactory.True
  }

  def writeEdge(source: AST, target: AST, fexpr: FeatureExpr) {
    fwriter.write("E;" + System.identityHashCode(source) + ";" + System.identityHashCode(target) + ";" + fexpr.toTextExpr + "\n")
  }

  def writeNode(o: AST, env: ASTEnv, externalDefFExprs: Map[ExternalDef, FeatureExpr]) {
    val fexpr = lookupFExpr(o, env, externalDefFExprs)
    fwriter.write("N;" + System.identityHashCode(o) + ";" + asText(o) + ";" + fexpr.toTextExpr + "\n")
  }
  def writeFooter() {
    fwriter.write("}\n")
  }
  def writeHeader(title: String) {
    fwriter.write("digraph \"" + title + "\" {" + "\n")
    fwriter.write("node [shape=record];\n")
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
  def writeNode(node: AST, env: ASTEnv, externalDefFExprs: Map[ExternalDef, FeatureExpr]) {
    writers.map(_.writeNode(node, env, externalDefFExprs))
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