package de.fosd.typechef.parser.c
import org.anarres.cpp.Main

import gnu.getopt.Getopt
import gnu.getopt.LongOpt

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import java.io.FileWriter
import java.io.File
import junit.framework._
import junit.framework.Assert._

object BoaFilesTest {
    ////////////////////////////////////////
    // General setup of built-in headers, should become more general and move
    // elsewhere.
    ////////////////////////////////////////
    val isCygwin = false
    var systemRoot = if (isCygwin) "C:\\cygwin" else ""; //Can be non-null also on Unix

    val predefMacroDef = "host" + File.separator + "platform.h"

    val systemIncludes =
    	if (isCygwin)
    		systemRoot + "\\usr\\include"
    	else
    		//"/Users/pgiarrusso/Documents/Admin/Gentoo/usr/include"
    		systemRoot + File.separator + "usr" + File.separator + "include"
    		
    val gccIncludes =
    	if (isCygwin)
    		"C:\\cygwin\\lib\\gcc\\i686-pc-cygwin\\3.4.4\\include"
    	else
    		//"/Users/pgiarrusso/Documents/Admin/Gentoo/usr/lib/gcc/i686-apple-darwin10/4.2.1/include"
    		"/usr/lib/gcc/x86_64-redhat-linux/4.4.4/include"

    var includeDirs =
        if (true)
            List(systemIncludes, gccIncludes)
        else
            List("/usr/local/include",
            "/usr/lib/gcc/x86_64-linux-gnu/4.4.1/include",
            "/usr/lib/gcc/x86_64-linux-gnu/4.4.1/include-fixed",
            "/usr/include/x86_64-linux-gnu",
            "/usr/include")

    def getIncludes = includeDirs.flatMap((path: String) =>
        List("-I", systemRoot + path))


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
            "_",
            "-I", boaDir /* XXX should _not_ be needed, since boa headers are
            included with "boa.h" rather than <boa.h> */
        ) ++
          getIncludes ++
            Array("-U", "HAVE_LIBDMALLOC"))
    }

    def parseFile(fileName: String) {
        val filePath = getFullPath(fileName + ".pi")
        val parentPath = getParentPath(fileName + ".pi")
        //XXX: should this be still here?
        val initialContext = new CTypeContext().addType("__uint32_t")
        return TypeCheckerMain.parserMain(filePath, parentPath, initialContext)
    }

    ////////////////////////////////////////
    // Boa test case.
    ////////////////////////////////////////

    //val boaDir = "d:\\work\\TypeChef\\boa\\src"
    val boaDir = "boa" + File.separator + "src"

    def getFullPath(fileName: String) = boaDir + File.separator + fileName
    def getParentPath(fileName: String) = boaDir

    def main(args: Array[String]) = {
        val g = new Getopt("testprog", args, ":r:I:");
        var loopFlag = true
        do {
            val c = g.getopt()
            if (c != -1) {
                val arg = g.getOptarg()
                c match {
                    case 'r' => systemRoot = arg
                    case 'I' => includeDirs = includeDirs :+ arg
                    case ':' => println("Missing required argument!")
                    case '?' => None
                }
            } else {
                loopFlag = false
            }
        } while (loopFlag)

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
            parseFile(filename)
            println("**************************************************************************")
	    println("** End of processing for: " + filename)
            println("**************************************************************************")
        }
    }
}
