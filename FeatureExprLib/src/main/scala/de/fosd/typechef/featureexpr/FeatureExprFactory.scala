package de.fosd.typechef.featureexpr

import bdd.BDDFeatureExprFactory
import sat.SATFeatureExprFactory


object FeatureExprFactory {

    var default: AbstractFeatureExprFactory = if (System.getProperty("FEATUREEXPR") == "BDD") bdd else sat

    def setDefault(newFactory: AbstractFeatureExprFactory) {
        default = newFactory
    }


    lazy val bdd: AbstractFeatureExprFactory = BDDFeatureExprFactory
    lazy val sat: AbstractFeatureExprFactory = SATFeatureExprFactory

}

trait AbstractFeatureExprFactory extends FeatureExprTreeFactory {
    def createDefinedExternal(v: String): FeatureExpr
    def createDefinedMacro(name: String, macroTable: FeatureProvider): FeatureExpr

    def createBooleanIf(expr: FeatureExpr, thenBr: FeatureExpr, elseBr: FeatureExpr): FeatureExpr = (expr and thenBr) or (expr.not and elseBr)

    def base: FeatureExpr
    def dead: FeatureExpr
    def True = base
    def False = dead
}