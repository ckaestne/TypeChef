package de.fosd.typechef.crewrite

import org.junit.Test
import io.Source
import java.io.{FileInputStream, FileWriter, File}
import de.fosd.typechef.featureexpr.{FeatureExprParser, FeatureExprFactory}

/**
 * Created with IntelliJ IDEA.
 * User: ckaestne
 * Date: 3/12/13
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
class WholeProjectCFGTest {

    import de.fosd.typechef.featureexpr.FeatureExprFactory._

    val fa = createDefinedExternal("A")

    @Test def testCFG() {
        val n1 = new CFGNode("declaration", null, 1, "foo", True)
        val n2 = new CFGNode("statement", null, 1, "foo()", True)
        val f1 = new FileCFG(Set(n1, n2), Set((n2, n1, True)))

        val d1 = new CFGNode("function", null, 1, "foo", True)
        val f2 = new FileCFG(Set(d1), Set())


        val ff = f1.link(f2)

        println(ff)
        assert(ff.edges.contains((n2, d1, True)))

        assert(ff == (f2 link f1))
    }

    @Test def testCFG2() {
        val n1 = new CFGNode("declaration", null, 1, "foo", True)
        val n2 = new CFGNode("statement", null, 1, "foo()", True)
        val f1 = new FileCFG(Set(n1, n2), Set((n2, n1, True)))

        val d1 = new CFGNode("function", null, 1, "foo", fa)
        val f2 = new FileCFG(Set(d1), Set())


        val ff = f1.link(f2)

        println(ff)
        assert(ff.edges.contains((n2, d1, fa)))
        assert(ff.edges.contains((n2, n1, fa.not)))

        assert(ff == (f2 link f1))
    }

}


/**
 * TODO should be moved to Busyboxproject eventually
 * currently here to avoid complication with rebuilding all ivy/maven packages
 *
 * run in busybox directory
 */
object LinkBusyboxCFG extends App {

    import FeatureExprFactory._

    FeatureExprFactory.setDefault(FeatureExprFactory.bdd)

    val path = "."
    //"/usr0/home/ckaestne/work/TypeChef/BusyboxAnalysis/gitbusybox/"
    var bigCFG = new FileCFG(Set(), Set())

    for (file <- Source.fromFile(path + "filelist").getLines()) {
        val cfgFile = path + file + ".cfg"
        print("linking " + cfgFile)

        val pcFile = new File(path + file + ".pc")
        val filePC = if (pcFile.exists()) new FeatureExprParser().parseFile(new FileInputStream(pcFile)) else True
        val cfg_ = WholeProjectCFG.loadFileCFG(new File(cfgFile))
        val cfg = cfg_ and filePC
        println(".")

        bigCFG = bigCFG link cfg
    }

    println("writing result")

    val writer = new FileWriter(path + "finalcfg.cfg")
    bigCFG.write(writer)
    writer.close()

    println("done.")

}
