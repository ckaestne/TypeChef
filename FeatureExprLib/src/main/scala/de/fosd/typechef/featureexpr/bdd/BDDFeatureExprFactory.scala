package de.fosd.typechef.featureexpr.bdd

import de.fosd.typechef.featureexpr._


object BDDFeatureExprFactory extends AbstractFeatureExprFactory {


    def createDefinedExternal(name: String): FeatureExpr = FExprBuilder.definedExternal(name)
    def createDefinedMacro(name: String, macroTable: FeatureProvider): FeatureExpr = FExprBuilder.definedMacro(name, macroTable)


    //helper
    //        def createIf(condition: FeatureExpr, thenBranch: FeatureExpr, elseBranch: FeatureExpr): FeatureExpr = FeatureExprFactory.createBooleanIf(condition, thenBranch, elseBranch)

    val baseB: BDDFeatureExpr = de.fosd.typechef.featureexpr.bdd.True
    val deadB: BDDFeatureExpr = de.fosd.typechef.featureexpr.bdd.False
    val base: FeatureExpr = baseB
    val dead: FeatureExpr = deadB
}