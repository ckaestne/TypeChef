package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr.FeatureModel
import de.fosd.typechef.parser.c.ASTEnv

class CheckReturnValueOfStandardLibraryFunctions(env: ASTEnv, fm: FeatureModel) {

    // list of standard library functions and their possible error returns
    private val functionErrorReturns = List[String, List]

}
