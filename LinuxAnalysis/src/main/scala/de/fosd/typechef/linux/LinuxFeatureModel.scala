/*
  * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 17.03.11
 * Time: 16:06
 */
package de.fosd.typechef.linux

import io.Source
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureModel}

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

    lazy val featureModelExcludingDead: FeatureModel = {
        val fm = featureModel
        fm and getDeadFeatures(fm) and partialConfiguration
    }


    /**
     * small feature model, used during parsing
     */
    val featureModelApprox = {
        import FeatureExpr._
        def d(f: String) = createDefinedExternal(f)

        FeatureModel.create(
            (d("CONFIG_SYMBOL_PREFIX").not)
                    and oneOf(List("CONFIG_FLATMEM", "CONFIG_DISCONTIGMEM", "CONFIG_SPARSEMEM")) //not all in FM!
                    and (d("CONFIG_DISCONTIGMEM") implies d("CONFIG_NEED_MULTIPLE_NODES")) //from FM
                    and (d("CONFIG_DISCONTIGMEM") implies d("CONFIG_SMP")) //from FM
                    and (d("CONFIG_DISCONTIGMEM") implies d("CONFIG_X86_PAE")) //from FM
                    and (d("CONFIG_MEMORY_HOTPLUG") implies d("CONFIG_SPARSEMEM")) //from FM
                    and (d("CONFIG_HOTPLUG_CPU") implies d("CONFIG_SMP")) //from FM
                    and (d("CONFIG_PROC_KCORE") implies d("CONFIG_PROC_FS")) //from FM
                    and (d("CONFIG_NEED_MULTIPLE_NODES") implies d("CONFIG_SMP")) //from FM
                    and (d("CONFIG_BLK_DEV_DRBD") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_BLK_DEV_INTEGRITY") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_BLK_DEV_LOOP") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_BLK_DEV_RAM") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_BLK_DEV_IO_TRACE") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_EXT3_FS") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_EXT4_FS") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_JBD") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_JBD2") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_DM_LOG_USERSPACE") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_DM_MULTIPATH_ST") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_MD_FAULTY") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_MD_LINEAR") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_BLK_DEV_MD") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_MD_MULTIPATH") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_MD_RAID0") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_MD_RAID10") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_MD_RAID1") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_MD_RAID456") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_NFTL") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_NILFS2_FS") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_OCFS2_FS") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_EXOFS_FS") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_BOUNCE") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_I2C_SCMI") implies d("CONFIG_ACPI")) //from FM
                    and (d("CONFIG_MD") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_GFS2_FS") implies d("CONFIG_BLOCK")) //from FM
                    and (d("CONFIG_MTD_UBI_DEBUG_PARANOID") implies d("CONFIG_MTD_UBI_DEBUG")) //from FM
                    and (d("CONFIG_GENERIC_PENDING_IRQ") implies d("CONFIG_SMP")) //from FM
                    and (d("CONFIG_PROVE_LOCKING") implies d("CONFIG_DEBUG_SPINLOCK")) //from FM
                    and (d("CONFIG_NET_EMATCH_U32") implies d("CONFIG_NET_EMATCH")) //from FM
                    and (d("CONFIG_NET_EMATCH_CMP") implies d("CONFIG_NET_EMATCH")) //from FM
                    and (d("CONFIG_NET_EMATCH_META") implies d("CONFIG_NET_EMATCH")) //from FM
                    and (d("CONFIG_NET_EMATCH_NBYTE") implies d("CONFIG_NET_EMATCH")) //from FM
                    and (d("CONFIG_NET_EMATCH_TEXT") implies d("CONFIG_NET_EMATCH")) //from FM
                    and (d("CONFIG_X86_USE_3DNOW") mex d("CONFIG_KMEMCHECK")) //from FM
                    and (d("CONFIG_TOUCHSCREEN_AD7879") equiv (d("CONFIG_TOUCHSCREEN_AD7879_I2C") or d("CONFIG_TOUCHSCREEN_AD7879_SPI"))) //from FM
                    and (d("CONFIG_NET_EMATCH_CMP") implies d("CONFIG_NET_EMATCH")) //from FM
                    and (d("CONFIG_NET_EMATCH_NBYTE") implies d("CONFIG_NET_EMATCH")) //from FM
                    and (d("CONFIG_NET_EMATCH_TEXT") implies d("CONFIG_NET_EMATCH")) //from FM
                    and (d("CONFIG_NET_EMATCH_U32") implies d("CONFIG_NET_EMATCH")) //from FM
                    and (d("CONFIG_DEBUG_LOCK_ALLOC") implies d("CONFIG_LOCKDEP")) //from FM
                    and (d("CONFIG_IRQSOFF_TRACER") implies d("CONFIG_TRACE_IRQFLAGS")) //from FM
                    and atLeastOne(List(/*"CONFIG_M386", "CONFIG_M486",*/ "CONFIG_M586", "CONFIG_M586TSC", "CONFIG_M586MMX",
                "CONFIG_M686", "CONFIG_MPENTIUMII", "CONFIG_MPENTIUMIII", "CONFIG_MPENTIUMM", "CONFIG_MPENTIUM4",
                "CONFIG_MK6", "CONFIG_MK7", "CONFIG_MK8", "CONFIG_MCRUSOE", "CONFIG_MEFFICEON", "CONFIG_MWINCHIPC6",
                "CONFIG_MWINCHIP3D", "CONFIG_MGEODEGX1", "CONFIG_MGEODE_LX", "CONFIG_MCYRIXIII", "CONFIG_MVIAC3_2",
                "CONFIG_MVIAC7", "CONFIG_MCORE2", "CONFIG_MATOM"))
                    and (d("CONFIG_HUGETLBFS") equiv d("CONFIG_HUGETLB_PAGE")) //from FM
        )
    }

    /**
     * creates (A or B or ... or X) and (A mex B) and (A mex X) and (B mex X) ...
     */
    def oneOf(featuresNames: List[String]): FeatureExpr = {
        val features = featuresNames.map(FeatureExpr.createDefinedExternal(_))
        features.foldLeft(FeatureExpr.dead)(_ or _) and (
                (for (f1 <- features; f2 <- features; if (f1.feature.compareTo(f2.feature)) > 0) yield f1 mex f2).
                        foldLeft(FeatureExpr.base)(_ and _)
                )
    }
    def atLeastOne(featuresNames: List[String]): FeatureExpr =
        featuresNames.map(FeatureExpr.createDefinedExternal(_)).foldLeft(FeatureExpr.dead)(_ or _)

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

    /**
     * reads the list of open features (LinuxSettings) and makes every
     * feature dead that is not in that list
     */
    private def getDeadFeatures(fm: FeatureModel): FeatureExpr = {
        import FeatureExpr._
        var result: FeatureExpr = base
        val openFeatures = Source.fromFile(LinuxSettings.openFeatureList).getLines.toList
        for (feature <- fm.variables.keys if (feature.startsWith("CONFIG_") && !feature.startsWith("CONFIG__X") && !feature.endsWith("_2")))
            if (!openFeatures.contains(feature))
                result = result andNot createDefinedExternal(feature)
        result
    }

}
