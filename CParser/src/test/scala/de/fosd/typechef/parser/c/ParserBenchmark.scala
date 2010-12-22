package de.fosd.typechef.parser.c


import junit.framework._
import java.io._
;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import org.junit.Test

object ParserBenchmark extends Application {

    val p = new CParser()
    def parseFile(fileName: String) {
        var inputStream = getClass.getResourceAsStream("/" + fileName)
        if (inputStream == null && new File(fileName).exists)
            inputStream = new FileInputStream(new File(fileName))
        assertNotNull("file not found " + fileName, inputStream)
        val in = CLexer.lexStream(inputStream, fileName, "testfiles/cgram/")
        println(in.tokens.size)
        val result = p.phrase(p.translationUnit)(in, FeatureExpr.base)
        (result: @unchecked) match {
            case p.Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                //succeed
            }
            case p.NoSuccess(msg, unparsed, inner) =>
                println(msg + " at " + unparsed + " " + inner)
        }

    }

    val start1 = System.currentTimeMillis
    parseFile("cgram/test30.c")
    println("test30: " + p.debugTokenCounter + ", backtracked " + p.debugBacktrackingCounter + " in " + (System.currentTimeMillis - start1) + " ms")

    p.debugTokenCounter = 0

    val start2 = System.currentTimeMillis
    parseFile("D:\\work\\TypeChef\\CParser\\src\\test\\resources\\other/grep.pi")
    println("minifork: " + p.debugTokenCounter + ", backtracked " + p.debugBacktrackingCounter + " in " + (System.currentTimeMillis - start2) + " ms")

}