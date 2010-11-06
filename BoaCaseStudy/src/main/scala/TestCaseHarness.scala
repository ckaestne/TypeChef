import java.io.FileInputStream
import de.fosd.typechef.parser.c._

import gnu.getopt.Getopt
import gnu.getopt.LongOpt

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import de.fosd.typechef.typesystem._

import java.io.FileWriter
import java.io.File

import junit.framework._
import junit.framework.Assert._

trait TestProject {
    def initialParsingContext: CTypeContext 
    def fileList: Array[(String, String, String, String)]
    def extraPreprocessorOpt: Array[String]
}

abstract class AbstractTestProject extends TestProject {
    def projDir: String

    protected def getFullPath(fileName: String) = projDir + File.separator + fileName
    override def fileList = for (name <- nameList) yield (name, getFullPath(name + ".c"), getFullPath(name + ".pi"), projDir)
    override def extraPreprocessorOpt = Array("-p", "_")
    
    protected def nameList: Array[String]

    override def initialParsingContext: CTypeContext = 
        new CTypeContext().
        //XXX: should this be still here?
        addType("__uint32_t")
}

class DirTest(_projDir: String) extends AbstractTestProject {
    override def projDir = _projDir
    protected override def nameList = (for (file <- (new java.io.File(projDir)).listFiles if file.isFile if file.getName.endsWith(".c")) yield file.getName.replaceFirst("\\.c$", ""))
}

class TestCaseHarness(testCase: TestProject) {
    def preprocessFile(inpName: String, outName: String) =
        PreprocessorFrontend.preprocessFile(inpName, outName, testCase.extraPreprocessorOpt)

    def parseFile(filePath: String, parentPath: String) : AST = {
        ParserMain.parserMain(filePath, parentPath, testCase.initialParsingContext)
    }
 
    def run(args: Array[String]): Unit = {
        PreprocessorFrontend.initSettings
        val g = new Getopt("testprog", args, ":r:I:c:");
        var loopFlag = true
        do {
            val c = g.getopt()
            if (c != -1) {
                val arg = g.getOptarg()
                c match {
                    case 'r' => PreprocessorFrontend.setSystemRoot(arg)
                    case 'I' => PreprocessorFrontend.cmdLinePostIncludes :+= arg
                    case 'c' => PreprocessorFrontend.loadSettings(arg)
                    case ':' => println("Missing required argument!")
                    case '?' => None
                }
            } else {
                loopFlag = false
            }
        } while (loopFlag)
        println(PreprocessorFrontend.includeFlags)
        val remArgs = args.slice(g.getOptind(), args.size) //XXX: not yet used!

        for ((shortName, inpName, outName, folderPath) <- testCase.fileList) {
                println("**************************************************************************")
                println("** Processing file: " + shortName)
                println("**************************************************************************")
                preprocessFile(inpName, outName)
                if (false) { //XXX for the VAMOS workshop, disable parsing.
                    val ast = parseFile(outName, folderPath)
                    new TypeSystem().checkAST(ast)
                }
                println("**************************************************************************")
                println("** End of processing for: " + shortName)
                println("**************************************************************************")
        }
    }
}
