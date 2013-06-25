package de.fosd.typechef.crefactor.BusyBoxEvaluation

import java.io._
import de.fosd.typechef.featureexpr.FeatureModel
import org.junit.Test
import de.fosd.typechef.crefactor.util.EvalHelper


trait BusyBoxEvaluation extends EvalHelper {

    val FORCE_VARIABILITY = true
    val MAX_DEPTH = 27

    val amountOfRefactorings = 3

    @Test
    def evaluate()
}


object RefactorVerification extends EvalHelper {

    def copyFile(file1: File, file2: File) = new FileOutputStream(file2) getChannel() transferFrom(new FileInputStream(file1) getChannel, 0, Long.MaxValue)

    def verify(bbFile: File, run: Int, fm: FeatureModel): Boolean = {
        val verfiyPath = bbFile.getCanonicalPath
        val orgFile = new File(bbFile.getCanonicalPath.replaceAll("busybox-1.18.5", "busybox-1.18.5_untouched"))
        val refFile = new File(bbFile.getCanonicalPath.replaceAll("busybox-1.18.5", "result") + "/" + run + "/" + bbFile.getName)
        val verfiyDir = new File(bbFile.getCanonicalPath.replaceAll("busybox-1.18.5", "result") + "/" + run + "/")

        val configs = verfiyDir.listFiles(new FilenameFilter {
            def accept(input: File, file: String): Boolean = file.endsWith(".config")
        })

        configs.forall(config => {
            val configBuild = new File(busyBoxPath + ".config")
            copyFile(config, configBuild)

            val orgBuild = buildBusyBox
            val org = runTest
            writeResult(orgBuild, verfiyDir.getCanonicalPath + "/" + config.getName + "_org" + ".build")
            writeResult(org, verfiyDir.getCanonicalPath + "/" + config.getName + "_org" + ".test")
            bbFile.delete()

            val buildRefFile = new File(verfiyPath)
            copyFile(refFile, buildRefFile)

            val refBuild = buildBusyBox
            val ref = runTest
            writeResult(refBuild, verfiyDir.getCanonicalPath + "/" + config.getName + "_ref" + ".build")
            writeResult(ref, verfiyDir.getCanonicalPath + "/" + config.getName + "_ref" + ".test")
            buildRefFile.delete()
            copyFile(orgFile, new File(verfiyPath))

            configBuild.delete()
            println("Result " + org.equals(ref))
            org.equals(ref)
        })
    }

    def writeResult(result: String, file: String) = {
        var out: FileWriter = null
        if (file.startsWith(".")) out = new java.io.FileWriter(file.replaceFirst(".", ""))
        else out = new java.io.FileWriter(file)

        out.write(result)
        out.flush()
        out.close()
    }

    def runTest: String = {
        var error = false
        val pb = new ProcessBuilder("./runtest")
        pb.directory(new File(busyBoxPath + "testsuite/"))
        val p = pb.start()
        p.waitFor()

        val reader = new BufferedReader(new InputStreamReader(p.getInputStream()))
        val sb = new StringBuilder
        while (reader.ready()) {
            val line = reader.readLine()
            sb.append(line)
            println(line)
        }

        val reader2 = new BufferedReader(new InputStreamReader(p.getErrorStream()))
        while (reader2.ready()) {
            error = true
            val line = reader2.readLine()
            sb.append(line)
            println(line)
        }
        sb.toString()
    }

    def buildBusyBox: String = {
        var error = false
        val pb = new ProcessBuilder("./buildBusyBox.sh")
        pb.directory(new File(busyBoxPath))
        val p = pb.start()
        p.waitFor()

        val reader = new BufferedReader(new InputStreamReader(p.getInputStream()))
        val sb = new StringBuilder
        while (reader.ready()) {
            val line = reader.readLine()
            sb.append(line)
            println(line)
        }

        val reader2 = new BufferedReader(new InputStreamReader(p.getErrorStream()))
        while (reader2.ready()) {
            error = true
            val line = reader2.readLine()
            sb.append(line)
            println(line)
        }
        sb.toString()
    }
}
