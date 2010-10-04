package de.fosd.typechef.parser.c
import org.anarres.cpp.Main

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import java.io.FileWriter
import junit.framework._
import junit.framework.Assert._

object BoaFilesTest extends Application {

    val fileList = List("alias", "boa", "buffer", "cgi",
        "cgi_header", "config", "escape", "get", "hash", "ip", "log",
        "mmap_cache", "pipe", "queue", "read", "request", "response",
        "select", "signals", "util", "sublog")

    val boaDir = "d:\\work\\TypeChef\\boa\\src"
    val cygwinFile = "d:\\work\\TypeChef\\boa\\src\\cygwin.h"
    def getFullPath(fileName: String) = boaDir + "\\" + fileName
    def preprocessFile(fileName: String) {
        Main.main(Array(
            getFullPath(fileName + ".c"), //
            "-o",
            getFullPath(fileName + ".pi"), //
            "--include",
            cygwinFile, //
            "-p",
            "_", //
            "-I",
            boaDir, //
            "-I",
            "C:\\cygwin\\usr\\include", //
            "-I",
            "C:\\cygwin\\lib\\gcc\\i686-pc-cygwin\\3.4.4\\include", //
            "-U", "HAVE_LIBDMALLOC"))
    }

    def parseFile(fileName: String) {
        val file = getFullPath(fileName + ".pi")
        val initialContext = new CTypeContext().addType("__uint32_t")
        val result = new CParser().translationUnit(
            CLexer.lexFile(file, "testfiles/cgram/").setContext(initialContext), FeatureExpr.base
            )
        val resultStr: String = result.toString
        println(FeatureSolverCache.statistics)
        val writer = new FileWriter(file + ".ast")
        writer.write(resultStr);
        writer.close
        println("done.")

        //        System.out.println(resultStr)

        (result: @unchecked) match {
            case Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream (" + unparsed.first.getPositionStr + "): " + unparsed, unparsed.atEnd)
                //succeed
            }
            case NoSuccess(msg, context, unparsed, inner) =>
                fail(msg + " at " + unparsed.first.getPositionStr + " " + unparsed + " with context " + context + " " + inner)
        }

    }

//        preprocessFile("hash")
    parseFile("hash")
}