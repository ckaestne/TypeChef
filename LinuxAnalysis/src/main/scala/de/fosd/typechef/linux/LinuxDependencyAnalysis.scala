package de.fosd.typechef.linux

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.featureexpr.FeatureExpr._


/**
 * class used to run queries against the feature model
 *
 * (currently by hardcoding them in source code. for the future, potentially provide commandline parameters)
 */

object LinuxDependencyAnalysis {


    def main(args: Array[String]): Unit = {
        val featureNames = List(
            "CONFIG_MATOM", "CONFIG_GENERIC_CPU", "CONFIG_X86_ELAN"
        );
        val features = featureNames.map(FeatureExpr.createDefinedExternal(_))


        val fm = LinuxFeatureModel.featureModel

        println(LinuxFeatureModel.featureModelApprox)

        println(features)
        //        for (f1 <- features) {
        //            if (f1.isTautology(fm))
        //                println(f1 + " is tautology")
        //            if (f1.isContradiction(fm))
        //                println(f1 + " is contradiction")
        //        }
        for (f1 <- features; f2 <- features if f1 != f2) {
            if ((f1 implies f2).isTautology(fm)) {
                println(f1 + " => " + f2)
                println("""Add to LinuxFeatureModel.featureModelApprox: d("%s") implies d("%s")""".format(f1.feature, f2.feature))
            }
            if ((f1 mex f2).isTautology(fm))
                println(f1 + " mex " + f2)
            //            println(f1 + " boh " + f2)
        }


    }

    def testErrorConditions {
        def d(s: String) = createDefinedExternal(s)
        val c1 = (d("CONFIG_BUG") and (d("CONFIG_SMP") or d("CONFIG_DEBUG_SPINLOCK")))

        println(c1 + ": " + (c1.isSatisfiable))

    }
}
