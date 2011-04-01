/*
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 24.03.11
 * Time: 15:06
 */
package de.fosd.typechef.linux

;

import scala.io.Source
import scala.util.matching.Regex

object CountFeatures {

    def main(args: Array[String]) {

        val FeatureName = new Regex(""".*CONFIG_(\w+).*""")

        val openFeatures = Source.fromFile("openFeaturesList.txt").getLines.toList.toSet

        val conf = (Source.fromFile("partialConf.h").getLines.toList ++
                Source.fromFile("completedConf.h").getLines.toList).filter(x => x.startsWith("#define") || x.startsWith("#undef")).map(_ match {
            case FeatureName(name) => "CONFIG_" + name
            case _ => ""
        })

        println(openFeatures.size)
        println((openFeatures -- conf).size)


    }

}