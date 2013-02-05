package de.fosd.typechef.crefactor

import backend.refactor.RenameIdentifier
import org.junit.Test
import java.io.{InputStream, FileInputStream, FilenameFilter, File}
import de.fosd.typechef.parser.c._
import de.fosd.typechef.parser.c.TranslationUnit
import de.fosd.typechef.parser.c.CTypeContext
import java.util.IdentityHashMap
import de.fosd.typechef.crewrite.{ConditionalNavigation, ASTNavigation}


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
class RenamingTest extends ASTNavigation with ConditionalNavigation {

  val rename = "refactored_Variable"

  val busyBox_folderPath = "/Users/and1/Dropbox/HiWi/busybox/TypeChef-BusyboxAnalysis/busybox-1.18.5/"


  @Test def evaluate_random_ids_in_busybox() {
    analyseDir(new File(busyBox_folderPath))
  }

  private def analyeDeclUse(map: IdentityHashMap[Id, List[Id]]): List[Int] = {
    val keys = map.keySet().toArray(Array[Id]())
    keys.map(key => map.get(key).length).toList
  }

  private def renameRandomIdInPiFile(piFile: File) {
    val resultBuilder = new StringBuilder
    resultBuilder.append("++Analyse: " + piFile.getName + "++\n")

    val startParsing = System.currentTimeMillis()
    val fis = new FileInputStream(piFile)
    val ast = parseFile(fis, piFile.getName, piFile.getParent)
    fis.close()
    resultBuilder.append("++Parsing time: " + (System.currentTimeMillis() - startParsing) + "ms ++\n")

    val startTypeCheck = System.currentTimeMillis()
    val morpheus = new Morpheus(ast, piFile)
    resultBuilder.append("++Typecheck .pi file time: " + (System.currentTimeMillis() - startTypeCheck) + "ms ++\n")

    val originAmount = analyeDeclUse(morpheus.getDeclUseMap()).sorted

    val ids = morpheus.getUseDeclMap.values().toArray(Array[List[Id]]()).par.foldLeft(List[Id]())((list, entry) => list ::: entry).toList
    val id = ids.apply((math.random * ids.size).toInt)

    resultBuilder.append("++Refactoring " + id + " " + id.range + " +++\n")
    if (morpheus.getDeclUseMap().containsKey(id)) resultBuilder.append("++DeclUseMap " + morpheus.getDeclUseMap().get(id) + " +++\n")
    if (morpheus.getUseDeclMap.containsKey(id)) resultBuilder.append("++DeclUseMap " + morpheus.getUseDeclMap.get(id) + " +++\n")

    resultBuilder.append("++Amount of ids: " + getAllRelevantIds(ast).length + " +++\n")
    resultBuilder.append("++Ids to rename: " + RenameIdentifier.getAllConnectedIdentifier(id, morpheus.getDeclUseMap, morpheus.getUseDeclMap).size + " +++\n")

    val startRenaming = System.currentTimeMillis()
    val refactored = RenameIdentifier.rename(id, rename, morpheus)
    resultBuilder.append("++Renaming time: " + (System.currentTimeMillis() - startRenaming) + "ms ++\n")

    val startTypeCheck2 = System.currentTimeMillis()
    val morpheus2 = new Morpheus(refactored, piFile)
    resultBuilder.append("++Typecheck refactored ast time: " + (System.currentTimeMillis() - startTypeCheck2) + "ms ++\n")

    /**
    val prettyPrint = System.currentTimeMillis()
    val prettyPrinter = PrettyPrinter.print(refactored)
    resultBuilder.append("++Pretty printing (size:" + prettyPrinter.length + ") time: " + (System.currentTimeMillis() - prettyPrint) + "ms ++\n")
      */


    val newAmount = analyeDeclUse(morpheus2.getDeclUseMap()).sorted
    val succ = originAmount == newAmount
    resultBuilder.append("++Refactoring was succesful: " + succ + " ++\n")
    println(resultBuilder.toString())
    assert(succ, "DeclUse is not the same anymore")
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

      dirs.foreach(dir => analyseDir(dir))
    }
  }

  private def parseFile(stream: InputStream, file: String, dir: String): TranslationUnit = {
    val ast: AST = new ParserMain(new CParser).parserMain(
      () => CLexer.lexStream(stream, file, dir, null), new CTypeContext, SilentParserOptions)
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
