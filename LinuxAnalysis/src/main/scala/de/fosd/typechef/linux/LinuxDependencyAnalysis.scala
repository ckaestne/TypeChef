package de.fosd.typechef.linux

import de.fosd.typechef.featureexpr._
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
            "CONFIG_IRQSOFF_TRACER", "CONFIG_TRACE_IRQFLAGS"
        );
        val d: String => DefinedExternal = FeatureExpr.createDefinedExternal(_)
        val features = featureNames.map(d(_))


        val fm = LinuxFeatureModel.featureModel

        //        println(LinuxFeatureModel.featureModelApprox)
        //        val v1 = d("CONFIG_PARAVIRT")
        //        //Succeeding config.
        //        val v2 = d("CONFIG_X86_USE_3DNOW")
        //        val v3 = d("CONFIG_PARAVIRT")
        //        println("Uh? " + (v1 | v2 | v3).isTautology)
        //        val v1_fromSrc = ((!d("CONFIG_PARAVIRT") | d("CONFIG_X86_USE_3DNOW") | !d("CONFIG_KMEMCHECK")) & (d("CONFIG_X86_USE_3DNOW") | d("CONFIG_KMEMCHECK") | d("CONFIG_PARAVIRT")) & d("CONFIG_PARAVIRT") & d("CONFIG_X86_USE_3DNOW") & (!d("CONFIG_PARAVIRT") | d("CONFIG_X86_USE_3DNOW") | d("CONFIG_KMEMCHECK")) & (d("CONFIG_X86_USE_3DNOW") | d("CONFIG_PARAVIRT") | !d("CONFIG_KMEMCHECK"))) | (d("CONFIG_X86_USE_3DNOW") & !d("CONFIG_PARAVIRT"))
        //        val v2_fromSrc = (d("CONFIG_X86_USE_3DNOW") | d("CONFIG_PARAVIRT") | !d("CONFIG_KMEMCHECK")) & ((d("CONFIG_PARAVIRT") & !d("CONFIG_X86_USE_3DNOW") & !d("CONFIG_KMEMCHECK") & (!d("CONFIG_PARAVIRT") | d("CONFIG_X86_USE_3DNOW") | !d("CONFIG_KMEMCHECK"))) | (!d("CONFIG_X86_USE_3DNOW") & !d("CONFIG_KMEMCHECK") & !d("CONFIG_PARAVIRT"))) & (d("CONFIG_PARAVIRT") | !d("CONFIG_X86_USE_3DNOW")) & ((d("CONFIG_PARAVIRT") & !d("CONFIG_X86_USE_3DNOW") & !d("CONFIG_KMEMCHECK")) | (!d("CONFIG_X86_USE_3DNOW") & d("CONFIG_KMEMCHECK")) | !d("CONFIG_X86_USE_3DNOW") | (!d("CONFIG_X86_USE_3DNOW") & !d("CONFIG_PARAVIRT") & d("CONFIG_KMEMCHECK")) | !d("CONFIG_PARAVIRT") | (!d("CONFIG_X86_USE_3DNOW") & !d("CONFIG_KMEMCHECK") & !d("CONFIG_PARAVIRT")))
        //        val v3_fromSrc = ((d("CONFIG_PARAVIRT") | !d("CONFIG_X86_USE_3DNOW")) & ((d("CONFIG_PARAVIRT") & !d("CONFIG_X86_USE_3DNOW") & d("CONFIG_KMEMCHECK") & (d("CONFIG_X86_USE_3DNOW") | d("CONFIG_KMEMCHECK") | d("CONFIG_PARAVIRT"))) | (!d("CONFIG_X86_USE_3DNOW") & !d("CONFIG_PARAVIRT") & d("CONFIG_KMEMCHECK"))) & ((d("CONFIG_PARAVIRT") & !d("CONFIG_X86_USE_3DNOW") & !d("CONFIG_KMEMCHECK")) | (!d("CONFIG_X86_USE_3DNOW") & d("CONFIG_KMEMCHECK")) | !d("CONFIG_X86_USE_3DNOW") | (!d("CONFIG_X86_USE_3DNOW") & !d("CONFIG_PARAVIRT") & d("CONFIG_KMEMCHECK")) | !d("CONFIG_PARAVIRT") | (!d("CONFIG_X86_USE_3DNOW") & !d("CONFIG_KMEMCHECK") & !d("CONFIG_PARAVIRT"))) & ((!d("CONFIG_X86_USE_3DNOW") & !d("CONFIG_PARAVIRT") & d("CONFIG_KMEMCHECK")) | ((d("CONFIG_X86_USE_3DNOW") | d("CONFIG_KMEMCHECK") | d("CONFIG_PARAVIRT")) & (!d("CONFIG_PARAVIRT") | d("CONFIG_X86_USE_3DNOW") | d("CONFIG_KMEMCHECK") | (d("CONFIG_PARAVIRT") & !d("CONFIG_X86_USE_3DNOW") & d("CONFIG_KMEMCHECK")))) | (d("CONFIG_X86_USE_3DNOW") & !d("CONFIG_PARAVIRT"))))
        //        println("Uh==? " + (v3 == v3_fromSrc))
        //        println("Mixed1Tauto? " + (v1 | v2 | v3_fromSrc).isTautology)
        //        println("Mixed2Tauto? " + (v1 | v2_fromSrc | v3_fromSrc).isTautology)
        //        println("FromSrcTauto? " + (v1_fromSrc | v2_fromSrc | v3_fromSrc).isTautology)
        //println("""d("CONFIG_TOUCHSCREEN_AD7879") implies (d("CONFIG_TOUCHSCREEN_AD7879_I2C") or d("CONFIG_TOUCHSCREEN_AD7879_SPI")): """ +
        //(d("CONFIG_TOUCHSCREEN_AD7879") implies (d("CONFIG_TOUCHSCREEN_AD7879_I2C") or d("CONFIG_TOUCHSCREEN_AD7879_SPI"))).isTautology(fm))

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
