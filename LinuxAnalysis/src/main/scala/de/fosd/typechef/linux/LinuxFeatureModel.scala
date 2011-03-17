/*
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 17.03.11
 * Time: 16:06
 */
package de.fosd.typechef.linux

import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureModel}

object LinuxFeatureModel {
    /**
     * full feature model, used for analysis and preparation
     */
    lazy val featureModel: FeatureModel = {
        println("loading feature model...");
        val start = System.currentTimeMillis
        val featureModel = FeatureModel.createFromDimacsFile_2Var(LinuxSettings.featureModelFile)
        println("done. [" + (System.currentTimeMillis - start) + " ms]")
        featureModel
    }


    /**
     * small feature model, used during parsing
     */
    val featureModelApprox = {
        import FeatureExpr._
        def d(f: String) = createDefinedExternal(f)

        FeatureModel.create(
            (d("CONFIG_SYMBOL_PREFIX").not)
                    and (d("CONFIG_FLATMEM") mex d("CONFIG_DISCONTIGMEM")) //from FM
                    and (d("CONFIG_FLATMEM") mex d("CONFIG_SPARSEMEM")) //not in FM!
                    and (d("CONFIG_DISCONTIGMEM") mex d("CONFIG_SPARSEMEM")) //not in FM!
                    and (d("CONFIG_DISCONTIGMEM") implies d("CONFIG_NEED_MULTIPLE_NODES")) //from FM
                    and (d("CONFIG_DISCONTIGMEM") implies d("CONFIG_SMP")) //from FM
                    and (d("CONFIG_DISCONTIGMEM") implies d("CONFIG_X86_PAE")) //from FM
                    and (d("CONFIG_MEMORY_HOTPLUG") implies d("CONFIG_SPARSEMEM")) //from FM
                    and (d("CONFIG_NEED_MULTIPLE_NODES") implies d("CONFIG_SMP")) //from FM
            //                    and (d("CONFIG_BUG") and (d("CONFIG_SMP") or d("CONFIG_DEBUG_SPINLOCK"))).not //parsing error
            //                    and (d("CONFIG_MEMORY_HOTPLUG") implies d("CONFIG_DEBUG_SPINLOCK")) //parsing error

        )
    }

}