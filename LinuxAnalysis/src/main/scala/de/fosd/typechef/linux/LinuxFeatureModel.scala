/*
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 17.03.11
 * Time: 16:06
 */
package de.fosd.typechef.linux

import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureModel}
import io.Source

/**
 * three feature models are available
 *
 * featureModel) the normal feature model (from dimacs)
 *
 * featureModelApprox) a small approximated feature model for parsing (contains only few dependencies). not accurate but fast
 *
 * featureModelFull) the normal feature model restricted by the partial configuration (i.e. certain features are already
 *    defined or excluded due to the partial configuration)
 *
 *
 * the approximated feature model is hardcoded in this file. the dimacs feature model and the partial configuration
 * are loaded according to the settings in LinuxSettings
 *
 */
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
                    and (d("CONFIG_BLK_DEV_DRBD") implies d("CONFIG_BLOCK")) //from FM
            //                    and (d("CONFIG_BUG") and (d("CONFIG_SMP") or d("CONFIG_DEBUG_SPINLOCK"))).not //parsing error
            //                    and (d("CONFIG_MEMORY_HOTPLUG") implies d("CONFIG_DEBUG_SPINLOCK")) //parsing error

        )
    }


    lazy val partialConfiguration: FeatureExpr = readPartialConfiguration

    lazy val featureModelFull = LinuxFeatureModel.featureModel and partialConfiguration


    private def readPartialConfiguration = {
        import FeatureExpr._
        val DEF = "#define"
        val UNDEF = "#undef"

        val directives = Source.fromFile(LinuxSettings.partialConfFile).getLines.filter(_.startsWith("#"))

        def findMacroName(directive: String) = directive.split(' ')(1)

        val booleanDefs = directives.filter(directive => directive.startsWith(DEF) && directive.endsWith(" 1")).map(findMacroName)
        val undefs = directives.filter(_.startsWith(UNDEF)).map(findMacroName)

        (booleanDefs.map(createDefinedExternal(_)) ++
                undefs.map(createDefinedExternal(_).not)).
                foldRight(base)(_ and _)
    }

}