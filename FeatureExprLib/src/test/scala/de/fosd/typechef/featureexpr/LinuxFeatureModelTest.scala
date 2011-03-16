package de.fosd.typechef.featureexpr

/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 02.01.11
 * Time: 15:05
 * To change this template use File | Settings | File Templates.
 */

import org.junit._
import org.junit.Assert._
import org.sat4j.minisat.SolverFactory
import junit.framework.TestCase
import de.fosd.typechef.featureexpr.FeatureExpr.createDefinedExternal

object LinuxFeatureModel {
    val featureModel: FeatureModel = {
        println("loading feature model...");
        val start = System.currentTimeMillis
        val featureModel = FeatureModel.createFromDimacsFile_2Var("2.6.33.3-2var.dimacs")
        println("done. [" + (System.currentTimeMillis - start) + " ms]")
        featureModel
    }
}

class LinuxFeatureModelTest extends TestCase {

    import LinuxFeatureModel.featureModel

    @Test
    def testSatisfiability {
        val CONFIG_LBDAF = FeatureExpr.createDefinedExternal("CONFIG_LBDAF")

        assertTrue(FeatureExpr.base.isSatisfiable(featureModel))
        assertTrue((CONFIG_LBDAF or (CONFIG_LBDAF.not)).isSatisfiable(featureModel))
        assertTrue(CONFIG_LBDAF.not.isSatisfiable(featureModel))
        assertTrue(CONFIG_LBDAF.isSatisfiable(featureModel))

    }
    @Test
    def testSatisfiability2 {
        val CONFIG_LBDAF = FeatureExpr.createDefinedExternal("unknown")

        assertTrue((CONFIG_LBDAF or (CONFIG_LBDAF.not)).isSatisfiable(featureModel))
        assertTrue(CONFIG_LBDAF.not.isSatisfiable(featureModel))
        assertTrue(CONFIG_LBDAF.isSatisfiable(featureModel))

    }

    @Test
    def testMiscConstraints {
        val CONFIG_X86_64 = FeatureExpr.createDefinedExternal("CONFIG_X86_64")
        val CONFIG_HIGHMEM64G = FeatureExpr.createDefinedExternal("CONFIG_HIGHMEM64G")

        assertTrue((CONFIG_X86_64 or CONFIG_HIGHMEM64G).isSatisfiable(featureModel))
        assertTrue(((CONFIG_X86_64.not) and (CONFIG_HIGHMEM64G.not)).isSatisfiable(featureModel))
    }

    @Test
    def testCorrectness {
        val allocators = List("SLAB", "SLOB", "SLUB")
        println(allocators.reduceLeft(_ + "|" + _) + ": " + allocators.map(x => createDefinedExternal("CONFIG_" + x)).foldRight(FeatureExpr.base)(_ or _).isTautology(featureModel))
        println("!(" + allocators.reduceLeft(_ + "&" + _) + "): " + allocators.map(x => createDefinedExternal("CONFIG_" + x)).foldRight(FeatureExpr.base)(_ and _).not.isTautology(featureModel))
        for (a <- allocators; b <- allocators if (a != b)) {
            println(a + " implies !" + b + ": " + (createDefinedExternal("CONFIG_" + a) implies createDefinedExternal("CONFIG_" + b).not).isTautology(featureModel))
            println("!(" + a + " & " + b + "): " + (createDefinedExternal("CONFIG_" + a) and createDefinedExternal("CONFIG_" + b)).not.isTautology(featureModel))
        }
        println("CONFIG_DEFAULT_SECURITY implies CONFIG_SECURITY: " + (createDefinedExternal("CONFIG_DEFAULT_SECURITY") implies createDefinedExternal("CONFIG_SECURITY")).isTautology(featureModel))
        println("!CONFIG_X86_EXTENDED_PLATFORM: " + (!createDefinedExternal("CONFIG_X86_EXTENDED_PLATFORM")).isTautology(featureModel))
    }
    @Test
    @Ignore
    def testIsModelSatisfiable {
        val solver = SolverFactory.newDefault();
        solver.setTimeoutMs(1000);
        //        solver.setTimeoutOnConflicts(100000)

        var uniqueFlagIds: Map[String, Int] =
            featureModel.variables

        solver.newVar(uniqueFlagIds.size)

        solver.addAllClauses(featureModel.clauses)

        assertTrue(solver.isSatisfiable())
    }


}


class LinuxDependencyAnalysis extends TestCase {

    import LinuxFeatureModel.featureModel

    @Test
    def testSatisfiability {
        val featureNames = List("CONFIG_MEMORY_HOTPLUG", "CONFIG_DEBUG_SPINLOCK", "CONFIG_BUG",
            "CONFIG_SMP", "CONFIG_DEBUG_SPINLOCK", "CONFIG_NEED_MULTIPLE_NODES", "CONFIG_DISCONTIGMEM", "CONFIG_FLATMEM", "CONFIG_SPARSEMEM", "CONFIG_ACPI");
        val features = featureNames.map(FeatureExpr.createDefinedExternal(_))




        println(features)
        for (f1 <- features; f2 <- features if f1 != f2) {
            if ((f1 implies f2).isTautology(featureModel))
                println(f1 + " => " + f2)
            if ((f1 mex f2).isTautology(featureModel))
                println(f1 + " mex " + f2)
        }


    }

    @Test
    def testErrorConditions {
        def d(s: String) = createDefinedExternal(s)
        val c1 = (d("CONFIG_BUG") and (d("CONFIG_SMP") or d("CONFIG_DEBUG_SPINLOCK")))

        println(c1 + ": " + (c1.isSatisfiable))

    }
}
