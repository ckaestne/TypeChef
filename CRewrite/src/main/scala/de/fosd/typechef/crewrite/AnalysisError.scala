package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.c.AST

// class to report errors and warnings of data-flow analyses
// condition => feature expression of the erroneous element
// msg       => specific message that comes along with the error message
// pos       => actually the AST element itself; we use the position information
//              of the AST element
class AnalysisError(val condition: FeatureExpr, val msg: String, val pos: AST) {
    override def toString  = msg + " [" + condition + "] " +
            (if (pos == null) "" else pos.getPositionFrom + "--" + pos.getPositionTo)
}
