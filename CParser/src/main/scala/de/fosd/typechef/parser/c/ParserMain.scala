package de.fosd.typechef.parser.c

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import java.io.File
import java.io.PrintStream
import java.io.FileOutputStream
import java.io.BufferedOutputStream

object MyUtil {
    implicit def runnable(f: () => Unit): Runnable =
        new Runnable() {
            def run() = f()
        }
}

object ParserMain {

    def main(args: Array[String]) = {
        val parserMain = new ParserMain(new CParser(null))

        for (filename <- args) {
            println("**************************************************************************")
            println("** Processing file: " + filename)
            println("**************************************************************************")
            val parentPath = new File(filename).getParent()
            parserMain.parserMain(filename, parentPath, new CTypeContext())
            println("**************************************************************************")
            println("** End of processing for: " + filename)
            println("**************************************************************************")
        }
    }
}

object LinuxParserMain {

    def main(args: Array[String]) = {
        println("loading feature model...");
        val start = System.currentTimeMillis
        //        val featuremodel = FeatureModel.createFromDimacsFile_2Var("2.6.33.3-2var.dimacs")
        //        val featuremodel = FeatureModel.createFromCNFFile("linux_2.6.28.6.fm.cnf")
        val featuremodel = null
        println("done. [" + (System.currentTimeMillis - start) + " ms]")

        val parserMain = new ParserMain(new CParser(featuremodel))

        for (filename <- args) {
            println("**************************************************************************")
            println("** Processing file: " + filename)
            println("**************************************************************************")
            val parentPath = new File(filename).getParent()
            parserMain.parserMain(filename, parentPath, new CTypeContext())
            println("**************************************************************************")
            println("** End of processing for: " + filename)
            println("**************************************************************************")
        }
    }
}


class ParserMain(p: CParser) {

    def parserMain(filePath: String, parentPath: String): AST =
        parserMain(filePath, parentPath, new CTypeContext())


    def parserMain(filePath: String, parentPath: String, initialContext: CTypeContext): AST = {
        val logStats = MyUtil.runnable(() => {
            if (TokenWrapper.profiling) {
                val statistics = new PrintStream(new BufferedOutputStream(new FileOutputStream(filePath + ".stat")))
                LineInformation.printStatistics(statistics)
                statistics.close()
            }
        })

        Runtime.getRuntime().addShutdownHook(new Thread(logStats))

        val lexerStartTime = System.currentTimeMillis
        val in = CLexer.lexFile(filePath, parentPath).setContext(initialContext)

        val parserStartTime = System.currentTimeMillis
        val result = p.phrase(p.translationUnit)(in, FeatureExpr.base)
        val endTime = System.currentTimeMillis

        println(printParseResult(result, FeatureExpr.base))

        println("Parsing statistics: \n" +
                "  Duration lexing: " + (parserStartTime - lexerStartTime) + " ms\n" +
                "  Duration parsing: " + (endTime - parserStartTime) + " ms\n" +
                "  Tokens: " + in.tokens.size + "\n" +
                "  Tokens Consumed: " + ProfilingTokenHelper.totalConsumed(in) + "\n" +
                "  Tokens Backtracked: " + ProfilingTokenHelper.totalBacktracked(in) + "\n" +
                "  Tokens Repeated: " + ProfilingTokenHelper.totalRepeated(in) + "\n" +
                "  Repeated Distribution: " + ProfilingTokenHelper.repeatedDistribution(in) + "\n")

        //        checkParseResult(result, FeatureExpr.base)

        //        val resultStr: String = result.toString
        //        println("FeatureSolverCache.statistics: " + FeatureSolverCache.statistics)
        //        val writer = new FileWriter(filePath + ".ast")
        //        writer.write(resultStr);
        //        writer.close
        //        println("done.")

        //XXX: that's too simple, we need to typecheck also split results.
        // Moreover it makes the typechecker crash currently (easily workaroundable though).
        result match {
            case p.Success(ast, _) => ast
            case _ => null
        }
    }

    def printParseResult(result: p.MultiParseResult[Any], feature: FeatureExpr): String = {
        result match {
            case p.Success(ast, unparsed) => {
                if (unparsed.atEnd)
                    (feature.toString + "\tsucceeded\n")
                else
                    (feature.toString + "\tstopped before end (at " + unparsed.first.getPosition + ")\n")
            }
            case p.NoSuccess(msg, unparsed, inner) =>
                (feature.toString + "\tfailed " + msg + "\n")
            case p.SplittedParseResult(f, left, right) => {
                printParseResult(left, feature.and(f)) + "\n" +
                        printParseResult(right, feature.and(f.not))
            }
        }
    }

    def checkParseResult(result: p.MultiParseResult[Any], feature: FeatureExpr) {
        result match {
            case p.Success(ast, unparsed) => {
                if (!unparsed.atEnd)
                    new Exception("parser did not reach end of token stream with feature " + feature + " (" + unparsed.first.getPosition + "): " + unparsed).printStackTrace
                //succeed
            }
            case p.NoSuccess(msg, unparsed, inner) =>
                new Exception(msg + " at " + unparsed + " with feature " + feature + " " + inner).printStackTrace
            case p.SplittedParseResult(f, left, right) => {
                checkParseResult(left, feature.and(f))
                checkParseResult(right, feature.and(f.not))
            }
        }
    }


}