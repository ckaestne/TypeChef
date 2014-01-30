package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import org.junit.Test
import java.io.{StringWriter, FileNotFoundException, InputStream}
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}
import de.fosd.typechef.parser.c.TranslationUnit
import de.fosd.typechef.parser.c.FunctionDef
import de.fosd.typechef.crewrite.asthelper.CASTEnv

/**
 * just some starter code for experimentation, not a real test yet
 */
class InterCFGTest extends TestHelper  {


    @Test def test_two_functions() {
        val folder = "testfiles/"
        val filename = "callgraph/intercfgtest01.c"
        val is: InputStream = getClass.getResourceAsStream("/" + folder + filename)
        if (is == null)
            throw new FileNotFoundException("Input file not fould!")

        val ast = parseFile(is, folder, filename)
        val env = CASTEnv.createASTEnv(ast)

        val interCFG = new InterCFGProducer(ast)

        val fdefs = interCFG.filterAllASTElems[FunctionDef](ast)

        val a = new StringWriter()
        val dot = new DotGraph(a)
        dot.writeHeader("test")

        val fsuccs = fdefs.map(interCFG.getAllSucc(_, env))

        def lookupFExpr(e: AST): FeatureExpr = e match {
            case o if env.isKnown(o) => env.featureExpr(o)
            case _ => FeatureExprFactory.True
        }

        for (s <- fsuccs)
            dot.writeMethodGraph(s, lookupFExpr, "")
        dot.writeFooter()
        dot.close()
        println(a)
//        println(interCFG.callGraph.toString())
    }
}
