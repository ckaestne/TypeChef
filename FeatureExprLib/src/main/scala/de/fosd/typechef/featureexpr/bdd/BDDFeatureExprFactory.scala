package de.fosd.typechef.featureexpr.bdd

import de.fosd.typechef.featureexpr._
import java.net.URI


object BDDFeatureExprFactory extends AbstractFeatureExprFactory {


    def createDefinedExternal(name: String): FeatureExpr = FExprBuilder.definedExternal(name)
    def createDefinedMacro(name: String, macroTable: FeatureProvider): FeatureExpr = FExprBuilder.definedMacro(name, macroTable)


    //helper
    //        def createIf(condition: FeatureExpr, thenBranch: FeatureExpr, elseBranch: FeatureExpr): FeatureExpr = FeatureExprFactory.createBooleanIf(condition, thenBranch, elseBranch)

    val TrueB: BDDFeatureExpr = de.fosd.typechef.featureexpr.bdd.True
    val FalseB: BDDFeatureExpr = de.fosd.typechef.featureexpr.bdd.False
    val True: FeatureExpr = TrueB
    val False: FeatureExpr = FalseB

    def featureModelFactory = BDDFeatureModel

    def createFeatureExprFast(enabledFeatures: Set[SingleFeatureExpr], disabledFeatures: Set[SingleFeatureExpr]) : FeatureExpr = {
        var retBDD = TrueB.bdd.id() // makes a copy of this bdd, so that it is not consumed by the andWith functions
        for (f <- enabledFeatures)
            if (! f.isInstanceOf[SingleBDDFeatureExpr])
                throw new InternalError("found a unknown feature expression type");
            else
                retBDD = f.asInstanceOf[SingleBDDFeatureExpr].bdd.id() andWith retBDD
        for (f <- disabledFeatures)
            if (! f.isInstanceOf[SingleBDDFeatureExpr])
                throw new InternalError("found a unknown feature expression type");
            else
                retBDD = f.asInstanceOf[SingleBDDFeatureExpr].bdd.not() andWith retBDD
        return new BDDFeatureExpr(retBDD)
    }
}