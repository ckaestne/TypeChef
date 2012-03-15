package de.fosd.typechef.crewrite

import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.featureexpr.{NoFeatureModel, FeatureModel, FeatureExpr, Configuration}

// this code determines all configurations for a file based on a given ast
// algorithms to get coverage are inspired by:
// [1] http://www4.informatik.uni-erlangen.de/Publications/2011/tartler_11_plos.pdf
object ConfigurationCoverage extends CASTEnv {
  def collectFeatureExpressions(env: ASTEnv) = {
    var res: Set[FeatureExpr] = Set()
    for (e <- env.keys())
      res += env.featureExpr(e)

    res
  }

  // naive coverage implementation inspired by [1]
  // given all optional nodes the algorithm determines all
  // partical configurations that are necessary to select all blocks
  // the result is not the number of variants that can be generated
  // from the input set in
  def naiveCoverage(in: Set[Opt[_]], fm: FeatureModel, env: ASTEnv) = {
    var R: Set[FeatureExpr] = Set()   // found configurations
    var B: Set[Opt[_]] = Set()        // selected blocks

    // iterate over all optional blocks
    for (b <- in) {
      // optional block b has not been handled before
      if (! B.contains(b)) {
        val fexpb = env.featureExpr(b)
        if (fexpb.isSatisfiable(fm)) {
          B ++= in.filter(fexpb implies env.featureExpr(_) isTautology())
          R += fexpb
        } else {
          B += b
        }
      }
    }

    assert(in.size == B.size, "configuration coverage missed the following optional blocks\n" +
      (in.diff(B).map(_.feature)) + "\n" +
      R
    )

    R
  }

  // create a new feature model from a given set of annotations
  def createFeatureModel(in: Set[Opt[_]]) = {
    var res = NoFeatureModel
    val annotations = in.map(_.feature)
    val combinedannotations = annotations.fold(FeatureExpr.base)(_ or _)

    res and combinedannotations
  }
}
