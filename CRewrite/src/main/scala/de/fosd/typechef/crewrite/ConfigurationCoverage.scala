package de.fosd.typechef.crewrite

import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.featureexpr.{NoFeatureModel, FeatureModel, FeatureExpr}

// this code determines all configurations for a file based on a given ast
// algorithms to get coverage are inspired by:
// [1] http://www4.informatik.uni-erlangen.de/Publications/2011/tartler_11_plos.pdf
object ConfigurationCoverage extends ConditionalNavigation {
  def collectFeatureExpressions(env: ASTEnv) = {
    var res: Set[FeatureExpr] = Set()
    for (e <- env.keys())
      res += env.featureExpr(e.asInstanceOf[Product])

    res
  }

  // naive coverage implementation inspired by [1]
  // given all optional nodes the algorithm determines all
  // partical configurations that are necessary to select all blocks
  // the result is not the number of variants that can be generated
  // from the input set in
  // wrapper for naiveCoverage
  def naiveCoverageAny(a: Product, fm: FeatureModel, env: ASTEnv) = {
    val opts = filterAllOptElems(a)
    naiveCoverage(opts.toSet, fm, env)
  }

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
      (in.diff(B).map(_.feature)) + "\n" + R
    )

    // reduce number of configurations using implication check; at most n^2 SAT checks!!!
    // https://github.com/ckaestne/TypeChef/blob/MinimalVariants/LinuxAnalysis/src/main/scala/de/fosd/typechef/minimalvariants/MinimalVariants.scala
    var Rreduced: Set[FeatureExpr] = Set()
    Rreduced = Set()
    for (f <- R) {
      if (!f.isTautology(fm))
        if (!Rreduced.exists(o => (o implies f).isTautology(fm)))
          Rreduced += f
    }

    Rreduced
  }

  // create a new feature model from a given set of annotations
  def createFeatureModel(in: Set[Opt[_]]) = {
    var res = NoFeatureModel
    val annotations = in.map(_.feature)
    val combinedannotations = annotations.fold(FeatureExpr.base)(_ or _)

    res and combinedannotations
  }
}
