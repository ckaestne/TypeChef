package de.fosd.typechef.crefactor.BusyBoxEvaluation

import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.Frontend
import java.io.File
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureModel}
import org.junit.Test
import de.fosd.typechef.crefactor.util.EvalHelper


trait BusyBoxEvaluation extends EvalHelper {

    val amountOfRefactorings = 3
    private val systemProperties: String = completeBusyBoxPath + "/redhat.properties"
    private val includeHeader: String = completeBusyBoxPath + "/config.h"
    private val includeDir: String = completeBusyBoxPath + "/busybox-1.18.5/include"
    private val featureModel: String = completeBusyBoxPath + "/featureModel"


    @Test
    def evaluate()

    def performRefactor(fileToRefactor: File): Boolean

    def parse(file: File): (AST, FeatureModel) = {
        def getTypeChefArguments(file: String) = Array(file, "-c", systemProperties, "-x", "CONFIG_", "--include", includeHeader, "-I", includeDir, "--featureModelFExpr", featureModel, "--debugInterface", "--recordTiming", "--parserstatistics", "-U", "HAVE_LIBDMALLOC", "-DCONFIG_FIND", "-U", "CONFIG_FEATURE_WGET_LONG_OPTIONS", "-U", "ENABLE_NC_110_COMPAT", "-U", "CONFIG_EXTRA_COMPAT", "-D_GNU_SOURCE")
        Frontend.main(getTypeChefArguments(file.getAbsolutePath))
        (Frontend.getAST, Frontend.getFeatureModel)
    }

}


object RefactorVerification {

    def verify(refactored: File, originalFile: File, affectedFeatures: List[FeatureExpr], fm: FeatureModel) {

    }
}
