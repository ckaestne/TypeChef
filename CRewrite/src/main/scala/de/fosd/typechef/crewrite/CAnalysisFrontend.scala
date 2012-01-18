package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr.{NoFeatureModel, FeatureModel}
import de.fosd.typechef.parser.c.AST

class CAnalysisFrontend(tunit: AST, featureModel: FeatureModel = NoFeatureModel) extends CASTEnv with ConditionalControlFlow {
  def succs: Boolean = {
    val env = createASTEnv(tunit)
    true
  }
}