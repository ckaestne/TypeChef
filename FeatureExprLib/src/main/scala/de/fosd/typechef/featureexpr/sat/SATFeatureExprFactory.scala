package de.fosd.typechef.featureexpr.sat

import de.fosd.typechef.featureexpr._
import java.net.URI

object SATFeatureExprFactory extends AbstractFeatureExprFactory {


    def createDefinedExternal(name: String): SingleFeatureExpr = FExprBuilder.definedExternal(name)
    def createDefinedMacro(name: String, macroTable: FeatureProvider): FeatureExpr = FExprBuilder.definedMacro(name, macroTable)


    //helper
    //        def createIf(condition: FeatureExpr, thenBranch: FeatureExpr, elseBranch: FeatureExpr): FeatureExpr = FeatureExprFactory.createBooleanIf(condition, thenBranch, elseBranch)

    val baseB: SATFeatureExpr = de.fosd.typechef.featureexpr.sat.True
    val deadB: SATFeatureExpr = de.fosd.typechef.featureexpr.sat.False
    val True: FeatureExpr = baseB
    val False: FeatureExpr = deadB


    //feature model stuff
    def featureModelFactory: FeatureModelFactory = SATFeatureModel

    def createFeatureExprFast(enabledFeatures: Set[SingleFeatureExpr], disabledFeatures: Set[SingleFeatureExpr]) : FeatureExpr = {
        // first a fold on the enabled Features (inner) then a fold on the disabled Features
        return disabledFeatures.foldLeft (
            enabledFeatures.foldLeft(True)({(f:FeatureExpr,sf:SingleFeatureExpr) => f.and(sf)})
        ) ({(f:FeatureExpr,sf:SingleFeatureExpr) => f.and(sf.not())})
    }
}