package de.fosd.typechef.crewrite

import io.Source
import java.io.{FileInputStream, FileWriter, File}
import de.fosd.typechef.featureexpr.{FeatureExprParser, FeatureExprFactory}

/**
 * currently here to avoid complication with rebuilding all ivy/maven packages
 *
 * run in busybox directory
 */
object LinkBusyboxCFG extends App {

    import FeatureExprFactory._

    FeatureExprFactory.setDefault(FeatureExprFactory.bdd)

    val path = "/usr0/home/ckaestne/work/TypeChef/BusyboxAnalysis/gitbusybox/"
    //
    var bigCFG = new FileCFG(Set(), Set())

    for (file <- Source.fromFile(path + "filelist").getLines()) {
        val cfgFile = path + file + ".cfg"
        print("linking " + cfgFile)

        val pcFile = new File(path + file + ".pc")
        val filePC = if (pcFile.exists()) new FeatureExprParser().parseFile(new FileInputStream(pcFile)) else True
        val cfg = WholeProjectCFG.loadFileCFG(new File(cfgFile), filePC)
        println(".")

        bigCFG = bigCFG link cfg
    }
    assert(bigCFG.checkConsistency)

    println("writing result")

    val writer = new FileWriter(path + "finalcfg.cfg")
    bigCFG.write(writer)
    writer.close()

    println("done.")

}