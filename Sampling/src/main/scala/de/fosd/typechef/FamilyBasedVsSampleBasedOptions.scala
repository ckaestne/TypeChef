package de.fosd.typechef

import java.util
import gnu.getopt.{Getopt, LongOpt}
import java.lang.String
import de.fosd.typechef.options.{FrontendOptionsWithConfigFiles, Options}
import de.fosd.typechef.options.Options.OptionGroup

class FamilyBasedVsSampleBasedOptions extends FrontendOptionsWithConfigFiles {

    def analyze = singleConf != "" || codeCoverage || codeCoverageNH || pairwise != "" || family

    private[typechef] var singleConf: String = ""
    private[typechef] var codeCoverage: Boolean = false
    private[typechef] var codeCoverageNH: Boolean = false
    private[typechef] var pairwise: String = ""
    private[typechef] var family: Boolean = false
    private[typechef] var errorDetection: Boolean = false
    private var rootFolder: String = ""

    private final val F_SINGLECONF: Char = Options.genOptionId
    private final val F_CODECOVERAGE: Char = Options.genOptionId
    private final val F_CODECOVERAGENH: Char = Options.genOptionId
    private final val F_PAIRWISE: Char = Options.genOptionId
    private final val F_FAMILY: Char = Options.genOptionId
    private final val F_ERRORDETECTION: Char = Options.genOptionId()

    protected override def getOptionGroups() = {
        val groups = new util.ArrayList[OptionGroup](super.getOptionGroups())

        groups.add(
            new Options.OptionGroup("Sampling options", 1,
                new Options.Option("singleconf", LongOpt.REQUIRED_ARGUMENT, F_SINGLECONF, "config",
                    "enable single config sampling; default is disabled"),

                new Options.Option("codecoverage", LongOpt.NO_ARGUMENT, F_CODECOVERAGE, null,
                    "enable code-coverage sampling; default is disabled"),

                new Options.Option("codecoveragenh", LongOpt.NO_ARGUMENT, F_CODECOVERAGENH, null,
                    "enable code-coverage (without header files) sampling; default is disabled"),

                new Options.Option("pairwise", LongOpt.REQUIRED_ARGUMENT, F_PAIRWISE, "config",
                    "enable pairwise sampling; default is disabled"),

                new Options.Option("family", LongOpt.NO_ARGUMENT, F_FAMILY, null,
                    "enable family-based checking, default is disabled"),

                new Options.Option("errordetection", LongOpt.NO_ARGUMENT, F_ERRORDETECTION, null,
                    "run all data-flow error detections and check results against sampling configurations, default is disabled")
            ))

        groups
    }

    protected override def interpretOption(c: Int, g: Getopt): Boolean = {
        if (c == F_SINGLECONF) singleConf = g.getOptarg
        else if (c == F_CODECOVERAGE) codeCoverage = true
        else if (c == F_CODECOVERAGENH) codeCoverageNH = true
        else if (c == F_PAIRWISE) pairwise = g.getOptarg
        else if (c == F_FAMILY) family = true
        else if (c == F_ERRORDETECTION) errorDetection = true
        else {
            return super.interpretOption(c, g)
        }

        true
    }

    def getRootFolder: String = {
        rootFolder
    }

}
