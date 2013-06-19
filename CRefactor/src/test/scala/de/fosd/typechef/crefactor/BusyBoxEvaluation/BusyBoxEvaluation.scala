package de.fosd.typechef.crefactor.BusyBoxEvaluation

import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.Frontend
import java.io.{FilenameFilter, File}
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureModel}
import org.junit.Test
import de.fosd.typechef.crefactor.util.EvalHelper


trait BusyBoxEvaluation extends EvalHelper {


    private val systemProperties: String = completeBusyBoxPath + "/redhat.properties"

    private val includeHeader: String = completeBusyBoxPath + "/busybox/config.h"
    private val includeDir: String = completeBusyBoxPath + "/busybox-1.18.5/include"
    private val featureModel: String = completeBusyBoxPath + "/busybox/featureModel"


    // private val typeChefArguments = List("-c", systemProperties, "-x", "CONFIG_", "--include", includeHeader, "-I", includeDir, "--featureModelFExpr", featureModel, "--debugInterface", "--recordTiming", "--parserstatistics", "-U", "HAVE_LIBDMALLOC", "-DCONFIG_FIND", "-U", "CONFIG_FEATURE_WGET_LONG_OPTIONS", "-U", "ENABLE_NC_110_COMPAT", "-U", "CONFIG_EXTRA_COMPAT ", "-D_GNU_SOURCE")
    @Test
    def evaluate()

    def performRefactor(fileToRefactor: File): Boolean

    def parse(file: File): (AST, FeatureModel) = {
        Frontend.main(getTypeChefArguments(file.getAbsolutePath))
        (Frontend.getAST, Frontend.getFeatureModel)
    }

    def getTypeChefArguments(file: String): Array[String] = {
        val pc = file.replace(".c", ".pc")
        Array(file, "-c", systemProperties, "-x", "CONFIG_", "--include", includeHeader, "-I", includeDir, "--featureModelFExpr", featureModel, "--partialConfiguration", pc, "--debugInterface", "--recordTiming", "--parserstatistics")
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


}


object RefactorVerification {

    def verify(refactored: File, originalFile: File, affectedFeatures: List[FeatureExpr], fm: FeatureModel) {

    }
}
