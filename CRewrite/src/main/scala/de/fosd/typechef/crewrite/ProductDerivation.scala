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
  def deriveProduct[T <: AST](ast: T, selectedFeatures: Set[String]): T = {
    assert(ast != null)

    val prod = everywheretd(rule {
      case Opt(feature, entry) => {
        if (feature.evaluate(selectedFeatures)) Opt(FeatureExprFactory.True, entry)
        else Opt(FeatureExprFactory.False, entry)
      }
      case Choice(feature, thenBranch, elseBranch) => {
        if (feature.evaluate(selectedFeatures)) thenBranch
        else elseBranch
      }
    })
    val cast = prod(ast)
    cast.asInstanceOf[T]
  }
}
