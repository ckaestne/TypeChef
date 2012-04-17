package de.fosd.typechef.featureexpr.sat

import de.fosd.typechef.featureexpr._


object SATFeatureExprFactory extends AbstractFeatureExprFactory {


    def createDefinedExternal(name: String): FeatureExpr = FExprBuilder.definedExternal(name)
    def createDefinedMacro(name: String, macroTable: FeatureProvider): FeatureExpr = FExprBuilder.definedMacro(name, macroTable)


    //helper
    //        def createIf(condition: FeatureExpr, thenBranch: FeatureExpr, elseBranch: FeatureExpr): FeatureExpr = FeatureExprFactory.createBooleanIf(condition, thenBranch, elseBranch)

    val baseB: SATFeatureExpr = de.fosd.typechef.featureexpr.sat.True
    val deadB: SATFeatureExpr = de.fosd.typechef.featureexpr.sat.False
    val base: FeatureExpr = baseB
    val dead: FeatureExpr = deadB
}