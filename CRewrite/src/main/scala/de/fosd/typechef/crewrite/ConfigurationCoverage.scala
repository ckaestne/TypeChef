package de.fosd.typechef.crewrite

import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureModel, FeatureExpr}
import de.fosd.typechef.featureexpr.sat.DefinedExpr

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
    val velems = filterAllVariableElems(a)
    naiveCoverage(velems.toSet, fm, env)
  }

  def naiveCoverage(in: Set[Product], fm: FeatureModel, env: ASTEnv) = {
    var R: Set[FeatureExpr] = Set()   // found configurations
    var B: Set[Product] = Set()       // selected blocks; Opt and One

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
      (in.diff(B).map(env.featureExpr(_))) + "\n" + R
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

    /**
   * Completes a partial configuration so that no variability remains.
   * Features are set to false if possible.
   * If no satisfiable configuration is found then null is returned.
   * @param partialConfig
   * @param remainingFeatures
   * @param fm
   */
  def completeConfiguration(partialConfig : FeatureExpr, remainingFeatures:List[DefinedExpr], fm:FeatureModel, preferDisabledFeatures : Boolean = true) : FeatureExpr = {
    var config : FeatureExpr = partialConfig
    val fIter = remainingFeatures.iterator
    var partConfigFeasible : Boolean = true
    while (partConfigFeasible && fIter.hasNext) {
      val fx :DefinedExpr = fIter.next()
      if (preferDisabledFeatures) {
        // try to set other variables to false first
        var tmp : FeatureExpr = config.andNot(fx)
        if (tmp.isSatisfiable(fm)) {
          config = tmp
        } else {
          tmp = config.and(fx)
          if (tmp.isSatisfiable(fm)) {
            config = tmp
          } else {
            // this configuration cannot be satisfied any more
            return null
            partConfigFeasible=false
          }
        }
      } else {
        // try to set other variables to true first
        var tmp : FeatureExpr = config.and(fx)
        if (tmp.isSatisfiable(fm)) {
          config = tmp
        } else {
          tmp = config.andNot(fx)
          if (tmp.isSatisfiable(fm)) {
            config = tmp
          } else {
            // this configuration cannot be satisfied any more
            return null
            partConfigFeasible=false
          }
        }
      }
    }
    if (partConfigFeasible) {
      // all features have been processed, and the config is still feasible.
      // so we have a complete configuration now!
      return config
    }
    return null
  }

  // create a new feature model from a given set of annotations
  def createFeatureModel(in: Set[Opt[_]]) = {
    val annotations = in.map(_.feature)
    val combinedannotations = annotations.fold(FeatureExprFactory.True)(_ and _)

    FeatureExprFactory.default.featureModelFactory.create(combinedannotations)
  }
}
