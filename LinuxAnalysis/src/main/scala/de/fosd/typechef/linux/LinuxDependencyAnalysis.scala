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
            //            "CONFIG_MEMORY_HOTPLUG", "CONFIG_DEBUG_SPINLOCK", "CONFIG_BUG",
            //            "CONFIG_SMP", "CONFIG_DEBUG_SPINLOCK",
            //            "CONFIG_NEED_MULTIPLE_NODES",
            //            "CONFIG_DISCONTIGMEM", "CONFIG_FLATMEM", "CONFIG_SPARSEMEM",
            //            "CONFIG_X86_PAE",
            //            "CONFIG_X86_IO_APIC", "CONFIG_ACPI",
            //                        "CONFIG_BLOCK","CONFIG_EXT3_FS","CONFIG_EXT4_FS"    ,"CONFIG_JBD" ,"CONFIG_JBD2"
            //        "CONFIG_IA32_EMULATION","CONFIG_64BIT",
            //"CONFIG_X86_32" ,
            //"CONFIG_X86_64",
            //"CONFIG_X86"  ,
            "CONFIG_PARAVIRT", "CONFIG_PROC_FS", "CONFIG_TRACE_IRQFLAGS_SUPPORT", "CONFIG_X86_PAE"
            // "CONFIG_MD", "CONFIG_GFS2_FS", "CONFIG_BLOCK"
            // "CONFIG_BLK_DEV_DRBD","CONFIG_BLK_DEV_INTEGRITY", "CONFIG_BLK_DEV_RAM" , "CONFIG_BLK_DEV_LOOP"
            //            "CONFIG_AMIGA", "CONFIG_X86", "CONFIG_M32R", "CONFIG_SPARC", "CONFIG_M68K", "CONFIG_AMIGA_FLOPPY"
            //            "CONFIG_BLOCK", "CONFIG_PS3_DISK", "CONFIG_PPC_PS3"
            //        "CONFIG_USB", "CONFIG_USB_LIBUSUAL"
        );
        val features = featureNames.map(FeatureExpr.createDefinedExternal(_))


        val fm = LinuxFeatureModel.featureModel

        println(features)
        for (f1 <- features) {
            if (f1.isTautology(fm))
                println(f1 + " is tautology")
            if (f1.isContradiction(fm))
                println(f1 + " is contradiction")
        }
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
