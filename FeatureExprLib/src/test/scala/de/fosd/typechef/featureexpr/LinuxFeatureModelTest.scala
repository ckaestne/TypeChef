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
import java.net.URI
import scala.io.Source
import de.fosd.typechef.featureexpr.bdd.BDDFeatureModel


abstract class AbstractLinuxFeatureModelTest {


    val dimacsFile = this.getClass.getResource("/x86.dimacs").toURI
//    val dimacsFile = this.getClass.getResource("/2.6.33.3-2var.dimacs").toURI

    def getFeatureExprFactory: AbstractFeatureExprFactory
    def getFeatureModel: FeatureModel

    @Test
    def testSatisfiability {
        val CONFIG_LBDAF = getFeatureExprFactory.createDefinedExternal("CONFIG_LBDAF")

        assertTrue(getFeatureExprFactory.True.isSatisfiable(getFeatureModel))
        assertTrue((CONFIG_LBDAF or (CONFIG_LBDAF.not)).isSatisfiable(getFeatureModel))
        assertTrue(CONFIG_LBDAF.not.isSatisfiable(getFeatureModel))
        assertTrue(CONFIG_LBDAF.isSatisfiable(getFeatureModel))
        println(getFeatureExprFactory.createDefinedExternal("CONFIG_X86").isTautology(getFeatureModel))

    }

    @Test
    def testSatisfiability2 {
        val CONFIG_LBDAF = getFeatureExprFactory.createDefinedExternal("unknown")

        assertTrue((CONFIG_LBDAF or (CONFIG_LBDAF.not)).isSatisfiable(getFeatureModel))
        assertTrue(CONFIG_LBDAF.not.isSatisfiable(getFeatureModel))
        assertTrue(CONFIG_LBDAF.isSatisfiable(getFeatureModel))

    }

    @Test
    def testMiscConstraints {
        val CONFIG_X86_64 = getFeatureExprFactory.createDefinedExternal("CONFIG_X86_64")
        val CONFIG_HIGHMEM64G = getFeatureExprFactory.createDefinedExternal("CONFIG_HIGHMEM64G")

        assertTrue((CONFIG_X86_64 or CONFIG_HIGHMEM64G).isSatisfiable(getFeatureModel))
        assertTrue(((CONFIG_X86_64.not) and (CONFIG_HIGHMEM64G.not)).isSatisfiable(getFeatureModel))
    }

    @Test
    def testContradictions {
        val slab = getFeatureExprFactory.createDefinedExternal("CONFIG_SLAB")
        val slub = getFeatureExprFactory.createDefinedExternal("CONFIG_SLUB")
        val slob = getFeatureExprFactory.createDefinedExternal("CONFIG_SLOB")

        assertTrue((slab or slub).isSatisfiable(getFeatureModel))
        assertTrue((slab.not and slub).isSatisfiable(getFeatureModel))
        assertFalse((slab and slub).isSatisfiable(getFeatureModel))
        assertTrue((slab.not and slub.not).isSatisfiable(getFeatureModel))
        assertFalse((slab.not and slub.not and slob.not).isSatisfiable(getFeatureModel))
    }

    @Test
    def testCorrectness {
        val allocators = List("SLAB", "SLOB", "SLUB")
        println(allocators.reduceLeft(_ + "|" + _) + ": " + allocators.map(x => getFeatureExprFactory.createDefinedExternal("CONFIG_" + x)).foldRight(getFeatureExprFactory.True)(_ or _).isTautology(getFeatureModel))
        println("!(" + allocators.reduceLeft(_ + "&" + _) + "): " + allocators.map(x => getFeatureExprFactory.createDefinedExternal("CONFIG_" + x)).foldRight(getFeatureExprFactory.True)(_ and _).not.isTautology(getFeatureModel))
        for (a <- allocators; b <- allocators if (a != b)) {
            println(a + " implies !" + b + ": " + (getFeatureExprFactory.createDefinedExternal("CONFIG_" + a) implies getFeatureExprFactory.createDefinedExternal("CONFIG_" + b).not).isTautology(getFeatureModel))
            println("!(" + a + " & " + b + "): " + (getFeatureExprFactory.createDefinedExternal("CONFIG_" + a) and getFeatureExprFactory.createDefinedExternal("CONFIG_" + b)).not.isTautology(getFeatureModel))
        }
        println("CONFIG_DEFAULT_SECURITY implies CONFIG_SECURITY: " + (getFeatureExprFactory.createDefinedExternal("CONFIG_DEFAULT_SECURITY") implies getFeatureExprFactory.createDefinedExternal("CONFIG_SECURITY")).isTautology(getFeatureModel))
        println("!CONFIG_X86_EXTENDED_PLATFORM: " + (!getFeatureExprFactory.createDefinedExternal("CONFIG_X86_EXTENDED_PLATFORM")).isTautology(getFeatureModel))
    }


}

class BDDLinuxFeatureModelTest extends AbstractLinuxFeatureModelTest {
    val featureModel:BDDFeatureModel = BDDFeatureModel.createFromDimacsFile(Source.fromURI(dimacsFile)).asInstanceOf[BDDFeatureModel]
    def getFeatureModel = featureModel
    def getFeatureExprFactory = FeatureExprFactory.bdd

    @Test
    def testFeatureModelAssumptionsDimacs() {
        val f = getFeatureExprFactory.createDefinedExternal("CONFIG_X86_32") and getFeatureExprFactory.createDefinedExternal("CONFIG_PARAVIRT")
        assertTrue(f.isSatisfiable())
        assertTrue(f.isSatisfiable(featureModel))
        assertFalse(f.isTautology(featureModel))
        val fm = featureModel.assumeFalse("CONFIG_X86_32")
        assertFalse(f.isSatisfiable(fm))

        val ff = getFeatureExprFactory.createDefinedExternal("CONFIG_X86_32") or getFeatureExprFactory.createDefinedExternal("CONFIG_PARAVIRT")
        val fm2 = fm.assumeTrue("CONFIG_PARAVIRT")
        assertTrue(ff.isSatisfiable(fm2))
        assertTrue(ff.isTautology(fm2))
    }

    @Test
    //    @Ignore
    def testIsModelSatisfiable {
        val solver = SolverFactory.newDefault();
        solver.setTimeoutMs(20000);
        //        solver.setTimeoutOnConflicts(100000)

        var uniqueFlagIds: Map[String, Int] =
            featureModel.variables

        solver.newVar(uniqueFlagIds.size)

        solver.addAllClauses(featureModel.clauses)

        assertTrue(solver.isSatisfiable())
    }

    @Test
    def testFeatureModelAssumptions() {
        var fm = de.fosd.typechef.featureexpr.bdd.BDDFeatureModel.empty
        fm = fm.assumeFalse("a")
        fm = fm.assumeTrue("b")

        def a = getFeatureExprFactory.createDefinedExternal("a")
        def b = getFeatureExprFactory.createDefinedExternal("b")
        def h = getFeatureExprFactory.createDefinedExternal("h")

        assertTrue(h.isSatisfiable(fm))
        assertTrue(a.isContradiction(fm))
        assertTrue(a.isSatisfiable())
        assertTrue(b.isTautology(fm))
        assertTrue((a or b).isTautology(fm))
        assertFalse((a or b).isTautology())

    }
}

class SATLinuxFeatureModelTest extends AbstractLinuxFeatureModelTest {
    val featureModel = de.fosd.typechef.featureexpr.sat.SATFeatureModel.createFromDimacsFile(Source.fromURI(dimacsFile))
    def getFeatureModel = featureModel
    def getFeatureExprFactory = FeatureExprFactory.sat
}