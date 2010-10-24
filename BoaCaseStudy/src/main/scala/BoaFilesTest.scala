import java.io.FileInputStream
import de.fosd.typechef.parser.c._
import org.anarres.cpp.Main

import gnu.getopt.Getopt
import gnu.getopt.LongOpt

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import de.fosd.typechef.typesystem._

import java.io.FileWriter
import java.io.File
import java.util.Properties

import junit.framework._
import junit.framework.Assert._

object BoaFilesTest {
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

	def initSettings {
		predefSettings.setProperty("systemRoot", File.separator)
		predefSettings.setProperty("systemIncludes", "usr" + File.separator + "include")
		predefSettings.setProperty("predefMacros", "host" + File.separator + "platform.h") //XXX not so nice a default
		//XXX no default for GCC includes - hard to guess (one could invoke GCC with special options to get it, though, but it's better to do that to generate the settings file).
	}

	def loadPropList(key: String) = for (x <- settings.getProperty(key, "").split(",")) yield x.trim
	
    def loadSettings(configPath: String) = {
    	settings.load(new FileInputStream(configPath))
    	preIncludeDirs = loadPropList("preIncludes")
    	println("preIncludes: " + preIncludeDirs)
    	println("systemIncludes: " + systemIncludes)
    	postIncludeDirs = loadPropList("postIncludes")
    	println("postIncludes: " + postIncludeDirs)
    }

    def includeFlags = (preIncludeDirs ++ List(systemIncludes) ++ postIncludeDirs).flatMap((path: String) =>
    	if (path != null && !("" equals path))
    		List("-I", systemRoot + File.separator + path)
    	else
    		List())


    ////////////////////////////////////////
    // Preprocessor/parser invocation
    ////////////////////////////////////////

    def preprocessFile(fileName: String) {
        Main.main(Array(
            getFullPath(fileName + ".c"), //
            "-o",
            getFullPath(fileName + ".pi"), //
            "--include",
            predefMacroDef, //
            "-p",
            "_"
        ) ++
          includeFlags ++
            Array("-U", "HAVE_LIBDMALLOC"))
    }

    def parseFile(fileName: String):AST={
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
    
    def main(args: Array[String]) = {
    	initSettings
        val g = new Getopt("testprog", args, ":r:I:c:");
        var loopFlag = true
        do {
            val c = g.getopt()
            if (c != -1) {
                val arg = g.getOptarg()
                c match {
                    case 'r' => setSystemRoot(arg)
                    case 'I' => postIncludeDirs :+= arg
                    case 'c' => loadSettings(arg)
                    case ':' => println("Missing required argument!")
                    case '?' => None
                }
            } else {
                loopFlag = false
            }
        } while (loopFlag)
    	println(includeFlags)
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
            val ast=parseFile(filename)
            new TypeSystem().checkAST(ast)
            println("**************************************************************************")
	    println("** End of processing for: " + filename)
            println("**************************************************************************")
        }
    }
}
