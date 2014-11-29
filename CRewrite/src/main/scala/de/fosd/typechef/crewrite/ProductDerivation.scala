package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.conditional.{Choice, Opt}
import de.fosd.typechef.parser.c.{EnforceTreeHelper, AST}
import org.kiama.rewriting.Rewriter._

object ProductDerivation extends EnforceTreeHelper {
    def deriveProduct[T <: Product](ast: T, selectedFeatures: Set[String]): T = {
        assert(ast != null)

        val prod = manytd(rule[Product] {
            case l: List[_] if l.forall(_.isInstanceOf[Opt[_]]) => {
                var res: List[Opt[_]] = List()
                // use l.reverse here to omit later reverse on res or use += or ++= in the thenBranch
                for (o <- l.reverse.asInstanceOf[List[Opt[_]]])
                    if (o.condition.evaluate(selectedFeatures)) {
                        res ::= o.copy(condition = FeatureExprFactory.True)
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
        copyPositions(ast, cast)
        cast
    }
}
