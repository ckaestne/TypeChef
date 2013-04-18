package de.fosd.typechef.crefactor

import backend.refactor.RenameIdentifier
import org.junit.Test
import java.io._
import de.fosd.typechef.parser.c._
import java.util.{Collections, IdentityHashMap}
import de.fosd.typechef.crewrite.{ConditionalNavigation, ASTNavigation}
import de.fosd.typechef.parser.c.Id
import de.fosd.typechef.parser.c.GnuAsmExpr
import de.fosd.typechef.parser.c.CTypeContext
import de.fosd.typechef.parser.c.TranslationUnit
import java.lang.Thread


/**
 * Test class to test the correctness of the refactoring rename identifier.
 *
 * In this case we can not speak of a test - more of an evaluation, because we are unable to determine the
 * correctness of our declUse Map automatically.
 *
 * The idea behind this test is simple - on a set of given .pi files we perform typechecking.
 * Afterwards we choose randomly some variables and compare it with the result before. If it is the same - good. If
 * not - something bad happend. In case of variable shadowing we log the occurence. Furthermore we save the time it took to refactor.
 *
 */
class RenamingTest extends ASTNavigation with ConditionalNavigation with Logging {

  var filesAnalyzed = 0

  val runs = 3

  val nsToMs = 1000000

  val rename = "refactored_Variable"

  val busyBox_folderPath = "/Users/and1/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/"

  val prettyPrint_Output = "/Users/and1/Dropbox/Bachelorarbeit/Evaluation/Rename/"


  @Test def evaluate_random_ids_in_busybox() {
    logger.info("Started Test")
    analyseDir(new File(busyBox_folderPath))
    logger.info("Finished Test")
  }

  private def analyeDeclUse(map: IdentityHashMap[Id, List[Id]]): List[Int] = {
    val keys = map.keySet().toArray(Array[Id]())
    keys.map(key => map.get(key).length).toList
  }

  private def renameRandomIdInPiFile(piFile: File) {
    val tb = java.lang.management.ManagementFactory.getThreadMXBean
    val testStart = tb.getCurrentThreadCpuTime

    filesAnalyzed += 1
    val resultBuilder = new StringBuilder
    resultBuilder.append("++Analyse: " + piFile.getName + "++\n")


    val startParsing = tb.getCurrentThreadCpuTime
    val fis = new FileInputStream(piFile)
    val ast = parseFile(fis, piFile.getName, piFile.getParent)
    fis.close()
    resultBuilder.append("++Parsing time: " + (tb.getCurrentThreadCpuTime - startParsing) / nsToMs + "ms ++\n")

    val startTypeCheck = tb.getCurrentThreadCpuTime
    val morpheus = new Morpheus(ast, piFile)
    resultBuilder.append("++Typecheck .pi file time: " + (tb.getCurrentThreadCpuTime - startTypeCheck) / nsToMs + "ms ++\n")

    val originAmount = analyeDeclUse(morpheus.getDeclUseMap).sorted

    val ids = morpheus.getUseDeclMap.values().toArray(Array[List[Id]]()).par.foldLeft(List[Id]())((list, entry) => list ::: entry).toList
    for (i <- 0 to (runs - 1)) {
      val id = ids.apply((math.random * ids.size).toInt)

      resultBuilder.append("\n++Refactoring " + id + " " + id.range + " +++\n")
      if (morpheus.getDeclUseMap.containsKey(id)) resultBuilder.append("++DeclUseMap " + morpheus.getDeclUseMap.get(id) + " +++\n")
      if (morpheus.getUseDeclMap.containsKey(id)) resultBuilder.append("++DeclUseMap " + morpheus.getUseDeclMap.get(id) + " +++\n")

      resultBuilder.append("++Amount of ids: " + getAllRelevantIds(ast).length + " +++\n")
      resultBuilder.append("++Ids to rename: " + RenameIdentifier.getAllConnectedIdentifier(id, morpheus.getDeclUseMap, morpheus.getUseDeclMap).size + " +++\n")

      val startRenaming = tb.getCurrentThreadCpuTime
      val refactored = RenameIdentifier.rename(id, rename, morpheus)
      resultBuilder.append("++Renaming time: " + (tb.getCurrentThreadCpuTime - startRenaming) / nsToMs + "ms ++\n")

      val startTypeCheck2 = tb.getCurrentThreadCpuTime
      val morpheus2 = new Morpheus(refactored, piFile)
      resultBuilder.append("++Typecheck refactored ast time: " + (tb.getCurrentThreadCpuTime - startTypeCheck2) / nsToMs + "ms ++\n")

      val prettyPrint = tb.getCurrentThreadCpuTime
      val prettyPrinter = PrettyPrinter.print(refactored)
      resultBuilder.append("++Pretty printing (size:" + prettyPrinter.length + ") time: " + (tb.getCurrentThreadCpuTime - prettyPrint) / nsToMs + "ms ++\n")
      val file = new File(prettyPrint_Output + piFile.getName + "_" + i)
      file.createNewFile()
      val out_file = new java.io.FileOutputStream(file)
      val out_stream = new java.io.PrintStream(out_file)
      out_stream.print(prettyPrinter)
      out_stream.flush()
      out_file.flush()
      out_file.close()

      val newAmount = analyeDeclUse(morpheus2.getDeclUseMap).sorted
      val succ = originAmount == newAmount
      resultBuilder.append("++Refactoring was succesful: " + succ + " ++\n")
      assert(succ, "DeclUse is not the same anymore")
    }
    resultBuilder.append("\n++Finished: " + piFile.getName + " (" + (tb.getCurrentThreadCpuTime - testStart) / nsToMs + "ms) ++\n")
    logger.info(resultBuilder.toString())
    Thread.sleep(5000) // make a small pause before each run to keep system noise low @ bib
  }

  private def analyseDir(dirToAnalyse: File) {
    // retrieve all pi from dir first
    if (dirToAnalyse.isDirectory) {
      val piFiles = dirToAnalyse.listFiles(new FilenameFilter {
        def accept(dir: File, file: String): Boolean = file.endsWith(".pi")
      })
      val dirs = dirToAnalyse.listFiles(new FilenameFilter {
        def accept(dir: File, file: String) = dir.isDirectory
      })

      piFiles.foreach(piFile => renameRandomIdInPiFile(piFile))

      // make a small pause before each run to keep system noise low @ bib
      if (filesAnalyzed > 10) {
        logger.info("Going to sleep!")
        Thread.sleep(90000)
        filesAnalyzed = 0
      }

      dirs.foreach(dir => analyseDir(dir))
    }
  }

  private def parseFile(stream: InputStream, file: String, dir: String): TranslationUnit = {
    val ast: AST = new ParserMain(new CParser).parserMain(
      () => CLexer.lexStream(stream, file, Collections.emptyList(), null), new CTypeContext, SilentParserOptions)
    ast.asInstanceOf[TranslationUnit]
  }

  private def getAllRelevantIds(a: Any): List[Id] = {
    a match {
      case id: Id => if (!(id.name.startsWith("__builtin"))) List(id) else List()
      case gae: GnuAsmExpr => List()
      case l: List[_] => l.flatMap(x => getAllRelevantIds(x))
      case p: Product => p.productIterator.toList.flatMap(x => getAllRelevantIds(x))
      case k => List()
    }
  }
}
