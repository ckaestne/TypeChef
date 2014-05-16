package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.{Declaration, FunctionDef, ExternalDef}
import de.fosd.typechef.featureexpr.FeatureExpr
import scala.collection.mutable.Map
import de.fosd.typechef.featureexpr.FeatureExprFactory.False

/**
 * The (mutable, conditional) call graph is a smaller abstraction than the control flow graph
 * and contains only edges between methods. Multiple edges between
 * methods are combined. Edges and nodes can have conditions, as usual.
 */
class CallGraph {

    var nodes: Map[ExternalDef, FeatureExpr] = Map()
    var edges: Map[(ExternalDef, ExternalDef), FeatureExpr] = Map()

    def addNode(node: ExternalDef, condition: FeatureExpr) {
        val newNodeCondition = nodes.getOrElse(node, False) or condition
        nodes.put(node, newNodeCondition)
    }

    def addEdge(from: ExternalDef, to: ExternalDef, edgeCondition: FeatureExpr) = {
        val newEdgeCondition = edges.getOrElse((from, to), False) or edgeCondition
        edges.put((from, to), newEdgeCondition)
    }

    override def toString() =
        edges.map(e => "%s -> %s if %s".format(printName(e._1._1), printName(e._1._2), e._2)).mkString("\n")

    private[crewrite] def printName(externalDef: ExternalDef) = externalDef match {
        case f: FunctionDef => f.getName
        case e => "External Declaration @ " + e.getPositionFrom.getLine
    }
}
