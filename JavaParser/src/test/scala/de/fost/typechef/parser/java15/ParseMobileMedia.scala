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
            val tokens = JavaLexer.lexFile(file.getAbsolutePath)

            var ast = new JavaParser().CompilationUnit(tokens, FeatureExpr.base)

            ast match {
                case Success(ast, unparsed) => { if (!unparsed.atEnd) println("parser did not reach end of token stream: " + unparsed) }
                case NoSuccess(msg, context, unparsed, inner) => println(msg + " at " + unparsed + " with context " + context + " " + inner)
                case SplittedParseResult(f,a,b)=>println("split")
            }
        }
    }

}