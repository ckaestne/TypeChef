package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import org.junit.Test
import java.io._
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}
import de.fosd.typechef.parser.c.FunctionDef
import de.fosd.typechef.crewrite.asthelper.CASTEnv

/**
 * just some starter code for experimentation, not a real test yet
 */
class CallGraphTest extends TestHelper {


    private def getCallGraph(filename: String): CallGraph = {
        val folder = "testfiles/"
        val is: InputStream = getClass.getResourceAsStream("/" + folder + filename)
        if (is == null)
            throw new FileNotFoundException("Input file not fould: " + filename)

        val ast = parseFile(is, folder, filename)
        new InterCFGProducer(ast).generateCallGraph()
    }
    /**
     * loads a .cc file which is a semicolon separated file of call graph edges
     * without presence conditions
     */
    private def loadCallGraphReference(filename: String): Set[(String, String)] = {
        val folder = "testfiles/"
        val is: InputStream = getClass.getResourceAsStream("/" + folder + filename)
        if (is == null)
            throw new FileNotFoundException("Input file not fould: " + filename)

        val reader = new BufferedReader(new InputStreamReader(is))
        var line = reader.readLine()
        var result = Set[(String, String)]()
        while (line != null) {
            val entries = line.split(";")
            assert(entries.size >= 2)

            result = result + ((entries(0), entries(1)))

            line = reader.readLine()
        }
        result
    }

    private def compareCallGraphs(filenameStem: String) {
        val expected = loadCallGraphReference(filenameStem + ".cg")
        val found = getCallGraph(filenameStem + ".c")

        var foundError: Boolean = false
        for ((expectedFrom, expectedTo) <- expected) {
            var foundEdge: Boolean = false
            for ((foundFrom, foundTo) <- found.edges.keys.map(keysToString(found))) {
                if (expectedFrom == foundFrom && expectedTo == foundTo)
                    foundEdge = true
            }
            if (!foundEdge) {
                println("Expected Edge %s -> %s not found")
                foundError = true
            }
        }

        for ((foundFrom, foundTo) <- found.edges.keys.map(keysToString(found))) {
            var foundEdge: Boolean = false
            for ((expectedFrom, expectedTo) <- expected) {
                if (expectedFrom == foundFrom && expectedTo == foundTo)
                    foundEdge = true
            }
            if (!foundEdge) {
                println("Unexpected Edge %s -> %s in call graph")
                foundError = true
            }
        }

        assert(!foundError, "Error in call graph comparison")
    }

    private def keysToString(callgraph: CallGraph)(a: (ExternalDef, ExternalDef)): (String, String) =
        (callgraph.printName(a._1), callgraph.printName(a._2))


    @Test
    def compareintercfgtest01() { compareCallGraphs("intercfgtest01")}

}
