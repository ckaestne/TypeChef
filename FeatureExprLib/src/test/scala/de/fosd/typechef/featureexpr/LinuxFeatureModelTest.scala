//package de.fosd.typechef.featureexpr
//
///**
// * Created by IntelliJ IDEA.
// * User: kaestner
// * Date: 02.01.11
// * Time: 15:05
// * To change this template use File | Settings | File Templates.
// */
//
//import org.junit._
//import org.junit.Assert._
//import org.sat4j.minisat.SolverFactory
//import junit.framework.TestCase
//import org.sat4j.reader.DimacsReader
//
//object LinuxFeatureModel {
//    val featureModel: FeatureModel = {
//        println("loading feature model...");
//        val start = System.currentTimeMillis
//        val featureModel = FeatureModel.createFromDimacsFile("2.6.33.3-1var.dimacs")
//        println("done. [" + (System.currentTimeMillis - start) + " ms]")
//        featureModel
//    }
//}
//
//class LinuxFeatureModelTest extends TestCase {
//
//    import LinuxFeatureModel.featureModel
//
//    @Test
//    def testSatisfiability {
//        val CONFIG_LBDAF = FeatureExpr.createDefinedExternal("CONFIG_LBDAF")
//
//        assertTrue(FeatureExpr.base.isSatisfiable(featureModel))
//        assertTrue((CONFIG_LBDAF or (CONFIG_LBDAF.not)).isSatisfiable(featureModel))
//        assertTrue(CONFIG_LBDAF.not.isSatisfiable(featureModel))
//        assertTrue(CONFIG_LBDAF.isSatisfiable(featureModel))
//
//    }
//    @Test
//    def testSatisfiability2 {
//        val CONFIG_LBDAF = FeatureExpr.createDefinedExternal("unknown")
//
//        assertTrue((CONFIG_LBDAF or (CONFIG_LBDAF.not)).isSatisfiable(featureModel))
//        assertTrue(CONFIG_LBDAF.not.isSatisfiable(featureModel))
//        assertTrue(CONFIG_LBDAF.isSatisfiable(featureModel))
//
//    }
//
//    @Test
//    def testBare {
//        val solver = SolverFactory.newDefault();
//                solver.setTimeoutMs(1000);
////        solver.setTimeoutOnConflicts(100000)
//
//        var uniqueFlagIds: Map[String, Int] =
//            featureModel.variables
//
//        solver.newVar(uniqueFlagIds.size)
//
//        solver.addAllClauses(featureModel.clauses)
//
//        assertTrue(solver.isSatisfiable())
//    }
//
//    @Test
//    def testLibrary {
//        val reader=new DimacsReader(SolverFactory.newDefault())
//        val problem=reader.parseInstance("2.6.33.3-1var.dimacs")
//        assertTrue(problem.isSatisfiable)
//    }
//
//
//}