package de.fosd.typechef.parser.c

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import java.io.File

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
            parserMain.parserMain(filename, parentPath)
            println("**************************************************************************")
            println("** End of processing for: " + filename)
            println("**************************************************************************")
        }
    }
}


class ParserMain(p: CParser) {

    def parserMain(filePath: String, parentPath: String): AST = {
        val lexer = (() => CLexer.lexFile(filePath, parentPath, p.featureModel))
        parserMain(lexer, new CTypeContext())
    }

    def parserMain(tokenstream: TokenReader[TokenWrapper, CTypeContext]): AST = {
        parserMain((() => tokenstream), new CTypeContext())
    }

    def parserMain(lexer: () => TokenReader[TokenWrapper, CTypeContext], initialContext: CTypeContext, printStatistics: Boolean = true): AST = {
        //        val logStats = MyUtil.runnable(() => {
        //            if (TokenWrapper.profiling) {
        //                val statistics = new PrintStream(new BufferedOutputStream(new FileOutputStream(filePath + ".stat")))
        //                LineInformation.printStatistics(statistics)
        //                statistics.close()
        //            }
        //        })
        //
        //        Runtime.getRuntime().addShutdownHook(new Thread(logStats))

        val lexerStartTime = System.currentTimeMillis
        val in = lexer().setContext(initialContext)

        val parserStartTime = System.currentTimeMillis
        val result: p.MultiParseResult[AST] = p.phrase(p.translationUnit)(in, FeatureExpr.base)
        //        val result = p.translationUnit(in, FeatureExpr.base)
        val endTime = System.currentTimeMillis

        if (printStatistics) {
            println(printParseResult(result, FeatureExpr.base))

            println("Parsing statistics: \n" +
                    //                "  Duration lexing: " + (parserStartTime - lexerStartTime) + " ms\n" +
                    "  Duration parsing: " + (endTime - parserStartTime) + " ms\n" +
                    "  Tokens: " + in.tokens.size + "\n" +
                    "  Tokens Consumed: " + ProfilingTokenHelper.totalConsumed(in) + "\n" +
                    "  Tokens Backtracked: " + ProfilingTokenHelper.totalBacktracked(in) + "\n" +
                    "  Tokens Repeated: " + ProfilingTokenHelper.totalRepeated(in) + "\n" +
                    //                "  Repeated Distribution: " + ProfilingTokenHelper.repeatedDistribution(in) + "\n" +
                    "  Conditional Tokens: " + countConditionalTokens(in.tokens) + "\n" +
                    "  Distinct Features: " + countFeatures(in.tokens) + "\n" +
                    "  Distinct Feature Expressions: " + countFeatureExpr(in.tokens) + "\n" +
                    "  Choice Nodes: " + countChoiceNodes(result) + "\n")

        }

        //        checkParseResult(result, FeatureExpr.base)

        //        val resultStr: String = result.toString
        //        println("FeatureSolverCache.statistics: " + FeatureSolverCache.statistics)
        //        val writer = new FileWriter(filePath + ".ast")
        //        writer.write(resultStr);
        //        writer.close
        //        println("done.")

        //XXX: that's too simple, we need to typecheck also split results.
        // Moreover it makes the typechecker crash currently (easily workaroundable though).
        val l = result.toList(FeatureExpr.base).filter(_._2.isSuccess)
        if (l.isEmpty) null else l.head._2.asInstanceOf[p.Success[AST]].result
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
                (feature.toString + "\tfailed: " + msg + " at " + unparsed.pos + " (" + inner + ")\n")
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


    def countConditionalTokens(tokens: List[TokenWrapper]): Int =
        tokens.count(_.getFeature != FeatureExpr.base)
    def countFeatures(tokens: List[TokenWrapper]): Int = {
        var features: Set[String] = Set()
        for (t <- tokens)
            features ++= t.getFeature.resolveToExternal.collectDistinctFeatures.map(_.feature)
        features.size
    }
    def countFeatureExpr(tokens: List[TokenWrapper]): Int =
        tokens.foldLeft[Set[FeatureExpr]](Set())(_ + _.getFeature).size

    def countChoiceNodes(ast: p.MultiParseResult[AST]): Int = ast match {
        case p.Success(ast, _) => countChoices(ast)
        case _ => -1
    }


    def countChoices(ast: AST): Int = {
        var result: Int = 0
        //        ast.accept(new ASTVisitor {
        //            def visit(node: AST, ctx: FeatureExpr) {
        //                if (node.isInstanceOf[Choice[_]])
        //                    result += 1
        //                for (opt <- node.getInnerOpt)
        //                    if (opt.feature != FeatureExpr.base && opt.feature != ctx)
        //                        if (!((ctx implies (opt.feature)).isTautology)) {
        //                            result += 1
        //                        }
        //            }
        //            def postVisit(node: AST, feature: FeatureExpr) {}
        //        })
        result
    }

    /* match {
        case x: TokenWrapper => 0
        case a ~ b => countCoices(a, ctx) + countCoices(b, ctx)
        case l: List[Any] => l.foldLeft[Int](0)(_ + countCoices(_, ctx))
        case Opt(f, r) =>
            countCoices(r, ctx and f) + (if ((ctx implies f).isTautology)
                0
            else
                1)
        case c: Choice => countCoices(c.left, ctx and c.feature) + countCoices(c.right, ctx andNot (c.feature)) + 1
        case Some(x) => countCoices(x, ctx)
        case None => 0
        case x: String => 0
        case e => {println(e); assert(false); 0}
    }*/
}