package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr.{FeatureExprParser, FeatureExprFactory, FeatureExpr}
import java.io.{Writer, FileReader, BufferedReader, File}
import de.fosd.typechef.featureexpr.FeatureExprFactory.True


case class CFGNode(val id: Int, val kind: String, val file: File, val line: Int, val name: String, val fexpr: FeatureExpr) {

    def write(writer: Writer) {
        writer.write("N;" + id + ";" + kind + ";" + (if (file != null) file.getPath else "null") + ";" + line + ";" + name + ";" + fexpr.toTextExpr + "\n")
    }

    //    def and(f: FeatureExpr) = new CFGNode(kind, file, line, name, fexpr and f)

    override def toString: String = kind + "-" + name

    //    override def hashCode = kind.hashCode + line + name.hashCode
    //    override def equals(that: Any) = {
    //        if (that.isInstanceOf[CFGNode]) {
    //            val t = that.asInstanceOf[CFGNode]
    //            (this.kind == t.kind) && (this.file == null || t.file ==null || this.file == t.file) && (this.line == t.line) && (this.name == t.name) && (this.fexpr equivalentTo t.fexpr)
    //        } else super.equals(that)
    //    }
}

case class FileCFG(nodes: Set[CFGNode], edges: Set[(CFGNode, CFGNode, FeatureExpr)]) {

    def checkConsistency: Boolean =
        edges.forall(e => (nodes contains e._1) && (nodes contains e._2))

    def link(that: FileCFG): FileCFG = {
        assert(this.checkConsistency)
        assert(that.checkConsistency)

        var nodesToRemove = Set[CFGNode]()

        val thatFunctions: Map[String, Set[CFGNode]] = that.nodes.filter(_.kind == "function").groupBy(e => e.name)
        var thisReplacements: Map[CFGNode, Set[CFGNode]] = Map()
        for (node <- this.nodes) {
            if (node.kind == "declaration") {
                val functions = thatFunctions.get(node.name)
                if (functions.isDefined) {
                    thisReplacements += (node -> functions.get)
                    nodesToRemove = nodesToRemove + node
                }
            }
        }

        val newThisEdges: Set[(CFGNode, CFGNode, FeatureExpr)] = this.edges.flatMap(
            e => if (thisReplacements.contains(e._2))
                thisReplacements(e._2).map(newTarget => (e._1, newTarget, e._3 and newTarget.fexpr)).toSet + ((e._1, e._2, thisReplacements(e._2).foldLeft(e._3)(_ andNot _.fexpr)))
            else Set(e)

        )

        val thisFunctions: Map[String, Set[CFGNode]] = this.nodes.filter(_.kind == "function").groupBy(e => e.name)
        var thatReplacements: Map[CFGNode, Set[CFGNode]] = Map()
        for (node <- that.nodes) {
            if (node.kind == "declaration") {
                val functions = thisFunctions.get(node.name)
                if (functions.isDefined) {
                    thatReplacements += (node -> functions.get)
                    nodesToRemove = nodesToRemove + node
                }
            }
        }

        val newThatEdges = that.edges.flatMap(
            e => if (thatReplacements.contains(e._2))
                thatReplacements(e._2).map(newTarget => (e._1, newTarget, e._3 and newTarget.fexpr)).toSet + ((e._1, e._2, thatReplacements(e._2).foldLeft(e._3)(_ andNot _.fexpr)))
            else Set(e)
        )


        val n = new FileCFG((this.nodes ++ that.nodes).filter(_.fexpr.isSatisfiable()), (newThisEdges ++ newThatEdges).filter(_._3.isSatisfiable()))
        assert(n.checkConsistency)
        n
    }

    def write(writer: Writer) {
        for (n <- nodes.toList.sortBy(_.id)) n.write(writer)
        for ((s, t, f) <- edges) {
            assert(nodes contains s)
            assert(nodes contains t)
            writer.write("E;" + s.id + ";" + t.id + ";" + f.toTextExpr + "\n")
        }
    }


    override def toString: String = "FileCFG(" + nodes + ", " + edges + ")"

}


/**
 * loads creates a whole-project CFG from individual .cfg files
 *
 * to save memory this is performed on the extracted call graphs, not on the in-memory program representation
 *
 * it loads all graphs and subsequently matches declarations with functions. all IDs are rewritten to unique IDs
 */
object WholeProjectCFG {

    val featureExprParser = new FeatureExprParser(FeatureExprFactory.dflt)


    def loadNode(s: String, file: File, filePC: FeatureExpr, isRawFormat: Boolean): (Int, CFGNode) = {
        val fields = s.split(";")
        if (isRawFormat)
            (fields(1).toInt, new CFGNode(IdGen.genId(), fields(2), file, fields(3).toInt, fields(4), parseFExpr(fields(5)) and filePC))
        else
            (fields(1).toInt, new CFGNode(IdGen.genId(), fields(2), new File(fields(3)), fields(4).toInt, fields(5), parseFExpr(fields(6)) and filePC))
    }

    private def parseFExpr(s: String): FeatureExpr = featureExprParser.parse(s)

    def loadEdge(s: String): (Int, Int, FeatureExpr) = {
        val fields = s.split(";")
        (fields(1).toInt, fields(2).toInt, parseFExpr(fields(3)))
    }

    //load a CFG file for one file
    def loadFileCFG(cfgFile: File, filePC: FeatureExpr = True): FileCFG = loadFile(cfgFile, filePC, true)

    //load a whole-project CFG file as a result of a linking process
    def loadCFG(cfgFile: File, filePC: FeatureExpr = True): FileCFG = loadFile(cfgFile, filePC, false)


    private def loadFile(cfgFile: File, filePC: FeatureExpr, isRawFormat: Boolean): FileCFG = {
        val reader = new BufferedReader(new FileReader(cfgFile))

        var nodes = Map[Int, CFGNode]()
        var edges = List[(CFGNode, CFGNode, FeatureExpr)]()

        var line = reader.readLine()
        while (line != null) {
            if (line.charAt(0) == 'N') {
                val node = loadNode(line, cfgFile, filePC, isRawFormat)
                nodes = nodes + node
            }
            if (line.charAt(0) == 'E') {
                val (srcId, targetId, fexpr) = loadEdge(line)
                edges = (nodes(srcId), nodes(targetId), fexpr) :: edges
            }

            line = reader.readLine()
        }

        val n = new FileCFG(nodes.values.toSet, edges.toSet)
        assert(n.checkConsistency)
        n
    }

}

object ComposeCFGs extends App

private object IdGen {
    private var nextId = 1

    def genId(): Int = {
        nextId += 1
        nextId
    }
}
