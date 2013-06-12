package de.fosd.typechef

/*
* temporarily copied from PreprocessorFrontend due to technical problems
*/


import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem._
import de.fosd.typechef.crewrite._
import java.io._
import parser.TokenReader
import de.fosd.typechef.options.{FrontendOptionsWithConfigFiles, FrontendOptions, OptionException}
import de.fosd.typechef.parser.c.CTypeContext
import de.fosd.typechef.parser.c.TranslationUnit
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.conditional.{One, Choice, Opt}
import de.fosd.typechef.error.WithPosition

object Frontend {


    def main(args: Array[String]) {
        // load options
        val opt = new FrontendOptionsWithConfigFiles()
        try {
            try {
                opt.parseOptions(args)
            } catch {
                case o: OptionException => if (!opt.isPrintVersion) throw o
            }

            if (opt.isPrintVersion) {
                var version = "development build"
                try {
                    val cl = Class.forName("de.fosd.typechef.Version")
                    version = "version " + cl.newInstance().asInstanceOf[VersionInfo].getVersion
                } catch {
                    case e: ClassNotFoundException =>
                }

                println("TypeChef " + version)
                return
            }
        }

        catch {
            case o: OptionException =>
                println("Invocation error: " + o.getMessage)
                println("use parameter --help for more information.")
                return
        }

        processFile(opt)
    }

    private class StopWatch {
        var lastStart: Long = 0
        var currentPeriod: String = "none"
        var times: Map[String, Long] = Map()

        private def measure(checkpoint: String) {
            times = times + (checkpoint -> System.currentTimeMillis())
        }

        def start(period: String) {
            val now = System.currentTimeMillis()
            val lastTime = now - lastStart
            times = times + (currentPeriod -> lastTime)
            lastStart = now
            currentPeriod = period
        }

        def get(period: String): Long = times.getOrElse(period, 0)

    }




    def astStats(ast: TranslationUnit, filePC: FeatureExpr) {
        val outputTopLevel = new FileWriter("asttoplevel.lst",true)
        val outputAST = new FileWriter("astany.lst",true)


        def findNesting(ast: Product, expr: FeatureExpr, newContext:Boolean) {
            def writeNode(a:Any, expr:FeatureExpr) =
                outputAST.write("%s;%s;%s;%d\n".format(a.getClass.getName,expr, if (a.isInstanceOf[WithPosition]) a.asInstanceOf[WithPosition].getPositionFrom.getFile else "", if (a.isInstanceOf[WithPosition]) a.asInstanceOf[WithPosition].getPositionFrom.getLine else -1))

            for (child<-ast.productIterator) {
                if (child.isInstanceOf[Choice[AST]]) {
                    val Choice(f,a,b) = child.asInstanceOf[Choice[AST]]
                    findNesting(a, expr and f,true)
                    findNesting(b, expr andNot f,true)
                }         else
                if (child.isInstanceOf[One[AST]]) {
                    val One(a:AST) = child.asInstanceOf[One[AST]]
                    if (newContext) writeNode(a,expr)
                    findNesting(a,expr,false)
                }     else
                if (child.isInstanceOf[Opt[_]]) {
                    val Opt(f,a) = child.asInstanceOf[Opt[_]]
                    if (!f.isTautology()) writeNode(a, expr and f)
                    if (a.isInstanceOf[AST])
                        findNesting(a.asInstanceOf[AST], expr and f, false)
                } else if (child.isInstanceOf[Product])
                    findNesting(child.asInstanceOf[Product], expr, false)

            }
        }

        for (Opt(f,tl)<-ast.defs)
            outputTopLevel.write("%s;%s;%s;%d\n".format(tl.getClass.getName,f and filePC, tl.getPositionFrom.getFile, tl.getPositionFrom.getLine))


        findNesting(ast, filePC, false)


        outputTopLevel.close()
        outputAST.close()
    }

    def processFile(opt: FrontendOptions) {
        val errorXML = new ErrorXML(opt.getErrorXMLFile)
        opt.setRenderParserError(errorXML.renderParserError)

        val stopWatch = new StopWatch()
        stopWatch.start("loadFM")

        val fm = opt.getLexerFeatureModel().and(opt.getLocalFeatureModel).and(opt.getFilePresenceCondition)
        opt.setFeatureModel(fm) //otherwise the lexer does not get the updated feature model with file presence conditions
        if (!opt.getFilePresenceCondition.isSatisfiable(fm)) {
            println("file has contradictory presence condition. existing.") //otherwise this can lead to strange parser errors, because True is satisfiable, but anything else isn't
            return;
        }

        var ast: AST = null
        if (opt.reuseAST && opt.parse && new File(opt.getSerializedASTFilename).exists()) {
            println("loading AST.")
            ast = loadSerializedAST(opt.getSerializedASTFilename)
            if (ast == null)
                println("... failed reading AST\n")
        }

        stopWatch.start("lexing")
        //no parsing if read serialized ast
        val in = if (ast == null) lex(opt) else null


        if (opt.parse) {
            stopWatch.start("parsing")

            if (ast == null) {
                //no parsing and serialization if read serialized ast
                val parserMain = new ParserMain(new CParser(fm))
                ast = parserMain.parserMain(in, opt)

                stopWatch.start("serialize")
                if (ast != null && opt.serializeAST)
                    serializeAST(ast, opt.getSerializedASTFilename)
            }


            astStats(ast.asInstanceOf[TranslationUnit],opt.getFilePresenceCondition)

            if (ast != null) {
                val fm_ts = opt.getTypeSystemFeatureModel.and(opt.getLocalFeatureModel).and(opt.getFilePresenceCondition)
                val ts = new CTypeSystemFrontend(ast.asInstanceOf[TranslationUnit], fm_ts, opt)

                /** I did some experiments with the TypeChef FeatureModel of Linux, in case I need the routines again, they are saved here. */
                //Debug_FeatureModelExperiments.experiment(fm_ts)

                if (opt.typecheck || opt.writeInterface) {
                    //ProductGeneration.typecheckProducts(fm,fm_ts,ast,opt,
                    //logMessage=("Time for lexing(ms): " + (t2-t1) + "\nTime for parsing(ms): " + (t3-t2) + "\n"))
                    //ProductGeneration.estimateNumberOfVariants(ast, fm_ts)

                    stopWatch.start("typechecking")
                    println("type checking.")
                    ts.checkAST()
                    ts.errors.map(errorXML.renderTypeError(_))
                }
                if (opt.writeInterface) {
                    stopWatch.start("interfaces")
                    val interface = ts.getInferredInterface().and(opt.getFilePresenceCondition)

                    stopWatch.start("writeInterfaces")
                    ts.writeInterface(interface, new File(opt.getInterfaceFilename))
                    if (opt.writeDebugInterface)
                        ts.debugInterface(interface, new File(opt.getDebugInterfaceFilename))
                }
                if (opt.conditionalControlFlow) {
                    stopWatch.start("controlFlow")

                    val cf = new CAnalysisFrontend(ast.asInstanceOf[TranslationUnit], fm_ts)
                    cf.checkCfG()
                }
                if (opt.dataFlow) {
                    stopWatch.start("dataFlow")
                    ProductGeneration.dataflowAnalysis(fm_ts, ast, opt,
                        logMessage = ("Time for lexing(ms): " + (stopWatch.get("lexing")) + "\nTime for parsing(ms): " + (stopWatch.get("parsing")) + "\n"))
                }

            }

        }
        stopWatch.start("done")
        errorXML.write()
        if (opt.recordTiming)
            println("timing (lexer, parser, type system, interface inference, conditional control flow, data flow)\n" + (stopWatch.get("lexing")) + ";" + (stopWatch.get("parsing")) + ";" + (stopWatch.get("typechecking")) + ";" + (stopWatch.get("interfaces")) + ";" + (stopWatch.get("controlFlow")) + ";" + (stopWatch.get("dataFlow")))

    }


    def lex(opt: FrontendOptions): TokenReader[CToken, CTypeContext] = {
        val tokens = new lexer.Main().run(opt, opt.parse)
        val in = CLexer.prepareTokens(tokens)
        in
    }

    def serializeAST(ast: AST, filename: String) {
        val fw = new ObjectOutputStream(new FileOutputStream(filename))
        fw.writeObject(ast)
        fw.close()
    }

    def loadSerializedAST(filename: String): AST = {
        val fr = new ObjectInputStream(new FileInputStream(filename))
        val ast = fr.readObject().asInstanceOf[AST]
        fr.close()
        ast
    }
}
