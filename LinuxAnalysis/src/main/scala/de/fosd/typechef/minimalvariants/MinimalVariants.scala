package de.fosd.typechef.minimalvariants

import de.fosd.typechef.lexer.Main
import de.fosd.typechef.featureexpr.FeatureExpr
import FeatureExpr._
import scala.collection.JavaConversions._
import scala.math.min


object MinimalVariants {
    def main(args: Array[String]): Unit = {

        val filename = args(0)


        val tokenStream = new Main().run(Array(filename), true, false, null)

        println("done parsing. " + tokenStream.size)

        var formulaSet = Set[FeatureExpr]()
        for (t <- tokenStream)
            formulaSet = formulaSet + t.getFeature

        println("done reduce replication. " + formulaSet.size)

        var implFormulaSet = List[FeatureExpr]()
        for (t <- formulaSet)
            if (!t.isTautology)
                if (!implFormulaSet.exists(o => (o implies t).isTautology))
                    implFormulaSet = t :: implFormulaSet

        println("done implication reduction. " + implFormulaSet.size)

        val nodes = (0 until implFormulaSet.size).toList
        var edges = Set[(Int, Int)]()
        for (a <- nodes)
            for (b <- a + 1 until implFormulaSet.size)
                if ((implFormulaSet(a) mex implFormulaSet(b)).isTautology)
                    edges = edges + ((a, b))



        println(edges.size)

        println("minimal number of variants: " +cromatic(nodes, edges))
    }

    private def v(vertexid: Int, colorid: Int) = FeatureExpr.createDefinedExternal("v_" + vertexid + "_" + colorid)

    def cromatic(vertices: List[Int], edges: Set[(Int, Int)]): Int =
        search(vertices, edges, 1, min(vertices.size,32), vertices.size)


    private def search(vertices: List[Int], edges: Set[(Int, Int)], low: Int, up: Int, last: Int): Int = {
        if (low == up)
            if (k_colorable(vertices, edges, low)) low else last
        else {
            val m: Int = (low + up) / 2
            if (k_colorable(vertices, edges, m))
                search(vertices, edges, low, m - 1, m)
            else search(vertices, edges, m + 1, up, last)
        }
    }


    def k_colorable(vertices: List[Int], edges: Set[(Int, Int)], k: Int): Boolean = {


        //for encoding see here: http://www7.in.tum.de/um/bibdb/kugele/kugele_diploma06.pdf (page 14f)

        var formula: FeatureExpr = base

        //         To enforce that each vertex is assigned a color, we
        //introduce formula '1.
        for (vertex <- vertices) {
            formula = formula and ((0 until k).foldRight(dead)(v(vertex, _) or _))
        }

        //The second step is to ensure, that no two adjacent vertices vi, vj have the same
        //coloring. Let '2 be the conjunction of the formula ¬(v1...
        //for each edge (vi, vj) in the graph G. By applying DeMorgan’s law we get the following
        for ((from, to) <- edges) {
            formula = formula and (
                    (0 until k).foldRight(base)(
                        (k, f) => (v(from, k) and v(to, k)).not and f)
                    )
        }

        val result = formula.isSatisfiable
        println(k + " - " + result)
        result

    }


}