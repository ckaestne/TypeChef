package de.fosd.typechef.conditional

import org.kiama.attribution.Attribution._
import org.kiama._
import attribution.Attributable
import de.fosd.typechef.featureexpr.FeatureExpr

/**
 * provides featureExpr lookup for *all* AST nodes
 */
trait FeatureExprLookup {

    val featureExpr: Attributable ==> FeatureExpr = attr {
        case node =>
            node.parent match {
                case o@Opt(f, _) => o -> featureExpr and f
                case c: Choice[_] => c -> featureExpr and (if (c.thenBranch == node) c.feature else c.feature.not)
                case null => FeatureExpr.base
                case e => e -> featureExpr
            }


    }

}

