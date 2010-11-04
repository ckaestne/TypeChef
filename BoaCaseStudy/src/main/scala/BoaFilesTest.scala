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

object BoaFilesTest {
    def parseFile(fileName: String) : AST = {
        val filePath = getFullPath(fileName + ".pi")
        val parentPath = getParentPath(fileName + ".pi")
        //XXX: should this be still here?
        val initialContext = new CTypeContext().addType("__uint32_t")
        ParserMain.parserMain(filePath, parentPath, initialContext)
    }

    ////////////////////////////////////////
    // Boa test case.
    ////////////////////////////////////////

    //val boaDir = "d:\\work\\TypeChef\\boa\\src"
    val boaDir = "boa" + File.separator + "src"

    def getFullPath(fileName: String) = boaDir + File.separator + fileName
    def getParentPath(fileName: String) = boaDir
    def preprocessFile(fileName: String) =
        PreprocessorFrontend.preprocessFile(getFullPath(fileName + ".c"), getFullPath(fileName + ".pi"))
 
    def main(args: Array[String]) = {
    	PreprocessorFrontend.initSettings
        val g = new Getopt("testprog", args, ":r:I:c:");
        var loopFlag = true
        do {
            val c = g.getopt()
            if (c != -1) {
                val arg = g.getOptarg()
                c match {
                    case 'r' => PreprocessorFrontend.setSystemRoot(arg)
                    case 'I' => PreprocessorFrontend.postIncludeDirs :+= arg
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

        val fileList = List(
            "alias", "boa", "buffer", "cgi",
            "cgi_header", "config", "escape", "get", "hash", "ip", "log",
            "mmap_cache", "pipe", "queue", "read", "request", "response",
            "select", "signals", "sublog", "util"
          )

        for (filename <- fileList) {
            println("**************************************************************************")
            println("** Processing file: "+filename)
            println("**************************************************************************")
            preprocessFile(filename)
            val ast = parseFile(filename)
            new TypeSystem().checkAST(ast)
            println("**************************************************************************")
	    println("** End of processing for: " + filename)
            println("**************************************************************************")
        }
    }
}
