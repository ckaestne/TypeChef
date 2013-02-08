package de.fosd.typechef

/*
* temporarily copied from PreprocessorFrontend due to technical problems
*/


import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem._
import de.fosd.typechef.crewrite._
import lexer.options.OptionException
import java.io.{FileWriter, File}

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


    def processFile(opt: FrontendOptions) {
        val t1 = System.currentTimeMillis()

        val fm = opt.getFeatureModel().and(opt.getLocalFeatureModel).and(opt.getFilePresenceCondition)
        opt.setFeatureModel(fm)//otherwise the lexer does not get the updated feature model with file presence conditions
        if (!opt.getFilePresenceCondition.isSatisfiable(fm)) {
            println("file has contradictory presence condition. existing.")//otherwise this can lead to strange parser errors, because True is satisfiable, but anything else isn't
            return;
        }
        /*
                val pcs = new FeatureExprParser(FeatureExprFactory.sat).parseFile("../TypeChef-LinuxAnalysis/tmpFolder/pcs.txt")
                opt.getFeatureModelTypeSystem.and(pcs).asInstanceOf[SATFeatureModel].writeToDimacsFile(new File(
                    //"/home/rhein/Tools/TypeChef/GitClone/TypeChef-BusyboxAnalysis/BB_fm.dimacs"
                    "/home/rhein/Tools/TypeChef/GitClone/TypeChef-LinuxAnalysis/tmpFolder/SuperFM.dimacs"
                ))
                if (pcs.and(FeatureExprFactory.True).isSatisfiable(fm)) {
                    println("TypeSystem SuperFM is satisfiable")
                } else {
                    println("TypeSystem SuperFM is NOT satisfiable")
                }
                if (true) return
        */
        val tokens = new lexer.Main().run(opt, opt.parse)

        //        val tokens = preprocessFile(filename, preprocOutputPath, extraOpt, opt.parse, fm)
        val errorXML = new ErrorXML(opt.getErrorXMLFile)
        opt.setRenderParserError(errorXML.renderParserError)
        val t2 = System.currentTimeMillis()
        var t3 = t2
        var t4 = t2
        var t5 = t2
        var t6 = t2
        var t7 = t2
//        ProductGeneration.generateAndTestRandomConfigurations(opt.getSATFeatureModel.and(opt.getLocalFeatureModel).and(opt.getFilePresenceCondition),
//                                                              opt.getSATFeatureModelTypeSystem.and(opt.getLocalFeatureModel).and(opt.getFilePresenceCondition))
        if (opt.parse) {
            println("parsing.")
            val in = CLexer.prepareTokens(tokens)
            val parserMain = new ParserMain(new CParser(fm))
            val ast = parserMain.parserMain(in, opt)
            t3 = System.currentTimeMillis()
            t7 = t3
            t6 = t3
            t5 = t3
            t4 = t3
            if (ast != null && opt.serializeAST)
                serializeAST(ast, opt.getSerializedASTFilename)

            if (ast != null) {
                val fm_ts = opt.getFeatureModelTypeSystem.and(opt.getLocalFeatureModel).and(opt.getFilePresenceCondition)
                val ts = new CTypeSystemFrontend(ast.asInstanceOf[TranslationUnit], fm_ts)

                /** I did some experiments with the TypeChef FeatureModel of Linux, in case I need the routines again, they are saved here. */
                //Debug_FeatureModelExperiments.experiment(fm_ts)

                if (opt.typecheck || opt.writeInterface) {
                    ProductGeneration.typecheckProducts(fm,fm_ts,ast,opt,
                      logMessage=("Time for lexing(ms): " + (t2-t1) + "\nTime for parsing(ms): " + (t3-t2) + "\n"))


//                    println("type checking.")
//                    ts.checkAST
//					          ts.errors.map(errorXML.renderTypeError(_))
                    t4 = System.currentTimeMillis()
                    t5 = t4
                    t6 = t4
                    t7 = t4
                }
                if (opt.writeInterface) {
                    println("inferring interfaces.")
                    val interface = ts.getInferredInterface().and(opt.getFilePresenceCondition)
                    t5 = System.currentTimeMillis()
                    t7 = t5
                    t6 = t5
                    ts.writeInterface(interface, new File(opt.getInterfaceFilename))
                    if (opt.writeDebugInterface)
                        ts.debugInterface(interface, new File(opt.getDebugInterfaceFilename))
                }
                if (opt.conditionalControlFlow) {
                    val cf = new CAnalysisFrontend(ast.asInstanceOf[TranslationUnit], fm_ts)
                    cf.checkCfG()
                    t6 = System.currentTimeMillis()
                    t7 = t6
                }
            }

        }
        errorXML.write()
        if (opt.recordTiming)
            println("timing (lexer, parser, type system, interface inference, conditional control flow, data flow)\n" + (t2 - t1) + ";" + (t3 - t2) + ";" + (t4 - t3) + ";" + (t5 - t4) + ";" + (t6 - t5) + ";" + (t7-t6))

    }


    def serializeAST(ast: AST, filename: String) {
        val fw = new FileWriter(filename)
        fw.write(ast.toString)
        fw.close()
    }
}
