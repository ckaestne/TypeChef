package de.fosd.typechef.typesystem

import de.fosd.typechef.featureexpr.FeatureExpr


/**
 * bundles all environments during type checking
 */
trait CEnv extends CTypeEnv {

    class Env(
                     val featureExpr: FeatureExpr,
                     val varEnv: VarTypingContext,
                     val structEnv: StructEnv,
                     val enumEnv: EnumEnv
                     ) {

    }


}