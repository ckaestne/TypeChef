package de.fosd.typechef

import de.fosd.typechef.featureexpr.FeatureModel
import java.util.Properties
import java.io.File
import java.io.FileInputStream

import gnu.getopt.Getopt
import gnu.getopt.LongOpt

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem._
import org.anarres.cpp.{Token, Main}

object PreprocessorFrontend {

    //can be overriden with command line parameters p and t
    def PARSEAFTERPREPROCESSING = true
    def TYPECHECKAFTERPARSING = false

    ////////////////////////////////////////
    // General setup of built-in headers, should become more general and move
    // elsewhere.
    ////////////////////////////////////////
    val predefSettings = new Properties()
    val settings = new Properties(predefSettings)

    def systemRoot = settings.getProperty("systemRoot")
    def systemIncludes = settings.getProperty("systemIncludes")
    def predefMacroDef = settings.getProperty("predefMacros")

    def setSystemRoot(value: String) = settings.setProperty("systemRoot", value)

    var preIncludeDirs: Seq[String] = Nil
    var postIncludeDirs: Seq[String] = Nil
    var cmdLinePostIncludes: Seq[String] = Nil

    def initSettings {
        predefSettings.setProperty("systemRoot", File.separator)
        predefSettings.setProperty("systemIncludes", "usr" + File.separator + "include")
        predefSettings.setProperty("predefMacros", "host" + File.separator + "platform.h") //XXX not so nice a default
        //XXX no default for GCC includes - hard to guess (one could invoke GCC with special options to get it, though, but it's better to do that to generate the settings file).
    }

    def loadPropList(key: String) = for (x <- settings.getProperty(key, "").split(",")) yield x.trim

    def loadSettings(configPath: String) = {
        settings.load(new FileInputStream(configPath))
        preIncludeDirs = loadPropList("preIncludes") ++ preIncludeDirs
        println("preIncludes: " + preIncludeDirs)
        println("systemIncludes: " + systemIncludes)
        postIncludeDirs = postIncludeDirs ++ loadPropList("postIncludes")
        println("postIncludes: " + postIncludeDirs)
    }

    def includeFlags =
        ((preIncludeDirs ++ List(systemIncludes) ++ postIncludeDirs).flatMap(
            (path: String) =>
                if (path != null && !("" equals path))
                    List(systemRoot + File.separator + path)
                else
                    List()
        ) ++ cmdLinePostIncludes).flatMap((path: String) => List("-I", path))


    ////////////////////////////////////////
    // Preprocessor/parser invocation
    ////////////////////////////////////////
    def preprocessFile(inFileName: String, outFileName: String, extraOpt: Seq[String]): java.util.List[Token] =
        new Main().run(Array(
            inFileName,
            "-o",
            outFileName,
            "--include",
            predefMacroDef
        ) ++
                extraOpt ++
                includeFlags, PARSEAFTERPREPROCESSING, !PARSEAFTERPREPROCESSING)


    def main(args: Array[String]): Unit = {
        initSettings
        var extraOpt = List("-p", "_")
        val optionsToForward = "pPUDx"
        val INCLUDE_OPT = 0
        val OPEN_FEAT_OPT = 1
        val longOpts = Array(new LongOpt("include", LongOpt.REQUIRED_ARGUMENT, null, INCLUDE_OPT),
            new LongOpt("openFeat", LongOpt.REQUIRED_ARGUMENT, null, OPEN_FEAT_OPT))
        val g = new Getopt("PreprocessorFrontend", args, ":r:I:c:o:t" + optionsToForward.flatMap(x => Seq(x, ':')), longOpts)
        var loopFlag = true
        var preprocOutputPathOpt: Option[String] = None
        var parse = PARSEAFTERPREPROCESSING
        var typecheck = TYPECHECKAFTERPARSING
        do {
            val c = g.getopt()
            if (c != -1) {
                val arg = g.getOptarg()
                c match {
                    case 'r' => setSystemRoot(arg)
                    case 'I' => cmdLinePostIncludes :+= arg
                    case 'c' => loadSettings(arg)
                    case 'o' => preprocOutputPathOpt = Some(arg)
                    case 'p' => parse = true
                    case 't' => typecheck = true

                    case ':' => println("Missing required argument!"); exit(1)
                    case '?' => println("Unexpected option!");
                    exit(1)

                    //Pass-through --include and --openFeat.
                    case INCLUDE_OPT => extraOpt ++= List("--include", arg)
                    case OPEN_FEAT_OPT => extraOpt ++= List("--openFeat", arg)

                    //Pass-through some other options
                    case _ => if (optionsToForward contains c) {
                        extraOpt ++= List("-" + c.asInstanceOf[Char], arg)
                    }
                }
            } else {
                loopFlag = false
            }
        } while (loopFlag)
        println(includeFlags)
        val remArgs = args.slice(g.getOptind(), args.size)

        for (filename <- remArgs) {
            preprocOutputPathOpt match {
                case None => preprocOutputPathOpt = Some(filename.replace(".c", ".pi"))
                case Some(_) => None
            }
            val preprocOutputPath = preprocOutputPathOpt.get
            val parserInput = preprocOutputPath
            val folderPath = new File(preprocOutputPath).getParent

            val tokens = preprocessFile(filename, preprocOutputPath, extraOpt)
            if (parse) {
                val in = CLexer.prepareTokens(tokens)
                val parserMain = new ParserMain(new CParser(getFeatureModel(preprocOutputPath)))
                val ast = parserMain.parserMain(in)
                if (typecheck)
                    new TypeSystem().checkAST(ast)
            }
        }
    }

    def getFeatureModel(cfilename: String): FeatureModel = null
}
