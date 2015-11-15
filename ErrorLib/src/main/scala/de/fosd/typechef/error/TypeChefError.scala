package de.fosd.typechef.error

import de.fosd.typechef.featureexpr.FeatureExpr


class TypeChefError(
                       val severity: Severity.Severity,
                       val condition: FeatureExpr,
                       val msg: String,
                       val where: WithPosition,
                       val severityExtra: String = "" // is written into the xml file and can be used for filtering; targeted for machines not humans
                       ) {

    override def toString =
        severity.toString.take(1) + " [" + condition + "] " +
            (if (where == null) "" else where.getPositionFrom + "--" + where.getPositionTo) + "\n\t" + msg


    def isWarning = Set(Severity.Warning, Severity.SecurityWarning) contains severity
}
