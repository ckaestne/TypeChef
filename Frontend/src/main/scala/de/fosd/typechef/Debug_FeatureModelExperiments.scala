package de.fosd.typechef

import conditional.{Choice, Opt}
import featureexpr.{FeatureExpr, SingleFeatureExpr, FeatureModel}
import parser.c.AST

/**
 * Created with IntelliJ IDEA.
 * User: rhein
 * Date: 5/24/12
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */

object Debug_FeatureModelExperiments {

    /** some feature model experimentation, only for debug purposes */
    def experiment(fm_ts: FeatureModel) {
        val rds_t = de.fosd.typechef.featureexpr.FeatureExprFactory.createDefinedExternal("CONFIG_RDS_TCP")
        val rds_r = de.fosd.typechef.featureexpr.FeatureExprFactory.createDefinedExternal("CONFIG_RDS_RDMA")
        val rds   = de.fosd.typechef.featureexpr.FeatureExprFactory.createDefinedExternal("CONFIG_RDS")
        val M4 = de.fosd.typechef.featureexpr.FeatureExprFactory.createDefinedExternal("CONFIG_M486")
        val M3 = de.fosd.typechef.featureexpr.FeatureExprFactory.createDefinedExternal("CONFIG_M386")

        println("M4 & M3 : " + M4.and(M3).isSatisfiable(fm_ts))
        println("!M4 & !M3 : " + M4.not().and(M3.not()).isSatisfiable(fm_ts))

        println("rds: " + rds.isSatisfiable(fm_ts))
        println("rds_r: " + rds_r.isSatisfiable(fm_ts))
        println("rds_t: " + rds_t.isSatisfiable(fm_ts))
        println("all rds: " + (rds).and(rds_r).and(rds_t).isSatisfiable(fm_ts))
    }
}


