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
    def parseFile(fileName: String) = {
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
           in
    }

    var start = System.currentTimeMillis
//    var in = parseFile("cgram/test30.c")
    var in = parseFile("D:\\work\\TypeChef\\CParser\\src\\test\\resources\\cgram/test30.c")
    println("test30: " + ProfilingTokenHelper.totalConsumed(in) + ", backtracked " + ProfilingTokenHelper.totalBacktracked(in) +", repeated "+ProfilingTokenHelper.totalRepeated(in) + " in " + (System.currentTimeMillis - start) + " ms")
    reportUnparsedTokens(in)

    start = System.currentTimeMillis
    in=parseFile("D:\\work\\TypeChef\\CParser\\src\\test\\resources\\other/grep.pi")
    println("grep: " + ProfilingTokenHelper.totalConsumed(in) + ", backtracked " + ProfilingTokenHelper.totalBacktracked(in) +", repeated "+ProfilingTokenHelper.totalRepeated(in) + " in " + (System.currentTimeMillis - start) + " ms")


    def reportUnparsedTokens(in: TokenReader[TokenWrapper, CTypeContext]) {
        for (t<-in.tokens)
            if (t.profile_consumed==0)
                println("not consumed: "+t)
    }

}