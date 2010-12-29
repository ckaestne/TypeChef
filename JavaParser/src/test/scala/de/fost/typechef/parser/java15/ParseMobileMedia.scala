package de.fost.typechef.parser.java15
import de.fosd.typechef.featureexpr.FeatureExpr

import de.fosd.typechef.parser.java15._
import de.fosd.typechef.parser._

import java.io._

object ParseMobileMedia {

    val path = "W:\\work\\workspaces\\cide2runtime\\MobileMedia08_OO\\src\\lancs\\mobilemedia"

    val dir = new File(path)

    def main(args: Array[String]) {
        checkDir(dir)
    }

    def checkDir(dir: File) {
        if (dir.isFile)
            checkFile(dir)
        else
            //recurse
            dir.listFiles.map(checkDir(_))
    }
    def checkFile(file: File) {
        if (file.getName.endsWith(".java")) {
            println(file.getAbsolutePath)



            val lexerStartTime = System.currentTimeMillis
            val tokens = JavaLexer.lexFile(file.getAbsolutePath)

            val parserStartTime = System.currentTimeMillis
            val p=new JavaParser()
            var ast = p.phrase(p.CompilationUnit)(tokens, FeatureExpr.base)
            val endTime = System.currentTimeMillis

            println("Parsing statistics: \n" +
                       "  Duration lexing: " + (parserStartTime - lexerStartTime) + " ms\n" +
                       "  Duration parsing: " + (endTime - parserStartTime) + " ms\n" +
                       "  Tokens: " + tokens.tokens.size + "\n" +
                       "  Tokens Consumed: " + ProfilingTokenHelper.totalConsumed(tokens) + "\n" +
                       "  Tokens Backtracked: " + ProfilingTokenHelper.totalBacktracked(tokens) + "\n" +
                       "  Tokens Repeated: " + ProfilingTokenHelper.totalRepeated(tokens) + "\n"+
                       "  Repeated Distribution: " + ProfilingTokenHelper.repeatedDistribution(tokens) + "\n")

            ast match {
                case p.Success(ast, unparsed) => { if (!unparsed.atEnd) println("parser did not reach end of token stream: " + unparsed) }
                case p.NoSuccess(msg, unparsed, inner) => println(msg + " at " + unparsed + " " + inner)
                case p.SplittedParseResult(f,a,b)=>println("split")
            }
        }
    }

}