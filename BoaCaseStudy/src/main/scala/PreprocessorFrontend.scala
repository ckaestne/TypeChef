import org.anarres.cpp.Main

import java.util.Properties
import java.io.File
import java.io.FileInputStream

import gnu.getopt.Getopt
import gnu.getopt.LongOpt

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem._

object PreprocessorFrontend {
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
    def preprocessFile(inFileName: String, outFileName: String, extraOpt: Seq[String]) {
        Main.main(Array(
                inFileName,
                "-o",
                outFileName,
                "--include",
                predefMacroDef
            ) ++
            extraOpt ++
            includeFlags)
    }

    def parseFile(filePath: String, parentPath: String) : AST = {
        ParserMain.parserMain(filePath, parentPath, new CTypeContext())
    }

    def main(args: Array[String]): Unit = {
        initSettings
        var extraOpt = List("-p", "_")
        val optionsToForward = "pPUDx"
        val INCLUDE_OPT = 0
        val longOpts = Array(new LongOpt("include", LongOpt.REQUIRED_ARGUMENT, null, INCLUDE_OPT))
        val g = new Getopt("PreprocessorFrontend", args, ":r:I:c:o:" + optionsToForward.flatMap(x => Seq(x, ':')), longOpts)
        var loopFlag = true
        var outputFileNameOpt: Option[String] = None
        do {
            val c = g.getopt()
            if (c != -1) {
                val arg = g.getOptarg()
                c match {
                    case 'r' => setSystemRoot(arg)
                    case 'I' => cmdLinePostIncludes :+= arg
                    case 'c' => loadSettings(arg)
                    case 'o' => outputFileNameOpt = Some(arg)
                    
                    case ':' => println("Missing required argument!"); exit(1)
                    case '?' => println("Unexpected option!"); exit(1)

                    //Pass-through --include.
                    case INCLUDE_OPT => extraOpt ++= List("--include", arg)

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
            outputFileNameOpt match {
                case None => outputFileNameOpt = Some(filename.replace(".c", "") + ".pi")
                case Some(_) => None
            }
            val outputFileName = outputFileNameOpt.get
            val folderPath = new File(outputFileName).getParent

            preprocessFile(filename, outputFileName, extraOpt)
            val ast = parseFile(outputFileName, folderPath)
            new TypeSystem().checkAST(ast)
        }
    }
}
