package de.fosd.typechef.crefactor.busyBoxVerifacation

import java.io.{FileInputStream, File}
import de.fosd.typechef.crefactor.Morpheus
import de.fosd.typechef.parser.c.{PrettyPrinter, Id}
import de.fosd.typechef.crefactor.backend.refactor.RenameIdentifier

/**
 * Renaming verification class for the busybox toolsuite.
 */
class Rename extends BusyBoxVerification {

    private val OUTPUT = OUTPUT_PATH + "/renamed/"

    private val MAX_DEPTH = 100

    private val FORCE_VARIABILITY = true

    def performRefactor(fileToRefactor: File) {
        val testStart = currentTime
        logger.info("+++ Rename Verification on " + fileToRefactor.getName + " +++")

        val path = fileToRefactor.getCanonicalPath.replaceFirst(new File(busyBoxPath).getCanonicalPath, new File(OUTPUT).getCanonicalPath).replace(".pi", "")
        val fis = new FileInputStream(fileToRefactor)
        val parsingStartTime = currentTime
        val ast = parseFile(fis, fileToRefactor.getName, fileToRefactor.getParent)
        fis.close()
        val parsingTime = currentTime - parsingStartTime

        val typeCheckStartTime = currentTime
        val morpheus = new Morpheus(ast, fileToRefactor)
        val typeCheckTime = currentTime - typeCheckStartTime


        val ids = morpheus.getUseDeclMap.values().toArray(Array[List[Id]]()).par.foldLeft(List[Id]())((list, entry) => list ::: entry).toList

        def getVariableIdForRename(depth: Int = 0): (Id, Int, List[String]) = {
            val id = ids.apply((math.random * ids.size).toInt)

            val amountOfIds = RenameIdentifier.getAllConnectedIdentifier(id, morpheus.getDeclUseMap, morpheus.getUseDeclMap).length
            val features = RenameIdentifier.getAllConnectedIdentifier(id, morpheus.getDeclUseMap, morpheus.getUseDeclMap).map(x => morpheus.getASTEnv.featureExpr(x).toString)
            // check recursive only for variable ids
            if ((features.distinct.length == 1) && features.contains("True") && FORCE_VARIABILITY && (depth < MAX_DEPTH)) getVariableIdForRename(depth + 1)
            else (id, amountOfIds, features)
        }



        val toReanme = getVariableIdForRename()
        val id = toReanme._1
        val features = toReanme._3

        val startRenaming = currentTime
        val refactored = RenameIdentifier.rename(id, refactor_name, morpheus)
        val renamingTime = currentTime - startRenaming

        val morpheus2 = new Morpheus(refactored, fileToRefactor)

        val originAmount = analsyeDeclUse(morpheus.getDeclUseMap).sorted
        val newAmount = analsyeDeclUse(morpheus2.getDeclUseMap).sorted
        val succ = originAmount == newAmount

        // print out into file
        val prettyPrinted = PrettyPrinter.print(refactored)

        val dirPath = fileToRefactor.getCanonicalPath.replaceFirst(new File(busyBoxPath).getCanonicalPath, new File(OUTPUT).getCanonicalPath).replace(fileToRefactor.getName, "")
        new File(dirPath).mkdirs()

        val file = new File(path + ".c")
        file.createNewFile()
        writeFile(file, prettyPrinted)

        val featureFile = new File(path + ".features")
        featureFile.createNewFile()

        val featureString = features.distinct.foldLeft("")((r, f) => r + f + "\n")
        writeFile(featureFile, featureString)

        val builder = new StringBuilder
        builder.append("+++ Parsing Time: " + parsingTime + "ms\n")
        builder.append("+++ TypeCheck Time: " + typeCheckTime + "ms\n")
        builder.append("+++ Renaming Time: " + renamingTime + "ms\n")
        builder.append("+++ Renamed " + id)
        builder.append("+++ Renamed Ids: " + toReanme._2 + "\n")
        builder.append("+++ Refactoring was successful: " + succ)

        val statsFile = new File(path + ".stats")
        statsFile.createNewFile()
        writeFile(statsFile, builder.toString())


        refactoredFiles += 1

        val runtime = currentTime - testStart
        logger.info("+++ Finished Refactoring on " + fileToRefactor.getName + " in " + runtime + "ms +++")
        logger.info("+++ Refactoring was successful: " + succ + " +++")
    }
}
