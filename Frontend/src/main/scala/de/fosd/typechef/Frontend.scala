package de.fosd.typechef

/*
* temporarily copied from PreprocessorFrontend due to technical problems
*/


import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem._
import de.fosd.typechef.crewrite._
import featureexpr.{FeatureExpr, FeatureExprFactory}
import lexer.options.OptionException
import java.io.{FileWriter, File}
import parser.Position

object Frontend {


    def main(args: Array[String]): Unit = {
        // load options
        val opt = new FrontendOptionsWithConfigFiles()
        try {
            try {
                opt.parseOptions(args)
            } catch {
                case o: OptionException => if (!opt.isPrintVersion) throw o;
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
                return;
            }
        }

        catch {
            case o: OptionException =>
                println("Invocation error: " + o.getMessage);
                println("use parameter --help for more information.")
                return;
        }

        processFile(opt)
    }


    def processFile(opt: FrontendOptions) {
        val t1 = System.currentTimeMillis()

        val fm = opt.getFeatureModel().and(opt.getLocalFeatureModel).and(opt.getFilePresenceCondition)

        val tokens = new lexer.Main().run(opt, opt.parse)

        //        val tokens = preprocessFile(filename, preprocOutputPath, extraOpt, opt.parse, fm)
        val errorXML = new ErrorXML(opt.errorXMLFile)
        opt.setRenderParserError(errorXML.renderParserError)
        val t2 = System.currentTimeMillis()
        var t3 = t2;
        var t4 = t2;
        var t5 = t2;
        var t6 = t2;
        if (opt.parse) {
            println("parsing.")
            val in = CLexer.prepareTokens(tokens)
            val parserMain = new ParserMain(new CParser(fm))
            val ast = parserMain.parserMain(in, opt)
            t3 = System.currentTimeMillis();
            t6 = t3
            t5 = t3
            t4 = t3
            if (ast != null && opt.serializeAST)
                serializeAST(ast, opt.getSerializedASTFilename)

            if (ast != null) {
                val fm_ts = opt.getFeatureModelTypeSystem().and(opt.getLocalFeatureModel).and(opt.getFilePresenceCondition)
                val ts = new CTypeSystemFrontend(ast.asInstanceOf[TranslationUnit], fm_ts)
                val cf = new CAnalysisFrontend(ast.asInstanceOf[TranslationUnit], fm_ts)
                if (opt.typecheck || opt.writeInterface) {
                    println("type checking.")
                    ts.checkAST
                    ts.errors.map(errorXML.renderTypeError(_))
                    t4 = System.currentTimeMillis();
                    t5 = t4
                    t6 = t4
                }
                if (opt.writeInterface) {
                    println("inferring interfaces.")
                    val interface = ts.getInferredInterface().and(opt.getFilePresenceCondition)
                    t5 = System.currentTimeMillis()
                    t6 = t5
                    ts.writeInterface(interface, new File(opt.getInterfaceFilename))
                    if (opt.writeDebugInterface)
                        ts.debugInterface(interface, new File(opt.getDebugInterfaceFilename))
                }
                if (opt.conditionalControlFlow) {
                    println("checking conditional control flow.")
                    cf.checkCfG()
                    t6 = System.currentTimeMillis()
                }
            }

        }
        errorXML.write()
        if (opt.recordTiming)
            println("timing (lexer, parser, type system, interface inference, conditional control flow)\n" + (t2 - t1) + ";" + (t3 - t2) + ";" + (t4 - t3) + ";" + (t5 - t4) + ";" + (t6 - t5))

    }


    def serializeAST(ast: AST, filename: String) {
        val fw = new FileWriter(filename)
        fw.write(ast.toString)
        fw.close()
    }
}
