package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.conditional.{Choice, Opt}
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.crewrite.asthelper.EnforceTreeHelper

object ProductDerivation {
    def deriveProduct[T <: Product](ast: T, selectedFeatures: Set[String]): T = {
        assert(ast != null)

        val prod = manytd(rule {
            case l: List[Opt[_]] =>
                l.filter { o => o.feature.evaluate(selectedFeatures) }
                .map { o => o.copy(feature = FeatureExprFactory.True) }
            case Choice(feature, thenBranch, elseBranch) =>
                if (feature.evaluate(selectedFeatures)) thenBranch
                else elseBranch
        })
        val cast = prod(ast).get.asInstanceOf[T]
        EnforceTreeHelper.copyPositions(ast, cast)
        cast
    }
}
