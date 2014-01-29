package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.conditional.{Choice, Opt}
import de.fosd.typechef.parser.c.{AST}
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.crewrite.asthelper.EnforceTreeHelper

object ProductDerivation {
    def deriveProduct[T <: Product](ast: T, selectedFeatures: Set[String]): T = {
        assert(ast != null)

        val prod = manytd(rule {
            case l: List[Opt[_]] => {
                var res: List[Opt[_]] = List()
                // use l.reverse here to omit later reverse on res or use += or ++= in the thenBranch
                for (o <- l.reverse)
                    if (o.feature.evaluate(selectedFeatures)) {
                        res ::= o.copy(feature = FeatureExprFactory.True)
                    }
                res
            }
            case Choice(feature, thenBranch, elseBranch) => {
                if (feature.evaluate(selectedFeatures)) thenBranch
                else elseBranch
            }
            case a: AST => a.clone()
        })
        val cast = prod(ast).get.asInstanceOf[T]
        EnforceTreeHelper.copyPositions(ast, cast)
        cast
    }
}
