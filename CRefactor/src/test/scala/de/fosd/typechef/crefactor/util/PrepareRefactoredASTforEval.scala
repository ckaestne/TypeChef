package de.fosd.typechef.crefactor.util

import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.featureexpr.{FeatureExprFactory, SingleFeatureExpr, FeatureModel, FeatureExpr}
import java.io.File

object PrepareRefactoredASTforEval extends EvalHelper {


    private def generateConfigsWithAffectedFeatures(enabledFeatures: List[SingleFeatureExpr], affectedFeatures: List[FeatureExpr], fm: FeatureModel): List[List[SingleFeatureExpr]] = {

        def generateConfig(affectedFeature: SingleFeatureExpr, enabledFeatures: List[SingleFeatureExpr], model: FeatureModel): List[SingleFeatureExpr] = {
            var found = false
            val config = enabledFeatures.foldLeft((List[SingleFeatureExpr](), FeatureExprFactory.True))((current, feature) => {
                if (feature.equals(affectedFeature)) {
                    found = true
                    current
                } else {
                    val currentConf = current._1.::(feature)
                    val expr = current._2.and(feature)
                    (currentConf, expr)
                }
            })
            if (config._2.isSatisfiable(fm)) config._1
            else List()
        }

        affectedFeatures.flatMap(expr => {
            val singleFeatures = expr.collectDistinctFeatureObjects.filterNot(ft => filterFeatures.contains(ft.feature))

            // default start config, it all starts from this config
            val startConfig = List(enabledFeatures)

            singleFeatures.foldLeft(startConfig)((configs, singleFt) =>
                configs.foldLeft(configs)((workingConfigs, config) => {
                    val current = generateConfig(singleFt, config, fm)
                    if (current.isEmpty) workingConfigs
                    else workingConfigs.::(current)
                }))
        })
    }

    def prepare(refactored: AST, fm: FeatureModel, originalFilePath: String, affectedFeatures: List[FeatureExpr], run: Int) {
        val dir = getResultDir(originalFilePath, run)
        val path = dir.getCanonicalPath + File.separatorChar + getFileName(originalFilePath)

        writeAST(refactored, path)

        val configRes = getClass.getResource("/busybox_Configs/")
        val configs = new File(configRes.getFile)

        val generatedConfigs = configs.listFiles().map(config => {
            val enabledFeatures = getEnabledFeaturesFromConfigFile(fm, config)
            (config, generateConfigsWithAffectedFeatures(enabledFeatures, affectedFeatures, fm))
        })

        println(generatedConfigs)

        generatedConfigs.foreach(genConfigs => {
            var configNumber = 0
            val name = genConfigs._1.getName
            genConfigs._2.foreach(genConfig => {
                writeConfig(genConfig, dir, configNumber + name)
                configNumber += 1
            })
        })
    }

}
