package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, Configuration}
import de.fosd.typechef.parser.WithPosition

import org.kiama.rewriting._
import org.kiama.rewriting.Rewriter._

/**
 * Created with IntelliJ IDEA.
 * User: rhein
 * Date: 7/11/12
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */

object ProductDerivation {
//  class WithPositionRewriter extends CallbackRewriter with WithPosition {
//     def rewriting[T <: AST](oldTerm: T, newTerm: T): T = {
//       (oldTerm, newTerm) match {
//         case (o: AST, n: AST) => n.setPositionRange(o.getPositionFrom, o.getPositionTo)
//       }
//     }
//  }
//
//  object WithPositionRewriter extends WithPositionRewriter

  // positions aren't copied so we loose them
  def deriveProduct[T <: Product](ast: T, selectedFeatures: Set[String]): T = {
    assert(ast != null)

    val prod = manytd(rule {
      case l: List[Opt[_]] => {
        var res: List[Opt[_]] = List()
        // use l.reverse here to omit later reverse on res or use += or ++= in the thenBranch
        for (o <- l.reverse)
          if (o.feature == FeatureExprFactory.True)
            res ::= o
          else if (o.feature.evaluate(selectedFeatures)) {
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
    cast
  }
}
