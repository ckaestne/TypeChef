package de.fosd.typechef.crefactor.busyBoxVerifacation

import org.junit.Test
import java.io.{InputStream, FilenameFilter, File}
import de.fosd.typechef.crefactor.Logging
import de.fosd.typechef.parser.c._
import java.util.{IdentityHashMap, Collections}
import de.fosd.typechef.parser.c.TranslationUnit
import de.fosd.typechef.parser.c.CTypeContext
import de.fosd.typechef.crewrite.{ConditionalNavigation, ASTNavigation}

trait BusyBoxVerification extends Logging with ASTNavigation with ConditionalNavigation {

    protected val busyBoxPath = "../TypeChef-BusyBoxAnalysis/busybox-1.18.5/"

    protected val OUTPUT_PATH = "../TypeChef-BusyBoxAnalysis/result/"

    protected val refactor_name = "refactored"

    protected var refactoredFiles = 0

    protected val nsToMs = 1000000

    def performRefactor(fileToRefactor: File): Boolean

    @Test def verify() {
        logger.info("+++ Starting Renaming Verification on BusyBox +++")
        val testStartTime = currentTime
        val succ = analyseDir(new File(busyBoxPath))
        val testEndTime = currentTime
        logger.info("+++ Verification finished - Runtime: " + (testEndTime - testStartTime) + "ms\n Verified " + refactoredFiles +
                " Files +++")
        assert(succ)
    }

    protected def analyseDir(dirToAnalyse: File): Boolean = {
        if (dirToAnalyse.isDirectory) {
            val piFiles = dirToAnalyse.listFiles(new FilenameFilter {
                def accept(input: File, file: String): Boolean = file.endsWith(".pi")
            })
            val dirs = dirToAnalyse.listFiles(new FilenameFilter {
                def accept(input: File, file: String) = input.isDirectory
            })

            // perform refactoring on all found .pi - files
            val filesSucc = piFiles.map(performRefactor(_))
            // continue on all found directories
            val dirSucc = dirs.map(analyseDir(_)) ++ filesSucc
            !dirSucc.exists(_ == false)
        } else true
    }

    protected def currentTime: Long = java.lang.management.ManagementFactory.getThreadMXBean.getCurrentThreadCpuTime / nsToMs

    protected def parseFile(stream: InputStream, file: String, dir: String): TranslationUnit = new ParserMain(new CParser).parserMain(() => CLexer.lexStream(stream, file, Collections.emptyList(), null), new CTypeContext, SilentParserOptions).asInstanceOf[TranslationUnit]

    protected def getAllRelevantIds(a: Any): List[Id] = {
        a match {
            case id: Id => if (!(id.name.startsWith("__builtin"))) List(id) else List()
            case gae: GnuAsmExpr => List()
            case l: List[_] => l.flatMap(x => getAllRelevantIds(x))
            case p: Product => p.productIterator.toList.flatMap(x => getAllRelevantIds(x))
            case k => List()
        }
    }

    protected def analsyeDeclUse(map: IdentityHashMap[Id, List[Id]]): List[Int] = map.keySet().toArray(Array[Id]()).map(key => map.get(key).length).toList


    protected def writeFile(file: File, output: String) {
        val out_file = new java.io.FileOutputStream(file)
        val out_stream = new java.io.PrintStream(out_file)
        out_stream.print(output)
        out_stream.flush()
        out_file.flush()
        out_file.close()
    }
}
