package de.fosd.typechef

import de.fosd.typechef.parser.c._
import lexer.FeatureExprLib
import org.junit.Test
import java.io.{FileNotFoundException, InputStream}
import de.fosd.typechef.options.FeatureModelOptions

class coverageGenerationTest extends TestHelper {
    val folder = "testfiles/"

    @Test def testNoVariabilityBDD() {
        de.fosd.typechef.featureexpr.FeatureExprFactory.setDefault(de.fosd.typechef.featureexpr.FeatureExprFactory.bdd)
        testNoVariability()
    }
    @Test def testNoVariability() {
        println("analysis " + "test_switch_case_default.c")
        val inputStream: InputStream = getClass.getResourceAsStream("/" + folder + "test_switch_case_default.c")

        if (inputStream == null)
            throw new FileNotFoundException("Input file not found: " + "test_switch_case_default.c")


        val ast:TranslationUnit = parseFile(inputStream, "test_switch_case_default.c", folder)

        FamilyBasedVsSampleBased.initializeFeatureList(ast)
        val features = FamilyBasedVsSampleBased.getAllFeatures(ast)
        val (configs, log) = FamilyBasedVsSampleBased.configurationCoverage(ast, FeatureExprLib.featureModelFactory.empty, features, List(), preferDisabledFeatures = true)
        assert(log.contains("found 1 NodeExpressions"))
        assert(log.contains("found 0 simpleAndNodes, 0 simpleOrNodes and 0 complex nodes"))
        assert(configs.size==1)
    }
    @Test def testVariabilityBDD() {
        de.fosd.typechef.featureexpr.FeatureExprFactory.setDefault(de.fosd.typechef.featureexpr.FeatureExprFactory.bdd)
        testVariability()
    }
    @Test def testVariability() {
        println("analysis " + "test.c")
        val inputStream: InputStream = getClass.getResourceAsStream("/" + folder + "test.c")

        if (inputStream == null)
            throw new FileNotFoundException("Input file not found: " + "test.c")

        val ast:TranslationUnit = parseFile(inputStream, "test.c", folder)

        FamilyBasedVsSampleBased.initializeFeatureList(ast)
        val features = FamilyBasedVsSampleBased.getAllFeatures(ast)
        val (configs, _) = FamilyBasedVsSampleBased.configurationCoverage(ast, FeatureExprLib.featureModelFactory.empty, features, List(), preferDisabledFeatures = true)
        val featureA = features.find(_.feature.equals("CONFIG_A")).get
        assert(configs.size==2)
        assert(configs.find(_.containsAllFeaturesAsEnabled(Set(featureA))).isDefined, "Did not find configuration with CONFIG_A enabled")
        assert(configs.find(_.containsAllFeaturesAsDisabled(Set(featureA))).isDefined, "Did not find configuration with CONFIG_A disabled")
    }
}
