package de.fosd.typechef.linux

/*
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 14.02.11
 * Time: 14:57
 */

import de.fosd.typechef.featureexpr.FeatureExpr._
import java.io._

/**
 * completes a partial configuration
 *
 * output path a parameter, writes completedConf.h there
 *
 * assumes partialConf.h and feature model as input (LinuxSettings)
 *
 */
object CompletePartialConfiguration {
    def main(args: Array[String]) {
        val outPath = args(0)
        new File(outPath).mkdir()


        val featureModel = LinuxFeatureModel.featureModelFull

        val completedConf = new FileWriter(outPath + File.separator + "completedConf.h")
        val openFeatures = new FileWriter(outPath + File.separator + "openFeaturesList.txt")

        for (feature <- featureModel.variables.keys if (!feature.startsWith("CONFIG__X") && !feature.endsWith("_2"))) {
            print("Testing feature: " + feature + "...")
            val start = System.currentTimeMillis
            val featureMandatory = createDefinedExternal(feature).isTautology(featureModel)
            val featureDead = !featureMandatory && createDefinedExternal(feature).not.isTautology(featureModel)
            val end = System.currentTimeMillis
            println("time " + (end - start))

            if (featureMandatory) {
                println("#define " + feature)
                completedConf.write("#define " + feature + "\n")
                completedConf.flush
            } else if (featureDead) {
                println("#undef " + feature)
                completedConf.write("#undef " + feature + "\n")
                completedConf.flush
            } else {
                println("Open feature: " + feature)
                openFeatures.write(feature + "\n")
                openFeatures.flush
            }
            // println(feature + ": "+featureMandatory+" "+featureDead )
        }

        print("done.\n\n")
        completedConf.close
        openFeatures.close
    }


    //    def d(n: String) = createDefinedExternal(n)
    //    def nd(n: String) = createDefinedExternal(n).not
    //
    //    val formulas = List(
    //        d("CONFIG_DISCONTIGMEM") and nd("CONFIG_NEED_MULTIPLE_NODES"),
    //        (d("CONFIG_DISCONTIGMEM") and d("CONFIG_NEED_MULTIPLE_NODES")),
    //        (d("CONFIG_DISCONTIGMEM") and d("CONFIG_NEED_MULTIPLE_NODES")).not,
    //        ((nd("CONFIG_DISCONTIGMEM") or nd("CONFIG_NEED_MULTIPLE_NODES")) and ((d("CONFIG_SPARSEMEM_EXTREME") and d("CONFIG_SPARSEMEM")) or (nd("CONFIG_SPARSEMEM_EXTREME") and d("CONFIG_SPARSEMEM")))),
    //        (((nd("CONFIG_GENERIC_LOCKBREAK") or d("CONFIG_DEBUG_LOCK_ALLOC")) and nd("CONFIG_LOCK_STAT") and d("CONFIG_LOCKDEP") and ((nd("CONFIG_DEBUG_SPINLOCK") and nd("CONFIG_SMP")) or (d("CONFIG_PROVE_LOCKING") and d("CONFIG_DEBUG_LOCK_ALLOC") and (d("CONFIG_PROVE_LOCKING") or nd("CONFIG_DEBUG_LOCK_ALLOC"))) or d("CONFIG_LOCKDEP") or d("CONFIG_PROVE_LOCKING") or (d("CONFIG_GENERIC_LOCKBREAK") and nd("CONFIG_DEBUG_LOCK_ALLOC")) or nd("CONFIG_DEBUG_LOCK_ALLOC") or ((nd("CONFIG_GENERIC_LOCKBREAK") or d("CONFIG_DEBUG_LOCK_ALLOC")) and d("CONFIG_DEBUG_LOCK_ALLOC") and d("CONFIG_LOCKDEP") and d("CONFIG_PROVE_LOCKING") and (d("CONFIG_DEBUG_SPINLOCK") or d("CONFIG_SMP")) and (d("CONFIG_PROVE_LOCKING") or nd("CONFIG_DEBUG_LOCK_ALLOC")))) and (d("CONFIG_DEBUG_SPINLOCK") or d("CONFIG_SMP")) and ((d("CONFIG_GENERIC_LOCKBREAK") and nd("CONFIG_DEBUG_LOCK_ALLOC")) or (nd("CONFIG_DEBUG_SPINLOCK") and nd("CONFIG_SMP")) or nd("CONFIG_LOCKDEP") or nd("CONFIG_LOCK_STAT")) and ((nd("CONFIG_DEBUG_SPINLOCK") and nd("CONFIG_SMP")) or nd("CONFIG_PROVE_LOCKING") or d("CONFIG_LOCKDEP") or (d("CONFIG_GENERIC_LOCKBREAK") and nd("CONFIG_DEBUG_LOCK_ALLOC")) or (nd("CONFIG_PROVE_LOCKING") and d("CONFIG_DEBUG_LOCK_ALLOC")) or nd("CONFIG_DEBUG_LOCK_ALLOC")))
    //                ).implies((d("CONFIG_DEBUG_SPINLOCK") or nd("CONFIG_SMP")) and (d("CONFIG_DEBUG_SPINLOCK") or d("CONFIG_SMP"))),
    //        (((nd("CONFIG_GENERIC_LOCKBREAK") or d("CONFIG_DEBUG_LOCK_ALLOC")) and d("CONFIG_LOCKDEP") and ((nd("CONFIG_DEBUG_SPINLOCK") and nd("CONFIG_SMP")) or (d("CONFIG_PROVE_LOCKING") and d("CONFIG_DEBUG_LOCK_ALLOC") and (d("CONFIG_PROVE_LOCKING") or nd("CONFIG_DEBUG_LOCK_ALLOC"))) or d("CONFIG_LOCKDEP") or d("CONFIG_PROVE_LOCKING") or (d("CONFIG_GENERIC_LOCKBREAK") and nd("CONFIG_DEBUG_LOCK_ALLOC")) or nd("CONFIG_DEBUG_LOCK_ALLOC") or ((nd("CONFIG_GENERIC_LOCKBREAK") or d("CONFIG_DEBUG_LOCK_ALLOC")) and d("CONFIG_DEBUG_LOCK_ALLOC") and d("CONFIG_LOCKDEP") and d("CONFIG_PROVE_LOCKING") and (d("CONFIG_DEBUG_SPINLOCK") or d("CONFIG_SMP")) and (d("CONFIG_PROVE_LOCKING") or nd("CONFIG_DEBUG_LOCK_ALLOC")))) and (d("CONFIG_DEBUG_SPINLOCK") or d("CONFIG_SMP")) and d("CONFIG_LOCK_STAT") and ((nd("CONFIG_DEBUG_SPINLOCK") and nd("CONFIG_SMP")) or nd("CONFIG_PROVE_LOCKING") or d("CONFIG_LOCKDEP") or (d("CONFIG_GENERIC_LOCKBREAK") and nd("CONFIG_DEBUG_LOCK_ALLOC")) or (nd("CONFIG_PROVE_LOCKING") and d("CONFIG_DEBUG_LOCK_ALLOC")) or nd("CONFIG_DEBUG_LOCK_ALLOC")))).implies((d("CONFIG_DEBUG_SPINLOCK") or nd("CONFIG_SMP")) and (d("CONFIG_DEBUG_SPINLOCK") or d("CONFIG_SMP")))
    //
    //    )
    //
    //
    //    for (form <- formulas) {
    //        println(
    //            (if (form.isTautology(featureModel)) "tautology"
    //            else
    //            if (form.isContradiction(featureModel)) "contradiction" else "satisfiable")
    //                    + ": " +
    //                    form)
    //    }


}

// vim: set sw=4:
